
package com.sdg.EPUBReader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.sdg.DisplayManager.Display_Manager;
import com.android.QuikE.R;
import com.sdg.organizer.NoteOrganizer;

/* This Class is used to show Active window on the top of any Activity*/
public class ActiveWindow extends Activity {

	/* create a new intent for BroadCast */
	Intent mIntentForBroadcast = new Intent(Intent.ACTION_DEFAULT);

	Intent mIntentForBroadcastToNote = new Intent(Intent.ACTION_DEFAULT);
	/* create a object of Broadcast class */
	BroadcastReceiver pbr = new BroadcastReceiver() {
		/* on receive of a broadcast message this method will invoke */
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getIntExtra("FinishActiveWindow", DEFAULT_VALUE_ONE) == DEFAULT_VALUE_ONE) {
				ActiveWindow.this.finish();
				
			}
		}
	};
	/* This Variable will Store the no of element in Database */
	static int mIndex;
	/* This variable is use to know from which activity Active Window is called */
	int mFromHome;

	/* this array will store the last opened note and book name */
	static String mBookData[];

	/* this Cursor is use to connect with Database */
	Cursor mCursor;
	/* This String is to indicate if it is a book ao a note */
	String mType;
	/*
	 * These two String will be the Table name in Database where the info will
	 * store
	 */
	
	/* This Grid will show one image ,then book or note name and one delete icon */
	GridView mActiveWindowIcon;
	GridView mActiveWindow;
	Bundle mBundle;
	DataBaseClass mDb;

	/*this string is used to indicate the default value 1*/
	private final int DEFAULT_VALUE_ONE=1;
	/*this string is used to indicate the default value 0*/
	private final int DEFAULT_VALUE_ZERO=0;
	/* These string are used to check from where Active window is Called */
	private final int TYPE_BOOK_OR_NOTE = 0;
	/* this is to ckeck if intent come from home */
	private final int FROM_HOME = 1;
	/* this is to check if intent come from book */
	private final int FROM_BOOK = 2;
	/* this is to check if intent come from note */
	private final int FROM_NOTE = 3;
	/* check if intent come from any other activity */
	private final int FROM_ANY_ACTIVITY = 4;
	/* this is to check if intent come from Organizer home page */
	private final int FROM_ORGANIZER_HOME = 5;
	/* max 4type of element will show in Active Window List */
	int MAX_ELEMENT = 4;
	/* Icon of book is Icon 0 */
	int ICON_BOOK = 0;
	/* Icon of Note is Icon 1 */
	int ICON_NOTE = 1;
	/* Icon of Note is Icon 1 */
	int ICON_BROWSER = 2;
	/* Icon of Note is Icon 1 */
	int ICON_MAGAZINE = 3;
	/* the below variables is the coloum no of Active Window Table */
	int NAME_BOOK_OR_NOTE = 0;
	/* coloum for book */
	int BOOK_PATH = 1;
	/* coloum for note id */
	int NOTE_ID = 2;
	/*
	 * this static variable is used to open the home page when book closed from
	 * bookview page
	 */
	public static boolean mOpenHomePage = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* set the active window layout */
		setContentView(R.layout.activewindowlayout);

		/* create a object of intent flter class */
		IntentFilter piFilter = new IntentFilter(Intent.ACTION_MAIN);
		/* register the receiver */
		registerReceiver(pbr, piFilter);

		/* Get the intent from where Active Window is called */
		Intent mFromHomeIntent = getIntent();
		/* get the intent */
		mFromHome = mFromHomeIntent.getIntExtra("FromHome", DEFAULT_VALUE_ZERO);
		/* create a object of the database class */
		mDb = new DataBaseClass(this);
		/* get the handler */
		mDb.mDatab = mDb.mDatah.getReadableDatabase();

		/* First get all the data from Active Window Table */
		try {
			/* take a cursor */
			mCursor = mDb.selectAllFromActiveWindow();
			/* set the cursor position to the first */
			mCursor.moveToFirst();
			/* get the no of active element */
			mIndex = mCursor.getCount();
			/* bookdata will store name of book and note for activewindow */
			mBookData = new String[mIndex];

			/* this loop wil store the active element */
			for (int i = 0; i < mIndex; i++) {

				mBookData[i] = mCursor.getString(NAME_BOOK_OR_NOTE);

				/* move the cursor to the next */
				mCursor.moveToNext();
			}
			/* close the cursor */
			mCursor.close();
		} catch (SQLException e) {
			e.getStackTrace();
		}
		/* initialize the grid view to show the active window */
		mActiveWindow = (GridView) findViewById(R.id.activewindowlist);

		/* This Adapter will show the Resent Book ann Note Nsme in the GridView */
		mActiveWindow.setAdapter(new AdapterForActiveWindow(this));
		/* set teh onclick listener on the grid view */
		mActiveWindow.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> AdapterForActiveWindow,
					View v, int position, long id) {

				/* Quary for if clicked item is a book or a note */
				Cursor mCallActivity = mDb
						.selectTypeActiveWindow(mBookData[position]);
				mCallActivity.moveToFirst();

				/* Get the type either book or note */
				mType = mCallActivity.getString(TYPE_BOOK_OR_NOTE);
				/*
				 * if from home page Request to open active window and try to
				 * open a book
				 */
				if (mFromHome == FROM_HOME
						&& (mType.equalsIgnoreCase("Book") || mType
								.equalsIgnoreCase("Magazine"))) {

					/*
					 * send the sd card path of the selected book to display
					 * manager
					 */
					Intent mCallBookDiaplay = new Intent(ActiveWindow.this,
							Display_Manager.class);
					/*
					 * this intent will use to finish the home page which is in
					 * the background
					 */
					Intent mFinishHomePage = new Intent(Intent.ACTION_DEFAULT);
					/* create a bundle */
					Bundle mBundle = new Bundle();
					/* set the sd card path of the book */
					mBundle.putString("BookPath", mCallActivity
							.getString(BOOK_PATH));
					mCallBookDiaplay.putExtras(mBundle);
					/* put intent to finish the home page */
					mFinishHomePage.putExtra("FinishHomePage", DEFAULT_VALUE_ONE);

					/* send the broadcast to home page */
					sendBroadcast(mFinishHomePage);
					/* finish the active window activity */
					finish();

					/* start the activity */
					startActivity(mCallBookDiaplay);

				}

				/*
				 * if from home page Request to open active window and try to
				 * open a Note
				 */
				if (mFromHome == FROM_HOME && mType.equalsIgnoreCase("Note")) {

					/* send the note id to the organizer to open the note */
					Intent mCallNoteDisplay = new Intent(ActiveWindow.this,
							NoteOrganizer.class);
					/*
					 * this intent will use to finish the home page which is in
					 * the background
					 */
					Intent mFinishHomePage = new Intent(Intent.ACTION_DEFAULT);
					/* create a bundle */
					Bundle mBundle = new Bundle();
					/* put the extra */
					mBundle.putInt("selectedNoteId_key", mCallActivity
							.getInt(NOTE_ID));
					mCallNoteDisplay.putExtras(mBundle);
					/* put intent to finish the home page */
					mFinishHomePage.putExtra("FinishHomePage", DEFAULT_VALUE_ONE);

					/* send the broadcast to home page */
					sendBroadcast(mFinishHomePage);
					/* finish the current activity */
					finish();
					/* start the activity */
					startActivity(mCallNoteDisplay);
				}
				/*
				 * if from book page request to open active window and try to
				 * open book
				 */
				if (mFromHome == FROM_BOOK) {
					if (mType.equalsIgnoreCase("Book")
							|| mType.equalsIgnoreCase("Magazine")) {
						Intent mCallBookDiaplay = new Intent(ActiveWindow.this,
								Display_Manager.class);
						/*
						 * this intent will use to finish the home page which is
						 * in the background
						 */
						Intent mFinishHomePage = new Intent(
								Intent.ACTION_DEFAULT);
						/* create a bundle */
						Bundle mBundle = new Bundle();
						/* put extra with book path of sd card */
						mBundle.putString("BookPath", mCallActivity
								.getString(BOOK_PATH));
						mCallBookDiaplay.putExtras(mBundle);
						/* put intent to finish the home page */
						mFinishHomePage.putExtra("FinishDisplayPage", 4);
						/* send the broadcast to home page */
						sendBroadcast(mFinishHomePage);
						/* finish the current activity */
						finish();
						/* start the activity */
						startActivity(mCallBookDiaplay);
					}
					/*
					 * if from book page request to open active window and try
					 * to open Note
					 */
					else {

						Intent mCallNoteDisplay = new Intent(ActiveWindow.this,
								NoteOrganizer.class);
						/* create a bundle */
						Bundle mBundle = new Bundle();
						/* put extra with the note id */
						mBundle.putInt("selectedNoteId_key", mCallActivity
								.getInt(NOTE_ID));
						mCallNoteDisplay.putExtras(mBundle);
						/* finish the current activity */
						finish();
						/* start the activity */
						startActivity(mCallNoteDisplay);
					}

				}
				/*
				 * if from book page request to open active window and try to
				 * open Note
				 */
				if (mFromHome == FROM_NOTE) {
					if (mType.equalsIgnoreCase("Note")) {
						Intent mCallNoteDisplay = new Intent(ActiveWindow.this,
								NoteOrganizer.class);
						/*
						 * this intent will use to finish the home page which is
						 * in the background
						 */
						Intent mFinishHomePage = new Intent(
								Intent.ACTION_DEFAULT);
						/* create the bundle */
						Bundle mBundle = new Bundle();
						/* put extra with note id */
						mBundle.putInt("selectedNoteId_key", mCallActivity
								.getInt(NOTE_ID));
						mCallNoteDisplay.putExtras(mBundle);
						/* put intent to finish the home page */
						mFinishHomePage.putExtra("FinishNotePage", 1);

						/* send the broadcast to home page */
						sendBroadcast(mFinishHomePage);
						/* finish the current activity */
						ActiveWindow.this.finish();
						/* start the activity */
						startActivity(mCallNoteDisplay);
					}
					/*
					 * if from book page request to open active window and try
					 * to open book
					 */

					else {
						
						Intent mCallBookDiaplay = new Intent(ActiveWindow.this,
								Display_Manager.class);
						/*
						 * this intent will use to finish the home page which is
						 * in the background
						 */
						Intent mFinishHomePage = new Intent(
								Intent.ACTION_DEFAULT);
						/* create a bundle */
						Bundle mBundle = new Bundle();
						/* put extra with book path of sd card */
						mBundle.putString("BookPath", mCallActivity
								.getString(BOOK_PATH));
						mCallBookDiaplay.putExtras(mBundle);
						/* put intent to finish the home page */
						mFinishHomePage.putExtra("FinishNotePage", DEFAULT_VALUE_ONE);
						/* send the broadcast to home page */
						sendBroadcast(mFinishHomePage);
						/* finish the current activity */
						ActiveWindow.this.finish();
						/* start the activity */
						startActivity(mCallBookDiaplay);
					}
				}
				/* if intent come from Organizer home page */
				if (mFromHome == FROM_ORGANIZER_HOME) {
					/* if request to open a note */
					if (mType.equalsIgnoreCase("Note")) {
						Intent mCallNoteDisplay = new Intent(ActiveWindow.this,
								NoteOrganizer.class);
						/*
						 * this intent will use to finish the home page which is
						 * in the background
						 */
						Intent mFinishHomePage = new Intent(
								Intent.ACTION_DEFAULT);
						/* create the bundle */
						Bundle mBundle = new Bundle();
						/* put extra with note id */
						mBundle.putInt("selectedNoteId_key", mCallActivity
								.getInt(NOTE_ID));
						mCallNoteDisplay.putExtras(mBundle);
						/* put intent to finish the home page */
						mFinishHomePage.putExtra("FinishOrganizerHomePage", DEFAULT_VALUE_ONE);

						/* send the broadcast to home page */
						sendBroadcast(mFinishHomePage);
						/* finish the current activity */
						finish();
						/* start the activity */
						startActivity(mCallNoteDisplay);
					}
					/* if request to open a book */
					else if (mType.equalsIgnoreCase("Book")
							|| mType.equalsIgnoreCase("Magazine")) {
						/*
						 * this intent will use to finish the home page which is
						 * in the background
						 */
						Intent mFinishHomePage = new Intent(
								Intent.ACTION_DEFAULT);
						/* put intent to finish the home page */
						mFinishHomePage.putExtra("FinishOrganizerHomePage", DEFAULT_VALUE_ONE);

						/* send the broadcast to home page */
						sendBroadcast(mFinishHomePage);
						Intent mCallBookDiaplay = new Intent(ActiveWindow.this,
								Display_Manager.class);
						/* create a bundle */
						Bundle mBundle = new Bundle();
						/* set the sd card path of the book */
						mBundle.putString("BookPath", mCallActivity
								.getString(BOOK_PATH));
						mCallBookDiaplay.putExtras(mBundle);
						finish();

						/* start the activity */
						startActivity(mCallBookDiaplay);
					}
				}
				/*
				 * if from any other page (other that book page and home page)
				 * active window is called and try to open a note
				 */
				if (mFromHome == FROM_ANY_ACTIVITY) {
					if (mType.equalsIgnoreCase("Note")) {
						Intent mCallNoteDisplay = new Intent(ActiveWindow.this,
								NoteOrganizer.class);
						/*
						 * this intent will use to finish the home page which is
						 * in the background
						 */
						Intent mFinishHomePage = new Intent(
								Intent.ACTION_DEFAULT);
						/* create the bundle */
						Bundle mBundle = new Bundle();
						/* put extra with note id */
						mBundle.putInt("selectedNoteId_key", mCallActivity
								.getInt(NOTE_ID));
						mCallNoteDisplay.putExtras(mBundle);
						/* put intent to finish the home page */
						mFinishHomePage.putExtra("FinishHomePage", DEFAULT_VALUE_ONE);
						/* put intent to finish the home page */
						mFinishHomePage.putExtra("FinishLibraryPage", DEFAULT_VALUE_ONE);

						/* send the broadcast to home page */
						sendBroadcast(mFinishHomePage);
						/* finish the current activity */
						finish();
						/* start the activity */
						startActivity(mCallNoteDisplay);
					}
					/*
					 * if from any other page (other that book page and home
					 * page) active window is called and try to open a book
					 */
					else if (mType.equalsIgnoreCase("Book")
							|| mType.equalsIgnoreCase("Magazine")) {
						Intent mCallBookDiaplay = new Intent(ActiveWindow.this,
								Display_Manager.class);
						/*
						 * this intent will use to finish the home page which is
						 * in the background
						 */
						Intent mFinishHomePage = new Intent(
								Intent.ACTION_DEFAULT);

						/* create a bundle */
						Bundle mBundle = new Bundle();
						/* put extra of sd card book path */
						mBundle.putString("BookPath", mCallActivity
								.getString(BOOK_PATH));
						mCallBookDiaplay.putExtras(mBundle);
						/* put intent to finish the home page */
						mFinishHomePage.putExtra("FinishLibraryPage", DEFAULT_VALUE_ONE);

						/* send the broadcast to home page */
						sendBroadcast(mFinishHomePage);
						/* finish the current activity */
						finish();
						/* start the activity */
						startActivity(mCallBookDiaplay);

					}
				}
				/*
				 * if from active window no request is done , then only finish
				 * the active window
				 */
				else {
					setResult(9);
					/* only finish the current activity */
					finish();
				}
				/* close the cursor */
				mCallActivity.close();
			}

		});
		/* close the active window */
		ImageView mCloseActiveWindow = (ImageView) findViewById(R.id.closeactivewindow);
		/* set a onclick listener on the image of close the active window */
		mCloseActiveWindow.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/* first check if home page need to be launched */
				if (LibraryMainPage.mPageToCall == EpubReader.QUIKE_PAGE) {
					// restart the activity afte one element
					// deleteion
					startActivity(new Intent(ActiveWindow.this, EpubReader.class));
					
				}
				/* close the active window activity */
				setResult(100);
				ActiveWindow.this.finish();
			}
		});

	}

	/* this adapter is to get the data from the Active window Database */
	private class AdapterForActiveWindow extends BaseAdapter {
		private LayoutInflater mInflater;
		private Bitmap mIcon[];
		private Bitmap mIconDelete[];
		/* this array will store a book image and a note image */
		private Integer[] mThumbIds = { R.drawable.thumbnailviewoption03,
				R.drawable.cascadeviewicon01,
				R.drawable.browsericon01, R.drawable.magazine02 };

		public AdapterForActiveWindow(Context context) {
			/* Cache the LayoutInflate to avoid asking for a new one each time */
			mInflater = LayoutInflater.from(context);
			Bitmap mIcon1[] = new Bitmap[4];
			/* create object for delete icon */
			Bitmap mIconDelete1[] = new Bitmap[mIndex];
			/* Icons bound to the rows */

			/* store the image of book for active window */
			mIcon1[0] = BitmapFactory.decodeResource(context.getResources(),
					mThumbIds[ICON_BOOK]);
			/* store the image of note for active window */
			mIcon1[1] = BitmapFactory.decodeResource(context.getResources(),
					mThumbIds[ICON_NOTE]);
			/* store the image of browser for active window */
			mIcon1[2] = BitmapFactory.decodeResource(context.getResources(),
					mThumbIds[ICON_BROWSER]);
			/* store the image of Magazine for active window */
			mIcon1[3] = BitmapFactory.decodeResource(context.getResources(),
					mThumbIds[ICON_MAGAZINE]);

			/*
			 * store same no of delete icon as many elements in active window
			 * list
			 */
			for (int k = 0; k < mIndex; k++) {
				mIconDelete1[k] = BitmapFactory.decodeResource(context
						.getResources(), R.drawable.activewindowclosebutton);
			}
			mIcon = mIcon1;
			mIconDelete = mIconDelete1;
		}

		public int getCount() {
			return mIndex;

		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			/*
			 * A ViewHolder keeps references to children views to avoid
			 * Unnecessary calls to findViewById() on each row.
			 */
			ViewHolder holder;
			final int mPosition = position;

			// // When convertView is not null, we can reuse it directly, there
			// is no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.activewindowimageanddata, null);
				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.deleteIcon = (ImageView) convertView
						.findViewById(R.id.icondelete);

				// this function will delete specific element from active window
				holder.deleteIcon
						.setOnClickListener(new View.OnClickListener() {

							public void onClick(View v) {
								// delete the specific note or book from active
								// window
								Cursor mTempCursor = mDb
										.selectAllFromActiveWindow();
								mTempCursor.moveToFirst();
								int mIndexFordelete = mTempCursor.getCount();
								String mSaveData[] = new String[mIndexFordelete];
								String mSavePath[] = new String[mIndexFordelete];
								for (int i = 0; i < mIndexFordelete; i++) {
									mSaveData[i] = mTempCursor
											.getString(NAME_BOOK_OR_NOTE);
									mSavePath[i] = mTempCursor.getString(NOTE_ID);
									mTempCursor.moveToNext();
								}
								/*
								 * first check that element which will be
								 * deleted what that is
								 */
								Cursor mTypeCursor = mDb
										.selectTypeActiveWindow(mSaveData[mPosition]);
								if (mTypeCursor != null) {
									/* move the cursor to the first position */
									mTypeCursor.moveToFirst();
									/* get the type of the deleted item */
									String mType = mTypeCursor
											.getString(NAME_BOOK_OR_NOTE);
									/*
									 * this intent will use to finish the home
									 * page which is in the background
									 */
									Intent mFinishHomePage = new Intent(
											Intent.ACTION_DEFAULT);
									if (EpubReader.mPageStatus == EpubReader.BOOK_DISPLAY_PAGE
											|| EpubReader.mPageStatus == EpubReader.NOTE_DISPLAY) {
										/*
										 * if somebody is trying to delete a
										 * note
										 */
										/*
										 * if somebody trying to close a note
										 * seating on note page
										 */
										if (EpubReader.mPageStatus == EpubReader.NOTE_DISPLAY) {
											if (mType.equalsIgnoreCase("Note")) {
												/*
												 * put intent to finish the home
												 * page
												 */
												
												if(mTypeCursor.getInt(2)==NoteOrganizer.mCurrentNoteIdActiveWindow){
													
													mFinishHomePage.putExtra(
															"FinishNotePageForActiveWindow", DEFAULT_VALUE_ONE);

													/*
													 * send the broadcast to home
													 * page
													 */
													sendBroadcast(mFinishHomePage);
													ActiveWindow.this.finish();
													}
												else{
													mDb
													.deleteSpecificActivity(mSaveData[mPosition]);
													ActiveWindow.this.finish();
													// restart the activity after one element
													// delete icon
													Intent mRestartDisplay = new Intent(
															ActiveWindow.this,
															ActiveWindow.class);
													startActivity(mRestartDisplay);

												}
											}
											/*
											 * if somebody try to close the book
											 * seating on note just remove the
											 * entry
											 */
											if (mType.equalsIgnoreCase("Book")
													|| mType
															.equalsIgnoreCase("Magazine")) {
												mDb
														.deleteSpecificActivity(mSaveData[mPosition]);
												ActiveWindow.this.finish();
												// restart the activity after one element
												// delete icon
												Intent mRestartDisplay = new Intent(
														ActiveWindow.this,
														ActiveWindow.class);
												startActivity(mRestartDisplay);

											}
										}
										if (EpubReader.mPageStatus == EpubReader.BOOK_DISPLAY_PAGE) {
											/*
											 * if somebody is trying to delete a
											 * book
											 */
											if (mType.equalsIgnoreCase("Book")
													|| mType
															.equalsIgnoreCase("Magazine")) {

												/*
												 * first check if which book is
												 * currently reading,trying to
												 * close that book
												 */

												if (Display_Manager.mBookPath
														.equalsIgnoreCase(mSavePath[mPosition])) {

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
													ActiveWindow.this.finish();
												}
												/*
												 * if somebody currently open
												 * one book and try to close
												 * another book then just delete
												 * the entry from active window
												 */
												else {
													mDb
															.deleteSpecificActivity(mSaveData[mPosition]);
													ActiveWindow.this.finish();
													// restart the activity after one element
													// delete icon
													Intent mRestartDisplay = new Intent(
															ActiveWindow.this,
															ActiveWindow.class);
													startActivity(mRestartDisplay);

												}

											}
											/*
											 * if somebody seating on book try
											 * to close a note just remove the
											 * entry
											 */
											if (mType.equalsIgnoreCase("Note")) {
												mDb
														.deleteSpecificActivity(mSaveData[mPosition]);
												ActiveWindow.this.finish();
												// restart the activity after one element
												// delete icon
												Intent mRestartDisplay = new Intent(
														ActiveWindow.this,
														ActiveWindow.class);
												startActivity(mRestartDisplay);

											}
										}
									}
									try {
										mDb
												.deleteSpecificActivity(mSaveData[mPosition]);
										ActiveWindow.this.finish();
										/*// restart the activity after one element
										// delete icon
										Intent mRestartDisplay = new Intent(
												ActiveWindow.this,
												ActiveWindow.class);
										startActivity(mRestartDisplay);*/

									} catch (Exception e) {
										e.getStackTrace();
									}

									
								}
							}

						});

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}
			Cursor mCallActivity = mDb
					.selectTypeActiveWindow(mBookData[position]);
			if (mCallActivity != null) {
				mCallActivity.moveToFirst();
				/*
				 * Check the type of element first,if it is a book set a book
				 * icon if it is a note set a note icon
				 */
				mType = mCallActivity.getString(TYPE_BOOK_OR_NOTE);
				/* Set the Icon Appropiately for book and note */
				if (mType.equalsIgnoreCase("Book")) {
					/* set the book icon */
					holder.icon.setImageBitmap(mIcon[ICON_BOOK]);
				} else if (mType.equalsIgnoreCase("Note")) {
					/* set the note icon */
					holder.icon.setImageBitmap(mIcon[ICON_NOTE]);
				} else if (mType.equalsIgnoreCase("Browser")) {
					/* set the browser icon */
					holder.icon.setImageBitmap(mIcon[ICON_BROWSER]);
				} else if (mType.equalsIgnoreCase("Magazine")) {
					/* set the MAGAZINE icon */
					holder.icon.setImageBitmap(mIcon[ICON_MAGAZINE]);
				}
				holder.text.setText("        " + mBookData[position]);
				holder.text.setTextColor(Color.BLACK);
				/* set the element accordingly */
				holder.deleteIcon.setImageBitmap(mIconDelete[position]);
			}
			return convertView;
		}

		/*
		 * this class will usefull to show 1 book or note icon, text and one
		 * delete icon
		 */
		class ViewHolder {
			TextView text;
			ImageView icon;
			ImageView deleteIcon;
		}
	}

	// This code is to return in home using H Key From KeyBoard
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		switch (KeyCode) {
		case KeyEvent.KEYCODE_BACK:

			return true;
		}
		return false;

	}

}
