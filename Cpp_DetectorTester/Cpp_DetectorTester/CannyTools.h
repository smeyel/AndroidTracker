#ifndef CANNY_H
#define CANNY_H

#include <iostream>
#include <vector>
#include "opencv2/imgproc/imgproc.hpp"

using std:: vector;
using std:: string;
using cv:: Mat;

class CannyTools
{
private:

	vector<string> testImageNames;
	bool loadTestImageNames(string testInfoFilePath);

public:
	void createCannyImages(string testPicturesPath, string outputPath, int lowThreshold = 50, int kernelSize = 3);
	Mat doCannyOnMat(Mat sourceImage, int lowThreshold, int kernel_size, int ratio = 3); //2:1 or 3:1 ratio is recommended 
};

#endif