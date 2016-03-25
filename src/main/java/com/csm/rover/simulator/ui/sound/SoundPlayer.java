package com.csm.rover.simulator.ui.sound;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.csm.rover.simulator.ui.events.InternalEventHandler;
import com.csm.rover.simulator.ui.events.MenuCommandEvent;
import com.csm.rover.simulator.ui.events.MenuCommandListener;

public class SoundPlayer {
	private static final Logger LOG = LogManager.getLogger(SoundPlayer.class);

	private final static String soundPackage = "/sounds/";
	
	private static float volume = 6;
	
	static {
		InternalEventHandler.registerInternalListener(new MenuCommandListener(){
			@Override
			public void menuAction(MenuCommandEvent e) {
				if (e.getAction().equals(MenuCommandEvent.Action.VOLUME_CHANGE)){
					switch((String)e.getValue()){
					case "HIGH":
						volume = 6;
						LOG.log(Level.DEBUG, "Volume set to HIGH");
						break;
					case "LOW":
						volume = -2;
						LOG.log(Level.DEBUG, "Volume set to LOW");
						break;
					case "MUTE":
						volume = 0;
						LOG.log(Level.DEBUG, "Volume set to MUTE");
						break;
					default:
						LOG.log(Level.WARN, "Unexpected volume change value: \"{}\"", e.getValue());
						break;
					}
				}
			}
		});
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
