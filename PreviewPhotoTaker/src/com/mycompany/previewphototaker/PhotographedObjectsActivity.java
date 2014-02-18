package com.mycompany.previewphototaker;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class PhotographedObjectsActivity extends Activity {

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

	/**
	 * Creates dynamically radio buttons.
	 */
	private void createRadioButtonsAndChooseButton() {

		String root = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String dir = root + "/" + "PhotosForResearch";
		File foldersandfilesDir = new File(dir);
		String[] names = foldersandfilesDir.list();
		int folderCounter = 0;
		if (foldersandfilesDir.isDirectory()) {
			for (String name : names) {
				if (new File(dir + "/" + name).isDirectory()) {
					folderCounter++;
				}
			}
		}

		if (folderCounter != 0) {
			ViewGroup layout = (ViewGroup) findViewById(R.id.layoutid);
			final RadioButton[] rb = new RadioButton[folderCounter];
			final RadioGroup rg = new RadioGroup(this); 
			rg.setOrientation(RadioGroup.VERTICAL);
			folderCounter = 0;
			for (String name : names) {
				if (new File(dir + "/" + name).isDirectory()) {
					rb[folderCounter] = new RadioButton(this);
					rg.addView(rb[folderCounter]); 
					rb[folderCounter].setText(name);
					folderCounter++;
				}
			}
			layout.addView(rg);

			Button chooseButton = new Button(this);
			chooseButton.setText("Choose");
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			layout.addView(chooseButton, lp);

			chooseButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					int selectedId = rg.getCheckedRadioButtonId();
					RadioButton selectedRadioButton = (RadioButton) findViewById(selectedId);
					Intent intent_start = new Intent(
							PhotographedObjectsActivity.this,
							StartActivity.class);
					Bundle b = new Bundle();
					if (selectedId != -1)
					{
						b.putString("objectname", selectedRadioButton.getText()
								.toString());
					} else 
					{
						b.putString("objectname", "");
					}
					intent_start.putExtras(b);
					startActivity(intent_start);
				}

			});
		} else {
			TextView historyEmpty = new TextView(this);
			historyEmpty.setText("There are no photos in the history");
			ViewGroup layout = (ViewGroup) findViewById(R.id.layoutid);
			layout.addView(historyEmpty);
		}
	}
}
