package com.csm.rover.simulator.test.objects.platforms;

import com.csm.rover.simulator.platforms.Platform;

@com.csm.rover.simulator.platforms.annotations.Platform(type = "Sub")
public class NoncreatableSub extends Platform {

    private NoncreatableSub(){
        super("Sub");
    }

}
