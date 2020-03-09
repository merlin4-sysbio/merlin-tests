package pt.uminho.ceb.biosystems.merlin.tests.Diogo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

import pt.uminho.ceb.biosystems.merlin.utilities.io.FileUtils;
import pt.uminho.ceb.biosystems.mew.utilities.datastructures.collection.CollectionUtils;

public class testZipFiles {

	
	static String DIR_SEP = "/";
	
	
	@Test
	public void main() throws IOException {
		
		String path = "C:\\Users\\BioSystems\\Desktop\\keggXMLbackup\\";
		String dest = "C:\\Users\\BioSystems\\Desktop\\kegg.zip";
		int compressionLevel = 5;
		boolean includeParentFolders = false;
		createZipFile(path, dest, compressionLevel, includeParentFolders);
		
	}
	
	public void createZipFile(String path, String dest, int compressionLevel, boolean includeParentFolders) throws IOException {
        
        File destFile = new File(dest);
        if(!destFile.exists()){
            FileUtils.createFoldersFromPath(destFile.getParent());
            destFile.createNewFile();
        }
        
        path = windowsWorkaround(path);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destFile));
        try{
            out.setLevel(compressionLevel);
            
            byte[] buf = new byte[10*1024];
            
            File dir = new File(path);
            List<String> filenames = directoryTree(dir, includeParentFolders);
            String[] dirs = path.split(DIR_SEP);
            path = CollectionUtils.join(Arrays.copyOfRange(dirs, 0, dirs.length-1), DIR_SEP) + DIR_SEP;
            for (int i=0; i< filenames.size(); i++) {
                String filename = filenames.get(i);
                File file = new File(filename);
                String relativeFilename = filename.replace(path, "");
               
                if(file.isDirectory()){
                    
                    ZipEntry k = new ZipEntry(relativeFilename+"/");
                    out.putNextEntry(k);
                    
                }else {
                    
                	relativeFilename = relativeFilename.split("/")[1];
                    InputStream in = new FileInputStream(filename);
                    try{
                        out.putNextEntry(new ZipEntry(relativeFilename));
                        
                        int len;
                        while ((len = in.read(buf)) > 0)
                            out.write(buf, 0, len);
                    }finally{
                        in.close();
                    }
                }
                out.closeEntry();
            }
        }finally{
            out.close();
        }
    }
	
	  public List<String> directoryTree(File file, boolean includeParentFolders) {
	        List<String> fileList = new ArrayList<String>();
	       
	        if(file.isDirectory()) {
	            for(File child : file.listFiles())
	                fileList.addAll(directoryTree(child,includeParentFolders));
	            if(includeParentFolders)
	            	fileList.add(windowsWorkaround(file.getPath()));
	        }
	        else {
	        	
	        	fileList.add(windowsWorkaround(file.getPath()));
	        }
	        
	        return fileList;
	    }

	    public String windowsWorkaround(String filePath) {
	        return filePath.replaceAll("\\"+ System.getProperty("file.separator"), DIR_SEP);
	    }
	    
}
