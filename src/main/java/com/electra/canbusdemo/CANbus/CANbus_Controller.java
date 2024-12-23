package com.electra.canbusdemo.CANbus;

import java.util.HexFormat;

public class CANbus_Controller {
    //1-definisco un attributo statico (per il Singleton)
    private static CANbus_Controller canBusController = null;
    private CANbus_Protocol protocol;
    private CANbusDataReader canBusDatareader;
    private Notifiable notifiable;

//aggiunto getter e setter
    public CANbusDataReader getCanBusDatareader() {
        return canBusDatareader;
    }

    //2-dichiaro il costruttore privato, non posso istanziarla dall'esterno (tipo Singleton)
    private CANbus_Controller(){
        protocol = new CANbus_Protocol();
        canBusDatareader = new CANbusDataReader(this, protocol.getQueue());
    }
    //3-definisco un metodo statico che crea una SOLA istanza disponibile,
    // se non è mai stata creata la crea all'occorenza
    public static synchronized CANbus_Controller getCanBusController(){
        if(canBusController == null){
            canBusController = new CANbus_Controller();
        }

        return canBusController;
    }

    public boolean isConnected() {
        return protocol.getDriver().isConnected();
    }

    /**
     * Open the connection with the associated driver.
     *
     * @see CANbus_Protocol#connect(String)
     */
    public boolean connect(String handler) {
        if(protocol.connect(handler)) {
            //canBusDatareader.startRead();
            protocol.startReceiverThread();
            return true;
        }
        return false;
    }

    /**
     * Close the connection with the associated driver.
     *
     * @see CANbus_Protocol#disconnect()
     */
    public void disconnect() {
        try {
            protocol.stopReceiverThread();
            //canBusDatareader.stopRead();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        protocol.disconnect();
        //canBusDatareader.stopRead();
    }

    /**
     * Send the N command instruction via the associated protocol.
     */
    public void sendCommand(int id, byte[] data) throws Exception {
//        byte[] data = {(byte)0x33, (byte)0x33, (byte)0x33, (byte)0x33,
//                (byte)0x33, (byte)0x33, (byte)0x33, (byte)0x33};

        InstructionCANbus instr = new InstructionCANbus(id, data, (byte)8);
        protocol.sendInstruction(instr); //invia pacchetti al peakcan
    }

    /**
     * Get all the available handlers associated with the {@link CANbus_Driver}.
     *
     * @return the list of available ports.
     */
    public String[] getAvailableHandlers() {
        return ((CANbus_Driver)protocol.getDriver()).getHandlerNames();
    }

    public void newData(String s) {
        notifiable._notify(s);
    }


    public void setNotifiable(Notifiable notifiable) {
        this.notifiable = notifiable;
    }
}
