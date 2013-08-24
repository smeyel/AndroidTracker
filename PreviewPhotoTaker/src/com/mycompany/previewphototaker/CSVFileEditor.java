package com.mycompany.previewphototaker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public class CSVFileEditor {
	
	/**
	 * Creates a .csv file from the folders and files in the PhotosForResearch folder.
	 * If there is an existing file, with the given filename, it will be overwritten.
	 * 
	 * @param filename Name of the .csv file, witch will be created.
	 */
	public void csvCreate(String filename) {
		
		try
	    {
			String root = Environment.getExternalStorageDirectory().getAbsolutePath();
			String dir = root + "/" + "PhotosForResearch";
			String fileNameWithType = filename + ".csv";
	        File tempfile = new File(dir, fileNameWithType);
	        FileWriter writer = new FileWriter(tempfile);
	        
	        File foldersandfilesDir = new File(dir);
	        String[] names = foldersandfilesDir.list();
	        for(String name : names)
	        {
	        	if(new File(dir + "/" + name).isDirectory())
	        	{
	        		File fileDir = new File(dir + "/" + name);
	        		File[] listedFiles = fileDir.listFiles();
	        		if(listedFiles != null)
	        		{
	        			for (File f : listedFiles) {
	        			    if (f.isFile())
	        			    {
	        			    	writer.append(fileDir.getAbsolutePath() + "/" + f.getName());
	        			    	writer.append("\n");
	        			    }
	        			}
	        		}
	        	}
	        }
	        writer.flush();
	        writer.close();
	    }
	    catch(IOException e)
	    {
	         e.printStackTrace();
	    }
		
	}
}
