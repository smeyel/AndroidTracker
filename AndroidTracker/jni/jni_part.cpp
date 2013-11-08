#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>

#include "FastColorFilter.h"
#include "MarkerCC2Tracker.h"
#include "MarkerCC2.h"
#include "DetectionResultExporterBase.h"
#include "TimeMeasurementCodeDefines.h"
#include "SimpleIniConfigReader.h"
#include "AndroidLogger.h"

#define LOG_TAG "SMEyeL"

using namespace std;
using namespace cv;
using namespace TwoColorCircleMarker;
//using namespace LogConfigTime;

const char* stringToCharStar(string str) {
	const char *cstr = str.c_str();
	return cstr;
}

string intToString(int i) {
	stringstream ss;
	ss << i;
	string str = ss.str();
	return str;
}

const char* intToCharStar(int i) {
	return stringToCharStar(intToString(i));
}

//double lastKnownX = 0.0;
//double lastKnownY = 0.0;
//bool lastKnownValid = false;

vector<MarkerCC2> foundMarkers;

class ResultExporter : public TwoColorCircleMarker::DetectionResultExporterBase
{
//	ofstream stream;
public:
	void open(char *filename)
	{
//		stream.open(filename);
	}

	void close()
	{
//		stream.flush();
//		stream.close();
	}

	int currentFrameIdx;
	int currentCamID;

	virtual void writeResult(MarkerBase *marker)
	{
//		stream << "FID:" << currentFrameIdx << ",CID:" << currentCamID << " ";
//		marker->exportToTextStream(&stream);
//		LOGD("aaaaaamarkerfound");
//		stream << endl;

//		lastKnownX =  marker->center.x;
//		lastKnownY =  marker->center.y;
//		lastKnownValid = marker->isCenterValid;
//
//		int valid = lastKnownValid ? 1 : 0;

		MarkerCC2* markerCc2 = (MarkerCC2*) marker;
		if(markerCc2 != NULL) {
			foundMarkers.push_back(*markerCc2);
//			if(markerCc2->isValid) {
//				int markerId = markerCc2->MarkerID;
//				Logger::getInstance()->Log(Logger::LOGLEVEL_ERROR, LOG_TAG, "aaaaaaa: %d", markerId);
//			}
		}

//		Logger::getInstance()->Log(Logger::LOGLEVEL_INFO, LOG_TAG, "Position: %f %f Valid: %d\n", lastKnownX, lastKnownY, valid);
	}
};

class MyConfigManager
{
	// This method is called by init of the base class to read the configuration values.
	virtual bool readConfiguration(char *filename)
	{
		SimpleIniConfigReader *SIreader = new SimpleIniConfigReader(filename);
		ConfigReader *reader = SIreader;

		resizeImage = reader->getBoolValue("Main","resizeImage");
		showInputImage = reader->getBoolValue("Main","showInputImage");
		verboseColorCodedFrame = reader->getBoolValue("Main","verboseColorCodedFrame");
		verboseOverlapMask = reader->getBoolValue("Main","verboseOverlapMask");
		waitFor25Fps = reader->getBoolValue("Main","waitFor25Fps");
		pauseIfNoValidMarkers = reader->getBoolValue("Main","pauseIfNoValidMarkers");
		waitKeyPressAtEnd = reader->getBoolValue("Main","waitKeyPressAtEnd");
		runMultipleIterations = reader->getBoolValue("Main","runMultipleIterations");
		return true;
	}

public:

	void init(char *filename)
	{
		readConfiguration(filename);
	}
	// --- Settings
	bool resizeImage;
	bool pauseIfNoValidMarkers;
	bool verboseOverlapMask;
	bool verboseColorCodedFrame;
	bool showInputImage;
	bool waitFor25Fps;
	bool waitKeyPressAtEnd;
	bool runMultipleIterations;
};

extern "C" {
JNIEXPORT void JNICALL Java_com_aut_smeyel_MainActivity_nativeFindFeatures(JNIEnv*, jobject, jlong addrGray, jlong addrRgba);

JNIEXPORT void JNICALL Java_com_aut_smeyel_MainActivity_nativeFindFeatures(JNIEnv*, jobject, jlong addrGray, jlong addrRgba)
{
    Mat& mGr  = *(Mat*)addrGray;
    Mat& mRgb = *(Mat*)addrRgba;
    vector<KeyPoint> v;

    FastFeatureDetector detector(50);
    detector.detect(mGr, v);
    for( unsigned int i = 0; i < v.size(); i++ )
    {
        const KeyPoint& kp = v[i];
        circle(mRgb, Point(kp.pt.x, kp.pt.y), 10, Scalar(255,0,0,255));
    }
}

JNIEXPORT void JNICALL Java_com_aut_smeyel_MainActivity_nativeFindCircles(JNIEnv*, jobject, jlong addrGray, jlong addrRgba);

JNIEXPORT void JNICALL Java_com_aut_smeyel_MainActivity_nativeFindCircles(JNIEnv*, jobject, jlong addrGray, jlong addrRgba)
{
    Mat& mGr  = *(Mat*)addrGray;
    Mat& mRgb = *(Mat*)addrRgba;


    // COLOR FILTERING
//    Mat mHsv;
//    cvtColor(mRgb, mHsv, COLOR_RGB2HSV, 3);
//
//    Mat mHSVThreshed;
//    inRange(mHsv, Scalar(0, 100, 30), Scalar(5, 255, 255), mHSVThreshed);
//
//    Mat rgba;
//    cvtColor(mHSVThreshed, mRgb, COLOR_GRAY2RGBA, 0);


    // DETECTING CIRCLES
    // Reduce the noise so we avoid false circle detection
    GaussianBlur( mGr, mGr, Size(9, 9), 2, 2 );

    vector<Vec3f> circles;
    HoughCircles(mGr, circles, CV_HOUGH_GRADIENT, 2, mGr.rows/4, 200, 100 );

    for( int i = 0; i < circles.size(); i++ )
    {
    	Point center(cvRound((double)circles[i][0]), cvRound((double)circles[i][1]));
    	int radius = cvRound((double)circles[i][2]);
    	// draw the circle center
    	circle( mRgb, center, 3, Scalar(0,255,0), -1, 8, 0 );
    	// draw the circle outline
    	circle( mRgb, center, radius, Scalar(0,0,255), 3, 8, 0 );
    }

}

//MarkerHandler markerHandler;
TwoColorCircleMarker::MarkerCC2Tracker* tracker = NULL;
ResultExporter resultExporter;

MyConfigManager configManager;
//char *configfilename = "/sdcard/testini2.ini";

AndroidLogger* logger = NULL;



JNIEXPORT void JNICALL Java_com_aut_smeyel_MainActivity_nativeInitTracker(JNIEnv* env, jobject thisObj, jint width, jint height, jstring configFileLocation);

JNIEXPORT void JNICALL Java_com_aut_smeyel_MainActivity_nativeInitTracker(JNIEnv* env, jobject thisObj, jint width, jint height, jstring configFileLocation)
{
	const char *configfilenameConst = env->GetStringUTFChars(configFileLocation, 0);

	char configfilename[strlen(configfilenameConst) + 1];
	strcpy(configfilename, configfilenameConst);
	env->ReleaseStringUTFChars(configFileLocation, configfilenameConst);

	logger = new AndroidLogger();
//	Logger::registerLogger(*logger);
//	Logger::log(Logger::LOGLEVEL_ERROR, LOG_TAG, "Szam:%d %d %s %d\n", 1, 2, "Hello", 3);

	configManager.init(configfilename);
//	int ii = configManager.runMultipleIterations ? 1 : 0;
//	int iii = configManager.waitKeyPressAtEnd ? 1 : 0;


	if(tracker == NULL) {
		tracker = new TwoColorCircleMarker::MarkerCC2Tracker();
		tracker->setResultExporter(&resultExporter);
		tracker->init(configfilename, true, width, height); // ez sokszor meghivodik (minden resume-kor), memoriaszivargasra figyelni
	}






}

JNIEXPORT jobjectArray JNICALL Java_com_aut_smeyel_MainActivity_nativeTrack(JNIEnv* env, jobject thisObj, jlong addrInput, jlong addrResult);

JNIEXPORT jobjectArray JNICALL Java_com_aut_smeyel_MainActivity_nativeTrack(JNIEnv* env, jobject thisObj, jlong addrInput, jlong addrResult)
{
	foundMarkers.clear();

	Mat& mInput  = *(Mat*)addrInput;
	Mat& mResult = *(Mat*)addrResult;

	Mat mInputBgr;
	cvtColor(mInput, mInputBgr, COLOR_RGBA2BGR);

	tracker->processFrame(mInputBgr,0,-1.0f);

//	Mat* mOut = tracker->visColorCodeFrame;
//	cvtColor(*mOut, mResult, COLOR_BGR2RGBA);
	cvtColor(mInputBgr, mResult, COLOR_BGR2RGBA);

//	Logger::getInstance()->Log(Logger::LOGLEVEL_INFO, LOG_TAG, "ProcessAll: %f ms", tracker->timeMeasurement->getavgms(TimeMeasurementCodeDefs::ProcessAll));
//	Logger::getInstance()->Log(Logger::LOGLEVEL_INFO, LOG_TAG, "FastColorFilter: %f ms", tracker->timeMeasurement->getavgms(TimeMeasurementCodeDefs::FastColorFilter));
//	Logger::getInstance()->Log(Logger::LOGLEVEL_INFO, LOG_TAG, "VisualizeDecomposedImage: %f ms", tracker->timeMeasurement->getavgms(TimeMeasurementCodeDefs::VisualizeDecomposedImage));
//	Logger::getInstance()->Log(Logger::LOGLEVEL_INFO, LOG_TAG, "TwoColorLocator: %f ms", tracker->timeMeasurement->getavgms(TimeMeasurementCodeDefs::TwoColorLocator));
//	Logger::getInstance()->Log(Logger::LOGLEVEL_INFO, LOG_TAG, "LocateMarkers: %f ms", tracker->timeMeasurement->getavgms(TimeMeasurementCodeDefs::LocateMarkers));
//	Logger::getInstance()->Log(Logger::LOGLEVEL_INFO, LOG_TAG, "---------------------");

	jclass trackerDataClass = env->FindClass("com/aut/smeyel/TrackerData");
	jobjectArray trackerDataArray = env->NewObjectArray(foundMarkers.size(), trackerDataClass, 0);

	for(int i = 0; i < foundMarkers.size(); i++) {
		jmethodID cid = env->GetMethodID(trackerDataClass,"<init>","()V");
		jobject obj = env->NewObject(trackerDataClass, cid);

		jfieldID fid_x = env->GetFieldID(trackerDataClass , "posx", "D");
		env->SetDoubleField(obj, fid_x, foundMarkers[i].center.x);

		jfieldID fid_y = env->GetFieldID(trackerDataClass , "posy", "D");
		env->SetDoubleField(obj, fid_y, foundMarkers[i].center.y);

		jfieldID fid_b = env->GetFieldID(trackerDataClass , "valid", "Z");
		env->SetBooleanField(obj, fid_b, foundMarkers[i].isCenterValid);

		env->SetObjectArrayElement(trackerDataArray, i, obj);

	}

	return trackerDataArray;

}

JNIEXPORT void JNICALL Java_com_aut_smeyel_MainActivity_nativeReleaseTracker(JNIEnv*, jobject);

JNIEXPORT void JNICALL Java_com_aut_smeyel_MainActivity_nativeReleaseTracker(JNIEnv*, jobject)
{
	if(tracker != NULL) {
		delete tracker;
		tracker = NULL;
	}

	if(logger != NULL) {
		delete logger;
		logger = NULL;
	}

}

//// pulling data, a method with push would be good
//JNIEXPORT jobjectArray JNICALL Java_com_aut_smeyel_CommsThread_nativeGetLastKnownPosition(JNIEnv* env, jobject thisObj);
//
//JNIEXPORT jobjectArray JNICALL Java_com_aut_smeyel_CommsThread_nativeGetLastKnownPosition(JNIEnv* env, jobject thisObj)
//{
//
//
//
//}
}
