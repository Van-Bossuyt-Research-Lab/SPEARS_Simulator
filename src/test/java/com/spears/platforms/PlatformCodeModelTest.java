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
import org.junit.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class PlatformCodeModelTest extends PlatformAutonomousCodeModel {

    private static final String logFile = "temp/test.log", message = "message to the void";

    @BeforeClass
    public static void setup(){
        DatedFileAppenderImpl.Log_File_Name =  logFile;
    }

    public PlatformCodeModelTest(){
        super("Test");
    }

    @Test
    public void testCreatesFile(){
        super.setPlatformName("test_platform");
        super.writeToLog(message);
        assert (new File(logFile + "/" + platform_name + ".log")).exists();
    }

    @Test
    public void testWritesToFile() throws FileNotFoundException {
        super.setPlatformName("test_platform");
        super.writeToLog(message);
        Scanner fin = new Scanner(new File(logFile + "/" + platform_name + ".log"));
        fin.useDelimiter("\t");
        Assert.assertEquals(message, fin.next());
        fin.close();
    }

    @Test
    public void testDoNothingWithoutFile(){
        super.writeToLog("you can't hear me");
        super.writeToLog("still can't hear me");
        assert !(new File(logFile + "/" + platform_name + ".log")).exists();
    }

    @After
    public void cleanUpFiles() throws IOException {
        File log = new File(logFile + "/test_platform.log");
        File dir = new File(logFile);
        log.delete();
        dir.delete();
        if (log.exists() || dir.exists()){
            throw new IOException("Failed to delete test log files");
        }
        super.platform_name = "";
        super.logFile = null;
    }

    @Override
    public void setEnvironment(PlatformEnvironment enviro) {}
    @Override
    public void constructParameters(Map<String, Double> params) {}
    @Override
    public String nextCommand(long milliTime, PlatformState state) {
        return null;
    }
}
