package com.csm.rover.simulator.platforms.sub.physicsModels;

import org.junit.Test;
import com.csm.rover.simulator.wrapper.Globals;

import static org.junit.Assert.fail;

public class SubPhysicsModelTest {

    @Test
    public void testStart(){
        subPhysicsModel model = new subPhysicsModel();
        model.setPlatformName("TestPhysics");
        model.start();
        try{
            Globals.getInstance().getThreadRunPermission("TestPhysics-physics");
        }
        catch (NullPointerException e){
            //No thread with that name found
            fail();
        }
    }

}
