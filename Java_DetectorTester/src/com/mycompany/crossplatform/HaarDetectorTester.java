package com.mycompany.crossplatform;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;

public class HaarDetectorTester {
/**
 * This function can be used on desktop Java and Android plattform.
 * 
 * @param xmlDirectory Directory of the xml file of the detector.
 * @param mRgbFrame The actual frame of the video / tested picture.
 */
	public Mat testDetector(String xmlDirectory, Mat mRgb){
		
		CascadeClassifier detector = new CascadeClassifier(xmlDirectory);
		MatOfRect faceDetections = new MatOfRect();
		detector.detectMultiScale(mRgb, faceDetections);
		
		for (Rect rect : faceDetections.toArray()) {
	        Core.rectangle(mRgb, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
	    }
		return mRgb;
	}
}
