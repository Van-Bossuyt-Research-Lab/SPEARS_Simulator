package com.csm.rover.simulator.objects.io;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ConfigurationFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || getSuffix(f).equalsIgnoreCase("cfg");
    }

    private String getSuffix(File f){
        return f.getName().substring(f.getName().lastIndexOf('.')+1);
    }

    @Override
    public String getDescription() {
        return "SPEARS Configuration Files";
    }
}
