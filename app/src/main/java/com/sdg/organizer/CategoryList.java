
package com.sdg.organizer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sdg.EPUBReader.DataBaseClass;
import com.android.QuikE.R;

public class CategoryList extends Activity {

	// the header for this activty ....
	private RelativeLayout mMoveToHeader;

	// this layout holds the grid for the category lists
	private LinearLayout mCategoryListHolder;
	
	// For closing the Move to list 
	private ImageView mCloseImage;

	// Database name
	public static final String DATABASE_NAME = "BookDataBase.db";

	// from table OrganizerHome
	public static final String ORGANIZER_HOME = "OrganizerHome";
	public static final String KEY_NOTENAME = "NoteName";
	public static final String KEY_CATEGORYID = "CategoryId";

	// From table CategoryTable
	public static final String CATEGORY_TABLE = "CategoryTable";
	public static final String KEY_CATEGORYNAME = "CategoryName";

	
	// cursor for selecting category names and adding new categories to Table
	private Cursor mNewCategoryCursor;

	
	// holds the count of categories in table
	int mCategoryCount;

	// holds the name of category selected to move to
	private String mSelectedMoveToCategory;

	// An array of buttons for selecting the category to move to
	private Button mCategoryButton[];

	// Button for creating the new Category
	private Button mNewCategoryButton;

	// this parameter holds the id of current note
	int mCurrentNoteId;
	
	DataBaseClass mDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moveto);

		// Object for the class of database
		mDatabase = new DataBaseClass(this);
		
		
		// Selecting all category names to populate on screen
			
		mNewCategoryCursor = mDatabase.returnAllCategoryName();//mNoteDB.rawQuery("SELECT CategoryName FROM CategoryTable", null);

		// getting the count of categories
		mCategoryCount = mNewCategoryCursor.getCount();
		mNewCategoryCursor.moveToFirst();

		// Catching the data from the Note_Edit class having new category name
		Bundle mBundle = CategoryList.this.getIntent().getExtras();
		
		if (mBundle != null) {
			if (mBundle.containsKey("lastNoteIdNO_key")) {
				mCurrentNoteId = mBundle.getInt("lastNoteIdNO_key");
				
			}

		}
		
		// This image is for closing the move to window 
		mCloseImage = (ImageView)findViewById(R.id.close_image);
		mCloseImage.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				CategoryList.this.finish();
				
			}
		});
		
		
		
		// Linear Layout for holding the buttons
		mCategoryListHolder = (LinearLayout) findViewById(R.id.cate_listholder);

		// String array to hold the category names
		String[] smCategoryData = null;

		// Declaring the arrays
		mCategoryButton = new Button[mCategoryCount];
		smCategoryData = new String[mCategoryCount];

		// Button for adding new category
		mNewCategoryButton = new Button(this);
		mNewCategoryButton.setText("Add Category");
		mNewCategoryButton.setTextColor(Color.WHITE);
		mNewCategoryButton.setBackgroundResource(R.drawable.itemstabnonselected);

		// Listener for button for adding new category
		mNewCategoryButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				mNewCategoryButton
						.setBackgroundResource(R.drawable.itemstabselected);
				mNewCategoryButton.setTextColor(Color.WHITE);
				Intent mNewCategoryIntent = new Intent(CategoryList.this,CategoryEdit.class);
				Bundle mBundle = new Bundle(); // note_bundle
				mBundle.putInt("currentNoteIdCL_key", mCurrentNoteId);// current_note_id
				mNewCategoryIntent.putExtras(mBundle);
				CategoryList.this.finish();
				startActivity(mNewCategoryIntent);

			}
		});

		// Listener for the array of buttons
		// getting category name from the button
		// selecting id of category based on category name selected
		// updating the categoryid in table OrganizerHome..
		// ugainst current note id . .
		OnClickListener moveCategoryListener = new OnClickListener() {

			public void onClick(View view) {

				((Button) view)
						.setBackgroundResource(R.drawable.itemstabselected);
				((Button)view).setTextColor(Color.WHITE);
				mSelectedMoveToCategory = (String) ((Button) view).getText();

				// Changing the Category Id field of Organizer Home

				
				mNewCategoryCursor = mDatabase.returnCategoryName(mSelectedMoveToCategory);
				
				
				mNewCategoryCursor.moveToFirst();
				// taking the current category id from category table.
				int mCurrentCategortyId = mNewCategoryCursor.getInt(0);

				
				mDatabase.updateOrganizerHomeCategory(mCurrentCategortyId,mCurrentNoteId);
				
				
				// Message for confirmation of note saved 
				String mInfoMessage = "Note successfully moved to "+mSelectedMoveToCategory+" category";
				
				// Sending intent to MoveInfo class with info message  
				Intent mMoveInfoIntent = new Intent(CategoryList.this,MoveInfo.class);
				Bundle mBundle = new Bundle(); // note_bundle
				mBundle.putString("infomessageCL_key",mInfoMessage);// current_note_id
				mMoveInfoIntent.putExtras(mBundle);
				CategoryList.this.finish();
				startActivity(mMoveInfoIntent);
			}

		};// end of onclick listener

		// populating the layout with buttons
		for (int i = 0; i < mCategoryCount; i++) {
			mCategoryButton[i] = new Button(this);
			smCategoryData[i] = mNewCategoryCursor.getString(0);
			mCategoryButton[i].setText(smCategoryData[i]);
			mCategoryButton[i].setTextColor(Color.WHITE);
			mCategoryButton[i].setWidth(320);
			mCategoryButton[i]
					.setBackgroundResource(R.drawable.itemstabnonselected);
			mCategoryButton[i].setOnClickListener(moveCategoryListener);
			mCategoryListHolder.addView(mCategoryButton[i]);
			mNewCategoryCursor.moveToNext();

		}

		// adding the new category button to parent
		mCategoryListHolder.addView(mNewCategoryButton);

	}


	@Override
	protected void onDestroy() {
		super.onDestroy(); // closing cursor used for inserting and updating
		// the category name and id
		if (mNewCategoryCursor != null)
			mNewCategoryCursor.close();
	}

}
