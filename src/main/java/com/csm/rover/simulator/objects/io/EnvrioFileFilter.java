package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.objects.CoverageIgnore;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * File filter for environment JSON files.
 */
public class EnvrioFileFilter extends FileFilter {

    private final String platfrom;

    /**
     * Requires the platform type name the environment belongs to.  To accept environments of any platform pass "*".
     *
     * @param platform The platform name type or wildcard
     */
    public EnvrioFileFilter(String platform){
        this.platfrom = platform;
    }

    /**
     * Checks if the given file is valid.  Also accepts directories for navigation purposes.
     *
     * @param file File to check
     * @return True for valid file or directory
     */
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

    @CoverageIgnore
	@Override
	public String getDescription() {
		return "SPEARS Environment Files";
	}
	
}
