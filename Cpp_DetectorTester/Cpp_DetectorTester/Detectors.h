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
	cv::CascadeClassifier objectCascade;
	vector<string> testImageNames;
	void cascadeDetectAndDisplay(cv::Mat image);
public:
	Detectors(string cascadeFilePath);
	bool loadTestImageNames(string testInfoFilePath);
	void runTest(string testPicturesPath,string outputPath);
};

#endif