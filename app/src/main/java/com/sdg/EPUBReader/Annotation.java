
package com.sdg.EPUBReader;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.QuikE.R;

public class Annotation extends Activity {

	/*
	 * Taking KeyBoard Text Input
	 */
	EditText mTextInput;
	/*
	 * Layout for Drawing
	 */
	FrameLayout mDrawLayout;
	/*
	 * It will store Handwritten Input
	 */
	/*
	 * Using Paint for drawing
	 */
	private Paint mPaint;
	public Bitmap mBitmap;
	public final int TEXT_WIDTH = 6;
	public final int DRAW_COLOR = 0xDB7093FF;
	public final int ACTIVITY_RETURN = 10000;
	public final int COLOR_WHITE = 0xFFFFFFFF;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.annotview);
		/*
		 * For Keypad Text Input
		 */
		mTextInput = (EditText) findViewById(R.id.SaveAsEditText);
		/*
		 * For Handwritten Text Input
		 */
		mDrawLayout = (FrameLayout) findViewById(R.id.DrawText);
		/*
		 * Save and exit from this view
		 */
		ImageView mClickToSaveText = (ImageView) findViewById(R.id.clicktosaveas);
		ImageView mClickToSaveImage = (ImageView) findViewById(R.id.clicktosaveas1);

		/*
		 * Adding the view with frameLayout which will be used for Drawing
		 */
		mDrawLayout.addView(new MyView(this));

		/*
		 * Using Paint for drawing
		 */
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(DRAW_COLOR);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.SQUARE);
		mPaint.setStrokeWidth(TEXT_WIDTH);
		/*
		 * Save and exit from this view
		 */
		mClickToSaveText.setOnClickListener(mSaveAs);
		mClickToSaveImage.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Annotation.this.finish();
				
			}
		});

	}

	/*
	 * To save KeyPad Text and Handwritten Text OnClick on Save Icon
	 */
	OnClickListener mSaveAs = new View.OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub

			String mAnnotText;
			byte[] mImage;
			int mAnnotId;
			int mChaptId;
			String mBookName;
			String mXmlLevel;
			int mOffset;
			int mTextLen;
			int mContiFlg;
			int mLineNumb;

			DataBaseClass mDataBaseClass = new DataBaseClass(Annotation.this);
			mDataBaseClass.mDatab = mDataBaseClass.mDatah.getReadableDatabase();

			// Hand written Text
			mAnnotText = mTextInput.getText().toString();

			// Image drawn
			// Conversion of bitmap in bytearray for data base storing
			ByteArrayOutputStream mByteArray = new ByteArrayOutputStream();
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, mByteArray);
			mImage = mByteArray.toByteArray();

			Bundle mBundle = new Bundle();
			mBundle = Annotation.this.getIntent().getExtras();

			mAnnotId = mBundle.getInt("ANNOT_ID");
			mChaptId = mBundle.getInt("ANNOT_CHPT_ID");

			// For specific Book
			mBookName = mBundle.getString("BOOK_NAME");

			for (int i = 0; i < mBundle.getInt("XMLLEVEL_COUNT"); i++) {
				mXmlLevel = mBundle.getString("ANNOT_XML_LEVEL" + i);
				mOffset = mBundle.getInt("ANNOT_OFFSET" + i);
				mTextLen = mBundle.getInt("ANNOT_WORDLEN" + i);
				mContiFlg = mBundle.getInt("ANNOT_CONTI_FLAG" + i);
				mLineNumb = mBundle.getInt("LINE_NUMB" + i);

				mDataBaseClass.insertAnnotDataInAnnotTable(mAnnotId,
						mAnnotText, mImage, mChaptId, mContiFlg, mXmlLevel,
						mOffset, mTextLen, mLineNumb, mBookName);

			}
			// Way of Taking the Entire Hand written Input as an Image
			// Storing as an Image on SD Card
			// Storing image as png file in SD Card
			// FileOutputStream mDrawFile = null;
			// try {
			//
			// String mFilePath = "/data/data/com.android.EpubReader/image.png";
			// mDrawFile = new FileOutputStream(mFilePath);
			//
			// if (mDrawFile != null) {
			// mBitmap.compress(Bitmap.CompressFormat.PNG, 100, mDrawFile);
			// mDrawFile.close();
			//
			// }
			//
			// } catch (Exception e) {
			// Log.e("testSaveView", "Exception: " + e.toString());
			// }

			/*
			 * set result for new activity and finish this activity
			 */
			setResult(ACTIVITY_RETURN);
			Annotation.this.finish();

		}
	};

	/*
	 * MyView class is used for Handling all the required Action for Drawing
	 */
	public class MyView extends View {

		private Canvas mCanvas;
		private Path mPath;
		private Paint mBitmapPaint;
		private float mX, mY;
		private static final float TOUCH_TOLERANCE = 4;

		public MyView(Context c) {
			super(c);
			/*
			 * Creating Bitmap for drawing
			 */
			mBitmap = Bitmap.createBitmap(420, 400, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);

		}

		/*
		 * onDraw() is used for Drawing on Canves using Paint
		 */
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(COLOR_WHITE);
			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
			canvas.drawPath(mPath, mPaint);
		}

		/*
		 * Handling touch event when starts
		 */
		private void touch_start(float x, float y) {
			mPath.reset();
			mPath.moveTo(x, y);
			mX = x;
			mY = y;
		}

		/*
		 * Handling touch event while moving
		 */
		private void touch_move(float x, float y) {
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}
		}

		/*
		 * Handling touch event
		 */
		private void touch_up() {
			mPath.lineTo(mX, mY);
			// commit the path to our offscreen
			mCanvas.drawPath(mPath, mPaint);
			// kill this so we don't double draw
			mPath.reset();
		}

		/*
		 * Handling User Touch while getting Handwritten input
		 */

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
			}
			return true;

		}

	}

}
