#include "Detectors.h"

int main( void )
{
	Detectors haarDetector("cascade.xml");
	haarDetector.loadTestImageNames("PicturesForTest\\info.txt");
	haarDetector.runTest("PicturesForTest\\", "TestResult\\");
	return 0;
}