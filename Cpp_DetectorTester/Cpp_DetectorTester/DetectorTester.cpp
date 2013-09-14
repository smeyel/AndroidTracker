#include "Detectors.h"

using namespace cv;

int main( void )
{
	Detectors HaarVsLbp(
		"XmlForTest\\H_700_400_20.xml","Haar", 
		"XmlForTest\\L_700_400_20.xml", "LBP"); //constructor for comparison used

	HaarVsLbp.loadTestImageNames("PicturesForTest\\info.txt"); //info file contains the list of filenames separated with "\n"
	HaarVsLbp.runTest("PicturesForTest\\", "TestResult\\"); //important: all folders must exist, that will be used!


	return 0;
}