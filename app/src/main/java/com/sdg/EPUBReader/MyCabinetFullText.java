
package com.sdg.EPUBReader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.QuikE.R;

public class MyCabinetFullText extends Activity{
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*set the layout for the sharing screen*/
		setContentView(R.layout.cabinetfullscreentext);
		/*create a bindle to take the intent*/
		Bundle mBundle=this.getIntent().getExtras();
		/*take the text drom display manager*/
		String mFullCabinetText=mBundle.getString("FullText");
		/*initialize the textview and ImageView*/
		TextView mCabinetFullText=(TextView)findViewById(R.id.cabinetfulltext);
		ImageView mCabinetFullTextClose=(ImageView)findViewById(R.id.CloseText);
		mCabinetFullText.setText(mFullCabinetText);
		// Relative Layout.. clicking on which will close the dialog
		RelativeLayout mInfoDialog = (RelativeLayout)findViewById(R.id.info_dialog);
		mCabinetFullTextClose.setOnClickListener(new View.OnClickListener(){

			public void onClick(View v) {
				/* finish the full screen activity*/
				MyCabinetFullText.this.finish();
			}
			
		});
	}

}
