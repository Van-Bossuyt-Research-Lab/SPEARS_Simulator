package objects;
import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FileNameFilter extends FileFilter{
	
	@Override
	public boolean accept(File arg0) {
		String path = arg0.getAbsolutePath();
		if (arg0.isDirectory()){
			return true;
		}
		return path.substring(path.length()-4, path.length()).equals(".map");
	}
	
	@Override
	public String getDescription() {
		return "Simulator Map Files";
	}
	
}
