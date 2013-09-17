#include "Detectors.h"

using namespace cv;

//TODO mappaletrehozas
int main( void )
{
	Detectors haar("XmlForTest\\L_700_400_20.xml","LBP", 2);
	haar.runTest("PicturesForTest\\", "TestResult\\");
	/*Detectors HaarVsLbp(
		"XmlForTest\\H_700_400_20.xml","Haar", 
		"XmlForTest\\L_700_400_20.xml", "LBP",2); //constructor for comparison used*/
	//HaarVsLbp.runTest("PicturesForTest\\", "TestResult\\");


	return 0;
}