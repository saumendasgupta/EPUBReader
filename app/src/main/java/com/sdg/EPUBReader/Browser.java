
package com.sdg.EPUBReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.android.QuikE.R;

public class Browser extends Activity {
	/*this web view is used to show the browser*/
	WebView mWebView;
	/*to store the go to address link*/
	static String mPageLink;
	/*this variable is used to create a object of DataBase Class*/
	DataBaseClass mDb;
	/*this variable is used to store the web page address*/
	String mWedAddress;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browser);
		/*
		 * create a bundle to take the address from the home page of the address
		 * of browser path
		 */
		Bundle mResentBrowserBundle = this.getIntent().getExtras();
		/* create a object of database class- */
		mDb = new DataBaseClass(Browser.this);
		/* check if any path came from home page */
		if (mResentBrowserBundle != null) {
			/* get the address from the home page */
			mWedAddress = mResentBrowserBundle.getString("Browser");
		} else {
			/* set the web address as default google */
			mWedAddress = "http://www.google.com";
		}

		/* this grid is for showing the header icons */
		GridView mBrowserplaceholerg = (GridView) findViewById(R.id.placehold);
		/* this place holder is used to show the search and other icons */
		mBrowserplaceholerg.setAdapter(new AdapterPlaceHold(this));
		/* on click on the place holder this action will done */
		mBrowserplaceholerg.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> AdapterPlaceHold, View v,
					int position, long id) {

			}

		});
		RelativeLayout mPageHeader = (RelativeLayout) findViewById(R.id.header);
		/* set the onclick listener for the header bar */
		mPageHeader.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/* create the intent for the active window class */
				Intent mActiveIntent = new Intent(Browser.this,
						ActiveWindow.class);
				EpubReader.mFromWhichPage = EpubReader.BROWSER;
				/* set the extra information for the active window class */
				mActiveIntent.putExtra("FromHome", 1);
				/* start the activity */
				startActivityForResult(mActiveIntent, 11);

				// for animation on opening activewindow
				overridePendingTransition(R.anim.push_down_in, 0);

			}
		});
		/* create a object of web view */
		mWebView = (WebView) findViewById(R.id.browserShowingWebView);
		/* create a object for the arrow image */
		ImageView mFindBrowserImage = (ImageView) findViewById(R.id.FindAddress);
		/* create a object of find address edit box */
		final EditText mFindBroeserEditbox = (EditText) findViewById(R.id.searchAddressEditText);

		/* on click of the arrow image this action will work */
		mFindBrowserImage.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				mWedAddress = mFindBroeserEditbox.getText().toString();
				webviewSetUp(mWebView, mWedAddress);
				mFindBroeserEditbox.setText("");

			}

		});

		final Activity activity = this;
		webviewSetUp(mWebView, mWedAddress);
		mWedAddress = null;

	}

	/* An instance of this class will be registered as a JavaScript interface */
	class MyJavaScriptInterface {
		@SuppressWarnings("unused")
		public void showHTML(String html) {
			Context myApp = Browser.this;
			
			
		}
	}

	/* this adapter will show serach,sort icons in place holder */
	public class AdapterPlaceHold extends BaseAdapter {
		public AdapterPlaceHold(Context c) {
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
				imageView.setLayoutParams(new GridView.LayoutParams(33, 33));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);

			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(mThumbIds1[position]);

			return imageView;
		}

		private Context mContext1;

		private Integer[] mThumbIds1 = { R.drawable.sorticon,
				R.drawable.settingsiconforlibgrid,
				R.drawable.searchiconforlibgrid,
				R.drawable.cabinetorganizericon, R.drawable.bookmarkicon,
				R.drawable.cabinetsendtoicon

		};

	}

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
				Log.e("Error on receive" + failingUrl, "" + description);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				/* insert into active window table also */
				Cursor mActiveWindowCursor = mDb.BrowserPresentInActiveTable();
				/* if no book present in active window table */
				if (mActiveWindowCursor.getCount() == 0) {
					mDb.insertInActiveWindow(url, url, "Browser");
					Log.e("In Browser Insert",
							"mActiveWindowCursor.getCount()="
									+ mActiveWindowCursor.getCount());
				}
				/*
				 * if already one book is there in active window just update the
				 * required field
				 */
				else if (mActiveWindowCursor.getCount() > 0) {
					mDb.updateActiveWindowForBrowser(url, url);
					Log.e("In Browser Update",
							"mActiveWindowCursor.getCount()="
									+ mActiveWindowCursor.getCount());
				}
				mActiveWindowCursor.close();
			}
		});
		/* JavaScript must be enabled if you want it to work, obviously */
		webview.getSettings().setJavaScriptEnabled(true);

		/* Register a new JavaScript interface called HTMLOUT */
		webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

		/* load a web page */
		webview.loadUrl(webAddress);
	}

	/* This code is to return in home using H key from Key */
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		switch (KeyCode) {
		case KeyEvent.KEYCODE_BACK:
			startActivity(new Intent(Browser.this, EpubReader.class));
			Browser.this.finish();

			return true;
		}
		return false;

	}
}
