package com.csm.rover.simulator.platforms.sub.physicsModels;


import com.csm.rover.simulator.wrapper.Globals;

public class SubPhysicsModelTest {
    @Test
    public void testStart(){
        subPhysicsModel model = new subPhysicsModel();
        model.setPlatformName("TestPhysics");
        model.start();
        try{
            Globals.getInstance().getThreadRunPermission("TestPhysics-physics");
            pass();
        }
        catch (NullPointerException e){
            //No thread with that name found
            fail();
        }
    }


}
