
package com.sdg.EPUBReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.android.QuikE.R;

public class SettingChange extends Activity {
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* code to hide the status bar */
		/*get the default window*/
		final Window win = getWindow();
		/*get the default hight*/
		final int screenHeight = win.getWindowManager().getDefaultDisplay()
				.getHeight();
		/*get the defaulut width*/
		final int screenWidth = win.getWindowManager().getDefaultDisplay()
				.getWidth();

		if ((screenHeight > 1 && screenWidth > 1)
				|| (screenHeight == EpubReader.SCREEN_HIGHT && screenWidth == EpubReader.SCREEN_WIDTH)) { // No
			/* Statusbar */
			win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN); /* No Titlebar */
			/*no title bar*/
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			requestWindowFeature(Window.FEATURE_PROGRESS);
		}
		/* code end for hiding the status bar */

		setContentView(R.layout.settingchange);
		/* this button is initialized */
		Button mSeetingChange = (Button) findViewById(R.id.managehome);
		/*initialize the close button foe setting change*/
		ImageView mCloseSettingchange=(ImageView)findViewById(R.id.homesettingheadericon2);
		/*on click onthat close button this setting change will close*/
		mCloseSettingchange.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				/*finish the setting chane activity*/
				SettingChange.this.finish();
				
			}
		});
		
		/* on click on the setting button manage home screen will come */
		mSeetingChange.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/*
				 * first change the background of that button to show that this
				 * button is selected
				 */
				v.setBackgroundResource(R.drawable.homeitemstabselected);
				/*create a intent*/
				Intent intent = new Intent(SettingChange.this, ManageHome.class);
				/*finish the setting chane activity*/
				SettingChange.this.finish();
				/* start the Manage Home Activity */
				startActivity(intent);
			}
		});

	}

	/* This code is to return in home */
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		switch (KeyCode) {
		case KeyEvent.KEYCODE_BACK:
			/*finish the current activity*/
			finish();
			/*start the home page*/
			startActivity(new Intent(SettingChange.this, EpubReader.class));
			return true;
		}
		return false;

	}

}
