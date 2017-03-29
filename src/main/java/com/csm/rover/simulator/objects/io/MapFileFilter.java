package com.csm.rover.simulator.objects.io;

import com.csm.rover.simulator.objects.CoverageIgnore;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * A swing FileFilter that accepts SPEARS map files as well as directories.
 */
public class MapFileFilter extends FileFilter {

	/**
	 * Checks if the given file is valid.  Also accepts directories for navigation purposes.
	 *
	 * @param file File to check
	 * @return True for valid file or directory
	 */
	@Override
	public boolean accept(File file) {
		String path = file.getAbsolutePath();
		return file.isDirectory() || path.substring(path.length() - 4, path.length()).equalsIgnoreCase(".map");
	}

	@CoverageIgnore
	@Override
	public String getDescription() {
		return "Simulator Map Files";
	}
	
}
