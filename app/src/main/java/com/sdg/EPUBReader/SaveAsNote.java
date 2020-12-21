
package com.sdg.EPUBReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.sdg.DisplayManager.Display_Manager;
import com.android.QuikE.R;
import com.sdg.organizer.MoveInfo;

public class SaveAsNote extends Activity {

	/* these two macro is used to check the return value of save as operation */
	private final int SUCCESS = 1;
	/*to show is save as fail*/
	private final int FAILURE = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*set the layout of the save as page*/
		setContentView(R.layout.saveas);
		/*
		 * create the object of database class which will create connection with
		 * databases
		 */
		final DataBaseClass mDb = new DataBaseClass(this);
		/*get the handler*/
		mDb.mDatab = mDb.mDatah.getReadableDatabase();
		/* this edit box is used to take some text which will be the note's name */
		final EditText mSaveAsEditText = (EditText) findViewById(R.id.SaveAsEditText);
		/*
		 * this image will show at the right of the edit box ,on click on that
		 * icon the extracted text will save from cabinet to note as a new note
		 * with this name
		 */
		ImageView mSaveAs = (ImageView) findViewById(R.id.clicktosaveas);
		/*initialize the close button*/
		ImageView mCloseSaveAs=(ImageView)findViewById(R.id.closeSaveAs);
		/*set the onclick listener on close save as*/
		mCloseSaveAs.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				SaveAsNote.this.finish();
				
			}  
		});
		/*set the on click listener*/
		mSaveAs.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				/* this string will get the note's name */
				String mExtractedText = mSaveAsEditText.getText().toString();
				/*
				 * return value will be used to deside if note is successfully
				 * saved or note
				 */
				int mReturnValue = mDb.insertInNoteTable(mExtractedText);
				/* if note name already exist don't save */
				if (mReturnValue == FAILURE) {

				}/*
				 * if note name is available then first save those element in
				 * the note Database and delete the content from Cabinet
				 * Database and update the cabinet table
				 */
				else if (mReturnValue == SUCCESS) {
					/*create a intent*/
					Intent mIntent = new Intent(SaveAsNote.this, MoveInfo.class);
					/*delete from the cabinet table*/
					mDb.deleteFromCabinetAfterSave(DataBaseClass.CABINET_CHECKED);
					/*remove the cabinet after save*/
					Display_Manager.removeCabinetafterSave();
					/* note_bundle */
					Bundle mBundle = new Bundle();
					/* current_note_id */
					mBundle.putString("infomessageCL_key",
							"Data Successfully Save");
					mIntent.putExtras(mBundle);
					/*update the cabinet table*/
					mDb.updateCabinetTableAfterSave();
					/*
					 * start the message to show i.e data saved successfully
					 * will call
					 */
					startActivity(mIntent);
					/*finishthe current activity*/
					SaveAsNote.this.finish();
				}

			}
		});

	}

	/* This code is to return in home using Esc key from KeyBoard */
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		switch (KeyCode) {
		case KeyEvent.KEYCODE_BACK:
			return false;
		}
		return false;

	}

}
