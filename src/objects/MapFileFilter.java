package objects;
import java.io.File;
import java.io.Serializable;

import javax.swing.filechooser.FileFilter;

public class MapFileFilter extends FileFilter implements Serializable {
	
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
