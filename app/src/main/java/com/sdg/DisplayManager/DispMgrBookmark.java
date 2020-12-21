

package com.sdg.DisplayManager;

import java.awt.font.TextAttribute;

import android.R.string;

public class DispMgrBookmark {

	/*
	 * DISPMGR_PAGEINFO_ST
	 */
	int miBmPageno;
	int miBmChapterno;
	string msBmXmlLevl;
	int miBmLineNo;

	/*
	 * DISPMGR_PAGE_SETTINGS_ST
	 */
	float mfBmFontSize;
	int miBmFontStyle;
	string msBmFontFamily;
	TextAttribute mBmFontWeight;
	float mfBmZoomLevel;

	public DispMgrBookmark() {

	}

}
