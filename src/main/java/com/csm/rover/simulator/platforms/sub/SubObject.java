package com.csm.rover.simulator.platforms.sub;

import com.csm.rover.simulator.objects.SynchronousThread;
import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.wrapper.Globals;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@com.csm.rover.simulator.platforms.annotations.Platform(type="Sub")
public class SubObject extends Platform {
    private static final Logger LOG = LogManager.getLogger(SubObject.class);

    private Globals globals;

    private long nextOperationTime;

    public SubObject(){
        super("Sub");
        globals = Globals.getInstance();
    }

    @Override
    public void start(){
        nextOperationTime = globals.timeMillis() + 10000;
        new SynchronousThread(100, this::executeCode, SynchronousThread.FOREVER, name+"-code");
        physicsModel.start();
    }

    private void executeCode(){
        try {
            if (globals.timeMillis() > nextOperationTime) {
                String cmd = autonomousCodeModel.nextCommand(
                        Globals.getInstance().timeMillis(),
                        physicsModel.getState()
                );
                switch (cmd) {
                    case "":
                        break;
                    case "delay":
                        nextOperationTime = globals.timeMillis() + 1000;
                        break;
                    default:
                        physicsModel.sendDriveCommand(cmd);
                        break;
                }
            }
        }
        catch (Exception e) {
            LOG.log(Level.ERROR, name + ": Error in run code", e);
        }
    }

}
