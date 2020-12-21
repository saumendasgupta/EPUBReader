
package com.sdg.organizer;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.widget.AdapterView.OnItemClickListener;

import com.sdg.EPUBReader.ActiveWindow;
import com.sdg.EPUBReader.DataBaseClass;
import com.sdg.EPUBReader.EpubReader;
import com.sdg.EPUBReader.MyCabinet;
import com.android.QuikE.R;

public class NoteOrganizer extends ActivityGroup {
	/* create a new intent for BroadCast */
	Intent mActionIntent = new Intent(Intent.ACTION_MAIN);
	/* create a object of Broadcast class */
	BroadcastReceiver mBroadCast = new BroadcastReceiver() {
		/* when message received to finish the home page this will execute */
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getIntExtra("FinishNotePage", 2) == 1) {
				// TODO Auto-generated method stub
				
				NoteOrganizer.this.finish();
				
				
			}
			/* if want to finish all pages */
			if (intent.getIntExtra("FinishAllPage", 2) == 1) {
				// TODO Auto-generated method stub
				
				NoteOrganizer.this.finish();
				
				startActivity(new Intent(NoteOrganizer.this, EpubReader.class));
			}
			/* if want to finish note page from active window */
			if (intent.getIntExtra("FinishNotePageForActiveWindow", 2) == 1) {
				// TODO Auto-generated method stub
				
				NoteOrganizer.this.finish();
				
				startActivity(new Intent(NoteOrganizer.this, EpubReader.class));
			}
		}
	};
	/** Called when the activity is first created. */

	// local activity manager
	public static LocalActivityManager mLocalActivityManager;

	// states whether the cabinet is open or closed
	public static boolean smCabinetOpen = false;

	// View to add cabinet
	private static View mMyCabinetview;

	// this takes the current note id for the active window reference
	public static int mCurrentNoteIdActiveWindow = 0;
	
	// This stores the last note that is created
	int mCurrentNoteId;

	// Layout for holding the data in a note
	private LinearLayout mNoteDataHolder;// note_data_holder;

	// GridView that holds the icons for menu items of NoteOrganizer
	private GridView mNoteMenuItems;

	// cursorto select the category name from database
	private Cursor mCategoryIdCursor;

	// cursor for selecting the note data.
	private Cursor mGetNoteDataCursor;

	// cursor for populating the activ window table
	private Cursor mActiveNoteCursor;

	// cursor to select the current note name from the database
	private Cursor mCurrentNoteCursor;

	// this string is to hold the default datatype for Notedatatable
	private String mDefaultDataType = "text";

	// this string holds the data that is dragged from the cabinet to note
	private String mTextDataString;

	// Image view to go to Active window
	private ImageView mGotoActiveWindow;

	// holds the count of data in note
	private int mNoteDataCount;

	// An array of textviews to show the text contents of note
	private TextView mNoteDataText[];

	// array of image views to show the image contents of the note
	private ImageView mNoteDataImage[] = null;

	// Image view to separate the text data in the note organizer
	private ImageView[] mNoteDataSeparator;

	// String to hold text data of note
	private String[] mNoteData = null;

	// Bitmap for note image content
	private Bitmap[] mNoteImageBitmap = null;

	// Text View to display the note name on the header bar
	private TextView mNoteHeadText;

	// flag to close the note
	static boolean toCloseNote;

	// Textview to display the number of notes in the note ...
	private TextView mNoteNumberDisplay;

	// Parent layout for the whole NoteOrganizer class
	static RelativeLayout mNotesMainContainer;

	// Database class object...
	private DataBaseClass mDatabase;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_organizer);

		/* indicate that user in Note Showing page */
		EpubReader.mPageStatus = EpubReader.NOTE_DISPLAY;

		/* create a intent filter object */
		IntentFilter iFilter = new IntentFilter(Intent.ACTION_DEFAULT);
		/* register the receiver */
		registerReceiver(mBroadCast, iFilter);

		// object of the data base class
		mDatabase = new DataBaseClass(this);

		// Text View to display the current note name in header bar
		mNoteHeadText = (TextView) findViewById(R.id.note_head_text);

		// Catching the intent data from Organizer.class
		Bundle mBundle = NoteOrganizer.this.getIntent().getExtras();
		if (mBundle != null) {

			// mCurrentNoteId holds the id of the last note that is added
			if (mBundle.containsKey("lastNoteId_key")) {
				// taking the last note id that is inserted
				mCurrentNoteId = mBundle.getInt("lastNoteId_key");

				// selecting the note name corresponding to selected note id
				mActiveNoteCursor = mDatabase
						.returnTextForOrganizer(mCurrentNoteId);

				mActiveNoteCursor.moveToFirst();

				String mActiveNoteName = mActiveNoteCursor.getString(0);

				// passing notename and noteid to update the activeWindowTable
				updateActiveWindowData(mActiveNoteName, mCurrentNoteId);

				// method to set the name of the note in the head bar
				setCurrentNoteHead();

			}

			// receiving the intent containing current note id
			// text id and string from the cabinet, when data is dragged from
			// cabinet
			else if (mBundle.containsKey("dragTextStringC_key")
					&& mBundle.containsKey("lastNoteIdC_key")) {
				mCurrentNoteId = mBundle.getInt("lastNoteIdC_key");
				mTextDataString = mBundle.getString("dragTextStringC_key");
				smCabinetOpen = true;

				mTextDataString = filterString(mTextDataString);

				// Inserting the values in NoteDataTable... with default type as
				// text
				mDatabase.insertIntoNoteDataTable(mCurrentNoteId,
						mDefaultDataType, mTextDataString);

				setCurrentNoteHead();

				mDatabase.deleteAfterDragToNote(MyCabinet.mDeletePosition);

				// Method to open the cabinet
				mOpenCabinet();
			}

			// receiving the intent containing current note id
			// and flag to indicate that image is dragged from cabinet

			else if (mBundle.containsKey("dragImageC_key")
					&& mBundle.containsKey("lastNoteIdC_key")) {
				mCurrentNoteId = mBundle.getInt("lastNoteIdC_key");
				smCabinetOpen = true;

				// calling database method to insert image data in current note
				mDatabase
						.insertIntoNoteDataTable(mCurrentNoteId, "image", null);

				setCurrentNoteHead();
				// deleting the saved data from the cabinet
				mDatabase.deleteAfterDragToNote(MyCabinet.mDeletePosition);
				mOpenCabinet();
			}
			// receiving intent from NoteOrganizer after cliked on cabinet down
			else if (mBundle.containsKey("lastNoteIdC_key")) {
				mCurrentNoteId = mBundle.getInt("lastNoteIdC_key");// last_noteid

				// method to set the name of the note in the head bar
				setCurrentNoteHead();
			}

			// receiving the intent from CategoryEdit class containing
			// new category name and current note id
			// taking the category name we find the id of that category
			// in CategoryTable,
			// Taking the id we update the CategoryId field in OrganizerHome
			// against the current note id ....
			else if (mBundle.containsKey("newCategoryCE_key")) {
				String mNewCategoryName = mBundle
						.getString("newCategoryCE_key");
				mCurrentNoteId = mBundle.getInt("currentNoteIdCE_key");

				Organizer.smRefreshCategories = true;

				// Inserting the new category Name in Category Table
				
				mDatabase.insertIntoCategoryTable(mNewCategoryName);
				
				mCategoryIdCursor = mDatabase
						.returnCategoryName(mNewCategoryName);

				mCategoryIdCursor.moveToFirst();
				// taking the current category id from category table.
				int mCurrentCategortyId = mCategoryIdCursor.getInt(0);

				mDatabase.updateOrganizerHomeCategory(mCurrentCategortyId,
						mCurrentNoteId);

				// method to set the name of the note in the head bar
				setCurrentNoteHead();

			}
			// receiving intent from Organizer when a particular note is
			// selected
			else if (mBundle.containsKey("selectedNoteId_key")) {
				mCurrentNoteId = mBundle.getInt("selectedNoteId_key");// last_noteid

				mActiveNoteCursor = mDatabase
						.returnTextForOrganizer(mCurrentNoteId);

				mActiveNoteCursor.moveToFirst();
				String tempNoteName = mActiveNoteCursor.getString(0);

				updateActiveWindowData(tempNoteName, mCurrentNoteId);

				// method to set the name of the note in the head bar
				setCurrentNoteHead();

			}

			mCurrentNoteIdActiveWindow = mCurrentNoteId;
			
			//_________________________________________________
		}

		// To goto Active Window
		mGotoActiveWindow = (ImageView) findViewById(R.id.note_active_window);

		mGotoActiveWindow.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent mGotoActiveWindowIntent = new Intent(NoteOrganizer.this,
						ActiveWindow.class);
				mGotoActiveWindowIntent.putExtra("FromHome", 3);
				startActivity(mGotoActiveWindowIntent);

				// for animation on opening activewindow
				overridePendingTransition(R.anim.push_down_in, 0);

				// TODO Auto-generated method stub

			}
		});

		// For opening the active window
		RelativeLayout mPageHeader = (RelativeLayout) findViewById(R.id.note_header);

		mPageHeader.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent mActiveIntent = new Intent(NoteOrganizer.this,
						ActiveWindow.class);
				mActiveIntent.putExtra("FromHome", 3);
				startActivity(mActiveIntent);

				// for animation on opening activewindow
				overridePendingTransition(R.anim.push_down_in, 0);

			}
		});

		// Relative Layout for holding notes(mid on screen)
		mNoteDataHolder = (LinearLayout) findViewById(R.id.note_data_holder);

		// method for populating the contents of current note
		populateCurrentNote();

		// Grid view that holds the menu items in note
		mNoteMenuItems = (GridView) findViewById(R.id.note_menuitems_grid);

		// goto cabinet image (down in screen)
		final RelativeLayout gotoCabinet = (RelativeLayout) findViewById(R.id.notefooter);

		// getting cabinet on on click
		gotoCabinet.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (smCabinetOpen == false) {

					// Since cabinet view is added and is transparent so we have
					// to remove all views coming behind cabinet

					View mNoteNumberImage = (ImageView) findViewById(R.id.note_pagenumber_back);
					mNoteDataHolder.removeView(mNoteNumberImage);

					// Method to open the cabinet
					mOpenCabinet();

				}

			}

		});

		// setting adapter for note menu items...
		mNoteMenuItems.setAdapter(new mImageAdapterNoteMenuItems(this));
		mNoteMenuItems.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> ImageAdapterPlaceHold,
					View v, int position, long id) {

				// conditions for each menu item...
				if (position == 0) {
					smCabinetOpen = false;

					// Calling the organizer

					Intent getOrganizerIntent = new Intent(NoteOrganizer.this,
							Organizer.class);
					NoteOrganizer.this.finish();
					startActivity(getOrganizerIntent);

				} else if (position == 1) {

					// Calling move to activity and passing current note id

					Intent getCategoryList = new Intent(NoteOrganizer.this,
							CategoryList.class);
					Bundle mLastNoteBundle = new Bundle(); // note_bundle
					mLastNoteBundle.putInt("lastNoteIdNO_key", mCurrentNoteId);// last_note_id_key
					getCategoryList.putExtras(mLastNoteBundle);

					startActivity(getCategoryList);
				}

			}// End of onItemClick

		}); // End of setOnItemClickListener for menu_items

	}// End of onCreate

	// method to filter the string (removing the ')
	private String filterString(String mDataToFilter) {
		// TODO Auto-generated method stub
		String mSplitStringGruop[];
		String mSplitString;
		mSplitStringGruop = mDataToFilter.split("'");
		int i = 1;

		mSplitString = mSplitStringGruop[0];
		while (mSplitStringGruop.length > i) {

			mSplitString += "''" + mSplitStringGruop[i];
			i++;
		}
		return mSplitString;

	}

	// This method is for opening the cabinet ....
	protected void mOpenCabinet() {
		// TODO Auto-generated method stub

		// Intent for calling the cabinet
		mDatabase.updateCabinetTable();

		Intent getCabinet = new Intent(NoteOrganizer.this, MyCabinet.class);
		MyCabinet.CABINET_OPEN_FROM_NOTE = true;
		Bundle mCurrentNoteBundle = new Bundle();

		// sending the current note id to cabinet
		mCurrentNoteBundle.putInt("lastNoteIdNO_key", mCurrentNoteId);
		getCabinet.putExtras(mCurrentNoteBundle);

		mLocalActivityManager = NoteOrganizer.this.getLocalActivityManager();
		mMyCabinetview = mLocalActivityManager.startActivity("MyCabinetID",
				getCabinet).getDecorView();
		WindowManager mWindowManager = getWindowManager();
		Display mDisplay = mWindowManager.getDefaultDisplay();
		int mWidth = mDisplay.getWidth();
		int mHeight = mDisplay.getHeight();
		mMyCabinetview.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, mHeight));

		mNotesMainContainer = (RelativeLayout) findViewById(R.id.note_main_container);// ///
		mNotesMainContainer.addView(mMyCabinetview);

		if (smCabinetOpen == false) {
			Animation mCabinetOpenAnim = AnimationUtils.loadAnimation(
					NoteOrganizer.this, R.anim.push_up);
			mMyCabinetview.startAnimation(mCabinetOpenAnim);
		}

		smCabinetOpen = true;

	}

	// setting the note name inthe header
	private void setCurrentNoteHead() {
		// TODO Auto-generated method stub
		mCurrentNoteCursor = mDatabase.returnTextForOrganizer(mCurrentNoteId);
		mCurrentNoteCursor.moveToFirst();
		String mActiveNoteName = mCurrentNoteCursor.getString(0);

		mNoteHeadText.setText(mActiveNoteName);

	}

	// this method is for updating the active window table ...
	// when any note is selected ....
	private void updateActiveWindowData(String noteName, int currentNoteId) {

		mDatabase.insertNoteInActiveTable(noteName, currentNoteId);

	}

	// ImageAdapter class for handling menu items of Note Window

	public class mImageAdapterNoteMenuItems extends BaseAdapter {
		public mImageAdapterNoteMenuItems(Context c) {
			mContext = c;
		}

		public int getCount() {

			return mMenuItems.length;

		}

		public Object getItem(int position) {

			return position;
		}

		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView mImageView = new ImageView(mContext);

			if (convertView == null) {
				mImageView = new ImageView(mContext);
				mImageView.setLayoutParams(new GridView.LayoutParams(50, 50));
				mImageView.setAdjustViewBounds(false);
				mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
				// mImageView.setPadding(12, 13, 12, 14);
				mImageView.clearFocus();

			} else {
				mImageView = (ImageView) convertView;
			}

			mImageView.setImageResource(mMenuItems[position]);

			return mImageView;
		}// End of getView

		private Context mContext;

		// Image Resources for menu items
		private Integer[] mMenuItems = { R.drawable.organizericon,
				R.drawable.moveicon, R.drawable.disabledsearchicon,
				R.drawable.settingsicon, R.drawable.sendtoicon,
				R.drawable.textediticon, R.drawable.annotateicon,
				R.drawable.deleteicon };

	}// End ImageAdapterMenuItems

	// for closing the cabinet
	public static void CloseCabinet() {
		smCabinetOpen = false;
		mNotesMainContainer.removeView(mMyCabinetview);

	}

	// This method is for populating the current note data
	private void populateCurrentNote() {
		// TODO Auto-generated method stub

		// this call returns the cursor that contains all the data in
		// a particular note
		mGetNoteDataCursor = mDatabase.getNoteData(mCurrentNoteId);

		// counting the number of data in cursor
		mNoteDataCount = mGetNoteDataCursor.getCount();
		mGetNoteDataCursor.moveToFirst();

		// creating new TextView to show note contents
		mNoteDataText = new TextView[mNoteDataCount];

		// This image view is for separating each text data
		mNoteDataSeparator = new ImageView[mNoteDataCount];

		// This string contains the note data
		mNoteData = new String[mNoteDataCount];

		// This bitmap contains image data
		mNoteImageBitmap = new Bitmap[mNoteDataCount];

		mNoteDataImage = new ImageView[mNoteDataCount];

		// Animation for translation of note data when a new note data is
		// dragged from cabinet to note
		Animation an = new TranslateAnimation(0, 0, 20, 0);
		an.setDuration(300);

		for (int i = 0; i < mNoteDataCount; i++) {
			mNoteDataText[i] = new TextView(this);
			mNoteDataImage[i] = new ImageView(this);

			// For note separator image
			mNoteDataSeparator[i] = new ImageView(this);
			mNoteDataSeparator[i].setBackgroundResource(R.drawable.separator);



			if (mGetNoteDataCursor.getString(1) == null) {
				
				// For showing the note data in image view and setting the props of
				// the imageview 
				mNoteDataImage[i].setMaxHeight(200);
				mNoteDataImage[i].setMaxWidth(200);
				byte[] mTempByteImage = mGetNoteDataCursor.getBlob(2);
				mNoteImageBitmap[i] = BitmapFactory.decodeByteArray(
						mTempByteImage, 0, mTempByteImage.length);
				mNoteDataImage[i].setImageBitmap(mNoteImageBitmap[i]);
				
				// adding views to LInear layout
				mNoteDataHolder.addView(mNoteDataImage[i]);
				mNoteDataHolder.addView(mNoteDataSeparator[i]);
			}
			// for text
			else if (mGetNoteDataCursor.getBlob(2) == null) {
				
				// For showing the note data in text view and setting the props of
				// the text view 
				
				mNoteData[i] = mGetNoteDataCursor.getString(1);
				mNoteDataText[i].setText(mNoteData[i]);
				mNoteDataText[i].setTextColor(Color.BLACK);
				mNoteDataText[i].setWidth(440);
				mNoteDataText[i].setTypeface(Typeface.SANS_SERIF);
				mNoteDataText[i].setTextSize(18);
				
				// adding views to LInear layout
				mNoteDataHolder.addView(mNoteDataText[i]);
				mNoteDataHolder.addView(mNoteDataSeparator[i]);
			}
			if (i == mNoteDataCount - 1 ) {
				mNoteDataSeparator[i].setAnimation(an);
				mNoteDataText[i].setAnimation(an);
			}

			// adding the text and separator image to the note holder

			mGetNoteDataCursor.moveToNext();
		}

		// displaying the count of note data in a note below on the screen
		mNoteNumberDisplay = (TextView) findViewById(R.id.note_number);
		mNoteNumberDisplay.setText("" + mNoteDataCount);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == 9) {
			NoteOrganizer.this.finish();
		
		}

	}

	/* This code is to return in home */
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		switch (KeyCode) {
		case KeyEvent.KEYCODE_BACK:

			mLocalActivityManager = null;
			smCabinetOpen = false;
			finish();
			/*
			 * this intent will use to finish all pages without the home page
			 * which is in the background
			 */
			Intent mFinishHomePage = new Intent(Intent.ACTION_DEFAULT);
			/* put intent to finish the home page */
			mFinishHomePage.putExtra("FinishDisplayPage", 4);
			/* send the broadcast to home page */
			sendBroadcast(mFinishHomePage);

			startActivity(new Intent(NoteOrganizer.this, EpubReader.class));
			return true;
		}
		return false;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mCategoryIdCursor != null)
			mCategoryIdCursor.close();

		// cursor for selecting the note data.
		if (mGetNoteDataCursor != null)
			mGetNoteDataCursor.close();

		// cursor for populating the activ window table
		if (mActiveNoteCursor != null)
			mActiveNoteCursor.close();

		if (mCurrentNoteCursor != null)
			mCurrentNoteCursor.close();

		this.finish();
	}

}
