
package com.sdg.EPUBReader;

import java.io.File;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.ZoomDensity;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import com.sdg.DisplayManager.Display_Manager;
import com.sdg.EPUBparser.EPUBparser;
import com.android.QuikE.R;
import com.sdg.organizer.NoteOrganizer;
import com.sdg.organizer.Organizer;

public class EpubReader extends Activity {
	/* create a new intent for BroadCast */
	Intent mInentAction = new Intent(Intent.ACTION_MAIN);
	/* create a object of Broadcast class */
	BroadcastReceiver mBroadCast = new BroadcastReceiver() {
		/* when message received to finish the home page this will execute */
		@Override
		public void onReceive(Context context, Intent intent) {
				if (intent.getIntExtra("FinishHomePge", DEFAULT_VALUES) == DEFAULT_VALUES) {
				EpubReader.this.finish();
			}

		}
	};
	/* this below three variable is used to save the themeId */
	static long mThemeId;
	/* this variable will indicate which theme is selected */
	static long mSelectedThemeId;
	static int mSelectedColor;   
	static int mCounter = 0;
	SQLiteDatabase mDatabase = null;
	public static final String WEBVIEW_DATABASE = "webview.db";
	/*
	 * These two macro is to indicate that library is opened in listview mode or
	 * in grid view mode
	 */
	public final static int GRID_VIEW = 0;
	/* this string will use to indicate library in list view */
	public final static int LIST_VIEW = 1;
	/* this string is to get the theme no from theme table */
	private final int THEME_COLOUM = 0;
	/* these string is to indicate the theme no */
	public final static int THEME1 = 0;
	/* this string is for theme2 */
	public final static int THEME2 = 1;
	/* this string is for theme 3 */
	public final static int THEME3 = 2;
	/*this string is used for color 0*/
	private final int  BLUE=0;
	/*this string is used for color 1*/
	private final int  RED=1;
	/*this string is used for color 2*/
	private final int  GREEN=2;
	/*this string is used to get the color from the database*/
	private final int GET_COLOR=1;
	/*this string will use to indicate the default values*/
	private final int DEFAULT_VALUES=1;
	/* this string is to set the ID for 1stRecent note in Cascade View */
	private final int NOTE1_ID = 100;
	/* this string is to set the ID for 2ndRecent note in Cascade View */
	private final int NOTE2_ID = 200;
	/* this string is to set the ID for 1st Recent book in Cascade View */
	private final int BOOK1_ID = 10;
	/* this string is to set the ID for 2nd Recent book in Cascade View */
	private final int BOOK2_ID = 20;
	/* this string is to set the ID for 1st Recent Magazine in Cascade View */
	private final int MAGAZINE1_ID = 50;
	/* this string is to set the ID for 2nd Recent Magazine in Cascade View */
	private final int MAGAZINE2_ID = 60;
	/* this string is to set the ID for 3rd Recent Magazine in Cascade View */
	private final int MAGAZINE3_ID = 70;
	/* this string is to set the ID for 1st Recent browser in Cascade View */
	private final int BROWSER1_ID = 80;
	/* this string is to set the ID for 2nd Recent browser in Cascade View */
	private final int BROWSER2_ID = 81;
	/* this string is to set the ID for 3rd Recent browser in Cascade View */
	private final int BROWSER3_ID = 82;
	/* this string is to set the ID for 4th Recent browser in Cascade View */
	private final int BROWSER4_ID = 83;
	/* this string is to set the ID for 5th Recent browser in Cascade View */
	private final int BROWSER5_ID = 84;
	/* table name for Index generation */
	public static final String INDEX_TABLE = "IndexTable";
	/* the location of the book stored */
	private static final String BOOK_PATH = new String("/sdcard/Books/");
	/* the location of the book stored */
	private static final String MAGAZINE_PATH = new String("/sdcard/Magazines/");
	/* the extension of the books supported */
	private static final String FILE_EXTENSION = ".epub";
	/* this string is to set the Screen Hight and Width */
	public final static int SCREEN_HIGHT = 480;
	/*to indicate screen width*/
	public final static int SCREEN_WIDTH = 320;


	/* first initialize the web address for storing the two web address */
	private String mWebAddressFirst = null;
	private String mWebAddressSecond = null;
	private String mWebAddressThird = null;
	private String mWebAddressFourth = null;
	private String mWebAddressFifth = null;

	/* mBookPath array is used to store the sd card path of the book */
	static String mBookPath[];
	/* mBookTitle array is used to store the book's title */
	static String mBookTitle[];
	/* mBookAuthor array is used to store the book's author */
	static String mBookAuthor[];
	/* mBookPath array is used to store the sd card path of the magazine */
	static String mMagazinePath[];
	/* mBookTitle array is used to store the Magazine's title */
	static String mMagazineTitle[];
	/* mBookAuthor array is used to store the magazine's author */
	static String mMagazineAuthor[];
	/* mNoteTitle array is used to store the note's title */
	static String mNoteTitle[];
	/* this variable is used to calculate no of books in database */
	static int mIndex;
	/* this variable is used to calculate no of Magazine in database */
	static int mIndexMagazine;
	/* this variable is used to calculate no of notes in database */
	static int mIndexNote;
	static int mIndexForDelete;
	/* this variable is used to store recent note ID */
	static int mNoteIdRecent[];
	/* initialize the listview */
	public static int mIsListView = GRID_VIEW;
	/* initialize the variable */
	public static int mFromOrganizerNoteIdInCabinet = 0;
	/* these variable will store in which page user are */
	public static int mFromWhichPage = 1;
	public static final int RESET_PAGE = 0;
	public static final int QUIKE_PAGE = 1;
	public static final int LIBRARY_GRID = 2;
	public static final int LIBRARY_LIST = 3;
	public static final int BOOK_DISPLAY_PAGE = 4;
	public static final int ORGANIZER_HOME = 5;
	public static final int NOTE_DISPLAY = 6;
	public static final int BROWSER = 7;

	/* this variable will indicate currently in which page user are */
	public static int mPageStatus = 1;
	/* this icon is to show recent book */
	private Bitmap mIcon[];
	/* this icon is to show recent book */
	private Bitmap mIcon1[];

	/* For absolute layout of library in theme2 */
	private AbsoluteLayout mLibraryLayout;
	// *For absolute layout of organizer in theme3*/
	private AbsoluteLayout mOrganizerLayout;
	/* this image is to show recent book1 */
	private ImageView mBook1;
	/* this image is to show recent book2 */
	private ImageView mBook2;
	/* this image is to show recent note 1 */
	private ImageView mNote1;
	/* this image is to show recent note2 */
	private ImageView mNote2;
	/*
	 * these two grid is to show library in grid view for theme 1 and organizer
	 * in grid view in theme 2
	 */
	GridView mLibg;
	/* this grid view will show note in grid view */
	GridView mOrganizerGrid;

	File mBookHome = new File(BOOK_PATH);
	File mMagazineHome = new File(MAGAZINE_PATH);
	/* these variables are used for database connection */

	/* object of database class */
	DataBaseClass mDb;

	/* this cursor is to store book info */
	Cursor mCursor = null;
	/* this cursor will use to search any book */
	Cursor mCursorSearch = null;

	
	@Override
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* first set the default theme theme1 */
		setContentView(R.layout.theme1blue);

		/*
		 * first inisialize in which page user are in a reset value otherwise
		 * when the application run twice previous value will be retain
		 */
		mPageStatus = RESET_PAGE;
		LibraryMainPage.mPageToCall = RESET_PAGE;

		/* set that user in home page */
		mPageStatus = QUIKE_PAGE;

		/* create a intent filter object */
		IntentFilter iFilter = new IntentFilter(Intent.ACTION_DEFAULT);
		/* register the receiver */
		registerReceiver(mBroadCast, iFilter);

		/* to refresh the categories on startup */
		Organizer.smRefreshCategories = true;

		/* this call is to store the theme iD */
		try {

			/* create all the tables at first */
			mDb = new DataBaseClass(EpubReader.this);
			/* get a readble database */
			mDb.mDatab = mDb.mDatah.getReadableDatabase();
			/*
			 * this function will create all the tables at start up of the
			 * application
			 */
			mDb.createAllTable();
			/* get the themeid */
			mCursor = mDb.returnThemeId();
			/* if atlease one entry is there in database table */
			if (mCursor.getCount() != 0) {
				/* move the cursor to the first */
				mCursor.moveToFirst();
				/* get the value from the themeId table */
				mSelectedThemeId = mCursor.getLong(THEME_COLOUM);
				/* get the color */
				mSelectedColor = mCursor.getInt(GET_COLOR);
				/* if theme1 is selected then set the main layout */
				if (mSelectedThemeId == THEME1) {
					/* if color is selecte the first */
					if (mSelectedColor == BLUE) {
						setContentView(R.layout.theme1blue);
					}
					/* if color is selecte the second */
					if (mSelectedColor == RED) {
						setContentView(R.layout.theme1red);
					}
					/* if color is selecte the third */
					if (mSelectedColor == GREEN) {
						setContentView(R.layout.theme1green);
					}

					/* set the cascade view for note */
					setTheme3ForNote();
					/* set theme for Browser for Theme1 */
					setTheme1ForBrowser();
					/* call the magazines for theme1 */
					getRecentMagazineInfo();
				}
				/* if theme 2 is selected set the theme 2 layout */
				if (mSelectedThemeId == THEME2) {
					/* if theme 2 i sselected set layout for theme2 */
					/* if color is selecte the second */
					if (mSelectedColor == BLUE) {
						setContentView(R.layout.theme2blue);
					}
					/* if color is selected the red */
					if (mSelectedColor == RED) {
						setContentView(R.layout.theme2red);
					}
					/* if color is selecte the green */
					if (mSelectedColor == GREEN) {
						setContentView(R.layout.theme2green);
					}

					/* set cascade view for book */
					setTheme2ForBook();
					/* set grid view for recent note */
					organizerGrid();
					/* set theme for Browser for Theme2 */
					setTheme1ForBrowser();
				}
				/*
				 * if theme 3 is selected set the theme 3 layout for the main
				 * page
				 */
				if (mSelectedThemeId == THEME3) {
					/* if theme3 is selected set the layout for theme3 */
					/* if color is selected the third */
					if (mSelectedColor == BLUE) {
						setContentView(R.layout.theme3blue);
					}
					/* if color is selected the red */
					if (mSelectedColor == RED) {
						setContentView(R.layout.theme3red);
					}
					/* if color is selected the green */
					if (mSelectedColor == GREEN) {
						setContentView(R.layout.theme3green);
					}

					/* set cascade view for book */
					setTheme2ForBook();
					/* set cascade view for note */
					setTheme3ForNote();
					/* set the cascade view of browser */
					setTheme3ForBrowser();
				}

			} else {
				/* if no theme is selected */
				setContentView(R.layout.theme1blue);
				/* insert first theme in the database as a default theme */
				mDb.insertThemeId();
			}

		} catch (SQLException e) {
			e.getStackTrace();
		}
		/*
		 * this will give the selected theme no if theme is changed from setting
		 * page and home page will start with this new selected theme
		 */
		/* get the bundle for theme change */
		Bundle mBundle = this.getIntent().getExtras();
		/* if bundle contains any value */
		if (mBundle != null) {
			long mThemeId = mBundle.getLong("ThemeId");
			int mColorId = mBundle.getInt("ColorID");
			/* if theme 3 is changed from the setting screen */
			if (mThemeId == THEME3) {
				/* if change to theme3 set the layout */
				/* if color is selected as 1 */
				if (mColorId == BLUE) {
					setContentView(R.layout.theme3blue);
				}

				/* set the cascade view for book */
				setTheme2ForBook();
				/* set the cascade view for note */
				setTheme3ForNote();
				/* set the cascade view of browser */
				setTheme3ForBrowser();
			}
			/* if theme 2 is changed from the setting screen */
			if (mThemeId == THEME2) {
				/* if change to theme2 is requested set the layout of theme2 */
				/* if color is selected as 1 */
				if (mColorId == BLUE) {
					setContentView(R.layout.theme2blue);
				}
				/* if color is selected as red */
				if (mColorId == RED) {
					setContentView(R.layout.theme2red);
				}
				/* if color is selected as 1 */
				if (mColorId == GREEN) {
					setContentView(R.layout.theme2green);
				}
				/* set the cascade view for book */
				setTheme2ForBook();
				/* set the grid view for note */
				organizerGrid();
				/* set theme for Browser for Theme2 */
				setTheme1ForBrowser();
			}
			/* if theme 1 is changed from the setting screen */
			if (mThemeId == THEME1) {
				/* if change in theme1 is requested set the layout for theme1 */
				/* if color selected aa 1 */
				if (mColorId == BLUE) {
					setContentView(R.layout.theme1blue);
				}
				/* if color selected aa 1 */
				if (mColorId == RED) {
					setContentView(R.layout.theme1red);
				}
				/* if color selected aa 1 */
				if (mColorId == GREEN) {
					setContentView(R.layout.theme1green);
				}

				/* set the cascade view for note */
				setTheme3ForNote();
				/* set theme for Browser for Theme1 */
				setTheme1ForBrowser();
				/* call the magazines for theme1 */
				getRecentMagazineInfo();
			}
		}
		/* create a object of async task */
		PopulateDatabase mPopulateBook = new PopulateDatabase();
		/*
		 * call the async task for database creation for storing the book
		 * information
		 */
		mPopulateBook.execute();
		/* active window is initialised here */
		ImageView mActiveWindow = (ImageView) findViewById(R.id.activewindow);

		/* set the onclick listener for the active window image */
		mActiveWindow.setOnClickListener(new View.OnClickListener() {
			/*
			 * on click on the active window image it will show the active
			 * window in listview
			 */
			public void onClick(View v) {
				/* create a intent for the active window class */
				Intent mActiveIntent = new Intent(EpubReader.this,
						ActiveWindow.class);
				/*
				 * set this variable to indicate from which page active window
				 * is calling
				 */
				mFromWhichPage = QUIKE_PAGE;
				/* put the extra information */
				mActiveIntent.putExtra("FromHome", DEFAULT_VALUES);
				/* start the activity active window */
				// startActivityForResult(mActiveIntent,11);
				startActivity(mActiveIntent);

				// for animation on opening activewindow
				overridePendingTransition(R.anim.push_down_in, 0);
			}
		});

		/*
		 * this relative layout is used so that instead of click in active
		 * window image if anyone click on any portion of the header bar the the
		 * active window should appear
		 */
		RelativeLayout mPageHeader = (RelativeLayout) findViewById(R.id.header);
		/* set the onclick listener for the header bar */
		mPageHeader.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/* create the intent for the active window class */
				Intent mActiveIntent = new Intent(EpubReader.this,
						ActiveWindow.class);
				mFromWhichPage = QUIKE_PAGE;
				/* set the extra information for the active window class */
				mActiveIntent.putExtra("FromHome", DEFAULT_VALUES);
				/* start the activity */
				startActivity(mActiveIntent);

				// for animation on opening activewindow
				overridePendingTransition(R.anim.push_down_in, 0);

			}
		});

		/*
		 * this grid view is to show the recently opened books in a grid view
		 * manner
		 */

		mLibg = (GridView) findViewById(R.id.libraryGrid);
		/* set the adapter to show the recent book */
		mLibg.setAdapter(new RecentBookViewAdapter(this));
		/* this function will gather recent magazine open from library */
		// getRecentMagazineInfo();
		/*
		 * on click of the recent book it will directly open the Display Manager
		 * page
		 */
		mLibg.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> ImageAdapter, View v,
					int position, long id) {
				/* give the function the appropriate position */
				openBookViewPage(position);

			}
		});
		/*
		 * on click of this button library will open ,first it will check in
		 * application which mode library was opened ,if it is in list view open
		 * at the last then it will open list view, if grid view is opened at
		 * the last then grid view will be opened
		 */
		Button mGotolib = (Button) findViewById(R.id.gotolibrary);
		/* set the onclick listener for gotolibrary button */
		mGotolib.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/* create a intent for the library grid view */
				Intent mIntentGrid = new Intent(EpubReader.this,
						LibraryMainPage.class);
				/* create a intent for the library list view */
				Intent mIntentList = new Intent(EpubReader.this,
						LibraryMainPageList.class);
				/* if previously list view was selected */
				if (mIsListView == GRID_VIEW) {
					/* finish the home screen first */
					EpubReader.this.finish();
					/* start the libray list view */
					startActivity(mIntentGrid);
				}
				/* if previously grid view was selected */
				if (mIsListView == LIST_VIEW) {
					/* finish the home screen first */
					EpubReader.this.finish();
					/* start the grid view of library */
					startActivity(mIntentList);
				}
			}
		});

		/* code for go to android browser */
		/* initialize the browser button */
		ImageView mGotoBrowser = (ImageView) findViewById(R.id.gotomybrowser);
		/* on click of that browser button open the browser page */
		mGotoBrowser.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
						Intent mBrowserIntent = new Intent(EpubReader.this,
						Browser.class);

				/* insert into active window table also */
					mDb.insertInActiveWindow("http://www.google.com/",
							"http://www.google.com/", "Browser");
				
				startActivity(mBrowserIntent);

			}
		});

		/* initialize the button for setting page */
		Button mGotosetting = (Button) findViewById(R.id.settings);

		/* Image View for calling the Organizer */
		ImageView mGoToMyOrganizer = (ImageView) findViewById(R.id.gotomyorganizer);

		/* Setting ON click listener for calling Organizer Activity */
		mGoToMyOrganizer.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/* create the intent for the organizer */
				Intent mCallOrgIntent = new Intent(EpubReader.this, Organizer.class);
				/* finish the home screen */
				EpubReader.this.finish();
				/* start the activity */
				startActivity(mCallOrgIntent);

			}

		});
		/*
		 * on click on the setting image it will open a new activity from where
		 * we can call manage home activity
		 */
		mGotosetting.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/* create the intent for the setting change page */
				Intent mIntent = new Intent(EpubReader.this, SettingChange.class);
				/* start the setting activity */
				startActivity(mIntent);
			}
		});
		/* close the cursor */
		mCursor.close();

	}

	/* this adapter is to show the libray books listing in a grid view manner */

	public class RecentBookViewAdapter extends BaseAdapter {
		/* take a generaic layout */
		private LayoutInflater mInflater;
		/* this icons will store the images of recent book */
		private Bitmap mIcon[];
		/* this icons has a white written place in library */
		private Integer[] mThumbIds = { R.drawable.thumbnailviewoption03 };
		
		/* constructor of this adapter */
		public RecentBookViewAdapter(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			/* get the recent book info */
			getRecentBookInfo();
			/* initialize the bitmap icons */
			Bitmap mIcon1[] = new Bitmap[mIndex];
			
			/*
			 * this loop will initialize the bitmaps according to the no of
			 * recent books
			 */
			for (int j = 0; j < mIndex; j++) {
				
				/* store a random image */
				mIcon1[j] = BitmapFactory.decodeResource(
						context.getResources(), mThumbIds[0]);
			}

			mIcon = mIcon1;
			// Icons bound to the rows.

		}

		/* this function will return the no of recent books */
		public int getCount() {
			return mIndex;
		}

		/* this function will return the clicked position */
		public Object getItem(int position) {
			return position;
		}

		/* this function will return the clicked position id */
		public long getItemId(int position) {
			return position;
		}

		/* this function is overridden to create the adapter */
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.iimageanddataforgrid,
						null);

				/* create a object of the holderclass */
				holder = new ViewHolder();
				/* initialize the text */
				holder.text = (TextView) convertView.findViewById(R.id.text);
				/* set the color of the text */
				holder.text.setTextColor(Color.WHITE);
				/* initialize the icons */
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				/* give every element a tag */
				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			/* set the images according to the position */
			holder.icon.setImageBitmap(mIcon[position]);
			holder.icon.setPadding(0, 10, 0, 0);
			/* set the text according to the position */
			holder.text.setText(mBookTitle[position]);

			return convertView;
		}
	}

	/*
	 * this class is capable of showing a image and a text at the same time in a
	 * grid view
	 */
	static class ViewHolder {
		TextView text;
		ImageView icon;
	}

	/*
	 * this async task will run as a thread when the application first launch
	 * and will collect the books name,title and book path stored in the sdcard
	 */
	public class PopulateDatabase extends AsyncTask<String, Void, Void> {
		protected void onPreExecute() {
			super.onPreExecute();
		}

		/* 0verridden function,not used */
		protected final void publishProgress(int values) {

		}

		/* overridden function,not used */
		protected void onProgressUpdate(Integer... progress) {

			super.onProgressUpdate();

		}

		/* after the thread end this function will execute */
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			/* create a object of the database class */
			DataBaseClass mForUpdate = new DataBaseClass(EpubReader.this);
			/* get a handler */
			mForUpdate.mDatab = mForUpdate.mDatah.getWritableDatabase();
			/* update the book info table */
			mForUpdate.ReturnCursorForDelete();
			/* update the book info table */
			mForUpdate.UpdateBookPresent();
			/*
			 * only when no book is in SD card the mCursor search will be null
			 * for the first time only
			 */
			if (mCursorSearch != null) {
				/* if cursor is not null the close it */
				mCursorSearch.close();
			}
		}

		/*
		 * call the JNI function to collect books title, author name and sd card
		 * path and store it in BookDataBase table
		 */
		@Override
		protected Void doInBackground(String... arg0) {

			try {
				/* if any element present in sd card */
				if (mBookHome.listFiles().length > 0
						|| mMagazineHome.listFiles().length > 0) {
					/* start this loop until any element present in sd card */
					for (File file : mBookHome.listFiles()) {
						/* if any element present ends with extension .epub */
						if ((file.getName()).endsWith(FILE_EXTENSION)) {
							extractEpubElements(file, BOOK_PATH, "Book");

						}

					}
					for (File file : mMagazineHome.listFiles()) {
						/* if any element present ends with extension .epub */
						if ((file.getName()).endsWith(FILE_EXTENSION)) {
							extractEpubElements(file, MAGAZINE_PATH, "Magazine");

						}

					}
				}
			} catch (Exception e) {
				e.getStackTrace();

			}

			return null;

		}

	}

	/* this action will done when first book will click in cascade view */
	OnClickListener bookOnClickListener = new OnClickListener() {

		public void onClick(View v) {
			/* check if click on first book icon */
			if (v.getId() == BOOK1_ID) {
				/* request to open that book */
				openBookViewPage(0);
			}
			/* if click on the 2nd images of the book */
			else if (v.getId() == BOOK2_ID) {
				/* request to open that book */
				openBookViewPage(1);
			}
		}

	};
	/* this action will done when from cascade view any note will open */
	OnClickListener NoteOnClickListener = new OnClickListener() {

		public void onClick(View v) {
			/* if clicked on first first note */
			if (v.getId() == NOTE1_ID) {
				/* request to open that note */
				openNoteViewPage(0);
			}
			/* if clicked on first first note */
			else if (v.getId() == NOTE2_ID) {
				/* request to open that note */
				openNoteViewPage(1);
			}
		}

	};
	/* this action will done Magazine click in cascade view */
	OnClickListener MagazineOnClickListener = new OnClickListener() {

		public void onClick(View v) {
			/* check if click on first magazine icon */
			if (v.getId() == MAGAZINE1_ID) {
				/* request to open that magazine */
				openMagazineViewPage(0);
			}
			/* if click on the 2nd images of the magazine */
			else if (v.getId() == MAGAZINE2_ID) {
				/* request to open that magazine */
				openMagazineViewPage(1);
			} else if (v.getId() == MAGAZINE3_ID) {
				/* request to open that magazine */
				openMagazineViewPage(2);
			}
		}

	};
	
	/* this function will open the browser from the home page */
	OnTouchListener browserOnClickListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub

			if (v.getId() == BROWSER1_ID) {
				/* request to open that magazine */
				openBrowserViewPage(1);
			}
			/* if click on the 2nd images of the magazine */
			else if (v.getId() == BROWSER2_ID) {
				/* request to open that magazine */
				openBrowserViewPage(2);
			}
			/* if click on the 3rd images of the magazine */
			else if (v.getId() == BROWSER3_ID) {
				/* request to open that magazine */
				openBrowserViewPage(3);
			}
			/* if click on the 4th images of the magazine */
			else if (v.getId() == BROWSER4_ID) {
				/* request to open that magazine */
				openBrowserViewPage(4);
			}
			/* if click on the 4th images of the magazine */
			else if (v.getId() == BROWSER5_ID) {
				/* request to open that magazine */
				openBrowserViewPage(5);
			}
			return true;
		}
	};

	/* this function is used to get the recentBookInfo */
	public void getRecentBookInfo() {
		/* open the cursor for recent book table */
		Cursor mCursorForRecent;

		/* get the cursor */
		mCursorForRecent = mDb
				.recentBookOpendRetrive(DataBaseClass.RECENT_BOOK_DATABASE_TABLE);
		/* get the no of books */
		mIndex = mCursorForRecent.getCount();
		mCursorForRecent.moveToFirst();
		/* Initialize the data for title */
		mBookTitle = new String[mIndex];
		/* initialize the data for author */
		mBookAuthor = new String[mIndex];
		/* initialize the data for book path */
		mBookPath = new String[mIndex];
		/* initialize bitmap image */
		Bitmap mIcon1[] = new Bitmap[mIndex];
		/* this loop will store the image for first recent book */
		for (int j = 0; j < mIndex; j++)
			mIcon1[j] = BitmapFactory.decodeResource(getResources(),
					R.drawable.thumbnailviewoption03);
		mIcon = mIcon1;

		/* Fetch the book information */
		for (int i = 0; i < mIndex; i++) {
			/* store the first book sd card */

			mBookPath[i] = mCursorForRecent.getString(1);
			/* store the recent book 1st author name */
			mBookAuthor[i] = mCursorForRecent.getString(2);
			/* store the 1st recent book title */
			mBookTitle[i] = mCursorForRecent.getString(3);
			/* move the cursor to the next position */
			mCursorForRecent.moveToNext();
		}
		mCursorForRecent.close();
	}

	/* this function will get the information from Magazine */
	public void getRecentMagazineInfo() {
		/* open the cursor for resent book table */
		Cursor mCursorForRecent;

		/* get the cursor */
		mCursorForRecent = mDb
				.recentBookOpendRetrive(DataBaseClass.RECENT_MAGAZINE_DATABASE_TABLE);
		/* get the no of books */
		mIndexMagazine = mCursorForRecent.getCount();
		mCursorForRecent.moveToFirst();
		/* Initialize the data for title */
		mMagazineTitle = new String[mIndexMagazine];
		/* initialize the data for author */
		mMagazineAuthor = new String[mIndexMagazine];
		/* initialize the data for book path */
		mMagazinePath = new String[mIndexMagazine];

		/* Fetch the book information */
		for (int i = 0; i < mIndexMagazine; i++) {
			/* store the first book sd card */

			mMagazinePath[i] = mCursorForRecent.getString(1);
			/* store the recent book 1st author name */
			mMagazineAuthor[i] = mCursorForRecent.getString(2);
			/* store the 1st recent book title */
			mMagazineTitle[i] = mCursorForRecent.getString(3);
			/* move the cursor to the next position */
			mCursorForRecent.moveToNext();
		}
		/* close the cursor */
		mCursorForRecent.close();
		/* first check if any magazine is present in recent magazine table */
		if (mIndexMagazine > 0) {
			/* initialize the layout of magazine and newspapwe */
			AbsoluteLayout mMagazineLayout = (AbsoluteLayout) findViewById(R.id.newspaperLayout);
			/* create a object for first image of magazine */
			ImageView mMagazineFirstImage = new ImageView(EpubReader.this);

			/* set the background for the first magazine */
			mMagazineFirstImage
					.setBackgroundResource(R.drawable.magazine02);
			/* set the ID for the first magazine */
			mMagazineFirstImage.setId(MAGAZINE1_ID);
			/*
			 * create a obeject of textview to show the name for the first
			 * magazine
			 */
			TextView mMagazineFirstTextView = new TextView(EpubReader.this);
			/*
			 * set the text of the textview from the database for the first
			 * magazine
			 */
			mMagazineFirstTextView.setText(mMagazineTitle[0]);
			/* set the color of the text */
			mMagazineFirstTextView.setTextColor(Color.WHITE);
			/* set the hight of the text */
			mMagazineFirstTextView.setTextSize(10);
			/* add with the layout first magazine image */
			mMagazineLayout.addView(mMagazineFirstImage,
					new AbsoluteLayout.LayoutParams(120, 180, 400, 0));
			/* place the name of the magazine for the first */
			mMagazineLayout.addView(mMagazineFirstTextView,
					new AbsoluteLayout.LayoutParams(100, 132, 400, 160));  
			/* set the on click listener for this image */
			mMagazineFirstImage.setOnClickListener(MagazineOnClickListener);
			/*
			 * check if second entry id there in database for magazine recent
			 * table
			 */
			if (mIndexMagazine > 1) {
				/* create a object for second image of magazine */
				ImageView mMagazineSecondImage = new ImageView(EpubReader.this);
				/* set the id for this imageview */
				mMagazineSecondImage.setId(MAGAZINE2_ID);
				/* set the background for the 2nd magazine */
				mMagazineSecondImage
						.setBackgroundResource(R.drawable.magazine02);
				/*
				 * create a obeject of textview to show the name for the 2nd
				 * magazine
				 */
				TextView mMagazineSecondTextView = new TextView(EpubReader.this);
				/*
				 * set the text of the textview from the database for the 2nd
				 * magazine
				 */
				mMagazineSecondTextView.setText(mMagazineTitle[1]);
				/* set the color of the text */
				mMagazineSecondTextView.setTextColor(Color.WHITE);
				/* set the hight of the text */
				mMagazineSecondTextView.setTextSize(10);
				/* add with the layout 2nd magazine image */
				mMagazineLayout.addView(mMagazineSecondImage,
						new AbsoluteLayout.LayoutParams(120, 180, 550, 0));
				/* place the name of the magazine for the 2nd */
				mMagazineLayout.addView(mMagazineSecondTextView,
						new AbsoluteLayout.LayoutParams(100, 132, 560, 160));
				/* set the on click listener for this image */
				mMagazineSecondImage
						.setOnClickListener(MagazineOnClickListener);
			}
			/* check if 3rd entry id there in database for magazine recent table */
			if (mIndexMagazine > 2) {
				/* create a object for 3rd image of magazine */
				ImageView mMagazineThirdImage = new ImageView(EpubReader.this);
				/* set the ID for this magazine */
				mMagazineThirdImage.setId(MAGAZINE3_ID);
				/* set the background for the 3ed magazine */
				mMagazineThirdImage
						.setBackgroundResource(R.drawable.magazine02);
				/*
				 * create a obeject of textview to show the name for the 3rd
				 * magazine
				 */
				TextView mMagazineThirdTextView = new TextView(EpubReader.this);
				/*
				 * set the text of the textview from the database for the 3rd
				 * magazine
				 */
				mMagazineThirdTextView.setText(mMagazineTitle[2]);
				/* set the color of the text */
				mMagazineThirdTextView.setTextColor(Color.WHITE);
				/* set the hight of the text */
				mMagazineThirdTextView.setTextSize(10);
				/* add with the layout 3rd magazine image */
				mMagazineLayout.addView(mMagazineThirdImage,
						new AbsoluteLayout.LayoutParams(120, 200, 475, 20));
				/* place the name of the magazine for the 3rd */
				mMagazineLayout.addView(mMagazineThirdTextView,
						new AbsoluteLayout.LayoutParams(100, 132, 475, 200));
				/* set the on click listener for this image */
				mMagazineThirdImage.setOnClickListener(MagazineOnClickListener);
			}

		}
	}

	@SuppressWarnings("deprecation")
	public void setTheme2ForBook() {
		/* initialize the absolute layout */
		mLibraryLayout = (AbsoluteLayout) findViewById(R.id.library_theme2);
		/* get the recent book info */
		getRecentBookInfo();
		/* if any recent book is present */
		if (mIndex > 0) {
			mBook1 = new ImageView(EpubReader.this);
			/* give the id for the first image */
			mBook1.setId(BOOK1_ID);

			/* set the image* */
			mBook1.setBackgroundResource(R.drawable.thumbnailviewoption03);
			TextView mTextView1 = new TextView(EpubReader.this);
			/* set the book name */
			mTextView1.setText(mBookTitle[0]);
			mTextView1.setTextSize(13);
			/* set the color */
			mTextView1.setTextColor(Color.WHITE);
			/* set text box width */

			/* set the layout param */
			mLibraryLayout.addView(mBook1, new AbsoluteLayout.LayoutParams(126,
					230, 50, 35));
			mLibraryLayout.addView(mTextView1, new AbsoluteLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 10,
					208));
			mBook1.setOnClickListener(bookOnClickListener);
			/* if 2nd recent book is present */
			if (mIndex > 1) {
				mBook2 = new ImageView(EpubReader.this);
				/* set the id forthe 2nd book */
				mBook2.setId(BOOK2_ID);
				/* set the image for the 2nd book */
				mBook2.setBackgroundResource(R.drawable.thumbnailviewoption03);

				TextView mTextView2 = new TextView(EpubReader.this);
				/* set the title of the second book */
				mTextView2.setText(mBookTitle[1]);
				/* set the text color */
				mTextView2.setTextColor(Color.WHITE);
				mTextView2.setTextSize(13);

				mLibraryLayout.addView(mBook2, new AbsoluteLayout.LayoutParams(
						126, 230, 110, 70));
				mLibraryLayout.addView(mTextView2,
						new AbsoluteLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT, 80, 240));
				mBook2.setOnClickListener(bookOnClickListener);

			}
		}

	}

	/* this function will display cascade view for theme 3 for note */
	@SuppressWarnings("deprecation")
	public void setTheme3ForNote() {
		mOrganizerLayout = (AbsoluteLayout) findViewById(R.id.organizertheme3);

		getRecentNoteInfo();
		if (mIndexNote > 0) {
			mNote1 = new ImageView(EpubReader.this);
			mNote1.setId(NOTE1_ID);
			mNote1.setBackgroundResource(R.drawable.notes02);
			TextView mTextView1 = new TextView(EpubReader.this);
			mTextView1.setTextSize(11);
			mTextView1.setText(mNoteTitle[0].substring(0, mNoteTitle[0].length() - 5));
			mTextView1.setTextColor(Color.BLACK);
			mTextView1.setWidth(70);
			mTextView1.setLines(1);
			mOrganizerLayout.addView(mNote1, new AbsoluteLayout.LayoutParams(
					135, 168, 25, 30));
			mOrganizerLayout.addView(mTextView1,
					new AbsoluteLayout.LayoutParams(90, 80, 36, 36));
			mNote1.setOnClickListener(NoteOnClickListener);
			if (mIndexNote > 1) { 
				mNote2 = new ImageView(EpubReader.this);
				mNote2.setId(NOTE2_ID);
				mNote2.setBackgroundResource(R.drawable.notes02);
				TextView mTextView2 = new TextView(EpubReader.this);
				mTextView2.setText(mNoteTitle[1].substring(0, mNoteTitle[1].length() - 5));
				mTextView2.setTextColor(Color.BLACK);
				mTextView2.setTextSize(11);
				mTextView2.setWidth(70);
				mTextView2.setLines(1);
				mOrganizerLayout.addView(mNote2,
						new AbsoluteLayout.LayoutParams(135, 168, 170, 30));
				mOrganizerLayout.addView(mTextView2,
						new AbsoluteLayout.LayoutParams(90, 80, 182, 36));  
				mNote2.setOnClickListener(NoteOnClickListener);

			}
		}

	}

	/* this function is to call the bookviewpage from home screen */
	public void openBookViewPage(int position) {
		Intent mIntentForResentBook = new Intent(EpubReader.this,
				Display_Manager.class);
		Bundle mBundle = new Bundle();

		/*
		 * For direct open the resent book from Main page
		 */
		mBundle.putString("BookPath", mBookPath[position]);
		mIntentForResentBook.putExtras(mBundle);

		/* drop the index table first then call bookdisplay */
		mDb.dropIndexTable();

		/* insert in active window table first */

		mDb.insertInActiveWindow(mBookTitle[position], mBookPath[position],
				"Book");

		/* Extract the fiel path according to the click on the grid */

		String mFilePath = mBookPath[position];
		String mExtractedAuthor = mBookAuthor[position];
		String mExtractedTitle = mBookTitle[position];

		mDb.recentBookOpendInsert(mFilePath, mExtractedAuthor, mExtractedTitle,
				DataBaseClass.RECENT_BOOK_DATABASE_TABLE);

		/* finish the home screen */
		EpubReader.this.finish();
		/* start the Display Manager */
		startActivity(mIntentForResentBook);
	}

	public void organizerGrid() {
		mOrganizerGrid = (GridView) findViewById(R.id.OrganizerGrid);
		mOrganizerGrid.setAdapter(new RecentNoteViewAdapter(this));
		mOrganizerGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> ImageAdapter, View v,
					int position, long id) {
				openNoteViewPage(position);

			}
		});

	}

	/* this class is for showing the recently opened note */
	public class RecentNoteViewAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Bitmap mIcon[];

		public RecentNoteViewAdapter(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			getRecentNoteInfo();
			Bitmap mIcon1[] = new Bitmap[mIndexNote];
			for (int j = 0; j < mIndexNote; j++) {

				mIcon1[j] = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.notes01);
			}

			mIcon = mIcon1;
			// Icons bound to the rows.

		}

		public int getCount() {
			return mIndexNote;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			NoteHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.imageanddatafornotegrid, null);
				holder = new NoteHolder();
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.text.setPadding(0, 50, 0, 0);
				holder.text.setTextSize(13);
				holder.text.setTextColor(Color.WHITE);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);

				convertView.setTag(holder);
			} else {

				holder = (NoteHolder) convertView.getTag();
			}
			holder.icon.setImageBitmap(mIcon[position]);
			holder.text.setText("        " + mNoteTitle[position]);

			return convertView;
		}
	}

	static class NoteHolder {
		TextView text;
		ImageView icon;
	}

	/*
	 * this function will get the recent note infornation and will show in the
	 * home screen
	 */
	public void getRecentNoteInfo() {
		/* get the information from NoteTable */

		Cursor mCursorForRecentNote;

		mCursorForRecentNote = mDb.recentNoteOpendRetrive();
		mIndexNote = mCursorForRecentNote.getCount();
		mCursorForRecentNote.moveToFirst();
		/* Initialize the image and data */
		mNoteTitle = new String[mIndexNote];
		mNoteIdRecent = new int[mIndexNote];
		Bitmap mIcon1[] = new Bitmap[mIndexNote];
		for (int j = 0; j < mIndexNote; j++)
			mIcon1[j] = BitmapFactory.decodeResource(getResources(),
					R.drawable.thumbnailviewoption03);
		mIcon = mIcon1;

		/* Fetch the book information */
		for (int i = 0; i < mIndexNote; i++) {

			mNoteIdRecent[i] = mCursorForRecentNote.getInt(0);
			mNoteTitle[i] = mCursorForRecentNote.getString(1);

			mCursorForRecentNote.moveToNext();
		}
		mCursorForRecentNote.close();
	}

	/* this function is to call the Recent Organizer from home screen */
	public void openNoteViewPage(int position) {
		Intent mIntentForResentNote = new Intent(EpubReader.this,
				NoteOrganizer.class);
		Bundle mBundle = new Bundle();

		/*
		 * For direct open the resent book from Main page
		 */
		mBundle.putInt("selectedNoteId_key", mNoteIdRecent[position]);
		mIntentForResentNote.putExtras(mBundle);
		/* finish the home screen */
		EpubReader.this.finish();

		startActivity(mIntentForResentNote);
	}

	/* this function is used when from home screen magazine will be clicked */
	public void openMagazineViewPage(int position) {
		Intent mIntentForResentBook = new Intent(EpubReader.this,
				Display_Manager.class);
		Bundle mBundle = new Bundle();

		/*
		 * For direct open the resent book from Main page
		 */
		mBundle.putString("BookPath", mMagazinePath[position]);
		mIntentForResentBook.putExtras(mBundle);

		/* drop the index table first then call bookdisplay */
		mDb.dropIndexTable();
		/* insert in active window table first */

		/* insert in active window database */
		mDb.insertInActiveWindow(mMagazineTitle[position],
				mMagazinePath[position], "Magazine");
		/* Extract the fiel path according to the click on the Magazine */

		String mFilePath = mMagazinePath[position];
		String mExtractedAuthor = mMagazineAuthor[position];
		String mExtractedTitle = mMagazineTitle[position];

		mDb.recentBookOpendInsert(mFilePath, mExtractedAuthor, mExtractedTitle,
				DataBaseClass.RECENT_MAGAZINE_DATABASE_TABLE);

		/* finish the home screen */
		EpubReader.this.finish();
		/* start the Display Manager */
		startActivity(mIntentForResentBook);
	}

	/* this function is to open the browser from the home page */
	public void openBrowserViewPage(int position) {
		/* create a intent to open the browser */
		Intent mBrowserOpenIntent = new Intent(EpubReader.this,
				Browser.class);
		/* create a bundle */
		Bundle mBundleBrowserIntent = new Bundle();
		/* if click on the first tab for the browser */
		if (position == 1) {
			/* put the address for the browser */
			mBundleBrowserIntent.putString("Browser", mWebAddressFirst);
			/* bundle it with the intent */
			mBrowserOpenIntent.putExtras(mBundleBrowserIntent);
			/* start the activity */
			startActivity(mBrowserOpenIntent);
		}
		/* if click on the second tab of browser */
		if (position == 2) {
			/* put the second web address path */
			mBundleBrowserIntent.putString("Browser", mWebAddressSecond);
			/* bundle it with intent */
			mBrowserOpenIntent.putExtras(mBundleBrowserIntent);
			/* start the browser activity */
			startActivity(mBrowserOpenIntent);
		}
		/* if click on the third tab of browser */
		if (position == 3) {
			/* put the second web address path */
			mBundleBrowserIntent.putString("Browser", mWebAddressThird);
			/* bundle it with intent */
			mBrowserOpenIntent.putExtras(mBundleBrowserIntent);
			/* start the browser activity */
			startActivity(mBrowserOpenIntent);
		}
		/* if click on the 4th tab of browser */
		if (position == 4) {
			/* put the second web address path */
			mBundleBrowserIntent.putString("Browser", mWebAddressFourth);
			/* bundle it with intent */
			mBrowserOpenIntent.putExtras(mBundleBrowserIntent);
			/* start the browser activity */
			startActivity(mBrowserOpenIntent);
		}
		/* if click on the 5th tab of browser */
		if (position == 5) {
			/* put the second web address path */
			mBundleBrowserIntent.putString("Browser", mWebAddressFifth);
			/* bundle it with intent */
			mBrowserOpenIntent.putExtras(mBundleBrowserIntent);
			/* start the browser activity */
			startActivity(mBrowserOpenIntent);
		}

	}
	
	/*
	 * this function is to extract Epub files and generate Book and Magazine
	 * name
	 */
	private void extractEpubElements(File file, String PATH, String Type) {
		/* create a object of the EpubParse */
		EPUBparser mEPUBparser = new EPUBparser();
		/* get the container parser info */
		mEPUBparser.cprootfile = mEPUBparser.EPUB_AL_ParseContainerFile(PATH
				+ file.getName(), mEPUBparser.cproot);
		/* get the opf path info */
		String mOpfpath = mEPUBparser.CP_GetOpfPath(mEPUBparser.cproot,
				mEPUBparser.cprootfile);
		/* if any opf file is present */
		if (mOpfpath != null) {
			/* get the book's title and author, and path */
			mEPUBparser.EPUB_AL_OPF_ParseTitleAndAuthor(PATH + file.getName(),
					mOpfpath, mEPUBparser.titileauthorelement);

			try {
				String mTemp = PATH + file.getName();
				mCursorSearch = mDb.searchASpecificBook(mTemp);
				int BookCount = mCursorSearch.getCount();
				if (mCursorSearch.getCount() == 0) {
					mDb.insertInBookDataBase(PATH + file.getName(), mEPUBparser
							.AuthorName(), mEPUBparser.TitleName(), Type);

				} else {
					mDb.updateInBookDataBase(mTemp);

				}

			} catch (SQLException e) {

				e.getStackTrace();
			}
		}
	}

	public void setTheme1ForBrowser() {
		/* initialize the webview 1 */
		WebView mWebViewFisrt = (WebView) findViewById(R.id.WebView1Theme1);
		/* give this web view a id */
		mWebViewFisrt.setId(BROWSER1_ID);
		/* initialize web view 2 */
		WebView mWebViewSecond = (WebView) findViewById(R.id.WebView2Theme1);
		/* give the second web view a id */
		mWebViewSecond.setId(BROWSER2_ID);
		/* initialize the absolute layout */
		AbsoluteLayout mWebViewLayout = (AbsoluteLayout) findViewById(R.id.browsertheme3);

		/* set the onclick listener for the web views */

		mWebViewFisrt.setOnTouchListener(browserOnClickListener);
		mWebViewSecond.setOnTouchListener(browserOnClickListener);

		/* setting of web vie client for the second web view end */
		try {
			/* open a connection for the database */
			mDatabase = this.openOrCreateDatabase(WEBVIEW_DATABASE,
					MODE_PRIVATE, null);
			/* take all the url from the database of inbuilt webview.db database */
			Cursor mGetRecentWebCursor = mDatabase.rawQuery(
					"SELECT url FROM formurl ;", null);
			/* take the last one */
			mGetRecentWebCursor.moveToLast();
			/* if atleast one entry is there in webview.db database */
			if (mGetRecentWebCursor.getCount() > 0) {
				mGetRecentWebCursor.moveToLast();
				/* take the address of the last opened web browser */
				mWebAddressFirst = mGetRecentWebCursor.getString(0);
				/* initialize and set web view client for the web view first */
				webviewSetUp(mWebViewFisrt, mWebAddressFirst);
				/* load the url in the first webview */
				mWebViewFisrt.loadUrl(mWebAddressFirst);
				mWebViewFisrt.setVisibility(0);
				/* add the first web view to the absolute layout */
				mWebViewLayout.addView(mWebViewFisrt,
						new AbsoluteLayout.LayoutParams(125, 10, 30, 55));
			}
			if (mGetRecentWebCursor.getCount() > 1) {
				/* move the cursor to the 2nd last position */
				mGetRecentWebCursor.moveToPrevious();
				/* take the address */
				mWebAddressSecond = mGetRecentWebCursor.getString(0);

				/* initialize and set web view client for the web view first */
				webviewSetUp(mWebViewSecond, mWebAddressSecond);

				/* load the address of 2nd open web address */
				mWebViewFisrt.loadUrl(mWebAddressSecond);
				mWebViewSecond.setVisibility(0);
				/* add the second web view to the absolute layout */
				mWebViewLayout.addView(mWebViewSecond,
						new AbsoluteLayout.LayoutParams(125, 100, 70, 70));
			}
			mGetRecentWebCursor.close();
		} catch (Exception e) {
			e.getStackTrace();

		}

	}

	/* this function will use to set the cascade view set of browser */
	@SuppressWarnings("deprecation")
	public void setTheme3ForBrowser() {
		/* initialize the webview 1 */
		WebView mWebViewFisrt = new WebView(EpubReader.this);
		/* disable the Horizental scrollbar */
		mWebViewFisrt.setHorizontalScrollBarEnabled(false);
		/* disable the Vertical scrollbar */
		mWebViewFisrt.setVerticalScrollBarEnabled(false);
		/* give this web view a id */
		mWebViewFisrt.setId(BROWSER1_ID);
		/* initialize web view 2 */
		WebView mWebViewSecond = new WebView(EpubReader.this);
		/* disable the Horizental scrollbar */
		mWebViewSecond.setHorizontalScrollBarEnabled(false);
		/* disable the vertical scrollbar */
		mWebViewSecond.setVerticalScrollBarEnabled(false);
		/* give the second web view a id */
		mWebViewSecond.setId(BROWSER2_ID);
		WebView mWebViewThird = new WebView(EpubReader.this);
		/* disable the Horizental scrollbar */
		mWebViewThird.setHorizontalScrollBarEnabled(false);
		/* disable the Vertical scrollbar */
		mWebViewThird.setVerticalScrollBarEnabled(false);
		/* give this web view a id */
		mWebViewThird.setId(BROWSER3_ID);
		/* initialize web view 2 */
		WebView mWebViewFourth = new WebView(EpubReader.this);
		/* disable the Horizental scrollbar */
		mWebViewFourth.setHorizontalScrollBarEnabled(false);
		/* disable the Verical scrollbar */
		mWebViewFourth.setVerticalScrollBarEnabled(false);
		/* give the second web view a id */
		mWebViewFourth.setId(BROWSER4_ID);
		/* initialize web view 2 */
		WebView mWebViewFifth = new WebView(EpubReader.this);
		/* disable the Horizental scrollbar */
		mWebViewFifth.setHorizontalScrollBarEnabled(false);
		/* disable the vertical scrollbar */
		mWebViewFifth.setVerticalScrollBarEnabled(false);
		/* give the second web view a id */
		mWebViewFifth.setId(BROWSER5_ID);
		/* initialize the absolute layout */
		AbsoluteLayout mWebViewLayout = (AbsoluteLayout) findViewById(R.id.browsertheme3);

		/* set the onclick listener for the web views */

		mWebViewFisrt.setOnTouchListener(browserOnClickListener);
		mWebViewSecond.setOnTouchListener(browserOnClickListener);
		mWebViewThird.setOnTouchListener(browserOnClickListener);
		mWebViewFourth.setOnTouchListener(browserOnClickListener);
		mWebViewFifth.setOnTouchListener(browserOnClickListener);

		/* setting of web vie client for the second web view end */
		try {
			/* open a connection for the database */
			mDatabase = this.openOrCreateDatabase(WEBVIEW_DATABASE,
					MODE_PRIVATE, null);
			/* take all the url from the database of inbuilt webview.db database */
			Cursor mGetRecentWebCursor = mDatabase.rawQuery(
					"SELECT url FROM formurl ;", null);
			/* take the last one */
			mGetRecentWebCursor.moveToLast();
			/* if atleast one entry is there in webview.db database */
			if (mGetRecentWebCursor.getCount() > 0) {
				mGetRecentWebCursor.moveToLast();
				/* take the address of the last opened web browser */
				mWebAddressFirst = mGetRecentWebCursor.getString(0);
				/* load the url in the first webview */
				mWebViewFisrt.loadUrl(mWebAddressFirst);
				/* add the first web view to the absolute layout */
				mWebViewLayout.addView(mWebViewFisrt,
						new AbsoluteLayout.LayoutParams(125, 100, 25, 20));
			}
			if (mGetRecentWebCursor.getCount() > 1) {
				/* move the cursor to the 2nd last position */
				mGetRecentWebCursor.moveToPrevious();
				/* take the address */
				mWebAddressSecond = mGetRecentWebCursor.getString(0);
				/* load the address of 2nd open web address */
				mWebViewFisrt.loadUrl(mWebAddressSecond);
				/* add the second web view to the absolute layout */
				mWebViewLayout.addView(mWebViewSecond,
						new AbsoluteLayout.LayoutParams(125, 100, 315, 20));

			}
			if (mGetRecentWebCursor.getCount() > 2) {
				/* move the cursor to the 3rd last position */
				mGetRecentWebCursor.moveToPrevious();
				/* take the address */
				mWebAddressThird = mGetRecentWebCursor.getString(0);
				/* load the address of 3rd open web address */
				mWebViewThird.loadUrl(mWebAddressThird);
				/* add the 3rd web view to the absolute layout */
				mWebViewLayout.addView(mWebViewThird,
						new AbsoluteLayout.LayoutParams(125, 100, 165, 140));

			}
			if (mGetRecentWebCursor.getCount() > 3) {
				/* move the cursor to the 4th last position */
				mGetRecentWebCursor.moveToPrevious();
				/* take the address */
				mWebAddressFourth = mGetRecentWebCursor.getString(0);
				/* load the address of 4th open web address */
				mWebViewFourth.loadUrl(mWebAddressFourth);
				/* add the 4th web view to the absolute layout */
				mWebViewLayout.addView(mWebViewFourth,
						new AbsoluteLayout.LayoutParams(125, 100, 25, 260));

			}
			if (mGetRecentWebCursor.getCount() > 4) {
				/* move the cursor to the 5th last position */
				mGetRecentWebCursor.moveToPrevious();
				/* take the address */
				mWebAddressFifth = mGetRecentWebCursor.getString(0);
				/* load the address of 5th open web address */
				mWebViewFifth.loadUrl(mWebAddressFifth);
				/* add the 5th web view to the absolute layout */
				mWebViewLayout.addView(mWebViewFifth,
						new AbsoluteLayout.LayoutParams(125, 100, 315, 260));

			}
			mGetRecentWebCursor.close();
		} catch (Exception e) {
			e.getStackTrace();

		}

		/* initialize and set web view client for the web view first */
		webviewSetUp(mWebViewFisrt, mWebAddressFirst);
		/* initialize and set web view client for the web view first */
		webviewSetUp(mWebViewSecond, mWebAddressSecond);
		/* initialize and set web view client for the web view first */
		webviewSetUp(mWebViewFisrt, mWebAddressThird);
		/* initialize and set web view client for the web view first */
		webviewSetUp(mWebViewSecond, mWebAddressFourth);
		/* initialize and set web view client for the web view first */
		webviewSetUp(mWebViewFisrt, mWebAddressFifth);

	}

	/* this function is used to set up the web crome client */
	public void webviewSetUp(WebView webview, String webAddress) {
		webview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {

			}
		});
		/* WebViewClient must be set BEFORE calling loadUrl! */
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				/*
				 * This call inject JavaScript into the page which just finished
				 * loading.
				 */

			}
		});
		/* JavaScript must be enabled if you want it to work, obviously */
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDefaultZoom(ZoomDensity.FAR);
		webview.zoomOut();
		webview.setInitialScale(3);

		/* load a web page */
		webview.loadUrl(webAddress);
	}

	/* This code is to return in home using Esc key from Key */
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		switch (KeyCode) {
		case KeyEvent.KEYCODE_BACK:
			/* finish the current activity */
			EpubReader.this.finish();
			/*
			 * this intent will use to finish the home page which is in the
			 * background
			 */
			Intent mFinishHomePage = new Intent(Intent.ACTION_DEFAULT);
			/* put intent to finish the home page */
			mFinishHomePage.putExtra("FinishActiveWindow", 1);
			/* send the broadcast to home page */
			sendBroadcast(mFinishHomePage);

			/* when the application close drop all unnecessary tables */
			mDb.dropTables();
			mDb.resetCabinetCheckBox();

			return true;
		}
		return false;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadCast);
	}
}