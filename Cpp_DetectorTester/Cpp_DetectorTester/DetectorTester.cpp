#include "DetectorTester.h"
#include <direct.h>
#include "opencv2/highgui/highgui.hpp"
#include <fstream>

using namespace cv;
using namespace std;

//constructor for one cascade classifier
DetectorTester :: DetectorTester(string cascadeFilePath, string detectorName, int detectedCounter)
{
	if(detectedCounter < 1 )
	{
		detectedCounter = 1; //default
	}

	vector<int> tempVec;
	for(int i=0; i <= detectedCounter + 1 ; i++)
	{
		tempVec.push_back(0);
	}
	numDetectedObjects.push_back(tempVec);

	CascadeClassifier classTemp;
	if(!classTemp.load(cascadeFilePath))
	{
		cout << "Error while loading the xml file\n";
		exit(0);
	}
	objectCascadeVec.push_back(classTemp);

	this->detectorNames.push_back(detectorName);
}

//constructor for comparison of two or more cascade classifiers
DetectorTester :: DetectorTester(vector<string> cascadeFilePaths, vector<string> detectorNames, int detectedCounter)
{
	if(detectedCounter < 1 )
	{
		detectedCounter = 1; //default
	}

	vector<int> tempVec;
	for(int i=0; i <= detectedCounter +1 ; i++)
	{
		tempVec.push_back(0);//for the first detector
	}

	CascadeClassifier classTemp;
	for(unsigned int i=0; i< cascadeFilePaths.size(); i++)
	{
		numDetectedObjects.push_back(tempVec);
		if(!classTemp.load(cascadeFilePaths[i]))
		{
			cout << "Error while loading the xml file\n";
			exit(0);
		}
		objectCascadeVec.push_back(classTemp);
		this->detectorNames.push_back(detectorNames[i]);
	}
}

//detector will be called and found objects will be marked
void DetectorTester :: cascadeDetectAndDisplay(Mat image)
{
    vector<Rect> objects;
    Mat frame_gray;
	string box_text;


    cvtColor( image, frame_gray, COLOR_BGR2GRAY );
    equalizeHist( frame_gray, frame_gray );

	for(unsigned int detNum=0; detNum < objectCascadeVec.size(); detNum++)
	{
		//Detect objects
		objectCascadeVec[detNum].detectMultiScale( frame_gray, objects, 1.1, 2, 0|CASCADE_SCALE_IMAGE, Size(30, 30) );
		
		if(objects.size() == 0)
		{
			numDetectedObjects[detNum][0]++;
		}
		else if(objects.size() <= numDetectedObjects.size())
		{
			numDetectedObjects[detNum][objects.size()]++;
		}
		else
		{
			numDetectedObjects[detNum][numDetectedObjects.size()]++;
		}
		

		for ( size_t i = 0; i < objects.size(); i++ )
		{
			//Mark with rectangle
			Rect currentObject = objects[i];
			switch(detNum)
			{
			case 0:
				rectangle(image, currentObject, CV_RGB(255, 0, 0), 2);
				box_text = detectorNames[detNum];
				putText(image, box_text, Point(0, 20), FONT_HERSHEY_COMPLEX_SMALL, 1, CV_RGB(255,0,0), 2);
				break;
			case 1:
				rectangle(image, currentObject, CV_RGB(0, 255, 0), 2);
				box_text = detectorNames[detNum];
				putText(image, box_text, Point(0, 50), FONT_HERSHEY_COMPLEX_SMALL, 1, CV_RGB(0,255,0), 2);
				break;
			case 2:
				rectangle(image, currentObject, CV_RGB(0, 0, 255), 2);
				box_text = detectorNames[detNum];
				putText(image, box_text, Point(0, 80), FONT_HERSHEY_COMPLEX_SMALL, 1, CV_RGB(0,0,255), 2);
				break;
			case 3:
				rectangle(image, currentObject, CV_RGB(0, 0, 0), 2);
				box_text = detectorNames[detNum];
				putText(image, box_text, Point(0, 110), FONT_HERSHEY_COMPLEX_SMALL, 1, CV_RGB(0,0,0), 2);
				break;
			default:
				break;
			}
		}
	}
}

//loads the names of test images from the info file
bool DetectorTester :: loadTestImageNames(string testInfoFilePath)
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

bool DetectorTester :: writeResultsToFile(string path, string fileName)
{
	ofstream fileWriter;
	string pathAndFile = path + fileName;
	
	_mkdir(path.c_str());
	fileWriter.open(pathAndFile);

	for(unsigned int i=0; i<numDetectedObjects.size(); i++)
	{
		fileWriter<< detectorNames[i]<<"\n";
		for(unsigned int j=0; j<numDetectedObjects[i].size(); j++)
		{
			if(j <  numDetectedObjects[i].size() - 1)
			{
				fileWriter<<j<<";"<< numDetectedObjects[i][j]<<"\n";
			}
			else
			{
				fileWriter<<"More"<<";"<< numDetectedObjects[i][j]<<"\n";
				fileWriter<<"Valid"<<";"<<"\n\n";
			}
		}
	}

	fileWriter.close();
	return 0;
}

//Test the classifier, an info file named "info.txt" in the testpicture folder is needed! It must contain the names of the testimage files.
void DetectorTester :: runTest(string testPicturesPath,string outputPath)
{
	string fileNameAndPath;
	stringstream sstm;
	Mat image;
	string infoFilePath = testPicturesPath + "info.txt";
	
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
        
			//Apply the classifier to the image
			cascadeDetectAndDisplay( image );
			sstm << outputPath << i << ".jpg";
			imwrite(sstm.str(), image);
			sstm.str("");
			cout<<"Picture tested: " << i << ".jpg\n";
		}
		writeResultsToFile(outputPath, "result.csv");
	}
}