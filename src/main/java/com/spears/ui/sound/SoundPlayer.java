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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.*;
import java.io.IOException;

public class SoundPlayer {
	private static final Logger LOG = LogManager.getLogger(SoundPlayer.class);

    public enum Volume { HIGH, LOW, MUTE }

	private final static String soundPackage = "/sounds/";
	
	private static float volume = 6;
	
	public static void setVolume(Volume level){
        switch(level){
            case HIGH:
                volume = 6;
                LOG.log(Level.DEBUG, "Volume set to HIGH");
                break;
            case LOW:
                volume = -2;
                LOG.log(Level.DEBUG, "Volume set to LOW");
                break;
            case MUTE:
                volume = 0;
                LOG.log(Level.DEBUG, "Volume set to MUTE");
                break;
            default:
                LOG.log(Level.DEBUG, "null Volume level");
                break;
        }
    }
	
	public static void playSound(SpearsSound sound){
		LOG.log(Level.DEBUG, "Requested to play sound: {}", sound);
		if (volume == 0){
			return;
		}
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream ais = AudioSystem.getAudioInputStream(SoundPlayer.class.getResourceAsStream(soundPackage+sound.getURL()));
			clip.open(ais);
			FloatControl gainControl = 
				    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				gainControl.setValue(volume);
			clip.start();
		} 
		catch (LineUnavailableException | UnsupportedAudioFileException | IOException | NullPointerException e) {
			LOG.log(Level.ERROR, "Sound Failed to Play", e);
		}
	}
	
}
