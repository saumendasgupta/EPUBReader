
package com.sdg.Dictionary;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;


import com.android.QuikE.R;

public class Dictionary extends Activity {
	/* this path is for dct file store in sdcard */
	final String DICTIONARY_PATH = "/sdcard/oxford.dct";
	/* this textview will show the mwaning of the word */
	WebView mMeaningText;  
	/* this editbox will take the input for the dictionary */
	EditText mWord;
	/* this string will collect the input from the edittext */
	String mExtractedText;
	/* load the SO file for Dictionary */
	static {
		System.loadLibrary("Dictionary");
	}

	/* function declaration for the native function of Dictionary */
	public static native int DICTIONARY_Init(String mDictionaryPath);

	public static native String DICTIONARY_FindWord(String pui8InputBuffer);

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dictionary);

		/* get the bundle from Display_Manager Activities */
		Bundle mBundle = this.getIntent().getExtras();
		/* initialize the input edittext */
		mWord = (EditText) findViewById(R.id.DictionaryEditText);
		/* initialize the meaning text box */
		mMeaningText = (WebView) findViewById(R.id.MeaningEditText);
		/* initialize the find meaning arraw image */
		ImageView mFindWords = (ImageView) findViewById(R.id.FindWords);
		/* initialize the close button */
		ImageView mCloseDictionary = (ImageView) findViewById(R.id.ImageViewSaveAs);
		ImageView mCloseDictionaryIcon = (ImageView) findViewById(R.id.closeDictionary);
		

		/* if click on the layout to close the dictionary activity */
		mCloseDictionary.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/* finish the dictionary activity */
				Dictionary.this.finish();
 
			}
		});
		/* if click on the close button close the dictionary activity */
		mCloseDictionaryIcon.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				/* finish the dictionary activity */
				Dictionary.this.finish();

			}
		});

		/* extract all the bundle information */
		if (mBundle != null) {
			/* get the copied text from book to Dictionary */
			mExtractedText = mBundle.getString("SelectedWord");
			/* remove space from the word */
			mExtractedText = splitWord(mExtractedText.toString());
			/* set the edittext for the word */
			mWord.setText(mExtractedText);
			/* make the bundle object null for next time use */
			mBundle = null;
		}
		/* if click on find word image */
		mFindWords.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				
				String mMeaning = null;
				/* if text is coming from the book */
				if (mExtractedText != null) {
					/* first initialize the Dictionary */
					DICTIONARY_Init(DICTIONARY_PATH);
					/* get the meaning from the Dictionary */
					mMeaning = DICTIONARY_FindWord(mExtractedText.toString());
					/*if no value is returned*/
					if(mMeaning.equalsIgnoreCase("")){
						mMeaning="No Match Found";
						 
					}
					/* set the meaning in the textview */
					if(mMeaning==null)
						mMeaning="No Match Found";
					mMeaningText.loadData(mMeaning, "text/html", "utf-8");
					mExtractedText = null;
				}
				/* if text is not coming from the book and user give the input */
				else {
					/* initialize the Dictionary */
					DICTIONARY_Init(DICTIONARY_PATH);
					/* get the Word to find from EditText */
					String mTempWord = mWord.getText().toString();
					/* remove the space */
					mTempWord = splitWord(mTempWord);
					if(mTempWord!=null){
					/* get the meaning from the Dictionary */
					mMeaning = DICTIONARY_FindWord(mTempWord);
					/*if no value is returned*/
					if(mMeaning=="" ){
						mMeaning="No Match Found";
						}
					}
					/* set the meaning in the TextView */
					if(mMeaning==null)
						mMeaning="No Match Found";
					mMeaningText.loadData(mMeaning, "text/html", "utf-8");
				}

			}
		});

	}

	/*
	 * this Function will delete the Extra Space From the Word and also convert
	 * the word in Lower case
	 */
	public String splitWord(String mWord) {
		/* take two String object */
		String tempString[]=null;
		String NewString=null;
		
		/* split the word from space */
		tempString = mWord.split(" ");
		int i = 1;
		/* store the first part of the splitted text */
		if(tempString.length>0){
			NewString = tempString[0];
			/* untill the splitted text are present concate them */
			while (tempString.length > i) {
	
				NewString += tempString[i];
				i++;
			}
			/* convert the whole word in lower case */
			NewString = NewString.toLowerCase();
			/* return the converted word */
			return NewString;
		}
		return null;
		
	}
}