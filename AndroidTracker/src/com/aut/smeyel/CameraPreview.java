package com.aut.smeyel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.opencv.android.JavaCameraView;

//import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import com.ol.research.measurement.TimeMeasurement;
//import android.view.Surface;

public class CameraPreview extends JavaCameraView implements PictureCallback {
	
	private boolean saveToSD = false;
	byte[] lastPhotoData;
	
	static long OnShutterEventTimestamp;
	String current_time = null;
	
	Handler handler = null;

	private ShutterCallback mShutter = new ShutterCallback()
	{
		@Override
		public void onShutter()
		{
			OnShutterEventTimestamp = TimeMeasurement.getTimeStamp();
			current_time = String.valueOf(OnShutterEventTimestamp); 
			CommsThread.TM.Stop(CommsThread.TakePictureMsID);   
			CommsThread.TM.Start(CommsThread.PostProcessJPEGMsID);
			Message timeMessage = new Message();
			timeMessage.what = MainActivity.TIME_ID;
			if(handler != null) {
				handler.sendMessage(timeMessage);
			}
		}
	};
	
//	Camera mCamera;
//	SurfaceHolder mHolder;

//	public CameraPreview(Context context, Camera camera) {
//		super(context);
//
//		mCamera = camera;
//
//		// Install a SurfaceHolder.Callback so we get notified when the
//		// underlying surface is created and destroyed.
//		mHolder = getHolder();
//		mHolder.addCallback(this);
//
//		// deprecated setting, but required on Android versions prior to 3.0
//		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//
//	}
	public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
	
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	public void takePicture() {
		mCamera.setPreviewCallback(null);
		mCamera.takePicture(mShutter, null, this); //--------takePicture command
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		CommsThread.TM.Stop(CommsThread.PostProcessJPEGMsID);
		CommsThread.TM.Start(CommsThread.PostProcessPostJpegMsID);
		//Option to save the picture to SD card
		if(saveToSD)
		{
			String pictureFile = Environment.getExternalStorageDirectory().getPath()+"/custom_photos"+"/__1.jpg";
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();   

			} catch (FileNotFoundException e) {
				Log.d("Photographer", "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d("Photographer", "Error accessing file: " + e.getMessage());
			}
			Log.v("Photographer", "Picture saved at path: " + pictureFile);
		}
		lastPhotoData = data;
		synchronized (MainActivity.syncObj) //notifys the Commsthread, if the picture is complete
		{
			CommsThread.isPictureComplete = true;
			MainActivity.syncObj.notifyAll();
		}
		mCamera.startPreview();
		mCamera.setPreviewCallback(this);
	}
	
//	@Override
//	public void surfaceCreated(SurfaceHolder holder) {
//		try {
//			Camera.Parameters parameters = mCamera.getParameters();
//			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
//			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//			parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
//			mCamera.setPreviewDisplay(holder);
//			mCamera.startPreview();
//		} catch (IOException e) {
//			Log.d("AutoCamera",
//					"Error setting camera preview: " + e.getMessage());
//		}
//
//	}

//	@Override
//	public void surfaceDestroyed(SurfaceHolder holder) {
//		// itt nem kell semmit csinalnunk
//	}
//	
//	
//	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
//		// If your preview can change or rotate, take care of those events here.
//		// Make sure to stop the preview before resizing or reformatting it.
//
//	/*	if (mHolder.getSurface() == null) {
//			// preview surface does not exist
//			return;
//		}
//
//		// stop preview before making changes
//		try {
//			mCamera.stopPreview();
//		} catch (Exception e) {
//			// ignore: tried to stop a non-existent preview
//		}
//
//		// Parameterek beallitasa
//		Camera.Parameters parameters = mCamera.getParameters();
//		List<Size> cameraPreviewSizes = parameters.getSupportedPreviewSizes();
//		for (Size size : cameraPreviewSizes) {
//			Log.v("AutoCamera", "cameraPreviewSize: " + size.width + "x"
//					+ size.height);
//		}
//		List<String> focusModes = parameters.getSupportedFocusModes();
//		for (String mode : focusModes) {
//			Log.v("AutoCamera", "cameraFocusMode: " + mode);
//		}
//		List<String> flashModes = parameters.getSupportedFlashModes();
//		for (String mode : flashModes) {
//			Log.v("AutoCamera", "cameraFlashMode: " + mode);
//		}
//
//		parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
//		parameters.setPreviewSize(800, 480);
//		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//		parameters.setJpegQuality(80); // TODO: ez legyen allithato WS-bol
//		mCamera.setDisplayOrientation(90); // allo preview
//
//		mCamera.setParameters(parameters);
//
//		// start preview with new settings
//		try {
//			mCamera.setPreviewDisplay(mHolder);
//			mCamera.startPreview();
//		} catch (Exception e) {
//			Log.d("AutoCamera",
//					"Error starting camera preview: " + e.getMessage());
//		}*/
//
//	}
}
