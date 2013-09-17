#include "Detectors.h"

using namespace cv;

int main( void )
{
	vector<string> cascadeFilePaths;
	vector<string> detectorNames;

	cascadeFilePaths.push_back("XmlForTest\\L_700_400_20.xml");
	cascadeFilePaths.push_back("XmlForTest\\H_700_400_20.xml");
	cascadeFilePaths.push_back("XmlForTest\\H_500_200_20.xml");
	detectorNames.push_back("LBP_700_400_20");
	detectorNames.push_back("Haar_700_400_20");
	detectorNames.push_back("Haar_500_200_20");

	Detectors haarVsLBP(cascadeFilePaths,detectorNames, 2);
	haarVsLBP.runTest("PicturesForTest\\", "NewDir\\");

	return 0;
}