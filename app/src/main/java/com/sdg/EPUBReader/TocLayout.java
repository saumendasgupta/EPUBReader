
package com.sdg.EPUBReader;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView.OnItemClickListener;

import com.android.QuikE.R;

public class TocLayout extends ActivityGroup {

	/* this variable is used to start TOC activity inside tocLayout Activity */
	public static LocalActivityManager mLocalActivityManager;
	/* this variable is used to store SD card Book Path */
	String mBookPath;
	/* this variable is used to store Books's OPF path */
	String mOpfPath;
	/* this imageview is used to open Active Window from this page */
	ImageView mGotoActiveWindow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * get the bundle from toclayout path and store the sd Card Book Path
		 * and OPF path
		 */
		Bundle mBundle = this.getIntent().getExtras();
		/*get the book path*/
		mBookPath = mBundle.getString("BookPath");
		/*get the opf path*/
		mOpfPath = mBundle.getString("OpfPath");

		/* code to hide the status bar */
		/*get the default hight of the window*/
		final Window win = getWindow();
		/*get the screen hight*/
		final int screenHeight = win.getWindowManager().getDefaultDisplay()
				.getHeight();
		/*get the screen width*/
		final int screenWidth = win.getWindowManager().getDefaultDisplay()
				.getWidth();

		if ((screenHeight > 1 && screenWidth > 1)
				|| (screenHeight == EpubReader.SCREEN_HIGHT && screenWidth == EpubReader.SCREEN_WIDTH)) {
			/* No Status bar */
			win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN); /* No Title bar */
			/*no title and no status bar*/
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			requestWindowFeature(Window.FEATURE_PROGRESS);
		}
		/* code for status bar hiding end */
		/*set the layout for the toc layout*/
		setContentView(R.layout.toclayout);
		/* code for calling sub activity for list view */

		/*
		 * create bundle of SD card card of the book and OPF path and send it to
		 * Display Manager
		 */
		Bundle mBundleBack = new Bundle();
		/*put the book paths*/
		mBundleBack.putString("BookPath", mBookPath);
		/*put the opf path*/
		mBundleBack.putString("OpfPath", "mOpfPath");
		/*create a intent*/
		Intent mScrollList = new Intent(this, TOC.class);
		mScrollList.putExtras(mBundle);
		/*
		 * get the local activity activity of toc layout,call the TOC
		 * activity,and start TOC activity,Change it to view and add to
		 * toclayout as a sub activity
		 */
		mLocalActivityManager = TocLayout.this.getLocalActivityManager();
		/*start the sub activity */
		View mScrollListView = mLocalActivityManager.startActivity(
				"ScrollList", mScrollList).getDecorView();
		/*get the default window manager*/
		WindowManager w = getWindowManager();
		Display d = w.getDefaultDisplay();
		/*get the hight*/
		int height = d.getHeight();
		/*set the layout param*/
		mScrollListView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, height));

		RelativeLayout mRelLayout = (RelativeLayout) findViewById(R.id.bookholder);
		mRelLayout.addView(mScrollListView);
		/* code end for sub activity */
		/*this grid view will show back to booresultCodek option*/
		GridView mLibplaceholerg = (GridView) findViewById(R.id.placeholderfortoc);
		/*set a adapter on this grid view*/
		mLibplaceholerg.setAdapter(new ImageAdapterPlaceHold_list(this));
		/*set the on click listener on that grid view*/
		mLibplaceholerg.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> ImageAdapterPlaceHold,
					View v, int position, long id) {
				/* back to bookview page*/
				if (position == 0) {
					setResult(1);
					/*finish the current activity*/
					finish();

				}
			}

		});
		/*on click on Active window image active window screen will open*/
		mGotoActiveWindow = (ImageView) findViewById(R.id.activewindow);
		
		/*set the on click listener on the active window image*/
		mGotoActiveWindow.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/*create a intent*/
				Intent mGotoActiveWindowIntent = new Intent(TocLayout.this,
						ActiveWindow.class);
				/*start the active window activity*/
				startActivity(mGotoActiveWindowIntent);
				// for animation on opening activewindow
				overridePendingTransition(R.anim.push_down_in,0);
			}
		});
	}

	/*this adapter is used to show back to book,search,annotate etc icons*/
	public class ImageAdapterPlaceHold_list extends BaseAdapter {
		public ImageAdapterPlaceHold_list(Context c) {
			/*current context*/
			mContext1 = c;
		}
		/*return the count*/
		public int getCount() {

			return mThumbIds1.length;

		}
		/*return the selected item position*/
		public Object getItem(int position) {

			return position;
		}
		/*return the selected item's id*/
		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext1);

			if (convertView == null) {
				imageView = new ImageView(mContext1);
				imageView.setLayoutParams(new GridView.LayoutParams(48, 48));

			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(mThumbIds1[position]);

			return imageView;
		}

		private Context mContext1;
		/*this array will store backtobook,search,annotate,hightlight option*/
		private Integer[] mThumbIds1 = { R.drawable.tocbacktobookicon,
				R.drawable.searchicon, R.drawable.annotateicon,
				R.drawable.tochighlighter

		};

	}

	/* This code is to return in home using H key from KeyBoard*/
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		switch (KeyCode) {
		case KeyEvent.KEYCODE_BACK:
			/*finish the current activity*/
			finish();
			/*start the home page*/
			startActivity(new Intent(TocLayout.this, EpubReader.class));
			return true;
		}
		return false;

	}

}
