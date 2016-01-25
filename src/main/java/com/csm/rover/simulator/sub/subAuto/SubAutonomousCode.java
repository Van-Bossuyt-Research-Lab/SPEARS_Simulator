package com.csm.rover.simulator.sub.subAuto;

import com.csm.rover.simulator.control.InterfaceAccess;
import com.csm.rover.simulator.map.SubMap;
import com.csm.rover.simulator.objects.DecimalPoint;
import com.csm.rover.simulator.wrapper.Globals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Map;

public abstract class SubAutonomousCode implements Serializable, Cloneable {
    private static final Logger LOG = LogManager.getLogger(SubAutonomousCode.class);

    private static final long serialVersionUID = 1L;

    protected static SubMap SMAP;

    private String name;
    private String roverName;

    private File logFile;

    public SubAutonomousCode(String name, String rover){
        this.name = name;
        roverName = rover;
    }

    public static void setSubMap(SubMap map){
        SMAP = map;
    }

    public SubAutonomousCode(SubAutonomousCode rac){
        this.name = rac.name;
        this.roverName = rac.roverName;
    }

    abstract public String nextCommand(
            long milliTime,
            DecimalPoint location,
            double direction,
            Map<String, Double> parameters
    );

    abstract public String nextCommand();

    public void setRoverName(String name){
        roverName = name;
    }

    public String getName(){
        return name;
    }

    private boolean tried = false;
    protected void writeToLog(String message){
        try {
            BufferedWriter write = new BufferedWriter(new FileWriter(logFile, true));
            
            write.flush();
            write.close();
        }
        catch (NullPointerException e){
            if (!tried){
                tried = true;


                writeToLog(message);
            }
            else {
                e.printStackTrace();

            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public abstract SubAutonomousCode clone();

}