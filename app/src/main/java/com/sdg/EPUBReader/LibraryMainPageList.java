
package com.sdg.EPUBReader;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.QuikE.R;

public class LibraryMainPageList extends ActivityGroup {
	/* create a new intent for BroadCast */
	Intent mIntentMain = new Intent(Intent.ACTION_MAIN);
	/* create a object of Broadcast class */
	BroadcastReceiver mBroadCast = new BroadcastReceiver() {
		/* when message received to finish the home page this will execute */
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getIntExtra("FinishLibrayPage", DEFAULT_VALUE_ONE) == DEFAULT_VALUE_ONE) {
				LibraryMainPageList.this.finish();
				
			}

		}
	};
	/*this string will be used to indicate the default value 1*/
	private final int DEFAULT_VALUE_ONE=1;
	/*this string will be used to indicate the default value 4*/
	private final int DEFAULT_VALUE_FOUR=4;
	protected static LocalActivityManager mLocalActivityManager;
	/* this Cursor is use to connect with the database */
	Cursor mCursor;
	/* this variable will be used show the total no of books */
	private int mIndex;

	/*
	 * this variable is used to show the total no of books and magazine present
	 * in sdcard
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* code to hide the status bar */
		/* get the dafault window hight */
		final Window win = getWindow();
		/* get the current window hight */
		final int screenHeight = win.getWindowManager().getDefaultDisplay()
				.getHeight();
		/* get the current window width */
		final int screenWidth = win.getWindowManager().getDefaultDisplay()
				.getWidth();

		if ((screenHeight > 1 && screenWidth > 1)
				|| (screenHeight == EpubReader.SCREEN_HIGHT && screenWidth == EpubReader.SCREEN_WIDTH)) { // No
			// Statusbar
			win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN); // No Titlebar
			/* no title bar and no status bar */
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			requestWindowFeature(Window.FEATURE_PROGRESS);
		}
		/* code for status bar hiding end */
		/* set the list view layout */
		setContentView(R.layout.librarymainpage_list);

		/* create a intent filter object */
		IntentFilter iFilter = new IntentFilter(Intent.ACTION_DEFAULT);
		/* register the receiver */
		registerReceiver(mBroadCast, iFilter);
		/* set that user in Library in ListView */
		EpubReader.mPageStatus = EpubReader.LIBRARY_LIST;
		/* first set the variable as listview is opened */
		EpubReader.mIsListView = EpubReader.LIST_VIEW;
		/* create the intent and call the sub activity */
		Intent mScrollList = new Intent(this, ListScroll.class);
		/* get the local activity manager */
		mLocalActivityManager = LibraryMainPageList.this
				.getLocalActivityManager();
		/* start the sub activity and convert it in a view */
		View mScrollListView = mLocalActivityManager.startActivity(
				"ScrollList", mScrollList).getDecorView();
		/* get the default hight */
		WindowManager w = getWindowManager();
		Display d = w.getDefaultDisplay();
		int height = d.getHeight();
		/* set the layout param */
		mScrollListView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, height));
		/* initialize the relative layout for the book s where it will be shown */
		RelativeLayout mRelLayout = (RelativeLayout) findViewById(R.id.bookholder);
		/* add the subactivity with this activity as a view */
		mRelLayout.addView(mScrollListView);
		/* code end for sub activity */
		/* initialize the grid view */
		GridView mLibplaceholerg = (GridView) findViewById(R.id.placehold);
		/* this grid view is initialized which will show A-Z letter */
		GridView mLibBookLetter = (GridView) findViewById(R.id.librarybookmarkportion);

		/* initialize the textview to show the pageno */
		TextView mNoOfBooks = (TextView) findViewById(R.id.pageno);
		mNoOfBooks.setText("" + mIndex);

		/* set the adapter which will show the A-Z letters */
		mLibBookLetter.setAdapter(new AdapterForLibMainBookmark(this));

		/* set a adapter on the palce holder */
		mLibplaceholerg.setAdapter(new ImageAdapterPlaceHold_list(this));
		/* set the on click listen */
		mLibplaceholerg.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> ImageAdapterPlaceHold,
					View v, int position, long id) {

			}

		});

		/*
		 * initialize the gridview switch icon and on click on that from list
		 * view it wil change to grid view
		 */
		/* initialize the grid view change theme */
		ImageView mGridViewChange = (ImageView) findViewById(R.id.thumbnailview);
		/* initialize the listviewicon to show it is hilighted */
		ImageView mListViewChange = (ImageView) findViewById(R.id.listviewchange);
		/* set the image as to show that list view is active */
		mListViewChange.setBackgroundResource(R.drawable.listviewactiveicon);

		/* set the on click listener on that imageview */
		mGridViewChange.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/* create a intent */
				Intent mIntent = new Intent(LibraryMainPageList.this,
						LibraryMainPage.class);
				/* finish the current acttivity */
				LibraryMainPageList.this.finish();
				/* start the grid activity */
				startActivity(mIntent);
			}
		});
		/*
		 * initialize the active window icon and on click on that active window
		 * will be open
		 */
		ImageView mActiveWindow = (ImageView) findViewById(R.id.activewindow);
		/* set the onclick listener on the active window image */
		mActiveWindow.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/* create a intent */
				Intent mActiveIntent = new Intent(LibraryMainPageList.this,
						ActiveWindow.class);
				mActiveIntent.putExtra("FromHome", DEFAULT_VALUE_FOUR);
				/* start the activity */
				startActivityForResult(mActiveIntent, DEFAULT_VALUE_FOUR);

				// for animation on opening activewindow
				overridePendingTransition(R.anim.push_down_in, 0);
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
		/* close the cursor */
		mCursor.close();
		/* set the total no of books */
		mNoOfBooks.setText("   " + mIndex);

		/*
		 * initialize the active window icon for the whole header bar and on
		 * click on the header bar any place active window will open
		 */
		RelativeLayout mPageHeader = (RelativeLayout) findViewById(R.id.header);
		/* set the on click listener on the whole layout */
		mPageHeader.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/* create a intent */
				Intent mActiveIntent = new Intent(LibraryMainPageList.this,
						ActiveWindow.class);
				mActiveIntent.putExtra("FromHome", DEFAULT_VALUE_FOUR);
				/* start the activity */
				startActivityForResult(mActiveIntent, DEFAULT_VALUE_FOUR);

				// for animation on opening activewindow
				overridePendingTransition(R.anim.push_down_in, 0);

			}
		});

	}

	/* this adapter is used to show the place holder on the above the screen */
	public class ImageAdapterPlaceHold_list extends BaseAdapter {
		public ImageAdapterPlaceHold_list(Context c) {
			/* current context */
			mContext1 = c;
		}

		/* get the count */
		public int getCount() {

			return mThumbIds1.length;

		}

		/* get the selected item position */
		public Object getItem(int position) {

			return position;
		}

		/* get the selected item id */
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
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);

			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(mThumbIds1[position]);

			return imageView;
		}

		private Context mContext1;

		private Integer[] mThumbIds1 = { R.drawable.a, R.drawable.b,
				R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.e,
				R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.i,
				R.drawable.j, R.drawable.k, R.drawable.l, R.drawable.m,
				R.drawable.n, R.drawable.n, R.drawable.o, R.drawable.p,
				R.drawable.q, R.drawable.r, R.drawable.s, R.drawable.t,
				R.drawable.u, R.drawable.v, R.drawable.w, R.drawable.x,
				R.drawable.y, R.drawable.z

		};

	}

	/* This code is to return in home using H key from the KeyBoard */
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		switch (KeyCode) {
		case KeyEvent.KEYCODE_BACK:
			/* finish the current activity */
			LibraryMainPageList.this.finish();
			/* start the home page */
			startActivity(new Intent(LibraryMainPageList.this, EpubReader.class));
			return true;
		}
		return false;

	}

}
