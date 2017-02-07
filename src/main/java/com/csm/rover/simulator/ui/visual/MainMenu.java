package com.csm.rover.simulator.ui.visual;

import com.csm.rover.simulator.ui.sound.VolumeChangeListener;

public interface MainMenu {

    void setCloseOperation(Runnable exit);

    void setVolumeListener(VolumeChangeListener listen);

}
