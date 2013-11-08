package com.aut.smeyel;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ol.research.measurement.MeasurementLog;
import com.ol.research.measurement.TimeMeasurement;

/**
 * The thread of the communication. Contains time measurement points. <br>
 * The thread waits for connection, when the client is connected, he waits for command.<br>
 * <br>
 * Handles following commands:<br>
 * Ping: The application replies with pong.<br>
 * Takepicture: A photo is taken and sent to the computer, and sends the picture.<br>
 * Sendlog: Time measurement results will be sent. <br>
 * <br>
 * Important: <br>
 * The received JSON command must end with # character.<br>
 * */
class CommsThread implements Runnable {
	
	private volatile boolean terminating = false;
	private CameraPreview mOpenCvCameraView;
	Handler handler;
	ServerSocket ss;
	static OutputStream socket_os;
	static Socket s = null;
	static boolean isPictureComplete;	// Used by SendImageService	
    private static final String TAG = "COMM";
    long TakingPicture;
    
	static MeasurementLog TimeMeasurementResults = new MeasurementLog();
	static TimeMeasurement TM = new TimeMeasurement();
	
	static int ReceptionMsID = 1;
	static int PreProcessMsID = 2;
	static int WaitingMsID = 3;
	static int TakePictureMsID = 4;
	static int AllMsID = 5;
	static int AllNoCommMsID = 6;
	static int PostProcessJPEGMsID = 7;
	static int PostProcessPostJpegMsID = 8;
	static int SendingJsonMsID = 9;
	static int SendingJpegMsID = 10;
	
	public CommsThread(CameraPreview mOpenCvCameraView, Handler hand, ServerSocket ss)
	{
		this.mOpenCvCameraView = mOpenCvCameraView;
		handler=hand;
		this.ss = ss;
		TM.setMeasurementLog(TimeMeasurementResults);
		TM.setName(ReceptionMsID, "ReceptionMs");
		TM.setName(PreProcessMsID, "PreProcessMs");
		TM.setName(WaitingMsID, "WaitingMs");
		TM.setName(TakePictureMsID, "TakePictureMs");
		TM.setName(AllMsID, "AllMs");
		TM.setName(AllNoCommMsID,"AllNoCommMs");
		TM.setName(PostProcessJPEGMsID, "PostProcessJpegGMs");
		TM.setName(PostProcessPostJpegMsID, "PostProcessPostJpegMs");
		TM.setName(SendingJsonMsID, "SendingJsonMs");
		TM.setName(SendingJpegMsID, "SendingJpegMs");
	}
	
	public void run() {
		
		// Wait until OpenCV is loaded (needed for time measurement)
		while(TimeMeasurement.isOpenCVLoaded == false)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException ex)
			{
				// Interruption is not a problem...
			}
		}
		
		long actual_time = TimeMeasurement.getTimeStamp();
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		InputStream is = null;
        OutputStream out = null;
        try
        { 
//        	while (!Thread.currentThread().isInterrupted()) {	 
        	while(!terminating) {
		       
        		ss.setReuseAddress(true);
        		ss.bind(new InetSocketAddress(MainActivity.SERVERPORT));
		        s = null;	    	
		        is = null;
		        out = null;       
		        
	    		Log.i(TAG, "Waiting for connection...");
	            s = ss.accept();       //----Waiting for connection     
	            out = s.getOutputStream();
	            Log.i(TAG, "Receiving...");
	            is = s.getInputStream();
	            
	            int ch=0;        

	            //	            while(true) //beak condition terminates the loop
	            while(!terminating)
	            {	
	        		TM.Start(AllMsID);
	        		TM.Start(ReceptionMsID);
	        		
	        		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		        	Message receivedMessage = handler.obtainMessage(MainActivity.MSG_ID);
	                do {
	                	ch=is.read();
	                	if(ch != '#' && ch != -1) //TODO: change to '\0'
	                	{
	                		bos.write(ch);
	                	}
	                }while(ch != '#' && ch != -1);
	                
	                if(ch==-1) //connection terminated
	                {
	                	Log.i(TAG, "Connection terminated");
	                	break; 
	                }
	                
	               	String message = new String(bos.toByteArray());
	               	TM.Stop(ReceptionMsID);
	               	TM.Start(AllNoCommMsID);
	               	TM.Start(PreProcessMsID);
	               	
	               	
	                Log.i(TAG, "Processing...");
	           		JSONObject jObj = new JSONObject(message);
	           		String type = jObj.getString("type");
	           		long desired_timestamp = jObj.getLong("desiredtimestamp");
	           		
		    		actual_time = TimeMeasurement.getTimeStamp();
		    		
	               	if (type.equals("takepicture"))// ----------- TAKE PICTURE command
	               	{
	                    Log.i(TAG, "Cmd: take picture...");
	                    
	                    Message pictureModeMessage = handler.obtainMessage(MainActivity.CHANGE_OPERATING_MODE_ID, MainActivity.OperatingMode.PICTURE_PER_REQUEST.ordinal(), 0);
	                    pictureModeMessage.sendToTarget();
	                	
	                    Log.i(TAG, "Waiting for desired timestamp...");
	                    TM.Stop(PreProcessMsID);
	                    TM.Start(WaitingMsID);
	               		if(desired_timestamp != 0 && desired_timestamp > actual_time)
	               		{
	               			while(desired_timestamp >= actual_time) 
	               			{
	               				actual_time = TimeMeasurement.getTimeStamp();
	               				if((desired_timestamp - actual_time) > 5000000) //if the desired time stamp is too far away, sleep 4.5 s
	               				{
	               					Thread.sleep(4500);
	               				}
	               			}
	               		}
	                    Log.i(TAG, "Taking picture...");
	                    isPictureComplete = false;	// SendImageService will set this true...
	                    TM.Stop(WaitingMsID);
	                    TM.Start(TakePictureMsID);
	                    mOpenCvCameraView.takePicture();
	               		
	                    Log.i(TAG, "Waiting for sync...");
	                    while(!isPictureComplete)
	                    {
		               		synchronized (MainActivity.syncObj)
		               		{
		               			// Wait() may also be interrupted,
		               			// does not necessarily mean that send is complete.
		               			MainActivity.syncObj.wait();
		               		}
		               		Log.i(TAG,"Wait() finished");
	                    }
	                    Log.i(TAG, "Sync received, sending picture...");
	                    
	            			// Get output stream from the CommsThread
	            			OutputStream os = s.getOutputStream();
	            			// Prepare data to send
	            			byte[] mybytearray = mOpenCvCameraView.lastPhotoData;
	            			
	            	        String buff = Integer.toString(mybytearray.length);
	            	        
	            	        StringBuilder sb = new StringBuilder("{\"type\":\"JPEG\",\"size\":\""); 
	            	        sb.append(buff);
	            	        sb.append("\",\"timestamp\":\"");
	            	        sb.append(Long.toString(CameraPreview.OnShutterEventTimestamp));
	            	        sb.append("\"}#");
	            	        String JSON_message = sb.toString();
	            	        
	            	        
	            	        // Send data
	            	        CommsThread.TM.Stop(CommsThread.PostProcessPostJpegMsID);
	            	        CommsThread.TM.Stop(CommsThread.AllNoCommMsID);
	            	        CommsThread.TM.Start(CommsThread.SendingJsonMsID);
	            	        
	            	        Log.i("COMM","Sending JSON and image to PC");
	            	        DataOutputStream output = new DataOutputStream(os);     
	            	        output.writeUTF(JSON_message);
	            	        output.flush();
	            	        CommsThread.TM.Stop(CommsThread.SendingJsonMsID);
	            	        CommsThread.TM.Start(CommsThread.SendingJpegMsID);
	            	        // ??? Ezt nem az output-ba kellene írni?
	            	        os.write(mybytearray,0,mybytearray.length);
	            	        
	            	        // Flush output stream
	            	        os.flush();
	            	        CommsThread.TM.Stop(CommsThread.SendingJpegMsID);
	            	        CommsThread.TM.Stop(CommsThread.AllMsID);
	            	        // Notify CommsThread that data has been sent
	            	        Log.i("COMM","Data sent.");      	           
	                    
	               	} else if(type.equals("ping"))	// ----------- PING command
	               	{
	                    Log.i(TAG, "Cmd: ping...");
	               		out = s.getOutputStream();       
	                    DataOutputStream output = new DataOutputStream(out);     
	                    output.writeUTF("pong#");
	                    output.flush();
	               	} else if (type.equals("sendlog"))// ----------- SENDLOG command
	               	{
	               		Log.i(TAG, "Cmd: sendlog...");
	               		out = s.getOutputStream();
	               		TimeMeasurementResults.WriteJSON(out);
	               	} else if(type.equals("requestposition"))
	               	{
	               		Log.i(TAG, "Cmd: send position");
	               		
	               		Message positionModeMessage = handler.obtainMessage(MainActivity.CHANGE_OPERATING_MODE_ID, MainActivity.OperatingMode.POSITION_PER_REQUEST.ordinal(), 0);
	               		positionModeMessage.sendToTarget();
	                	
	               		out = s.getOutputStream();       
	                    DataOutputStream output = new DataOutputStream(out);    
	                    //double x = 0, y = 0;
	                    // TODO: send TrackerDatas if available - in a while loop maybe
//	                    TrackerData[] td = nativeGetLastKnownPosition();
//	                    output.writeUTF("x: " + td.posx + " y: " + td.posy + " valid: " + td.valid + "#");
	                    output.flush();
	               	}
	               	              	
	               	MainActivity.mClientMsg = message;
	                Log.i(TAG, "Sending response...");
	                receivedMessage.sendToTarget();
	               	// Save timing info
	               	double DelayTakePicture = TimeMeasurement.calculateIntervall(desired_timestamp, TakingPicture);
	               	TM.pushIntervallToLog("TakePictureDelayMs", DelayTakePicture);
	               	double DelayOnShutter = TimeMeasurement.calculateIntervall(desired_timestamp, CameraPreview.OnShutterEventTimestamp);
	               	TM.pushIntervallToLog("OnShutterDelayMs", DelayOnShutter);
	            }
	            ss.close(); 
	            s.close();
			}
    	} catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
			e.printStackTrace();
		}
        finally{
        	terminating = true;
        	try {
        		if(ss != null) {
        			ss.close();
        			ss = null;
        		}
        		if(s != null) {
        			s.close();
        			s = null;
        		}
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
        }
        Log.d(TAG, "Exiting CommThread");
    }      
	public boolean isTerminating() {
		return terminating;
	}

	public void setTerminating() {
		this.terminating = true;
	}
//	public native TrackerData[] nativeGetLastKnownPosition();
}

