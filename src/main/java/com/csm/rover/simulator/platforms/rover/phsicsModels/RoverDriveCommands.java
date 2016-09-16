package com.csm.rover.simulator.platforms.rover.phsicsModels;

public enum RoverDriveCommands {

    DRIVE_FORWARD("move"), DRIVE_BACKWARD("backward"), STOP("stop"), SPIN_CW("spin_cw"), SPIN_CCW("spin_ccw"),
    TURN_FRONT_LEFT("turnFL"), TURN_FRONT_RIGHT("turnFR"), TURN_BACK_LEFT("turnBL"), TURN_BACK_RIGHT("turnBR"),
    CHANGE_MOTOR_PWR("change_motor");

    private String value;

    RoverDriveCommands(String val){
        this.value = val;
    }

    public String getCmd(){
        return value;
    }

}
