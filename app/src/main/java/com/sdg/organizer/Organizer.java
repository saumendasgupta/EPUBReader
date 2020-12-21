
package com.sdg.organizer;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
//import android.view.animation.Animation;
//import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.sdg.DisplayManager.Display_Manager;
import com.sdg.EPUBReader.ActiveWindow;
import com.sdg.EPUBReader.DataBaseClass;
import com.sdg.EPUBReader.EpubReader;
import com.sdg.EPUBReader.MyCabinet;
import com.android.QuikE.R;

@SuppressWarnings("deprecation")
public class Organizer extends ActivityGroup implements OnGestureListener {

	/* create a new intent for BroadCast */
	Intent mActionIntent = new Intent(Intent.ACTION_MAIN);
	/* create a object of Broadcast class */
	BroadcastReceiver mBroadCast = new BroadcastReceiver() {
		/* when message received to finish the home page this will execute */
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getIntExtra("FinishOrganizerHomePage", 1) == 1)
				Organizer.this.finish();
			
		}
	};

	// For Cabinet Open
	public static int smForCabinetChangeID = 0;

	// Checking if list view is open
	public static boolean smListViewOpen;

	// Cursor mCursor=null;
	// SQLiteDatabase mNoteDB;

	// cursor used at start
	private Cursor mCategoryCursor;

	// for getting the note id of last added note
	private Cursor mLastNoteCursor;

	// cursor to add view.
	private Cursor mCascadeViewCursor;

	// For Recent note table operation
	private Cursor mRecentNoteCursor;

	// For Active Note Table Operation
	private Cursor mActiveNoteCursor;

	// For getting the note content for display on the first page
	private Cursor mGetNoteContentCursor;

	// default note category id for allnotes
	// ( 1 because 1st category will be allnotes by default)
	private int mCategoryDefaultId = 1;

	// Default note name... while inserting unique number is also added
	private String mNoteDefaultName = "New Note";

	// Category selected by user (default All notes)
	private String mSelectedCategory = "All Notes";

	// smRowCount stores the count of notes in particular category
	// smAllRowCount stores the count of all notes
	static int smRowCount = 0;
	static int smAllRowCount = 0;

	// stores the number of categories (folders) in database
	private int mCategoryCount;

	// private int row_count2; // should always be 1, its for selecting
	// particular row from table
	private int mNoteIndex;
	private int mNameIndex;
	private int mCategoryIndex;

	// Default front note number
	// When switching the category ... the note number 1 will always be at front
	private int mFrontNoteNumber = 1;

	// Initial count of notes
	private int mNoteCounter = 0;

	// stores the names of all notes from database.
	static String smNoteData[];

	// stores the categories from the databases.
	static String smCategoryData[];

	// Absolute view for showing cascaded notes
	private AbsoluteLayout mNotesHolder;

	// Relative layout that holda the grid view for category
	private RelativeLayout mCategoryHolder;

	// Grid View that holds the menu items for organizer(up in screen)
	private GridView mMenuItems;

	// Grid View that holds the note folders (below in screen)
	private GridView mNoteFolders;

	// holds the name of the note selected from the cascade view
	public static int mSelectedNoteId;

	// background image for displaying note number
	private ImageView mPagenumberBack;

	// A text view that display the number of text ...
	private TextView mNoteNumberDisplay;

	// For switching between Cascade View
	private ImageView mCascadeView;

	// For switching between list view
	private ImageView mListview;

	// Image view for calling the Active Window
	private ImageView mGotoActiveWindow;

	// / This layout is parent for page number display and view switching
	// imageviews
	private RelativeLayout mNotesFooter;

	// for storing the current note ids on display
	private int[] mCurrentNotesOnDisplay;

	// defining the middle horizontal pixel of screen
	final int MID_VERTICAL_PIXEL = 160;

	// the base number for category that will be displayed highlighted
	private final static int CATEGORY_BASE = 2;

	// To check whether the intent is coming from Cabinet
	private boolean isComingFromCabinet = false;

	// this stores the id value of the selected category
	int mSelectedCategoryId = 1;

	// Flag for the state of cabinet
	public static boolean smCabinetOpen = false;

	// flag to refresh the category block
	public static boolean smRefreshCategories = true;

	// flag to set the delete functionality...
	public static boolean smDeleteNote = false;

	// Offsets / difference for image and text placeholders for cascading
	private static final int NOTE_IMAGE_HOR_OFFSET = 200;
	private static final int NOTE_IMAGE_VERT_OFFSET = 140;
	private static final int NOTE_TEXT_HOR_OFFSET = 180;
	private static final int NOTE_TEXT_VERT_OFFSET = 150;

	// base starting value of images and text placeholders for cascading
	private static final int NOTE_IMAGE_HOR_BASE = 90;
	private static final int NOTE_IMAGE_VERT_BASE = 350;
	private static final int NOTE_TEXT_HOR_BASE = 160;
	private static final int NOTE_TEXT_VERT_BASE = 395;

	// Base and offset/ diff for the note content in cascade view

	private static final int NOTE_CONTENT_HOR_BASE = 130;
	private static final int NOTE_CONTENT_VERT_BASE = 475;
	private static final int NOTE_CONTENT_HOR_OFFSET = 200;
	private static final int NOTE_CONTENT_VERT_OFFSET = 170;

	// Base and offset for the note content width and height
	private static final int NOTE_CONTENT_WIDTH_BASE = 230;
	private static final int NOTE_CONTENT_HEIGHT_BASE = 190;
	private static final int NOTE_CONTENT_WIDTH_OFFSET = 40;
	private static final int NOTE_CONTENT_HEIGHT_OFFSET = 45;

	// offset or diff for the image height and width
	private static final int NOTE_IMAGE_MAXHEIGHT_OFFSET = 60;
	private static final int NOTE_IMAGE_MAXWIDTH_OFFSET = 60;

	// base starting height and width of image and data for cascading
	private static final int NOTE_IMAGE_MAXHEIGHT_BASE = 450;
	private static final int NOTE_IMAGE_MAXWIDTH_BASE = 450;

	// Height values of the 3 note images in cascade view

	private static final int NOTE_IMAGE_FIRST_HEIGHT = 423;
	private static final int NOTE_IMAGE_SECOND_HEIGHT = 312;
	private static final int NOTE_IMAGE_THIRD_HEIGHT = 231;

	// Initial count of the categories when system is booted up ...
	private static final int INITIAL_CATEGORY_COUNT = 5;

	// The total number of categories that will be visible on the screen
	private static final int CATEGORY_COUNT_DISPLAY = 5;

	// The total number of notes on display at a time in cascade view
	private static final int NOTES_COUNT_ON_DISPLAY = 3;

	// Maximum number of entries in Recent Note Table
	private static final int RECENT_NOTE_MAX_COUNT = 6;

	// Localactivity manager for starting a child activity
	protected static LocalActivityManager mLocalActivityManager;

	// swipe distance limit for fling
	private static final int SWIPE_MIN_DISTANCE = 60;

	// Gesture scanner for Fling Operation
	private GestureDetector gestureScanner;

	// Layout for opening the active window
	private RelativeLayout mPageHeader;

	// Height of the note holder for the list view
	private static final int NOTE_HOLDER_HEIGHT = 929;

	// Object of the DataBaseClass in the EpubReader package
	private DataBaseClass mDatabase;

	// Absolute Layout for showing the transition of categories
	private AbsoluteLayout mCategoryTransitionContainer;

	// creating the object of class PageTransition where the animations are
	// defined 
	// previously Page_Transition
	//private Transitions transitions;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.organizer_home);

		/* create a intent filter object */
		IntentFilter iFilter = new IntentFilter(Intent.ACTION_DEFAULT);
		/* register the receiver */
		registerReceiver(mBroadCast, iFilter);

		// Function for initializing all components
		initializeAllComponents();

		// Creating the database
		// creating the database using cursor c
		try {

			// getting all the notes list from the organizer table
			mCategoryCursor = mDatabase.returnAllFromOrganizeHome();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// catching the bundle from cabinet ...
		// to check whether the intent is coming from cabinet of saumen ...
		Bundle mFromCabinetBundle = Organizer.this.getIntent().getExtras();
		if (mFromCabinetBundle != null) {
			// mCurrentNoteId holds the id of the last note that is added
			if (mFromCabinetBundle.containsKey("fromCabinetC_key")) {
				isComingFromCabinet = true;
			}
		}

		// sending cursor pointer to the start
		mCategoryCursor.moveToFirst();

		// Getting the column index for OrganizerHome Table
		mNoteIndex = mCategoryCursor.getColumnIndex("_id"); // 0
		mNameIndex = mCategoryCursor.getColumnIndex("NoteName"); // 1
		mCategoryIndex = mCategoryCursor.getColumnIndex("CategoryId"); // 2

		// counting rows for list view and creating notedata string having
		// note names
		smAllRowCount = mCategoryCursor.getCount();
		smNoteData = new String[smAllRowCount];

		// storing the note names in an array
		for (int i = 0; i < smAllRowCount; i++) {
			smNoteData[i] = mCategoryCursor.getString(mNameIndex);
			mCategoryCursor.moveToNext();
		}
		// sending cursor pointer to the start
		mCategoryCursor.moveToFirst();

		try {
			// calling the method in DataBaseClass
			// returns the names of all the categories
			mCategoryCursor = mDatabase.returnAllCategoryName();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// sending cursor pointer to the start
		mCategoryCursor.moveToFirst();

		// counting number of categories
		mCategoryCount = mCategoryCursor.getCount();

		// Inserting initial categories in database
		// initially there should be five categories when
		// the system boots up

		if (mCategoryCount < INITIAL_CATEGORY_COUNT)

			// calling method in DataBaseClass which inserts
			// 5 initial categories in the database
			mCategoryCount = mDatabase.insertInitialCategories();

		if (smRefreshCategories == true) {

			// selecting category name again after adding to database
			// to fetch the name of added categories
			try {
				// calling the method in DataBaseClass
				// returns the names of all the categories
				mCategoryCursor = mDatabase.returnAllCategoryName();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			mCategoryCursor.moveToFirst();

			smCategoryData = new String[mCategoryCount];
			for (int i = 0; i < mCategoryCount; i++) {

				// doing getString(0) because in query we r getting only
				// category name in cursor so oth field will be categoryname
				smCategoryData[i] = mCategoryCursor.getString(0);
				mCategoryCursor.moveToNext();
			}

		}


		// Listener for switching between listview and cascade view
		OnClickListener mViewchangeListener = new OnClickListener() {

			public void onClick(View v) {

				switch (v.getId()) {
				case R.id.listview: {
					// Removing views from noteholder(absolute view)
					smListViewOpen = true;

					// when switching from cascade to list view changing the
					// icons from
					// active to inactive and vice versa
					mListview
							.setBackgroundResource(R.drawable.listviewactiveicon);
					mCascadeView
							.setBackgroundResource(R.drawable.cascadeviewicon);

					// removing all views before adding the new list activity
					// view
					mNotesHolder.removeAllViews();

					// when switching from the cascade to list view the
					// background
					// changes
					mNotesHolder.setBackgroundResource(R.drawable.listviewbg);
					// Adding scroll list view to noteholder
					Intent mScrollListIntent = new Intent(Organizer.this,
							NotesListScroll.class);
					Bundle mListCategoryBundle = new Bundle();

					// if the flow is coming from the cabinet then adding a flag
					if (isComingFromCabinet == true) {
						mListCategoryBundle.putBoolean("isComingFromCabinet_key",
								isComingFromCabinet);
					}

					// sending the selected category to list view activity
					mListCategoryBundle.putInt("selectedCategoryO_key",
							mSelectedCategoryId);
					mScrollListIntent.putExtras(mListCategoryBundle);

					// Creating local activity manager and starting listscroll
					// activity
					mLocalActivityManager = Organizer.this
							.getLocalActivityManager();
					View mScrollListView = mLocalActivityManager.startActivity(
							"ScrollList", mScrollListIntent).getDecorView();
					WindowManager mWindowManager = getWindowManager();
					Display mDisplay = mWindowManager.getDefaultDisplay();

					// setting the height of the container where the
					// list activity view is to be added
					int height = NOTE_HOLDER_HEIGHT;

					// setting the layout parameters for the list activity view
					mScrollListView.setLayoutParams(new ViewGroup.LayoutParams(
							ViewGroup.LayoutParams.WRAP_CONTENT, height));

					// adding list activity view to the notes holder container
					mNotesHolder.addView(mScrollListView);

					// removing the text view and image view that is for
					// display of the note number because in list view we
					// cannot show the note number on scrolling
					mNotesFooter.removeView(mNoteNumberDisplay);
					mNotesFooter.removeView(mPagenumberBack);

					break;
				}// end of case listview
				case R.id.cascadeview: {

					smListViewOpen = false;
					Intent intent = new Intent(Organizer.this, Organizer.class);
					Organizer.this.finish();
					startActivity(intent);

					overridePendingTransition(0, 0);

					break;
				}// end of case cascadeview
				case R.id.active_window: {
					Intent mActiveWindowIntent = new Intent(Organizer.this,
							ActiveWindow.class);
					mActiveWindowIntent.putExtra("FromHome", 5);
					startActivityForResult(mActiveWindowIntent, 5);
					EpubReader.mPageStatus = EpubReader.ORGANIZER_HOME;
					// for animation on opening activewindow
					overridePendingTransition(R.anim.push_down_in, 0);

					break;
				}// end of case R.id.activewindow
				}// end of switch(v.getId())
			}// end of onClick()
		};// end of listener mViewchangeListener for changing views

		// onclick listener for opening active window when user clicks on page
		// header
		mPageHeader.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent mActiveIntent = new Intent(Organizer.this,
						ActiveWindow.class);
				mActiveIntent.putExtra("FromHome", 5);
				startActivityForResult(mActiveIntent, 5);
				EpubReader.mPageStatus = EpubReader.ORGANIZER_HOME;
				// for animation on opening activewindow
				overridePendingTransition(R.anim.push_down_in, 0);

			}// end of onClick(View v)
		});

		// setting onClicklisteners for the list view, cascade view and
		// active window
		mGotoActiveWindow.setOnClickListener(mViewchangeListener);
		mCascadeView.setOnClickListener(mViewchangeListener);
		mListview.setOnClickListener(mViewchangeListener);

		// setting adapter for note category items(below on screen)
		mNoteFolders.setAdapter(new EfficientCateAdapter(this));
		mNoteFolders.setOnItemClickListener(new OnItemClickListener() {
			String mTempCategory = null;

			// onitemclick on the items of grid view for categories
			public void onItemClick(AdapterView<?> EfficientCateAdapter,
					View v, int position, long id) {

				// Removing all views from layout added for showing the
				// animation for categories
				mCategoryTransitionContainer.removeAllViews();

				// Removing animation container from the parent rel layout
				mCategoryHolder.removeView(mCategoryTransitionContainer);

				// setting layout params of the absolute layout
				mCategoryTransitionContainer
						.setLayoutParams(new RelativeLayout.LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT));

				// Adding the Absolute layout for category animation to the
				// parent relative layout... the transition of category icons
				// will be shown here
				mCategoryHolder.addView(mCategoryTransitionContainer);

				// calling method where the animation / transition effect is
				// defined and passing clicked position, absolute layout on
				// which the annimation is to be shown and Organizer context
				//transitions.categoryTransition(position,
				//		mCategoryTransitionContainer, Organizer.this);

				// duration and translate point of the grid view that is shown
				// to be translated

				//int mAnimDuration = 800;

				//if (position == 1 || position == 3) {
				//	mAnimDuration = 400;
				//}

				//final int mGridMoveAnim = 10000;

				// Defining animation for removing the grid view at back while
				// showing the annimation / transition of note category icons
				//Animation gridan = new TranslateAnimation(0, 0, 0,
						//mGridMoveAnim);
				// setting duration of animation and starting the animation
				//gridan.setDuration(mAnimDuration);
				//mNoteFolders.startAnimation(gridan);

				if (position == 0) {

					// getting offset for category shifting
					int diff = CATEGORY_BASE - position;

					// calling method swap categories for swapping category and
					// changing view in the grid for categories
					swap_categories(diff);

				} else if (position == 1) {

					// getting offset for category shifting
					int diff = CATEGORY_BASE - position;
					// calling method swap categories for swapping category and
					// changing view in the grid for categories
					swap_categories(diff);
				}

				else if (position == 2) {
				} else if (position == 3) {

					// getting offset for category shifting
					int diff = CATEGORY_BASE - position;
					// calling method swap categories for swapping category and
					// changing view in the grid for categories
					swap_categories(diff);

				}

				else if (position == 4) {

					// getting offset for category shifting
					int diff = CATEGORY_BASE - position;
					// calling method swap categories for swapping category and
					// changing view in the grid for categories
					swap_categories(diff);
				}
			}// end of method onItemClick() 

			// method for swapping the placeholders of categories
			private void swap_categories(int diff) {

				if (diff > 0)

					// swapping the categories to left
					for (int j = 0; j < diff; j++) {
						mTempCategory = smCategoryData[smCategoryData.length - 1];

						for (int i = (smCategoryData.length - 1); i > 0; i--) {
							smCategoryData[i] = smCategoryData[i - 1];
						}
						smCategoryData[0] = mTempCategory;
					}
				else if (diff < 0) {
					// swapping the categories to right
					for (int j = 0; j < Math.abs(diff); j++) {
						mTempCategory = smCategoryData[0];
						for (int i = 0; i < smCategoryData.length - 1; i++) {
							smCategoryData[i] = smCategoryData[i + 1];
						}
						smCategoryData[smCategoryData.length - 1] = mTempCategory;
					}
				}

				// Condition for whether the current view is List or Cascade

				if (smListViewOpen == false) {
					mNoteFolders.setAdapter(new EfficientCateAdapter(
							Organizer.this));
					mFrontNoteNumber = 1;
					// because mFrontNoteNumber should be always 1...
					// while switching categories
					// because notes will be displayed from index 1

					// calling addnote() for displaying notes on screen
					addNote();

				} else if (smListViewOpen == true) {

					mNoteFolders.setAdapter(new EfficientCateAdapter(
							Organizer.this));
					// Removing all views and child Activies
					mNotesHolder.removeAllViews();

					// removing all activities before adding the new list
					// activity
					// view
					mLocalActivityManager.removeAllActivities();

					// 2nd will be the mid selected category
					mSelectedCategory = smCategoryData[CATEGORY_BASE];

					Cursor mTempCursor = mDatabase
							.returnCategoryName(mSelectedCategory);

					mTempCursor.moveToFirst();
					int mSelectedCategoryId = mTempCursor.getInt(0);

					Intent mScrollListIntent = new Intent(Organizer.this,
							NotesListScroll.class);

					Bundle mListCateBundle = new Bundle();
					// sending the selected category to list view activity
					mListCateBundle.putInt("selectedCategoryO_key",
							mSelectedCategoryId);

					// if the flow is coming from the cabinet then adding a flag
					if (isComingFromCabinet == true) {
						mListCateBundle.putBoolean("isComingFromCabinet_key",
								isComingFromCabinet);
					}
					mScrollListIntent.putExtras(mListCateBundle);

					// Local Activity manager for list view
					mLocalActivityManager = Organizer.this
							.getLocalActivityManager();
					View mScrollListView = mLocalActivityManager.startActivity(
							"ScrollList", mScrollListIntent).getDecorView();
					WindowManager mWindowManager = getWindowManager();
					Display mDisplay = mWindowManager.getDefaultDisplay();

					// setting the height of list activity view
					int height = NOTE_HOLDER_HEIGHT;
					mScrollListView.setLayoutParams(new ViewGroup.LayoutParams(
							ViewGroup.LayoutParams.WRAP_CONTENT, height));

					// adding list activity view to note holder absolute layout
					mNotesHolder.addView(mScrollListView);

				}

				// setting the flag for refresh categories to false ... because
				// when switching from the cascade view to list view and vice
				// versa
				smRefreshCategories = false;
			}// end of method swap_categories(int)

		});// End of setOnItemClickListener for note_items

		if (smListViewOpen == true)
			callListView();
		else
			// displays the note images and text (cascade)
			addNote();

		// Setting adapter view for menu items (above in screen)
		mMenuItems.setAdapter(new ImageAdapterMenuItems(this));
		mMenuItems.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> ImageAdapterPlaceHold,
					View v, int position, long id) {

				// conditions for each menu item...

				// for making a new note
				if (position == 0) {

					// condition for -
					// if the flow is coming from the cabinet the new note
					// should not be created
					if (isComingFromCabinet == false) {

						// intent for calling another activity
						// mLastNoteId stores the id of the last note that is
						// inserted
						int mLastNoteId = insertIntoNoteTables();

						Intent mIntentNote = new Intent(Organizer.this,
								NoteOrganizer.class);
						Bundle noteid_bundle = new Bundle();
						noteid_bundle.putInt("lastNoteId_key", mLastNoteId);
						mIntentNote.putExtras(noteid_bundle);
						Organizer.this.finish();

						// starting activity NoteOrganizer
						startActivity(mIntentNote);

						// incrementing the note counter
						mNoteCounter++;
					} else if (isComingFromCabinet == true) {

						// Book -> Cabinet -> Organizer -> new Note -> Cabinet

						// inserting new note in the database before going to
						// cabinet
						int mLastNoteId = insertIntoNoteTables();

						// intent for calling back the cabinet class
						// with new note number created
						Intent mSelectedNoteIdIntent = new Intent(
								Organizer.this, MyCabinet.class);
						Bundle mSelectedNoteIdBundle = new Bundle();
						mSelectedNoteIdIntent.putExtra("selectedNoteId_key",
								mLastNoteId);
						Display_Manager.mCabinetOpen = 1;
						smForCabinetChangeID = mLastNoteId;

						mSelectedNoteIdIntent.putExtras(mSelectedNoteIdBundle);

						// sending the note id back to the cabinet
						// to display in cabbinet area
						setResult(1, mSelectedNoteIdIntent);

						Organizer.this.finish();

					}

				} else if (position == 7) {

					// setting the delete note flag to true
					smDeleteNote = true;
				}

			}// End of onItemClick

		}); // End of setOnItemClickListener for mMenuItems

	}// end of method OnCreate()

	private void callListView() {

		// when switching from cascade to list view changing the icons from
		// active to inactive and vice versa
		mListview.setBackgroundResource(R.drawable.listviewactiveicon);
		mCascadeView.setBackgroundResource(R.drawable.cascadeviewicon);

		// changing the background after while switching the views
		mNotesHolder.setBackgroundResource(R.drawable.listviewbg);

		mNoteFolders.setAdapter(new EfficientCateAdapter(Organizer.this));
		// Removing all views and child Activies
		mNotesHolder.removeAllViews();

		// removing all activities before adding the new list
		// activity view
		mLocalActivityManager.removeAllActivities();

		// 2nd will be the mid selected category
		mSelectedCategory = smCategoryData[CATEGORY_BASE];

		Cursor mTempCursor = mDatabase.returnCategoryName(mSelectedCategory);

		mTempCursor.moveToFirst();
		int mSelectedCategoryId = mTempCursor.getInt(0);

		Intent mScrollListIntent = new Intent(Organizer.this,
				NotesListScroll.class);

		Bundle mListCategoryBundle = new Bundle();
		// if the flow is coming from the cabinet then adding a flag
		if (isComingFromCabinet == true) {
			mListCategoryBundle.putBoolean("isComingFromCabinet_key",
					isComingFromCabinet);
		}
		// sending the selected category to list view activity
		mListCategoryBundle.putInt("selectedCategoryO_key", mSelectedCategoryId);
		mScrollListIntent.putExtras(mListCategoryBundle);

		// Local Activity manager for list view
		mLocalActivityManager = Organizer.this.getLocalActivityManager();
		View mScrollListView = mLocalActivityManager.startActivity(
				"ScrollList", mScrollListIntent).getDecorView();
		WindowManager mWindowManager = getWindowManager();
		Display mDisplay = mWindowManager.getDefaultDisplay();

		// setting the height of list activity view
		int height = NOTE_HOLDER_HEIGHT;
		mScrollListView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, height));

		// adding list activity view to note holder absolute layout
		mNotesHolder.addView(mScrollListView);

		// removing the note number display while in listview
		mNotesFooter.removeView(mNoteNumberDisplay);
		mNotesFooter.removeView(mPagenumberBack);
	}//end of method callListView()

	// method for inserting the note entry in Organizer_home when user clicks on
	// new note
	// Note with default name and default category will be inserted
	// And also returning the inserted note id so it can be send to
	// NoteOrganizer
	protected int insertIntoNoteTables() {

		int mLastNoteId = 0;
		try {

			// calling the method in DataBaseClass
			// returns all the notes present in OrganizerHome
			mLastNoteCursor = mDatabase.returnAllNoteId();

			mLastNoteCursor.moveToFirst();
			// getting the last note id from the database
			// mLastNoteid + 1 will be the new note id
			mLastNoteId = mLastNoteCursor.getInt(0);

			// inserting the new note in database table with new note name
			// 'NewNote'+mLastNoteId; -> new note
			mDatabase.insertIntoOrganizerHome(mNoteDefaultName, mLastNoteId,
					mCategoryDefaultId);

			// Inserting note values in RecentNote Table
			insertIntoRecentNoteTable(mNoteDefaultName + mLastNoteId + ".note",
					mLastNoteId + 1);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// the note id of the last inserted note
		return (mLastNoteId + 1);
	} // end of insertIntoNoteTables()

	// method to count the number of notes, add and populating in front screen
	// (mid of screen) in absolute layout
	// this method is responsible for displaying the note data based on the
	// selected category, selection of a particular note and calling
	// NoteOrganizer for displaying the note contents
	private void addNote() {

		// removing all views from the notes holder before pupulating the notes
		// in cascade view
		mNotesHolder.removeAllViews();

		// a string that holds the selected category

		mSelectedCategory = smCategoryData[CATEGORY_BASE];

		// Selecting id from category table where which is selected by user...
		try {

			// This method returns the category id based on category name
			mCascadeViewCursor = mDatabase.returnCategoryName(mSelectedCategory);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		mCascadeViewCursor.moveToFirst();

		// storing the id in an integer ...
		mSelectedCategoryId = mCascadeViewCursor.getInt(0);

		// since 1 category is always 1 so we will select all notes from the
		// database

		try {
			if (mSelectedCategoryId == 1)
				// selecting all notes in a particular category
				// this method retuns all the note names in OrganizerHome
				mCascadeViewCursor = mDatabase.returnAllFromOrganizeHome();
			else
				// this method returns names of notes in a particular category
				mCascadeViewCursor = mDatabase
						.selectAllFromOrganizerOnCategory(mSelectedCategoryId);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// here count notes in selected category will come
		smRowCount = mCascadeViewCursor.getCount();

		mCascadeViewCursor.moveToFirst();

		// holds the note names in particular category
		String[] mNoteDataCategory = new String[smRowCount]; // note_data_categ

		// holds the note ids of notes in particular category
		int[] mNoteIdCategory = new int[smRowCount];

		// for storing the current note ids on display
		// mCurrentNotesOnDisplay stores the ids of the current notes that
		// are being displayed currently on the screen...
		// the array length will be based on number of notes on display
		// if displayed notes are 2 ... array length ll be 2
		if (smRowCount - mFrontNoteNumber < NOTES_COUNT_ON_DISPLAY) {
			mCurrentNotesOnDisplay = new int[smRowCount - mFrontNoteNumber + 1];
		} else {
			mCurrentNotesOnDisplay = new int[NOTES_COUNT_ON_DISPLAY];
		}

		// for selecting notes from cursor and copying them into array (for
		// selecting category wise)
		for (int i = 0; i < smRowCount; i++) {
			mNoteDataCategory[i] = mCascadeViewCursor.getString(mNameIndex);
			mNoteIdCategory[i] = mCascadeViewCursor.getInt(0);
			mCascadeViewCursor.moveToNext();

		}

		// Creating an array of note icons
		final ImageView[] note_image = new ImageView[smRowCount];

		// Creating an array of note textviews that display name of note
		final TextView[] note_text = new TextView[smRowCount];

		// Creating an array of note textviews that display first text content
		// of the note
		TextView[] note_content_text = new TextView[smRowCount];

		// creating the array of image view that shows first image content of
		// note
		ImageView[] note_content_image = new ImageView[smRowCount];

		// Initializing the note icons ImageView and text views for names
		// and note content textviews and imageviews
		for (int i = 0; i < smRowCount; i++) {
			note_image[i] = new ImageView(this);
			note_text[i] = new TextView(this);
			note_content_text[i] = new TextView(this);
			note_content_image[i] = new ImageView(this);

		}

		// Listener for selecting note from cascade view
		OnClickListener mGetNoteListener = new OnClickListener() {

			public void onClick(View view) {

				// checking the height of image and based on that we get which
				// image is selected and then taking the current displayed note
				// id from mCurrentNotesOnDisplay[] and displaying note

				int a = ((ImageView) view).getHeight();
				
				if (((ImageView) view).getHeight() == NOTE_IMAGE_FIRST_HEIGHT) {

					mSelectedNoteId = mCurrentNotesOnDisplay[0];
				} else if (((ImageView) view).getHeight() == NOTE_IMAGE_SECOND_HEIGHT) {

					mSelectedNoteId = mCurrentNotesOnDisplay[1];
				} else if (((ImageView) view).getHeight() == NOTE_IMAGE_THIRD_HEIGHT) {

					mSelectedNoteId = mCurrentNotesOnDisplay[2];
				}

				// Selecting the Note name to be entered into active window
				// and Recent Note Table from the OrganizerHome table
				// this method returns the note name from OrganizerHome based on
				// note id
				Cursor mActiveNoteCursor = mDatabase
						.returnTextForOrganizer(mSelectedNoteId);

				mActiveNoteCursor.moveToFirst();

				// Fetching the note name
				String tempNoteName = mActiveNoteCursor.getString(0);

				// inserting note entry in active window since there is no
				// note entry in the active window table
				mDatabase.insertNoteInActiveTable(tempNoteName, mSelectedNoteId);

				// condition for deleting the note
				if (smDeleteNote == true) {
					// calling data base method to delete the note
					mDatabase.deleteNote(mSelectedNoteId);
					// calling addNote to show the cascade view
					addNote();
					smDeleteNote = false;
				} else {

					// Calling the note depending on whether the isCabinet is
					// true or not ...
					// if isComingFrom Cabinet = true ... intent will be sent to
					// Display Manager
					if (isComingFromCabinet == false) {
						
						// calling the NoteOrganizer for showing note contents
						Intent mSelectedNoteIdIntent = new Intent(
								Organizer.this, NoteOrganizer.class);
						Bundle mSelectedNoteIdBundle = new Bundle();
						mSelectedNoteIdBundle.putInt("selectedNoteId_key",
								mSelectedNoteId);
						mSelectedNoteIdIntent.putExtras(mSelectedNoteIdBundle);

						Organizer.this.finish();
						startActivity(mSelectedNoteIdIntent);
					} else if (isComingFromCabinet == true) {

						// Calling cabinet to show note contents in the cabinet
						Intent mSelectedNoteIdIntent = new Intent(
								Organizer.this, MyCabinet.class);
						Bundle mSelectedNoteIdBundle = new Bundle();
						mSelectedNoteIdIntent.putExtra("selectedNoteId_key",
								mSelectedNoteId);
						Display_Manager.mCabinetOpen = 1;
						smForCabinetChangeID = mSelectedNoteId;
						mSelectedNoteIdIntent.putExtras(mSelectedNoteIdBundle);

						// Selecting count of note values in Active Window Table
						// ....
						// and updating active window table based on that ...
						setResult(1, mSelectedNoteIdIntent);

						Organizer.this.finish();

						// startActivity(mSelectedNoteIdIntent);

					}

					// Inserting the selected Note in RecentNoteTable
					insertIntoRecentNoteTable(tempNoteName, mSelectedNoteId);
				}

			}
		};// end of onclick listener

		// this for loop is the logic for displaying the note icons and note
		// text views in the cascade view stle
		for (int i = smRowCount - mFrontNoteNumber; i >= 0 && i < smRowCount; i--) {
			// <<up>> here smRowCount - mFrontNoteNumber will limit the
			// iteration till names in.. mNoteData[] finish

			int note_count_scroll_current = i + mFrontNoteNumber;
			// note_count_scroll_current is for changing row value at each
			// iteration

			// for storing the ids of notes currently displayed on screen
			// conditins are based on length of mCurrentNotesOnDisplay
			if (mCurrentNotesOnDisplay.length == 1) {
				mCurrentNotesOnDisplay[0] = mNoteIdCategory[mFrontNoteNumber - 1];
			} else if (mCurrentNotesOnDisplay.length == 2) {
				mCurrentNotesOnDisplay[0] = mNoteIdCategory[mFrontNoteNumber - 1];
				mCurrentNotesOnDisplay[1] = mNoteIdCategory[mFrontNoteNumber];
			} else if (mCurrentNotesOnDisplay.length >= 3) {
				mCurrentNotesOnDisplay[0] = mNoteIdCategory[mFrontNoteNumber - 1];
				mCurrentNotesOnDisplay[1] = mNoteIdCategory[mFrontNoteNumber];
				mCurrentNotesOnDisplay[2] = mNoteIdCategory[mFrontNoteNumber + 1];
			}

			// setting the bounds and properties of noteicon at runtime
			// setting the positions and bounds for TextViews
			note_image[i].setAdjustViewBounds(true);

			if (i == 0) {
				note_image[i].setImageResource(R.drawable.cascadeviewicon01);

			} else if (i == 1) {
				note_image[i].setImageResource(R.drawable.cascadeviewicon02);

			} else if (i == 2) {
				note_image[i].setImageResource(R.drawable.cascadeviewicon03);

			} 
			// setting the layout parameters for the note icons
			note_image[i].setLayoutParams(new AbsoluteLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
					NOTE_IMAGE_HOR_BASE + i * NOTE_IMAGE_HOR_OFFSET,
					NOTE_IMAGE_VERT_BASE - i * NOTE_IMAGE_VERT_OFFSET));

			// setting on click listener for the icons of cascade view
			note_image[i].setOnClickListener(mGetNoteListener);

			// setting max height and max width for the note icons
			note_image[i].setMaxWidth(NOTE_IMAGE_MAXWIDTH_BASE - i
					* NOTE_IMAGE_MAXWIDTH_OFFSET);
			note_image[i].setMaxHeight(NOTE_IMAGE_MAXHEIGHT_BASE - i
					* NOTE_IMAGE_MAXHEIGHT_OFFSET);

			// For displaying the note name for only 3 notes
			if (i < NOTES_COUNT_ON_DISPLAY) {

				// setting the layout parameters for note text views ...
				note_text[i].setLayoutParams(new AbsoluteLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						NOTE_TEXT_HOR_BASE + i * NOTE_TEXT_HOR_OFFSET,
						NOTE_TEXT_VERT_BASE - i * NOTE_TEXT_VERT_OFFSET));

				// setting note name for note text views
				note_text[i].setText(mNoteDataCategory[i + mFrontNoteNumber
						- 1]);
				// <<up>> taking mNoteData value from scrolled position

				// setting color and size for the notes text views
				note_text[i].setTextColor(Color.BLACK);
				note_text[i].setTextSize(16.0f - i * 2); 

				// **********properties of the text content holder (text view )
				// is to be modified after getting the new note icons

				// fetching the contents of a particular note from NoteDataTable
				mGetNoteContentCursor = mDatabase.getNoteData(mNoteIdCategory[i
						+ mFrontNoteNumber - 1]);
				mGetNoteContentCursor.moveToFirst();

				// fetching the content of a particular note from the data base
				// and setting the fetched text to the text view
				
				if (mGetNoteContentCursor.getCount() != 0) {

					if (mGetNoteContentCursor.getString(1) == null) {
						// saving the image data (blob)  from db to byte array
						byte[] mTempByteImage = mGetNoteContentCursor
								.getBlob(2);
						Bitmap mNoteContentBmp = BitmapFactory.decodeByteArray(
								mTempByteImage, 0, mTempByteImage.length);

						note_content_image[i].setImageBitmap(mNoteContentBmp);

						// ((ImageView)
						// note_content_text[i]).setImageBitmap(mNoteContentBmp);

					} else if (mGetNoteContentCursor.getBlob(2) == null) {

						note_content_text[i].setText(mGetNoteContentCursor
								.getString(1));

						// setting the text size of each note content text
						note_content_text[i].setTextSize(13.0f - i * 2);
						// setting the color and font face of the text
						note_content_text[i].setTextColor(Color.rgb(36, 143,
								215));
						note_content_text[i].setTypeface(Typeface.SANS_SERIF,
								Typeface.NORMAL);
					}

				}
				// Setting the layout parameters of the text view
				note_content_text[i]
						.setLayoutParams(new AbsoluteLayout.LayoutParams(
								NOTE_CONTENT_WIDTH_BASE - i
										* NOTE_CONTENT_WIDTH_OFFSET,
								NOTE_CONTENT_HEIGHT_BASE - i
										* NOTE_CONTENT_HEIGHT_OFFSET,
								NOTE_CONTENT_HOR_BASE + i
										* NOTE_CONTENT_HOR_OFFSET,
								NOTE_CONTENT_VERT_BASE - i
										* NOTE_CONTENT_VERT_OFFSET));
				
				// setting the layout parameters of the ImageView ( noteimage
				// content)
				note_content_image[i]
						.setLayoutParams(new AbsoluteLayout.LayoutParams(
								NOTE_CONTENT_WIDTH_BASE - i
										* NOTE_CONTENT_WIDTH_OFFSET,
								NOTE_CONTENT_HEIGHT_BASE - i
										* NOTE_CONTENT_HEIGHT_OFFSET,
								NOTE_CONTENT_HOR_BASE + i
										* NOTE_CONTENT_HOR_OFFSET,
								NOTE_CONTENT_VERT_BASE - i
										* NOTE_CONTENT_VERT_OFFSET));

			}
			// Adding the TextView( note name text) and ImageViews(note icons )
			// to the absolute layout ...
			mNotesHolder.addView(note_image[i]);
			mNotesHolder.addView(note_text[i]);

			// Adding the TextView( note text content) and ImageViews( note
			// image contents) to the absolute layout
			mNotesHolder.addView(note_content_text[i]);
			mNotesHolder.addView(note_content_image[i]);

		}

		// Logic for displaying the current note number on note footer layout
		if (smRowCount == 0)
			mNoteNumberDisplay.setText("  0 / " + smRowCount);
		else
			mNoteNumberDisplay.setText("  " + mFrontNoteNumber + " / "
					+ smRowCount);

	}// end of addNote

	// This method is for inserting the note entry in recent note table
	// The logic is that if the recent note table contains less than 3
	// categories the note entry is done
	// if the recent note table contains 3 or more than 3 the note entry is
	// updated
	private void insertIntoRecentNoteTable(String tempNoteName,
			int selectedNoteId) {

		// This method is for retrieving all the notes present in the recent
		// note table
		mRecentNoteCursor = mDatabase.recentNoteOpendRetrive();

		// getting the count of note in Recent note table
		int mRecentNoteCount = mRecentNoteCursor.getCount();
		if (mRecentNoteCount < RECENT_NOTE_MAX_COUNT) {
			mRecentNoteCount++;
			try {

				// insering in recent note table
				mDatabase.insertInRecentNoteTable(selectedNoteId, tempNoteName,
						mRecentNoteCount);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			mDatabase.updateRecentNoteTable();
			try {

				// selecting note from recent note table
				mRecentNoteCursor = mDatabase.selectFromRecentNote(tempNoteName);
				int mcount = mRecentNoteCursor.getCount();
				if (!(mcount > 0)) {
					try {

						// updating the recent note table
						mDatabase.updateDataRecentNoteTable(selectedNoteId,
								tempNoteName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// updating recent note table after checking
				mDatabase.updateRecentNoteTableCheck();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}// end of insertIntoRecentNoteTable

	// Efficient adapter for note item grid to stack both text and icons
	// together

	public static class EfficientCateAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Bitmap mIcon[];

		public EfficientCateAdapter(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			Bitmap mIcon1[] = new Bitmap[CATEGORY_COUNT_DISPLAY];
			// Icons bound to the rows.
			for (int i = 0; i < CATEGORY_COUNT_DISPLAY; i++) {
				if (i == CATEGORY_BASE)
					// setting the icon for opened category
					// in the moddle
					mIcon1[i] = BitmapFactory.decodeResource(context
							.getResources(), R.drawable.allnotes);
				else
					// setting the icon for other categories
					mIcon1[i] = BitmapFactory.decodeResource(context
							.getResources(), R.drawable.miscnotes);
			}

			mIcon = mIcon1;
		}

		public int getCount() {
			return mIcon.length;
			// return 1;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder mHolder; // holder

			// // When convertView is not null, we can reuse it directly, there
			// there is no need to reinflate it.
			// We only inflate a new View when the convertView supplied
			// by ListView is null.
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.note_grid, null);
				// convertView = mInflater.inflate(null, null);
				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				mHolder = new ViewHolder();
				mHolder.text = (TextView) convertView.findViewById(R.id.text);
				mHolder.icon = (ImageView) convertView.findViewById(R.id.icon);

				convertView.setTag(mHolder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				mHolder = (ViewHolder) convertView.getTag();
			}

			// mHolder.icon.setLayoutParams(new GridView.LayoutParams(48, 48));
			// Bind the data efficiently with the holder.

			mHolder.icon.setImageBitmap(mIcon[position]);

			// since the position are different therefore the text font, padding
			// will all be different for all
			if (position == CATEGORY_BASE) {

				// making the icon to go up in middle by setting the padding
				// and setting the properties of text also
				mHolder.icon.setPadding(10, 0, 10, 20);
				mHolder.text.setPadding(30, -4, 0, 0);
				mHolder.text.setTextColor(Color.WHITE);
				mHolder.text.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

			} else if (position == 3 || position == 1) {

				// holding the icon by setting the padding
				// and setting the properties of text also
				mHolder.icon.setPadding(10, 20, 10, 10);
				mHolder.text.setPadding(30, 0, 0, 0);
				mHolder.text.setTextColor(Color.WHITE);
			} else {
				// lowering the icon by setting the padding
				// and setting the properties of text also
				mHolder.icon.setPadding(10, 35, 10, 0);
				mHolder.text.setPadding(30, 5, 0, 0);
				mHolder.text.setTextColor(Color.WHITE);
			}

			mHolder.text.setText(smCategoryData[position]);

			return convertView;
		}// End of getView

		// Class view holder
		public static class ViewHolder {
			public TextView text;
			public ImageView icon;

		}// End of class ViewHolder
	}// End of class EfficientCateAdapter

	// Adapter class for menu items
	// this class is responsible for populating the menu items in the grid
	public class ImageAdapterMenuItems extends BaseAdapter {
		public ImageAdapterMenuItems(Context c) {
			mContext = c;
		}

		public int getCount() {

			return mMenuIcons.length;

		}

		public Object getItem(int position) {

			return position;
		}

		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext);
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.

			// // When convertView is not null, we can reuse it directly, there
			// there is no need to reinflate it.
			// We only inflate a new View when the convertView supplied
			// by ListView is null.
			if (convertView == null) {
				imageView = new ImageView(mContext);

				// setting the layout parameters for grid items
				imageView.setLayoutParams(new GridView.LayoutParams(50, 50));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				imageView.clearFocus();

			} else {
				imageView = (ImageView) convertView;
			}

			// taking the image resources from the mMenuIcons array
			// and setting the icons
			imageView.setImageResource(mMenuIcons[position]);

			return imageView;
		}// End of getView

		private Context mContext;

		// Image Resources for menu items
		private Integer[] mMenuIcons = { R.drawable.newnoteicon,
				R.drawable.disabledsearchicon, R.drawable.disabledsorticon,
				R.drawable.disabledsettingsicon, R.drawable.disabledcabinetsendtoicon,
				R.drawable.disabledtagicon, R.drawable.disabledmergeicon,
				R.drawable.deleteicon };

	}// End ImageAdapterMenuItems

	// This funtioin initilaizes and populates all the views and variables
	private void initializeAllComponents() {

		// Initializing the views
		mMenuItems = (GridView) findViewById(R.id.menuitems_grid);
		mNoteFolders = (GridView) findViewById(R.id.notes_grid);

		// Initializing note holder
		mNotesHolder = (AbsoluteLayout) findViewById(R.id.notes_holder);

		// this text view is for displaying the current note number on screen
		mPagenumberBack = (ImageView) findViewById(R.id.pagenumber_back);

		// For displaying note number using textview and setting its properties
		mNoteNumberDisplay = (TextView) findViewById(R.id.notenumber_display);
		mNoteNumberDisplay.setTextColor(Color.BLACK);

		// For switching between Cascade View
		mCascadeView = (ImageView) findViewById(R.id.cascadeview);

		// For switching between list view
		mListview = (ImageView) findViewById(R.id.listview);

		// / This layout is parent for page number display and view switching
		// imageviews
		mNotesFooter = (RelativeLayout) findViewById(R.id.notes_footer);

		// Gesture Scanner for Fling
		gestureScanner = new GestureDetector(this);

		// For calling the active window
		mGotoActiveWindow = (ImageView) findViewById(R.id.active_window);

		// For opening the active window
		mPageHeader = (RelativeLayout) findViewById(R.id.header);

		// Object for the class of database
		mDatabase = new DataBaseClass(this);

		// layout that holds the note categories
		mCategoryHolder = (RelativeLayout) findViewById(R.id.allnotes_container);

		// Absolute layout for showing category transition
		mCategoryTransitionContainer = new AbsoluteLayout(this);

		// object of class Transitions (Previously Page_Transition)
		//transitions = new Transitions(this);

	}//end of method initializeAllComponents

	// This method is for detecting the touch event done for the scrolling of
	// the notes
	public boolean onTouchEvent(MotionEvent me) {
		return gestureScanner.onTouchEvent(me);
	}//end of method onTouchEvent

	// This method is auto generated by OnGestureListener
	// This method is for handling down event
	public boolean onDown(MotionEvent arg0) {
		// No Operation 
		return false;
	}//end of method onDown()

	// This method is auto generated by OnGestureListener
	// On fling here we are using for the scrolling of the notes
	// if the user scrolls up previous notes will be shown
	// if the user scrolls down next notes will be shown
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		try {
			mDatabase.returnNoteNameForScrolling(mFrontNoteNumber);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			// up down swipe

			// if the swipe is from up to down
			if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE) {
				if (mFrontNoteNumber != 1) {
					mFrontNoteNumber = mFrontNoteNumber - 3;
					if (mFrontNoteNumber <= smRowCount
							&& mFrontNoteNumber > 0) {
						switchnotes();
					}
				}// cursor to add view in cascade view

				// if the swipe is from down to up
			} else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE) {
				if (smRowCount - mFrontNoteNumber > 2) {
					mFrontNoteNumber = mFrontNoteNumber + 3;

					// scroll limit is 3 here
					if (mFrontNoteNumber <= smRowCount) {
						switchnotes();

					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}//end of method onFling()

	// the method for switchnotes i.e. calling the addnote again for populating
	// the notes
	private void switchnotes() {

		mNotesHolder.removeAllViews();

		addNote();
		// displaying note number in notenumber field
		mNoteNumberDisplay.setText("  " + mFrontNoteNumber + " / "
				+ smRowCount);

	}//end of method switchNotes()

	// This method is auto generated by OnGestureListener
	// This method is for handling LongPress event
	public void onLongPress(MotionEvent e) {
		// No Operation 
	}//end of method onLongPress

	// This method is auto generated by OnGestureListener
	// This method is for handling scroll event
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// No Operation 
		return false;
	}//end of methodonScroll

	// This method is auto generated by OnGestureListener
	// This method is for handling press event
	public void onShowPress(MotionEvent e) {
		// No Operation 
	}//end of method onShowPress

	// This method is auto generated by OnGestureListener
	// This method is for handling single tap event
	public boolean onSingleTapUp(MotionEvent e) {
		
		// No Operation 
		return false;
	}//end of method onSingleTap()

	// Ondestroy is the exit point of the activity
	// here we are finishing the activity and closing all the cursors
	@Override
	protected void onDestroy() {
		super.onDestroy();

		// cursor used at start for selecting notes of diff category
		if (mCategoryCursor != null)
			mCategoryCursor.close();

		// for getting the note id of last added note
		if (mLastNoteCursor != null)
			mLastNoteCursor.close();

		// cursor to add view in cascade view
		if (mCascadeViewCursor != null)
			mCascadeViewCursor.close();

		// For Recent note table operation
		if (mRecentNoteCursor != null)
			mRecentNoteCursor.close();

		// For Active Note Table Operation
		if (mActiveNoteCursor != null)
			mActiveNoteCursor.close();

	}//end of method onDestroy()

	/* This code is to return in home */
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		switch (KeyCode) {
		case KeyEvent.KEYCODE_BACK:
			if(isComingFromCabinet==true){
				Intent mFinishHomePage = new Intent(
						Intent.ACTION_DEFAULT);
				/*
				 * then create broadcast for
				 * sending to the
				 * Display_Manager
				 */
				mFinishHomePage.putExtra(
						"mak", 2);
				/*
				 * send the broadcast to
				 * home page
				 */
				sendBroadcast(mFinishHomePage);
				finish();
				
			}
			else{
				finish();
			}
			startActivity(new Intent(Organizer.this, EpubReader.class));
			
		}
		return true;

	}// end of method onKeyDown()
}
