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

package com.spears.objects.io;

import com.spears.objects.CoverageIgnore;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * A swing FileFilter implementation that accepts image files in the form of jpeg, tif, png, and gif
 * as well as directories.
 */
public class ImageFileFilter extends FileFilter {

	/**
	 * Checks if the given file is valid.  Also accepts directories for navigation purposes.
	 *
	 * @param file File to check
	 * @return True for valid file or directory
	 */
	@Override
	public boolean accept(File file) {
		String path = file.getAbsolutePath();
		if (file.isDirectory()){
			return true;
		}
		int start = path.indexOf('.');
		String type = path.substring(start+1, path.length());
		return type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("jpeg") || type.equalsIgnoreCase("tif") || type.equalsIgnoreCase("png") || type.equalsIgnoreCase("gif");
	}

	@CoverageIgnore
	@Override
	public String getDescription() {
		return "Image Files";
	}
	
}