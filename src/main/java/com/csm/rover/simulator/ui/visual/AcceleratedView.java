package com.csm.rover.simulator.ui.visual;

public interface AcceleratedView {

    void show();

    void hide();

    void setDuration(int min);

    void setAbortAction(AbortListener listen);

}
