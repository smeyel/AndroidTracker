package com.aut.smeyel;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
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
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.aut.smeyel.R;

public class MainActivity extends Activity implements CvCameraViewListener2, View.OnTouchListener {
	
	private static final String TAG = "SMEyeL::AndroidTracker::MainActivity";
	private CameraBridgeViewBase mOpenCvCameraView;
	private Mat mRgba;
	private Mat mGray;
	
	private Mat mResult;
	
	public static boolean openCVLoaded = false;
	public static final int SERVERPORT = 6000;
	public static final int MSG_ID = 0x1337;
	
	Thread myCommsThread = null;
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    openCVLoaded = true;

                    /* OpenCV specific init, for example: enable camera view */
                    
                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("native_module");
                    
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                    mOpenCvCameraView.enableView();
                    
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
		setContentView(R.layout.activity_main);
		
		PreferenceManager.setDefaultValues(this, R.xml.settingsscreen, false);
		
//		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//		boolean b = sharedPref.getBoolean("pref_config1", true);
		
//		SharedPreferences.Editor editor = sharedPref.edit();
//		editor.putBoolean("pref_config1", true);
//		editor.commit();
		
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.opencv_start_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
        myCommsThread = new Thread(new CommsThread());
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
	
	@Override
    public void onPause()
    {
		super.onPause();
		if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
		Release();
		myCommsThread.interrupt();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_5, this, mLoaderCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        Release();
        myCommsThread.interrupt();
    }

	@Override
	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		mGray = new Mat(height, width, CvType.CV_8UC1);
		mResult = new Mat();
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String configLocation = sharedPref.getString("pref_configFileLocation", "/sdcard/rossz.ini");
		Init(width, height, configLocation);
		
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
        mGray = inputFrame.gray();
        //FindCircles(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr());
        FastColor(mRgba.getNativeObjAddr(), mResult.getNativeObjAddr());
		return mResult;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public native void FindFeatures(long matAddrGr, long matAddrRgba);
	public native void FindCircles(long matAddrGr, long matAddrRgba);
	
	public native void Init(int width, int height, String configFileLocation);
	public native void FastColor(long matAddrInput, long matAddrResult);
	public native void Release();
	

}
