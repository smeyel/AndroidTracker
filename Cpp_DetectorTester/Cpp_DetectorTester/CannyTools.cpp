#include "CannyTools.h"
#include "opencv2/highgui/highgui.hpp"
#include <fstream>
#include <direct.h>

using namespace cv;
using namespace std;

void CannyTools :: createCannyImages(string testPicturesPath, string outputPath, int lowThreshold, int kernelSize)
{
	Mat image;
	Mat cannyImage;

	string infoFilePath = testPicturesPath + "info.txt";
	string fileNameAndPath;
	stringstream sstm;

	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;

	_mkdir(outputPath.c_str());

	if(!loadTestImageNames(infoFilePath)) //info file contains the list of filenames separated with "\n"
	{
		cout << "Error while loading the testfile names\n";
	}
	else
	{
		for(unsigned int i=1; i<= testImageNames.size(); i++)
		{
			fileNameAndPath = testPicturesPath + testImageNames[i-1];
			image = imread(fileNameAndPath, CV_LOAD_IMAGE_ANYCOLOR);
			
			cannyImage = doCannyOnMat(image, lowThreshold, kernelSize);

			sstm << outputPath << i << ".jpg";
			imwrite(sstm.str(), cannyImage);
			sstm.str("");
			cout<<"Picture created: " << i << ".jpg\n";
		}
	}
}

//loads the names of test images from the info file
bool CannyTools :: loadTestImageNames(string testInfoFilePath)
{
	string tempString;
	char tempCharArr[250];
	ifstream infile;
	infile.open(testInfoFilePath);
    if(infile.is_open())
	{
		while(!(infile.eof()))
		{
			infile.getline(tempCharArr,250);
			tempString = tempCharArr;
			if(tempString.find(".jpg") != string::npos) //if the file is .jpg
			{
				testImageNames.push_back(tempString);
			}
		}
		return true; //names loaded succesfully
	}
	else
	{
		return false; //file couldn't be opened
	}
}

Mat CannyTools:: doCannyOnMat(Mat sourceImage, int lowThreshold, int kernel_size, int ratio)
{
	Mat grayImage;
	Mat detectedEdges;
	Mat cannyImage;

	cannyImage.create(sourceImage.size(), sourceImage.type());
	cvtColor(sourceImage, grayImage, CV_BGR2GRAY);
	// Reduce noise with a kernel 3x3
	blur( grayImage, detectedEdges, Size(3,3) );
	// Canny detector
	Canny( detectedEdges, detectedEdges, lowThreshold, lowThreshold*ratio, kernel_size );	
	// Using Canny's output as a mask, we display our result
	cannyImage = Scalar::all(0); 
	sourceImage.copyTo(cannyImage, detectedEdges);
	return cannyImage;
}
