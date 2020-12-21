
package com.sdg.DisplayManager;

public class DispMgrUtility {
	/*
	 * Layoutheight to display the page
	 */
	public static final int Layoutheight = 1500;
	public static int PAGEWIDTH = 700;

	/*
	 * Data base name in which tables are created
	 */
	public static final String DATABASE_NAME_BOOK = "BookDataBase.db";
	public static final String TABLE_NAME_CABINET = "CabinetInfoTable";
	public static final String INDEX_TABLE = "IndexTable";
	//public static final String FASTER_TOC = "FasterTOC";
	
	public static final String CURRENT_INDEX_TABLE = "CurrentIndexTable";
	public static final String PREVIOUS_INDEX_TABLE = "PreviousIndexTable";
	
	

	/*
	 * For Array Size
	 */
	public static final int MAX_SIZE = 100;
	
	/*
	 * For CSS Parser Style
	 */
	public static final int DISPMGR_ARIAL_DEFAULTSIZE  =0;
	public static final int  DISPMGR_ARIAL_SMALL        =1;
	public static final int   DISPMGR_ARIAL_SMALLER      =2;
	public static final int   DISPMGR_ARIAL_SMALLEST     =3;
	public static final int   DISPMGR_ARIAL_LARGE        =4;
	public static final int  DISPMGR_ARIAL_LARGER       =5;
	public static final int DISPMGR_ARIAL_LARGEST      =6;
	    public static final int DISPMGR_ARIAL_MEDIUM       =7;

	    public static final int  DISPMGR_ARIAL_BOLD_DEFAULTSIZE =8; /*TODO:More enum to be added*/
	    public static final int DISPMGR_ARIAL_BOLD_SMALL       =9;
	    public static final int DISPMGR_ARIAL_BOLD_SMALLER     =10;
	    public static final int DISPMGR_ARIAL_BOLD_SMALLEST    =11;
	    public static final int DISPMGR_ARIAL_BOLD_LARGE       =12;
	    public static final int DISPMGR_ARIAL_BOLD_LARGER      =13;
	    public static final int DISPMGR_ARIAL_BOLD_LARGEST     =14;
	    public static final int DISPMGR_ARIAL_BOLD_MEDIUM      =15;

	    public static final int DISPMGR_ARIAL_ITALIC_DEFAULTSIZE =16;
	    public static final int DISPMGR_ARIAL_ITALIC_SMALL     =17;
	    public static final int DISPMGR_ARIAL_ITALIC_SMALLER   =18;
	    public static final int DISPMGR_ARIAL_ITALIC_SMALLEST  =19;
	    public static final int DISPMGR_ARIAL_ITALIC_LARGE     =20;
	    public static final int DISPMGR_ARIAL_ITALIC_LARGER    =21;
	    public static final int DISPMGR_ARIAL_ITALIC_LARGEST   =22;
	    public static final int DISPMGR_ARIAL_ITALIC_MEDIUM    =23;

	    public static final int DISPMGR_ARIAL_ITALIC_BOLD_DEFAULTSIZE =24; /*TODO:More enum to be added*/
	    public static final int DISPMGR_ARIAL_ITALIC_BOLD_SMALL    =25;
	    public static final int DISPMGR_ARIAL_ITALIC_BOLD_SMALLER  =26;
	    public static final int DISPMGR_ARIAL_ITALIC_BOLD_SMALLEST =27;
	    public static final int DISPMGR_ARIAL_ITALIC_BOLD_LARGE    =28;
	    public static final int DISPMGR_ARIAL_ITALIC_BOLD_LARGER   =29;
	    public static final int DISPMGR_ARIAL_ITALIC_BOLD_LARGEST  =30;
	    public static final int DISPMGR_ARIAL_ITALIC_BOLD_MEDIUM   =31;

	    public static final int DISPMGR_ARIAL_OBLIQUE_DEAFULTSIZE  =32;
	    public static final int DISPMGR_ARIAL_OBLIQUE_SMALL        =33;
	    public static final int DISPMGR_ARIAL_OBLIQUE_SMALLER      =34;
	    public static final int DISPMGR_ARIAL_OBLIQUE_SMALLEST     =35;
	    public static final int DISPMGR_ARIAL_OBLIQUE_LARGE        =36;
	    public static final int DISPMGR_ARIAL_OBLIQUE_LARGER       =37;
	    public static final int DISPMGR_ARIAL_OBLIQUE_LARGEST      =38;
	    public static final int DISPMGR_ARIAL_OBLIQUE_MEDIUM       =39;

	    public static final int DISPMGR_ARIAL_OBLIQUE_BOLD_DEFAULTSIZE =40;
	    public static final int DISPMGR_ARIAL_OBLIQUE_BOLD_SMALL    =41;
	    public static final int DISPMGR_ARIAL_OBLIQUE_BOLD_SMALLER  =42;
	    public static final int DISPMGR_ARIAL_OBLIQUE_BOLD_SMALLEST =43 ;
	    public static final int DISPMGR_ARIAL_OBLIQUE_BOLD_LARGE   =44;
	    public static final int DISPMGR_ARIAL_OBLIQUE_BOLD_LARGER  =45;
	    public static final int DISPMGR_ARIAL_OBLIQUE_BOLD_LARGEST =46;
	    public static final int DISPMGR_ARIAL_OBLIQUE_BOLD_MEDIUM  =47;

	
	
	
	    public static final int FONTSIZE_BITPOS    = 0;
	    public static final int FONTWEIGHT_BITPOS  = 3;
	    public static final int FONTSTYLE_BITPOS   = 4;
	    public static final int FONTFAMILY_BITPOS  = 6;

	
	
	
	
	
	

	/*
	 * enum for return values DISPMGR_RETVAL_EN
	 */

	public static final int DISPMGR_SUCCESS = 0;
	public static final int DISPMGR_EOFPAGE = 1;
	public static final int DISPMGR_ENDOFBOOK = 2;
	public static final int DISPMGR_ENDOFBUFFER = 3;
	public static final int DISPMGR_INVALIDCHAP = 4;
	public static final int DISPMGR_ZIP_FILE_NOT_FOUND = 5;
	public static final int DISPMGR_FAILURE = -1;
	public static final int DISPMGR_MEMERR = -2;

	/*
	 * EBOOK_RETVAL_EN
	 */

	public static final int EBOOK_SUCCESS = 0;
	public static final int EBOOK_TIMEOUT_EXIT = 2;
	public static final int EBOOK_FAILURE = -1;

	public static final int EBOOK_FALSE = 0;
	public static final int EBOOK_TRUE = 1;

	/*
	 * XHTML_CONTENTTYPE_EN
	 */

	public static final int XHTML_NO_TYPE = -1;
	public static final int XHTML_BODY = 0;
	public static final int XHTML_IMAGE = 1;
	public static final int XHTML_TABLE = 2;
	public static final int XHTML_CT_SECTION = 3;
	public static final int XHTML_PARAGRAPH = 4;
	public static final int XHTML_HEADER1 = 5;
	public static final int XHTML_HEADER2 = 6;
	public static final int XHTML_HEADER3 = 7;
	public static final int XHTML_HEADER4 = 8;
	public static final int XHTML_HEADER5 = 9;
	public static final int XHTML_HEADER6 = 10;
	public static final int XHTML_BLOCKQUOTE = 11;
	public static final int XHTML_DIV = 12;
	public static final int XHTML_SPAN = 13;
	public static final int XHTML_LINK = 14;
	public static final int XHTML_UNORDERED_LIST = 15;
	public static final int XHTML_ORDERED_LIST = 16;
	public static final int XHTML_LIST = 17;
	public static final int XHTML_UNDERLINE = 18;
	public static final int XHTML_EMPHASIS = 19;
	public static final int XHTML_LINEBREAK = 20;
	public static final int XHTML_BOLD = 21;
	public static final int XHTML_ITALICS = 22;
	public static final int XHTML_CENTER = 23;
	public static final int XHTML_PARAM = 24;
	public static final int XHTML_NOSCRIPT = 25;
	public static final int XHTML_TAG_ELEMENT_COUNT = 26;

	/*
	 * CSS_TEXT_ALIGN_EN
	 */
	public static final int DISPMGR_TEXT_ALIGN_LEFT = 0;
	public static final int DISPMGR_TEXT_ALIGN_RIGHT = 1;
	public static final int DISPMGR_TEXT_ALIGN_CENTER = 2;
	public static final int DISPMGR_TEXT_ALIGN_JUSTIFY = 3;
	public static final int DISPMGR_TEXT_ALIGN_INHERIT = 4;

	/*
	 * CSS_DECL_UNITS_EN
	 */

	public static final int DISPMGR_CSS_NO_PROP_VAL_ENUM_MATCH = -3;
	public static final int DISPMGR_CSS_UNITS_PROP_VAL_ENUM = -2;
	public static final int DISPMGR_CSS_NO_UNITS = -1;
	public static final int DISPMGR_CSS_DEFAULT_UNITS = 0;
	public static final int DISPMGR_CSS_UNITS_PERCENTAGE = 1;
	public static final int DISPMGR_CSS_UNITS_IN = 2;
	public static final int DISPMGR_CSS_UNITS_CM = 3;
	public static final int DISPMGR_CSS_UNITS_MM = 4;
	public static final int DISPMGR_CSS_UNITS_EM = 5;
	public static final int DISPMGR_CSS_UNITS_EX = 6;
	public static final int DISPMGR_CSS_UNITS_PT = 7;
	public static final int DISPMGR_CSS_UNITS_PC = 8;
	public static final int DISPMGR_CSS_UNITS_PX = 9;
	public static final int DISPMGR_CSS_TOTAL_UNITS = 10; /* Total No. Of Units */

	/*
	 * CSS_FONT_SIZE_EN
	 */
	public static final int DISPMGR_CSS_FONT_SIZE_XX_SMALL = 0;
	public static final int DISPMGR_CSS_FONT_SIZE_X_SMALL = 1;
	public static final int DISPMGR_CSS_FONT_SIZE_SMALL = 2;
	public static final int DISPMGR_CSS_FONT_SIZE_MEDIUM = 3;
	public static final int DISPMGR_CSS_FONT_SIZE_LARGE = 4;
	public static final int DISPMGR_CSS_FONT_SIZE_X_LARGE = 5;
	public static final int DISPMGR_CSS_FONT_SIZE_XX_LARGE = 6;
	public static final int DISPMGR_CSS_FONT_SIZE_SMALLER = 7;
	public static final int DISPMGR_CSS_FONT_SIZE_LARGER = 8;
	public static final int DISPMGR_CSS_FONT_SIZE_LENGTH = 9;
	public static final int DISPMGR_CSS_FONT_SIZE_PERCENTAGE = 10;
	public static final int DISPMGR_CSS_FONT_SIZE_INHERIT = 11;

	/*
	 * CSS_FONT_WEIGHT_EN
	 */
	public static final int DISPMGR_CSS_FONT_WEIGHT_NORMAL = 0;
	public static final int DISPMGR_CSS_FONT_WEIGHT_BOLD = 1;
	public static final int DISPMGR_CSS_FONT_WEIGHT_BOLDER = 2;
	public static final int DISPMGR_CSS_FONT_WEIGHT_LIGHTER = 3;
	public static final int DISPMGR_CSS_FONT_WEIGHT_100 = 4;
	public static final int DISPMGR_CSS_FONT_WEIGHT_200 = 5;
	public static final int DISPMGR_CSS_FONT_WEIGHT_300 = 6;
	public static final int DISPMGR_CSS_FONT_WEIGHT_400 = 7;
	public static final int DISPMGR_CSS_FONT_WEIGHT_500 = 8;
	public static final int DISPMGR_CSS_FONT_WEIGHT_600 = 9;
	public static final int DISPMGR_CSS_FONT_WEIGHT_700 = 10;
	public static final int DISPMGR_CSS_FONT_WEIGHT_800 = 11;
	public static final int DISPMGR_CSS_FONT_WEIGHT_900 = 12;
	public static final int DISPMGR_CSS_FONT_WEIGHT_EN = 13;

	/*
	 * default page settings values have to be changed to dip margin calculation
	 * has to be refined
	 */

	public static final int DISPMGR_PAGE_END_SAPCE = 50;
	public static final int DISP_WIDTH = 700;
	public static final int DISP_HEIGHT = 1500;

	public static final int DIASPMGR_SPACE_IN_HEADERTOPARA = 30;

	public static final int DISPMGR_LINESPACE = 4;
	public static final int DISPMGR_PARA_LM = 20;
	public static final int DISPMGR_PARA_RM = (DISP_WIDTH - DISPMGR_PARA_LM);
	public static final int DISPMGR_PARA_BM = (DISP_HEIGHT - DISPMGR_PAGE_END_SAPCE);
	public static final int DISPMGR_PARA_START_XPOS = 30;
	public static final int DISPMGR_TOP_MARGIN = 20;

	public static final int DISPMGR_HEADER_TOP_MARGIN = 40;
	public static final int DISPMGR_HEADER_LINESPACE = 8;
	public static final int DISPMGR_H1_START_XPOS = 20;
	public static final int DISPMGR_H1_RM = (DISP_WIDTH - DISPMGR_H1_START_XPOS);
	public static final int DISPMGR_H1_BM = (DISP_HEIGHT - DISPMGR_PAGE_END_SAPCE);
	public static final int DISPMGR_H1_TOP_MARGIN = 30;

	public static final int DISPMGR_H2_START_XPOS = 20;
	public static final int DISPMGR_H2_RM = (DISP_WIDTH - DISPMGR_H2_START_XPOS);
	public static final int DISPMGR_H2_BM = (DISP_HEIGHT - DISPMGR_PAGE_END_SAPCE);
	public static final int DISPMGR_H2_TOP_MARGIN = 30;

	public static final int DISPMGR_H3_START_XPOS = 20;
	public static final int DISPMGR_H3_RM = (DISP_WIDTH - DISPMGR_H3_START_XPOS);
	public static final int DISPMGR_H3_BM = (DISP_HEIGHT - DISPMGR_PAGE_END_SAPCE);
	public static final int DISPMGR_H3_TOP_MARGIN = 30;

	public static final int DISPMGR_H4_START_XPOS = 20;
	public static final int DISPMGR_H4_RM = (DISP_WIDTH - DISPMGR_H4_START_XPOS);
	public static final int DISPMGR_H4_BM = (DISP_HEIGHT - DISPMGR_PAGE_END_SAPCE);
	public static final int DISPMGR_H4_TOP_MARGIN = 30;

	public static final int DISPMGR_H5_START_XPOS = 20;
	public static final int DISPMGR_H5_RM = (DISP_WIDTH - DISPMGR_H5_START_XPOS);
	public static final int DISPMGR_H5_BM = (DISP_HEIGHT - DISPMGR_PAGE_END_SAPCE);
	public static final int DISPMGR_H5_TOP_MARGIN = 30;

	public static final int DISPMGR_H6_START_XPOS = 20;
	public static final int DISPMGR_H6_RM = (DISP_WIDTH - DISPMGR_H6_START_XPOS);
	public static final int DISPMGR_H6_BM = (DISP_HEIGHT - DISPMGR_PAGE_END_SAPCE);
	public static final int DISPMGR_H6_TOP_MARGIN = 30;

	public static final int DISPMGR_SPAN_LM = DISPMGR_PARA_LM;
	public static final int DISPMGR_SPAN_RM = DISPMGR_PARA_RM;
	public static final int DISPMGR_SPAN_BM = DISPMGR_PARA_BM;
	public static final int DISPMGR_SPAN_START_XPOS = DISPMGR_SPAN_LM;

	public static final int DISPMGR_BLKQT_LM = 20;
	public static final int DISPMGR_BLKQT_RM = (DISP_WIDTH - DISPMGR_BLKQT_LM);
	public static final int DISPMGR_BLKQT_BM = (DISP_HEIGHT - DISPMGR_PAGE_END_SAPCE);
	public static final int DISPMGR_BLKQT_START_XPOS = DISPMGR_BLKQT_LM;

	public static final int DISPMGR_LIST_LM = 25;
	public static final int DISPMGR_LIST_RM = (DISP_WIDTH - DISPMGR_LIST_LM);
	public static final int DISPMGR_LIST_START_XPOS = 65;
	public static final int DISPMGR_LIST_BULLET_XPOS = 50;

	public static final int DISPMGR_IMAGE_LM = 20;
	public static final int DISPMGR_IMAGE_RM = (DISP_WIDTH - DISPMGR_IMAGE_LM);
	public static final int DISPMGR_IMAGE_TM = 70;
	public static final int DISPMGR_IMAGE_BM = (DISP_HEIGHT
			- DISPMGR_PAGE_END_SAPCE - 20);

	public static final int DISPMGR_FIRSTLINE = 1;

	public static final int DISPMGR_FONT_SIZE_DEFAULT = 0x12;
	public static final int DISPMGR_FONT_SIZE_SMALL = 0x13;
	public static final int DISPMGR_FONT_SIZE_SMALLER = 0x14;
	public static final int DISPMGR_FONT_SIZE_SMALLEST = 0x15;
	public static final int DISPMGR_FONT_SIZE_LARGE = 0x16;
	public static final int DISPMGR_FONT_SIZE_LARGER = 0x17;
	public static final int DISPMGR_FONT_SIZE_LARGEST = 0x23;
	public static final int DISPMGR_FONT_SIZE_MEDIUM = 0x22;

	/* Font weight enum */

	public static final float DISPMGR_FONT_WGT_DEFAULT = 0x0;
	public static final int DISPMGR_FONT_WGT_BOLD = 0x1;

	/* Font style enum */

	public static final int DISPMGR_FONT_STYLE_DEFAULT = 0x0;
	public static final int DISPMGR_FONT_STYLE_ITALIC = 0x1;
	public static final int DISPMGR_FONT_STYLE_OBLIQUE = 0x2;

	public static final int DISPMGR_FONT_FAMILY_ARIAL = 0x0;

}
