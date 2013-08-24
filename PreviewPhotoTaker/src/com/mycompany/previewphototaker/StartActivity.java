package com.mycompany.previewphototaker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.previewphototaker.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class StartActivity extends Activity implements OnClickListener {

	EditText objectName; //from this object will be the photos taken
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		objectName = (EditText) this.findViewById(R.id.objectname_edittext);
		Bundle b = getIntent().getExtras(); //the activity can get extras from the PhotographedObjectsActivity
		
		if(b != null)
		{
			if(b.containsKey("objectname"))
			{
				String objectNameFromChoose = b.getString("objectname");
				objectName.setText(objectNameFromChoose);
			}
		}
		View startButton = this.findViewById(R.id.start_button);
		startButton.setOnClickListener(this);
		View createCSVButton = this.findViewById(R.id.createcsv_button);
		createCSVButton.setOnClickListener(this);
		View chooseButton = this.findViewById(R.id.choose_button);
		chooseButton.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.start_button:
			Intent intent_picturetaker = new Intent(this, PictureTakerActivity.class);
			Bundle b = new Bundle();
			b.putString("objectname", objectName.getText().toString()); 
			intent_picturetaker.putExtras(b);
			startActivity(intent_picturetaker);
			break;
		case R.id.createcsv_button:
			CSVFileEditor csvFile = new CSVFileEditor();
			csvFile.csvCreate("metadata");
			Toast.makeText(getApplicationContext(), ".csv updated", Toast.LENGTH_SHORT).show();
			break;	
		case R.id.choose_button:
			Intent intent_poa = new Intent(this, PhotographedObjectsActivity.class);
			startActivity(intent_poa);
			break;
		}
	}
}
