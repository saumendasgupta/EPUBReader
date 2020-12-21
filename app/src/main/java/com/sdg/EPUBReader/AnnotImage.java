
package com.sdg.EPUBReader;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.QuikE.R;

/*
 * AnnotImage Class is used for only showing Hand written and KeyPad Text Input
 */
public class AnnotImage extends Activity {

	/*
	 * Relative layout for showing Hand written Text and Keypad Text
	 */
	RelativeLayout mImageLayout;
	public final int TEXT_SIZE = 20;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.annotateimage);
		/*
		 * For showing the Hand written Text
		 */
		ImageView mImage;
		/*
		 * Close Button
		 */
		ImageView mClose;
		/*
		 * For showing KeyPad Text Input
		 */
		TextView mKeyPadText;
		int mAnnotID;
		String mTableName;

		mImageLayout = (RelativeLayout) findViewById(R.id.SaveAsPlace);
		mImage = new ImageView(this);
		AbsoluteLayout mAbsLayout = new AbsoluteLayout(this);

		mKeyPadText = (TextView) findViewById(R.id.KeyPadText);
		mClose = (ImageView) findViewById(R.id.closeAnnotate);

		Bundle mBundle = new Bundle();
		mBundle = AnnotImage.this.getIntent().getExtras();
		mAnnotID = mBundle.getInt("ID");
		mTableName = mBundle.getString("TABLENAME");

		DataBaseClass mDataBaseClass = new DataBaseClass(this);
		mDataBaseClass.mDatab = mDataBaseClass.mDatah.getReadableDatabase();

		// for Handwritten Text
		Cursor mAnnotCursorImage = mDataBaseClass.selectBlobFromAnnotable(
				mAnnotID, mTableName);
		mAnnotCursorImage.moveToFirst();
		/*
		 * Storing blob in an Byte Array to display
		 */
		byte[] img = mAnnotCursorImage.getBlob(0);

		mImage
				.setImageBitmap(BitmapFactory.decodeByteArray(img, 0,
						img.length));
		mImage
				.setLayoutParams(new AbsoluteLayout.LayoutParams(426, 320, 0,
						15));

		mImageLayout.addView(mAbsLayout);
		mAbsLayout.addView(mImage);

		// for KeyPad Text
		Cursor mAnnotCursorText = mDataBaseClass.selectTextFromAnnotable(
				mAnnotID, mTableName);
		mAnnotCursorText.moveToFirst();
		String mText = mAnnotCursorText.getString(0);
		mKeyPadText.setTextSize(TEXT_SIZE);
		mKeyPadText.setTextColor(Color.BLUE);
		mKeyPadText.setText("   " + mText);

		/*
		 * Closing this Activity
		 */
		mClose.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Finishing current activity
				AnnotImage.this.finish();
			}
		});

	}
}
