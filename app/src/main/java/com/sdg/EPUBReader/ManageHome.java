
package com.sdg.EPUBReader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.QuikE.R;

public class ManageHome extends Activity {

	/* create a new intent for BroadCast */
	Intent mIntentMain = new Intent(Intent.ACTION_DEFAULT);
	/* create a object of Broadcast class */
	BroadcastReceiver pbr = new BroadcastReceiver() {
		/* on receive of a broadcast message this method will invoke */
		@Override
		public void onReceive(Context context, Intent intent) {

		}
	};

	/** Called when the activity is first created. */

	/* this variable will store theme ID no */
	static int mThemeNo;
	/* to store the theme color no */
	static int mThemeColor = 0;
	/* three different color is used,Blue indicate 0 */
	protected final int BLUE = 0;
	/* red indicate 1 */
	protected final int RED = 1;
	/* green indicate 2 */
	protected final int GREEN = 2;
	ImageView mSelectedTheme;
	/* gridview object */
	GridView mTheme;
	/* take a cursor object */
	DataBaseClass mDb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* code to hide the status bar */

		final Window win = getWindow();
		/* get the default hight */
		final int screenHeight = win.getWindowManager().getDefaultDisplay()
				.getHeight();
		/* get the default width */
		final int screenWidth = win.getWindowManager().getDefaultDisplay()
				.getWidth();
		if ((screenHeight > 1 && screenWidth > 1)
				|| (screenHeight == EpubReader.SCREEN_HIGHT && screenWidth == EpubReader.SCREEN_WIDTH)) { // No
			// Statusbar
			win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN); // No Titlebar
			/* no title bar wiil be shown */
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			requestWindowFeature(Window.FEATURE_PROGRESS);
		}

		/* code end for hiding status bar */
		/* set the layout for the manage home */
		setContentView(R.layout.managehome);

		/* initialize the color imageview and apply button */
		ImageView BackGroundBlue = (ImageView) findViewById(R.id.backgroundColor1);
		ImageView BackGroundRed = (ImageView) findViewById(R.id.backgroundColor2);
		ImageView BackGroundGreen = (ImageView) findViewById(R.id.backgroundColor3);
		ImageView ApplyColor = (ImageView) findViewById(R.id.ApplyBackground);
		/* initialize the close button for ManageHome change */
		ImageView mCloseSettingchange = (ImageView) findViewById(R.id.homesettingheadericon2);
		/* on click onthat close button this ManageHome change will close */
		mCloseSettingchange.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/* finish the ManageHome activity */
				ManageHome.this.finish();

			}
		});
		/* create a object of a database class */
		mDb = new DataBaseClass(this);

		/* if click on first color */
		BackGroundBlue.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				mThemeColor = BLUE;
				/* first check on which theme is selected */
				if (mThemeNo == EpubReader.THEME1) {
					/* set the red theme for theme1 */
					mSelectedTheme
							.setBackgroundResource(R.drawable.mylibraryhighlightedbigger);
					mTheme.setAdapter(new ImageAdapterMyLibrarySelected(
							ManageHome.this));
				}
				if (mThemeNo == EpubReader.THEME2) {
					/* set the red theme for theme2 */
					mSelectedTheme
							.setBackgroundResource(R.drawable.organizerhighlightedbigger);
					mTheme.setAdapter(new ImageAdapterOrganizerSelected(
							ManageHome.this));
				}
				if (mThemeNo == EpubReader.THEME3) {
					/* set the red theme for theme3 */
					mSelectedTheme
							.setBackgroundResource(R.drawable.browserhighlightedbigger);
					mTheme.setAdapter(new ImageAdapterBrowserSelected(
							ManageHome.this));
				}

			}
		});
		/* if click on second color */
		BackGroundRed.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				mThemeColor = RED;
				/* first check on which theme is selected */
				if (mThemeNo == EpubReader.THEME1) {
					/* set the red theme for theme1 */
					mSelectedTheme
							.setBackgroundResource(R.drawable.mylibraryhighlightedbiggerred);
					mTheme.setAdapter(new ImageAdapterMyLibrarySelected(
							ManageHome.this));
				}
				if (mThemeNo == EpubReader.THEME2) {
					/* set the red theme for theme2 */
					mSelectedTheme
							.setBackgroundResource(R.drawable.organizerhighlightedbiggerred);
					mTheme.setAdapter(new ImageAdapterOrganizerSelected(
							ManageHome.this));
				}
				if (mThemeNo == EpubReader.THEME3) {
					/* set the red theme for theme3 */
					mSelectedTheme
							.setBackgroundResource(R.drawable.browserhighlightedbiggerred);
					mTheme.setAdapter(new ImageAdapterBrowserSelected(
							ManageHome.this));
				}

			}
		});
		/* if click on third color */
		BackGroundGreen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				mThemeColor = GREEN;
				/* first check on which theme is selected */
				if (mThemeNo == EpubReader.THEME1) {
					/* set the red theme for theme1 */
					mSelectedTheme
							.setBackgroundResource(R.drawable.mylibraryhighlightedbiggergreen);
					/* set the left side adapter to show all in new color */
					mTheme.setAdapter(new ImageAdapterOrganizerSelected(
							ManageHome.this));
					mTheme.setAdapter(new ImageAdapterMyLibrarySelected(
							ManageHome.this));
				}
				if (mThemeNo == EpubReader.THEME2) {
					/* set the red theme for theme2 */
					mSelectedTheme
							.setBackgroundResource(R.drawable.organizerhighlightedbiggergreen);
					mTheme.setAdapter(new ImageAdapterOrganizerSelected(
							ManageHome.this));
				}
				if (mThemeNo == EpubReader.THEME3) {
					/* set the red theme for theme3 */
					mSelectedTheme
							.setBackgroundResource(R.drawable.browserhighlightedbiggergreen);
					mTheme.setAdapter(new ImageAdapterBrowserSelected(
							ManageHome.this));
				}

			}
		});

		/* if click on applybutton */
		ApplyColor.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				try {
					/* update the theme ID in DataBase Table */
					mDb.updateThemeId(mThemeNo, mThemeColor);

				} catch (SQLException e) {
					e.getStackTrace();
				}

				/* start the new activity with selected database */
				final Intent intent = new Intent(ManageHome.this, EpubReader.class);
				/* create a bunle */
				Bundle mBundle = new Bundle();
				/* put the theme no as extra */
				mBundle.putLong("ThemeId", mThemeNo);
				mBundle.putInt("ColorID", mThemeColor);
				intent.putExtras(mBundle);
				/* broadcast the message to close the previous home page */
				sendBroadcast(mIntentMain);
				/* finish the current Activity */
				ManageHome.this.finish();

				/* start the home page */
				startActivityForResult(intent, 0);

			}
		});
		/* create a object of intent flter class */
		IntentFilter piFilter = new IntentFilter(Intent.ACTION_MAIN);
		/* register the receiver */
		registerReceiver(pbr, piFilter);

		/* get the handler of the database class */
		mDb.mDatab = mDb.mDatah.getReadableDatabase();
		/* open the database to show the last selected theme */
		/* initilalize the gridview */
		mTheme = (GridView) findViewById(R.id.gridtheme);
		/* initialize the imageview */
		mSelectedTheme = (ImageView) findViewById(R.id.selectedtheme);
		/* set the last selected theme as highlighted */
		Cursor mTempCursor = mDb.selectThemeId();
		/* if atleast any element is in the database */
		if (mTempCursor.getCount() > 0) {
			/* move the cursor to the first position */
			mTempCursor.moveToFirst();
			/* get the theme no */
			mThemeNo = mTempCursor.getInt(0);
			/* get the previous Color selected */
			mThemeColor = mTempCursor.getInt(1);
			/* if theme 1 is selected the highlight it */
			if (mThemeNo == EpubReader.THEME1) {
				/* check also which color it was selected */
				if (mThemeColor == BLUE) {
					/* if color was selected as blue */
					mSelectedTheme
							.setBackgroundResource(R.drawable.mylibraryhighlightedbigger);
				}
				if (mThemeColor == RED) {
					/* if color was selected as red */
					mSelectedTheme
							.setBackgroundResource(R.drawable.mylibraryhighlightedbiggerred);
				}
				if (mThemeColor == GREEN) {
					/* if color was selected as green */
					mSelectedTheme
							.setBackgroundResource(R.drawable.mylibraryhighlightedbiggergreen);
				}
			}/* if theme 2 is selected the highlight it */
			if (mThemeNo == EpubReader.THEME2) {
				/* first check which color was selected */
				if (mThemeColor == BLUE) {
					/* if blue was selected */
					mSelectedTheme
							.setBackgroundResource(R.drawable.organizerhighlightedbigger);
				}
				if (mThemeColor == RED) {
					/* if red was selected */
					mSelectedTheme
							.setBackgroundResource(R.drawable.organizerhighlightedbiggerred);
				}
				if (mThemeColor == GREEN) {
					/* if green was selected */
					mSelectedTheme
							.setBackgroundResource(R.drawable.organizerhighlightedbiggergreen);
				}
			}/* if theme 3 is selected the highlight it */
			if (mThemeNo == EpubReader.THEME3) {
				/* first check previously which color was selected */
				if (mThemeColor == BLUE) {
					/* if blue color is selected */
					mSelectedTheme
							.setBackgroundResource(R.drawable.browserhighlightedbigger);
				}
				if (mThemeColor == RED) {
					/* if red color is selected */
					mSelectedTheme
							.setBackgroundResource(R.drawable.browserhighlightedbiggerred);
				}
				if (mThemeColor == GREEN) {
					/* if green color is selected */
					mSelectedTheme
							.setBackgroundResource(R.drawable.browserhighlightedbiggergreen);
				}
			}
		}

		/* to show downwards the selected theme1 as dotted */
		if (mThemeNo == EpubReader.THEME1) {
			mTheme.setAdapter(new ImageAdapterMyLibrarySelected(this));
		}/* to show downwards the selected theme2 as dotted */
		if (mThemeNo == EpubReader.THEME2) {
			mTheme.setAdapter(new ImageAdapterOrganizerSelected(this));
		}/* to show downwards the selected theme3 as dotted */
		if (mThemeNo == EpubReader.THEME3) {
			mTheme.setAdapter(new ImageAdapterBrowserSelected(this));
		}

		/*
		 * on click on the icons of theme1,theme 2 ,and theme3 icons it will
		 * first set the theme no in databse and start the application with
		 * changed theme
		 */
		mTheme.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> ImageAdapter, View v,
					int position, long id) {

				mThemeNo = position;
				/* to show downwards the selected theme1 as dotted */
				if (mThemeNo == EpubReader.THEME1) {
					/* first show the dotted image */
					mTheme.setAdapter(new ImageAdapterMyLibrarySelected(
							ManageHome.this));
					/* then show that theme in the middle* */
					if (mThemeColor == BLUE) {
						mSelectedTheme
								.setBackgroundResource(R.drawable.mylibraryhighlightedbigger);
					}
					if (mThemeColor == RED) {
						mSelectedTheme
								.setBackgroundResource(R.drawable.mylibraryhighlightedbiggerred);
					}
					if (mThemeColor == GREEN) {
						mSelectedTheme
								.setBackgroundResource(R.drawable.mylibraryhighlightedbiggergreen);
					}
				}/* to show downwards the selected theme2 as dotted */
				if (mThemeNo == EpubReader.THEME2) {
					mTheme.setAdapter(new ImageAdapterOrganizerSelected(
							ManageHome.this));
					if (mThemeColor == BLUE) {
						mSelectedTheme
								.setBackgroundResource(R.drawable.organizerhighlightedbigger);
					}
					if (mThemeColor == RED) {
						mSelectedTheme
								.setBackgroundResource(R.drawable.organizerhighlightedbiggerred);
					}
					if (mThemeColor == GREEN) {
						mSelectedTheme
								.setBackgroundResource(R.drawable.organizerhighlightedbiggergreen);
					}
				}/* to show downwards the selected theme3 as dotted */
				if (mThemeNo == EpubReader.THEME3) {
					mTheme.setAdapter(new ImageAdapterBrowserSelected(
							ManageHome.this));
					if (mThemeColor == BLUE) {
						mSelectedTheme
								.setBackgroundResource(R.drawable.browserhighlightedbigger);
					}
					if (mThemeColor == RED) {
						mSelectedTheme
								.setBackgroundResource(R.drawable.browserhighlightedbiggerred);
					}
					if (mThemeColor == GREEN) {
						mSelectedTheme
								.setBackgroundResource(R.drawable.browserhighlightedbiggergreen);
					}
				}

			}
		});
		/* this imageview is used to go back to setting change screen */
		ImageView mBackSetting = (ImageView) findViewById(R.id.managehomesettingImage);
		/* set the onclick listener on setting image */
		mBackSetting.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/* create a intent */
				Intent mIntent = new Intent(ManageHome.this,
						SettingChange.class);
				/* finish the current activity */
				ManageHome.this.finish();
				/* start the home page */
				startActivity(mIntent);
			}
		});
	}

	/*
	 * this adapter will set by default when no theme is selected ,it will set
	 * the first theme as default
	 */
	public class ImageAdapter extends BaseAdapter {
		public ImageAdapter(Context c) {
			/* current context */
			mContext = c;
		}

		/* return the no of element in array */
		public int getCount() {

			return mThumbIds.length;

		}

		/* return the clicked element position */
		public Object getItem(int position) {

			return position;
		}

		/* return the clicked element id */
		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext);

			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(96, 121));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

			} else {
				imageView = (ImageView) convertView;
			}
			/* set the images */
			imageView.setImageResource(mThumbIds[position]);

			return imageView;
		}

		/* declare the on click listener */
		public OnClickListener imagelistener = new OnClickListener() {

			public void onClick(View v) {

			}
		};
		private Context mContext;
		/* this array is for to store the three image of three themes */
		private Integer[] mThumbIds = { R.drawable.mylibrarynonselected,
				R.drawable.organizernonselected, R.drawable.browsernonselected

		};

	}

	/* this adapter is to show that my library is clicked */
	public class ImageAdapterMyLibrarySelected extends BaseAdapter {
		public ImageAdapterMyLibrarySelected(Context c) {
			/* current context */
			mContext = c;
		}

		/* get the element count */
		public int getCount() {

			return mThumbIds.length;

		}

		/* get the position */
		public Object getItem(int position) {

			return position;
		}

		/* get the itemid */
		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext);

			if (convertView == null) {
				/* set the view */
				imageView = new ImageView(mContext);
				/* set layout param */
				imageView.setLayoutParams(new GridView.LayoutParams(96, 121));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

			} else {
				imageView = (ImageView) convertView;
			}
			/* set image */
			/* first check which color is selected */
			/* if blue color is seleceted */
			if (mThemeColor == BLUE) {
				imageView.setImageResource(mThumbIds[position]);
			}
			/* if red color is seleceted */
			if (mThemeColor == RED) {
				imageView.setImageResource(mThumbIdsRed[position]);
			}
			/* if green color is seleceted */
			if (mThemeColor == GREEN) {
				imageView.setImageResource(mThumbIdsGreen[position]);
			}

			return imageView;
		}

		/* set on click listener */
		public OnClickListener imagelistener = new OnClickListener() {

			public void onClick(View v) {

			}
		};
		private Context mContext;
		/*
		 * this array is for to store the three image of three themes with
		 * library as selected, organizer and browse non selected
		 */
		/* this image array is for show in blue */

		private Integer[] mThumbIds = { R.drawable.mylibraryselected,
				R.drawable.organizernonselected, R.drawable.browsernonselected

		};
		/* this image array is for show in red */
		private Integer[] mThumbIdsRed = { R.drawable.mylibraryselectedred,
				R.drawable.organizernonselectedred,
				R.drawable.browsernonselectedred

		};
		/* this image array is for show in green */
		private Integer[] mThumbIdsGreen = { R.drawable.mylibraryselectedgreen,
				R.drawable.organizernonselectedgreen,
				R.drawable.browsernonselectedgreen

		};

	}

	/* this adapter is to show that organizer is clicked */
	public class ImageAdapterOrganizerSelected extends BaseAdapter {
		public ImageAdapterOrganizerSelected(Context c) {
			/* current context */
			mContext = c;
		}

		/* get the no of elements */
		public int getCount() {

			return mThumbIds.length;

		}

		/* get the item */
		public Object getItem(int position) {

			return position;
		}

		/* get the id */
		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext);

			if (convertView == null) {
				/* create a new image */
				imageView = new ImageView(mContext);
				/* set the layout param */
				imageView.setLayoutParams(new GridView.LayoutParams(96, 121));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

			} else {
				imageView = (ImageView) convertView;
			}
			/* set the icon */
			/* if blue color is seleceted */
			if (mThemeColor == BLUE) {
				imageView.setImageResource(mThumbIds[position]);
			}
			/* if red color is seleceted */
			if (mThemeColor == RED) {
				imageView.setImageResource(mThumbIdsRed[position]);
			}
			/* if Green color is seleceted */
			if (mThemeColor == GREEN) {
				imageView.setImageResource(mThumbIdsGreen[position]);
			}

			return imageView;
		}

		/* set the onclick listener */
		public OnClickListener imagelistener = new OnClickListener() {

			public void onClick(View v) {

			}
		};
		private Context mContext;
		/*
		 * this array is for to store the three image of three themes with
		 * library and browser not selected ,organizer as selected
		 */
		/* this image array is to show all small thumbnail in blue */
		private Integer[] mThumbIds = { R.drawable.mylibrarynonselected,
				R.drawable.organizerselected, R.drawable.browsernonselected

		};
		/* this image array is to show all small thumbnail in Red */
		private Integer[] mThumbIdsRed = { R.drawable.mylibrarynonselectedred,
				R.drawable.organizerselectedred,
				R.drawable.browsernonselectedred

		};
		/* this image array is to show all small thumbnail in Green */
		private Integer[] mThumbIdsGreen = {
				R.drawable.mylibrarynonselectedgreen,
				R.drawable.organizerselectedgreen,
				R.drawable.browsernonselectedgreen

		};
	}

	/* this adapter is to show that browser is clicked */
	public class ImageAdapterBrowserSelected extends BaseAdapter {
		public ImageAdapterBrowserSelected(Context c) {
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
				imageView.setLayoutParams(new GridView.LayoutParams(96, 121));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				
			} else {
				imageView = (ImageView) convertView;
			}
			/* if blue color is seleceted */
			if (mThemeColor == BLUE) {
				imageView.setImageResource(mThumbIds[position]);
			}
			/* if red color is seleceted */
			if (mThemeColor == RED) {
				imageView.setImageResource(mThumbIdsRed[position]);
			}
			/* if Green color is seleceted */
			if (mThemeColor == GREEN) {
				imageView.setImageResource(mThumbIdsGreen[position]);
			}

			return imageView;
		}

		public OnClickListener imagelistener = new OnClickListener() {

			public void onClick(View v) {

			}
		};
		private Context mContext;
		/*
		 * this array is for to store the three image of three themes with
		 * library and organizer not selected ,browser as selected
		 */
		/* this image array will store all images for blue color */
		private Integer[] mThumbIds = { R.drawable.mylibrarynonselected,
				R.drawable.organizernonselected, R.drawable.browserselected

		};
		/* this image array will store all images for red color */
		private Integer[] mThumbIdsRed = { R.drawable.mylibrarynonselectedred,
				R.drawable.organizernonselectedred,
				R.drawable.browserselectedred

		};
		/* this image array will store all images for green color */
		private Integer[] mThumbIdsGreen = {
				R.drawable.mylibrarynonselectedgreen,
				R.drawable.organizernonselectedgreen,
				R.drawable.browserselectedgreen

		};
	}

	/* This code is to return in home using H key from the KeyBoard */
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		switch (KeyCode) {
		case KeyEvent.KEYCODE_BACK:
			/* finish the current activity */
			finish();
			/* start the home page */
			startActivity(new Intent(ManageHome.this, EpubReader.class));
			return true;
		}
		return false;

	}

}
