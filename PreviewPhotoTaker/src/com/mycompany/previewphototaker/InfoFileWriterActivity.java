package com.mycompany.previewphototaker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class InfoFileWriterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photographedobjects);
		createRadioButtonsAndChooseButton();
	}
	
	@Override
	protected void onPause() {
	    super.onPause();  
	    finish();
	}
	
	private void createRadioButtonsAndChooseButton() {
			    
	    String root = Environment.getExternalStorageDirectory().getAbsolutePath();
		String dir = root + "/" + "PhotosForResearch";
		File foldersandfilesDir = new File(dir);
        String[] names = foldersandfilesDir.list();
        int folderCounter=0;
        if(foldersandfilesDir.isDirectory())
        {
	        for(String name : names)
	        {
	        	if(new File(dir + "/" + name).isDirectory())
	        	{
	        		folderCounter++;
	        	}
	        }
        }
	    
        if(folderCounter != 0)
        {
	        ViewGroup layout = (ViewGroup) findViewById(R.id.layoutid);
		    final RadioButton[] rb = new RadioButton[folderCounter];
		    final RadioGroup rg = new RadioGroup(this); //create the RadioGroup
		    rg.setOrientation(RadioGroup.VERTICAL);		   
		    folderCounter = 0;
		    for(String name : names)
	        {
	        	if(new File(dir + "/" + name).isDirectory())
	        	{
	        		rb[folderCounter]  = new RadioButton(this);
	 		        rg.addView(rb[folderCounter]); //the RadioButtons are added to the radioGroup instead of the layout
	 		        rb[folderCounter].setText(name);
	 		        folderCounter++;
	        	}
	        }
		    layout.addView(rg);  
		    
		    Button chooseButton = new Button(this);
		    chooseButton.setText("Choose");
		    LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		    layout.addView(chooseButton, lp);
		    
		    chooseButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					int selectedId = rg.getCheckedRadioButtonId();
					RadioButton selectedRadioButton = (RadioButton) findViewById(selectedId);
					if(selectedId != -1)
					{
						writeInfoFile(selectedRadioButton.getText().toString(), selectedRadioButton.getText().toString());
						Toast.makeText(getApplicationContext(), "Info file updated", Toast.LENGTH_SHORT).show();
					}
					else 
					{
						Toast.makeText(getApplicationContext(), "Please choose an object!", Toast.LENGTH_SHORT).show();
					}
					
				}
		    	
		    });
        }
        else
        {
        	TextView historyEmpty = new TextView(this);
        	historyEmpty.setText("There are no photos in the history");
        	ViewGroup layout = (ViewGroup) findViewById(R.id.layoutid);
        	layout.addView(historyEmpty);
        }
	}
	
	public void writeInfoFile(String choosenFolder,String filename) {
		
		try
	    {
			FileWriter writer = null;
			String root = Environment.getExternalStorageDirectory().getAbsolutePath();
			String choosenDir = root + "/" + "PhotosForResearch/" + choosenFolder;
	        
	        File fileChoosenPath = new File(choosenDir);
	        
			String fileNameWithType = filename + ".txt";
			File tempfile = new File(fileChoosenPath, fileNameWithType);
			writer = new FileWriter(tempfile);

			File[] listedFiles = fileChoosenPath.listFiles();
			if (listedFiles != null) {
				for (File f : listedFiles) {
					if (f.isFile()) {
						if (fileNameWithType.compareTo(f.getName()) != 0 && f.getName().contains(".jpg")) {
							writer.append(f.getName());
							writer.append("\r\n");
						}
					}
				}
			}
			writer.flush();
			writer.close();
	    }
	    catch(IOException e)
	    {
	         e.printStackTrace();
	    }
		
	}

}
