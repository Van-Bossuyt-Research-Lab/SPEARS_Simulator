package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.objects.CoverageIgnore;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * File filter for identifying configuration files.
 */
public class ConfigurationFileFilter extends FileFilter {

    /**
     * Checks if the given file is valid.  Also accepts directories for navigation purposes.
     *
     * @param file File to check
     * @return True for valid file or directory
     */
    @Override
    public boolean accept(File file) {
        return file.isDirectory() || getSuffix(file).equalsIgnoreCase("cfg");
    }

    private String getSuffix(File f){
        return f.getName().substring(f.getName().lastIndexOf('.')+1);
    }

    @CoverageIgnore
    @Override
    public String getDescription() {
        return "SPEARS Configuration Files";
    }
}
