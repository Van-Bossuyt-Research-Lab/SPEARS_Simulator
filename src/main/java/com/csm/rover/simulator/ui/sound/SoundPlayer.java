package com.csm.rover.simulator.ui.sound;

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
		catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			LOG.log(Level.ERROR, "Sound Failed to Play", e);
		}
	}
	
}
