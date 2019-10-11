package pt.uminho.ceb.biosystems.merlin.tests.files;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class random {
	
	@Test
	public void copyFolders() throws IOException{
		
		File newWorkspace = new File("C:\\Users\\BioSystems\\merlin4\\merlin-aibench\\ws\\tvol6");
		
		File oldWorkspace = new File("C:\\Users\\BioSystems\\releases\\merlin-3.9.5-beta\\ws\\tvol");
		
		org.apache.commons.io.FileUtils.copyDirectory(oldWorkspace, newWorkspace);
		
	}

}
