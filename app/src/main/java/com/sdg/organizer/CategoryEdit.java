
package com.sdg.organizer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sdg.EPUBReader.DataBaseClass;
import com.android.QuikE.R;

public class CategoryEdit extends Activity {
	/** Called when the activity is first created. */

	DataBaseClass mDatabase;

	Cursor mGetCategoryCursor;

	// Edit text to insert to name of note
	EditText mNameEdit;

	// mCurrentNoteId holds the id of the current note
	int mCurrentNoteId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.saveas);

		mDatabase = new DataBaseClass(this);

		// EditText where the new category name will be written
		mNameEdit = (EditText) findViewById(R.id.SaveAsEditText);

		String mNewCategoryName = null;

		// Button to save the category Name
		ImageView mSave = (ImageView) findViewById(R.id.clicktosaveas);

		// Catching the intent from Category list containing the current note id
		Bundle mBundle = CategoryEdit.this.getIntent().getExtras();

		// checking if the bundle is not null
		if (mBundle != null) {

			mCurrentNoteId = mBundle.getInt("currentNoteIdCL_key");

		}

		// Setting on click listener on button for saving note name
		mSave.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				// fetching the name from the edittext
				String mNewCategoryName = mNameEdit.getText().toString();// str_head

				mGetCategoryCursor = mDatabase
						.returnCategoryName(mNewCategoryName);

				

				// sending intent to CategoryList activity having new category
				// name and current note id
				if (mNewCategoryName.length() != 0) {

					if (mGetCategoryCursor.getCount() == 0) {

						// intent for calling the note organizer
						// and passing the data to it
						Intent mIntent = new Intent(CategoryEdit.this,NoteOrganizer.class);

						// creating new bundle
						Bundle mNoteBundle = new Bundle();

						// adding string having category name to bundle
						mNoteBundle.putString("newCategoryCE_key",mNewCategoryName);

						// adding integer having current note id to bundle
						mNoteBundle.putInt("currentNoteIdCE_key",mCurrentNoteId);

						// adding bundle to the intent
						mIntent.putExtras(mNoteBundle);

						// finishing the CategoryEdit Class
						CategoryEdit.this.finish();

						// Starting activity NnoteOrganizer
						startActivity(mIntent);
					}else{
						Toast.makeText(CategoryEdit.this, "Category Already Exists", Toast.LENGTH_LONG).show();
					}
				} else {
					// if the user does note enter anything in edittext
					Toast.makeText(CategoryEdit.this,
							"Please Enter Category Name", Toast.LENGTH_SHORT);
				}

			}
		});
	}

}
