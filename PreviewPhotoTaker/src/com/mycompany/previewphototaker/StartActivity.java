package com.mycompany.previewphototaker;

import com.mycompany.previewphototaker.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class StartActivity extends Activity implements OnClickListener {

	EditText objectName; //from this object will be the photos taken
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		objectName = (EditText) this.findViewById(R.id.objectname_edittext);
		View cannyButton = this.findViewById(R.id.start_button);
		cannyButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.start_button:
			Intent intent = new Intent(this, PictureTakerActivity.class);
			Bundle b = new Bundle();
			b.putString("objectname", objectName.getText().toString()); 
			intent.putExtras(b);
			startActivity(intent);
			break;
		}
	}
	
}
