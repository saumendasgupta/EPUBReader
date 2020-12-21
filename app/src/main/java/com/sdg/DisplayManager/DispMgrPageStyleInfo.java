
package com.sdg.DisplayManager;

import java.awt.font.TextAttribute;

import android.graphics.Typeface;

import com.sdg.EPUBparser.EPUBparser;

public class DispMgrPageStyleInfo {

	DispMgrUtility utl = new DispMgrUtility();
	//EPUBparser epubparser = new EPUBparser();
	/*
	 * page style info
	 */
	int miPgstLineSpace;
	int miPgstLeftMargin;
	int miPgstRightMargin;
	int miPgstTopMargin;
	int miPgstBottomMargin;
	int miPgstStartXPos;
	int miPgstTextAlign; /* to be used like enum */

	/*
	 * font details for the page
	 */
	int mfPgstFontSize;
	int miPgstFontStyle;
	Typeface msPgstFontFamily;
	float mPgstFontWeight;

	
	//public int penFontSize;
	int pi32Fontsize_Point;
	
	public DispMgrPageStyleInfo() {

	}

	
	/*
	 * to get the page layout according to the CSS parser values
	 */
	
	void DISPLAYOUT_GetPageLayout(EPUBparser epubparse)
	{
		int i;
		for(i =0 ; i< epubparse.getCsscount();i++)
		{
			DispLayout_SetProperty(epubparse,i);
		}                         
		DISPLAYOUT_GetFont();
		//return;

	}
	
	/*
	 * to create the font size
	 */
	void DISPLAYOUT_GetFont()
	{
	    if(this.mfPgstFontSize == 0 && this.mPgstFontWeight==0  && this.miPgstFontStyle==0 )
	    {
	        return;
	    }
	   this.mfPgstFontSize= ((this.mfPgstFontSize << utl.FONTSIZE_BITPOS) |
	                    ((int)this.mPgstFontWeight << utl.FONTWEIGHT_BITPOS) |
	                    (this.miPgstFontStyle <<utl.FONTSTYLE_BITPOS) );
	                    //(this.msPgstFontFamily << utl.FONTFAMILY_BITPOS));
	    return;
	}

	
	
	
	/*
	 * set the properties according to font size
	 */
	void DispLayout_SetProperty(EPUBparser epubparse,int i)
	{
		switch(epubparse.getenpropty(i))
		{	   
			 case 13 :
		            DISPLAYOUT_SetFontSize(epubparse,i);
		            break;


		}
	}
	
	/*
	 * set the font size by converting the values
	 */
	void DISPLAYOUT_SetFontSize(EPUBparser epubparser,int i)
	{
	    //int i32Fontsize_Point = 0;
	    switch (epubparser.getenunit(i))
	    {
	        case DispMgrUtility.DISPMGR_CSS_UNITS_PROP_VAL_ENUM:
	            /*TODO:Check with Palani*/
	            DISPLAYOUT_GetFontSizeFromEnum(epubparser.getvalue(i));
	            break;
	        case DispMgrUtility.DISPMGR_CSS_NO_UNITS:
	            this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_DEFAULT;
	            break;
	        default:
	            DISPLAYOUT_MapToPoint(epubparser,i);
	            DISPLAYOUT_GetFontFromPointValue(pi32Fontsize_Point);
//	            break;

	    }

	}

	/*
	 * get the font size according the  point sizes
	 */
	void DISPLAYOUT_GetFontFromPointValue(int i32Fontsize_Point)
	{
	    if(i32Fontsize_Point >=0 && i32Fontsize_Point<=12)
	    {
	        this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_SMALLEST;
	    }
	    else if(i32Fontsize_Point >=13 && i32Fontsize_Point<=25)
	    {
	        this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_SMALLER;
	    }
	    else if(i32Fontsize_Point >=26 && i32Fontsize_Point<=38)
	    {
	        this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_SMALL;
	    }
	    else if(i32Fontsize_Point >=39 && i32Fontsize_Point<=51)
	    {
	        this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_DEFAULT;
	    }
	    else if(i32Fontsize_Point >=52 && i32Fontsize_Point<=64)
	    {
	        this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_MEDIUM;
	    }
	    else if(i32Fontsize_Point >=65 && i32Fontsize_Point<=77)
	    {
	        this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_LARGE;
	    }
	    else if(i32Fontsize_Point >=78 && i32Fontsize_Point<=91)
	    {
	        this.mfPgstFontSize= utl.DISPMGR_FONT_SIZE_LARGER;
	    }
	    else if(i32Fontsize_Point >=92)
	    {
	        this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_LARGEST;
	    }
	}

	
	
	/*
	 * convert inches and mm values to pixels
	 */
	
	void DISPLAYOUT_MapToPoint(EPUBparser epubparser,int i)
	{
		switch(epubparser.getenunit(i))
		{
		
		case DispMgrUtility.DISPMGR_CSS_UNITS_PERCENTAGE:
				break;
		case DispMgrUtility.DISPMGR_CSS_UNITS_IN:
			pi32Fontsize_Point = epubparser.getvalue(i)/72;
			break;
		case DispMgrUtility.DISPMGR_CSS_UNITS_CM:
			pi32Fontsize_Point = (int) ((epubparser.getvalue(i)*0.39)/72);
			break;
		case DispMgrUtility.DISPMGR_CSS_UNITS_MM:
			pi32Fontsize_Point = (int) (epubparser.getvalue(i)*0.39*10)/72;
			break;
		case DispMgrUtility.DISPMGR_CSS_UNITS_EM:
			pi32Fontsize_Point = 10;
			pi32Fontsize_Point *=  epubparser.getvalue(i);/*TODO:Not correct*/
			break;

		case DispMgrUtility.DISPMGR_CSS_UNITS_EX:
			break;
		case DispMgrUtility.DISPMGR_CSS_UNITS_PT:
			pi32Fontsize_Point= epubparser.getvalue(i);
			break;
		case DispMgrUtility.DISPMGR_CSS_UNITS_PC:
			pi32Fontsize_Point= epubparser.getvalue(i)/12;
			break;
		case DispMgrUtility.DISPMGR_CSS_UNITS_PX:
			pi32Fontsize_Point= (int) ((int) epubparser.getvalue(i)+0.75);
			break;
		default :
			pi32Fontsize_Point = 10;/*TODO:Need to be declared in header file*/


			break;


		}
		
}

	
	
	
	/*
	 * get the font size from declared values and assign them 
	 */
	
	void DISPLAYOUT_GetFontSizeFromEnum(int i32value)
	{
	    switch(i32value)
	    {
	        case DispMgrUtility.DISPMGR_CSS_FONT_SIZE_XX_SMALL :
	            this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_SMALLEST;
	            break;
	        case DispMgrUtility.DISPMGR_CSS_FONT_SIZE_X_SMALL:
	            this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_SMALLER;
	            break;
	        case DispMgrUtility.DISPMGR_CSS_FONT_SIZE_SMALL:
	            this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_SMALL;
	            break;

	        case DispMgrUtility.DISPMGR_CSS_FONT_SIZE_MEDIUM:
	            this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_MEDIUM;
	            break;
	        case DispMgrUtility.DISPMGR_CSS_FONT_SIZE_LARGE:
	            this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_LARGE;
	            break;
	        case DispMgrUtility.DISPMGR_CSS_FONT_SIZE_X_LARGE:
	            this.mfPgstFontSize =utl.DISPMGR_FONT_SIZE_LARGER;
	            break;
	        case DispMgrUtility.DISPMGR_CSS_FONT_SIZE_XX_LARGE:
	            this.mfPgstFontSize =utl.DISPMGR_FONT_SIZE_LARGEST;
	            break;
	        case DispMgrUtility.DISPMGR_CSS_FONT_SIZE_SMALLER:
	            this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_SMALLER;
	            break;
	        case DispMgrUtility.DISPMGR_CSS_FONT_SIZE_LARGER:
	            this.mfPgstFontSize =utl.DISPMGR_FONT_SIZE_LARGER;
	            break;

	        default:
	            this.mfPgstFontSize = utl.DISPMGR_FONT_SIZE_DEFAULT;
	            break;
	            
	    }    
	}         

	
	
	
	/*
	 * to set the default page layout
	 */
	
	void display_manager_SetDefaultPageLayout(EPUBparser epubparser) {
		switch (epubparser.getContentType()) {
		case DispMgrUtility.XHTML_PARAGRAPH:
			miPgstLineSpace = utl.DISPMGR_LINESPACE;
			miPgstLeftMargin = utl.DISPMGR_PARA_LM;
			miPgstRightMargin = utl.DISPMGR_PARA_RM;
			miPgstBottomMargin = utl.DISPMGR_PARA_BM;
			miPgstStartXPos = utl.DISPMGR_PARA_START_XPOS;
			miPgstTextAlign = utl.DISPMGR_TEXT_ALIGN_LEFT;
			miPgstTopMargin = utl.DISPMGR_TOP_MARGIN;
			mfPgstFontSize = utl.DISPMGR_FONT_SIZE_DEFAULT;
			mPgstFontWeight =  TextAttribute.WEIGHT_LIGHT;
			msPgstFontFamily = Typeface.SERIF;
			miPgstFontStyle = utl.DISPMGR_FONT_STYLE_DEFAULT;
			
			break;
		case DispMgrUtility.XHTML_HEADER1:
			miPgstLineSpace = utl.DISPMGR_LINESPACE;
			miPgstLeftMargin = utl.DISPMGR_H1_START_XPOS;
			miPgstRightMargin = utl.DISPMGR_H1_RM;
			miPgstBottomMargin = utl.DISPMGR_H2_BM;
			miPgstStartXPos = utl.DISPMGR_H1_START_XPOS;
			miPgstTextAlign = utl.DISPMGR_TEXT_ALIGN_CENTER;
			miPgstTopMargin = utl.DISPMGR_HEADER_TOP_MARGIN;
			mfPgstFontSize = utl.DISPMGR_FONT_SIZE_LARGEST;
			mPgstFontWeight =  TextAttribute.WEIGHT_HEAVY;

			msPgstFontFamily = Typeface.SERIF;
			miPgstFontStyle = utl.DISPMGR_FONT_STYLE_OBLIQUE;
			
			break;
		case DispMgrUtility.XHTML_HEADER2:
			miPgstLineSpace = utl.DISPMGR_LINESPACE;
			miPgstLeftMargin = utl.DISPMGR_H2_START_XPOS;
			miPgstRightMargin = utl.DISPMGR_H2_RM;
			miPgstBottomMargin = utl.DISPMGR_H2_BM;
			miPgstStartXPos = utl.DISPMGR_H2_START_XPOS;
			miPgstTopMargin = utl.DISPMGR_HEADER_TOP_MARGIN;
			miPgstTextAlign = utl.DISPMGR_TEXT_ALIGN_CENTER;
			mfPgstFontSize = utl.DISPMGR_FONT_SIZE_LARGER;
			mPgstFontWeight = TextAttribute.WEIGHT_BOLD;
			msPgstFontFamily = Typeface.SERIF;
			miPgstFontStyle = utl.DISPMGR_FONT_STYLE_DEFAULT;
			
			break;
		case DispMgrUtility.XHTML_HEADER3:
			miPgstLineSpace = utl.DISPMGR_LINESPACE;
			miPgstLeftMargin = utl.DISPMGR_H3_START_XPOS;
			miPgstRightMargin = utl.DISPMGR_H3_RM;
			miPgstBottomMargin = utl.DISPMGR_H3_BM;
			miPgstStartXPos = utl.DISPMGR_H3_START_XPOS;
			miPgstTopMargin = utl.DISPMGR_HEADER_TOP_MARGIN;
			miPgstTextAlign = utl.DISPMGR_TEXT_ALIGN_CENTER;
			mfPgstFontSize = utl.DISPMGR_FONT_SIZE_LARGE;
			mPgstFontWeight = utl.DISPMGR_FONT_WGT_BOLD;
			msPgstFontFamily = Typeface.SERIF;
			miPgstFontStyle = utl.DISPMGR_FONT_STYLE_DEFAULT;
		
			break;
		case DispMgrUtility.XHTML_HEADER4:
			miPgstLineSpace = utl.DISPMGR_LINESPACE;
			miPgstLeftMargin = utl.DISPMGR_H4_START_XPOS;
			miPgstRightMargin = utl.DISPMGR_H4_RM;
			miPgstBottomMargin = utl.DISPMGR_H4_BM;
			miPgstStartXPos = utl.DISPMGR_H4_START_XPOS;
			miPgstTopMargin = utl.DISPMGR_HEADER_TOP_MARGIN;
			miPgstTextAlign = utl.DISPMGR_TEXT_ALIGN_CENTER;
			mfPgstFontSize = utl.DISPMGR_FONT_SIZE_MEDIUM;
			mPgstFontWeight = utl.DISPMGR_FONT_WGT_BOLD;
			msPgstFontFamily = Typeface.SERIF;
			miPgstFontStyle = utl.DISPMGR_FONT_STYLE_DEFAULT;
	
			break;
		case DispMgrUtility.XHTML_HEADER5:
			miPgstLineSpace = utl.DISPMGR_LINESPACE;
			miPgstLeftMargin = utl.DISPMGR_H5_START_XPOS;
			miPgstRightMargin = utl.DISPMGR_H5_RM;
			miPgstBottomMargin = utl.DISPMGR_H5_BM;
			miPgstStartXPos = utl.DISPMGR_H5_START_XPOS;
			miPgstTopMargin = utl.DISPMGR_HEADER_TOP_MARGIN;
			miPgstTextAlign = utl.DISPMGR_TEXT_ALIGN_CENTER;
			mfPgstFontSize = utl.DISPMGR_FONT_SIZE_SMALL;
			mPgstFontWeight = utl.DISPMGR_FONT_WGT_BOLD;
			msPgstFontFamily = Typeface.SERIF;
			miPgstFontStyle = utl.DISPMGR_FONT_STYLE_DEFAULT;
		
			break;
		case DispMgrUtility.XHTML_HEADER6:
			miPgstLineSpace = utl.DISPMGR_LINESPACE;
			miPgstLeftMargin = utl.DISPMGR_H6_START_XPOS;
			miPgstRightMargin = utl.DISPMGR_H6_RM;
			miPgstBottomMargin = utl.DISPMGR_H6_BM;
			miPgstStartXPos = utl.DISPMGR_H6_START_XPOS;
			miPgstTopMargin = utl.DISPMGR_HEADER_TOP_MARGIN;
			miPgstTextAlign = utl.DISPMGR_TEXT_ALIGN_CENTER;
			mfPgstFontSize = utl.DISPMGR_FONT_SIZE_SMALLER;
			mPgstFontWeight = utl.DISPMGR_FONT_WGT_BOLD;
			msPgstFontFamily = Typeface.SANS_SERIF;
			miPgstFontStyle = utl.DISPMGR_FONT_STYLE_DEFAULT;
			
			break;
	
		case DispMgrUtility.XHTML_DIV:
			break;
		case DispMgrUtility.XHTML_SPAN:
			miPgstLineSpace = utl.DISPMGR_LINESPACE;
			miPgstLeftMargin = utl.DISPMGR_SPAN_LM;
			miPgstRightMargin = utl.DISPMGR_SPAN_RM;
			miPgstBottomMargin = utl.DISPMGR_SPAN_BM;
			miPgstStartXPos = utl.DISPMGR_SPAN_START_XPOS;
			miPgstTopMargin = utl.DISPMGR_TOP_MARGIN;
			miPgstTextAlign = utl.DISPMGR_TEXT_ALIGN_LEFT;
			mfPgstFontSize = utl.DISPMGR_FONT_SIZE_DEFAULT;
			mPgstFontWeight = utl.DISPMGR_FONT_WGT_DEFAULT;
			msPgstFontFamily = Typeface.SANS_SERIF;
			miPgstFontStyle = utl.DISPMGR_FONT_STYLE_DEFAULT;
			
			break;
		case DispMgrUtility.XHTML_LINK:
			break;
		case DispMgrUtility.XHTML_UNORDERED_LIST:
			miPgstLineSpace = utl.DISPMGR_LINESPACE;
			miPgstLeftMargin = utl.DISPMGR_LIST_LM;
			miPgstRightMargin = utl.DISPMGR_LIST_RM;
			miPgstBottomMargin = utl.DISPMGR_PARA_BM;
			miPgstStartXPos = utl.DISPMGR_LIST_START_XPOS;
			miPgstTextAlign = utl.DISPMGR_TEXT_ALIGN_LEFT;
			miPgstTopMargin = utl.DISPMGR_TOP_MARGIN;
			mfPgstFontSize = utl.DISPMGR_FONT_SIZE_DEFAULT;
			mPgstFontWeight = utl.DISPMGR_FONT_WGT_DEFAULT;
			msPgstFontFamily = Typeface.SANS_SERIF;
			miPgstFontStyle = utl.DISPMGR_FONT_STYLE_DEFAULT;

			break;
		case DispMgrUtility.XHTML_ORDERED_LIST:
			miPgstLineSpace = utl.DISPMGR_LINESPACE;
			miPgstLeftMargin = utl.DISPMGR_LIST_LM;
			miPgstRightMargin = utl.DISPMGR_LIST_RM;
			miPgstBottomMargin = utl.DISPMGR_PARA_BM;
			miPgstStartXPos = utl.DISPMGR_LIST_START_XPOS;
			miPgstTextAlign = utl.DISPMGR_TEXT_ALIGN_CENTER;
			miPgstTopMargin = utl.DISPMGR_TOP_MARGIN;
			mfPgstFontSize = utl.DISPMGR_FONT_SIZE_DEFAULT;
			mPgstFontWeight = utl.DISPMGR_FONT_WGT_DEFAULT;
			msPgstFontFamily = Typeface.SANS_SERIF;
			miPgstFontStyle = utl.DISPMGR_FONT_STYLE_DEFAULT;

			break;
		case DispMgrUtility.XHTML_LIST:
			break;
		case DispMgrUtility.XHTML_UNDERLINE:
			break;
		case DispMgrUtility.XHTML_EMPHASIS:
			break;
		case DispMgrUtility.XHTML_LINEBREAK:
			break;
		case DispMgrUtility.XHTML_BOLD:
			break;
		case DispMgrUtility.XHTML_ITALICS:
			break;
		case DispMgrUtility.XHTML_CENTER:
			break;
		case DispMgrUtility.XHTML_PARAM:
			break;
		case DispMgrUtility.XHTML_NOSCRIPT:
			break;
		case DispMgrUtility.XHTML_BLOCKQUOTE:
			miPgstLineSpace = utl.DISPMGR_LINESPACE;
			miPgstLeftMargin = utl.DISPMGR_BLKQT_LM;
			miPgstRightMargin = utl.DISPMGR_BLKQT_RM;
			miPgstBottomMargin = utl.DISPMGR_BLKQT_BM;
			miPgstStartXPos = utl.DISPMGR_BLKQT_START_XPOS;
			miPgstTopMargin = utl.DISPMGR_TOP_MARGIN;
			miPgstTextAlign = utl.DISPMGR_TEXT_ALIGN_LEFT;
			mfPgstFontSize = utl.DISPMGR_FONT_SIZE_DEFAULT;
			mPgstFontWeight = utl.DISPMGR_FONT_WGT_DEFAULT;
			msPgstFontFamily = Typeface.SANS_SERIF;
			miPgstFontStyle = utl.DISPMGR_FONT_STYLE_ITALIC;
			break;
		case DispMgrUtility.XHTML_IMAGE:

			miPgstLineSpace = utl.DISPMGR_LINESPACE;
			miPgstLeftMargin = utl.DISPMGR_IMAGE_LM;
			miPgstRightMargin = utl.DISPMGR_IMAGE_RM;
			miPgstBottomMargin = utl.DISPMGR_IMAGE_BM;
			miPgstStartXPos = utl.DISPMGR_IMAGE_LM;
			miPgstTopMargin = utl.DISPMGR_IMAGE_TM;
			miPgstTextAlign = utl.DISPMGR_TEXT_ALIGN_CENTER;
			mfPgstFontSize = utl.DISPMGR_FONT_SIZE_DEFAULT;
			mPgstFontWeight = utl.DISPMGR_FONT_WGT_DEFAULT;
			msPgstFontFamily =Typeface.SANS_SERIF;
			miPgstFontStyle = utl.DISPMGR_FONT_STYLE_ITALIC;

			break;
		default:
			miPgstLineSpace = utl.DISPMGR_LINESPACE;
			miPgstLeftMargin = utl.DISPMGR_BLKQT_LM;
			miPgstRightMargin = utl.DISPMGR_BLKQT_RM;
			miPgstBottomMargin = utl.DISPMGR_BLKQT_BM;
			miPgstStartXPos = utl.DISPMGR_BLKQT_START_XPOS;
			miPgstTopMargin = utl.DISPMGR_TOP_MARGIN;
			miPgstTextAlign = utl.DISPMGR_TEXT_ALIGN_LEFT;
			mfPgstFontSize = utl.DISPMGR_FONT_SIZE_DEFAULT;
			mPgstFontWeight = TextAttribute.WEIGHT_LIGHT;
			msPgstFontFamily = Typeface.SANS_SERIF;
			miPgstFontStyle = utl.DISPMGR_FONT_STYLE_ITALIC;
		

			break;

		}

	}
	
}
