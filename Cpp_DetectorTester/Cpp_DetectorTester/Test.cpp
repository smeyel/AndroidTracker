#include "DetectorTester.h"

//#define CANNY
//#define BMW
//#define KONYV
//#define UVEG
//#define MCDON
//#define ONE_DETECTOR
#define CREATEANNFILES

using namespace cv;

int main( void )
{

#ifdef KONYV
	vector<string> cascadeFilePaths;
	vector<string> detectorNames;

	cascadeFilePaths.push_back("konyv\\konyv_XmlForTest\\900_900_15_0.2_0.999_l.xml");
	cascadeFilePaths.push_back("konyv\\konyv_XmlForTest\\750_900_11_l.xml");
	detectorNames.push_back("LBP_900_900_15_0.2_0.999_M");
	detectorNames.push_back("LBP_750_900_11_0.5");

	DetectorTester haarVsLBP(cascadeFilePaths,detectorNames, 2);
	haarVsLBP.runTest("konyv\\konyv_ForTest\\konyv_pos_4\\", "konyv\\konyv_Result\\posTest4\\");
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
	DetectorTester haar("mcdon\\mcdon_XmlForTest\\mcdon_canny_250_500_8_0.2_0.999_h.xml","Haar_250_500_8_0.2_0.999", 2);
	haar.runTest("mcdon\\mcdon_ForTest\\mcdon_test_canny\\", "mcdon\\mcdon_Results\\posCannyTest\\");
#endif

#ifdef CANNY
	CannyTools test;
	test.createCannyImages("mcdon\\mcdon_canny_gen\\mcdon_test_1\\", "mcdon\\mcdon_canny_gen\\mcdon_test_result\\");
#endif

#ifdef UVEG
	vector<string> cascadeFilePaths;
	vector<string> detectorNames;

	cascadeFilePaths.push_back("sor\\sor_XmlForTest\\uveg_900_1000_17_0.3_0.999_l_merg.xml");
	cascadeFilePaths.push_back("sor\\sor_XmlForTest\\uveg_500_1000_14_0.4_h_ann.xml");
	cascadeFilePaths.push_back("sor\\sor_XmlForTest\\uveg_900_1000_15_h_merg.xml");
	
	detectorNames.push_back("LBP_900_1000_17_0.3_0.999_merg");
	detectorNames.push_back("Haar_500_1000_14_0.4_ann");
	detectorNames.push_back("Haar_900_1000_15_merg");

	DetectorTester haarVsLBP(cascadeFilePaths,detectorNames, 2);
	haarVsLBP.runTest("sor\\sor_ForTest\\sor_test_1\\", "sor\\sor_Results\\posTest1\\");
#endif

#ifdef MCDON
	vector<string> cascadeFilePaths;
	vector<string> detectorNames;

	cascadeFilePaths.push_back("mcdon\\mcdon_XmlForTest\\mcdon_325_650_10_0.2_0.999_h_mer.xml");
	cascadeFilePaths.push_back("mcdon\\mcdon_XmlForTest\\mcdon_325_650_14_0.2_0.999_l_mer.xml");
	
	detectorNames.push_back("Haar_325_650_10_0.2_0.999_merg");
	detectorNames.push_back("LBP_325_650_14_0.2_0.999_merg");

	DetectorTester haarVsLBP(cascadeFilePaths,detectorNames, 2);
	haarVsLBP.runTest("mcdon\\mcdon_ForTest\\mcdon_test_1\\", "mcdon\\mcdon_Results\\posTest1\\");
#endif

#ifdef CREATEANNFILES
	CrossValidationTools vmi;
	vmi.createFourFilesFromAnnotatedFile("uveg_ann_600.txt");
#endif

	return 0;
}