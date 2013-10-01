package com.mycompany.previewphototaker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public class InfoFileWriter {
	
	/**
	 * Creates a .txt file in each folder.The .txt contains the filenames in the folder.
	 * If there is an existing file, with the given filename, it will be overwritten.
	 * 
	 * @param filename Name of the .txt file, witch will be created.
	 */
	public void writeInfoFiles(String filename) {
		
		try
	    {
			FileWriter writer = null;
			String root = Environment.getExternalStorageDirectory().getAbsolutePath();
			String dir = root + "/" + "PhotosForResearch";
	        
	        File foldersandfilesDir = new File(dir);
	        String[] names = foldersandfilesDir.list();
	        for(String name : names)
	        {
	        	if(new File(dir + "/" + name).isDirectory())
	        	{
	        		File filesDir = new File(dir + "/" + name);
	        		
	        		String fileNameWithType = filename + ".txt";
	    	        File tempfile = new File(filesDir, fileNameWithType);
	    	        writer = new FileWriter(tempfile);
	    	        
	        		File[] listedFiles = filesDir.listFiles();
	        		if(listedFiles != null)
	        		{
	        			for (File f : listedFiles) {
	        			    if (f.isFile())
	        			    {
	        			    	if(fileNameWithType.compareTo(f.getName()) != 0)
	        			    	{
		        			    	writer.append(f.getName());
		        			    	writer.append("\r\n");
	        			    	}
	        			    }
	        			}
	        		}
	        		writer.flush();
	    	        writer.close();
	        	}
	        }
	    }
	    catch(IOException e)
	    {
	         e.printStackTrace();
	    }
		
	}
}
