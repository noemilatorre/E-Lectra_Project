package com.electra.canbusdemo;
/**
 * This class contains static variables representing various constants used in the application.
 * These constants include mode names, message IDs, offsets, and other values that are utilized
 * throughout the application for maintaining consistency and ease of reference.
 * <p>
 * The class provides getter methods for accessing these constants.
 * </p>
 * <p>
 * Note: The values stored in this class are considered constants and should not be modified
 * during runtime.
 * </p>
 */
public class MainViewStaticVariables {
    //State Emergency Stop
    private static String EMERGENCY_STOP1= "EMERGENCY STOP";
    private static String EMERGENCY_STOP2= "RESET";

    // Traction Modes
    private static String TRACTION_MODE1 = "Forward (eco)";
    private static String TRACTION_MODE2 = "Forward (sport)";
    private static String TRACTION_MODE3 = "Reverse";

    // Charging Modes
    private static String CHARGING_MODE1 = "GRID";
    private static String CHARGING_MODE2 = "RES";

    // Charging Profiles
    private static String CHARGING_PROFILE1 = "Constant current";
    private static String CHARGING_PROFILE2 = "Custom Profile";

    // Control Modes
    private static String CONTROL_MODE1 = "Speed";
    private static String CONTROL_MODE2 = "Torque";

    // Contactor Status
    private static String CONTACTOR_STATUS1 = "CHARGE";
    private static String CONTACTOR_STATUS2 = "DISCHARGE";

    // Status
    private static String STATUS_ON = "ENABLED";
    private static String STATUS_OFF = "DISABLED";

    // Set Point Labels
    private static String SET_POINT_TORQUE = "SET POINT TORQUE";
    private static String SET_POINT_SPEED = "SET POINT SPEED";

    // Message IDs
    private static int PC_VCU_ID = 0x222;
    private static int PC_VCU_NEW_ID = 0x223;
    private static int INVERTER_CODE_FAULT = 0x88;
    private static int INVERTER_POD2_TX = 0x288;
    private static int INVERTER_PD01_TX = 0x188;
    private static int CHARGER_ID = 0x187;
    private static int VCU_TO_CHARGER_ID = 0x207;

    // Battery Value Constraints
    private static int MAX_BATTERY_VALUE = 100;
    private static int MIN_BATTERY_VALUE = 0;

    private static int MAX_PERCENTAGE_VALUE = 100;

    // Offsets
    private static int OFFSET_BATTERY_VOLT = 1000;
    private static int OFFSET_VOLTAGE_REQ = 256;
    private static int OFFSET_CURRENT_REQ = 16;
    private static int OFFSET_CURRENT_CHARGER = 256;
    private static  int OFFSET_SPEED = 10;
    private static int OFFEST_TEMP_ENGINE = 40;
    private static int OFFSET_BATTERY_CURRENT = 2;


    public static String getEmergencyStop1() { return EMERGENCY_STOP1; }

    public static String getEmergencyStop2() { return EMERGENCY_STOP2; }
    public static String getTractionMode1() {
        return TRACTION_MODE1;
    }

    public static String getTractionMode2() {
        return TRACTION_MODE2;
    }

    public static String getTractionMode3() {
        return TRACTION_MODE3;
    }

    public static String getChargingMode1() {
        return CHARGING_MODE1;
    }

    public static String getChargingMode2() {
        return CHARGING_MODE2;
    }

    public static String getChargingProfile1() {
        return CHARGING_PROFILE1;
    }

    public static String getChargingProfile2() {
        return CHARGING_PROFILE2;
    }

    public static String getControlMode1() {
        return CONTROL_MODE1;
    }

    public static String getControlMode2() {
        return CONTROL_MODE2;
    }

    public static String getContactorStatus1() {
        return CONTACTOR_STATUS1;
    }

    public static String getContactorStatus2() {
        return CONTACTOR_STATUS2;
    }

    public static String getStatusOn() {
        return STATUS_ON;
    }

    public static String getStatusOff() {
        return STATUS_OFF;
    }

    public static String getSetPointTorque() {return SET_POINT_TORQUE;}

    public static String getSetPointSpeed() {
        return SET_POINT_SPEED;
    }

    public static int getPcVcuId() {
        return PC_VCU_ID;
    }

    public static int getPcVcuNewId() {
        return PC_VCU_NEW_ID;
    }

    public static int getInverterCodeFault() {
        return INVERTER_CODE_FAULT;
    }

    public static int getInverterPod2Tx() { return INVERTER_POD2_TX; }

    public static int getInverterPd01Tx() {
        return INVERTER_PD01_TX;
    }

    public static int getChargerId() {
        return CHARGER_ID;
    }

    public static int getVcuToChargerId() {
        return VCU_TO_CHARGER_ID;
    }

    public static int getMaxBatteryValue() {
        return MAX_BATTERY_VALUE;
    }

    public static int getMinBatteryValue() {
        return MIN_BATTERY_VALUE;
    }

    public static int getMaxPercentageValue() { return MAX_PERCENTAGE_VALUE; }

    public static int getOffsetBatteryVolt() { return OFFSET_BATTERY_VOLT; }

    public static int getOffsetVoltageReq() {
        return OFFSET_VOLTAGE_REQ;
    }

    public static int getOffsetCurrentReq() {
        return OFFSET_CURRENT_REQ;
    }

    public static int getOffsetCurrentCharger() {
        return OFFSET_CURRENT_CHARGER;
    }

    public static int getOffsetSpeed() { return OFFSET_SPEED; }

    public static int getOffestTempEngine() {
        return OFFEST_TEMP_ENGINE;
    }

    public static int getOffsetBatteryCurrent() {
        return OFFSET_BATTERY_CURRENT;
    }
}
