/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spears.platforms;

import com.spears.environments.PlatformEnvironment;
import com.spears.objects.io.DatedFileAppenderImpl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public abstract class PlatformAutonomousCodeModel {
    private static final Logger LOG = LogManager.getLogger(PlatformAutonomousCodeModel.class);

    protected File logFile = null;

    private boolean warnedAboutLog = false;

    protected final String platform_type;
    protected String platform_name;

    protected PlatformAutonomousCodeModel(String type){
        this.platform_type = type;
    }

    public abstract void setEnvironment(PlatformEnvironment enviro);

    public final String getType(){
        return platform_type;
    }

    public abstract void constructParameters(Map<String, Double> params);

    public abstract String nextCommand(long milliTime, final PlatformState state);

    public final void setPlatformName(String name){
        this.platform_name = name;
        logFile = new File(String.format("%s/%s.log", DatedFileAppenderImpl.Log_File_Name, platform_name));
        warnedAboutLog = false;
    }

    protected void writeToLog(String message){
        if (logFile == null){
            if (!warnedAboutLog){
                LOG.log(Level.WARN, "Logging was attempted but no log file is set -- further warnings suppressed");
                warnedAboutLog = true;
            }
            return;
        }
        if (!logFile.exists()) {
            try {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
                LOG.log(Level.INFO, "Writing rover {}'s autonomous log file to: {}", platform_name, logFile.getAbsolutePath());
            }
            catch (IOException e){
                LOG.log(Level.ERROR, platform_name + "'s autonomous log file failed to initialize.", e);
                logFile = null;
            }
        }
        try {
            BufferedWriter write = new BufferedWriter(new FileWriter(logFile, true));
            write.write(message + "\t\t" +
                    new DateTime().toString(DateTimeFormat.forPattern("[MM/dd/yyyy hh:mm:ss.SS]" +
                            System.getProperty("line.separator"))));
            write.flush();
            write.close();
        }
        catch (IOException e) {
            LOG.log(Level.ERROR, "Failed to write to autonomous log file for " + platform_name, e);
        }
    }

}
