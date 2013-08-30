package com.mycompany.desktopjava;

import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import com.mycompany.crossplatform.HaarDetectorTester;


public class Main {

	static{ System.loadLibrary("opencv_java246"); }
	
	public static void main(String[] args) {
		String dirOfXml = "E:\\Visual_Studio_Projects\\OpenCV_HaarTrainingSources\\OpenCV_HaarTraining_Haartraining\\trainout.xml"; //here is the directory of the detector xml given
		Mat matFrame = new Mat(); //E:\\opencv2_4_6\\opencv\\data\\haarcascades\\haarcascade_eye.xml
		final VideoCapture video = new VideoCapture();
		video.open(0);
		
		//GUI for preview
		JFrame frame = new JFrame("BasicPanel");  
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
	    frame.setSize(400,400);
	    frame.setVisible(true);
	    Graphics g1 = frame.getGraphics();
	    BufferedImage bufImage = null;
	    video.read(matFrame);
		frame.setSize(matFrame.width()+40,matFrame.height()+60);
		
		//Releasing the camera, if the window is closed
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
	        public void windowClosing(WindowEvent winEvt) {
	            video.release();
	            System.exit(0);
	        }
	    });
		
		//cross-plattform Haar detector tester
		HaarDetectorTester detTester = new HaarDetectorTester();
	    
		while(true)
		{
			video.read(matFrame);
			matFrame = detTester.testDetector(dirOfXml, matFrame);
			bufImage=matToBufferedImage(matFrame);
	        g1.drawImage(bufImage, 0, 0, null);
		}

	}
	
	/**
	 * 
	 * @param matrix The Mat, witch should be converted to BufferedImage 
	 * @return The image converted in BufferedImage
	 */
	public static BufferedImage matToBufferedImage(Mat matrix) {  
	     int cols = matrix.cols();  
	     int rows = matrix.rows();  
	     int elemSize = (int)matrix.elemSize();  
	     byte[] data = new byte[cols * rows * elemSize];  
	     int type;  
	     matrix.get(0, 0, data);  
	     switch (matrix.channels()) {  
	       case 1:  
	         type = BufferedImage.TYPE_BYTE_GRAY;  
	         break;  
	       case 3:  
	         type = BufferedImage.TYPE_3BYTE_BGR;  
	         // bgr to rgb  
	         byte b;  
	         for(int i=0; i<data.length; i=i+3) {  
	           b = data[i];  
	           data[i] = data[i+2];  
	           data[i+2] = b;  
	         }  
	         break;  
	       default:  
	         return null;  
	     }  
	     BufferedImage image2 = new BufferedImage(cols, rows, type);  
	     image2.getRaster().setDataElements(0, 0, cols, rows, data);  
	     return image2;  
	   }

}
