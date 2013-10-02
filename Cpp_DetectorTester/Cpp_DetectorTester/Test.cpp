#include "DetectorTester.h"

//#define MORE_DETECTOR
//#define CANNYTEST
//#define BMW
#define KONYV

using namespace cv;

int main( void )
{

#ifdef KONYV
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
	haarVsLBP.runTest("konyv\\konyv_ForTest\\konyv_pos_1\\", "konyv\\konyv_Result\\posTest\\");
#endif

#ifdef BMW
	vector<string> cascadeFilePaths;
	vector<string> detectorNames;

	cascadeFilePaths.push_back("bmw\\bmw_XmlForTest\\H_500_200_20.xml");
	cascadeFilePaths.push_back("bmw\\bmw_XmlForTest\\H_700_400_18.xml");
	cascadeFilePaths.push_back("bmw\\bmw_XmlForTest\\L_700_400_20.xml");
	cascadeFilePaths.push_back("bmw\\bmw_XmlForTest\\L_1800_1200_10_02_0999.xml");
	detectorNames.push_back("Haar_500_200_20");
	detectorNames.push_back("Haar_700_400_18");
	detectorNames.push_back("LBP_700_400_20");
	detectorNames.push_back("LBP_1800_1200_10_0.2_0.999");

	DetectorTester haarVsLBP(cascadeFilePaths,detectorNames, 2);
	haarVsLBP.runTest("bmw\\bmw_ForTest\\bmw_papir\\", "bmw\\bmw_Results\\bmw_papir\\");
#endif

#ifdef ONE_DETECTOR
	DetectorTester haar("XmlForTest\\L_700_400_20.xml","Haar", 3);
	haar.runTest("PicturesForTest\\", "NewDir\\");
#endif

#ifdef CANNYTEST
	CannyTools test;
	test.createCannyImages("mcdon\\mcdon_tan\\", "mcdon\\mcdon_tan_canny\\");
#endif

	return 0;
}