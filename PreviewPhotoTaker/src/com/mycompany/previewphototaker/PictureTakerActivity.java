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
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PictureTakerActivity extends Activity implements
		CvCameraViewListener2, View.OnTouchListener {

	private static final String TAG = "OpenCV";
	private CameraBridgeViewBase mOpenCvCameraView;
	private Mat mRgba;
	private String objectName;
	SaveImageToExtStor save = new SaveImageToExtStor();
	boolean autoSaveFlag = false;
	int counter = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picturemaker);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.opencv_start_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);

		Bundle b = getIntent().getExtras();
		objectName = b.getString("objectname");
		if (objectName.length() == 0) {
			objectName = "not_specified";
		}

		TextView objectNameTextView = (TextView) findViewById(R.id.objectname_textview);
		objectNameTextView.setText(objectName);

		final Button autoButton = (Button) findViewById(R.id.automated_button);
		autoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (autoSaveFlag == false) {
					autoSaveFlag = true;
					autoButton.setText(getString(R.string.stop_button));
				} else {
					autoSaveFlag = false;
					autoButton.setText(getString(R.string.automated_button));
				}

			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this,
				mLoaderCallback);
	}

	@Override
	public void onPause() {
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
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.setOnTouchListener(PictureTakerActivity.this);
				mOpenCvCameraView.enableView();
				mOpenCvCameraView.enableFpsMeter();

			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
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
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			if (autoSaveFlag) {
				counter++;
				if (counter % 3 == 0) {
					save.saveJpegImage(mRgba, objectName);
					counter = 0;
				}
			}
		}
		return mRgba;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			if (autoSaveFlag == false) {
				String savedFileName = save.saveJpegImage(mRgba, objectName);
				Toast.makeText(getApplicationContext(),
						"Image " + savedFileName + " saved", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"External storage not found!", Toast.LENGTH_SHORT).show();
		}
		return false;
	}
}
