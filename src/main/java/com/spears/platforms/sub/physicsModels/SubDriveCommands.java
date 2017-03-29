/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spears.platforms.sub.physicsModels;

/**
 * Created by PHM-Lab2 on 4/25/2016.
 */
public enum SubDriveCommands {
    DRIVE_FORWARD("move"), DRIVE_BACKWARD("backward"), STOP("stop"), SPIN_CW("spin_cw"), SPIN_CCW("spin_ccw"),
    TURN_FRONT_LEFT("turnFL"), TURN_FRONT_RIGHT("turnFR"), TURN_BACK_LEFT("turnBL"), TURN_BACK_RIGHT("turnBR"),
    CHANGE_MOTOR_PWR("change_motor");

    private String value;

    SubDriveCommands(String val){
        this.value = val;
    }

    public String getCmd(){
        return value;
    }

}
