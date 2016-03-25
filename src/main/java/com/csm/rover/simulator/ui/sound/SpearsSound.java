package com.csm.rover.simulator.ui.sound;

public enum SpearsSound {
	
	ERROR("censor_beep.wav"), POPPING("alert_asterisk_1.wav"), ATTENTION("app_game_interactive_alert_tone_014.wav"),
	NEW("app_game_interactive_alert_tone_001.wav"), BEEP_LOW("multimedia_rollover_037.wav"), BEEP_HIGH("multimedia_rollover_028.wav"),
	DROPPING("multimedia_rollover_051.wav"), ERROR_SHORT("multimedia_rollover_069.wav"), SONAR("multimedia_rollover_067.wav"),
	RINGING("app_game_interactive_alert_tone_011.wav"), RINGING_LONG("app_game_interactive_alert_tone_024.wav"), HELLO("app_game_interactive_alert_tone_015.wav");
	
	private String url;
	
	private SpearsSound(String url){
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
