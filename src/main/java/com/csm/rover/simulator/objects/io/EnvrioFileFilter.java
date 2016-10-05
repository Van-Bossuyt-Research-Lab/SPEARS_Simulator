package com.csm.rover.simulator.objects.io;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class EnvrioFileFilter extends FileFilter {

    private final String platfrom;

    public EnvrioFileFilter(String platform){
        this.platfrom = platform;
    }

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()){
            return true;
        }
        String[] parts = file.getName().split("\\.");
        if (parts.length < 3){
            return false;
        }
        if (!parts[parts.length-1].equalsIgnoreCase("env")){
            return false;
        }
        return platfrom.equals("*") || parts[parts.length-2].equalsIgnoreCase(platfrom);
	}
	
	@Override
	public String getDescription() {
		return "SPEARS Environment Files";
	}
	
}
