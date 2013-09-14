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
	void cascadeDetectAndDisplay(cv::Mat image);

public:
	Detectors(string cascadeFilePath, string detectorName);
	Detectors(string cascadeFilePath_1, string detectorName_1, string cascadeFilePath_2, string detectorName_2);
	bool loadTestImageNames(string testInfoFilePath);
	void runTest(string testPicturesPath,string outputPath);
};

#endif