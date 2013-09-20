package com.mycompany.previewphototaker;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Vector;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.graphics.Bitmap;
import android.os.Environment;

public class SaveImageToExtStor {

	/**
	 * Saves a Mat to the external storage to "PhotosForResearch" folder as a jpg. Filename is generated for the saved image.
	 * 
	 * @param source The image to save.
	 * @param directoryName The directory where the 
	 * @return The name of the saved file.
	 */
	public String saveJpegImage(Mat source, String directoryName){

	    Mat mat = source.clone();
	    
	    String filename="nothing";

	    Bitmap bmpOut = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
	    Utils.matToBitmap(mat, bmpOut);
	    if (bmpOut != null){
	    	
	    	mat.release();
	    	OutputStream fout = null;
	    	String root = Environment.getExternalStorageDirectory().getAbsolutePath();
	    	String dir = root + "/" + "PhotosForResearch" + "/" + directoryName;
	    	
	    	filename = fileNameGenerator(dir);
	    	
	    	String fileWithType = filename + ".jpg";
	    	File file = new File(dir);
	    	file.mkdirs();
	    	file = new File(dir, fileWithType);

	        try {
	        	fout = new FileOutputStream(file);
	        	BufferedOutputStream bos = new BufferedOutputStream(fout);
	        	bmpOut.compress(Bitmap.CompressFormat.JPEG, 100, bos);
	        	bos.flush();
	        	bos.close();
	        	bmpOut.recycle();
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }

	        catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    bmpOut.recycle();
	    return filename;
	}
	
	/**
	 * Generates number values for the files in ascending order. 
	 * 
	 * @param dir The direction, where the files are.
	 * @return Generated file name.
	 */
	private String fileNameGenerator(String dir)
	{
		String fileName="1"; //if the directory does not exist, or it is empty, then fileName = "1"
		Vector<Integer> allFileNamesInt = new Vector<Integer>();
		int index;
		int fileCounter=0;
		File fileDir = new File(dir);
		File[] listedFiles = fileDir.listFiles();
		if(listedFiles != null) //if directory exists
		{
			for (File f : listedFiles) {
			    if (f.isFile())
			    {
			    	fileName = f.getName();
			    	if(fileName.indexOf(".jpg") != -1)
			    	{
				    	index = fileName.indexOf(".jpg");
				    	fileName = fileName.substring(0,index);
				    	/*fileNameIntTemp=Integer.getInteger(fileName);
				    	if(fileNameIntTemp != null)
				    	{*/
					    allFileNamesInt.add(fileCounter, Integer.parseInt(fileName));
					    fileCounter++;
				    	//}
			    	}
			    }
			}
			Collections.sort(allFileNamesInt);
			
			fileCounter = 1;
			for(@SuppressWarnings("unused") int fileNameInt : allFileNamesInt) {
				if(allFileNamesInt.get(fileCounter-1) > (fileCounter))
				{
					fileName = Integer.toString(fileCounter);
					return fileName;
				}
				fileCounter++;
				fileName = Integer.toString(fileCounter);
			}
		}
		return fileName;
	}
}
