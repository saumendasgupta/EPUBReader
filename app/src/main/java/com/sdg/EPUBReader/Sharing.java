
package com.sdg.EPUBReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.android.QuikE.R;

public class Sharing extends Activity{
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*set the layout for the sharing screen*/
		setContentView(R.layout.sharing);
		/*initilaize the image view*/
		ImageView mCloseSharingView=(ImageView)findViewById(R.id.homesettingheadericon2);
		/*set the on click listener on thta image*/
		mCloseSharingView.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				/*finish the current activity*/
				finish();
				
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
			startActivity(new Intent(Sharing.this, EpubReader.class));
			return true;
		}
		return false;

	}
}
