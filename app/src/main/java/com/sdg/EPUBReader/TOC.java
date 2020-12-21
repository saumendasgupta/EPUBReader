
package com.sdg.EPUBReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import android.widget.AdapterView.OnItemClickListener;

import com.sdg.DisplayManager.Display_Manager;
import com.sdg.EPUBparser.EPUBparser;
import com.android.QuikE.R;

public class TOC extends Activity {
	public static int mTOCResultCode=0;
	/*this string will store the toc path*/
	public static String tocpath;
	/*create a object of the EpubParser*/

	EPUBparser mEpubObj = new EPUBparser();
	/*load the library*/
	static {
		System.loadLibrary("EPUBparser");
	}
	/*
	 * alChapterName will hold the list of all chapters name
	 */
	List<Map<String, String>> alChapterName = new ArrayList<Map<String, String>>();
	/*
	 * alChapterName will hold the list of all content file name with respect to
	 * the corresponding chapter name
	 */
	List<Map<String, String>> alChapterContent = new ArrayList<Map<String, String>>();
	/*
	 * mName used as a key
	 */
	String mName = "NAME";
	/*
	 * mContent used as a key
	 */
	String mContent = "CONTENT";
	/*
	 * mList is the ListView of TOC
	 */
	ListView mList;
	/*take a simple adapter*/
	SimpleAdapter mAdapter;
	/*
	 * mContentFileName will hold the content file once any list item gets
	 * clicked
	 */
	String mContentFileName;
	/*string to store book path*/
	String mBookPath;
	/*string to store opf path*/
	String mOpfPath;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*get the bundle coming from toc layout page*/
		Bundle mBundle = this.getIntent().getExtras();
		/*get the book path*/
		mBookPath = mBundle.getString("BookPath");
		/*get the opf path*/
		mOpfPath = mBundle.getString("OpfPath");
		/*set the layout of TOC page*/
		setContentView(R.layout.toc);
		/*parse the ncx file*/
		parseNCX();

		/*
		 * mList is getting point to the ListView01 of XML File
		 */
		mList = (ListView) findViewById(R.id.ListView01);

		/*
		 * setting the basic characteristic of the ListView
		 */
		// mList.setBackgroundColor(Color.WHITE);
		mList.setDivider(null);
		/*space between two element is 0*/
		mList.setDividerHeight(0);
		/*
		 * mListItem is getting point to the TextView01 of XML File
		 */
		TextView mListItem = (TextView) findViewById(R.id.TextView01);
		/*set the element hight*/
		mListItem.setHeight(100);

		/*
		 * CreateList() will create the ListView of all chapters Name, Clicking
		 * on any item of the List it will give the ContentFile Name
		 */
		CreateList();
		/*set a onclick listener on the element item*/
		mList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> mDummyAdapter, View view,
					int iPosition, long arg3) {

				/*
				 * mContentFileName will hold the content file once any list
				 * item gets clicked
				 */

				mContentFileName = alChapterContent.get(iPosition).toString();
				/*split the string as it comes with braces*/
				String SplitString[] = mContentFileName.split("=");
				/*store the spillted string*/
				mContentFileName = SplitString[1].substring(0, (SplitString[1]
						.length() - 1));
				/*crreate a intent*/
				Intent mIntent = new Intent(TOC.this, Display_Manager.class);
				/*assaing the toc path*/
				tocpath = mContentFileName;
				setResult(0);
				mTOCResultCode=1;
				/*finish the current activity*/
				finish();
				

			}
		});

	}

	/*
	 * CreateList() will create the ListView of all chapters
	 */

	public void CreateList() {
		for (int i = 0; i < mEpubObj.getui32LVL1NodeCount(); i++) {
			/*
			 * LEVEL 1 - hmLevel1ChapName and hmLevel1ChapContent used to add
			 * the Level1 ChapterName and ContentFIlename to the main List
			 */
			HashMap<String, String> hmLevel1ChapName = new HashMap<String, String>();
			HashMap<String, String> hmLevel1ChapContent = new HashMap<String, String>();
			/*set the level 1 chap name*/
			hmLevel1ChapName.put(mName, "	" + mEpubObj.getaui8Text(i));
			/*set the level one chap content*/
			hmLevel1ChapContent.put(mContent, mEpubObj.getContentName(i));
			/*add the chap name in list*/
			alChapterName.add(hmLevel1ChapName);
			/*add the chap content in list*/
			alChapterContent.add(hmLevel1ChapContent);
			/*this will check if level 2 toc is present*/
			if (mEpubObj.getui32LVL2NodeCount(i) > 0) {
				for (int i1 = 0; i1 < mEpubObj.getui32LVL2NodeCount(i); i1++) {
					/*
					 * LEVEL 2 - hmLevel2ChapName and hmLevel2ChapContent used
					 * to add the Level2 ChapterName and ContentFIlename
					 */
					HashMap<String, String> hmLevel2ChapName = new HashMap<String, String>();
					HashMap<String, String> hmLevel2ChapContent = new HashMap<String, String>();

					/*get the 2nd level chap name*/
					hmLevel2ChapName.put(mName, "		"
							+ mEpubObj.getaui8Text(i, i1));
					/*get the 2nd level chap content*/
					hmLevel2ChapContent.put(mContent, mEpubObj.getContentName(
							i, i1));
					/*add  tha chap name in list*/
					alChapterName.add(hmLevel2ChapName);
					/*add the chap element in list*/
					alChapterContent.add(hmLevel2ChapContent);
					/*this will check if 3rd level TOC present*/
					if (mEpubObj.getui32LVL3NodeCount(i, i1) > 0) {
						for (int i2 = 0; i2 < mEpubObj.getui32LVL3NodeCount(i,
								i1); i2++) {
							/*
							 * LEVEL 3 - hmLevel3ChapName and
							 * hmLevel3ChapContent used to add the Level3
							 * ChapterName and ContentFIlename
							 */
							HashMap<String, String> hmLevel3ChapName = new HashMap<String, String>();
							HashMap<String, String> hmLevel3ChapContent = new HashMap<String, String>();
							/*get the chap name*/
							hmLevel3ChapName.put(mName, "			"
									+ mEpubObj.getaui8Text(i, i1, i2));
							/*get the chap content*/
							hmLevel3ChapContent.put(mContent, mEpubObj
									.getContentName(i, i1, i2));
							/*set the chap name in list*/
							alChapterName.add(hmLevel3ChapName);
							/*set the chap element in list*/
							alChapterContent.add(hmLevel3ChapContent);
							/*check if TOC level 4 id present*/
							if (mEpubObj.getui32LVL4NodeCount(i, i1, i2) > 0) {
								for (int i3 = 0; i3 < mEpubObj
										.getui32LVL4NodeCount(i, i1, i2); i3++) {
									/*
									 * LEVEL 4 - hmLevel4ChapName and
									 * hmLevel4ChapContent used to add the
									 * Level4 ChapterName and ContentFIlename
									 */
									HashMap<String, String> hmLevel4ChapName = new HashMap<String, String>();
									HashMap<String, String> hmLevel4ChapContent = new HashMap<String, String>();
									/*get the 4th level chap name*/
									hmLevel4ChapName.put(mName, "				"
											+ mEpubObj.getaui8Text(i, i1, i2,
													i3));
									/*get the 4th level chap element*/
									hmLevel4ChapContent.put(mContent, mEpubObj
											.getContentName(i, i1, i2, i3));
									/*set the chap name in list*/
									alChapterName.add(hmLevel4ChapName);
									/*set the chap element in list*/
									alChapterContent.add(hmLevel4ChapContent);

								}

							}

						}
					}

				}
			}

		}
		/*create a object of the simplae adapter*/
		mAdapter = new SimpleAdapter(this.getApplicationContext(),
				alChapterName, R.layout.toc, new String[] { mName },
				new int[] { R.id.TextView01 });
		/*set the adapter in the list*/
		mList.setAdapter(mAdapter);

	}
	/*this function will parse the ncx file*/
	void parseNCX() {
		int retval = mEpubObj.EPUB_AL_ParseNCXFile(mBookPath, mOpfPath,
				mEpubObj.pstNCXroot); /* call to the JNI method */
		/*to setthe level info*/
		mEpubObj.setLevel1Info(mEpubObj.getLevel1Info()); /*
														 * call to the JNI
														 * method
														 */
		/* Below loop gets the table of content based on the levels */
		for (int i = 0; i < mEpubObj.getui32LVL1NodeCount(); i++) {
			if (mEpubObj.getui32LVL2NodeCount(i) > 0) {
				/* call to the JNI method */
				mEpubObj.setLevel2Info(mEpubObj.getLevel2Info(i), i);
				for (int i1 = 0; i1 < mEpubObj.getui32LVL2NodeCount(i); i1++) {
					if (mEpubObj.getui32LVL3NodeCount(i, i1) > 0) {
						mEpubObj.setLevel3Info(mEpubObj.getLevel3Info(i, i1),
								i, i1); /* call to the JNI method */
						for (int i2 = 0; i2 < mEpubObj.getui32LVL3NodeCount(i,
								i1); i2++) {
							if (mEpubObj.getui32LVL4NodeCount(i, i1, i2) > 0) {
								mEpubObj.setLevel4Info(mEpubObj.getLevel4Info(
										i, i1, i2), i, i1, i2); /*
																 * call to the
																 * JNI method
																 */
								for (int i3 = 0; i3 < mEpubObj
										.getui32LVL4NodeCount(i, i1, i2); i3++) {

								}
							}

						}
					}

				}
			}

		}

	}
}
