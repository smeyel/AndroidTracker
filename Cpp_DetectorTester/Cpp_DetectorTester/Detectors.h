#ifndef DETECTORS_H
#define DETECTORS_H
#include <iostream>
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/objdetect/objdetect.hpp"
#include <vector>

using std:: vector;
using std:: string;

class Detectors
{
private:
	vector<cv::CascadeClassifier> objectCascadeVec;
	vector<string> testImageNames;
	vector<string> detectorNames;										
	vector< vector<int> > numDetectedObjects;  //[detectornumber][number of pictures, where 'index + 1' objects are found]
											  // example: numDetectedObjects[0][1] --> Returns that the 0th detector has found 'value' pictures, where 2 objects are found
	void cascadeDetectAndDisplay(cv::Mat image);
	bool loadTestImageNames(string testInfoFilePath);

public:
	Detectors(string cascadeFilePath, string detectorName, int detectedCounter);
	Detectors(string cascadeFilePath_1, string detectorName_1, string cascadeFilePath_2, string detectorName_2, int detectedCounter);
	void runTest(string testPicturesPath,string outputPath);
};

#endif