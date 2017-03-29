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

package com.spears.ui.sound;

public enum SpearsSound {
	
	ERROR("censor_beep.wav"),
    POPPING("alert_asterisk_1.wav"),
    ATTENTION("app_game_interactive_alert_tone_014.wav"),
	NEW("app_game_interactive_alert_tone_001.wav"),
    BEEP_LOW("multimedia_rollover_037.wav"),
    BEEP_HIGH("multimedia_rollover_028.wav"),
	DROPPING("multimedia_rollover_051.wav"),
    ERROR_SHORT("multimedia_rollover_069.wav"),
    SONAR("multimedia_rollover_067.wav"),
	RINGING("app_game_interactive_alert_tone_011.wav"),
    RINGING_LONG("app_game_interactive_alert_tone_024.wav"),
    HELLO("app_game_interactive_alert_tone_015.wav"),
    GOODBYE("wood_door_close.wav");
	
	private String url;
	
	SpearsSound(String url){
		this.url = url;
	}
	
	public String getURL(){
		return url;
	}
	
	@Override
	public String toString(){
		return this.name()+":"+url;
	}

}
