
package com.sdg.EPUBReader;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.sdg.DisplayManager.Display_Manager;
import com.sdg.DisplayManager.Transitions;
import com.android.QuikE.R;
import com.sdg.organizer.NoteOrganizer;
import com.sdg.organizer.Organizer;

public class MyCabinet extends Activity {

	/*
	 * this variable will take the current activity and start another activity
	 * inside this activity
	 */
	protected static LocalActivityManager mLocalActivityManager;
	/* this array is used to store Cabinet data */
	static String mSaveData[];
	static int mSaveDataId[];
	static int mImageDataId[];
	// -----------------------------------------------------------------------
	/* this array will use to store image data from cabinet */
	static Bitmap[] mImageData;
	int mIndexImage = 0;
	int mIndexAll = 0;
	private static int mCheckBoxPosition;
	static int mImageCount = 0;
	// -----------------------------------------------------------------------
	/* this variable will store the number of data in the database */
	int mIndex = 0;

	// this flag will be usefull when cabinet
	// will refresh and we need to show checkbox
	static int mRefreshCheckBox = 0;

	// Note id received from the NoteOrganizer
	int mCurrentNoteId = 0;
	/*
	 * this variable is to used for showing the checkbox when organizer called
	 * from my cabinet
	 */
	boolean mCountCheck = true;

	// Text string that is to be dragged from cabinet to note
	String mTextToDrag;

	// this variable for the position of data in cabinet which is to be deleted
	public static int mDeletePosition;
	public static int mFlag;

	/* these views are used for showing NoteSaving,showing note name */
	GridView mNoteSavingPlace;
	ImageView mCabinetImageIcon;
	public TextView mCabinetTextFullScreen;
	public static TextView mSetTextForCabinet;
	AbsoluteLayout mDragTextLayout;
	RelativeLayout mMyCabinet;

	// Relative layout for closing the cabinet
	RelativeLayout mMyCabinetDown;

	// View on which drag action is to be done
	View mViewToDrag;
	// static int mCabinetPlace=0;
	String mCabinetPlace = "Temp";
	/* 0 is used to show cabinet is open,1 means Note open in place of Cabinet */

	/* this boolean is used to indicate if Cabinet is opened */
	/* if mIncabinet is true that means u r viewing cabinet */
	/* if mIncabinet is false then u r viewing note */
	public static boolean mIncabinet = true;

	/* these variables are used to database connection */
	private static final String DATABASE_NAME_BOOK = "BookDataBase.db";
	/* table name for cabinet */
	private static final String TABLE_NAME_CABINET = "CabinetInfoTable";
	/* id for dummy textview */
	protected static final int VIEW_TO_DRAG_ID = 3000;
	/* constant system time */
	protected final int SYSTEM_TIME = 1243;
	/* this variable is to indicate if cabinet is in note or in book */
	public final static int SWITCH_POSSIBLE = 2;
	/* this variable is to indicate if cabinet is in note or in book */
	public final static int SWITCH_NOT_POSSIBLE = 0;
	/* this variable is to indicate cabinet checkbox should refresh */
	protected final int REFRESH_CHECKBOK_POSSIBLE = 1;
	/*
	 * this variable will indicate after click on the check box if cabinet
	 * should be refreshed or not
	 */
	protected final int FOR_CABINET_REFRESH_POSSIBLE = 1;
	/* this variable is to indicate cabinet checkbox should not refresh */
	protected final int REFRESH_CHECKBOK_NOTPOSSIBLE = 0;
	/* macro for indicate cabinet will go to full screen */
	protected final int FULL_SCREEN = 700;
	/* macro to indicate cabinet will close */
	protected final int CLOSE_CABINET = 1200;
	/* this macro is to indicate if cabinet is open */
	public int CABINETISOPEN = 1;
	/* this macro is to indicate if cabinet is closed */
	public int CABINETISCLOSED = 0;
	/* this macro is to set if cabinet open from note or from book */
	public static boolean CABINET_OPEN_FROM_NOTE = false;
	// SQLiteDatabase mDataBase = null;
	/* object of database class */
	 DataBaseClass mDb;
	/* cursor object */
	Cursor mCursor;
	/* cursor object to check the checkbox */
	Cursor mIsSelectInCabinet;
	/* string to indicate full screen */
	String mFullScreen;
	/* string to store from where cabinet is called */
	String mTextCalledFrom;
	/* show the text as cabinet or note name */
	String mCabinetText;
	/* take the selected text */
	String mExtractedText;
	/* take the selected image path */
	String mExtractedImagePath;

	Bundle mBundleFromOrganizer;

	// ___________Added by shashi ______________________________

	private final int TEXT_TO_DRAG_WIDTH = 300;
	private final int TEXT_TO_DRAG_HEIGHT = 200;

	private final int TEXT_TO_DRAG_HORIZONTAL_OFFSET = 30;
	private final int TEXT_TO_DRAG_VERTICAL_OFFSET = 825;

	boolean mHoldShrink = false;

	// this class defines all the transitions
	Transitions transition;

	// byte array that stores the image as byte / blob
	byte[] mTempByteImage;

	// __________________________End by shashi _________________________

	/* this function is used when in place of cabinet selected note will open */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == 1 || resultCode == 0) {

			/* get the selected note ID */
			EpubReader.mFromOrganizerNoteIdInCabinet = Organizer.smForCabinetChangeID;
			/* create the object of the database class */
			mDb = new DataBaseClass(this);
			/* get the handler of the database class */
			mDb.mDatab = mDb.mDatah.getReadableDatabase();
			/* first set the text as note name */
			Cursor mTextCursor = mDb
					.returnTextForOrganizer(EpubReader.mFromOrganizerNoteIdInCabinet);
			/*
			 * for setting the name of the note in place of cabinet first check
			 * if that note has atleast one name
			 */
			if (mTextCursor.getCount() > 0) {

				mTextCursor.moveToFirst();
				/* get the note name */
				String mSetText = mTextCursor.getString(0);
				/* display the note name in place of CABINET */
				mSetTextForCabinet.setText(mSetText);
				/* set the cabinet name as note name */
				mCabinetText = mSetTextForCabinet.getText().toString();
				mCabinetImageIcon.setBackgroundResource(R.drawable.notesheadericon);
			}
			/* close the cursor */
			mTextCursor.close();
			// take data from Organizer database
			/*
			 * set the flag that Switcher once happen so now Switcher icon
			 * should work now
			 */
			mFlag = SWITCH_POSSIBLE;
			/* take the data from the organizer table */
			mCursor = mDb
					.returnCursorForOrganizer(EpubReader.mFromOrganizerNoteIdInCabinet);
			/* no of element in organizer table */
			mIndexAll = mCursor.getCount();
			/* move the cursor to the first position */
			mCursor.moveToFirst();
			
			// initialize the text saving array
			mSaveData = new String[mIndexAll];
			// initialize the image saving array
			mImageData = new Bitmap[mIndexAll];
			// store all data from CabinetDatabase to a local array
			for (int k = 0; k < mIndexAll; k++) {
				// first check if element is text or not
				if (mCursor.getString(1) != null) {
					// if element is text save it in array
					mSaveData[k] = mCursor.getString(1);

				}
				// else if element is image then do this
				if (mCursor.getBlob(2) != null) {
					// to prevent save null in arry save "Image" in Array to
					// check in future
					mSaveData[k] = "Image";
					// get the blob from the database
					byte[] mTempByteImage = mCursor.getBlob(2);
					// decode the byte and store the actula image
					mImageData[k] = BitmapFactory.decodeByteArray(
							mTempByteImage, 0, mTempByteImage.length);

				}
				// move the cursor to the next position
				mCursor.moveToNext();
			}
			mCursor.close();
			/* set the adapter for showing the Cabinet Data */
			mNoteSavingPlace.setAdapter(new AdapterNoteSavingPlace(this));

			//
			Organizer.smForCabinetChangeID = 0;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// object of class Transitions (previously Page_Transition)
		transition = new Transitions(MyCabinet.this);

		/* code to hide the status bar */
		/* get the default window hight */
		final Window win = getWindow();
		/* get the current window hight */
		final int screenHeight = win.getWindowManager().getDefaultDisplay()
				.getHeight();
		/* get the current window width */
		final int screenWidth = win.getWindowManager().getDefaultDisplay()
				.getWidth();

		if ((screenHeight > 1 && screenWidth > 1)
				|| (screenHeight == EpubReader.SCREEN_HIGHT && screenWidth == EpubReader.SCREEN_WIDTH)) {
			// No
			// Statusbar
			win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN); // No Titlebar
			/* no title bar, no status bar */
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			requestWindowFeature(Window.FEATURE_PROGRESS);
		}
		/* code end for hiding the status bar */

		/*
		 * create one new absolute layout where the copied text will be shown
		 * and give its parameter
		 */
		/* initialize the absolute layout */
		mDragTextLayout = new AbsoluteLayout(this);
		/* set the layout param */
		mDragTextLayout.setLayoutParams(new AbsoluteLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0, 0));

		/* get the bundle from different Activities */
		Bundle mBundle = this.getIntent().getExtras();
		/* first set the cabinet layout */
		setContentView(R.layout.mycabinet);
		mDb = new DataBaseClass(this);
		 mCabinetImageIcon=(ImageView)findViewById(R.id.mycabinetimage);
		/* initialize the textview */
		/* set the default text as 'Cabinet' */
		mSetTextForCabinet = (TextView) findViewById(R.id.cabinettext);
		mSetTextForCabinet.setText("Cabinet");
		mCabinetImageIcon.setBackgroundResource(R.drawable.cabinetheadericon);

		/*
		 * if any place of the Cabinet ,ImageView or the whole header,if clicked
		 * then Cabinet should work according to the gesture
		 */
		mMyCabinet = (RelativeLayout) findViewById(R.id.mycabinetlayout);
		/* initialize the imageview */
		// ImageView mCloseCabinet = (ImageView) findViewById(R.id.endCabinet);

		/* Relative Layout for closing the cabinet */
		mMyCabinetDown = (RelativeLayout) findViewById(R.id.placeholerMyCabinet);
		/* set the touch listener on the layout */
		mMyCabinetDown.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// if fullscreen mode gesture is done
				case MotionEvent.ACTION_UP: {
					
					if (event.getRawY() < FULL_SCREEN) {
						// if request fro fullscreen
						fullScreen();
					}
					// if close cabinet gesture is done
					if (event.getRawY() > CLOSE_CABINET) {
						// if cabinet is opened from Note
						if (NoteOrganizer.mLocalActivityManager != null) {
							// close the cabinet when in note
							NoteOrganizer.CloseCabinet();
						}
						// if cabinet is opened from display Manager
						else
							Display_Manager.CloseCabinet();
						// close the cabinet in book
					}
				}
					break;
				}
				return true;
			}
		});

		/* extract all the bundle information */
		if (mBundle != null) {
			/* get the copied text from book to cabinet */
			mExtractedText = mBundle.getString("ExtractedText");
			/* get the path of the image of book */
			mExtractedImagePath = mBundle.getString("CopiedImagePath");
			/* get the selected note id for switching */
			if (mBundle.getInt("selectedNoteId_key") != 0) {
				EpubReader.mFromOrganizerNoteIdInCabinet = mBundle
						.getInt("selectedNoteId_key");
			}
			/*
			 * get the intent if cabinet will be opened in ful screen or small
			 * screen
			 */
			mFullScreen = mBundle.getString("BookViewFullScreen");
			/* get the last note open in the organizer */
			mCurrentNoteId = mBundle.getInt("selectedNoteId_key");
			/*
			 * first check if cabinet is already opened and if it is not in full
			 * screen mode
			 */
			if (mFullScreen != null
					&& mFullScreen.equalsIgnoreCase("CabinetOpen")) {
				setContentView(R.layout.cabinetfullscreen);
				mCabinetTextFullScreen=(TextView)findViewById(R.id.cabinettext);
				if(EpubReader.mFromOrganizerNoteIdInCabinet==0){
				mCabinetImageIcon.setBackgroundResource(R.drawable.cabinetheadericon);
				mCabinetTextFullScreen.setText("Cabinet");
				/* close the full screen mode of cabinet */
				
				}
				else if(mCurrentNoteId>0){
			
				/* first set the text as note name */
				mSetTextForCabinet = (TextView) findViewById(R.id.cabinettext);
				/* create the object of the database and connect with database */
				mDb = new DataBaseClass(this);
				/* get the data from the organizer table */
				Cursor mTextCursor = mDb
						.returnTextForOrganizer(EpubReader.mFromOrganizerNoteIdInCabinet);
				/* if the selected note has its name then set it */
				if (mTextCursor.getCount() > 0) {
					/* move the cursor to the first position */
					mTextCursor.moveToFirst();
					/* get the note name */
					String mSetText = mTextCursor.getString(0);
					/* set the cabinet name as note name */
					mSetTextForCabinet.setText(mSetText);
					mCabinetTextFullScreen.setText(mSetText);
					mCabinetImageIcon.setBackgroundResource(R.drawable.notesheadericon);
				}
				/* close the cursor */
				mTextCursor.close();
				}
				RelativeLayout mCloseCabinetFullScreen = (RelativeLayout) findViewById(R.id.placeholerMyCabinet);

				/* set the touch listener on the layout */
				mCloseCabinetFullScreen
						.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								setResult(10);
								Display_Manager.mCabinetOpen = CABINETISCLOSED;
								MyCabinet.this.finish();

							}
						});
			}	

			if(mBundle.containsKey("lastNoteIdNO_key")==true)
			{
				mCurrentNoteId = mBundle.getInt("lastNoteIdNO_key");
			}
			
		}
		/* create the object of the database and connect with database */
		 mDb = new DataBaseClass(this);
		mDb.mDatab = mDb.mDatah.getReadableDatabase();
		/* if some text is selected from book and is intented to save in cabinet */
		if (mExtractedText != null)
			mDb.insertExtractedData(SYSTEM_TIME, mExtractedText, "Text");

		/* if seleted image is not null */
		if (mExtractedImagePath != null) {

			/* first create a output stream */
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			/* first decode the image from the file path */
			Bitmap mCopyImage = BitmapFactory.decodeFile(mExtractedImagePath);
			/* compress the image first */
			mCopyImage.compress(Bitmap.CompressFormat.PNG, 100, out);
			/* change it to the byte array */
			byte[] outputByte = out.toByteArray();
			/* insert into the database */
			mDb.insertExtractedImage(100, outputByte, "Image");
		}

		/* this code is added to show switcher */
		/* if cabinet is opended in Note Organizer not in display manager */
		if (mBundle.getInt("selectedNoteId_key") != 0) {
			Cursor mTextCursor = mDb
					.returnTextForOrganizer(EpubReader.mFromOrganizerNoteIdInCabinet);
			/* get the note bame */
			if (mTextCursor.getCount() > 0) {
				/* set the cursor to the first position */
				mTextCursor.moveToFirst();
				/* get the note name */
				String mSetText = mTextCursor.getString(0);
				/* set the cabinet text as note name */
				mSetTextForCabinet.setText(mSetText);
				mCabinetImageIcon.setBackgroundResource(R.drawable.notesheadericon);
			}
			/* close the cursor */
			mTextCursor.close();

		}
		/* get the text what is written on the left corner of the cabinet */
		mCabinetText = mSetTextForCabinet.getText().toString();

		/*
		 * check the text ,if it is a cabinet then take data from cabinet
		 * Database
		 */
		/* if it is in cabinet mode */
		if (mCabinetText.equalsIgnoreCase("Cabinet")) {
			/* take the data from cabinet table */
			mSetTextForCabinet = (TextView) findViewById(R.id.cabinettext);
			mSetTextForCabinet.setText("Cabinet");
			// select all images and Text from Cabinet
			Cursor mCursorToSelectAll = mDb.returnCursorForCabinet();
			// get the total no of element in cabinet
			mIndexAll = mCursorToSelectAll.getCount();
			// move the cursor to the first
			mCursorToSelectAll.moveToFirst();
			// initialize the text saving array
			mSaveData = new String[mIndexAll];
			// initialize the image saving array
			mImageData = new Bitmap[mIndexAll];
			// store all data from CabinetDatabase to a local array
			for (int k = 0; k < mIndexAll; k++) {
				// first check if element is text or not
				if (mCursorToSelectAll.getString(1) != null) {
					// if element is text save it in array
					mSaveData[k] = mCursorToSelectAll.getString(1);

				}
				// else if element is image then do this
				if (mCursorToSelectAll.getBlob(2) != null) {
					// to prevent save null in arry save "Image" in Array to
					// check in future
					mSaveData[k] = "Image";
					// get the blob from the database
					byte[] mTempByteImage = mCursorToSelectAll.getBlob(2);
					// decode the byte and store the actula image
					mImageData[k] = BitmapFactory.decodeByteArray(
							mTempByteImage, 0, mTempByteImage.length);

				}
				// move the cursor to the next position
				mCursorToSelectAll.moveToNext();
			}

		}
		/* check the text ,if it is a note then take data from note Database */
		else {
			/* set the now switch is possible */
			mFlag = SWITCH_POSSIBLE;
			/* get the data from the organizer */
			mCursor = mDb
					.returnCursorForOrganizer(EpubReader.mFromOrganizerNoteIdInCabinet);
			/* get the no of element in the organizer table */
			mIndexAll = mCursor.getCount();
			/* move the cursor to the first position */
			mCursor.moveToFirst();
			/* initialize the array */
			mSaveData = new String[mIndexAll];
			mImageData=new Bitmap[mIndexAll];
			// store all data from CabinetDatabase to a local array
			for (int k = 0; k < mIndexAll; k++) {
				// first check if element is text or not
				if (mCursor.getString(1) != null) {
					// if element is text save it in array
					mSaveData[k] = mCursor.getString(1);

				}
				// else if element is image then do this
				if (mCursor.getBlob(2) != null) {
					// to prevent save null in arry save "Image" in Array to
					// check in future
					mSaveData[k] = "Image";
					// get the blob from the database
					byte[] mTempByteImage = mCursor.getBlob(2);
					// decode the byte and store the actula image
					mImageData[k] = BitmapFactory.decodeByteArray(
							mTempByteImage, 0, mTempByteImage.length);

				}
				mCursor.moveToNext();
			}
			/* close the cursor */
			mCursor.close();

		}
		/* this grid will show the cabinet save as ,switcher etc icons */
		GridView mMyCabinetPlaceHolderG = (GridView) findViewById(R.id.gplageholederMyCabinet);
		/* set a adpter on this grid view */
		mMyCabinetPlaceHolderG
				.setAdapter(new ImageAdapterPlaceHoldForMyCabinet(this));

		final AnimationListener al = new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				mNoteSavingPlace.setAdapter(new AdapterNoteSavingPlace(
						MyCabinet.this));
			}

			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

			}
		};

		/* set a on click listener on this grid view */
		mMyCabinetPlaceHolderG
				.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(
							AdapterView<?> ImageAdapterPlaceHoldForMyCabinet,
							View v, int position, long id) {

						/* click for save as option */
						if (position == 2) {
							/*
							 * first check if cabinet is opened from Note, if it
							 * opened from note no option should work
							 */
							if (CABINET_OPEN_FROM_NOTE == false) {
									/* create a intent */
									Intent mSaveAs = new Intent(MyCabinet.this,
											SaveAsNote.class);
									/* start the save as activity */
									startActivity(mSaveAs);
								
							}
						}
						/* to share a note */
						if (position == 3) {
							/*
							 * first check if cabinet is opened from Note, if it
							 * opened from note no option should work
							 */
							/* create a intent */
							Intent mSharing = new Intent(MyCabinet.this,
									Sharing.class);
							/* start the activity */
							startActivity(mSharing);

						}

						/* if click on new note option from my cabinet */
						if (position == 4) {
							/*
							 * first check if cabinet is opened from Note, if it
							 * opened from note no option should work
							 */
							if (CABINET_OPEN_FROM_NOTE == false) {
								Intent mStartOrganizer = new Intent(
										MyCabinet.this, Organizer.class);
								/*
								 * this bundle is to know the organizer that
								 * organizer is called from cabinet
								 */
								Bundle mBundle = new Bundle();
								mBundle.putString("fromCabinetC_key",
										"FromCabinet");
								mStartOrganizer.putExtras(mBundle);

								if (mIncabinet == true)
									mIncabinet = false;
								/* start the organizer activity */
								/*
								 * set it false if once organizer called from
								 * MyCabinet
								 */
								mCountCheck = false;
								startActivityForResult(mStartOrganizer, 1);
							}

						}
						/* if click on delete note */
						if (position == 5) {
							/*
//							 * delete all data from cabinet info table where is
//							 * checked =1
//							 */
//							mDb.deleteFromCabinetAfterSave(DataBaseClass.CABINET_CHECKED);
//							/* refresh the content */
//							mNoteSavingPlace
//									.setAdapter(new AdapterNoteSavingPlace(
//											MyCabinet.this));
						}

						/* if click for switcher */
						if (position == 6) {

							//-------------------------------------------------------
							if(mFullScreen!=null && mFullScreen.equalsIgnoreCase("CabinetOpen")){
								mCabinetSwitching();
							}
							
							final int mAnimationFirst = 1;
							final int mAnimationSecond = 2;
														
							final Animation mAnimIn = transition.mNoteCabinetSwitchAnim(mAnimationSecond);
													
							final Animation mAnimOut = transition.mNoteCabinetSwitchAnim(mAnimationFirst);
													
							if (mFlag == SWITCH_POSSIBLE && CABINET_OPEN_FROM_NOTE == false){
							mMyCabinet.startAnimation(mAnimOut);
							mMyCabinet.invalidate();
							
							}
							 
							 
							mAnimOut
									.setAnimationListener(new AnimationListener() {

										public void onAnimationEnd(
												Animation animation) {
											// TODO Auto-generated method stub

											mMyCabinet.setAnimation(mAnimIn);

											mCabinetSwitching();

										}

										public void onAnimationRepeat(
												Animation animation) {
											// TODO Auto-generated method stub

										}

										public void onAnimationStart(
												Animation animation) {
											// TODO Auto-generated method stub

										}
									});
							
							
							

						}//////
					}
					
					
					

				});
		
	
		
		/* this portion for SavingNote Portion */
		/* initialize the frid view */
		mNoteSavingPlace = (GridView) findViewById(R.id.mycabinetSavingPlace);
		/* set a adapter fro this grid */
		mNoteSavingPlace.setAdapter(new AdapterNoteSavingPlace(this));
		/* set a onclick listener on this grid */
		mNoteSavingPlace.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> AdapterNoteSavingPlace,
					View v, final int position, long id) {
				if (CABINET_OPEN_FROM_NOTE == true) {

					// The text is selected by grid view position number

					// mDeletePosition stores the data number to be deleted
					mDeletePosition = position + 1;
					/* remove the previous view */
					mDragTextLayout.removeAllViews();
					/* remove the dummy textview also */
					mMyCabinet.removeView(mDragTextLayout);

					/* Selecting data from Database */
					final Cursor mGetText = mDb
							.selectTextDataFromCabinet(position);
					/* move the cursor to the first */
					mGetText.moveToFirst();
					mTextToDrag = mGetText.getString(0);

					if (NoteOrganizer.smCabinetOpen == true) {

						mMyCabinet.addView(mDragTextLayout);

					}
					// If the data from the cabinet is text..
					// the image data will be null ...
					// checking whether the data we got is image or string
					if (mGetText.getBlob(1) == null) {
						mTextToDrag = mGetText.getString(0);
						// Creating new TextView to be dragged to the cabinet
						// and setting the properties of text view, size of text
						// in text view
						// and setting the background of text view
						mViewToDrag = new TextView(MyCabinet.this);
						((TextView) mViewToDrag).setText(mTextToDrag);
						mViewToDrag.setId(VIEW_TO_DRAG_ID);
						((TextView) mViewToDrag).setTextSize(15);
						((TextView) mViewToDrag).setTextColor(Color.BLACK);
						mViewToDrag
								.setBackgroundResource(R.drawable.backgroundforselectedtext);
					} else if (mGetText.getString(0) == null) {

						// storing image data in the byte array from the blob
						// received from database
						mTempByteImage = mGetText.getBlob(1);

						// creating Imageview to be dragged to the cabinet
						// and setting the properties of imageview
						mViewToDrag = new ImageView(MyCabinet.this);
						Bitmap bmp = BitmapFactory.decodeByteArray(
								mTempByteImage, 0, mTempByteImage.length);
						((ImageView) mViewToDrag).setImageBitmap(bmp);
						mViewToDrag.setId(VIEW_TO_DRAG_ID);
					}

					// setting the layout parameters of text view to be dragged
					mViewToDrag.setLayoutParams(new AbsoluteLayout.LayoutParams(
									TEXT_TO_DRAG_WIDTH, TEXT_TO_DRAG_HEIGHT, 
									v.getLeft()+ TEXT_TO_DRAG_HORIZONTAL_OFFSET, v.getTop()+ TEXT_TO_DRAG_VERTICAL_OFFSET));

					// adding text view to the back layout
					mDragTextLayout.addView(mViewToDrag);

					// CReating on touch listener for Text View
					mViewToDrag.setOnTouchListener(new OnTouchListener() {

						public boolean onTouch(View v, MotionEvent event) {

							// Fetching the layout params of view
							AbsoluteLayout.LayoutParams par = (AbsoluteLayout.LayoutParams) v
									.getLayoutParams();

							switch (v.getId()) {
							case VIEW_TO_DRAG_ID:
								switch (event.getAction()) {
								case MotionEvent.ACTION_MOVE: {

									// if we wanr to shrink the text while
									// dragging
									if (mHoldShrink == false) {
										par.y = (int) event.getRawY() - v.getHeight();
										par.x = (int) event.getRawX() - v.getWidth() / 2;
										mHoldShrink = true;
									}

									// getting the x and y in temp variables
									int yTemp = (int) event.getRawY() - v.getHeight();
									int xTemp = (int) event.getRawX() - v.getWidth() / 2;

									// setting layout boundaries and parameters
									// for the text on
									// action down
									// reducing length and height on each move
									// cycle
									if (xTemp != par.x && yTemp != par.y) {
										if (par.width > 100 && par.height > 60) {
											par.width = par.width - (par.width * 3 / 100);
											par.height = par.height	- (par.height * 3 / 100);

											par.x = par.x - (par.width * 3 / 100);
											par.y = par.y - (par.height * 3 / 100);
											/*
											if (mGetText.getString(0) != null) {
												float mTextSize = ((TextView) mViewToDrag).getTextSize()
														+ ((TextView) mViewToDrag).getTextSize()* 2 / 100;

												((TextView) mViewToDrag)
														.setTextSize(mTextSize);
											}*/
											
										}
										// setting the layout params of text
										// view
										v.setLayoutParams(par);

										// fetching x and y for next move cycle
										par.y = (int) event.getRawY()- v.getHeight();
										par.x = (int) event.getRawX()- v.getWidth() / 2;

									}

									// if we move the text to less tha 400 pixel
									// the text view data
									// is to be sent to note organizer for
									// processing(storing and display)
									if (event.getRawY() < 500) {
										Intent mCallNoteOrgIntent = new Intent(
												MyCabinet.this,
												NoteOrganizer.class);
										Bundle mCallNoteOrgBundle = new Bundle();
										mCallNoteOrgBundle.putInt(
												"lastNoteIdC_key",
												mCurrentNoteId);
										// if data received from the
										// CabinetInfoTable is string
										if (mGetText.getString(0) != null) {
											mCallNoteOrgBundle.putString(
													"dragTextStringC_key",
													mTextToDrag);

											// if the data received from
											// CabinetInfoTable is image
										} else if (mGetText.getBlob(1) != null) {
											mCallNoteOrgBundle.putString(
													"dragImageC_key",
													"ImageToDrag");
										}
										mCallNoteOrgIntent
												.putExtras(mCallNoteOrgBundle);
										MyCabinet.this.finish();
										startActivity(mCallNoteOrgIntent);

									}

									v.setLayoutParams(par);

									break;

								}// end of case MotionEvent.ACTION_MOVE

								case MotionEvent.ACTION_DOWN: {

									// setting the x and y params for the view
									// to be dragged
									par.y = (int) event.getRawY()- v.getHeight();
									par.x = (int) event.getRawX()- v.getWidth() / 2;

									v.setLayoutParams(par);
									break;
								}// end of case MOtionEvent.ACTION_DOWN

								}// end of switch event.getAction
							}// end of switch

							return true;

						}

					});

				}
			}
		}); // this image view is to close the cabinet activity

	}
	
	
	public void mCabinetSwitching() {
		// TODO Auto-generated method stub
		/*
		 * first check if cabinet is opened from Note, if it
		 * opened from note no option should work
		 */
		if (CABINET_OPEN_FROM_NOTE == false) {
			/* if in Cabinet */
			if (mFlag == SWITCH_POSSIBLE) {
				/* if in cabinet switch to note */
				if (mIncabinet == false) {
					mIncabinet = true;
				}/* if in note switch to cabinet */
				else if (mIncabinet == true) {
					mIncabinet = false;
				}
			}

			if (mFlag == SWITCH_POSSIBLE
					&& mIncabinet == true) {
				/*
				 * this flag will be helpful to show the
				 * check box which was previously selected
				 */
				mRefreshCheckBox = REFRESH_CHECKBOK_POSSIBLE;

				/* set the text as cabinet */
				mSetTextForCabinet.setText("Cabinet");
				/*set the image for cabinet*/
				mCabinetImageIcon.setBackgroundResource(R.drawable.cabinetheadericon);
				/* get the data from Cabinet table */
				Cursor mCursorForCabinetSwitch = mDb
						.returnCursorForCabinet();
				/* get the no of element */
				mIndexAll = mCursorForCabinetSwitch
						.getCount();   
				/* move the cursor to the first */
				mCursorForCabinetSwitch.moveToFirst();
				/* next time note will open */

				mSaveData = new String[mIndexAll];
				mImageData = new Bitmap[mIndexAll];
				/* get the data from cabinet table */
				// store all data from CabinetDatabase to a
				// local array
				for (int k = 0; k < mIndexAll; k++) {
					// first check if element is text or not
					if (mCursorForCabinetSwitch
							.getString(1) != null) {
						// if element is text save it in
						// array
						mSaveData[k] = mCursorForCabinetSwitch
								.getString(1);

					}
					// else if element is image then do this
					if (mCursorForCabinetSwitch.getBlob(2) != null) {
						// to prevent save null in arry save
						// "Image" in Array to check in
						// future
						mSaveData[k] = "Image";
						// get the blob from the database
						byte[] mTempByteImage = mCursorForCabinetSwitch
								.getBlob(2);
						// decode the byte and store the
						// actula image
						mImageData[k] = BitmapFactory
								.decodeByteArray(
										mTempByteImage,
										0,
										mTempByteImage.length);

					}
					// move the cursor to the next position
					mCursorForCabinetSwitch.moveToNext();
				}
				/* close the cursor */
				mCursorForCabinetSwitch.close();
				/*set the text as Cabinet*/
				if(mCabinetTextFullScreen!=null){
					mCabinetTextFullScreen.setText("Cabinet");
				}
				/* refresh the content */
				mNoteSavingPlace
						.setAdapter(new AdapterNoteSavingPlace(
								MyCabinet.this));

			}
			/* this case will show data from organizer */
			else if (mFlag == SWITCH_POSSIBLE
					&& mIncabinet == false) {
				mRefreshCheckBox = REFRESH_CHECKBOK_NOTPOSSIBLE;
				/*
				 * take data from organizer with the
				 * selected note id
				 */
				Cursor mTextCursor = mDb
						.returnTextForOrganizer(EpubReader.mFromOrganizerNoteIdInCabinet);
				/* if note is present */
				if (mTextCursor.getCount() > 0) {
					/* get the note name */
					mTextCursor.moveToFirst();
					String mSetText = mTextCursor
							.getString(0);
					/* set the cabinet text as the note name */
					mSetTextForCabinet.setText(mSetText);
					/*set the text as Cabinet*/
					if(mCabinetTextFullScreen!=null){
						mCabinetTextFullScreen.setText(mSetText);
					}
					mCabinetImageIcon.setBackgroundResource(R.drawable.notesheadericon);
				}
				/* close the cursor */
				mTextCursor.close();
				/* next time cabinet will open */

				/* take data from Organizer database */
				mFlag = SWITCH_POSSIBLE;
				/* take data from organizer */
				mCursor = mDb
						.returnCursorForOrganizer(EpubReader.mFromOrganizerNoteIdInCabinet);
				/* get the element count */
				mIndexAll = mCursor.getCount();
				/* move the cursor to the first place */
				mCursor.moveToFirst();
				/* initialize the array */
				mSaveData = new String[mIndexAll];
				mImageData = new Bitmap[mIndexAll];
				/* get the element of organizer in array */
				for (int k = 0; k < mIndexAll; k++) {
					// first check if element is text or not
					if (mCursor.getString(1) != null) {
						// if element is text save it in
						// array
						mSaveData[k] = mCursor.getString(1);

					}
					// else if element is image then do this
					if (mCursor.getBlob(2) != null) {
						// to prevent save null in arry save
						// "Image" in Array to check in
						// future
						mSaveData[k] = "Image";
						// get the blob from the database
						byte[] mTempByteImage = mCursor
								.getBlob(2);
						// decode the byte and store the
						// actula image
						mImageData[k] = BitmapFactory
								.decodeByteArray(
										mTempByteImage,
										0,
										mTempByteImage.length);

					}
					// move the cursor to the next position
					mCursor.moveToNext();
				}
				/* close the cursor */
				mCursor.close();
				/* refresh the cabinet data */
				mNoteSavingPlace
						.setAdapter(new AdapterNoteSavingPlace(
								MyCabinet.this));
			}
		}
	}

	

	/*
	 * this adapter will show the icons like save as ,switch icons in
	 * placeholder
	 */
	public class ImageAdapterPlaceHoldForMyCabinet extends BaseAdapter {
		public ImageAdapterPlaceHoldForMyCabinet(Context c) {
			mContext = c;
		}

		public int getCount() {

			return mThumbIds.length;

		}

		public Object getItem(int position) {

			return position;
		}

		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext);

			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(40, 40));
				imageView.setAdjustViewBounds(false);

				imageView.setScaleType(ImageView.ScaleType.FIT_XY);

			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(mThumbIds[position]);

			return imageView;
		}

		private Context mContext;
		/* this array will store the below mentioned icons */
		private Integer[] mThumbIds = { R.drawable.cabinetsearchicon,
				R.drawable.cabinetsorticon, R.drawable.cabinetentersaveicon,
				R.drawable.cabinetsendtoicon, R.drawable.cabinetorganizericon,
				R.drawable.cabinetdeleteicon, R.drawable.cabinetswitchericon

		};
	}

	/* this class is for saving the extracted note */
	private class AdapterNoteSavingPlace extends BaseAdapter {
		private LayoutInflater mInflater;
		private Bitmap mIcon[];
		private Bitmap mIconFull[];
		ViewHolder holder;

		final DataBaseClass mDb = new DataBaseClass(MyCabinet.this);

		public AdapterNoteSavingPlace(Context context) {
			/* this code is to added which item is checked after refresh */

			int mIsCheckedInCabinet = 0;

			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			Bitmap[] mIcon1 = new Bitmap[mIndexAll];
			Bitmap[] mFullscreenIcon = new Bitmap[mIndexAll];
			// check box will not work when in place of cabinet note came

			if (mRefreshCheckBox == REFRESH_CHECKBOK_POSSIBLE
					|| mRefreshCheckBox == FOR_CABINET_REFRESH_POSSIBLE) {
				for (int j = 0; j < mIndexAll; j++) {
					//if (EpubReader.mFromOrganizerNoteIdInCabinet == 0) {
					if(mSetTextForCabinet.getText()
								.toString().equalsIgnoreCase("Cabinet")){
						mIsSelectInCabinet = mDb.isCheckedInCabinet(j);
						mIsSelectInCabinet.moveToFirst();

						mIsCheckedInCabinet = mIsSelectInCabinet.getInt(0);
						// first check from database that data is selected or
						// not
						if (mIsCheckedInCabinet == 1) {
							mIcon1[j] = BitmapFactory.decodeResource(context
									.getResources(),
									R.drawable.checkboxselected);

						}
						if (mIsCheckedInCabinet == CABINETISCLOSED) {
							mIcon1[j] = BitmapFactory.decodeResource(context
									.getResources(), R.drawable.checkbox);

						}
					}

				}

			}

			mIcon = mIcon1;
			mIconFull = mFullscreenIcon;

		}

		public int getCount() {
			return mIndexAll;

		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			final int mPosition = position;

			if (convertView == null) {
				holder = new ViewHolder();

				if (!mSaveData[position].equalsIgnoreCase("Image")) {
					convertView = mInflater.inflate(R.layout.checkboxtextbox,
							null);
					holder.FullScreenIcon = (ImageView) convertView
							.findViewById(R.id.CabinetFullScreenIcon);
					holder.text = (TextView) convertView
							.findViewById(R.id.text);
					holder.icon = (ImageView) convertView
							.findViewById(R.id.iconforcheckbox);
					holder.text.setPadding(10, 15, 0, 0);
					holder.text.setTextColor(Color.BLACK);
					holder.FullScreenIcon
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {
									Cursor mFullTextCursor = null;
									String mCabinetTextInFull;
									// if sombody click to see note in full mode
									if (mIncabinet == false) {
										mCabinetTextInFull = mDb
												.getFullTextForNote(
														mPosition,
														EpubReader.mFromOrganizerNoteIdInCabinet);
									}

									// / if somebody want to see cabinet data in
									// full
									// * mode

									else {
										mFullTextCursor = mDb
												.getFullText(mPosition);
										mFullTextCursor.moveToFirst();
										mCabinetTextInFull = mFullTextCursor
												.getString(0);
									}
									Bundle mBundle = new Bundle();
									mBundle.putString("FullText",
											mCabinetTextInFull);
									Intent mForFullText = new Intent(
											MyCabinet.this,
											MyCabinetFullText.class);
									mForFullText.putExtras(mBundle);
									startActivity(mForFullText);
								}
							});

				} else {

					convertView = mInflater.inflate(R.layout.checkboximagebox,
							null);
					holder.Image = (ImageView) convertView
							.findViewById(R.id.CabinetImageData);
					holder.icon = (ImageView) convertView
							.findViewById(R.id.iconforcheckbox);

				}
				holder.icon = (ImageView) convertView
						.findViewById(R.id.iconforcheckbox);
				holder.FullScreenIcon = (ImageView) convertView
						.findViewById(R.id.CabinetFullScreenIcon);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (!mSaveData[position].equalsIgnoreCase("Image")) {
				if (holder == null) {
					holder = new ViewHolder();
				}
				convertView = mInflater.inflate(R.layout.checkboxtextbox, null);
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.iconforcheckbox);
				holder.FullScreenIcon = (ImageView) convertView
						.findViewById(R.id.CabinetFullScreenIcon);
				holder.text.setPadding(10, 15, 0, 0);
				holder.text.setTextColor(Color.BLACK);
				holder.text.setText(mSaveData[position]);
				holder.text.setTextSize(15);
				holder.text.setBackgroundColor(Color.WHITE);
				holder.FullScreenIcon
						.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								// TODO Auto-generated method stub

								Cursor mFullTextCursor = null;
								String mCabinetTextInFull;
								// if sombody click to see note in full mode
								if (mIncabinet == false) {
									mCabinetTextInFull = mDb
											.getFullTextForNote(
													mPosition,
													EpubReader.mFromOrganizerNoteIdInCabinet);
								}

								// / if somebody want to see cabinet data in
								// full
								// * mode

								else {
									mFullTextCursor = mDb
											.getFullText(mPosition);
									mFullTextCursor.moveToFirst();
									mCabinetTextInFull = mFullTextCursor
											.getString(0);
								}
								Bundle mBundle = new Bundle();
								mBundle.putString("FullText",
										mCabinetTextInFull);
								Intent mForFullText = new Intent(
										MyCabinet.this, MyCabinetFullText.class);
								mForFullText.putExtras(mBundle);
								startActivity(mForFullText);
							}

						});
				holder.icon.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						String mStopClick = mSetTextForCabinet.getText()
								.toString();

						if (mStopClick.equalsIgnoreCase("Cabinet")) {

							Cursor mCheckCursor = mDb
									.isCheckedInCabinet(mPosition);
							mCheckCursor.moveToFirst();
							int misChecked = mCheckCursor.getInt(0);
							if (misChecked == CABINETISCLOSED) {
								mDb.updateIsCheckedInCabinetToSet(mPosition);
								mRefreshCheckBox = FOR_CABINET_REFRESH_POSSIBLE;
								mNoteSavingPlace
										.setAdapter(new AdapterNoteSavingPlace(
												MyCabinet.this));

							}
							if (misChecked == CABINETISOPEN) {
								mDb.updateIsCheckedInCabinetToUnSet(mPosition);
								mRefreshCheckBox = FOR_CABINET_REFRESH_POSSIBLE;
								mNoteSavingPlace
										.setAdapter(new AdapterNoteSavingPlace(
												MyCabinet.this));

							}
							mCheckCursor.close();
						}
						

					}
				});
				holder.FullScreenIcon
						.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								// TODO Auto-generated method stub

								Cursor mFullTextCursor = null;
								String mCabinetTextInFull;
								// if sombody click to see note in full mode
								if (mIncabinet == false) {
									mCabinetTextInFull = mDb
											.getFullTextForNote(
													mPosition,
													EpubReader.mFromOrganizerNoteIdInCabinet);
								}

								// / if somebody want to see cabinet data in
								// full
								// * mode

								else {
									mFullTextCursor = mDb
											.getFullText(mPosition);
									mFullTextCursor.moveToFirst();
									mCabinetTextInFull = mFullTextCursor
											.getString(0);
								}
								Bundle mBundle = new Bundle();
								mBundle.putString("FullText",
										mCabinetTextInFull);
								Intent mForFullText = new Intent(
										MyCabinet.this, MyCabinetFullText.class);
								mForFullText.putExtras(mBundle);
								startActivity(mForFullText);
							}

						});
			} else {

				if (holder == null) {
					holder = new ViewHolder();
				}
				convertView = mInflater
						.inflate(R.layout.checkboximagebox, null);
				holder.Image = (ImageView) convertView
						.findViewById(R.id.CabinetImageData);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.iconforcheckbox);

				holder.Image.setImageBitmap(mImageData[position]);
				holder.icon.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						String mStopClick = mSetTextForCabinet.getText()
								.toString();
						if (mStopClick.equalsIgnoreCase("Cabinet")) {

							Cursor mCheckCursor = mDb
									.isCheckedInCabinet(mPosition);
							mCheckCursor.moveToFirst();
							int misChecked = mCheckCursor.getInt(0);
							if (misChecked == CABINETISCLOSED) {
								mDb.updateIsCheckedInCabinetToSet(mPosition);
								mRefreshCheckBox = FOR_CABINET_REFRESH_POSSIBLE;
								mNoteSavingPlace
										.setAdapter(new AdapterNoteSavingPlace(
												MyCabinet.this));

							}
							if (misChecked == CABINETISOPEN) {
								mDb.updateIsCheckedInCabinetToUnSet(mPosition);
								mRefreshCheckBox = FOR_CABINET_REFRESH_POSSIBLE;
								mNoteSavingPlace
										.setAdapter(new AdapterNoteSavingPlace(
												MyCabinet.this));

							}
							mCheckCursor.close();
						}
						

					}
				});
				

			}
			if (mRefreshCheckBox == 1) {
				holder.icon.setImageBitmap(mIcon[position]);
			}
			return convertView;
		}

		class ViewHolder {
			TextView text;
			ImageView Image;
			ImageView icon;
			ImageView FullScreenIcon;
		}

		OnClickListener FullScreenTextListener = new OnClickListener() {

			public void onClick(View v) {
				Cursor mFullTextCursor = null;
				String mCabinetTextInFull;
				// if sombody click to see note in full mode
				if (mIncabinet == false) {
					mCabinetTextInFull = mDb.getFullTextForNote(
							mCheckBoxPosition,
							EpubReader.mFromOrganizerNoteIdInCabinet);
				}

				// / if somebody want to see cabinet data in full
				// * mode

				else {
					mFullTextCursor = mDb.getFullText(mCheckBoxPosition);
					mFullTextCursor.moveToFirst();
					mCabinetTextInFull = mFullTextCursor.getString(0);
				}
				Bundle mBundle = new Bundle();
				mBundle.putString("FullText", mCabinetTextInFull);
				Intent mForFullText = new Intent(MyCabinet.this,
						MyCabinetFullText.class);
				mForFullText.putExtras(mBundle);
				startActivity(mForFullText);
			}

		};
	}  

	/* this function is to show the cabinet in full screen mode */
	public void fullScreen() {
		Intent mIntentFullScreen = new Intent(MyCabinet.this, MyCabinet.class);
		Bundle mBundleFullScreen = new Bundle();
		mIntentFullScreen.putExtra("BookViewFullScreen", "CabinetOpen");
		mCabinetText = mSetTextForCabinet.getText().toString();
		if (mCabinetText.equalsIgnoreCase("Cabinet")) {
			mIntentFullScreen.putExtra("selectedNoteId_key", 0);
		} else
			mIntentFullScreen.putExtra("selectedNoteId_key",
					EpubReader.mFromOrganizerNoteIdInCabinet);
		/*remove the cabinet small*/
		Display_Manager.removeCabinetafterSave();
		startActivityForResult(mIntentFullScreen,10);
	}

	/* This code is to return in home using H key from KeyBoard */
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		switch (KeyCode) {
		case KeyEvent.KEYCODE_BACK:
			/* finish the current activity */
			finish();
			/* start the home page */
			startActivity(new Intent(MyCabinet.this, EpubReader.class));
			return true;
		}
		return false;

	}

}
