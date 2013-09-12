#include "Detectors.h"
#include "opencv2/highgui/highgui.hpp"
#include <fstream>

#define RECT

using namespace cv;
using namespace std;

//constructor for cascade classifier
Detectors :: Detectors(string cascadeFilePath)
{
	if(!objectCascade.load(cascadeFilePath))
	{
		cout << "Error while loading the xml file\n";
		exit(0);
	}
}

//detector will be called and found objects will be marked
void Detectors :: cascadeDetectAndDisplay(Mat image)
{
    std::vector<Rect> objects;
    Mat frame_gray;

    cvtColor( image, frame_gray, COLOR_BGR2GRAY );
    equalizeHist( frame_gray, frame_gray );

    //Detect objects
	objectCascade.detectMultiScale( frame_gray, objects, 1.1, 2, 0|CASCADE_SCALE_IMAGE, Size(30, 30) );

    for ( size_t i = 0; i < objects.size(); i++ )
    {

#ifdef CIRCLE
		//Mark with circle
        Point center( objects[i].x + objects[i].width/2, objects[i].y + objects[i].height/2 );
        ellipse( image, center, Size( objects[i].width/2, objects[i].height/2 ), 0, 0, 360, Scalar( 255, 0, 255 ), 4, 8, 0 );
#endif
#ifdef RECT
		//Mark with rectangle
		Rect currentObject = objects[i];
		rectangle(image, currentObject, CV_RGB(0, 255,0), 1);
#endif
    }
}

//loads the names of test images from the info file
bool Detectors :: loadTestImageNames(string testInfoFilePath)
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
			testImageNames.push_back(tempString);
		}
		return true; //names loaded succesfully
	}
	else
	{
		return false; //file couldn't be opened
	}
}

//test the classifier, initialization is needed!
void Detectors :: runTest(string testPicturesPath,string outputPath)
{
	string fileNameAndPath;
	stringstream sstm;
	Mat image;

	for(int i=1; i<= testImageNames.size(); i++)
	{
		fileNameAndPath = testPicturesPath + testImageNames[i-1];
		image = imread(fileNameAndPath, CV_LOAD_IMAGE_ANYCOLOR);
        
		//Apply the classifier to the frame
        cascadeDetectAndDisplay( image );
		sstm << "TestResult/" << i << ".jpg";
		imwrite(sstm.str(), image);
		sstm.str("");
		cout<<"Picture tested: " << i << ".jpg\n";
	}
}