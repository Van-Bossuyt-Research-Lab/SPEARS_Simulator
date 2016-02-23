package com.csm.rover.simulator.platforms;

public abstract class Platform {

    protected final String platform_type;

    protected PlatformAutonomousCodeModel autonomousCodeModel;
    protected PlatformPhysicsModel physicsModel;

    protected Platform(String type){
        this.platform_type = type;
    }

    public final String getType(){
        return platform_type;
    }

//    public PlatformConfig getConfiguration(){
//        return PlatformConfig.builder()
//                .setType(platform_type)
//                //TODO .setScreenName(screen_name)
//                .setAutonomousModel(autonomousCodeModel.getType(), autonomousCodeModel.getParameters())
//                .setPhysicsModel(physicsModel.getType(), physicsModel.getParameters())
//                .build();
//    }

}
