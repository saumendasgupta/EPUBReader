
package com.sdg.organizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.QuikE.R;

// This class is for displaying the Confirmation of moving of note
public class MoveInfo extends Activity {
	/** Called when the activity is first created. */

	// Text to display the confirmation message 
	private TextView mInfoMessageText;
	private RelativeLayout mInfoDialog;
	
	// String holds the name of category to which the note is moved
	String mInfoMessage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.movetoinfo);
	
		// Receiving category name from the 
		Bundle mBundle = MoveInfo.this.getIntent().getExtras();
		if (mBundle != null) {

			// Receiving the message to be displayed
			if (mBundle.containsKey("infomessageCL_key")) {
				mInfoMessage = mBundle.getString("infomessageCL_key");
			}
		}
		// Setting the message text 
		mInfoMessageText = (TextView)findViewById(R.id.infomessage);
		mInfoMessageText.setText(mInfoMessage);
		
		// Relative Layout.. clicking on which will close the dialog
		mInfoDialog = (RelativeLayout)findViewById(R.id.info_dialog);
		mInfoDialog.setOnClickListener(new View.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				MoveInfo.this.finish();
			}
			
		});
	}
}
