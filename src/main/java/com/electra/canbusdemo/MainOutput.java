package com.electra.canbusdemo;

import com.electra.canbusdemo.CANbus.CANbus_Controller;
import javafx.scene.control.Alert;
import peak.can.basic.TPCANMsg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static com.electra.canbusdemo.CANbus.CANbus_Controller.getCanBusController;
import static peak.can.basic.TPCANMessageType.PCAN_MESSAGE_STANDARD;

/**
 * The class MainOutput class manages message transmission, reception, and logging via the Controller Area Network (CAN) bus.
 * This class facilitates communication with the CAN bus controller, allowing sending and receiving messages.
 * Additionally, it provides functionality to log data to a specified CSV file path for record-keeping.
 *
 * This class encapsulates methods for sending and receiving CAN bus messages,
 * handling exceptions during message transmission and reception, and logging data to a CSV file.
 *
 * The methods within this class assist in:
 * - Sending messages with specified message IDs and data over the CAN bus.
 * - Writing log data to a CSV file, including various parameters like timestamps,
 *   temperature, current, control mode, fault codes, etc.
 *
 * This class serves as a bridge between the main view controller and the CAN bus controller,
 * ensuring seamless interaction and handling of CAN bus messages and log data.
 */
public class MainOutput{
    private MainViewController mainViewController;
    private CANbus_Controller canBusController=getCanBusController();
    private String logPath = "./log/";
    private static String logName = "log.csv";
    private String timeStamp;
    private String finalString;
    public static String getLogName() {
        return logName;
    }

    public String getLogPath() {
        return logPath;
    }
    public String getTimeStamp() {
        timeStamp =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").format(new java.util.Date());
        return timeStamp;
    }
    public static void setLogName(String logName1) {
        logName = logName1;
    }

    public MainOutput(MainViewController mainViewController1){
        mainViewController=mainViewController1;
    }

    /**
     * Sends a message via the CAN bus with the specified message ID and data.
     *
     * @param messageId   The unique identifier of the message to be sent.
     * @param messageData The byte array containing the data to be sent along with the message.
     *                    The length of this byte array determines the amount of data sent.
     * @throws RuntimeException If an error occurs during the message sending process, it raises a runtime exception.
     *                           This exception triggers the display of an error alert to notify about the failure.
     *                           Possible errors include issues with the CAN bus connection, data sending failure,
     *                           or any other unforeseen problems related to the message sending mechanism.
     */
    public void sendMessage(int messageId, byte messageData[]){
        try {
            if(canBusController.isConnected()){
                canBusController.sendCommand(messageId, messageData); //invio dati al peakCanView
                TPCANMsg sendMessage = new TPCANMsg(messageId, PCAN_MESSAGE_STANDARD.getValue(), (byte) messageData.length, messageData);
                canBusController.getCanBusDatareader().processData(sendMessage);
            }
        }catch (Exception e) {
            mainViewController.fireAlarm(Alert.AlertType.ERROR, "Error", "The message could not be sent");
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes log data to a .csv file, displaying various parameters including timestamp, current (A), RPM calculation frequency (HZ),
     * Motor Temperature (°C), Inverter Temperature (°C), Battery Current (A), Battery Voltage (V), Control Mode, and Fault Code.
     * Allows the user to select the file path via the GUI; if the file does not exist, it creates a new one.
     * Generates an alert if an error occurs during the writing process.
     *
     * @param messageId   The ID of the message.
     * @param messageData The data associated with the message.
     * @throws RuntimeException If an error occurs during the file writing process, it throws a runtime exception and displays an error alert.
     */
    public void createMessageToFileLog(int messageId, byte messageData[]){
            if (!mainViewController.getTextLogFilePath().equals(logPath)) {
                logPath = (mainViewController.getTextLogFilePath()) + logName;
                mainViewController.setLogFilePathTextField(logPath);
            }

            timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").format(new java.util.Date());
            String textToInsertFile[] = new String[4];
            textToInsertFile[0] = 0 + ";" + 0 + ";";
            textToInsertFile[1] = 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";";
            textToInsertFile[2] = 0 + ";";
            textToInsertFile[3] = 0 + ";";

            if (messageId == MainViewStaticVariables.getInverterPd01Tx()) {
                String temp1 = Integer.toString(messageData[0]);
                String temp2 = Integer.toString(messageData[7]);
                textToInsertFile[0] = temp1 + ";" + temp2 + ";";
            }
            if (messageId == MainViewStaticVariables.getInverterPod2Tx()) {
                String temp1 = Integer.toString(messageData[2]);
                String temp2 = Integer.toString(messageData[3]);
                String temp3 = Integer.toString(messageData[5]);
                String temp4 = Integer.toString(messageData[6]);
                textToInsertFile[1] = temp1 + ";" + temp2 + ";" + temp3 + ";" + temp4 + ";";
            }

            if (messageId == MainViewStaticVariables.getPcVcuNewId()) {
                String temp1 = "";
                if (messageData[7] == 0) {
                    temp1 = MainViewStaticVariables.getControlMode2();
                    textToInsertFile[2] = temp1 + ";";
                } else if (messageData[7] == 1)
                    temp1 = MainViewStaticVariables.getControlMode1();
                textToInsertFile[2] = temp1 + ";";
            }

           if (messageId == MainViewStaticVariables.getInverterCodeFault()) {
                    int messageCodeFault = messageData[0]+messageData[1]+messageData[2]+messageData[3]+ messageData[4]+messageData[5]+messageData[6];
                    textToInsertFile[3] = String.valueOf(messageCodeFault);
                }


            finalString = "[" + timeStamp + "]" + ";" + textToInsertFile[0] + textToInsertFile[1] + textToInsertFile[2] + textToInsertFile[3];

            try {
                sendMessageToFileLog(logPath, finalString);
            }catch (IOException e) {
                mainViewController.fireAlarm(Alert.AlertType.ERROR, "Error", "The log file could not be written");
                throw new RuntimeException(e);
            }
    }


    /**
     * Writes the provided string content to a specified file path. If the file doesn't exist, this method creates a new file
     * at the specified location. If the file already exists, it appends the new content to the existing file content.
     *
     * This method is responsible for writing data to a log file in CSV (Comma-Separated Values) format. The log file
     * comprises entries containing timestamped data representing various system parameters. Each entry consists of fields
     * structured as follows:
     *
     * - Timestamp: The date and time when the log entry is created (in the format 'yyyy-MM-dd HH:mm:ss.SSSSSS').
     * - Real Current (A): The actual current in amperes.
     * - RPM Calculation Frequency (HZ): The frequency of RPM calculation in Hertz.
     * - Motor Temperature (°C): The temperature of the motor in Celsius degrees.
     * - Inverter Temperature (°C): The temperature of the inverter in Celsius degrees.
     * - Battery Current (A): The current flowing through the battery in amperes.
     * - Battery Voltage (V): The voltage level of the battery in volts.
     * - Control Mode: The operational mode of the system's control (e.g., modes represented as strings).
     * - Fault Code: The system's fault code, if present. If no fault is detected, it will be denoted as 'code fault'.
     *
     * Each log entry represents a snapshot of system parameters captured at a specific point in time.
     *
     * @param filePath  The path to the file where the string content will be written.
     * @param newString The string content to be appended into the specified file.
     * @throws IOException Signals that an I/O (Input/Output) exception of some sort has occurred when writing to the file.
     *                     This exception is thrown if there is an issue with accessing, reading, or writing the file
     *                     specified by the filePath.
     *                     Possible reasons include, but are not limited to: insufficient permissions, file not found,
     *                     or other issues related to the file system.
     */
    public void sendMessageToFileLog(String filePath, String newString) throws IOException {
        try {
            File logFile = new File(filePath);
            if (!logFile.exists()) {
                logFile.createNewFile();
                System.out.println("New log file created at: " + filePath);
            }
            int size= (int) logFile.length();
            FileWriter fileWriter = new FileWriter(filePath, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            if(size==0) {
                bufferedWriter.write("Timestamp"+";"+"Corrente Reale (A)" +";"+ "Frequenza calcolo RPM (HZ)"+";"+
                        "Temperatura Motore (°C)"+";"+"Temperatura Inverter (°C)" +";"+
                        "Corrente Batteria (A)"+";"+"Tensione Batteria (V)" +";"+
                        "Modalita' di Controllo"+";"+"Codice Fault");
                bufferedWriter.newLine();
            }
            bufferedWriter.write(newString);
            bufferedWriter.newLine(); // Move to the next line
            bufferedWriter.close();
        }
        catch (IOException e) {
            mainViewController.fireAlarm(Alert.AlertType.ERROR, "Error", "An error occurred while opening/creating the log file");
        }
    }
}
