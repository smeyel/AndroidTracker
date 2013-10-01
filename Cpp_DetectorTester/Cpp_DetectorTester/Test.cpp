#include "DetectorTester.h"
#include "CannyTools.h"

//#define MORE_DETECTOR
#define CANNYTEST

using namespace cv;

int main( void )
{

#ifdef MORE_DETECTOR
	vector<string> cascadeFilePaths;
	vector<string> detectorNames;

	cascadeFilePaths.push_back("konyv\\konyv_XmlForTest\\750_900_10_0.2_l.xml");
	cascadeFilePaths.push_back("konyv\\konyv_XmlForTest\\750_900_12_0.2_l.xml");
	cascadeFilePaths.push_back("konyv\\konyv_XmlForTest\\750_900_11_l.xml");
	cascadeFilePaths.push_back("konyv\\konyv_XmlForTest\\750_900_12_h.xml");
	detectorNames.push_back("LBP_750_900_10_0.2");
	detectorNames.push_back("LBP_750_900_12_0.2");
	detectorNames.push_back("LBP_750_900_11_0.5");
	detectorNames.push_back("Haar_750_900_12_0.5");

	DetectorTester haarVsLBP(cascadeFilePaths,detectorNames, 2);
	haarVsLBP.runTest("konyv\\konyv_ForTest\\konyv_pos\\", "konyv\\konyv_Result\\posTest\\");
#endif

#ifdef ONE_DETECTOR
	DetectorTester haar("XmlForTest\\L_700_400_20.xml","Haar", 3);
	haar.runTest("PicturesForTest\\", "NewDir\\");
#endif

#ifdef CANNYTEST
	CannyTools test;
	test.createCannyImages("mcdon\\PreviousCannyTestSource\\", "mcdon\\PreviousCannyTestResult\\");
#endif

	return 0;
}