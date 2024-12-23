package com.electra.canbusdemo;

import com.electra.canbusdemo.CANbus.CANbus_Controller;
import com.electra.canbusdemo.CANbus.Notifiable;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import eu.hansolo.tilesfx.addons.Switch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;

import java.util.HexFormat;

import static com.electra.canbusdemo.CANbus.CANbus_Controller.getCanBusController;

public class MainViewController implements Notifiable {
    @FXML
    private Button connectButton;
    @FXML
    private Button sendButton;
    @FXML
    private Button emergencyStopButton;
    @FXML
    private Button sendSetPointButton;
    @FXML
    private CheckBox parkingModeCheckBox;
    @FXML
    private ComboBox tractionModeComboBox;
    @FXML
    private ComboBox controlModeComboBox;
    @FXML
    private ComboBox chargingModeComboBox;
    @FXML
    private ComboBox chargingProfileComboBox;
    @FXML
    private ComboBox<String> deviceComboBox;
    private Gauge batteryGauge;
    private Gauge temperatureEngineGauge;
    private Gauge temperatureInverterGauge;
    @FXML
    private Gauge speedEngineGauge;
    @FXML
    private Switch contactorSwitch;
    @FXML
    private Text unitOfMeasureSetPoint;
    @FXML
    private Text tractionStatusText;
    @FXML
    private Text contactorMainStatusText;
    @FXML
    private Text emergencyStopStatusText;
    @FXML
    private Text chargerModeText;
    @FXML
    private Text chargerSoCPercentageText;
    @FXML
    private Text chargerCurrentText;
    @FXML
    private Text chargerVoltageText;
    @FXML
    private Text chargerStatusText;
    @FXML
    private Text speedText;
    @FXML
    private Text batteryVoltageText;
    @FXML
    private Text batteryCurrentText;
    @FXML
    private Text voltageRequestText;
    @FXML
    private Text currentRequestText;
    @FXML
    private TextField voltageTextField;
    @FXML
    private TextField currentTextField;
    @FXML
    private TextField setPointTextField;
    @FXML
    private TextField logFilePathTextField;
    @FXML
    private TextField idTextField;
    @FXML
    private TextField data0TextField, data1TextField, data2TextField, data3TextField, data4TextField,
            data5TextField, data6TextField, data7TextField;
    @FXML
    private VBox batteryVBox;
    @FXML
    private VBox temperatureEngineVBox;
    @FXML
    private VBox temperatureInverterVBox;
    private ObservableList<String> canBusDevice_List = FXCollections.observableArrayList();
    private CANbus_Controller canBusController;
    private ObservableList<String> tractionModeComboItem;
    private ObservableList<String> controlModeComboItem;
    private ObservableList<String> chargingModeComboItem;
    private ObservableList<String> chargingProfileComboItem;
    private String canBusDevice;
    private byte messageData[] = new byte[8];
    private int messageId;
    private boolean chargerModeAbility = false;
    private MainOutput mainOutput = null;

    /**
     * Initializes the UI components and event handlers when the associated JavaFX controller is initialized.
     * This method configures the initial state of buttons, text fields, and gauge visualizations.
     * Sets up various ComboBoxes like 'tractionModeComboBox', 'controlModeComboBox', 'chargingProfileComboBox',
     * and 'chargingModeComboBox' by populating them with predefined options.
     * Configures specific event filters for input text fields like 'setPointTextField', 'voltageRequestTextField',
     * 'currentRequestTextField', 'data0TextField', 'data1TextField', 'data2TextField', 'data3TextField',
     * 'data4TextField', 'data5TextField', 'data6TextField', and 'data7TextField' to validate user input.
     * Initializes the 'deviceComboBox' by adding available CAN bus adapter devices.
     * Establishes event handling for 'deviceComboBox' click action to update the list of available handlers.
     * Initializes and configures gauge visualizations for engine temperature, inverter temperature, and battery levels.
     * <p>
     * Note: This method is invoked upon loading the JavaFX controller and sets up the initial state of UI components
     * and their respective event listeners.
     */
    @FXML
    public void initialize() {
        sendButton.setDisable(true);
        canBusDevice_List.addAll();
        deviceComboBox.setItems(canBusDevice_List);
        canBusController = getCanBusController();
        //se hai qualcosa da notificarmi usa questo oggetto
        canBusController.setNotifiable(this);
        mainOutput = new MainOutput(this);
        mainOutput.setLogName(mainOutput.getLogPath() + mainOutput.getTimeStamp().replaceAll(":", ",") + "_" + MainOutput.getLogName());

        temperatureEngineGauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.DASHBOARD)
                .animated(true)
                .title("Temperature Engine")
                .unit("°C")
                .maxValue(100)
                .barColor(Color.CRIMSON)
                .valueColor(Color.BLACK)
                .titleColor(Color.BLACK)
                .unitColor(Color.BLACK)
                .shadowsEnabled(true)
                .gradientBarEnabled(true)
                .gradientBarStops(new Stop(0.00, javafx.scene.paint.Color.BLUE),
                        new Stop(0.25, Color.CYAN),
                        new Stop(0.50, Color.LIME),
                        new Stop(0.75, Color.YELLOW),
                        new Stop(1.00, Color.RED))
                .build();
        temperatureInverterGauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.DASHBOARD)
                .animated(true)
                .title("Temperature Inverter")
                .unit("°C")
                .maxValue(100)
                .barColor(Color.CRIMSON)
                .valueColor(Color.BLACK)
                .titleColor(Color.BLACK)
                .unitColor(Color.BLACK)
                .shadowsEnabled(true)
                .gradientBarEnabled(true)
                .gradientBarStops(new Stop(0.00, javafx.scene.paint.Color.BLUE),
                        new Stop(0.25, Color.CYAN),
                        new Stop(0.50, Color.LIME),
                        new Stop(0.75, Color.YELLOW),
                        new Stop(1.00, Color.RED))
                .build();

        batteryGauge = GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.BATTERY)
                .animated(true)
                .sectionsVisible(true)
                .sections(new Section(0, 10, Color.rgb(200, 0, 0, 0.8)),
                        new Section(10, 30, Color.rgb(200, 200, 0, 0.8)),
                        new Section(30, 100, Color.rgb(0, 200, 0, 0.8)))
                .build();
        batteryVBox.getChildren().add(batteryGauge);
        temperatureEngineVBox.getChildren().add(temperatureEngineGauge);
        temperatureInverterVBox.getChildren().add(temperatureInverterGauge);

        tractionModeComboItem = FXCollections.observableArrayList(
                MainViewStaticVariables.getTractionMode1(),
                MainViewStaticVariables.getTractionMode2(),
                MainViewStaticVariables.getTractionMode3()
        );
        tractionModeComboBox.getItems().addAll(tractionModeComboItem);

        controlModeComboItem = FXCollections.observableArrayList(
                MainViewStaticVariables.getControlMode1(),
                MainViewStaticVariables.getControlMode2()
        );
        controlModeComboBox.getItems().addAll(controlModeComboItem);


        chargingProfileComboItem = FXCollections.observableArrayList(
                MainViewStaticVariables.getChargingProfile1(),
                MainViewStaticVariables.getChargingProfile2()
        );
        chargingProfileComboBox.getItems().addAll(chargingProfileComboItem);

        chargingModeComboItem = FXCollections.observableArrayList(
                MainViewStaticVariables.getChargingMode1(),
                MainViewStaticVariables.getChargingMode2()
        );
        chargingModeComboBox.getItems().addAll(chargingModeComboItem);

        setPointTextField.setDisable(true);

        setPointTextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInputSetPoint(keyEvent, setPointTextField);
        });
        voltageTextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInputVoltageAndCurrent(keyEvent, voltageTextField);
        });
        currentTextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInputVoltageAndCurrent(keyEvent, currentTextField);
        });

        data0TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data0TextField);
        });

        data0TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data0TextField);
        });

        data1TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data1TextField);
        });

        data2TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data2TextField);
        });

        data3TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data3TextField);
        });

        data4TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data4TextField);
        });

        data5TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data5TextField);
        });

        data6TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data6TextField);
        });

        data7TextField.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            checkTextFieldInput(keyEvent, data7TextField);
        });
        deviceComboBox.setOnMouseClicked(event -> {
            canBusDevice_List.removeAll(canBusDevice_List);
            canBusDevice_List.addAll(canBusController.getAvailableHandlers());
        });
    }

    /**
     * Validates and restricts user input in the provided TextField to accept hexadecimal characters (0-9, A-F).
     * Limits the input length to a maximum of 2 characters.
     * Consumes the KeyEvent 'keyEvent' if the input character is not in the allowed set or if the input exceeds
     * the maximum length specified for the TextField 'textField'.
     *
     * @param keyEvent  The KeyEvent representing the user's keyboard input action.
     *                  Captures the character inputted by the user.
     * @param textField The TextField component where the user input is being validated and limited.
     *                  Accepts only hexadecimal characters and enforces a maximum length of 2 characters.
     *
     */
    private void checkTextFieldInput(KeyEvent keyEvent, TextField textField) {
        String newChar = keyEvent.getCharacter().toUpperCase();
        String keyFilter = "0123456789ABCDEF";

        if (!keyFilter.contains(newChar) || textField.getText().length() > 1) {
            keyEvent.consume();
        }
    }

    /**
     * Validates and constrains user input in the provided TextField for setting a numeric value (0-100).
     * Accepts only numeric characters (0-9) and restricts the length to a maximum of 3 characters.
     * Prevents further input if the maximum length is reached or if non-numeric characters are entered
     * via KeyEvent 'keyEvent'.
     *
     * @param keyEvent  The KeyEvent representing the user's keyboard input action.
     *                  Captures the character inputted by the user.
     * @param textField The TextField component where the user input is being validated and limited.
     *                  Ensures only numeric characters and a specific length are entered.
     *
     */
    private void checkTextFieldInputSetPoint(KeyEvent keyEvent, TextField textField) {
        String newChar = keyEvent.getCharacter();
        String keyFilter = "0123456789";
        if (!keyFilter.contains(newChar) || textField.getText().length() > 2) {
            keyEvent.consume();
        }
    }

    /**
     * Filters and restricts user input in the provided TextField to allow only numeric characters (0-9)
     * +Blocks any other characters inputted via KeyEvent 'keyEvent'.
     *
     * @param keyEvent  The KeyEvent representing the user's keyboard input action.
     *                  It captures the character inputted by the user.
     * @param textField The TextField component where the user input is being checked and filtered.
     *                  It restricts the entry of non-numeric and non-decimal point characters.
     */
    private void checkTextFieldInputVoltageAndCurrent(KeyEvent keyEvent, TextField textField) {
        String newChar = keyEvent.getCharacter();
        String keyFilter = "0123456789.";
        if (!keyFilter.contains(newChar) || textField.getText().length() > 4)
        {
            keyEvent.consume();
        }
    }

    public String getTextLogFilePath() {
        return logFilePathTextField.getText();
    }

    public void setLogFilePathTextField(String logPath) {
        logFilePathTextField.setText(logPath);
    }

    /**
     * Notifies the user interface based on the data received through the 'receiveMessage()' function.
     * Set text fields,labels and gauges with the received data; and the other information,
     * into meaningful representations in the user interface.
     *
     * @param data The incoming message data in String format.
     *             This method processes the incoming data and updates the UI components accordingly.
     */
    @Override
    public void _notify(String data) {
        Platform.runLater(() -> {
            receiveMessage();
        });
    }

    /**
     * Handles the reception of CAN messages and updates the UI components accordingly.
     * This method is called when a CAN message is received, and it checks the message ID
     * to determine the type of message and update the corresponding UI components.
     *
     * If the message ID matches specific predefined IDs (e.g., InverterPod2Tx, InverterPd01Tx, ChargerId, etc.),
     * the method extracts relevant information from the message data and updates the corresponding UI elements,
     * such as gauges, text fields, and labels.
     *
     * Additionally, this method logs the received message data to a file using the mainOutput object.
     * The logging includes information about temperature, battery charge, charger status, speed, and other parameters.
     *
     * @implNote The method assumes a specific structure for the message data and updates UI elements accordingly.
     *           It includes handling for different types of CAN messages and specific data processing logic.
     *
     * @see MainViewStaticVariables
     * @see MainOutput
     * @see #temperatureEngineGauge
     * @see #temperatureInverterGauge
     * @see #batteryGauge
     * @see #batteryCurrentText
     * @see #batteryVoltageText
     * @see #speedEngineGauge
     * @see #chargerSoCPercentageText
     * @see #chargerCurrentText
     * @see #chargerStatusText
     * @see #voltageRequestText
     * @see #currentRequestText
     * @see #tractionStatusText
     * @see #contactorMainStatusText
     * @see #emergencyStopStatusText
     * @see #chargerModeText
     * @see #chargerVoltageText
     *
     */
    void receiveMessage() {
        if (messageId == MainViewStaticVariables.getInverterPod2Tx()) {
            //VALORE DI TEMPERATURA DEL MOT. (X-40) [°C]
            if (temperatureEngineGauge.getValue() != messageData[2]) {
                double temperatureEngineValue = messageData[2] - MainViewStaticVariables.getOffestTempEngine();
                temperatureEngineGauge.setValue(temperatureEngineValue);
            }
            //VALORE DI TEMPERATURA DELL'INV.
            if (temperatureInverterGauge.getValue() != messageData[3]) {
                double temperatureInverterValue = messageData[3];
                temperatureInverterGauge.setValue(temperatureInverterValue);
            }
            //VALORE IN % DELLA CARICA BATT. 0-100%
            if (batteryGauge.getValue() != messageData[4]) {
                if (messageData[4] <= MainViewStaticVariables.getMaxBatteryValue() && messageData[4] >= MainViewStaticVariables.getMinBatteryValue()) {
                    chargerSoCPercentageText.setText(String.valueOf(messageData[4]));
                    double chargerSoCPercentageValue = Double.parseDouble(String.valueOf(messageData[4]));
                    batteryGauge.setValue(chargerSoCPercentageValue);
                } else {
                    fireAlarm(Alert.AlertType.ERROR, "Error", "The values entered for the battery are wrong");
                }
            }
            //VALORE CORENTE BATTERIA, A*2
            if (messageData[5] != 0 || batteryCurrentText.getText() != String.valueOf(messageData[5])) {
                batteryCurrentText.setVisible(true);
                batteryCurrentText.setText(String.valueOf((MainViewStaticVariables.getOffsetBatteryCurrent() * messageData[5])));
            }
            //VALORE VOLTS BATTERIA, Vnom/1000
            if (batteryVoltageText.getText() != String.valueOf(messageData[6])) {
                batteryVoltageText.setVisible(true);
                double batteryVoltageValue = ((double) messageData[6]) / MainViewStaticVariables.getOffsetBatteryVolt();
                batteryVoltageText.setText(String.valueOf(batteryVoltageValue));
            }
            mainOutput.createMessageToFileLog(messageId, messageData);
        }

        if (messageId == MainViewStaticVariables.getInverterPd01Tx()) {
            //VALORE DI FREQUENZA PER RPM, Hz/10
            if (speedEngineGauge.getValue() != messageData[0] || speedText.getText() != String.valueOf(messageData[0])) {
                double speedEngineValue = Double.parseDouble(String.valueOf((messageData[0] / MainViewStaticVariables.getOffsetSpeed())));
                speedEngineGauge.setValue(speedEngineValue);
                speedText.setText(String.valueOf(messageData[0]));
            }
            mainOutput.createMessageToFileLog(messageId, messageData);
        }

        if (messageId == MainViewStaticVariables.getChargerId()) {
            //Charge Current, Offset 1/256
            if (messageData[0] != 0 || messageData[1] != 0) {
                String currentChargerValueHex1 = Integer.toHexString(messageData[0]);
                String currentChargerValueHex2 = Integer.toHexString(messageData[1]);

                String currentChargerValueHexResult = (currentChargerValueHex1) + (currentChargerValueHex2);
                double currentChargerValueResult = HexFormat.fromHexDigits(currentChargerValueHexResult) / MainViewStaticVariables.getOffsetCurrentCharger();
                chargerCurrentText.setText(String.valueOf(currentChargerValueResult));
            }

            byte bitFive = (byte) (messageData[4] & (1 << 4));
            if (bitFive == 0) {
                chargerStatusText.setText(MainViewStaticVariables.getStatusOff());
            } else {
                chargerStatusText.setText(MainViewStaticVariables.getStatusOn());
            }
        }

        if (messageId == MainViewStaticVariables.getVcuToChargerId()) {
            //BATTERY SOC 0-100%
            if (chargerSoCPercentageText.getText() != String.valueOf(messageData[1])) {
                if (messageData[1] <= MainViewStaticVariables.getMaxBatteryValue() && messageData[1] >= MainViewStaticVariables.getMinBatteryValue()) {
                    if (!chargerSoCPercentageText.isVisible())
                        chargerSoCPercentageText.setVisible(true);
                    chargerSoCPercentageText.setText(String.valueOf(messageData[1]));
                }
            }
            //Voltage Request, Offset 1/256
            if (messageData[3] != 0 || messageData[4] != 0) {
                String voltageRequestValueHex1 = Integer.toHexString(messageData[4]);
                String voltageRequestValueHex2 = Integer.toHexString(messageData[3]);

                String voltageRequestResultValueHex = (voltageRequestValueHex1) + (voltageRequestValueHex2);
                double voltageRequestResultValueDouble = HexFormat.fromHexDigits(voltageRequestResultValueHex) / MainViewStaticVariables.getOffsetVoltageReq();
                voltageRequestText.setText(String.valueOf(voltageRequestResultValueDouble));
                if (!voltageRequestText.isVisible())
                    voltageRequestText.setVisible(true);
            }
            //Current Request, Offset 1/16
            if (messageData[6] != 0 || messageData[5] != 0) {
                String currentRequestValueHex1 = Integer.toHexString(messageData[6]);
                String currentRequestValueHex2 = Integer.toHexString(messageData[5]);

                String currentRequestResultValueHex = (currentRequestValueHex2) + (currentRequestValueHex1);
                double currentRequestResultValueDouble = HexFormat.fromHexDigits(currentRequestResultValueHex) / MainViewStaticVariables.getOffsetCurrentReq();
                currentRequestText.setText(String.valueOf(currentRequestResultValueDouble));
                if (!currentRequestText.isVisible())
                    currentRequestText.setVisible(true);
            }
        }

        if (messageId == MainViewStaticVariables.getPcVcuId()) {
            // ABILITAZIONE STADIO POTENZA + RICHIESTA DI MARCIA AVANTI + forward (eco)
            if (messageData[0] == 1 && messageData[1] == 1 && messageData[2] == 0
                    && messageData[7] == 0) {
                tractionStatusText.setText(MainViewStaticVariables.getTractionMode1());
            }// ABILITAZIONE STADIO POTENZA + RICHIESTA DI MARCIA AVANTI + forward (sport)
            else if (messageData[0] == 1 && messageData[1] == 1 && messageData[2] == 0
                    && messageData[7] == 1) {
                tractionStatusText.setText(MainViewStaticVariables.getTractionMode2());
            }// ABILITAZIONE STADIO POTENZA + RICHIESTA DI MARCIA INDIETRO
            else if (messageData[0] == 1 && messageData[2] == 1 && messageData[7] == 2) {
                tractionStatusText.setText(MainViewStaticVariables.getTractionMode3());
            }
            //APERTURA MAIN CONTACTOR
            if (messageData[4] == 0) {
                contactorMainStatusText.setText(MainViewStaticVariables.getContactorStatus1());
            }//CHIUSURA MAIN CONTACTOR
            else if (messageData[4] == 1) {
                contactorMainStatusText.setText(MainViewStaticVariables.getContactorStatus2());
            }
        }
        if (messageId == MainViewStaticVariables.getPcVcuNewId()) {
            // DISABILITAZIONE EMERGENCY STOP
            if (messageData[0] == 0) {
                emergencyStopStatusText.setText(MainViewStaticVariables.getStatusOff());
            }//EMERGENCY STOP ABILITATO
            else if (messageData[0] == 1) {
                emergencyStopStatusText.setText(MainViewStaticVariables.getStatusOn());
            }
            //GRID
            if (messageData[1] == 0 && chargerModeAbility) {
                chargerModeText.setText(MainViewStaticVariables.getChargingMode1());
                chargerModeText.setFill(Paint.valueOf("BLACK"));
                chargerModeAbility = false;
            } //RES
            else if (messageData[1] == 1 && chargerModeAbility) {
                chargerModeText.setText(MainViewStaticVariables.getChargingMode2());
                chargerModeText.setFill(Paint.valueOf("BLACK"));
                chargerModeAbility = false;
            }

            //current
            if (messageData[3] + "." + messageData[4] != chargerCurrentText.getText()){
              chargerCurrentText.setVisible(true);
                if(messageData[4]!=0)
                     chargerCurrentText.setText(messageData[3]+"."+messageData[4]);
                else
                    chargerCurrentText.setText(String.valueOf(messageData[3]));
            } //voltage
            if (messageData[5] + "." + messageData[6]!= chargerVoltageText.getText()){
                chargerVoltageText.setVisible(true);
                if(messageData[6]!=0)
                        chargerVoltageText.setText(messageData[5] + "." + messageData[6]);
                else
                        chargerVoltageText.setText(String.valueOf(messageData[5]));
            }

        mainOutput.createMessageToFileLog(messageId, messageData);
    }

    //CODE FAULT
    if(messageId ==MainViewStaticVariables.getInverterCodeFault())
    {
        mainOutput.createMessageToFileLog(messageId, messageData);
    }
}
    /**
     * Manages the action performed when the "Connect" button is clicked.
     * Establishes a connection with the CAN bus controller device.
     * If the "Connect" button is clicked:
     *   - Checks for a valid CAN bus adapter device selection from the dropdown menu.
     *   - Attempts to establish a connection with the selected device using 'canBusController.connect(canBusDevice)'.
     *   - If successful, updates the interface by enabling various control buttons and fields for user interaction.
     *   - Enables buttons like 'sendButton', 'emergencyStopButton', 'sendSetPointButton', etc.
     *   - Disables the "Connect" button and changes its text to "Disconnect".
     *   - If unsuccessful, displays an error alert with possible causes and recommendations for resolution.
     *
     * If the "Disconnect" button is clicked:
     *   - Terminates the connection with the CAN bus device using 'canBusController.disconnect()'.
     *   - Disables buttons and input fields related to CAN bus operations and re-enables the "Connect" button.
     *
     * Note: This method provides the functionality to establish and terminate connections with the CAN bus adapter device.
     */
    @FXML
    void connectButtonAction() {
        if (connectButton.getText().equals("Connect")) {
            canBusDevice = deviceComboBox.getValue();
            if (!canBusController.isConnected()) {
                if (canBusDevice == null || canBusDevice.equals("")) {
                    fireAlarm(Alert.AlertType.ERROR, "Warning", "Please select a valid CAN bus adapter Device.");
                    return;
                }
            }
            if (!canBusController.connect(canBusDevice)) {
                String mex = "Connection with the CANbus failed!!\n";
                mex += "Possible causes:\n";
                mex += "  - the selected device is wrong;\n" +
                        "  - the device is not connected to the USB port;\n\n";
                mex += "If the problem persist, try to unplug and replug the device.";

                fireAlarm(Alert.AlertType.ERROR, "Connection ERROR!!", mex);
                return;
            }

            connectButton.setText("Disconnect");
            sendButton.setDisable(false);
            emergencyStopButton.setDisable(false);
            contactorSwitch.setDisable(false);
            tractionModeComboBox.setDisable(false);
            controlModeComboBox.setDisable(false);
            parkingModeCheckBox.setDisable(false);
            chargingModeComboBox.setDisable(false);
            chargingProfileComboBox.setDisable(false);
            currentTextField.setDisable(false);
            voltageTextField.setDisable(false);
            logFilePathTextField.setDisable(false);
            sendSetPointButton.setDisable(false);

        } else {
            canBusController.disconnect();
            sendButton.setDisable(true);
            connectButton.setText("Connect");
            connectButton.setDisable(false);
            sendButton.setDisable(true);
            emergencyStopButton.setDisable(true);
            contactorSwitch.setDisable(true);
            tractionModeComboBox.setDisable(true);
            controlModeComboBox.setDisable(true);
            parkingModeCheckBox.setDisable(true);
            chargingModeComboBox.setDisable(true);
            chargingProfileComboBox.setDisable(true);
            currentTextField.setDisable(true);
            voltageTextField.setDisable(true);
            logFilePathTextField.setDisable(true);
            sendSetPointButton.setDisable(true);
        }

    }
    /**
     * Sends a message via the CAN bus with the provided hexadecimal data and message ID.
     * This method constructs a message to be sent via the CAN bus using the data entered in the GUI text fields.
     * It extracts hexadecimal data from the respective input fields (data0TextField to data7TextField)
     * using the 'fromHexDigits' method of the 'HexFormat' class, transforming them into byte arrays.
     * The constructed byte array 'data' contains the parsed hexadecimal values.
     * The 'idTextField' provides the message ID in hexadecimal format, converted into an integer as 'messageId'.
     * Finally, the 'mainOutput' object sends the constructed message, consisting of 'messageId' and 'data',
     * via the CAN bus using the 'sendMessage' method, facilitating communication with the hardware.
     */
    @FXML
    public void sendButtonAction() {
        byte data[] =
                {
//fromHexDigits è un metodo statico della classe HexFormat che trasforma il testo esadecimale in byte
                        (byte) HexFormat.fromHexDigits(data0TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data1TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data2TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data3TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data4TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data5TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data6TextField.getText()),
                        (byte) HexFormat.fromHexDigits(data7TextField.getText())
                };
        messageId = HexFormat.fromHexDigits(idTextField.getText());
        messageData = data;
        mainOutput.sendMessage(messageId,messageData);
    }

    /**
     * Displays an alert message of a specified type with the given title and content text.
     *
     * @param type         The type of the alert, specifying its appearance and severity.
     * @param title        The title of the alert window.
     * @param contentText  The text content to be displayed in the alert window.
     *
     * This method generates and displays an alert message based on the specified parameters.
     * It creates an Alert object of the specified type (e.g., WARNING, ERROR, INFORMATION),
     * setting the provided title and content text.
     * The alert is initialized to prevent interaction with the main application window
     * (if applicable) via the 'initOwner' method, ensuring that the alert remains in focus
     * until the user interacts with it.
     * Finally, the alert window is displayed using 'showAndWait()' to block execution
     * until the user closes the alert.
     */

    protected void fireAlarm(Alert.AlertType type, String title, String contentText) {
        Alert alert = new Alert(type);
        alert.initOwner(MainApplication.stage);
        alert.setTitle(title);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Manages the emergency stop functionality.
     *
     * When the 'emergencyStopButton' is pressed:
     * - If the current state disables UI components:
     *   * Enables the UI components, allowing user interaction.
     *   * Changes the 'emergencyStopButton' text to "EMERGENCY STOP".
     *
     * - If the current state enables UI components:
     *   * Disables specific UI components, preventing further user interaction.
     *   * Changes the 'emergencyStopButton' text to "RESET".
     *
     * If the CAN bus controller is connected:
     * - When "EMERGENCY STOP" is pressed:
     *   * Sends a message to disable the emergency stop (PC_VCU_NEW_ID: messageData[0] = 0).
     *
     * - When "RESET" is pressed:
     *   * Sends a message to enable the emergency stop (PC_VCU_NEW_ID: messageData[0] = 1).
     *
     * This method allows users to toggle between emergency stop and reset states.
     * When enabled, it restricts interaction with specific UI components to ensure safety.
     * It also sends corresponding messages through the CAN bus to enable/disable emergency stop functionality.
     */

    @FXML
    void emergencyStopAction() {
        if (emergencyStopButton.getText().equals(MainViewStaticVariables.getEmergencyStop2())) {
            emergencyStopButton.setText(MainViewStaticVariables.getEmergencyStop1());
            tractionModeComboBox.setDisable(false);
            controlModeComboBox.setDisable(false);
            parkingModeCheckBox.setDisable(false);
            setPointTextField.setDisable(false);
            chargingModeComboBox.setDisable(false);
            chargingProfileComboBox.setDisable(false);
            voltageTextField.setDisable(false);
            currentTextField.setDisable(false);
            contactorSwitch.setDisable(false);
            sendSetPointButton.setDisable(false);
        } else if(emergencyStopButton.getText().equals(MainViewStaticVariables.getEmergencyStop1()))
        {  emergencyStopButton.setText(MainViewStaticVariables.getEmergencyStop2());
            tractionModeComboBox.setDisable(true);
            controlModeComboBox.setDisable(true);
            parkingModeCheckBox.setDisable(true);
            setPointTextField.setDisable(true);
            chargingModeComboBox.setDisable(true);
            chargingProfileComboBox.setDisable(true);
            voltageTextField.setDisable(true);
            currentTextField.setDisable(true);
            contactorSwitch.setDisable(true);
            sendSetPointButton.setDisable(true);
        }
        if(canBusController.isConnected()) {
            //emergency stop disabilitato
            if (emergencyStopButton.getText() == MainViewStaticVariables.getEmergencyStop1()) {
                messageId = MainViewStaticVariables.getPcVcuNewId();
                messageData[0] = 0;
            }
            //emergency stop abilitato
            else if (emergencyStopButton.getText() == MainViewStaticVariables.getEmergencyStop2()) {
                messageId = MainViewStaticVariables.getPcVcuNewId();
                messageData[0] = 1;
            }
            mainOutput.sendMessage(messageId,messageData);
        }
    }

    /**
     * Handles the activation or deactivation of parking mode based on the state of the 'parkingModeCheckBox'.
     * When the 'parkingModeCheckBox' is selected (checked):
     * - Disables the 'tractionModeComboBox', 'controlModeComboBox', and 'setPointTextField'.
     *
     * When the 'parkingModeCheckBox' is deselected (unchecked):
     * - Enables the 'tractionModeComboBox', 'controlModeComboBox', and 'setPointTextField' for user interaction.
     *
     * This method allows users to toggle the parking mode, restricting or enabling interaction with specific UI components
     * based on the state of the 'parkingModeCheckBox'.
     */

    @FXML
    void parkingModeAction() {
        if (parkingModeCheckBox.isSelected()) {
            tractionModeComboBox.setDisable(true);
            controlModeComboBox.setDisable(true);
            setPointTextField.setDisable(true);
        } else {
            tractionModeComboBox.setDisable(false);
            controlModeComboBox.setDisable(false);
            setPointTextField.setDisable(false);
        }
    }
    /**
     * Enables and configures the 'setPointTextField' along with associated UI components based on the selected control mode.
     * Sets 'setPointTextField' to be visible and responsive for user input.
     * Depending on the selected control mode, prompts the user to input either 'SET POINT TORQUE' or 'SET POINT SPEED'.
     * The method adjusts the UI visibility and behavior accordingly, enabling/disabling components and providing appropriate prompts.
     *
     * If the CAN bus controller is connected, determines the control mode selected:
     * - If 'CONTROL_MODE2' is selected, prompts for 'SET POINT TORQUE' and prepares to send a message for torque control.
     * - If 'CONTROL_MODE1' is selected, prompts for 'SET POINT SPEED' and prepares to send a message for speed control.
     *
     * Note: The method does not currently send messages. It configures the UI elements based on the selected control mode
     *       and sets up the prompt texts for user input.
     */
    @FXML
    void enableSetPointTextFieldAction() {
        setPointTextField.setDisable(false);
        setPointTextField.setVisible(true);
        unitOfMeasureSetPoint.setVisible(true);
        sendSetPointButton.setDisable(false);
        sendSetPointButton.setVisible(true);

        if (canBusController.isConnected()) {
            if (controlModeComboBox.getValue().equals(MainViewStaticVariables.getControlMode2())) {
                setPointTextField.setPromptText(MainViewStaticVariables.getSetPointTorque());
                messageId= MainViewStaticVariables.getPcVcuNewId();
                messageData[7] = 0;
            }
            else if (controlModeComboBox.getValue().equals(MainViewStaticVariables.getControlMode1())) {
                setPointTextField.setPromptText(MainViewStaticVariables.getSetPointSpeed());
                messageId= MainViewStaticVariables.getPcVcuNewId();
                messageData[7] = 1;
            }
            mainOutput.sendMessage(messageId,messageData);
        }
    }
    /**
     * Checks and validates the input in the 'setPointTextField' for the set point.
     * Retrieves the newly added character from the 'setPointTextField'.
     * Converts the input value to an integer representing a percentage.
     * If the percentage value exceeds the maximum allowed battery value (MAX_PERCENTAGE_VALUE),
     * removes the last character from the 'setPointTextField' to ensure it stays within the permissible range.
     *
     * Note: This method is designed to regulate user input in the 'setPointTextField' to prevent exceeding the maximum percentage value.
     */

    @FXML
    void checkSetPointOnKeyReleased() {
        String newChar = setPointTextField.getText();
        if(!newChar.isEmpty()) {
            int percentage = Integer.valueOf(newChar);
            //elimina ultimo carattere
            if (percentage > MainViewStaticVariables.getMaxPercentageValue()) {
                setPointTextField.deleteText(2, 3);
            }
        }
    }

     /**
     * Handles the key release event for the 'sendVoltage' functionality.
     * This method is invoked when the user releases a key in the voltageTextField.
     * It retrieves the entered voltage value, formats it, and sends a CAN message
     * with the voltage data to the specified message ID (PcVcuNewId).
     *
     * If the CAN bus is connected, the method processes the entered voltage value,
     * extracts the integer and decimal parts, and updates the messageData accordingly.
     * If the entered value is empty, the voltage data in messageData is set to 0.
     *
     * @implNote This method assumes a specific message structure with integer and decimal parts
     * represented by messageData[5] and messageData[6], respectively.
     *
     * @see #voltageTextField
     * @see #messageId
     * @see MainViewStaticVariables#getPcVcuNewId()
     * @see MainOutput#sendMessage(int, byte[])
     *
     */
    @FXML
    void sendVoltageOnKeyReleased() {
        if (canBusController.isConnected()) {
            messageId = MainViewStaticVariables.getPcVcuNewId();
            String valueVoltageString = voltageTextField.getText();

            if (!valueVoltageString.isEmpty()) {
                int indicePuntoDecimale = valueVoltageString.indexOf('.');
                if (valueVoltageString.contains(".") && indicePuntoDecimale != (valueVoltageString.length() - 1)) {
                    String integerPart = valueVoltageString.substring(0, indicePuntoDecimale);
                    String decimalPart = valueVoltageString.substring(indicePuntoDecimale + 1);
                    messageData[5] = Byte.parseByte(integerPart);
                    messageData[6] = Byte.parseByte(decimalPart);
                } else{
                    String[] valueVoltageSplitPoint = valueVoltageString.split("\\.");
                    String valueVoltage = String.join("", valueVoltageSplitPoint);
                    messageData[5] = Byte.parseByte(valueVoltage);
                    messageData[6] = 0;
                }

            }else {
                messageData[5] = 0;
                messageData[6] = 0;
            }
                mainOutput.sendMessage(messageId, messageData);
        }
    }

    /**
     * Handles the key release event for the 'sendCurrent' functionality.
     * This method is invoked when the user releases a key in the currentTextField.
     * It retrieves the entered current value, formats it, and sends a CAN message
     * with the current data to the specified message ID (PcVcuNewId).
     *
     * If the CAN bus is connected, the method processes the entered current value,
     * extracts the integer and decimal parts, and updates the messageData accordingly.
     * If the entered value is empty, the current data in messageData is set to 0.
     *
     * @implNote This method assumes a specific message structure with integer and decimal parts
     * represented by messageData[3] and messageData[4], respectively.
     *
     * @see #currentTextField
     * @see #messageId
     * @see MainViewStaticVariables#getPcVcuNewId()
     * @see MainOutput#sendMessage(int, byte[])
     *
     */

    @FXML
    void sendCurrentOnKeyReleased(){
        if (canBusController.isConnected()) {
            messageId = MainViewStaticVariables.getPcVcuNewId();
            String valueCurrentString = currentTextField.getText();
            if(!valueCurrentString.isEmpty()) {
                int indicePuntoDecimale = valueCurrentString.indexOf('.');
                if (valueCurrentString.contains(".") && (indicePuntoDecimale != valueCurrentString.length() - 1)) {
                    String integerPart = valueCurrentString.substring(0, indicePuntoDecimale);
                    String decimalPart = valueCurrentString.substring(indicePuntoDecimale + 1);
                    messageData[3] = Byte.parseByte(integerPart);
                    messageData[4] = Byte.parseByte(decimalPart);
                } else {
                    String[] valueCurrentSplitPoint = valueCurrentString.split("\\.");
                    String valueCurrent = String.join("", valueCurrentSplitPoint);
                    messageData[3] = Byte.parseByte(valueCurrent);
                    messageData[4] = 0;
                }
            }else {
                messageData[3] = 0;
                messageData[4] = 0;
            }
            mainOutput.sendMessage(messageId, messageData);
        }

    }

    /**
     * Sends a control setpoint to the Vehicle Control Unit (VCU) based on the selected control mode and input value.
     * Checks if the CAN bus controller is connected before proceeding.
     *
     * If the 'controlModeComboBox' value is equal to 'CONTROL_MODE1',
     * sets the message ID to 'PC_VCU_ID' and retrieves the input value from the 'setPointTextField' for the control setpoint.
     * Converts the input string value to an integer and assigns it to the appropriate index in the 'messageData' array for transmission.
     *
     * If the 'controlModeComboBox' value is equal to 'CONTROL_MODE2',
     * sets the message ID to 'PC_VCU_NEW_ID' and retrieves the input value from the 'setPointTextField' for the control setpoint.
     * Converts the input string value to an integer and assigns it to the appropriate index in the 'messageData' array for transmission.
     *
     * The constructed message containing the control setpoint command is sent to the VCU using the 'sendMessage' method in 'mainOutput'.
     *
     * Note: This method assumes an active connection to the CAN bus controller for successful transmission
     * of the control setpoint command message to the VCU.
     */

    @FXML
    void sendSetPointOnAction() {
        if (canBusController.isConnected()) {
            if (controlModeComboBox.getValue() == MainViewStaticVariables.getControlMode1()) {
                messageId = MainViewStaticVariables.getPcVcuId();
                String setPointSpeedString = setPointTextField.getText();
                if(!setPointSpeedString.isEmpty()) {
                    int setPointSpeedInt = Integer.parseInt(setPointSpeedString);
                    messageData[3] = (byte) setPointSpeedInt;
                }else {
                    messageData[3]=0;
                }

            } else if (controlModeComboBox.getValue() == MainViewStaticVariables.getControlMode2()) {
                messageId = MainViewStaticVariables.getPcVcuNewId();
                String setPointTorqueString = setPointTextField.getText();
                if(!setPointTorqueString.isEmpty()) {
                    int setPointTorqueInt = Integer.parseInt(setPointTorqueString);
                    messageData[2] = (byte) setPointTorqueInt;
                }else
                    messageData[2]=0;
            }
            mainOutput.sendMessage(messageId,messageData);
        }

    }
    /**
     * Sends a command to switch on/off the contactor based on its current state.
     * Checks if the CAN bus controller is connected before proceeding.
     * If the 'contactorSwitch' is active, sets the message ID to 'PC_VCU_ID' and the contactor switch value to '1' (indicating Closed Main Contactor).
     * If the 'contactorSwitch' is not active, sets the message ID to 'PC_VCU_ID' and the contactor switch value to '0' (indicating Open Main Contactor).
     * The constructed message containing the contactor switch command is sent to the Vehicle Control Unit (VCU) using the 'sendMessage' method in 'mainOutput'.
     *
     * Note: This method assumes an active connection to the CAN bus controller for successful transmission
     * of the contactor switch command message to control the contactor state.
     */

    @FXML
    void sendContactorSwitchOnMouseClicked() {
        if(canBusController.isConnected()) {
            //switch
            if (contactorSwitch.isActive()) {
                messageId = MainViewStaticVariables.getPcVcuId();
                messageData[4] = 1;
            } else if (!contactorSwitch.isActive()) {
                messageId = MainViewStaticVariables.getPcVcuId();
                messageData[4] = 0;
            }
            mainOutput.sendMessage(messageId,messageData);
        }
    }
    /**
     * Sends a traction mode message based on the selected value in the traction mode combo box.
     * If the CAN bus controller is connected, this method reads the selected traction mode from the combo box.
     * If 'TRACTION_MODE1' is selected, it sets the message ID to 'PC_VCU_ID', and the message data is configured for (Eco) forward motion.
     * If 'TRACTION_MODE2' is selected, it sets the message ID to 'PC_VCU_ID', and the message data is configured for (Sport) forward motion.
     * If 'TRACTION_MODE3' is selected, it sets the message ID to 'PC_VCU_ID', and the message data is configured for Reverse mode of traction.
     * The composed message is then sent to the Vehicle Control Unit (VCU) using the 'sendMessage' method in 'mainOutput'.
     *
     * Note: Successful execution of this method requires an active connection to the CAN bus controller for the transmission
     * of messages to set the traction mode as per the user's selection.
     */
    @FXML
    void sendTractionModeAction() {
        if(canBusController.isConnected()){
            //tractionmode
            if (tractionModeComboBox.getValue() == MainViewStaticVariables.getTractionMode1()) {
                messageId = MainViewStaticVariables.getPcVcuId();
                messageData[0] = 1;
                messageData[1] = 1;
                messageData[2] = 0;
                messageData[7] = 0; //ne aggiungo uno che identifica forward (eco)
            } else if (tractionModeComboBox.getValue() == MainViewStaticVariables.getTractionMode2()) {
                messageId = MainViewStaticVariables.getPcVcuId();
                messageData[0] = 1;
                messageData[1] = 1;
                messageData[2] = 0;
                messageData[7] = 1; //identifica forward (sport)
            } else if (tractionModeComboBox.getValue() == MainViewStaticVariables.getTractionMode3()) {
                messageId = MainViewStaticVariables.getPcVcuId();
                messageData[0] = 1;
                messageData[1] = 0;
                messageData[2] = 1;
                messageData[7] = 2;
            }
            mainOutput.sendMessage(messageId,messageData);
        }
    }
    /**
     * Sets the charging mode based on the value selected in the charging mode combo box.
     * If the CAN bus controller is connected, this method reads the selected charging mode from the combo box.
     * If the mode is 'CHARGING_MODE1', it sets the message ID to 'PC_VCU_NEW_ID' and the message data's second byte to '0'.
     * If the mode is 'CHARGING_MODE2', it sets the message ID to 'PC_VCU_NEW_ID' and the message data's second byte to '1'.
     * It then sends the composed message to the Vehicle Control Unit (VCU) via the 'sendMessage' method in 'mainOutput'.
     *
     * Note: The execution of this method requires an active connection to the CAN bus controller for successful
     * transmission of messages to configure the charging mode.
     */
    @FXML
    void chargingModeAction () {
        if(canBusController.isConnected()) {
            if (chargingModeComboBox.getValue() == MainViewStaticVariables.getChargingMode1()){
                messageId = MainViewStaticVariables.getPcVcuNewId();
                messageData[1] = 0;
                chargerModeAbility=true;
            } else if (chargingModeComboBox.getValue() == MainViewStaticVariables.getChargingMode2()) {
                messageId = MainViewStaticVariables.getPcVcuNewId();
                messageData[1] = 1;
                chargerModeAbility=true;
            }
            mainOutput.sendMessage(messageId,messageData);
        }
    }
}

