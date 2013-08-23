package com.mycompany.previewphototaker;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class PictureTakerActivity extends Activity implements CvCameraViewListener2, View.OnTouchListener {

	private static final String TAG = "OpenCV";
	private CameraBridgeViewBase mOpenCvCameraView;
	private Mat mRgba;
	private String objectName; //name of the object, that will be photographed
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picturemaker);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.opencv_start_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
		
		Bundle b = getIntent().getExtras();
		objectName= b.getString("objectname");
		if(objectName.length()==0)
		{
			objectName = "not_specified";
		}
		
		TextView objectNameTextView = (TextView) findViewById(R.id.objectname_textview);
		objectNameTextView.setText(objectName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public void onResume()
    {
	    super.onResume();
	    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }
	
	@Override
    public void onPause()
    {
	    super.onPause();
	    if (mOpenCvCameraView != null)
	    mOpenCvCameraView.disableView();
    }
    
    @Override
    public void onDestroy() {
	    super.onDestroy();
	    if (mOpenCvCameraView != null)
	    mOpenCvCameraView.disableView();
    }
	
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                                     
                    /* OpenCV specific init, enable camera view */
                    mOpenCvCameraView.setOnTouchListener(PictureTakerActivity.this);
                    mOpenCvCameraView.enableView();
                    
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    
    @Override
    public void onCameraViewStarted(int width, int height) {
    	mRgba = new Mat(height, width, CvType.CV_8UC4);
    }
    @Override
    public void onCameraViewStopped() {
    	mRgba.release();
    }
    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	inputFrame.rgba().copyTo(mRgba);
    	return mRgba;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
    	SaveImageToSD save = new SaveImageToSD();
    	save.saveJpegImage(mRgba, objectName);
    	Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();
    	return false;
    }
}
