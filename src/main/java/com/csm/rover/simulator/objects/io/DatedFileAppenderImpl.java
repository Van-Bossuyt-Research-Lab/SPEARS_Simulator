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

package com.csm.rover.simulator.objects.io;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.*;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Integers;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

@Plugin(name="DatedFile", category="Core", elementType="appender", printObject=true)
public final class DatedFileAppenderImpl extends AbstractOutputStreamAppender<FileManager> {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private final String fileName;
    private final Advertiser advertiser;
    private Object advertisement;

    public static String Log_File_Name;

    private DatedFileAppenderImpl(String name, Layout<? extends Serializable> layout, Filter filter, FileManager manager, String filename, boolean ignoreExceptions, boolean immediateFlush, Advertiser advertiser) {
        super(name, layout, filter, ignoreExceptions, immediateFlush, manager);
        if(advertiser != null) {
            HashMap configuration = new HashMap(layout.getContentFormat());
            configuration.putAll(manager.getContentFormat());
            configuration.put("contentType", layout.getContentType());
            configuration.put("name", name);
            this.advertisement = advertiser.advertise(configuration);
        }

        this.fileName = filename;
        Log_File_Name = filename.substring(0, filename.lastIndexOf('.'));
        this.advertiser = advertiser;
    }

    public void stop() {
        super.stop();
        if(this.advertiser != null) {
            this.advertiser.unadvertise(this.advertisement);
        }

    }

    public String getFileName() {
        return this.fileName;
    }

    @PluginFactory
    public static DatedFileAppenderImpl createAppender(
            @PluginAttribute("rootpath") String rootpath,
            @PluginAttribute("append") String append,
            @PluginAttribute("locking") String locking,
            @PluginAttribute("name") String name,
            @PluginAttribute("immediateFlush") String immediateFlush,
            @PluginAttribute("ignoreExceptions") String ignore,
            @PluginAttribute("bufferedIo") String bufferedIo,
            @PluginAttribute("bufferSize") String bufferSizeStr,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") Filter filter,
            @PluginAttribute("advertise") String advertise,
            @PluginAttribute("advertiseUri") String advertiseUri,
            @PluginConfiguration Configuration config) {
        boolean isAppend = Booleans.parseBoolean(append, true);
        boolean isLocking = Boolean.parseBoolean(locking);
        boolean isBuffered = Booleans.parseBoolean(bufferedIo, true);
        boolean isAdvertise = Boolean.parseBoolean(advertise);
        if(isLocking && isBuffered) {
            if(bufferedIo != null) {
                LOGGER.warn("Locking and buffering are mutually exclusive. No buffering will occur for log file");
            }

            isBuffered = false;
        }

        int bufferSize = Integers.parseInt(bufferSizeStr, 8192);
        if(!isBuffered && bufferSize > 0) {
            LOGGER.warn("The bufferSize is set to {} but bufferedIO is not true: {}", new Object[]{Integer.valueOf(bufferSize), bufferedIo});
        }

        if (rootpath == null){
            rootpath = "";
        }

        boolean isFlush = Booleans.parseBoolean(immediateFlush, true);
        boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
        if(name == null) {
            LOGGER.error("No name provided for FileAppender");
            return null;
        }
        else {
            if(layout == null) {
                layout = PatternLayout.createDefaultLayout();
            }

            String fileName = generateFilename(rootpath);

            FileManager manager = FileManager.getFileManager(fileName, isAppend, isLocking, isBuffered, advertiseUri, (Layout)layout, bufferSize);
            return manager == null?null:new DatedFileAppenderImpl(name, (Layout)layout, filter, manager, fileName, ignoreExceptions, isFlush, isAdvertise?config.getAdvertiser():null);
        }
    }

    private static String generateFilename(String rootpath){
        String filename;
        DateTime date = new DateTime();
        int ver = 1;
        do {
            filename = String.format("%s/%d/%d/%d/LOG_%s_(%d).log", rootpath, date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), date.toString(DateTimeFormat.forPattern("MM-dd-yyyy_HH.mm")), ver);
            ver++;
        } while (new File(filename).exists());
        return filename;
    }
}