package com.aut.smeyel;

import java.lang.ref.WeakReference;
import java.net.ServerSocket;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.ol.research.measurement.TimeMeasurement;

/**
 * 
 * MainActivity of the application (UI thread). <br>
 * It starts the CommsThread, and if picture was taken, the SendImageService is started from here.<br>
 * Contains the time measurement points.<br>
 * <br>
 * @author Milan Tenk <br>
 * <br>
 * Callbacks:<br>
 * PictureCallback: If the taken photo is ready, here will be the SendImageService started, the picture can be saved to SD card.<br>
 * ShutterCallback: Called right after taking photo.<br>
 * BaseLoaderCallback: Needed for OpenCV initialization.<br>
 * <br>
 * Handler: Handles the messages, that are written to the screen.<br>
 * <br>
 * Methods:<br>
 * onCreate: Will be called at the start of the activity, Commsthread will be started.<br>
 * onResume: Called, when the user is interfacing with the application. Contains the initialization of OpenCV.<br>
 * onStop: Called, when the activity is stopped.<br>
 * getLocalIpAddress: Determines the IP address of the Phone.<br>
 * httpReg: Registrates the phone on the specified Server.<br>
 */

public class MainActivity extends Activity implements CvCameraViewListener2, View.OnTouchListener {
	
	private enum OperatingMode {
		IDLE, POSITION_PER_REQUEST, PICTURE_PER_REQUEST, POSITION_STREAM, PICTURE_STREAM
	}
	private OperatingMode currentOperatingMode = OperatingMode.IDLE;
	
	private static final String TAG = "SMEyeL::AndroidTracker::MainActivity";
//	private CameraBridgeViewBase mOpenCvCameraView;
	private CameraPreview mOpenCvCameraView;
	private Mat mRgba;
	private Mat mGray;
	private int width;
	private int height;
	
	private Mat mResult;
	
	protected static final int MSG_ID = 0x1337;
	protected static final int SERVERPORT = 6000;
	protected static final int TIME_ID = 0x1338;
	//	private static final String  TAG = "TMEAS";
	ServerSocket ss = null;
	static String mClientMsg = "";
	static Object syncObj = new Object();
	
	Thread myCommsThread = null;

//	CameraPreview mPreview;
//	Camera mCamera;
	
	//Handler a socket�zenet sz�m�ra
	private MyHandler myUpdateHandler = new MyHandler(this);
	static class MyHandler extends Handler {
		private final WeakReference<MainActivity> mActivity;
		
		public MyHandler(MainActivity activity) {
			mActivity = new WeakReference<MainActivity>(activity);
		}
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ID:
				if(mActivity != null) {}
//				TextView tv = (TextView) findViewById(R.id.TextView_receivedme);
//				tv.setText(mClientMsg);
				break;
			case TIME_ID:
//				TextView tv2 = (TextView) findViewById(R.id.TextView_timegot);
//				tv2.setText(current_time);
				break;
			default:

				break;
			}
			super.handleMessage(msg);
		}
	};
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                	TimeMeasurement.isOpenCVLoaded = true;
                    Log.i(TAG, "OpenCV loaded successfully");

                    /* OpenCV specific init, for example: enable camera view */
                    
                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("native_module");
                    
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                    
                    
//                    InitMarkerHandler(800, 480);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		
		PreferenceManager.setDefaultValues(this, R.xml.settingsscreen, false);
		
//		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//		boolean b = sharedPref.getBoolean("pref_config1", true);
		
//		SharedPreferences.Editor editor = sharedPref.edit();
//		editor.putBoolean("pref_config1", true);
//		editor.commit();
		
        mOpenCvCameraView = (CameraPreview) findViewById(R.id.androidtracker_surface_view);
        mOpenCvCameraView.setHandler(myUpdateHandler);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
        myCommsThread = new Thread(new CommsThread(mOpenCvCameraView, myUpdateHandler, ss));
		myCommsThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_settings:
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			break;
		}
		return true;
	}
	
	/** initialize operating mode specific resources **/
	private void init()
	{
		switch(currentOperatingMode) {
			case IDLE: break;
			case POSITION_PER_REQUEST:
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
				String configLocation = sharedPref.getString("pref_configFileLocation", Environment.getExternalStorageDirectory().getPath() + "rossz.ini");
				nativeInitTracker(width, height, configLocation);
				break;
			default: break;
		}
	}
	
	/** release operating mode specific resources **/
	private void release()
	{
		switch(currentOperatingMode) {
			case IDLE: break;
			case POSITION_PER_REQUEST: nativeReleaseTracker();
			default: break;
		}
	}
	
	@Override
    public void onPause()
    {
		super.onPause();
		if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
		release();
		myCommsThread.interrupt();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_5, this, mLoaderCallback);
        //TODO: myCommsThread should be probably resumed or restarted here (also it should be properly paused/finished in onPause/onDestroy)
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        release();
        myCommsThread.interrupt();
    }

	@Override
	public void onCameraViewStarted(int _width, int _height) {
		width = _width;
		height = _height;
		
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		mGray = new Mat(height, width, CvType.CV_8UC1);
		mResult = new Mat();
		
		init();
		
	}

	@Override
	public void onCameraViewStopped() {
		mRgba.release();
		mGray.release();
		mResult.release();
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
//		inputFrame.rgba().copyTo(mRgba);
//      Core.putText(mRgba, "OpenCV+Android", new Point(10, inputFrame.rgba().rows() - 10), 3, 1, new Scalar(255, 0, 0, 255), 2);
		
		mRgba = inputFrame.rgba();
		
		switch(currentOperatingMode) {
			case IDLE: return mRgba;
			case POSITION_PER_REQUEST:
				mGray = inputFrame.gray();
				nativeTrack(mRgba.getNativeObjAddr(), mResult.getNativeObjAddr());
				return mResult;
			default: return mRgba;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void changeOperatingMode(OperatingMode newMode)
	{
		if(newMode != currentOperatingMode) {
			
			// release data for current mode
			release();
			
			// enter new mode
			currentOperatingMode = newMode;
			init();
			Toast.makeText(MainActivity.this, "Changed operating mode to " + currentOperatingMode.name(), Toast.LENGTH_LONG).show();
		}
	}
	
	public native void nativeFindFeatures(long matAddrGr, long matAddrRgba);
	public native void nativeFindCircles(long matAddrGr, long matAddrRgba);
	
	public native void nativeInitTracker(int width, int height, String configFileLocation);
	public native void nativeTrack(long matAddrInput, long matAddrResult);
	public native void nativeReleaseTracker();
	

}
