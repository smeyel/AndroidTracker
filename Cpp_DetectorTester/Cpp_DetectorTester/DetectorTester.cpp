#include "DetectorTester.h"
#include <direct.h>
#include "opencv2/highgui/highgui.hpp"
#include <fstream>

using namespace cv;
using namespace std;

//loads the names of test images from the info file
bool DetectorTools :: loadTestImageNames(string testInfoFilePath)
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

//constructor for one cascade classifier
DetectorTester :: DetectorTester(string cascadeFilePath, string detectorName, int detectedCounter)
{
	if(detectedCounter < 1 )
	{
		detectedCounter = 1; //default
	}

	this->detectedCounter = detectedCounter;

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

	this->detectedCounter = detectedCounter;

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
		else if(objects.size() <= detectedCounter)
		{
			numDetectedObjects[detNum][objects.size()]++;
		}
		else
		{
			numDetectedObjects[detNum][detectedCounter+1]++;
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

//write the result statistics into a file
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

//generates images, that contain only the edges
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

// does the canny edge detection and masking
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

void CrossValidationTools:: createFourFilesFromAnnotatedFile(string txtFilePath)
{
	ifstream annotationFile;
	ofstream infoFile;
	ofstream txtFile;
	ostringstream convert;
	string actualLine;
	string tempString;
	string filePath;
	string fileName;
	vector<string> vec1;
	vector<string> vec2;
	vector<string> vec3;
	vector<string> vec4;
	Mat image;

	int lineCounter = 1;
	int beginPos;
	int endPos;
	annotationFile.open(txtFilePath);

	if(annotationFile.is_open())
	{
		while(!annotationFile.eof())
		{	
			getline(annotationFile, actualLine);
			switch (lineCounter%4)
			{
			case 1:
				vec1.push_back(actualLine);
				break;
			case 2:
				vec2.push_back(actualLine);
				break;
			case 3:
				vec3.push_back(actualLine);
				break;
			case 0:
				vec4.push_back(actualLine);
				break;
			default:
				break;
			}
			lineCounter++;
		}
		annotationFile.close();
	}
	else
	{
		cout<<"The file couldn't be opened\n";
	}

	mkdir("cross_validation");
	mkdir("cross_validation\\annotation_files");

	txtFile.open("cross_validation\\annotation_files\\train_4.txt");
	for(int i=0; i < vec1.size(); i++)
	{
		txtFile<<vec1[i]<<"\n";
	}
	for(int i=0; i < vec2.size(); i++)
	{
		txtFile<<vec2[i]<<"\n";
	}
	for(int i=0; i < vec3.size(); i++)
	{
		txtFile<<vec3[i]<<"\n";
	}
	txtFile.close();
	mkdir("cross_validation\\testset_4");
	infoFile.open("cross_validation\\testset_4\\info.txt");
	for(int i=0; i < vec4.size(); i++)
	{
		tempString = vec4[i];
		if(tempString != "")
		{
			endPos = tempString.find("jpg");
			filePath = tempString.substr(0,endPos);
			filePath += "jpg";

			beginPos = filePath.find_last_of("\\");
			fileName = filePath.substr(beginPos+1,endPos);

			image = imread(filePath);
			filePath = "cross_validation\\testset_4\\";
			filePath += fileName;
			imwrite(filePath, image);
			cout<<fileName<<"\n";

			infoFile<<fileName<<"\n";
		}
	}
	infoFile.close();


	txtFile.open("cross_validation\\annotation_files\\train_2.txt");
	for(int i=0; i < vec3.size(); i++)
	{
		txtFile<<vec3[i]<<"\n";
	}
	for(int i=0; i < vec4.size(); i++)
	{
		txtFile<<vec4[i]<<"\n";
	}
	for(int i=0; i < vec1.size(); i++)
	{
		txtFile<<vec1[i]<<"\n";
	}
	txtFile.close();
	mkdir("cross_validation\\testset_2");
	infoFile.open("cross_validation\\testset_2\\info.txt");
	for(int i=0; i < vec2.size(); i++)
	{
		tempString = vec2[i];
		if(tempString != "")
		{
			endPos = tempString.find("jpg");
			filePath = tempString.substr(0,endPos);
			filePath += "jpg";

			beginPos = filePath.find_last_of("\\");
			fileName = filePath.substr(beginPos+1,endPos);

			image = imread(filePath);
			filePath = "cross_validation\\testset_2\\";
			filePath += fileName;
			imwrite(filePath, image);
			cout<<fileName<<"\n";

			infoFile<<fileName<<"\n";
		}
	}
	infoFile.close();

	txtFile.open("cross_validation\\annotation_files\\train_3.txt");
	for(int i=0; i < vec4.size(); i++)
	{
		txtFile<<vec4[i]<<"\n";
	}
	for(int i=0; i < vec1.size(); i++)
	{
		txtFile<<vec1[i]<<"\n";
	}
	for(int i=0; i < vec2.size(); i++)
	{
		txtFile<<vec2[i]<<"\n";
	}
	txtFile.close();
	mkdir("cross_validation\\testset_3");
	infoFile.open("cross_validation\\testset_3\\info.txt");
	for(int i=0; i < vec3.size(); i++)
	{
		tempString = vec3[i];
		if(tempString != "")
		{
			endPos = tempString.find("jpg");
			filePath = tempString.substr(0,endPos);
			filePath += "jpg";

			beginPos = filePath.find_last_of("\\");
			fileName = filePath.substr(beginPos+1,endPos);

			image = imread(filePath);
			filePath = "cross_validation\\testset_3\\";
			filePath += fileName;
			imwrite(filePath, image);
			cout<<fileName<<"\n";

			infoFile<<fileName<<"\n";
		}
	}
	infoFile.close();


	txtFile.open("cross_validation\\annotation_files\\train_1.txt");
	for(int i=0; i < vec2.size(); i++)
	{
		txtFile<<vec2[i]<<"\n";
	}
	for(int i=0; i < vec3.size(); i++)
	{
		txtFile<<vec3[i]<<"\n";
	}
	for(int i=0; i < vec4.size(); i++)
	{
		txtFile<<vec4[i]<<"\n";
	}
	txtFile.close();
	mkdir("cross_validation\\testset_1");
	infoFile.open("cross_validation\\testset_1\\info.txt");
	for(int i=0; i < vec1.size(); i++)
	{
		tempString = vec1[i];
		if(tempString != "")
		{
			endPos = tempString.find("jpg");
			filePath = tempString.substr(0,endPos);
			filePath += "jpg";

			beginPos = filePath.find_last_of("\\");
			fileName = filePath.substr(beginPos+1,endPos);

			image = imread(filePath);
			filePath = "cross_validation\\testset_1\\";
			filePath += fileName;
			imwrite(filePath, image);
			cout<<fileName<<"\n";

			infoFile<<fileName<<"\n";
		}
	}
	infoFile.close();

	return;
}