
package com.sdg.EPUBReader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

import com.sdg.DisplayManager.Display_Manager;
import com.android.QuikE.R;

public class LibraryMainPage extends Activity {

	
	/* create a new intent for BroadCast */
	Intent mIntentMain = new Intent(Intent.ACTION_MAIN);
	/* create a object of Broadcast class */
	BroadcastReceiver mBroadCast = new BroadcastReceiver() {
		/* when message received to finish the home page this will execute */
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getIntExtra("FinishLibrayPage", DEFAULT_VALUE_ONE) == DEFAULT_VALUE_ONE) {
				LibraryMainPage.this.finish();
				
			}

		}
	};
	/*this variable is used to indicate the default value 1*/
	private final int DEFAULT_VALUE_ONE=1;
	/*
	 * this static variable is used to prevent
	 * Senario:Home->Library->BookDisplay->bookClose call again Library page
	 */
	public static int mPageToCall = EpubReader.RESET_PAGE;
	/* this variable will calculate the no of data in the databases */
	static int mIndex;
	/* this String is to store the book title from the Database */
	public static String bookdata[];
	/* this Cursor is use to connect with the database */
	Cursor mCursor;
	/* these Variable is used for Coloum index of BookInfoTable */
	private int BOOK_PATH = 1;
	/* coloum foe book's author */
	private int BOOK_AUTHOR = 2;
	/* coloum for book's title */
	private int BOOK_TITLE = 3;
	/* coloum for type of Epub,i.e Book or Magazine */
	private int TYPE = 4;
	/* the location of the book stored */
	private static final String BOOK_PATH_EPUB = new String("/sdcard/Books/");
	/* the location of the book stored */
	private static final String MAGAZINE_PATH_EPUB = new String(
			"/sdcard/Magazines/");
	/*
	 * this variable is used to show the total no of books and magazine present
	 * in sdcard
	 */

	public int mNoOfEpub = 0;

	// This class is for defining all the transitions and animations
	// Previously Page_Transition
//	Transitions transitions;
	// These image and text views are for showing the animation when an item in
	// the grid is selected
	ImageView mImageToTransit;
	TextView mTextToTransit;

	// This absolute layout is for holding the image and text views that are to
	// be transited or animated
	// This absolute layout is added to grid view on click of item
	AbsoluteLayout mTransitionContainer;

	// Relative Layout that holds the library grid view
	RelativeLayout mBookHolder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.librarypage_grid);

		// Object of Transitions class...
	//	transitions = new Transitions(this);

		mBookHolder = (RelativeLayout) findViewById(R.id.bookholder);

		// Creating the object of the Absolute layout on which the image and
		// text view are added to show the transition effect
		mTransitionContainer = new AbsoluteLayout(LibraryMainPage.this);
		// Setting layout params of absolutelayout
		mTransitionContainer.setLayoutParams(new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		// Creating object of Image and text to be animated/ transited
		mImageToTransit = new ImageView(LibraryMainPage.this);
		mTextToTransit = new TextView(LibraryMainPage.this);

		/* create a intent filter object */
		IntentFilter iFilter = new IntentFilter(Intent.ACTION_DEFAULT);
		/* register the receiver */
		registerReceiver(mBroadCast, iFilter);
		/*
		 * first check if from Senario:Home->Library->BookDisplay->bookClose
		 * book is closed then don't call library main page
		 */

		/* set that user in GRID view of library */
		EpubReader.mPageStatus = EpubReader.LIBRARY_GRID;
		/*
		 * this Static variable will be set to 0 as listview is opened ,this is
		 * usefull to show when come back from display manager if listview is
		 * open then it will strat listview or it will start grid view
		 */
		EpubReader.mIsListView = EpubReader.GRID_VIEW;
		/* this grid is for showing the books icons */

		final GridView mLibg = (GridView) findViewById(R.id.librarymainpage);
		/* this grid view is initialized which will show A-Z letter */
		GridView mLibBookLetter = (GridView) findViewById(R.id.librarybookmarkportion);

		/* set the adapter which will show the A-Z letters */
		mLibBookLetter.setAdapter(new AdapterForLibMainBookmark(this));

		/* this grid is for showing the header icons */
		GridView mLibplaceholerg = (GridView) findViewById(R.id.placehold);

		/* initialize the ListView image */
		ImageView mListViewChange = (ImageView) findViewById(R.id.listviewchange);
		/* initialize the GridView image */
		ImageView mGridViewChange = (ImageView) findViewById(R.id.thumbnailview);
		/* to show the no of books in the database */
		TextView mNoOfBooks = (TextView) findViewById(R.id.pageno);

		/* show the user that Grid View is Active */
		mGridViewChange.setBackgroundResource(R.drawable.gridviewactiveicon);

		/* set a onclick listener on that image */
		mListViewChange.setOnClickListener(new View.OnClickListener() {
			/* if click to change from grid view to list view */
			public void onClick(View v) {
				Intent mIntent = new Intent(LibraryMainPage.this,
						LibraryMainPageList.class);
				/* finish the current activity */
				LibraryMainPage.this.finish();
				/* start the list activity */
				startActivity(mIntent);
			}
		});
		/*
		 * First create a object of database class and fetch the data from the
		 * databases for books
		 */
		DataBaseClass mDbBook = new DataBaseClass(this);
		/* get the handler of the database class */
		mDbBook.mDatab = mDbBook.mDatah.getReadableDatabase();
		/* get a cursor */
		mCursor = mDbBook.returnCursor();
		/* move the cursor position to the first */
		mCursor.moveToFirst();
		/* get the no of element in the database */
		mIndex = mCursor.getCount();
		mNoOfEpub = mIndex;
		/* initialize the book data array */
		bookdata = new String[mIndex];
		/* set the coloum index */
		BOOK_TITLE = 2;
		/* this loop is for storing the book's title */
		for (int i = 0; i < mIndex; i++) {

			bookdata[i] = mCursor.getString(BOOK_TITLE);
			/* move the cursor to the next position* */
			mCursor.moveToNext();
		}
		mCursor.close();
		/* set the no of books present in the sdcard */
		mNoOfBooks.setText("    " + mNoOfEpub);
		/* in grid view to show all the books set the below adapter */
		mLibg.setAdapter(new AdapterForLibMain(this));

		/* on click on any books it will open the selected books */
		mLibg.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> AdapterForLibMain, View v,
					int position, long id) {

				// Removing all views from Absolutelayout before transition
				// effect is applied and removing the absolute layout from the
				// parent relative
				// layout
				mTransitionContainer.removeAllViews();
				mBookHolder.removeView(mTransitionContainer);

				// Adding absolute layout to relative layout after item click
				mBookHolder.addView(mTransitionContainer);

				// setting the backgroud of the image which is to be added to
				// absolute layout to show transition effect
				mImageToTransit
						.setBackgroundResource(R.drawable.thumbnailviewoption03);
				// setting the book name based on the position
				mTextToTransit.setText(bookdata[position]);
				mTextToTransit.setTextColor(Color.BLACK);

				// Calling method which defines the animation properties
	//			transitions.bookOpenInGrid(mTextToTransit, mImageToTransit,
	//					mTransitionContainer, v);

				// Applying fade out effect on the grid view in which the books
				// are shown
			//	Animation gridan = AnimationUtils.loadAnimation(
			//			LibraryMainPage.this, R.anim.fade_out);
			//	mLibg.startAnimation(gridan);

				/* create a object of the database class */
				DataBaseClass mDb = new DataBaseClass(LibraryMainPage.this);
				/* get the handler */
				mDb.mDatab = mDb.mDatah.getReadableDatabase();
				/* set the coloum index for the table for book path */
				BOOK_PATH = 1;
				/* set the coloum index for the table for book author */
				BOOK_AUTHOR = 2;
				/* set the coloum index for the table for book title */
				BOOK_TITLE = 3;
				/* Extract the file path according to the click on the grid */
				Cursor mExtractFilePathCursor;
				/* return the cursor fro a specific position */
				mExtractFilePathCursor = mDb.returnCursorForSpecificPos();
				mExtractFilePathCursor.moveToPosition(position);
				/* store the clicked book's File Path */
				String mFilePath = mExtractFilePathCursor.getString(BOOK_PATH);
				/* store the clicked book's Author Name */
				String mExtractedAuthor = mExtractFilePathCursor
						.getString(BOOK_AUTHOR);
				/* store the clicked book's Title */
				String mExtractedTitle = mExtractFilePathCursor
						.getString(BOOK_TITLE);
				/* get the type of epub,Book ao magazine */
				String mBookOrMagazine = mExtractFilePathCursor.getString(TYPE);
				/* close the cursor */
				mExtractFilePathCursor.close();

				Intent intent = new Intent(LibraryMainPage.this,
						Display_Manager.class);
				Bundle mBundle = new Bundle();
				/* sent the book's path */
				mBundle.putString("BookPath", mFilePath);
				intent.putExtras(mBundle);
				/* insert into Resect table about this book or Magazine */
				/* First check if it is book or magazine */
				/* if type is book insert in recent book table */
				if (mBookOrMagazine.equalsIgnoreCase("Book")) {
					mDb.recentBookOpendInsert(mFilePath, mExtractedAuthor,
							mExtractedTitle,
							DataBaseClass.RECENT_BOOK_DATABASE_TABLE);
					/* Insert in active window table first */

					mDb
							.insertInActiveWindow(mExtractedTitle, mFilePath,
									"Book");

				}
				/* or if type is magazine insert in recent magazine table */
				else if (mBookOrMagazine.equalsIgnoreCase("Magazine")) {
					mDb.recentBookOpendInsert(mFilePath, mExtractedAuthor,
							mExtractedTitle,
							DataBaseClass.RECENT_MAGAZINE_DATABASE_TABLE);

					/* insert the magazine in active window */
					mDb.insertInActiveWindow(mExtractedTitle, mFilePath,
							"Magazine");

				}

				/* finish this activity first */
				LibraryMainPage.this.finish();
				/* start book reading activity */
				startActivity(intent);

				// applying transition / animation between activities
				overridePendingTransition(R.anim.book_open, R.anim.no_amin);

			}

		});
		/* this place holder is used to show the search and other icons */
		mLibplaceholerg.setAdapter(new ImageAdapterPlaceHold(this));
		/* on click on the place holder this action will done */
		mLibplaceholerg.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> ImageAdapterPlaceHold,
					View v, int position, long id) {

			}

		});
		/*
		 * initialize the active window icon for the whole header bar and on
		 * click on the header bar any place active window will open
		 */
		RelativeLayout mPageHeader = (RelativeLayout) findViewById(R.id.header);

		mPageHeader.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent mActiveIntent = new Intent(LibraryMainPage.this,
						ActiveWindow.class);
				/* send the intent */
				mActiveIntent.putExtra("FromHome", 4);
				/* start the activity */
				startActivityForResult(mActiveIntent, 4);

				// for animation on opening activewindow
				overridePendingTransition(R.anim.push_down_in, 0);

			}
		});
		/*
		 * initialize the active window icon and on click on that active window
		 * will be open
		 */
		ImageView mActiveWindow = (ImageView) findViewById(R.id.activewindow);
		mActiveWindow.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent mActiveIntent = new Intent(LibraryMainPage.this,
						ActiveWindow.class);
				/* set the intent */
				mActiveIntent.putExtra("FromHome", 4);
				/* start the activity */
				startActivityForResult(mActiveIntent, 4);

				// for animation on opening activewindow
				overridePendingTransition(R.anim.push_down_in, 0);
			}
		});
	}

	/* this adapter is used to show the place holder on the above the screen */
	private static class AdapterForLibMain extends BaseAdapter {
		/* get a generic layout */
		private LayoutInflater mInflater;
		private Bitmap mIcon[];
		// this icons has a white written place in library
		// this icons has written place at down
		private Integer[] mThumbIds = { R.drawable.thumbnailviewoption03 };

		public AdapterForLibMain(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			/* get a generic layout */
			mInflater = LayoutInflater.from(context);
			/* initialize the bitmap */
			Bitmap mIcon1[] = new Bitmap[mIndex];
			/* this variable is to generate a random number */
			int random_number;
			/* set the images in the array at a random */
			for (int j = 0; j < mIndex; j++) {
				random_number = (int) (Math.random() * mThumbIds.length);
				mIcon1[j] = BitmapFactory.decodeResource(
						context.getResources(), mThumbIds[random_number]);

			}

			mIcon = mIcon1;

		}

		/* retuen the no of element in database */
		public int getCount() {
			return mIndex;

		}

		/* return the position of the adapter which was clicked */
		public Object getItem(int position) {
			return position;
		}

		/* get the id of the selected element */
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			/* create a object of the viewholder class */
			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.homeimageanddataforgrid, null);

				holder = new ViewHolder();
				/* initialize the textview */
				holder.text = (TextView) convertView.findViewById(R.id.text);
				/* set the color */
				holder.text.setTextColor(Color.BLACK);
				/* initialize the image view */
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				/* give a tag to every object */
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			/* set the icon as appropiate position */
			holder.icon.setImageBitmap(mIcon[position]);
			/* set the text for the appropiate position */
			holder.text.setText(bookdata[position]);
			/* set the size of the text view */
			holder.text.setTextSize(14);
			return convertView;
		}

		/* this class is usefull to show one image and one text in a grid view */
		static class ViewHolder {
			TextView text;
			ImageView icon;
		}
	}

	/* this adapter will show serach,sort icons in place holder */
	public class ImageAdapterPlaceHold extends BaseAdapter {
		public ImageAdapterPlaceHold(Context c) {
			mContext1 = c;
		}

		/* return the length */
		public int getCount() {

			return mThumbIds1.length;

		}

		/* return the clicked icon position */
		public Object getItem(int position) {

			return position;
		}

		/* return the clicked icons id */
		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext1);

			if (convertView == null) {
				imageView = new ImageView(mContext1);
				imageView.setLayoutParams(new GridView.LayoutParams(48, 48));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);

			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(mThumbIds1[position]);

			return imageView;
		}

		private Context mContext1;

		private Integer[] mThumbIds1 = { R.drawable.disabledfootersearchicon,
				R.drawable.disabledsorticon,
				R.drawable.disabledfootersettingsicon,
				R.drawable.disabledfavouriteicon, R.drawable.disabledtagicon,
				R.drawable.disabledmoveicon, R.drawable.disableddeleteicon

		};

	}

	/* this adapter will show list view and grid view change icons */
	public class ImageAdapterFooterHold extends BaseAdapter {
		public ImageAdapterFooterHold(Context c) {
			mContext = c;
		}

		/* return the length */
		public int getCount() {

			return mThumbIds.length;

		}

		/* return the clicked items position */
		public Object getItem(int position) {

			return position;
		}

		/* return the clicked icons position */
		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext);

			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(128, 128));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(mThumbIds[position]);

			return imageView;
		}

		private Context mContext;
		/* this array will contain grid view and list view icon */
		private Integer[] mThumbIds = { R.drawable.listviewchange,
				R.drawable.thumbnail,

		};

	}

	/* this adapter is used to show A-Z in right side */
	/* this adapter will show serach,sort icons in place holder */
	public class AdapterForLibMainBookmark extends BaseAdapter {
		public AdapterForLibMainBookmark(Context c) {
			mContext1 = c;
		}

		/* return the length */
		public int getCount() {

			return mThumbIds1.length;

		}

		/* return the clicked icon position */
		public Object getItem(int position) {

			return position;
		}

		/* return the clicked icons id */
		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext1);

			if (convertView == null) {
				imageView = new ImageView(mContext1);
				imageView.setLayoutParams(new GridView.LayoutParams(34, 34));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.FIT_START);

			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(mThumbIds1[position]);

			return imageView;
		}

		private Context mContext1;

		private Integer[] mThumbIds1 = { R.drawable.a, R.drawable.b,
				R.drawable.c, R.drawable.d, R.drawable.e,R.drawable.f, 
				R.drawable.g, R.drawable.h, R.drawable.i,R.drawable.j,
				R.drawable.k, R.drawable.l, R.drawable.m,R.drawable.n, 
				R.drawable.o, R.drawable.p,R.drawable.q,R.drawable.r,
				R.drawable.s, R.drawable.t,R.drawable.u, 
				R.drawable.v, R.drawable.w, R.drawable.x,
				R.drawable.y, R.drawable.z

		};

	}

	/* This code is to return in home using H key from Key */
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		switch (KeyCode) {
		case KeyEvent.KEYCODE_BACK:
			/* finish the current activity */
			LibraryMainPage.this.finish();
			/* start the home page */
			startActivity(new Intent(LibraryMainPage.this, EpubReader.class));
			return true;
		}
		return false;

	}

}
