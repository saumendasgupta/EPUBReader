

package com.sdg.DisplayManager;

import java.util.concurrent.Semaphore;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.sdg.Dictionary.Dictionary;
import com.sdg.EPUBReader.ActiveWindow;
import com.sdg.EPUBReader.Annotation;
import com.sdg.EPUBReader.DataBaseClass;
import com.sdg.EPUBReader.EpubReader;
import com.sdg.EPUBReader.LibraryMainPage;
import com.sdg.EPUBReader.LibraryMainPageList;
import com.sdg.EPUBReader.MyCabinet;
import com.android.QuikE.R;
import com.sdg.EPUBReader.TOC;
import com.sdg.EPUBReader.TocLayout;
import com.sdg.EPUBReader.AnnotImage;
import com.sdg.organizer.Organizer;

public class Display_Manager extends ActivityGroup {
	/* create a new intent for BroadCast */
	Intent mIntentAction = new Intent(Intent.ACTION_MAIN);
	
	public static boolean mOpenDispalyManager = false;
	public static String BookpathForLib;
	static int ISCHAPTERCHANGE = 0;
	static int ISCHAPTERNOTCHANGED = 0;
	/* create a object of Broadcast class */
	BroadcastReceiver mBroadCast = new BroadcastReceiver() {
		/* this will come if close book is called from activewindow */
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getIntExtra("mak", 0) == 2) {
				mIsGoingHome = 1;
				try {
					// acquire semaphore
					mSemaphore.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// set flag for normal page operation

				mBookInfo.mObjDifFlag = PAGE_NAVIGATION_START;
				// cancel the thread i.e doInBackgroung task
				if (!mDbopp.cancel(true)) {
					// Drop index table
					myDbBook.execSQL("DROP TABLE IF EXISTS IndexTable;");
					// finish display manager activity
					Display_Manager.this.finish();
					startActivity(new Intent(Display_Manager.this, EpubReader.class));
				}
				// reset the flag
				mBookInfo.mObjDifFlag = PAGE_NAVIGATION_END;
				// release semaphore
				mSemaphore.release();

			}
			/* Senario:Home->Library->BookDisplay->bookClose */
			if (intent.getIntExtra("CloseBookFromActiveWindow", 3) == 1) {

				mIsGoingHome = 1;
				try {
					// acquire semaphore
					mSemaphore.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// set flag for normal page operation

				mBookInfo.mObjDifFlag = PAGE_NAVIGATION_START;
				// cancel the thread i.e doInBackgroung task
				if (!mDbopp.cancel(true)) {
					// Drop index table
					myDbBook.execSQL("DROP TABLE IF EXISTS IndexTable;");
					// finish display manager activity
					Display_Manager.this.finish();
					mIsGoingHome = 0;

				}
				// reset the flag
				mBookInfo.mObjDifFlag = PAGE_NAVIGATION_END;
				// release semaphore
				mSemaphore.release();

			}
			if (intent.getIntExtra("FinishDisplayPage", 1) != 1) {
				mIsGoingHome = 2;
				try {
					// acquire semaphore
					mSemaphore.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// set flag for normal page operation

				mBookInfo.mObjDifFlag = PAGE_NAVIGATION_START;
				// cancel the thread i.e doInBackgroung task
				if (!mDbopp.cancel(true)) {
					// Drop index table
					myDbBook.execSQL("DROP TABLE IF EXISTS IndexTable;");

					// finish display manager activity
					Display_Manager.this.finish();
					mOpenDispalyManager = true;

				}
				// reset the flag
				mBookInfo.mObjDifFlag = PAGE_NAVIGATION_END;
				// release semaphore
				mSemaphore.release();

			}

		}
	};
	// all the declaration needed for display manager start
	DispMgrBookInfo mBookInfo = new DispMgrBookInfo();
	DispMgrBookInfo mBookInfoForPageUp = new DispMgrBookInfo();
	DispMgrBookInfo mBookInfoForFasterTOC = new DispMgrBookInfo();
	DispMgrUtility utl = new DispMgrUtility();

	// Page Navigation next and previous
	RelativeLayout mNextPage;
	RelativeLayout mPreviousPage;
	// Parent layout for displaying book content
	RelativeLayout mRelativeLayout;

	// Layout to show the animation effect of page
	RelativeLayout Temp_anim;
	// RelativeLayout for Zoom
	AbsoluteLayout mZoomLayout;

	DataBaseClass mDb;

	// Absolute Layout for showing Drag and drop
	private AbsoluteLayout mDragDropMenuContainer;
	// this layout is for the whole Display_manager layout
	public RelativeLayout mRel;
	// this layout is for BookDisplay portion only
	public static RelativeLayout mRelLayout;
	// parent layout for holding different types of views of a page
	LinearLayout mPageLayout;

	// For Putting each paragraph of a book in different EditText
	EditText mText[] = new EditText[utl.MAX_SIZE];
	// For generating page for index generation
	EditText mTextpageup[] = new EditText[utl.MAX_SIZE];
	// For constructing one text view
	EditText mTempText[] = new EditText[utl.MAX_SIZE];
	EditText mDummyTextView;
	// For displaying page no
	TextView mPage;
	// For showing text for drag and drop
	TextView mCopyEditText;
	// this RelativeLayout will open MyCabinet in Book Display page
	RelativeLayout mMyCabinet;
	// For displaying image
	ImageView mImage[] = new ImageView[utl.MAX_SIZE];
	// For copying displaying image
	ImageView mCopyImage;

	// to store the image path from the book
	String ImagePath;
	// to indicate if image is selected for touched or not*/
	private boolean mImageTouched = false;

	// For holding image of table of content
	ImageView mToc;
	// this imageview will open annotate in Book Display page
	ImageView mAnnotate;
	// this imageview will open BookMark option in Book Display page
	ImageView mBookmarkImage;

	/* Image view Added for Zoom */
	ImageView mZoom;
	/* slideing Bar for the Zoom control */
	AbsoluteLayout mZoomSlider;

	/* imageview to show the slider bar of the Zoom Slider */
	ImageView mSlideBar;
	ImageView mPositiveZoom;
	ImageView mNegativeZoom;
	ImageView mScoller;

	// No of paragraphs in a book page
	int mParaIndex = 0;
	// No of images in a book page
	int mImageIndex;
	// index for edittext which hold all the paragraphs before encountering the
	// image
	int mTempedittextindex = 0;
	// to hold the return value from functions
	static int mReturnVal;
	// to hold line no
	int LineNo;
	/* all the variable used for Zoom Control */
	/* static value for Zoom Level */
	private float mZoomLevel = 1f;
	private static float mPreviousZoomLevel = 1f;
	// this boolean is used to understand that zoom slide bar is added already
	// or not
	private static boolean mZoomSliderAdded = false;
	/* this macro is to indicate if cabinet is open */
	public int CABINETISOPEN = 1;
	/* this macro is to indicate if cabinet is closed */
	public int CABINETISCLOSED = 0;
	/*
	 * FLAGS FOR TOC
	 */
	int PrevioustIndexGeneration = 0;

	int IndexGeneratinfortocorzoom = 0;

	int IndexGenerationCrossedTheChapter = 0;

	public static int TOCClicked = 0;

	int CurrentChapterFromIndexTable;

	int PreviousChapterFromIndexTable;

	int StopGeneratingCurrentIndextableAgain = 0;

	// int makFlag=0;
	// flag to get the TOC is clicked or not
	int mFlagToKnowTOCisClickedOrNot = 0;

	public static final int COMLETED = 1;
	public static final int GENERATING = 0;
	public static int mPageNumberIsNotcurroct = 0;
	public static int ResetPageNumForTOC = 0;
	public static int mFlagToCheckPageIsFromTOCtoIndexGeneration = 0;

	// To know the Indexgeneration threads status
	public int mIndexGenerationStatus = GENERATING;

	// Flag to know the image or text
	int mimageflag;
	// these two macro is used for drag and drop effect
	// final int UPPER_LAYER = 125;
	final int UPPER_LAYER = 144;
	final int SIDE_LAYER = 40;
	// these macros are used to set the ID of layout,textview etc
	final int LAYOUT_ID = 2000;
	// macros to start the activity from TOC
	final int FROM_TOC = 2;
	// close book is called from active window
	final int CLOSE_BOOK = 9;
	// for opening selected book
	final int OPEN_SELECTED_BOOK = 3;
	final int DRAW_ANNOTATION = 10000;
	final int ANNOT_ID = 1000;
	final int ANNOT_NUMB = 50;
	/* macro to store start position of zoom slider */
	final int START_POSITION_OF_ZOOM_SLIDER = 320;

	// macro for the index generation
	public static final int INDEX_GENERATION_START = 1;
	public static final int INDEX_GENERATION_STOP = 0;

	// macro for page navigation
	public static final int PAGE_NAVIGATION_START = 1;
	public static final int PAGE_NAVIGATION_END = 0;

	// this flag is to check whether the action from display manager is to
	// go to library ( on menu item click) or home page( on H key press...)

	int mIsGoingHome = 0;

	// to check if cabinet is already open
	public static int mCabinetOpen = 0;
	// to prevent multiple insertion of same
	static int mCabinetAlreadyOpen = 0;
	// to show the selection of text
	// Start point where the text selection starts
	int mStart;
	// end point where the text selection stops
	int mEnd;

	int mZoomClicked = 0;

	// no of clicks made to select
	static int selectcounter = 0;
	// to differentiate between start of a selection and end of selection
	static boolean isSelected = false;
	int mPreventFlag;
	public static int TocFlagForBlocking = 0;
	int mAgainGeneratingTOC = 0;
	// For Storing line number for TOC
	public int lineNumberForPageUpTOC = 0;
	// For Storing xmlLevel for TOC
	public String XmlLevelForPageUpTOC;
	// To hold page count for TOC operation
	static int PageCount = 0;
	// data in cabinet
	static int mFromOrganizerNoteId;
	// This macro is for swipe limit during page flipping
	private static final int SWIPE_MIN_DISTANCE = 1;

	static int mFromPrevious = 0;

	// To hold chapter number
	static int chnum;

	// public static int GetDataFromFasterToc = 0;

	// to hold the book path
	public static String mBookPath;
	// to hold the path of file given by table of content
	String mTocPath;
	// Temporary buffer to hold the data
	String mTempBuf;
	// to store the selected text
	String mTempStr;
	// to get the selected text
	String mTempStr2;

	// Binary semaphore for differentiate between data from Asynctask and normal
	// main thread flow
	public static final Semaphore mSemaphore = new Semaphore(1, false);
	// to get data from activity by which this activity is called
	Bundle mBundle;
	SQLiteDatabase myDbBook = null;

	static View mMyCabinetview;
	// object of a class used for running and cancelling the asynctask
	DataBaseOperations mDbopp;
	// object of a class used for running and cancelling the asynctask for Fast
	// TOC operation
	IndexGeneration mFastTOC;
	// GenerateOnechapter mInsertOnechapter;

	// to start the Cabinet Activity
	public static LocalActivityManager mLocalActivityManager;

	// Gesture scanner for Long press Operation
	private GestureDetector gestureScanner;

	
	// For the selection of text from book by pixel selection method

	// integer to store the pixel values of point from where the text selection
	// is started
	private int mTextSelectionStart_X = 0;
	private int mTextSelectionStart_Y = 0;

	// integer to store the pixel values of point where the text selection ends
	private int mTextSelectionEnd_X = 0;
	private int mTextSelectionEnd_Y = 0;

	// integer to store the pixel values of point to where the text selection is
	// going in on move

	private int mTextSelectionMove_X = 0;
	private int mTextSelectionMove_Y = 0;

	// integer value to hold the character index from where the text selection
	// is to be started

	private int mTextSelectionIndexStart = 0;

	// integer value to hold the character index where the text selection ends
	private int mTextSelectionIndexEnd = 0;

	// integer value to hold the character index to where the text selection is
	// going on in on move
	private int mTextSelectionIndexMove = 0;

	// this holds the string from which a particular part is to be highlighted
	Spannable colourString;

	// offsets/ margins on top and left

	private int UPPER_BAR_OFFSET = 154;
	private int LEFT_BAR_OFFSET = 40;

	// THese arrays store the size and typefaces of the text for selection 
	// procedure  
	int mAllLineSizeArray[] = new int[utl.MAX_SIZE];
	Typeface mAllLineFontArray[] = new Typeface[utl.MAX_SIZE];
	//__________________________________________________________________________________
	String mAllUriArray[] = new String[utl.MAX_SIZE];

	// for measuring the character size
	Paint mPen;

	// String that holds the data to be dragged to cabinet from book
	String mTextToDrag = null;

	// flag to stop the text shrinking while move gesture is on hold
	boolean mHoldShrink = false;

	// This class is for defining all the transitions and animations
	// Previously Page_Transition
	Transitions transitions;

	// EditText for selction
	EditText mSelectionEditText;

	// flag to determine whether image is selected
	boolean mImageSelected = false;

	//
	private ImageView mSelectionImage;

	
	// Added for proto 3
	Spannable Sspan;
	// For Annotation
	int pos;
	final int ANNO_LAYOUT_ID = 3000;
	AbsoluteLayout mAnnotAbsLay;
	public int Annot_count = 0;
	public int annotid;
	// for Drawing icon
	public int mFontSize = 0;
	Paint pen = new Paint();
	Annotinfo mAnnotInfo[];
	int[][] IconPos;
	DispMgrBookInfo.XmlLevel_param mXMLlevelParam[];
	LineInfo mLineInfo;
	HL_STRING_POS mHighlightPos = new HL_STRING_POS();
	String Anno_BookName = null;
	int mOffsetInfo[];
	public ANNOTATION_INFO_PARAM mAnnotInfoParam[];
	int Annotation_StartXPos;
	int Annotation_EndXPos;
	public ImageView Image_sub;

	// this progress dialog will show at the time of bookmark
	private ProgressDialog dialog;

	// This method is called when the this(Display_Manager) activity starts
	// another activity by using StartActivityForResult() and that activity
	// finished
	// its execution
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if TOC is called from Disply_Manager
		if (resultCode == 0 && TOC.mTOCResultCode == 1) {

			// reset the resultcode value
			TOC.mTOCResultCode = 0;

			// To Delete previous content of tables (i.e Old content)
			myDbBook.execSQL("DELETE FROM  " + utl.CURRENT_INDEX_TABLE + ";");

			// Hold the TOC path i.e the file name of the selected TOC

			mTocPath = TOC.tocpath;

			/* Fix for the TOC To Remove # From TOC path MAK */

			String TempMak[] = mTocPath.split("#");
			mTocPath = TempMak[0];

			// GetDataFromFasterToc = 1;
			// To get the chapter number according to the chapter name
			chnum = mBookInfo.epubparser.OPF_GetChapterIndex(mTocPath,
					mBookInfo.epubparser.objMetadata,
					mBookInfo.epubparser.aspineelement,
					mBookInfo.epubparser.amanifestelement);
			/*
			 * // Open the database myDbBook =
			 * Display_Manager.this.openOrCreateDatabase(
			 * DispMgrUtility.DATABASE_NAME_BOOK, 1, null);
			 */
			// Check for index generation completion
			if (mIndexGenerationStatus == COMLETED) {
				// get the data from index table to display
				mBookInfo.mdisplayManagerGetChapterInfo(myDbBook, chnum + 1);
				// Display the book page of selected chapter
				bookview_display_page();
				// reset the flag
				mBookInfo.mObjDifFlag = 0;
				// release semaphore
				mSemaphore.release();

			}
			// If index generation not completed
			else {
				mBookInfo.mFasterTOc = 1;
				TOCClicked = 1;
				// Make some initialisation to display the selected chapters
				// first page
				InitToDisplayTOC();
				mBookInfo.miDmbPageNo = 1;
				// Display the page
				bookview_display_page();
				mBookInfo.miDmbPageNo = 1;
				// Store the line no after the page display
				lineNumberForPageUpTOC = mBookInfo.miDmbLineNo;
				// Store the xmllevel after page display
				XmlLevelForPageUpTOC = mBookInfo.epubparser.getContentPath();
				// Reset the flag
				mBookInfo.mObjDifFlag = 0;
				// release semaphore
				mSemaphore.release();
				// if chapter no is grater than 2 then only generate Index for
				// faster TOC operation
				/*
				 * if (chnum > 2) { // Initialisation and start index generation
				 * async task to // generate table for faster TOC
				 * StartIndexingForNextandPreviousPage(); }
				 */

			}

		}
		// this will come if close book is called from activewindow
		if (resultCode == CLOSE_BOOK) {

			try {
				// acquire semaphore
				mSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// set flag for normal page operation
			mBookInfo.mObjDifFlag = 1;
			// cancel the thread i.e doInBackgroung task
			if (!mDbopp.cancel(true)) {
				// Drop index table
				myDbBook.execSQL("DROP TABLE IF EXISTS IndexTable;");
				// finish display manager activity
				Display_Manager.this.finish();
			}
			// reset the flag
			mBookInfo.mObjDifFlag = 0;
			// release semaphore
			mSemaphore.release();

		}
		// Close the previously opened book and open selecte book
		if (resultCode == OPEN_SELECTED_BOOK) {
			// get the book path
			mBookPath = data.getStringExtra("BookPath");
			// create the intent
			Intent mCallBookDiaplay = new Intent(Display_Manager.this,
					Display_Manager.class);
			Bundle mBundle = new Bundle();
			// put book path into bundle
			mBundle.putString("BookPath", mBookPath);
			// put the bundle in intent
			mCallBookDiaplay.putExtras(mBundle);
			//
			try {
				// acquire semaphore
				mSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// set flag for normal page operation
			mBookInfo.mObjDifFlag = 1;
			// cancel the thread i.e doInBackgroung task
			if (!mDbopp.cancel(true)) {
				// Drop index table
				myDbBook.execSQL("DROP TABLE IF EXISTS IndexTable;");
				// finish display manager activity
				Display_Manager.this.finish();
			}
			// reset the flag
			mBookInfo.mObjDifFlag = 0;
			// release semaphore
			mSemaphore.release();
			// finish the display manager activity
			Display_Manager.this.finish();
			// start new activity
			startActivity(mCallBookDiaplay);

		}
		if (resultCode == DRAW_ANNOTATION) {
			DrawAnnotation();
		}
	}

	private void InitToDisplayTOC() {
		try {
			/* acquire semaphore */
			mSemaphore.acquire();
			/* set page flag for normal page operation */
			mBookInfo.mObjDifFlag = 1;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// to initialise the chapter number
		mBookInfo.miDmbCurrentChapter = chnum;
		// to initialise line buffer status
		mBookInfo.miDmbLineBufferStatus = utl.EBOOK_FALSE;
		// to start the line buffer status when TOC chapter is selected
		mBookInfo.miDmbLineNo = utl.DISPMGR_FIRSTLINE;
		// set book status
		mBookInfo.miDmbBookStatus = utl.EBOOK_TRUE;
		// To set displayed page number is not correct
		mPageNumberIsNotcurroct = 1;
		mFlagToCheckPageIsFromTOCtoIndexGeneration = 1;
	}

	/** Called when the activity is first created. */

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initialise CSS parser
		mBookInfo.epubparser.CSSInit();
		mBookInfoForPageUp.epubparser.CSSInit();
		mBookInfoForFasterTOC.epubparser.CSSInit();
		/* Clear the content of Current index table */
		myDbBook = Display_Manager.this.openOrCreateDatabase(
				DispMgrUtility.DATABASE_NAME_BOOK, 1, null);

		myDbBook.execSQL("DELETE FROM  " + utl.CURRENT_INDEX_TABLE + ";");
		// To check whether the cabinet is opening from Book
		Organizer.smCabinetOpen = false;

		// to indiacte cabinet is can be opened from book view page
		MyCabinet.CABINET_OPEN_FROM_NOTE = false;

		// get the data from other activities
		mBundle = this.getIntent().getExtras();
		// these two variable will store the Book's SD card path
		// TOC path

		mBookPath = mBundle.getString("BookPath");
		mTocPath = mBundle.getString("TOCPath");

		Temp_anim = new RelativeLayout(this);
		// set the Display_Manager layout
		setContentView(R.layout.mak);

		/* initialize the progress dialog */
		dialog = new ProgressDialog(Display_Manager.this);
		/* create a object of the database class */
		mDb = new DataBaseClass(this);
		/* get the handler */
		mDb.mDatab = mDb.mDatah.getReadableDatabase();

		/* update the database as that book is currently open */
		// mDb.bookOpened(mBookPath,"BookOpen");

		/* indicate that user in BookDisplay page */
		EpubReader.mPageStatus = EpubReader.BOOK_DISPLAY_PAGE;

		// Object of animations class...
		transitions = new Transitions(this);

		/* create a intent filter object */
		IntentFilter iFilter = new IntentFilter(Intent.ACTION_DEFAULT);
		/* register the receiver */
		registerReceiver(mBroadCast, iFilter);

		mRel = (RelativeLayout) findViewById(R.id.BookViewPage);
		// Parent layout for displayin a page data
		mRelativeLayout = (RelativeLayout) findViewById(R.id.relativelayout01);
		// parent layout which contains the different views created for putting
		// data to a page
		mPageLayout = (LinearLayout) findViewById(R.id.LinearLayout01);
		// navigation next point to page
		mNextPage = (RelativeLayout) findViewById(R.id.pagenext);
		// navigation point to previous page
		mPreviousPage = (RelativeLayout) findViewById(R.id.pageback);

		// Initialize RelativeLayout for Zoom
		mZoomLayout = (AbsoluteLayout) findViewById(R.id.LayoutofZoom);
		// Initialize he Zoom Sliding Bar
		mZoomSlider = new AbsoluteLayout(Display_Manager.this);
		// add the background of the scrollbar
		mZoomSlider.setBackgroundResource(R.drawable.zoombg);
		// add the negative zoom buttom
		mNegativeZoom = new ImageView(Display_Manager.this);
		mNegativeZoom.setBackgroundResource(R.drawable.zoomout);
		// add the scroll white portion
		mScoller = new ImageView(Display_Manager.this);
		mScoller.setBackgroundResource(R.drawable.zoomingsliderbar);
		// add the positive button
		mPositiveZoom = new ImageView(Display_Manager.this);
		mPositiveZoom.setBackgroundResource(R.drawable.zoomin);

		// add this three view with the ZoomLayout
//		mZoomSlider.addView(mNegativeZoom, new AbsoluteLayout.LayoutParams(15,
//				17, 5, 5));
//		mZoomSlider.addView(mScoller, new AbsoluteLayout.LayoutParams(150, 17,
//				25, 8));
//		mZoomSlider.addView(mPositiveZoom, new AbsoluteLayout.LayoutParams(15,
//				17, 178, 5));

		// create a imageview for the Zoom nob
		mSlideBar = new ImageView(Display_Manager.this);
		
		mSlideBar.setBackgroundResource(R.drawable.zoombutt);
		// add the zoom nob with the zoom white portion add it at default
		// position 1x zoom
		mZoomSlider.addView(mSlideBar, new AbsoluteLayout.LayoutParams(20, 20,
				65, 6));
		// initialize the Zoom Icon

		mZoom = (ImageView) findViewById(R.id.Zoom);

		// navigation point ot TOC
		mToc = (ImageView) findViewById(R.id.toc);
		// Initialise PageNo,Annotate and BookMark Icons
		mPage = (TextView) findViewById(R.id.pagenodisplay);
		mAnnotate = (ImageView) findViewById(R.id.bookviewpageannotate);
		mBookmarkImage = (ImageView) findViewById(R.id.bookviewbookmark);
		/* Initialise the Absolute layout for Drag and Drop */
		mDragDropMenuContainer = new AbsoluteLayout(this);

		// set the absolute layout for the whole screen
		mDragDropMenuContainer.setLayoutParams(new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		mDragDropMenuContainer.setId(LAYOUT_ID);
		mAnnotAbsLay = new AbsoluteLayout(this);
		// set the absolute layout for the whole screen
		mAnnotAbsLay.setLayoutParams(new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		

		mAnnotAbsLay.setId(ANNO_LAYOUT_ID);

		// setting the ontouchlistener on absolute layout for selection
		mDragDropMenuContainer.setOnTouchListener(mTextSelectListener);

		// to get the chapter number based on chapter name
		chnum = mBookInfo.epubparser.OPF_GetChapterIndex(mTocPath,
				mBookInfo.epubparser.objMetadata,
				mBookInfo.epubparser.aspineelement,
				mBookInfo.epubparser.amanifestelement);

		/*
		 * // To open the database myDbBook = this.openOrCreateDatabase(
		 * DispMgrUtility.DATABASE_NAME_BOOK, MODE_PRIVATE, null);
		 */
		// Free the XHTML parser pointers (JNI CALL)
		mBookInfo.epubparser.XHTML_Deinit();
		mBookInfoForPageUp.epubparser.XHTML_DeinitIndexgeneration();
		// Drop index table
		myDbBook.execSQL("DROP TABLE IF EXISTS IndexTable;");
		// Initialize book path
		mBookInfo.msDmbBookName = mBookPath;
		mBookInfoForFasterTOC.msDmbBookName = mBookInfoForPageUp.msDmbBookName = mBookInfo.msDmbBookName;

		/********* Added for ANNOTATION *********/
		Anno_BookName = new String();
		String mTempbookName = mBookInfo.msDmbBookName.substring(14);

		// Object of Transitions class... (previously PageTransition)
		transitions = new Transitions(this);

		String[] mSplit = mTempbookName.split(".epub");
		Anno_BookName = mSplit[0];

		mDb = new DataBaseClass(this);
		mDb.CreateAnnoTable(Anno_BookName);
		// method called for initialization of book display
		mBookInfo.mDisplayManagerInit(myDbBook, 1);
		// method called for initialization for index generation
		mBookInfoForPageUp.mDisplayManagerInit(myDbBook, 0);
		// method called for initialization for index generation for Faster TOC
		mBookInfoForFasterTOC.mDisplayManagerInit(myDbBook, 0);

		// added to show the place holder in bookdisplay page
		GridView mLibPlaceHolderg = (GridView) findViewById(R.id.placehold);
		mLibPlaceHolderg.setAdapter(new BookViewAdapter(this));
		mLibPlaceHolderg.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> BookViewAdapter, View v,
					int position, long id) {
				// on click go back to my library
				if (position == 0) {

					try {
						// acquire semaphore
						mSemaphore.acquire();

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// set flag for normal page operation
					mBookInfo.mObjDifFlag = 1;
					// cancel the thread i.e doInBackgroung task
					if (!mDbopp.cancel(true)) {
						Intent mIntentLibraryBack = null;
						// check the last in which view library was opened
						// if it was in GridView
						if (EpubReader.mIsListView == EpubReader.GRID_VIEW) {
							mIntentLibraryBack = new Intent(
									Display_Manager.this, LibraryMainPage.class);

						}/* if it was in ListView */
						if (EpubReader.mIsListView == EpubReader.LIST_VIEW) {
							mIntentLibraryBack = new Intent(
									Display_Manager.this,
									LibraryMainPageList.class);
						}
						// Drope index table
						myDbBook.execSQL("DROP TABLE IF EXISTS IndexTable;");
						// finish the display manager activity
						Display_Manager.this.finish();
						// start new activity
						startActivity(mIntentLibraryBack);
					}
					// reset flag
					mBookInfo.mObjDifFlag = 0;
					// Release semaphore
					mSemaphore.release();

				}
				// to open the Dictionary option
				if (position == 3) {
					/*
					 * create a bundle to send the selected word to dictionary
					 * activity
					 */
					Bundle mSelectedWord = new Bundle();
					/* create a intent of Dictionary class */
					Intent mDictionaryIntent = new Intent(Display_Manager.this,
							Dictionary.class);
					if (mTempStr != null) {
						mSelectedWord.putString("SelectedWord", mTempStr);
						mDictionaryIntent.putExtras(mSelectedWord);
						mTempStr = null;
					}

					// removing edittext for text dragging
					mDragDropMenuContainer.removeView(mCopyEditText);

					// Starting activity for dictionary feature
					startActivity(mDictionaryIntent);

					// Animation for opening the dictionary from bottom
					overridePendingTransition(R.anim.push_up, 0);

				}
				// to make a bookmark
				if (position == 4) {
					/*
					 * dialog.setMessage("Inserting data..."); dialog.show();
					 */
					Cursor PageCursor;
					PageCursor = myDbBook.rawQuery("Select * From "
							+ utl.CURRENT_INDEX_TABLE + " Where ChapterNo = "
							+ mBookInfo.miDmbCurrentChapter + ";", null);
					PageCursor.moveToFirst();
					if (PageCursor.getCount() <= 0) {
						if (TOCClicked == 1) {
							myDbBook.execSQL("DELETE FROM  "
									+ utl.CURRENT_INDEX_TABLE + ";");
							
							chnum = mBookInfo.miDmbCurrentChapter - 1;
							mBookInfoForFasterTOC.miDmbCurrentChapter = chnum;

							mBookInfoForFasterTOC.miDmbLineBufferStatus = utl.EBOOK_FALSE;
							mBookInfoForFasterTOC.miDmbLineNo = utl.DISPMGR_FIRSTLINE;
							// set book status
							mBookInfoForFasterTOC.miDmbBookStatus = utl.EBOOK_TRUE;
							// mBookInfo.miDmbBookStatus = utl.EBOOK_TRUE;

							mPageNumberIsNotcurroct = 1;
							mFastTOC = new IndexGeneration();
							mFastTOC.execute();

						}
					}
					// Code for Book mark
					

					while (IndexGeneratinfortocorzoom != 0) {
						continue;
					}

					// delete previous book mark
					myDbBook
							.execSQL("Delete From BookMarkTable Where BookName = '"
									+ mBookPath + "';");

					if (TOCClicked == 1 && mIndexGenerationStatus != COMLETED) {
						PageCursor = myDbBook.rawQuery("Select * From "
								+ utl.CURRENT_INDEX_TABLE + " Where PageNo = "
								+ mBookInfo.miDmbPageNo + ";", null);
					} else {
						do{
							PageCursor = myDbBook.rawQuery("Select MAX(PageNo) From "
									+ utl.INDEX_TABLE + ";", null);
							PageCursor.moveToFirst();
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}while(PageCursor.getInt(0) < mBookInfo.miDmbPageNo);
						PageCursor = myDbBook.rawQuery("Select * From "
								+ utl.INDEX_TABLE + " Where PageNo = "
								+ mBookInfo.miDmbPageNo + ";", null);
					}
					PageCursor.moveToFirst();
					myDbBook
							.execSQL("INSERT INTO BookMarkTable (BookName,PageNo,ChapterNo,XmlLevel,LineNo,ZoomLevel,TocClicked) VALUES ('"
									+ mBookPath
									+ "',"
									+ PageCursor.getInt(0)
									+ ","
									+ PageCursor.getInt(1)
									+ ",'"
									+ PageCursor.getString(2)
									+ "',"
									+ PageCursor.getInt(3)
									+ ","
									+ PageCursor.getInt(4)
									+ ","
									+ TOCClicked
									+ ");");

					Toast.makeText(Display_Manager.this, "BookMark Done...",
							Toast.LENGTH_LONG).show();
					// MAK Code END
					// BookMarkCode Stop

				}
				if (position == 7) {



				}
				if (position == 5) {
					if (mTempStr != null) {

						int mSelectionStart_Y = mTextSelectionStart_Y
								- UPPER_LAYER + mXMLlevelParam[0].StartYpos;
						mHighlightPos.StartYpos = HL_CorrectionYpos(
								mXMLlevelParam, mSelectionStart_Y);
						int mSelectionEnd_Y = mTextSelectionEnd_Y - UPPER_LAYER
								+ mXMLlevelParam[0].StartYpos;
						mHighlightPos.EndYpos = HL_CorrectionYpos(
								mXMLlevelParam, mSelectionEnd_Y);

						mAnnotInfoParam = new ANNOTATION_INFO_PARAM[ANNOT_NUMB];

						int xml_lelCnt = Mapping_HighlightedString(
								mHighlightPos, mBookInfo.rawbufferptr,
								mXMLlevelParam, mAnnotInfoParam);
						if (xml_lelCnt != 0) {
							Bundle mBundle = new Bundle();
							mFontSize = mAnnotInfoParam[0].FontSize;

							/* Changes for setting Annot ID */
							DataBaseClass mDb = new DataBaseClass(
									Display_Manager.this);
							mDb.mDatab = mDb.mDatah.getReadableDatabase();

							Cursor curs_id = mDb
									.selectIdFromAnnotable(Anno_BookName);
							curs_id.moveToFirst();
							annotid = curs_id.getInt(0);
							if (annotid == 0) {
								annotid = ANNOT_ID;
							}

							annotid = annotid + 2;
							for (int i = 0; i < xml_lelCnt; i++) {
								mBundle.putString("ANNOT_XML_LEVEL" + i,
										mAnnotInfoParam[i].xmllvl);
								mBundle.putInt("ANNOT_OFFSET" + i,
										mAnnotInfoParam[i].Offset);
								mBundle.putInt("ANNOT_WORDLEN" + i,
										mAnnotInfoParam[i].Wordlength);
								mBundle.putInt("ANNOT_CONTI_FLAG" + i,
										mAnnotInfoParam[i].Continue_flag);
								mBundle.putInt("LINE_NUMB" + i,
										mAnnotInfoParam[i].LineNumb);
							}
							mBundle.putInt("ANNOT_ID", annotid);
							mBundle.putInt("ANNOT_CHPT_ID",
									mBookInfo.miDmbCurrentChapter);
							/* for Book specific Table */
							mBundle.putString("BOOK_NAME", Anno_BookName);
							mBundle.putInt("XMLLEVEL_COUNT", xml_lelCnt);
							Intent mIntent = new Intent(Display_Manager.this,
									Annotation.class);
							mIntent.putExtras(mBundle);
							mTempStr = null;
							startActivityForResult(mIntent, DRAW_ANNOTATION);
						} else {
							Toast.makeText(Display_Manager.this,
									"ANNOTATION ALREADY EXISTS", 10).show();
						}
					} else
						Toast.makeText(Display_Manager.this, "NO SELECTION", 10)
								.show();

					mDragDropMenuContainer.removeView(mCopyEditText);

				}

			}

		});
		// mMyCabinet is for the cabinet activity in bookview page
		
		mMyCabinet = (RelativeLayout) findViewById(R.id.BookViewFooter);
		mMyCabinet.setOnClickListener(new OnClickListener() {
			// if click on the openCabinet option
			public void onClick(View v) {
				// call to open cabinet
				openCabinet();

			}
		});

		// For Page Indexing
		try {
			// acquire semaphore
			mSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// set flag for nomal page operation
		mBookInfo.mObjDifFlag = 1;
		// display the page
		mReturnVal = bookview_display_page();
		// reset the flag
		mBookInfo.mObjDifFlag = 0;
		// release semaphore
		mSemaphore.release();
		mDbopp = new DataBaseOperations();
		// start async task
		mDbopp.execute();

		// Navigation form present page to next page
		mNextPage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/**********/
				if (TOCClicked == 1) {
					ResetPageNumForTOC = 1;
				}

				// store in temp layout to show the animation on page next
				Temp_anim = mRelativeLayout;

				if (mBookInfo.miDmbBookStatus != utl.DISPMGR_ENDOFBOOK) {

					mAnnotAbsLay.removeAllViews();

					// remove the absolute layout
					mDragDropMenuContainer.removeAllViews();

					// remove the drag and drop textview container
					mRelativeLayout.removeView(mDragDropMenuContainer);

					// page number increment
					mBookInfo.miDmbPageNo++;
					// object of Page_Transition class

					try {
						// acquire semaphore
						mSemaphore.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// set flag for normal page operation
					mBookInfo.mObjDifFlag = 1;
					// reset the selection counter
					selectcounter = 0;
					// reset the selection
					isSelected = false;
					// display the page
					mReturnVal = bookview_display_page();
					lineNumberForPageUpTOC = mBookInfo.miDmbLineNo;
					XmlLevelForPageUpTOC = mBookInfo.epubparser
							.getContentPath();

					// show the page with animation
				//	transitions.pageNextTransition(Display_Manager.this,
				//			mRelativeLayout);

					// reset flag
					mBookInfo.mObjDifFlag = 0;
					// release semaphore
					mSemaphore.release();
					// For Starting Index Generation on chapter change
				}

			}
		});

		mPreviousPage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// creating temporary layout for animation
				Temp_anim = mRelativeLayout;

				if (IndexGeneratinfortocorzoom != 1) {

					/**********/

					// if page no is not equal to 1 the we can go to previous
					// page

					if (mBookInfo.miDmbPageNo != 1) {

						// Animation for page transition while going previous
						// page
					//	transitions.pagePreviousTransition(Display_Manager.this,
					//			mRelativeLayout);

						mAnnotAbsLay.removeAllViews();

						// remove the absolute layout
						mDragDropMenuContainer.removeAllViews();
						// remove the drag and drop textview container
						mRelativeLayout.removeView(mDragDropMenuContainer);

						if (TOCClicked == 1) {
							// decrement the page no
							Cursor pageCursor = myDbBook
									.rawQuery(
											"SELECT * FROM "
													+ DispMgrUtility.CURRENT_INDEX_TABLE
													+ ";", null);
							pageCursor.moveToFirst();
							if (pageCursor.getCount() <= 0) {
								myDbBook.execSQL("DELETE FROM  "
										+ utl.CURRENT_INDEX_TABLE + ";");
								
								chnum = mBookInfo.miDmbCurrentChapter - 1;
								if (mBookInfo.miDmbPageNo == 1) {

									mBookInfoForFasterTOC.miDmbCurrentChapter = chnum - 1;

								} else if (mBookInfo.miDmbPageNo > 1) {

									mBookInfoForFasterTOC.miDmbCurrentChapter = chnum;
								}
								mBookInfoForFasterTOC.miDmbLineBufferStatus = utl.EBOOK_FALSE;
								mBookInfoForFasterTOC.miDmbLineNo = utl.DISPMGR_FIRSTLINE;
								// set book status
								mBookInfoForFasterTOC.miDmbBookStatus = utl.EBOOK_TRUE;
								// mBookInfo.miDmbBookStatus = utl.EBOOK_TRUE;

								mPageNumberIsNotcurroct = 0;
								// mBookInfo.epubparser.SetContentPath();
								mFastTOC = new IndexGeneration();
								mFastTOC.execute();

								while (IndexGeneratinfortocorzoom != 0) {
									continue;
								}

							}
						}
						mBookInfo.miDmbPageNo--;
						DiplyPage();

					} else if (mIndexGenerationStatus != COMLETED
							&& TOCClicked == 1) {

						myDbBook.execSQL("DELETE FROM  "
								+ utl.CURRENT_INDEX_TABLE + ";");
						if (mBookInfo.ISTOCFROMBOOKMARK == 1) {
							chnum = mBookInfo.miDmbCurrentChapter - 1;
							mBookInfo.ISTOCFROMBOOKMARK = 0;
						} else {
							chnum = mBookInfoForFasterTOC.epubparser
									.OPF_GetChapterIndex(
											mTocPath,
											mBookInfoForFasterTOC.epubparser.objMetadata,
											mBookInfoForFasterTOC.epubparser.aspineelement,
											mBookInfoForFasterTOC.epubparser.amanifestelement);
						}
						if (mBookInfo.miDmbPageNo == 1) {

							mBookInfoForFasterTOC.miDmbCurrentChapter = chnum - 1;

						} else if (mBookInfo.miDmbPageNo > 1) {

							mBookInfoForFasterTOC.miDmbCurrentChapter = chnum;
						}
						mBookInfoForFasterTOC.miDmbLineBufferStatus = utl.EBOOK_FALSE;
						mBookInfoForFasterTOC.miDmbLineNo = utl.DISPMGR_FIRSTLINE;
						// set book status
						mBookInfoForFasterTOC.miDmbBookStatus = utl.EBOOK_TRUE;
						// mBookInfo.miDmbBookStatus = utl.EBOOK_TRUE;

						mPageNumberIsNotcurroct = 1;
						// mBookInfo.epubparser.SetContentPath();
						mFastTOC = new IndexGeneration();
						mFastTOC.execute();

						while (IndexGeneratinfortocorzoom != 0) {
							continue;
						}

						DiplyPage();
						lineNumberForPageUpTOC = mBookInfo.miDmbLineNo;
						XmlLevelForPageUpTOC = mBookInfo.epubparser
								.getContentPath();
						// Added By MAK
						String ChapterName = mBookInfo.epubparser
								.OPF_GetFilename(
										mBookInfo.epubparser.objMetadata,
										mBookInfo.miDmbCurrentChapter,
										mBookInfo.epubparser.aspineelement,
										mBookInfo.epubparser.amanifestelement);

						mTocPath = mBookInfoForFasterTOC.epubparser
								.OPFGetPriviousChapterName(ChapterName);
						// End of Code
					}

					else if (IndexGeneratinfortocorzoom == 0) {
						if (TOCClicked == 1) {
							DiplyPage();
							lineNumberForPageUpTOC = mBookInfo.miDmbLineNo;
							XmlLevelForPageUpTOC = mBookInfo.epubparser
									.getContentPath();
						}
					}
					// }

				} else {
					Toast.makeText(Display_Manager.this,
							"Please Wait... We are Genertating Index",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		
		mZoom.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				
				if (mZoomSliderAdded == false) {
					
					mZoomLayout.addView(mZoomSlider,
							new AbsoluteLayout.LayoutParams(200,
									LayoutParams.WRAP_CONTENT, 180, 15));
					mZoomSliderAdded = true;
				} else if (mZoomSliderAdded == true) {
					
					mZoomLayout.removeView(mZoomSlider);
					mZoomSliderAdded = false;
				}
			}
		});

		mZoomSlider.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				
				switch (event.getAction()) {
				
				case MotionEvent.ACTION_UP: {
					
					if (event.getRawX() > 205 && event.getRawX() < 238) {
						// this will show the slider bar at .5x zoom position
						mZoomSlider.removeView(mSlideBar);
						mZoomSlider.addView(mSlideBar,
								new AbsoluteLayout.LayoutParams(37, 20, 26, 6));
						if (mZoomLevel != .5f) {
							mZoomLevel = .5f;
							startZooming(mZoomLevel);
						}
					}
					if (event.getRawX() > 239 && event.getRawX() < 277) {
						// this will show the slider bar at 1x zoom position
						mZoomSlider.removeView(mSlideBar);
						mZoomSlider.addView(mSlideBar,
								new AbsoluteLayout.LayoutParams(37, 20, 65, 6));
						if (mZoomLevel != 1f) {
							mZoomLevel = 1f;
							startZooming(mZoomLevel);
						}
					}
					if (event.getRawX() > 278 && event.getRawX() < 310) {
						// this will show the slider bar at 1x zoom position
						mZoomSlider.removeView(mSlideBar);
						mZoomSlider
								.addView(mSlideBar,
										new AbsoluteLayout.LayoutParams(37, 20,
												102, 6));
						if (mZoomLevel != 1.5f) {
							mZoomLevel = 1.5f;
							startZooming(mZoomLevel);
						}
					}
					if (event.getRawX() > 310 && event.getRawX() < 350) {
						// this will show the slider bar at 1x zoom position
						mZoomSlider.removeView(mSlideBar);
						mZoomSlider
								.addView(mSlideBar,
										new AbsoluteLayout.LayoutParams(37, 20,
												137, 6));
						if (mZoomLevel != 2f) {
							mZoomLevel = 2f;
							startZooming(mZoomLevel);
						}
					}
				}
				}
				return true;
			}
		});

		// code to call active window
		ImageView mActiveWindow = (ImageView) findViewById(R.id.activewindow);
		// if click on the active window icons
		mActiveWindow.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// intent to start the ActiveWindow activity
				Intent mActiveIntent = new Intent(Display_Manager.this,
						ActiveWindow.class);
				// put the data to be send to ActiveWindow activity
				mActiveIntent.putExtra("FromHome", 2);
				// start the ActiveWindow activity
				startActivity(mActiveIntent);

				// for animation on opening activewindow
				overridePendingTransition(R.anim.push_down_in, 0);

			}
		});
		// if click on the whole header bar
		RelativeLayout mPageHeader = (RelativeLayout) findViewById(R.id.header);
		// if click on the active window layout
		mPageHeader.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// intent to start the ActiveWindow activity
				Intent mActiveIntent = new Intent(Display_Manager.this,
						ActiveWindow.class);
				// put the data to be send to ActiveWindow activity
				mActiveIntent.putExtra("FromHome", 2);
				// start the ActiveWindow activity
				startActivity(mActiveIntent);

				// for animation on opening activewindow
				overridePendingTransition(R.anim.push_down_in, 0);

			}
		});
		// Navigation to table of content of the book
		mToc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// set the flag for faster TOC operation
				// mFasterTOc = 1;
				// reset selection counter
				selectcounter = 0;
				// reset selection
				isSelected = false;
				// remove the absolute layout
				mDragDropMenuContainer.removeAllViews();
				// remove the drag and drop textview container
				// mRelativeLayout.removeView(mDragDropMenuContainer);
				/* Added for removing the annotation layout */
				mAnnotAbsLay.removeAllViews();
				// intent to start the TocLayout activity
				Intent mIntent = new Intent(Display_Manager.this,
						TocLayout.class);
				// bundle to hold the data
				Bundle mBundle = new Bundle();
				// put data in bundle
				mBundle.putString("BookPath", mBookPath);
				// get the path of TOC file
				String ncxfilename = mBookInfo.epubparser.OPF_GetTOCFileInfo(
						mBookInfo.epubparser.objMetadata,
						mBookInfo.epubparser.amanifestelement);
				// put the toc path in bundle
				mBundle.putString("OpfPath", mBookInfo.epubparser.getOpfPath()
						+ "/" + ncxfilename);
				// put bundle in intent
				mIntent.putExtras(mBundle);
				// Start the TOCLayout activity
				startActivityForResult(mIntent, 5);
			}
		});

	}

	/*
	 * Generating index(DB table) for page up(previous page) This function run
	 * in background
	 */

	public void DiplyPage() {

		/*
		 * // open database myDbBook = Display_Manager.this.openOrCreateDatabase(
		 * DispMgrUtility.DATABASE_NAME_BOOK, 1, null);
		 */// get the page information from index table
		mBookInfo.mDisplayManagerGetPageInfo(myDbBook,
				mBookInfo.miDmbCurrentChapter, XmlLevelForPageUpTOC,
				lineNumberForPageUpTOC, mBookInfo.miDmbPageNo);
		// reset image flag
		mimageflag = 0;
		// reset the selection counter
		selectcounter = 0;
		// reset the selection
		isSelected = false;
		// display the page
		bookview_display_page();

		lineNumberForPageUpTOC = mBookInfo.miDmbLineNo;
		XmlLevelForPageUpTOC = mBookInfo.epubparser.getContentPath();

		// reset flag
		mBookInfo.mObjDifFlag = 0;
		// release semaphore
		mSemaphore.release();

	}

	int mPageIndex() {
		DispMgrUtility utl = new DispMgrUtility();

		// Page status indicator
		boolean Page = true;
		// reset Retval
		int Retval = utl.DISPMGR_SUCCESS;
		// reset start page
		int StartPage = utl.EBOOK_TRUE;

		// to hold line height
		int LineHeight = 0;
		// paraindexpageup to page up operation
		int paraindexpageup;
		// change the values to fit into the bookview space
		mBookInfoForPageUp.miDmbViewYpos = 0;
		mBookInfoForPageUp.miDmbViewXpos = 0;
		// Assigns lineno
		LineNo = mBookInfoForPageUp.miDmbLineNo;
		// if buffer status is false then get the tag element count
		if (mBookInfoForPageUp.miDmbLineBufferStatus == utl.EBOOK_FALSE) {
			(mBookInfoForPageUp.miDmbPreContent) = (utl.XHTML_TAG_ELEMENT_COUNT);
		}
		// reset paraindexpageup
		paraindexpageup = 0;
		// page is true
		while (Page) {
			// Checking for endof the line buffer
			if (mBookInfoForPageUp.miDmbLineBufferStatus == utl.EBOOK_FALSE) {
				// if not start page
				if (StartPage == utl.EBOOK_FALSE) {
					// xhtml call to get the content type
					mBookInfoForPageUp.miDmbPreContent = mBookInfoForPageUp.epubparser
							.getContentType();
				}
				// To get the data from the .epub file(i.e Get The Book Content)
				// ADDED for annotation:jy
				mBookInfoForPageUp.rawbufferptr = null;

				Retval = mBookInfoForPageUp.mDisplayManagerGetData();
				mBookInfoForPageUp.msDmbXmlLevl = null;
				// if no space left in the page
				if (Retval != utl.DISPMGR_SUCCESS) {
					Page = false;
					LineNo = utl.DISPMGR_FIRSTLINE;
					break;
				}

				/* TO check first line of the buffer */
				if (LineNo == utl.DISPMGR_FIRSTLINE) {
					mBookInfoForPageUp.miDmbViewXpos = mBookInfoForPageUp.mDmPageStyleInfo.miPgstLeftMargin;
				} else {
					mBookInfoForPageUp.miDmbViewXpos = mBookInfoForPageUp.mDmPageStyleInfo.miPgstLeftMargin;
				}
			}
			/* If page starts with old buffer */
			else {
				/* create the corresponding type of new view */
				/* If it is first line of the buffer */
				if (LineNo == utl.DISPMGR_FIRSTLINE) {
					mBookInfoForPageUp.miDmbViewXpos = mBookInfoForPageUp.mDmPageStyleInfo.miPgstLeftMargin;
				} else {
					mBookInfoForPageUp.miDmbViewXpos = mBookInfoForPageUp.mDmPageStyleInfo.miPgstLeftMargin;
				}

			}
			// to set the y position between paragraphs and images
			if (StartPage == utl.EBOOK_TRUE) {
				if (mBookInfoForPageUp.epubparser.getContentType() == utl.XHTML_IMAGE) {
					mBookInfoForPageUp.miDmbViewYpos = 0;
				} else
					(mBookInfoForPageUp.miDmbViewYpos) = (mBookInfoForPageUp.miDmbViewYpos)
							+ (mBookInfoForPageUp.mDmPageStyleInfo.miPgstTopMargin);

			}
			// set line buffer status
			mBookInfoForPageUp.miDmbLineBufferStatus = utl.EBOOK_TRUE;
			// For image data
			if (mBookInfoForPageUp.epubparser.getContentType() == utl.XHTML_IMAGE) {
				ImageView temp = new ImageView(this);
				// get image path
				String ImagePath = mBookInfoForPageUp.mDispMgrLoadImage();
				// check image path is not null
				if (ImagePath != null) {
					// parse the imagepath
					Uri imageURI = Uri.parse(ImagePath);
					// set to image view
					temp.setImageURI(imageURI);
					// get Drawable components
					Drawable mdraw = temp.getDrawable();

					if (StartPage == utl.EBOOK_TRUE) {
						// Stores the index of a page to DB
						Retval = DISPMGR_StorePageBreakInfo(mBookInfoForPageUp,
								LineNo);
						// reset the startpage
						StartPage = utl.EBOOK_FALSE;
					}
					// check the page space in present page
					if ((DispMgrUtility.Layoutheight - mBookInfoForPageUp.miDmbViewYpos) >= mdraw
							.getMinimumHeight()) {
						// add image height to page size
						mBookInfoForPageUp.miDmbViewYpos += mdraw
								.getMinimumHeight();
						// set line buffer status
						mBookInfoForPageUp.miDmbLineBufferStatus = utl.EBOOK_FALSE;
						// set start page
						StartPage = utl.EBOOK_FALSE;

					}
					// check for a page space in new page
					else if ((DispMgrUtility.Layoutheight - mBookInfoForPageUp.miDmbViewYpos) < mdraw
							.getMinimumHeight()) {
						// to create new page for a image
						if (mBookInfoForPageUp.miDmbViewYpos != 0
								&& StartPage == utl.EBOOK_FALSE) {
							// set page
							Page = false;
							break;
						}
						// add image height to page size
						mBookInfoForPageUp.miDmbViewYpos += mdraw
								.getMinimumHeight();
						// set StartPage
						StartPage = utl.EBOOK_FALSE;
						// set page
						Page = false;
						// set line buffer status
						mBookInfoForPageUp.miDmbLineBufferStatus = utl.EBOOK_FALSE;
						break;

					}
					// assignment of line number
					LineNo = utl.DISPMGR_FIRSTLINE;
					continue;
				}
			}
			// For text data
			else {
				// create editview for the paragraph
				mTextpageup[paraindexpageup++] = new EditText(this);
				// set the padding
				mTextpageup[paraindexpageup - 1]
						.setPadding(
								mBookInfoForPageUp.miDmbViewXpos,
								0,
								utl.DISP_WIDTH
										- mBookInfoForPageUp.mDmPageStyleInfo.miPgstRightMargin,
								0);
				// set the text size
				mTextpageup[paraindexpageup - 1]
						.setTextSize(mBookInfoForPageUp.mDmPageStyleInfo.mfPgstFontSize
								* mBookInfoForPageUp.mfDmbZoomLevel);
				// set font family and style info
				mTextpageup[paraindexpageup - 1]
						.setTypeface(mBookInfoForPageUp.mDmPageStyleInfo.msPgstFontFamily);
				// set the page
				mTextpageup[paraindexpageup - 1].setWidth(utl.PAGEWIDTH);
				// line height of present line
				LineHeight = mTextpageup[paraindexpageup - 1].getLineHeight();
				// temp string to hold the split values
				String Temp[] = mBookInfoForPageUp.epubparser.getContent()
						.split("\n");

				while (true) {
					// line count is less than line number
					if (mBookInfoForPageUp.miDmbLineCount < LineNo) {
						// set line no to first line
						LineNo = utl.DISPMGR_FIRSTLINE;
						// set line buffer status
						mBookInfoForPageUp.miDmbLineBufferStatus = utl.EBOOK_FALSE;
						break;
					}
					// to check the space left in page
					if ((mBookInfoForPageUp.miDmbViewYpos) <= DispMgrUtility.Layoutheight)
						Retval = utl.DISPMGR_SUCCESS;
					else
						Retval = utl.DISPMGR_EOFPAGE;

					// Page boundary reached
					if (Retval == utl.DISPMGR_EOFPAGE) {
						Page = false;
						break;
					}
					// if it is a start of page
					if (StartPage == utl.EBOOK_TRUE) {
						// store page index to Index table
						Retval = DISPMGR_StorePageBreakInfo(mBookInfoForPageUp,
								LineNo);
						// set the start page
						StartPage = utl.EBOOK_FALSE;
					}
					// increment line number
					LineNo++;
					// increase the y position i.e decrees the available space
					// of a page
					mBookInfoForPageUp.miDmbViewYpos += LineHeight;

				}

			}

		}
		// store line no
		mBookInfoForPageUp.miDmbLineNo = LineNo;
		mBookInfoForPageUp.msDmbXmlLevl = null;
		return Retval;
	}

	/*
	 * Store the information to the DB for page up (goto previous page)
	 */
	int DISPMGR_StorePageBreakInfo(DispMgrBookInfo bookinfoDB, int lineno) {
		try {
			myDbBook
					.execSQL("INSERT INTO "
							+ DispMgrUtility.INDEX_TABLE
							+ "( PageNo, ChapterNo, XmlLevel, LineNo, ZoomLevel) VALUES( "
							+ bookinfoDB.miDmbPageNo + ", "
							+ bookinfoDB.miDmbCurrentChapter + ", '"
							+ bookinfoDB.epubparser.getContentPath() + "', "
							+ lineno + ", " + bookinfoDB.mfDmbZoomLevel + ");");
		} catch (Exception e) {
		}
		return 0;
	}

	
	/*
	 * Store the information to the DB for Faster TOC operation
	 */
	int DISPMGR_StorePageBreakInfoForFasterToc(DispMgrBookInfo bookinfoDB,
			int lineno) {
		try {
			if (bookinfoDB.miDmbCurrentChapter == chnum
					&& ISCHAPTERNOTCHANGED == 0) {
				myDbBook
						.execSQL("INSERT INTO "
								+ DispMgrUtility.CURRENT_INDEX_TABLE
								+ "( PageNo, ChapterNo, XmlLevel, LineNo, ZoomLevel) VALUES( "
								+ bookinfoDB.miDmbPageNo + ", "
								+ bookinfoDB.miDmbCurrentChapter + ", '"
								+ bookinfoDB.epubparser.getContentPath()
								+ "', " + lineno + ", "
								+ bookinfoDB.mfDmbZoomLevel + ");");
				// /ITS not WORKINFG
				ISCHAPTERCHANGE = 1;
			} else if (bookinfoDB.miDmbCurrentChapter == chnum + 1
					&& ISCHAPTERCHANGE == 0) {
				myDbBook
						.execSQL("INSERT INTO "
								+ DispMgrUtility.CURRENT_INDEX_TABLE
								+ "( PageNo, ChapterNo, XmlLevel, LineNo, ZoomLevel) VALUES( "
								+ bookinfoDB.miDmbPageNo + ", "
								+ bookinfoDB.miDmbCurrentChapter + ", '"
								+ bookinfoDB.epubparser.getContentPath()
								+ "', " + lineno + ", "
								+ bookinfoDB.mfDmbZoomLevel + ");");
				ISCHAPTERNOTCHANGED = 1;
			} else {
				IndexGenerationCrossedTheChapter = 1;
				ISCHAPTERCHANGE = 0;
				ISCHAPTERNOTCHANGED = 0;
			}
		} catch (Exception e) {
		}
		return 0;
	}

	/*
	 * Generating index(DB table) for FasterTOC to get previous page.This
	 * function run in background
	 */

	int mFasetTOCIndex() {
		DispMgrUtility utl = new DispMgrUtility();

		// Page status indicator
		boolean Page = true;
		// reset Retval
		int Retval = utl.DISPMGR_SUCCESS;
		// reset start page
		int StartPage = utl.EBOOK_TRUE;

		// to hold line height
		int LineHeight = 0;
		// paraindexpageup to page up operation
		int paraindexpageup;
		// change the values to fit into the bookview space
		mBookInfoForFasterTOC.miDmbViewYpos = 0;
		mBookInfoForFasterTOC.miDmbViewXpos = 0;
		// Assigns lineno
		LineNo = mBookInfoForFasterTOC.miDmbLineNo;
		// if buffer status is false then get the tag element count
		if (mBookInfoForFasterTOC.miDmbLineBufferStatus == utl.EBOOK_FALSE) {
			(mBookInfoForFasterTOC.miDmbPreContent) = (utl.XHTML_TAG_ELEMENT_COUNT);
		}
		// reset paraindexpageup
		paraindexpageup = 0;
		// page is true
		while (Page) {
			// Checking for endof the line buffer
			if (mBookInfoForFasterTOC.miDmbLineBufferStatus == utl.EBOOK_FALSE) {
				// if not start page
				if (StartPage == utl.EBOOK_FALSE) {
					// xhtml call to get the content type
					mBookInfoForFasterTOC.miDmbPreContent = mBookInfoForFasterTOC.epubparser
							.getContentType();
				}
				// To get the data from the .epub file(i.e Get The Book Content)

				Retval = mBookInfoForFasterTOC.mDisplayManagerGetData();
				mBookInfoForFasterTOC.msDmbXmlLevl = null;
				// if no space left in the page
				if (Retval != utl.DISPMGR_SUCCESS) {
					Page = false;
					LineNo = utl.DISPMGR_FIRSTLINE;
					break;
				}

				/* TO check first line of the buffer */
				if (LineNo == utl.DISPMGR_FIRSTLINE) {
					mBookInfoForFasterTOC.miDmbViewXpos = mBookInfoForFasterTOC.mDmPageStyleInfo.miPgstLeftMargin;
				} else {
					mBookInfoForFasterTOC.miDmbViewXpos = mBookInfoForFasterTOC.mDmPageStyleInfo.miPgstLeftMargin;
				}
			}
			/* If page starts with old buffer */
			else {
				/* create the corresponding type of new view */
				/* If it is first line of the buffer */
				if (LineNo == utl.DISPMGR_FIRSTLINE) {
					mBookInfoForFasterTOC.miDmbViewXpos = mBookInfoForFasterTOC.mDmPageStyleInfo.miPgstLeftMargin;
				} else {
					mBookInfoForFasterTOC.miDmbViewXpos = mBookInfoForFasterTOC.mDmPageStyleInfo.miPgstLeftMargin;
				}

			}
			// to set the y position between paragraphs and images
			if (StartPage == utl.EBOOK_TRUE) {
				if (mBookInfoForFasterTOC.epubparser.getContentType() == utl.XHTML_IMAGE) {
					mBookInfoForFasterTOC.miDmbViewYpos = 0;
				} else
					(mBookInfoForFasterTOC.miDmbViewYpos) = (mBookInfoForFasterTOC.miDmbViewYpos)
							+ (mBookInfoForFasterTOC.mDmPageStyleInfo.miPgstTopMargin);

			}
			// set line buffer status
			mBookInfoForFasterTOC.miDmbLineBufferStatus = utl.EBOOK_TRUE;
			// For image data
			if (mBookInfoForFasterTOC.epubparser.getContentType() == utl.XHTML_IMAGE) {
				ImageView temp = new ImageView(this);
				// get image path
				String ImagePath = mBookInfoForFasterTOC.mDispMgrLoadImage();
				// check image path is not null
				if (ImagePath != null) {
					// parse the imagepath
					Uri imageURI = Uri.parse(ImagePath);
					// set to image view
					temp.setImageURI(imageURI);
					// get Drawable components
					Drawable mdraw = temp.getDrawable();

					if (StartPage == utl.EBOOK_TRUE) {
						// Stores the index of a page to DB
						Retval = DISPMGR_StorePageBreakInfoForFasterToc(
								mBookInfoForFasterTOC, LineNo);
						// reset the startpage
						StartPage = utl.EBOOK_FALSE;
					}
					// check the page space in present page
					if ((DispMgrUtility.Layoutheight - mBookInfoForFasterTOC.miDmbViewYpos) >= mdraw
							.getMinimumHeight()) {
						// add image height to page size
						mBookInfoForFasterTOC.miDmbViewYpos += mdraw
								.getMinimumHeight();
						// set line buffer status
						mBookInfoForFasterTOC.miDmbLineBufferStatus = utl.EBOOK_FALSE;
						// set start page
						StartPage = utl.EBOOK_FALSE;

					}
					// check for a page space in new page
					else if ((DispMgrUtility.Layoutheight - mBookInfoForFasterTOC.miDmbViewYpos) < mdraw
							.getMinimumHeight()) {
						// to create new page for a image
						if (mBookInfoForFasterTOC.miDmbViewYpos != 0
								&& StartPage == utl.EBOOK_FALSE) {
							// set page
							Page = false;
							break;
						}
						// add image height to page size
						mBookInfoForFasterTOC.miDmbViewYpos += mdraw
								.getMinimumHeight();
						// set StartPage
						StartPage = utl.EBOOK_FALSE;
						// set page
						Page = false;
						// set line buffer status
						mBookInfoForFasterTOC.miDmbLineBufferStatus = utl.EBOOK_FALSE;
						break;

					}
					// assignment of line number
					LineNo = utl.DISPMGR_FIRSTLINE;
					continue;
				}
			}
			// For text data
			else {
				// create editview for the paragraph
				mTextpageup[paraindexpageup++] = new EditText(this);
				// set the padding
				mTextpageup[paraindexpageup - 1]
						.setPadding(
								mBookInfoForFasterTOC.miDmbViewXpos,
								0,
								utl.DISP_WIDTH
										- mBookInfoForFasterTOC.mDmPageStyleInfo.miPgstRightMargin,
								0);
				// set the text size
				mTextpageup[paraindexpageup - 1]
						.setTextSize(mBookInfoForFasterTOC.mDmPageStyleInfo.mfPgstFontSize
								* mBookInfoForFasterTOC.mfDmbZoomLevel);
				// set font family and style info
				mTextpageup[paraindexpageup - 1]
						.setTypeface(mBookInfoForFasterTOC.mDmPageStyleInfo.msPgstFontFamily);
				// set the page
				mTextpageup[paraindexpageup - 1].setWidth(utl.PAGEWIDTH);
				// line height of present line
				LineHeight = mTextpageup[paraindexpageup - 1].getLineHeight();
				// temp string to hold the split values
				String Temp[] = mBookInfoForFasterTOC.epubparser.getContent()
						.split("\n");

				while (true) {
					// line count is less than line number
					if (mBookInfoForFasterTOC.miDmbLineCount < LineNo) {
						// set line no to first line
						LineNo = utl.DISPMGR_FIRSTLINE;
						// set line buffer status
						mBookInfoForFasterTOC.miDmbLineBufferStatus = utl.EBOOK_FALSE;
						break;
					}
					// to check the space left in page
					if ((mBookInfoForFasterTOC.miDmbViewYpos) <= DispMgrUtility.Layoutheight)
						Retval = utl.DISPMGR_SUCCESS;
					else
						Retval = utl.DISPMGR_EOFPAGE;

					// Page boundary reached
					if (Retval == utl.DISPMGR_EOFPAGE) {
						Page = false;
						break;
					}
					// if it is a start of page
					if (StartPage == utl.EBOOK_TRUE) {
						// store page index to Index table
						Retval = DISPMGR_StorePageBreakInfoForFasterToc(
								mBookInfoForFasterTOC, LineNo);
						// set the start page
						StartPage = utl.EBOOK_FALSE;
					}
					// increment line number
					LineNo++;
					// increase the y position i.e decrees the available space
					// of a page
					mBookInfoForFasterTOC.miDmbViewYpos += LineHeight;

				}

			}

		}
		// store line no
		mBookInfoForFasterTOC.miDmbLineNo = LineNo;
		mBookInfoForFasterTOC.msDmbXmlLevl = null;
		return Retval;
	}

	

	/*
	 * The function is called to display the content of a book on screen
	 */
	int bookview_display_page() {
		DispMgrUtility utl = new DispMgrUtility();
		// set the page
		boolean Page = true;
		// set the return value
		int Retval = utl.DISPMGR_SUCCESS;
		// set start page
		int StartPage = utl.EBOOK_TRUE;
		// to hold line number
		int LineNo;
		// store line height
		int LineHeight = 0;
		// ADDED for annotation:jy
		mBookInfoForPageUp.rawbufferptr = null;
		// added for proto3 annotation
		String XMLlevel = null;
		int XmlLevelcount = 0;
		int mlineCount = 0;
		// set type fase
		pen.setTypeface(Typeface.SERIF);
		int mEndXpos;
		int mtotalstrlen = 0;
		int AnnotCount = 0;
		int mTotalNumbAnnot = 0;
		String temprawbufptr = null;
		int cnt = 0;
		int prevstrlen = 0;
		String temp1 = null;
		int TempYpos = 0;
		int PrevStrFlag = 0;
		int tempXmlLevelcnt = 0;

		/* For removing the annotation view on zoom */
		mAnnotAbsLay.removeAllViews();
		if (mBookInfo.mXmlLevelCount == 0) {
			tempXmlLevelcnt = 0;
		} else {
			tempXmlLevelcnt = mBookInfo.mXmlLevelCount - 1;
		}

		if (mBookInfo.miDmbLineBufferStatus == utl.EBOOK_TRUE) {
			temprawbufptr = new String();
			while (cnt < /* (mBookInfo.mXmlLevelCount - 1) */tempXmlLevelcnt) {
				prevstrlen += mXMLlevelParam[cnt].totalstrlen;
				cnt++;
			}
			temprawbufptr = mBookInfo.rawbufferptr;
			temprawbufptr = temprawbufptr.substring(prevstrlen);
			mBookInfo.rawbufferptr = "";
			mBookInfo.rawbufferptr = temprawbufptr;
			temprawbufptr = null;
			mXMLlevelParam[0].Total_lines = mXMLlevelParam[tempXmlLevelcnt].Total_lines;
			mBookInfo.miDmbLineCount = mXMLlevelParam[0].Total_lines;
			mlineCount = mXMLlevelParam[tempXmlLevelcnt].XmlLvlProLineCnt;
			mtotalstrlen = mXMLlevelParam[tempXmlLevelcnt].totalstrlen;

			for (cnt = 0; cnt < mlineCount; cnt++) {
				mXMLlevelParam[0].Strlen_eachline[cnt] = mXMLlevelParam[tempXmlLevelcnt].Strlen_eachline[cnt];
			}

			mXMLlevelParam[0].ZoomLevel = mXMLlevelParam[tempXmlLevelcnt].ZoomLevel;
			mXMLlevelParam[0].LineBufferStatusFlag = utl.EBOOK_TRUE;
			mXMLlevelParam[0].PrevLineProcessed = mXMLlevelParam[tempXmlLevelcnt].XmlLvlProLineCnt;
			mXMLlevelParam[0].aui8xmllvl = mXMLlevelParam[tempXmlLevelcnt].aui8xmllvl;
			mXMLlevelParam[0].totalstrlen = mXMLlevelParam[tempXmlLevelcnt].totalstrlen;
			for (int i = 1; i < mBookInfo.mXmlLevelCount; i++) {
				mXMLlevelParam[i] = null;
			}
		} else {
			if (mBookInfo.miDmbLineNo != 1) {
				mlineCount = mBookInfo.miDmbLineNo - 1;
			}
			mBookInfo.rawbufferptr = "";
			mXMLlevelParam = new DispMgrBookInfo.XmlLevel_param[250];
			for (int i = 0; i < mBookInfo.mXmlLevelCount; i++) {
				mXMLlevelParam[i] = null;
			}
			mXMLlevelParam[0] = mBookInfo.new XmlLevel_param();
			mXMLlevelParam[0].Strlen_eachline = new int[1000];
		}
		// set y position
		mBookInfo.miDmbViewYpos = 0;
		// set x position
		mBookInfo.miDmbViewXpos = 0;
		// assigene line number
		LineNo = mBookInfo.miDmbLineNo;
		// if ebook status is false
		if (mBookInfo.miDmbLineBufferStatus == utl.EBOOK_FALSE) {
			(mBookInfo.miDmbPreContent) = (utl.XHTML_TAG_ELEMENT_COUNT);
		}
		// remove all layouts used to display previous page
		mRelativeLayout.removeAllViews();
		// remove all views used in constructing previous page
		mPageLayout.removeAllViews();
		// reset para index
		mParaIndex = 0;
		// reset image count index
		mImageIndex = 0;
		// reset display text index
		mTempedittextindex = 0;
		mTempText[mTempedittextindex] = new EditText(this);
		// if imageflag is 0
		if (mimageflag == 0) {
			// set the edittext background color
			mTempText[mTempedittextindex].setBackgroundColor(Color.WHITE);
			// add the view to page layout
			mPageLayout.addView(mTempText[mTempedittextindex]);
			// cancel the long press
			mTempText[mTempedittextindex].setLongClickable(false);
			// set id to dynamically generated edittext
			mTempText[mTempedittextindex].setId(mTempedittextindex);
			// setOnClickListener to perform text selection on the edittext
			// mTempText[mTempedittextindex].setOnClickListener(meditclick);
		}
		// while page size is not reache the end
		while (Page) {
			int mIndex = 0;
			/* Checking for end of the line buffer */
			if (mBookInfo.miDmbLineBufferStatus == utl.EBOOK_FALSE) {
				if (StartPage == utl.EBOOK_FALSE) {
					// xhtml call to get the content type
					mBookInfo.miDmbPreContent = mBookInfo.epubparser
							.getContentType();
				}
				// get the data from EPUB book i.e EPUB parser
				Retval = mBookInfo.mDisplayManagerGetData();
				
				lineNumberForPageUpTOC = mBookInfo.miDmbLineNo;
				XmlLevelForPageUpTOC = mBookInfo.epubparser.getContentPath();
			
				// if return value of mDisplayManagerGetData is false
				if (Retval != utl.DISPMGR_SUCCESS) {
					// set end of page
					Page = false;
					// reset line number
					LineNo = utl.DISPMGR_FIRSTLINE;
					break;
				}

				// Added for Annotation
				mXMLlevelParam[XmlLevelcount].aui8xmllvl = mBookInfo.epubparser
						.getContentPath();
				mXMLlevelParam[XmlLevelcount].Total_lines = mBookInfo.miDmbLineCount;
				mXMLlevelParam[XmlLevelcount].ZoomLevel = mBookInfo.mfDmbZoomLevel;
				// set xmllevel null to go to next page
				mBookInfo.msDmbXmlLevl = null;

				// TO check first line of the buffer
				if (LineNo == utl.DISPMGR_FIRSTLINE) {
					mBookInfo.miDmbViewXpos = mBookInfo.mDmPageStyleInfo.miPgstLeftMargin;
				} else {
					mBookInfo.miDmbViewXpos = mBookInfo.mDmPageStyleInfo.miPgstLeftMargin;
				}
			}
			// If page starts with old buffer
			else {
				// If it is first line of the buffer
				if (LineNo == utl.DISPMGR_FIRSTLINE) {
					mBookInfo.miDmbViewXpos = mBookInfo.mDmPageStyleInfo.miPgstLeftMargin;
				} else {
					mBookInfo.miDmbViewXpos = mBookInfo.mDmPageStyleInfo.miPgstLeftMargin;
				}

			}
			// added for proto3 Annotation

			mXMLlevelParam[XmlLevelcount].ParaOffset = mBookInfo.mDmPageStyleInfo.miPgstLeftMargin;
			mXMLlevelParam[XmlLevelcount].LeftMargin = mBookInfo.mDmPageStyleInfo.miPgstLeftMargin;

			// if its a start of page
			if (StartPage == utl.EBOOK_TRUE) {
				// if the content is image set y position
				if (mBookInfo.epubparser.getContentType() == utl.XHTML_IMAGE) {
					mBookInfo.miDmbViewYpos = 0;
				}
				// if it is text set y position
				else {
					(mBookInfo.miDmbViewYpos) = (mBookInfo.miDmbViewYpos)
							+ (mBookInfo.mDmPageStyleInfo.miPgstTopMargin);
				}

			}
			// set buffer status
			mBookInfo.miDmbLineBufferStatus = utl.EBOOK_TRUE;
			// For image data
			if (mBookInfo.epubparser.getContentType() == utl.XHTML_IMAGE) {
				// if we have any text content
				if (mParaIndex > 0) {
					// increase the mTempedittextindex
					mTempedittextindex++;
					mTempText[mTempedittextindex] = new EditText(this);
					// sets background color
					mTempText[mTempedittextindex]
							.setBackgroundColor(Color.WHITE);
					// set image flag
					mimageflag = 1;

				}
				ImageView temp = new ImageView(this);
				// store the image path returned by mDispMgrLoadImage
				ImagePath = mBookInfo.mDispMgrLoadImage();
				// if Image path is not null
				if (ImagePath != null) {
					// parse the image
					Uri imageURI = Uri.parse(ImagePath);
					
					//___________________________________________
					mAllUriArray[mImageIndex] = ImagePath;
					
					
					// set the image to image view
					temp.setImageURI(imageURI);
					// get the Drawable properties of image
					Drawable mdraw = temp.getDrawable();
					// check for the page space if space exist
					if ((DispMgrUtility.Layoutheight - mBookInfo.miDmbViewYpos) >= mdraw
							.getMinimumHeight()) {
						// create image viev
						mImage[mImageIndex++] = new ImageView(this);

						// add image view to page layout
						mPageLayout.addView(mImage[mImageIndex - 1]);
						// store the image path returned by mDispMgrLoadImage
						ImagePath = mBookInfo.mDispMgrLoadImage();
						// parse the image
						imageURI = Uri.parse(ImagePath);
						
						
						
						
						// set the image to image view
						mImage[mImageIndex - 1].setImageURI(imageURI);

						// set the background of image view
						mImage[mImageIndex - 1].setBackgroundColor(Color.WHITE);
						// increase the y position i.e decrease the space left
						// in page
						mBookInfo.miDmbViewYpos += mdraw.getMinimumHeight();
						// set line buffer status
						mBookInfo.miDmbLineBufferStatus = utl.EBOOK_FALSE;
						// set start page
						StartPage = utl.EBOOK_FALSE;

					}
					// if page not have space to hold the image create new page
					else if ((DispMgrUtility.Layoutheight - mBookInfo.miDmbViewYpos) < mdraw
							.getMinimumHeight()) {
						if (mBookInfo.miDmbViewYpos != 0
								&& StartPage == utl.EBOOK_FALSE) {
							// reset page
							Page = false;
							break;
						}
						// create image view
						mImage[mImageIndex++] = new ImageView(this);
						// add image view to page layout
						mPageLayout.addView(mImage[mImageIndex - 1]);
						// store the image path returned by mDispMgrLoadImage
						ImagePath = mBookInfo.mDispMgrLoadImage();
						// parse the image
						
						//___________________________________________________________________________
						imageURI = Uri.parse(ImagePath);
						// set the image to image view
						mImage[mImageIndex - 1].setImageURI(imageURI);
						// set the background of image view
						mImage[mImageIndex - 1].setBackgroundColor(Color.WHITE);
						// increase the y position i.e decrease the space left
						// in page
						mBookInfo.miDmbViewYpos += mdraw.getMinimumHeight();
						// set atart page
						StartPage = utl.EBOOK_FALSE;
						// set page
						Page = false;
						// set line buffer status
						mBookInfo.miDmbLineBufferStatus = utl.EBOOK_FALSE;
						break;

					}
					// if image flag is set previously
					if (mimageflag == 1) {
						// add the edittext to page layout
						mPageLayout.addView(mTempText[mTempedittextindex]);
						// set long clickable false
						mTempText[mTempedittextindex].setLongClickable(false);
						// set id for edittext
						mTempText[mTempedittextindex].setId(mTempedittextindex);
						// set onclick listener for text selection
						// mTempText[mTempedittextindex].setOnClickListener(meditclick);
						// reset image flag
						mimageflag = 0;
					}
					// assigne line number
					LineNo = utl.DISPMGR_FIRSTLINE;
					mlineCount = 0;
					continue;
				}
			}
			// For text data
			else {
				// create edittext to hold paragraph
				mText[mParaIndex++] = new EditText(this);
				// set padding to display the text
				mText[mParaIndex - 1].setPadding(mBookInfo.miDmbViewXpos, 0,
						utl.DISP_WIDTH
								- mBookInfo.mDmPageStyleInfo.miPgstRightMargin,
						0);
				// set text size
				mText[mParaIndex - 1]
						.setTextSize(mBookInfo.mDmPageStyleInfo.mfPgstFontSize
								* mBookInfo.mfDmbZoomLevel);
				// set font family
				mText[mParaIndex - 1]
						.setTypeface(mBookInfo.mDmPageStyleInfo.msPgstFontFamily);
				// get the line height
				LineHeight = mText[mParaIndex - 1].getLineHeight();

				// For text selection
				// storing the text size and font of each line of edittext in an
				// array... this is to be used in text selection and
				// highlighting

				mAllLineSizeArray[mParaIndex - 1] = (int) mText[mParaIndex - 1]
						.getTextSize();
				mAllLineFontArray[mParaIndex - 1] = mText[mParaIndex - 1]
						.getTypeface();

				// string to hold the line by line information
				String Temp[] = mBookInfo.epubparser.getContent().split("\n");
				mTempBuf = "";

				mXMLlevelParam[XmlLevelcount].StartXpos = mBookInfo.miDmbViewXpos;
				mXMLlevelParam[XmlLevelcount].StartYpos = (int) (mBookInfo.miDmbViewYpos);
				mXMLlevelParam[XmlLevelcount].FontSize = (int) LineHeight;
				mXMLlevelParam[XmlLevelcount].LineSpace = mBookInfo.mDmPageStyleInfo.miPgstLineSpace;
				// set the font size
				pen.setTextSize(mBookInfo.mDmPageStyleInfo.mfPgstFontSize);

				int CHaptID = mBookInfo.miDmbCurrentChapter;
				XMLlevel = mBookInfo.epubparser.getContentPath();
				AnnotCount = Get_Annotation_Count(XMLlevel, CHaptID);

				if (AnnotCount != 0) {

					mAnnotInfo = new Annotinfo[AnnotCount];

					// AnnotInfo class members
					for (int i = 0; i < AnnotCount; i++) {

						mAnnotInfo[i] = Fill_Annotation_info(XMLlevel, i,
								CHaptID);
						mTotalNumbAnnot++;

					}
					// offset adding
					Annotation_SortOffset(mAnnotInfo, AnnotCount);

				}

				while (true) {
					// if line count is less than line no
					if (mBookInfo.miDmbLineCount < LineNo) {
						// reset line no
						LineNo = utl.DISPMGR_FIRSTLINE;
						// reset line buffer status
						mBookInfo.miDmbLineBufferStatus = utl.EBOOK_FALSE;
						break;
					}
					// to check the page size left in a screen
					if ((mBookInfo.miDmbViewYpos) <= DispMgrUtility.Layoutheight)
						Retval = utl.DISPMGR_SUCCESS;
					else
						Retval = utl.DISPMGR_EOFPAGE;

					// Page boundary reached
					if (Retval == utl.DISPMGR_EOFPAGE) {
						// set page
						Page = false;
						break;
					}
					// if its a startof a page
					if (StartPage == utl.EBOOK_TRUE) {
						StartPage = utl.EBOOK_FALSE;
					}
					// put line by line information to edittext
					if (Temp[LineNo - 1].length() != 0) {
						mText[mParaIndex - 1].append(Temp[LineNo - 1]);
						mText[mParaIndex - 1].append("\n");
					}
					// increase line count
					LineNo++;

					// Poulate LineInfo Class members
					if (mBookInfo.miDmbLineBufferStatus == utl.EBOOK_TRUE) {
						mXMLlevelParam[XmlLevelcount].EndYpos = mBookInfo.miDmbViewYpos;
					}
					if (mLineInfo != null) {
						if ((("").equals(mLineInfo.linestart))
								&& (mLineInfo.PrevlineStrlen_count == 0)) {

							TempYpos = mLineInfo.LineYpos;
							PrevStrFlag = 1;
						}
					}

					mLineInfo = new LineInfo();
					mLineInfo.linestart = Temp[mlineCount];
					mLineInfo.LineNo = LineNo - 1;
					mLineInfo.ZoomLevel = mBookInfo.mfDmbZoomLevel;
					mLineInfo.ParaOffset = mBookInfo.mDmPageStyleInfo.miPgstStartXPos;
					mLineInfo.LeftMargin = mBookInfo.miDmbViewXpos;

					if (PrevStrFlag == 1) {
						mLineInfo.LineYpos = TempYpos;
						PrevStrFlag = 0;
					} else {
						mLineInfo.LineYpos = mBookInfo.miDmbViewYpos;
					}
					mLineInfo.LineSpace = mBookInfo.mDmPageStyleInfo.miPgstLineSpace;
					mLineInfo.AnnotationCnt = AnnotCount;
					mLineInfo.FontSize = (int) LineHeight;

					if ((LineNo - 1) != 1) {
						mLineInfo.Strlen_count = mtotalstrlen;
					}
					mLineInfo.Strlen_count += Temp[mlineCount].length();
					mLineInfo.PrevlineStrlen_count = (mLineInfo.Strlen_count)
							- Temp[mlineCount].length();
					int i = 0;
					if (AnnotCount != 0) {
						// It will calculate Annot icon's X,Y co-ordinate
						if (IconPos == null)
							IconPos = new int[AnnotCount][4];

						mIndex = Annotation_XY_cal(mAnnotInfo, mLineInfo,
								IconPos, mIndex);

					}
					// increase the y position i.e decrease the page size
					// remaining
					mBookInfo.miDmbViewYpos += LineHeight;

					if (mlineCount <= mBookInfo.miDmbLineCount) {

						mXMLlevelParam[XmlLevelcount].Strlen_eachline[mlineCount] = Temp[mlineCount]
								.length();
					}

					/*
					 * Added as linecount starts from 0 and newlinecount gives
					 * +1 count
					 */
					if (mlineCount == mBookInfo.miDmbLineCount - 1) {
						mEndXpos = (int) pen.measureText(Temp[mlineCount]);
						mXMLlevelParam[XmlLevelcount].EndXpos = mEndXpos
								+ mBookInfo.miDmbViewXpos;
						mXMLlevelParam[XmlLevelcount].EndYpos = (int) (mBookInfo.miDmbViewYpos - LineHeight);
					}

					mtotalstrlen += Temp[mlineCount].length();
					mlineCount++;
				}
				IconAddonAbsl(mIndex);
				IconPos = null;
				// set background cloor
				mTempText[mTempedittextindex].setBackgroundColor(Color.WHITE);
				// store line by line text in a string to set the font and size
				String tempstr = mText[mParaIndex - 1].getText().toString();
				// length of a line
				int paralength = tempstr.length();
				// append to the edittext
				mTempText[mTempedittextindex].append(tempstr);
				// spannable string to apply size and font information to
				// edittext
				Spannable str = mTempText[mTempedittextindex].getText();
				// length of spannable string
				int length = str.length();
				// to set font style information
				str.setSpan(new StyleSpan(
						(int) mBookInfo.mDmPageStyleInfo.miPgstFontStyle),
						length - paralength, length,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// to set typeface infoormation
				str.setSpan(new TypefaceSpan("SERIF"), length - paralength,
						length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// to set font size
				str
						.setSpan(
								new AbsoluteSizeSpan(
										(int) (mBookInfo.mDmPageStyleInfo.mfPgstFontSize * mBookInfo.mfDmbZoomLevel)),
								length - paralength, length,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			/* Added for annotation */
			mXMLlevelParam[XmlLevelcount].XmlLvlProLineCnt = mlineCount;
			mXMLlevelParam[XmlLevelcount].totalstrlen = mtotalstrlen;
			XmlLevelcount++;
			mtotalstrlen = 0;
			mlineCount = 0;

			if (mLineInfo != null) {
				mLineInfo.Strlen_count = 0;
				mLineInfo.PrevlineStrlen_count = 0;
			}

			if (Page) {
				mXMLlevelParam[XmlLevelcount] = mBookInfo.new XmlLevel_param();
				mXMLlevelParam[XmlLevelcount].Strlen_eachline = new int[50];
			}
		}
		// set page number
		mPage.setText("     " + mBookInfo.miDmbPageNo);
		// set page no font size
		mPage.setTextSize(12);
		// set text color of page number
		mPage.setTextColor(Color.BLACK);
		// add the dynamically generated page to parent layout
		mRelativeLayout.addView(mPageLayout);
		// to add the icons
		// adding the covering container for text selection using pixel method
		mRelativeLayout.addView(mDragDropMenuContainer);
		// store line number
		mBookInfo.miDmbLineNo = LineNo;
		// reset xmllevel
		mBookInfo.msDmbXmlLevl = null;
		mBookInfo.mXmlLevelCount = XmlLevelcount;
		/* Adding Abs layout for annotation */
		mRelativeLayout.addView(mAnnotAbsLay);
		return Retval;

	}

	/*
	 * used for generation index for page up navigation its done in background
	 */
	class DataBaseOperations extends AsyncTask<String, Void, Void> {

		protected void onPreExecute() {

			/*
			 * myDbBook = Display_Manager.this.openOrCreateDatabase(
			 * DispMgrUtility.DATABASE_NAME_BOOK, 1, null);
			 */
			myDbBook
					.execSQL("CREATE TABLE IF NOT EXISTS "
							+ DispMgrUtility.INDEX_TABLE
							+ " (PageNo INT,ChapterNo INT,XmlLevel VARCHAR,LineNo INT,ZoomLevel FLOAT);");

		}

		// Task to be done in background
		protected Void doInBackground(String... params) {
			int retvalofPageup = utl.DISPMGR_SUCCESS;
			Cursor PageCursore;
			while (retvalofPageup != utl.DISPMGR_ENDOFBOOK) {
				if (IndexGeneratinfortocorzoom == 0) {
					try {
						mSemaphore.acquire();
						if (mZoomClicked == 1) {
							mBookInfoForPageUp.msDmbXmlLevl = mBookInfo
									.mResetXmllevelForZoom(myDbBook,
											mBookInfo.miDmbCurrentChapter,
											mBookInfo.miDmbPageNo);
							mBookInfoForPageUp.epubparser
									.setContentPath(mBookInfoForPageUp.msDmbXmlLevl);
							myDbBook.execSQL("DELETE FROM IndexTable;");
							mBookInfoForPageUp.miDmbCurrentChapter = 0;
							mBookInfoForPageUp.miDmbPageNo = 1;
							mBookInfoForPageUp.miDmbBookStart = utl.EBOOK_TRUE;
							mBookInfoForPageUp.miDmbLineNo = utl.DISPMGR_FIRSTLINE;
							mBookInfoForPageUp.miDmbLineBufferStatus = utl.EBOOK_FALSE;
							mBookInfoForPageUp.miDmbBookStatus = utl.EBOOK_TRUE;
						//	mBookInfoForPageUp.mFasterTOc = 1;
						    mBookInfoForPageUp.BypassgetnextField = 1;
							mZoomClicked = 0;
						}
						mBookInfoForPageUp.mObjDifFlag = 2;

					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if (!mDbopp.isCancelled()) {
						retvalofPageup = mPageIndex();
						mBookInfoForPageUp.msDmbXmlLevl = null;
						mBookInfoForPageUp.miDmbPageNo++;
					} else {
						retvalofPageup = utl.DISPMGR_ENDOFBOOK;
						// check the last in which view library was opened
						// if it was in GridView
						Intent mIntentLibraryBack = null;

						if (mIsGoingHome == 0) {
							if (EpubReader.mIsListView == EpubReader.GRID_VIEW) {
								mIntentLibraryBack = new Intent(
										Display_Manager.this,
										LibraryMainPage.class);

							}
							// if it was in ListView
							if (EpubReader.mIsListView == EpubReader.LIST_VIEW) {
								mIntentLibraryBack = new Intent(
										Display_Manager.this,
										LibraryMainPageList.class);
							}
						} else if (mIsGoingHome == 1) {
							mIntentLibraryBack = new Intent(
									Display_Manager.this, EpubReader.class);
							mIsGoingHome = 0;
						} else {
						}
						
						myDbBook.execSQL("DROP TABLE IF EXISTS IndexTable;");
						// myDbBook.execSQL("DROP TABLE IF EXISTS FasterTOC;");
						Display_Manager.this.finish();
						startActivity(mIntentLibraryBack);
					}
					mBookInfoForPageUp.mObjDifFlag = 0;
					mSemaphore.release();
				} else {

					continue;
				}
			}
			mIndexGenerationStatus = COMLETED;
			TOCClicked = 0;
			// GetDataFromFasterToc = 0;
			return null;

		}

	}

	/*
	 * used for generation index for Faster TOC Operation
	 */
	class IndexGeneration extends AsyncTask<String, Void, Void> {

		protected void onPreExecute() {
			super.onPreExecute();

			
			// Table to hold current chapter index (i.e selected chapters index)
			myDbBook
					.execSQL("CREATE TABLE IF NOT EXISTS "
							+ DispMgrUtility.CURRENT_INDEX_TABLE
							+ " (PageNo INT,ChapterNo INT,XmlLevel VARCHAR,LineNo INT,ZoomLevel FLOAT);");
			// Table to hold previous chapter index (i.e previous chapter of
			// selected)

			// flag to indicate index generation started
			IndexGeneratinfortocorzoom = 1;
			// flag to show the page displayd is from Faster toc tables
			mBookInfoForFasterTOC.mFasterTOc = 1;
			// To checked for TOC is Clicked or not
			TOCClicked = 1;

		}

		protected void onPostExecute(final Void unused) {
			if (dialog.isShowing()) {

				dialog.dismiss();
			}
		}

		// Task to be done in background
		protected Void doInBackground(String... params) {
			int retvalofPageup = utl.DISPMGR_SUCCESS;

			Cursor tempcursor = null;

			try {
				// acquire semaphore
				mSemaphore.acquire();
				// set the flag for JNI operation
				mBookInfoForFasterTOC.mObjDifFlag = 3;

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			while (true) {

				if (!mFastTOC.isCancelled()) {
					// set page number flag to show displayed page with wrong
					// page number
					ResetPageNumForTOC = 1;
					// generate page to store in Faster TOC tables
					retvalofPageup = mFasetTOCIndex();
					// increment page number
					mBookInfoForFasterTOC.miDmbPageNo++;
					// if index generation finished then finish the while loop
					if (IndexGenerationCrossedTheChapter == 1) {
						// reset the flag
						IndexGenerationCrossedTheChapter = 0;
						break;
					}

				}
			}

			myDbBook.execSQL("DELETE FROM "
					+ DispMgrUtility.CURRENT_INDEX_TABLE
					+ " WHERE  ChapterNo  = " + (chnum + 2) + ";");

			// Flag To Check The IndexGeneration For TOC/Zoom is Finished
			IndexGeneratinfortocorzoom = 0;
			mBookInfoForFasterTOC.mObjDifFlag = 0;
			StopGeneratingCurrentIndextableAgain = 0;
			// relese semaphore
			mSemaphore.release();

			return null;

		}

	}

	/*
	 * class to show the placeholeder in bookview page
	 */

	public class BookViewAdapter extends BaseAdapter {
		public BookViewAdapter(Context c) {
			mContext1 = c;
		}

		public int getCount() {
			return mThumbIds1.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(mContext1);
			if (convertView == null) {
				imageView = new ImageView(mContext1);
				imageView.setLayoutParams(new GridView.LayoutParams(48, 48));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setImageResource(mThumbIds1[position]);
			return imageView;
		}

		private Context mContext1;
		// this array will store all the icons for place holder
		private Integer[] mThumbIds1 = { R.drawable.libraryicon,
				R.drawable.disabledinformationicon, R.drawable.disabledsearchicon,
				R.drawable.dictionaryicon, R.drawable.bookmarkicon,
				R.drawable.annotateicon, R.drawable.disabledunderlineicon,
				R.drawable.disabledsettingsicon

		};
	}

	public void onPause() {
		super.onPause();
		mCabinetOpen = 0;
	}

	// Touch listener for absolute layout added for pixel text selection
	OnTouchListener mTextSelectListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {

			// TODO Auto-generated method stub
			switch (v.getId()) {
			case LAYOUT_ID: {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {

					mImageSelected = false;

					// fetching the x, y pixel coordinates from where the text
					// highlighting and spanning is to be started
					mTextSelectionStart_X = (int) event.getRawX();
					mTextSelectionStart_Y = (int) event.getRawY();

					mSelectionEditText = mTempText[0];

					// for selection in multiple edittexts
					for (int i = 0; i <= mTempedittextindex; i++) {

						if ((mTextSelectionStart_Y - UPPER_BAR_OFFSET) < mTempText[i]
								.getBottom()
								&& (mTextSelectionStart_Y - UPPER_BAR_OFFSET) > mTempText[i]
										.getTop()) {
							mSelectionEditText = mTempText[i];
						}
					}

					// for selection when there are more than 1 images in the
					// page
					for (int i = 0; i <= mImageIndex - 1; i++) {
						if ((mTextSelectionStart_Y - UPPER_BAR_OFFSET) < mImage[i].getBottom()
								&& (mTextSelectionStart_Y - UPPER_BAR_OFFSET) > mImage[i].getTop()) {
							mSelectionImage = mImage[i];

							ImagePath = mAllUriArray[i];
							
							//String str = ImagePath;

							mImageSelected = true;

							// calling method createImageToCopy() for creating
							// the new ImageView to be dragged
							createImageToCopy();
							break;
						}

					}

					if (mImageSelected == true
							|| mSelectionEditText.getText().toString()
									.equalsIgnoreCase(""))
						break;


					// getting the string from edittext to be worked on
					colourString = mSelectionEditText.getText();

					// removing text to be dragged before drag and drop
					// operation
					mDragDropMenuContainer.removeView(mCopyEditText);

					// setting the span to white before doing span
					colourString.setSpan(new BackgroundColorSpan(Color.WHITE),
							0, mSelectionEditText.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

					Annotation_StartXPos = utl.EBOOK_TRUE;

					// calling method calculateCharacterIndex for calculating
					// character index from where selection is to be started
					mTextSelectionIndexStart = calculateCharacterIndexY(
							mTextSelectionStart_X - LEFT_BAR_OFFSET,
							mTextSelectionStart_Y - UPPER_BAR_OFFSET);

					break;
				}
				case MotionEvent.ACTION_MOVE: {

					if (mImageSelected == true
							|| mSelectionEditText.getText().toString()
									.equalsIgnoreCase(""))
						break;

					// fetching the x, y pixel coordinates to where the
					// selection and highlighting is to be continued

					mTextSelectionMove_X = (int) event.getRawX();
					mTextSelectionMove_Y = (int) event.getRawY();

					// calling method calculateCharacterIndex for calculating
					// character index from where selection is to be continued
					mTextSelectionIndexMove = calculateCharacterIndexY(
							mTextSelectionMove_X - LEFT_BAR_OFFSET,
							mTextSelectionMove_Y - UPPER_BAR_OFFSET);

					// setting the span colour white before doing selection
					// spanning
					colourString.setSpan(new BackgroundColorSpan(Color.WHITE),
							0, mSelectionEditText.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

					// checking if the move index exceeds the total length of
					// edittext
					// if it exceeds then set move idex to total edittext length
					if (mTextSelectionIndexMove > mSelectionEditText.length()) {
						mTextSelectionIndexMove = mSelectionEditText.length();
					}

					// checking if start and end character index is less than
					// edittext length and greater than 0...
					if (mTextSelectionIndexStart >= 0 && mTextSelectionIndexMove >= 0 
							&& mTextSelectionIndexStart <= mSelectionEditText.length() && mTextSelectionIndexMove <= mSelectionEditText.length()
							  ) {

						// checking if start char index is less than end char
						// index
						if (mTextSelectionIndexStart < mTextSelectionIndexMove) {

							// highlighting the selected text
							colourString.setSpan(new BackgroundColorSpan(Color
									.rgb(240, 192, 242)),
									mTextSelectionIndexStart,
									mTextSelectionIndexMove,
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

							// fetching and storing the selected text to do drag
							// and drop operation
							mTempStr = mSelectionEditText.getText().toString()
									.substring(mTextSelectionIndexStart,
											mTextSelectionIndexMove);

						} else {

							// highlighting the selected text
							colourString.setSpan(new BackgroundColorSpan(Color
									.rgb(240, 192, 242)),
									mTextSelectionIndexMove,
									mTextSelectionIndexStart,
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

							// fetching and storing the selected text to do drag
							// and drop operation
							mTempStr = mSelectionEditText.getText().toString()
									.substring(mTextSelectionIndexMove,
											mTextSelectionIndexStart);
						}
					}

					break;
				}

				case MotionEvent.ACTION_UP: {

					if (mImageSelected == true
							|| mSelectionEditText.getText().toString()
									.equalsIgnoreCase(""))
						break;

					// fetching the x, y pixel coordinates where the
					// selection and highlighting will end
					mTextSelectionEnd_X = (int) event.getRawX();
					mTextSelectionEnd_Y = (int) event.getRawY();

					Annotation_EndXPos = utl.EBOOK_TRUE;

					// calling method calculateCharacterIndex for calculating
					// character index from where selection ends
					mTextSelectionIndexEnd = calculateCharacterIndexY(
							mTextSelectionEnd_X - LEFT_BAR_OFFSET,
							mTextSelectionEnd_Y - UPPER_BAR_OFFSET);

					// setting the span colour white before doing selection
					// spanning
					colourString.setSpan(new BackgroundColorSpan(Color.WHITE),
							0, mSelectionEditText.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

					// checking if the end index exceeds the total length of
					// edittext
					// if it exceeds then set end idex to total edittext length
					if (mTextSelectionIndexEnd > mSelectionEditText.length()) {
						mTextSelectionIndexEnd = mSelectionEditText.length();
					}

					// checking if start and end character index is less than
					// edittext length and greater than 0...
					if (mTextSelectionIndexStart >= 0
							&& mTextSelectionIndexEnd >= 0
							&& mTextSelectionIndexStart <= mSelectionEditText
									.length()
							&& mTextSelectionIndexEnd <= mSelectionEditText
									.length() ) {

						// checking if start char index is less than end char
						// index
						if (mTextSelectionIndexStart < mTextSelectionIndexEnd) {

							// highlighting the selected text
							colourString.setSpan(new BackgroundColorSpan(Color
									.rgb(240, 192, 242)),
									mTextSelectionIndexStart,
									mTextSelectionIndexEnd,
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

							// fetching and storing the selected text to do drag
							// and drop operation
							mTempStr = mSelectionEditText.getText().toString()
									.substring(mTextSelectionIndexStart,
											mTextSelectionIndexEnd);
						} else {

							// highlighting the selected text
							colourString.setSpan(new BackgroundColorSpan(Color
									.rgb(240, 192, 242)),
									mTextSelectionIndexEnd,
									mTextSelectionIndexStart,
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

							// fetching and storing the selected text to do drag
							// and drop operation
							mTempStr = mSelectionEditText.getText().toString()
									.substring(mTextSelectionIndexEnd,
											mTextSelectionIndexStart);

						}

					}

					// calling method createTextToCopy() for drag and drop
					// operation

					if (mTempStr != null && !mTempStr.equalsIgnoreCase(""))
						createTextToCopy();

					break;
				}

				}

			}// end of case LAYOUT_ID....

			} // end of switch(v.getId())

			return true;
		}// end of onTouch

	};// end of mTextSelectListener

	// method to swap the

	// This method is for calculating the index of the character
	// from where the text selection starts or ends
	protected int calculateCharacterIndexY(int mTextSelectionPixel_X,
			int mTextSelectionPixel_Y) {

		// integer that holds height of each line
		int linehieght = 0;

		// integer that holds the character index at touched pixel
		int mTextSelectionCharIndex = 0;
		// TODO Auto-generated method stub

		// 
		int mAllLineHeight = mSelectionEditText.getTop();

		// int mAllLineHeight = mTempText[0].getTop();

		// integer that holds the no. of characters in a line
		int mTempCharIndex = 0;

		int mTempCharIndex_X = 0;

		// edit text that regenerates the properties of each line displayed
		EditText et = new EditText(this);

		// str that holds the whole text of the edittext displayed
		String str = mSelectionEditText.getText().toString();

		// string array that holds content of each line
		String strarray[] = str.split("\n");

		// this code is for checking the y pixel position by adding the height
		// of each line of edit text and calculates index of starting character
		// of each line and stops where the total line height becomes greater
		// than the y pixel and returns the index of char and returns it to
		// calling code
		int i;
		for (i = 0; i < strarray.length
				&& mAllLineHeight < mTextSelectionPixel_Y; i++) {

			// setting the properties of text displayed to new temp edittext
			// which is fo
			// the height calculation...
			et.setText(strarray[i]);
			et.setTextSize(mAllLineSizeArray[i]);
			et.setTypeface(mAllLineFontArray[i]);

			// taking the height of each line of the edittext and adding it to
			// total line height for y calculation
			linehieght = et.getLineHeight();
			mAllLineHeight = mAllLineHeight + linehieght;

			// checking when the total line height is less than the y pixel
			// value
			// or linecount is equal to total line in edittext
			if (mAllLineHeight <= mTextSelectionPixel_Y && i < strarray.length) {

				// adding total chars in each line to total index count
				mTempCharIndex = mTempCharIndex + strarray[i].length() + 1;
			} else if (mAllLineHeight > mTextSelectionPixel_Y) {
				break;
			}

		}

		// sending the string of line to calculateCharacterIndex on which the
		// text
		// selection measurement is to be done.
		if (i == strarray.length)
			mTempCharIndex_X = calculateCharacterIndexX(strarray[i - 1],
					mTextSelectionPixel_X, i - 1);

		else
			mTempCharIndex_X = calculateCharacterIndexX(strarray[i],
					mTextSelectionPixel_X, i);

		// adding the index of returned edittext line to total index count
		mTextSelectionCharIndex = mTempCharIndex + mTempCharIndex_X;

		return mTextSelectionCharIndex;

	}

	// this method is for measuring the text in each edittext line
	// and calculating the index of character from which the seletion is
	// started, moved or ended.
	int calculateCharacterIndexX(String tempStr, int mTextSelectionPixelX,
			int index) {

		Paint mPen = new Paint();

		mPen.setTextSize(mAllLineSizeArray[index]);
		mPen.setTypeface(mAllLineFontArray[index]);

		// this value stores the index of characters in the line while selecting
		// this value increments after each char selection, this value gives the
		// actual index from where the text selection is to be started or end.
		int mCharIndexX = 0;

		// this value stores pixel width till the char which is chosen for
		// selection
		int mCharPixelX = 0;

		// this value stores the count of spaces in the line
		int mCharSpaceCount = 0;

		char mChar = 0;

		for (mCharIndexX = 0; mCharPixelX < mTextSelectionPixelX; mCharIndexX++) {

			if (tempStr != null)

				// this logic is for checking the presence of spaces
				if (mCharIndexX < tempStr.length()) {
					mChar = tempStr.charAt(mCharIndexX);

					// counting the space " " .
					if (mChar == ' ') {
						mCharSpaceCount = mCharSpaceCount + 1;
					}
				}

			mCharPixelX += mPen.measureText(tempStr, mCharIndexX,
					mCharIndexX + 1);
		}

		/* Jyotsana */
		if (Annotation_StartXPos == utl.EBOOK_TRUE) {
			mHighlightPos.StartXpos = mCharPixelX + 20;
			Annotation_StartXPos = utl.EBOOK_FALSE;
		}
		if (Annotation_EndXPos == utl.EBOOK_TRUE) {
			mHighlightPos.EndXpos = mCharPixelX + 20;
			Annotation_EndXPos = utl.EBOOK_FALSE;
		}
		// returning the index of char chosen for selection in a line
		return mCharIndexX - 1 + mCharSpaceCount;

	}

	// This method is for doing drag and drop operation...
	// when the text highlighting and extraction is done ..
	// a new text view is created (invisible) and its layout is defined
	// when the user again touches the highlighted text
	// the text view becomes visible and user can drag it to cabinet
	@SuppressWarnings("deprecation")
	protected void createTextToCopy() {
		// TODO Auto-generated method stub

		// creating new text view and setting new layout parameters
		mCopyEditText = new TextView(this);

		// if end index is less than the starting index
		// swapping the start and end index
		if (mTextSelectionEnd_Y < mTextSelectionStart_Y) {
			int mTempSelectionIndex = mTextSelectionStart_Y;
			mTextSelectionStart_Y = mTextSelectionEnd_Y;
			mTextSelectionEnd_Y = mTempSelectionIndex;
		}

		// checking of the y difference between the text selection start and end
		// if the difference is less than 20 then some default height of the
		// textview (to be dragged) is to be defined
		// else height depending on start and end will be set
		if (Math.abs(mTextSelectionEnd_Y - mTextSelectionStart_Y) > 20) {
			mCopyEditText.setLayoutParams(new AbsoluteLayout.LayoutParams(700,
					Math.abs(mTextSelectionEnd_Y - mTextSelectionStart_Y), 15,
					mTextSelectionStart_Y - UPPER_BAR_OFFSET));
		} else
			mCopyEditText.setLayoutParams(new AbsoluteLayout.LayoutParams(200,
					70, mTextSelectionStart_X - LEFT_BAR_OFFSET,
					mTextSelectionStart_Y - UPPER_BAR_OFFSET));

		// adding text view to Drag and drop container
		mDragDropMenuContainer.addView(mCopyEditText);

		// ontouch listener for dragging the text.....
		mCopyEditText.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				AbsoluteLayout.LayoutParams par = (android.widget.AbsoluteLayout.LayoutParams) v
						.getLayoutParams();

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN: {

					// setting the text and parameters of text view so that it
					// becomes visible
					mCopyEditText
							.setBackgroundResource(R.drawable.backgroundforselectedtext);

					mCopyEditText.setText(mTempStr);
					mCopyEditText.setTextColor(Color.CYAN);

					// flag for preventing the repetition of dragging
					mPreventFlag = 0;
					// setting layout boundaries and parameters for the text
					v.setLayoutParams(par);
					break;

				}// end of case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP: {

//					par.y = (int) event.getRawY() - (UPPER_BAR_OFFSET);
//					par.x = (int) event.getRawX() - (2 * LEFT_BAR_OFFSET);
					
					
					par.y = (int) event.getRawY() - (v.getHeight()/2) - UPPER_BAR_OFFSET;
					par.x = (int) event.getRawX() - (v.getWidth()/2) - LEFT_BAR_OFFSET;

					// setting layout boundaries and parameters for the text on
					// action down
					v.setLayoutParams(par);

					// dropping the text in the cabinet and removing the text
					// view from the drag drop container
					if (event.getRawY() > 1300 && mCabinetOpen == 0) {
						mDragDropMenuContainer.removeView(mCopyEditText);

						mTextToDrag = mTempStr;

						// mDragDropMenuContainer.removeView(mTextTrail);
						// mDragDropMenuContainer.removeView(mTextTrail2);

						// mPreventFlag = 1;
						openCabinet();
						// checking if the cabinet is opened
						mCabinetAlreadyOpen = 1;
					} else if (mCabinetOpen == 1 && event.getRawY() > 1000
							&& mPreventFlag == 0) {
						mDragDropMenuContainer.removeView(mCopyEditText);
						mTextToDrag = mTempStr;

						// refreshing the cabinet after data is dropped
						refreshCabinet();
						mPreventFlag = 1;
					}

					break;
				}// end of case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_MOVE: {

					if (mHoldShrink == false) {
						//par.y = (int) event.getRawY() - (UPPER_BAR_OFFSET);
						//par.x = (int) event.getRawX() - (2 * LEFT_BAR_OFFSET);
						
						par.y = (int) event.getRawY() - (v.getHeight()/2) - UPPER_BAR_OFFSET;
						par.x = (int) event.getRawX() - (v.getWidth()/2) - LEFT_BAR_OFFSET;

						
						mHoldShrink = true;
					}

					// These holds the values of x and y for 1 move cycle

//					int xTemp = (int) event.getRawX() - (2 * LEFT_BAR_OFFSET);
//					int yTemp = (int) event.getRawY() - (UPPER_BAR_OFFSET);
					
					int xTemp = (int) event.getRawX() - (v.getWidth()/2) - UPPER_BAR_OFFSET;
					int yTemp = (int) event.getRawY() - (v.getHeight()/2) - LEFT_BAR_OFFSET;


					// setting layout boundaries and parameters for the text on
					// action down
					// reducing length and height on each move cycle
					if (xTemp != par.x && yTemp != par.y) {
						if (par.width > 100 && par.height > 100) {
							par.width = par.width - (par.width * 3 / 100);
							par.height = par.height - (par.height * 3 / 100);

							par.x = par.x - (par.width * 3 / 100);
							par.y = par.y - (par.height * 3 / 100);

//							float mTextSize = mCopyEditText.getTextSize()
//									- mCopyEditText.getTextSize() * 2 / 100;
//							float mTextSize=10;
//							mCopyEditText.setTextSize(mTextSize);

						}
						v.setLayoutParams(par);

//						par.y = (int) event.getRawY() - (UPPER_BAR_OFFSET);
//						par.x = (int) event.getRawX() - (2 * LEFT_BAR_OFFSET);
						
						par.y = (int) event.getRawY() - (v.getHeight() /2) -UPPER_BAR_OFFSET;
						par.x = (int) event.getRawX() - (v.getWidth()/2) - LEFT_BAR_OFFSET;


						break;

					}// end of case MotionEvent.ACTION_MOVE:
				}// end of switch (event.getAction())
				}
				// TODO Auto-generated method stub
				return true;
			}// end of method onTouch(View v, MotionEvent event)
		});// end of ontouch listener

	}

	// ___________________________________________________________________

	// This method is for doing drag and drop operation...
	// when the image selection is done (on touch of image)
	// a new imageview is created (invisible) and its layout is defined
	// when the user again touches the imageview
	// the imageview becomes visible and user can drag it to cabinet
	private void createImageToCopy() {
		// TODO Auto-generated method stub

		// removing all views from the absolute layout before doing drag and
		// drop operation
		mDragDropMenuContainer.removeAllViews();

		mCopyImage = new ImageView(this);

		LayoutParams par = mSelectionImage.getLayoutParams();

		Drawable dr = mSelectionImage.getDrawable();

		mCopyImage.setLayoutParams(new AbsoluteLayout.LayoutParams(dr
				.getBounds().right, dr.getBounds().bottom,
				mDragDropMenuContainer.getWidth() / 2 - dr.getBounds().right
						/ 2, mSelectionImage.getTop()));

		mSelectionImage.setAlpha(150);

		

		mDragDropMenuContainer.addView(mCopyImage);

		mCopyImage.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				AbsoluteLayout.LayoutParams par = (android.widget.AbsoluteLayout.LayoutParams) v
						.getLayoutParams();

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {

					mCopyImage.setBackgroundDrawable(mSelectionImage
							.getDrawable());
					// mCopyImage.setAlpha(255);

					// flag for preventing the repetition of dragging
					mPreventFlag = 0;
					// setting layout boundaries and parameters for the text
					v.setLayoutParams(par);
					break;
				}// end of case MotionEvent.ACTION_DOWN
				case MotionEvent.ACTION_MOVE: {

					if (mHoldShrink == false) {
						par.y = (int) event.getRawY() - v.getHeight() / 2
								- (UPPER_BAR_OFFSET);
						par.x = (int) event.getRawX() - v.getWidth() / 2
								- (LEFT_BAR_OFFSET);
						mHoldShrink = true;
					}

					// These holds the values of x and y for 1 move cycle

					int xTemp = (int) event.getRawX() - v.getWidth() / 2
							- (LEFT_BAR_OFFSET);
					int yTemp = (int) event.getRawY() - v.getHeight() / 2
							- (UPPER_BAR_OFFSET);

					// setting layout boundaries and parameters for the text on
					// action down
					// reducing length and height on each move cycle
					if (xTemp != par.x && yTemp != par.y) {
						if (par.width > 100 && par.height > 100) {
							par.width = par.width - (par.width * 3 / 100);
							par.height = par.height - (par.height * 3 / 100);

							par.x = par.x - (par.width * 3 / 100);
							par.y = par.y - (par.height * 3 / 100);

						}

						v.setLayoutParams(par);

						par.y = (int) event.getRawY() - v.getHeight() / 2
								- (UPPER_BAR_OFFSET);
						par.x = (int) event.getRawX() - v.getWidth() / 2
								- (LEFT_BAR_OFFSET);

					}
					break;

				}
				case MotionEvent.ACTION_UP: {
					par.y = (int) event.getRawY() - v.getHeight() / 2
							- (UPPER_BAR_OFFSET);
					par.x = (int) event.getRawX() - v.getWidth() / 2
							- (LEFT_BAR_OFFSET);

					v.setLayoutParams(par);

					// dropping the text in the cabinet and removing the text
					// view from the drag drop container
					if (event.getRawY() > 740 && mCabinetOpen == 0) {
						mDragDropMenuContainer.removeView(mCopyImage);

						// mPreventFlag = 1;
						openCabinet();
						// checking if the cabinet is opened
						mCabinetAlreadyOpen = 1;
						mSelectionImage.setAlpha(255);
					} else if (mCabinetOpen == 1 && event.getRawY() > 500
							&& mPreventFlag == 0) {
						mDragDropMenuContainer.removeView(mCopyImage);

						// refreshing the cabinet after data is dropped
						refreshCabinet();
						mPreventFlag = 1;
						mSelectionImage.setAlpha(255);
					}

					break;

				}

				}// end of switch (event.getAction())

				return true;
			}// end of method onTouch()
		});// end of onTouchListener

	}

	public void openCabinet() {

		/* open the cabinet from down */
		if (mCabinetOpen != CABINETISOPEN) {

			Intent mMyCabinetActivity = new Intent(Display_Manager.this,
					MyCabinet.class);
			Bundle mNewExtractedTextBundle = new Bundle();
			if (Organizer.mSelectedNoteId > 0 && MyCabinet.mIncabinet != true) {
				mNewExtractedTextBundle.putInt("selectedNoteId_key",
						Organizer.mSelectedNoteId);
			} else {
				// first check that atleast something is selected
				if (mTextToDrag != null) {
					mNewExtractedTextBundle.putString("ExtractedText",
							mTextToDrag);
				}
				if (mImageSelected == true) {
					mNewExtractedTextBundle.putString("CopiedImagePath",
							ImagePath);
				}
				ImagePath=null;
			}
			mMyCabinetActivity.putExtras(mNewExtractedTextBundle);
			mLocalActivityManager = Display_Manager.this
					.getLocalActivityManager();
			mLocalActivityManager.removeAllActivities();
			mMyCabinetview = mLocalActivityManager.startActivity("MyCabinetID",
					mMyCabinetActivity).getDecorView();
			WindowManager w = getWindowManager();
			Display d = w.getDefaultDisplay();
			int width = d.getWidth();
			int height = d.getHeight();
			mMyCabinetview.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, height));

			mRelLayout = (RelativeLayout) findViewById(R.id.BookViewPage);
			mRelLayout.addView(mMyCabinetview);

			if (mCabinetOpen == CABINETISOPEN) {
				Animation mCabinetOpenAnim = AnimationUtils.loadAnimation(
						Display_Manager.this, R.anim.push_up);
				mMyCabinetview.startAnimation(mCabinetOpenAnim);
			}

			mCabinetOpen = 1;
			mTempStr = null;
			mTextToDrag = null;
			// removing added text view to drag
			mDragDropMenuContainer.removeView(mCopyEditText);
		}
		/* cabinet open code finish */

	}

	// this function will refresh the view after insert any data in cabinet
	public void refreshCabinet() {
		if (mCabinetOpen == CABINETISOPEN) {
			mCabinetOpen = CABINETISCLOSED;
			mRelLayout.removeView(mMyCabinetview);
			if (MyCabinet.mIncabinet == true) {// Organizer.mSelectedNoteId ==
				// 0) {
				openCabinet();
			} else {
				DataBaseClass mDb = new DataBaseClass(Display_Manager.this);
				mDb.mDatab = mDb.mDatah.getReadableDatabase();

				String mCabinetText = MyCabinet.mSetTextForCabinet.getText()
						.toString();
				Cursor mNoteDataCursor = mDb.getNoteID(mCabinetText);
				mNoteDataCursor.moveToFirst();
				int mForCabinetChangeNoteId = mNoteDataCursor.getInt(0);
				if(mTempStr!=null){
				mDb
						.insertNoteDataInNoteTable(mForCabinetChangeNoteId,
								mTempStr,"Text");
				//make the temp string as null otherwise 
				//	the data extracted to note will also come in cabinet also
				mTempStr=null;
				mTextToDrag = null;
				}
				if(ImagePath!=null){
					mDb
					.insertNoteDataInNoteTable(mForCabinetChangeNoteId,
							ImagePath,"Image");
				}
				openCabinet();
				mCabinetOpen = CABINETISOPEN;
				mPreventFlag = CABINETISCLOSED;
			}
		}
	}

	/* this function is for controlling the Zoom */
	public void startZooming(float mCurrentZoomLevel) {

		/*
		 * myDbBook = Display_Manager.this.openOrCreateDatabase(
		 * DispMgrUtility.DATABASE_NAME_BOOK, 1, null);
		 */
		if (TOCClicked == 1) {

			myDbBook.execSQL("DELETE FROM  " + utl.CURRENT_INDEX_TABLE + ";");
			chnum = mBookInfo.miDmbCurrentChapter - 1;
			mBookInfoForFasterTOC.miDmbCurrentChapter = chnum;
			mBookInfoForFasterTOC.miDmbLineBufferStatus = utl.EBOOK_FALSE;
			mBookInfoForFasterTOC.miDmbLineNo = utl.DISPMGR_FIRSTLINE;
			// set book status
			mBookInfoForFasterTOC.miDmbBookStatus = utl.EBOOK_TRUE;
			// mBookInfo.miDmbBookStatus = utl.EBOOK_TRUE;

			mPageNumberIsNotcurroct = 1;
			mFastTOC = new IndexGeneration();
			mFastTOC.execute();
		}

		while (IndexGeneratinfortocorzoom != 0) {
			continue;
		}
		mBookInfo.mDisplayManagerGetNthPageInfor(myDbBook,
				mBookInfo.miDmbPageNo, mBookInfo.miDmbCurrentChapter);

		mBookInfo.mfDmbZoomLevel = mCurrentZoomLevel;
		mBookInfoForFasterTOC.mfDmbZoomLevel = mCurrentZoomLevel;
		mBookInfoForPageUp.mfDmbZoomLevel = mCurrentZoomLevel;

		bookview_display_page();
		lineNumberForPageUpTOC = mBookInfo.miDmbLineNo;
		XmlLevelForPageUpTOC = mBookInfo.epubparser.getContentPath();

		mZoomClicked = 1;
		myDbBook.execSQL("DELETE From " + utl.CURRENT_INDEX_TABLE + ";");
		myDbBook.execSQL("DELETE From " + utl.PREVIOUS_INDEX_TABLE + ";");
		// RestartIndexGeneration();
		// reset the flag
		mBookInfo.mObjDifFlag = 0;
		// release semaphore
		mSemaphore.release();

		if (mIndexGenerationStatus == COMLETED) {
			mDbopp = new DataBaseOperations();
			mDbopp.execute();
		}

	}

	// this function will close the cabinet
	public static void CloseCabinet() {
		mCabinetOpen = 0;
		Organizer.smForCabinetChangeID = 0;
		mRelLayout.removeView(mMyCabinetview);
	}

	public static void removeCabinetafterSave() {
		mCabinetOpen = 0;
		mRelLayout.removeView(mMyCabinetview);

	}

	/*
	 * It will Draw the Annotation Icon on the Displayed Page once user done any
	 * Annotation
	 */
	public void DrawAnnotation() {
		/*
		 * To get the Annot ID
		 */
		DataBaseClass mDb = new DataBaseClass(Display_Manager.this);
		mDb.mDatab = mDb.mDatah.getReadableDatabase();
		String TableName = Anno_BookName + "_AnnoTable";

		Cursor mAnnotCursor1 = mDb.selectAnnot_IDFromAnnotable(TableName);
		mAnnotCursor1.moveToLast();

		int annot_Image_ID = mAnnotCursor1.getInt(0);

		Image_sub = new ImageView(this);
		Image_sub.setImageResource(R.drawable.footerannotatedicon);
		Image_sub.setId(annot_Image_ID);

		int xpos = mHighlightPos.StartXpos;
		int ypos = mHighlightPos.StartYpos - mXMLlevelParam[0].StartYpos;

		Image_sub.setLayoutParams(new AbsoluteLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, xpos,
				ypos));

		mAnnotAbsLay.addView(Image_sub);
		Image_sub.setOnClickListener(AnnotClickd);
	}

	/*
	 * Drawing Annotation Icon on the page while displaying the page itself
	 */
	public void IconAddonAbsl(int Element_Numb) {

		int x = 0;
		int y = 0;

		for (int i = 0; i < Element_Numb; i++) {

			if (IconPos[i][3] == utl.EBOOK_TRUE) {

				int annot_Image_ID = IconPos[i][2];
				Image_sub = new ImageView(this);
				Image_sub.setImageResource(R.drawable.footerannotatedicon);
				Image_sub.setId(annot_Image_ID);

				// x= IconPos[i][0] - 13 ;
				x = IconPos[i][0];
				y = IconPos[i][1] - mXMLlevelParam[0].StartYpos;

				Image_sub.setLayoutParams(new AbsoluteLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						x, y));
				IconPos[i][3] = utl.EBOOK_FALSE;
				mAnnotAbsLay.addView(Image_sub);
				Image_sub.setOnClickListener(AnnotClickd);

			}
		}

	}

	/*
	 * Annotation info Parametre stucture to be stored in the database
	 */
	public class ANNOTATION_INFO_PARAM {
		int Chapter_id;
		int Offset;
		int Wordlength;
		int Continue_flag;
		String xmllvl;
		int FontSize;
		int LineNumb;

	}

	/* Stores information of the highlighted string */
	public class HL_STRING_POS {

		int StartXpos;
		int StartYpos;
		int EndXpos;
		int EndYpos;

	}

	/* Stores information of the highlighted Xml level */
	public class HL_STRING_XMLLVL_INFO {
		int HL_Xmllevelcount; /* Total no of Xml levels highlighted */
		int StartXmllevelIndex; /* Which xml level the highlighting starts */
		int aui32LineCountForEachLevel[]; /*
										 * No of lines highlighted in each xml
										 * level
										 */
		int HL_StartLineNo; /* Line No at which highlighting starts */

	}

	/* This will store the Line Infomation to draw Annot Icon */
	public class LineInfo {
		String linestart; /* the start of the line */
		int LineNo; /* Current line no which is drawn */
		float ZoomLevel;
		int ParaOffset; /* Para Offset */
		int LeftMargin;
		int LineYpos; /* Y pos of the line */
		int Strlen_count; /*
						 * Counter for the string length of the current line and
						 * previous lines of that xml level
						 */
		int AnnotationCnt; /* No of annotations in that xml level */
		int LineSpace;
		int PrevlineStrlen_count; /*
								 * Str length of the prev lines before the
								 * annotated line
								 */
		int FontSize;

	}

	/* This will store the annot info from Database */
	public class Annotinfo {
		int Anno_ID;
		String Anno_Notes;
		int Anno_Offset;
		String Anno_Length;

		public void setData(int x, String y, int z, String p) {
			Anno_ID = x;
			Anno_Notes = y;
			Anno_Offset = z;
			Anno_Length = p;

		}
	}

	/* returns the Annotation count of the given xmllevel */
	int Get_Annotation_Count(String XMLlevel, int chapID) {
		int mNumbAnnot = 0;
		String mXMLlevel = XMLlevel;
		int mChaptID = chapID;

		/* To get the Number of Annotation for perticular XML level */
		DataBaseClass mDb = new DataBaseClass(Display_Manager.this);
		mDb.mDatab = mDb.mDatah.getReadableDatabase();

		String TableName = Anno_BookName + "_AnnoTable";
		Cursor mAnnotCursor = mDb.AnnotCount_XMLlevel_FromAnnotable(mXMLlevel,
				mChaptID, TableName);
		mAnnotCursor.moveToFirst();
		mNumbAnnot = mAnnotCursor.getInt(0);

		return mNumbAnnot;
	}

	/*
	 * for filling Annotinfo class members
	 */
	Annotinfo Fill_Annotation_info(String XMLlevel, int pos, int ChaptID) {
		Annotinfo mAnnotInfo = null;
		mAnnotInfo = new Annotinfo();

		DataBaseClass mDb = new DataBaseClass(Display_Manager.this);
		mDb.mDatab = mDb.mDatah.getReadableDatabase();

		String TableName = Anno_BookName + "_AnnoTable";
		Cursor mAnnotCursor = mDb.getAnnotInfoByXMLlevel(XMLlevel, ChaptID,
				TableName);

		mAnnotCursor.moveToPosition(pos);

		int Anno_ID = mAnnotCursor.getInt(1);
		String Anno_Notes = mAnnotCursor.getString(2);
		int Anno_Offset = mAnnotCursor.getInt(7);
		String Anno_Length = mAnnotCursor.getString(8);

		mAnnotInfo.setData(Anno_ID, Anno_Notes, Anno_Offset, Anno_Length);

		return mAnnotInfo;

	}

	// It will calculate Annot icon's X,Y co-ordinate
	public int Annotation_XY_cal(Annotinfo mAnnotInfo[], LineInfo mLineInfo,
			int[][] Pos, int mIndex) {

		int ActiveOffset = 0; // offset of the xmllevel in which annotated
		// word lies
		int Strlen_count = 0; // Counter for the string length of the current
		// line and previous lines of tha t xml level
		int PrevlineStrlen_count = 0; // Counter for the string length of the
		// previous lines of that xml level

		int ProcessedAnnotatedLen = 0;
		int strwidth = 0;
		float IconXpos = 0;
		float IconYpos = 0;
		String orgstartptr;
		// int mIndex = 0;

		orgstartptr = mLineInfo.linestart;
		if (mIndex < mLineInfo.AnnotationCnt) {
			while (true) {
				if (mIndex >= mLineInfo.AnnotationCnt) {
					break;
				}
				ActiveOffset = mAnnotInfo[mIndex].Anno_Offset;

				// To check if annotation
				// lies within
				// the current line

				if (mLineInfo.Strlen_count > ActiveOffset) {
					// Reach the annoated word
					while (mLineInfo.PrevlineStrlen_count < ActiveOffset) {
						// mLineInfo.linestart++;
						mLineInfo.linestart = mLineInfo.linestart.substring(1);
						mLineInfo.PrevlineStrlen_count++;
						ProcessedAnnotatedLen++;
					}

					strwidth = (int) pen.measureText(orgstartptr, 0,
							ProcessedAnnotatedLen);

					Pos[mIndex][0] = strwidth + mLineInfo.LeftMargin;
					Pos[mIndex][1] = mLineInfo.LineYpos;
					Pos[mIndex][2] = mAnnotInfo[mIndex].Anno_ID;
					Pos[mIndex][3] = utl.EBOOK_TRUE;
					mIndex++;
				} else {
					// Pos[mIndex][3] = utl.EBOOK_FALSE;
					break;
				}

			}

		}
		return mIndex;
	}

	int Fill_Offset_info(String XMLlevel, int pos, int ChaptID) {

		DataBaseClass mDb = new DataBaseClass(Display_Manager.this);
		mDb.mDatab = mDb.mDatah.getReadableDatabase();

		String TableName = Anno_BookName + "_AnnoTable";
		Cursor mOffsetCursor = mDb.getOffsetInfo(XMLlevel, ChaptID, TableName);

		mOffsetCursor.moveToPosition(pos);

		int Anno_Offset = mOffsetCursor.getInt(0);

		return Anno_Offset;

	}

	/*
	 * It will map the highlighted text and calculates all information of
	 * annotated text while doing Annotation
	 */
	int Mapping_HighlightedString(HL_STRING_POS mHighlightPos,
			String rawbufferptr,
			DispMgrBookInfo.XmlLevel_param mXMLlevelParam[],
			ANNOTATION_INFO_PARAM mAnnotInfo[]) {

		int EndYPos;
		int Start_YPos = 0;
		int End_YPos = 0;
		int count = 0;
		String mLocalRawBuffer;
		int XmlLevelNo = 0;
		int NoofLinesSelected = 1;
		int Start_YposforLineNo = 0;
		int Linecount = 0;
		int StartLineNo = 0;
		int i = 0;
		int j = 0;
		int ctr = 0;
		int strlencnt = 0;
		int XmlLevelsSelectedCtr = 1;
		int wordlengthpixel = 0;
		int wordLenCtr = 0;
		int tempXmlLevelctr = 0;
		String XmlLevelStartptr;
		int MultipleXmlLevelFlag = utl.EBOOK_FALSE;
		int HL_LineCountForEachLevel[] = null;
		int ProcessedXmlLevelctr = 0;
		int FirstTimeFlag = utl.EBOOK_FALSE;
		int SelectedLinesctr = 0;
		int StartXmlLevelIndex = 0;
		int currlinestrlen = 0;
		int firstlinelength = 0;
		String HighlightedLineStrtptr;
		int HL_StartLineNo = 0;
		String mHLStart = null;
		int wordlengthbyte = 0;
		int Totalwordlength = 0;
		int offsetctr = 0;
		int offsetcnt = 0;

		Paint pen1 = new Paint();
		XmlLevelStartptr = new String();

		/* End y pos of the first xml level */
		EndYPos = mXMLlevelParam[count].EndYpos;
		mLocalRawBuffer = rawbufferptr;

		/* Finds the xml level number where the highlighting starts */
		while ((mHighlightPos.StartYpos > EndYPos)
				&& ((mHighlightPos.StartYpos - 1) > EndYPos)) {
			count++;
			XmlLevelNo++;
			EndYPos = mXMLlevelParam[count].EndYpos;
		}
		tempXmlLevelctr = XmlLevelNo;

		/* To check if highlighting goes in multiple xml levels */
		if (mHighlightPos.EndYpos > EndYPos) {
			MultipleXmlLevelFlag = utl.EBOOK_TRUE;
		}
		/* to get the font size for that XML level */
		/* set the font size */
		pen1.setTextSize(mXMLlevelParam[XmlLevelNo].FontSize);
		/* set type fase */
		pen1.setTypeface(Typeface.SERIF);

		/* Finds the no. of xml levels highlighted */
		while (mHighlightPos.EndYpos > EndYPos) {
			XmlLevelsSelectedCtr++;
			count++;
			EndYPos = mXMLlevelParam[count].EndYpos;
			if (EndYPos == 0) /* Added for old buffer */
			{
				break;
			}

		}

		HL_LineCountForEachLevel = new int[XmlLevelsSelectedCtr];
		if (MultipleXmlLevelFlag == utl.EBOOK_TRUE) {
			Start_YPos = mHighlightPos.StartYpos;

			while (j < XmlLevelsSelectedCtr) {
				Start_YPos = mXMLlevelParam[tempXmlLevelctr].StartYpos;

				if ((mXMLlevelParam[tempXmlLevelctr].Total_lines) != (mXMLlevelParam[tempXmlLevelctr].XmlLvlProLineCnt)) {
					End_YPos = mHighlightPos.EndYpos;

				} else {
					End_YPos = mXMLlevelParam[tempXmlLevelctr].EndYpos;
				}
				while (Start_YPos < End_YPos && (Start_YPos < (End_YPos - 1))) {
					Start_YPos += mXMLlevelParam[tempXmlLevelctr].FontSize;
					NoofLinesSelected++;
				}

				HL_LineCountForEachLevel[j] = NoofLinesSelected;

				NoofLinesSelected = 1;

				tempXmlLevelctr++;

				j++;

			}

		}

		/* For one xml level */
		else {
			Start_YPos = mHighlightPos.StartYpos;
			End_YPos = mHighlightPos.EndYpos;

			while (Start_YPos < End_YPos && (Start_YPos < (End_YPos - 1))) {
				Start_YPos += (mXMLlevelParam[XmlLevelNo].FontSize);
				NoofLinesSelected++;
			}
			HL_LineCountForEachLevel[0] = NoofLinesSelected;

		}
		/* Calculate the line number at which highlighting starts */
		Start_YposforLineNo = mXMLlevelParam[XmlLevelNo].StartYpos;

		while ((Start_YposforLineNo < mHighlightPos.StartYpos)
				&& (Start_YposforLineNo < (mHighlightPos.StartYpos - 1))) {
			Start_YposforLineNo += mXMLlevelParam[XmlLevelNo].FontSize;

			Linecount++;
		}

		StartLineNo = Linecount;

		if (mXMLlevelParam[XmlLevelNo].LineBufferStatusFlag == utl.EBOOK_TRUE) {
			StartLineNo = StartLineNo
					+ (mXMLlevelParam[XmlLevelNo].PrevLineProcessed);
		}

		XmlLevelStartptr = rawbufferptr;
		/* Moves the pointer to starting of the highlighted Xml Level */
		while (i < XmlLevelNo) {
			rawbufferptr = rawbufferptr
					.substring(mXMLlevelParam[i].totalstrlen);
			i++;
			XmlLevelStartptr = rawbufferptr;
		}

		/* Moves the pointer to starting of the highlighted line */
		while (ctr < StartLineNo) {
			rawbufferptr = rawbufferptr
					.substring(mXMLlevelParam[XmlLevelNo].Strlen_eachline[ctr]);
			ctr++;
		}
		/* Local ptr to start of the highlighted line */
		HighlightedLineStrtptr = rawbufferptr;

		/*
		 * For changing the loaction of ui32strlencnt to the beginning of the
		 * highlighted line
		 */
		if (StartLineNo == 0) {
			strlencnt = mXMLlevelParam[XmlLevelNo].ParaOffset;

		} else {
			strlencnt = mXMLlevelParam[XmlLevelNo].LeftMargin;
		}

		

		while (strlencnt < mHighlightPos.StartXpos) {

			int str_width = (int) pen1.measureText(rawbufferptr, 0, 1);
			strlencnt += str_width;
			rawbufferptr = rawbufferptr.substring(1);

		}

		/* memory allocation for the first object */
		mAnnotInfo[0] = new ANNOTATION_INFO_PARAM();

		/*
		 * this offset indicates the offset from the beginning of xml level in
		 * which HL word starts
		 */
		mAnnotInfo[0].Offset = XmlLevelStartptr.length()
				- rawbufferptr.length();
		/* Jyotsana: Added to eliminate annotation at the same offset */
		DataBaseClass mDb = new DataBaseClass(Display_Manager.this);
		mDb.mDatab = mDb.mDatah.getReadableDatabase();

		/* Added to avoid annotation at the same point */
		Cursor offset_curs_id = mDb.OffsetCount(
				mXMLlevelParam[XmlLevelNo].aui8xmllvl,
				mBookInfo.miDmbCurrentChapter, Anno_BookName);
		offset_curs_id.moveToFirst();
		offsetcnt = offset_curs_id.getInt(0);

		if (offsetcnt != 0) {
			mOffsetInfo = new int[offsetcnt];

			for (int k = 0; k < offsetcnt; k++) {

				mOffsetInfo[k] = Fill_Offset_info(
						mXMLlevelParam[XmlLevelNo].aui8xmllvl, k,
						mBookInfo.miDmbCurrentChapter);

			}
		}

		for (offsetctr = 0; offsetctr < offsetcnt; offsetctr++) {
			if (mAnnotInfo[0].Offset == mOffsetInfo[offsetctr])
				return 0;
		}

		mAnnotInfo[0].Continue_flag = 1;
		mAnnotInfo[0].FontSize = mXMLlevelParam[XmlLevelNo].FontSize;

		mHLStart = rawbufferptr;
		/* If the highlighted word lies in the single line */
		if ((XmlLevelsSelectedCtr == 1) && (NoofLinesSelected == 1)) {

			while (wordLenCtr < (mHighlightPos.EndXpos - mHighlightPos.StartXpos)) {
				wordLenCtr += (int) pen1.measureText(rawbufferptr, 0, 1);
				rawbufferptr = rawbufferptr.substring(1);

			}
			mAnnotInfo[0].Wordlength = mHLStart.length()
					- rawbufferptr.length();
			mAnnotInfo[0].xmllvl = mXMLlevelParam[XmlLevelNo].aui8xmllvl;
			/* to get the linenumber for one XML level and one line */
			mAnnotInfo[0].LineNumb = NoofLinesSelected;

		}
		/* For multiple xml levels and multiple lines for one xml level */
		else {
			StartXmlLevelIndex = XmlLevelNo;
			HL_StartLineNo = StartLineNo;

			/* for one Xml level and multiple lines */
			if (XmlLevelsSelectedCtr == 1) {
				mAnnotInfo[0].LineNumb = NoofLinesSelected;
			}

			while (ProcessedXmlLevelctr < XmlLevelsSelectedCtr) {
				FirstTimeFlag = utl.EBOOK_FALSE;
				pen1.setTextSize(mXMLlevelParam[StartXmlLevelIndex].FontSize);

				while (SelectedLinesctr < HL_LineCountForEachLevel[ProcessedXmlLevelctr]) {
					if (ProcessedXmlLevelctr == 0 && SelectedLinesctr == 0) {
						currlinestrlen = (int) pen1
								.measureText(
										HighlightedLineStrtptr,
										0,
										mXMLlevelParam[StartXmlLevelIndex].Strlen_eachline[HL_StartLineNo]);

						if (HL_StartLineNo == 0) {
							firstlinelength = (currlinestrlen + mXMLlevelParam[StartXmlLevelIndex].ParaOffset)
									- mHighlightPos.StartXpos;
						} else {
							firstlinelength = (currlinestrlen + mXMLlevelParam[StartXmlLevelIndex].LeftMargin)
									- mHighlightPos.StartXpos;

						}

						if (currlinestrlen < firstlinelength) {
							wordlengthpixel = currlinestrlen;
						} else {
							wordlengthpixel = firstlinelength;
						}
						FirstTimeFlag = utl.EBOOK_TRUE;
						while (wordLenCtr < wordlengthpixel) {
							if (("").equals(rawbufferptr)) {
								break;
							}
							wordLenCtr += (int) pen1.measureText(rawbufferptr,
									0, 1);

							rawbufferptr = rawbufferptr.substring(1);

						}

					}// End of if
					else {
						if ((SelectedLinesctr == (HL_LineCountForEachLevel[ProcessedXmlLevelctr] - 1))
								&& (ProcessedXmlLevelctr == XmlLevelsSelectedCtr - 1)) {

							wordlengthpixel = (mHighlightPos.EndXpos)
									- (mXMLlevelParam[StartXmlLevelIndex].LeftMargin);
							currlinestrlen = (int) pen1
									.measureText(
											HighlightedLineStrtptr,
											0,
											mXMLlevelParam[StartXmlLevelIndex].Strlen_eachline[HL_StartLineNo]);

							if (currlinestrlen < wordlengthpixel) {
								wordlengthpixel = currlinestrlen;

							}

						} else {
							wordlengthpixel = (int) pen1
									.measureText(
											rawbufferptr,
											0,
											mXMLlevelParam[StartXmlLevelIndex].Strlen_eachline[HL_StartLineNo]);

						}
						while (wordLenCtr < wordlengthpixel) {
							if (("").equals(rawbufferptr)) {
								break;
							}
							wordLenCtr += (int) pen1.measureText(rawbufferptr,
									0, 1);

							rawbufferptr = rawbufferptr.substring(1);

						}

					} /* End of else(i32FirstTimeFlag) */

					// HighlightedLineStrtptr =
					// HighlightedLineStrtptr.substring(mXMLlevelParam[ProcessedXmlLevelctr].Strlen_eachline[HL_StartLineNo]);
					HighlightedLineStrtptr = HighlightedLineStrtptr
							.substring(mXMLlevelParam[StartXmlLevelIndex].Strlen_eachline[HL_StartLineNo]);
					wordlengthbyte = mHLStart.length() - rawbufferptr.length();
					mHLStart = rawbufferptr;
					wordLenCtr = 0;

					HL_StartLineNo++;
					SelectedLinesctr++;

					/* Calcuate the total word length */
					Totalwordlength += wordlengthbyte;

				} /* End of 2nd while */
				mAnnotInfo[ProcessedXmlLevelctr].Wordlength = Totalwordlength;
				mAnnotInfo[ProcessedXmlLevelctr].xmllvl = mXMLlevelParam[StartXmlLevelIndex].aui8xmllvl;
				/* for Mulitple XML levels */
				mAnnotInfo[ProcessedXmlLevelctr].LineNumb = HL_LineCountForEachLevel[ProcessedXmlLevelctr];

				Totalwordlength = 0;
				SelectedLinesctr = 0;
				HL_StartLineNo = 0;
				wordLenCtr = 0;
				ProcessedXmlLevelctr++;
				StartXmlLevelIndex++;

				if (ProcessedXmlLevelctr < XmlLevelsSelectedCtr) {
					/* memory allocation */
					mAnnotInfo[ProcessedXmlLevelctr] = new ANNOTATION_INFO_PARAM();
					mAnnotInfo[ProcessedXmlLevelctr].Offset = 0;
					mAnnotInfo[ProcessedXmlLevelctr].Continue_flag = 0;

				}

			} /* End of 1st while */

		}
		return XmlLevelsSelectedCtr;

	}

	public int HL_CorrectionYpos(
			DispMgrBookInfo.XmlLevel_param mXmlLevelParam[], int HL_Ypos) {
		int XmlLevelcnt = 0;
		int NoofLinescnt = 0;
		int CorrectYpos = 0;

		int strYpos = 0;
		int endYpos = 0;

		if (HL_Ypos <= mXmlLevelParam[0].StartYpos) {
			CorrectYpos = mXmlLevelParam[0].StartYpos;
		} else {
			if (HL_Ypos >= mXmlLevelParam[mBookInfo.mXmlLevelCount - 1].EndYpos) {
				CorrectYpos = mXmlLevelParam[mBookInfo.mXmlLevelCount - 1].EndYpos;
			} else {

				for (XmlLevelcnt = 0; XmlLevelcnt < mBookInfo.mXmlLevelCount; XmlLevelcnt++) {
					if (HL_Ypos >= (mXmlLevelParam[XmlLevelcnt].StartYpos)
							&& HL_Ypos <= (mXmlLevelParam[XmlLevelcnt].EndYpos + mXmlLevelParam[XmlLevelcnt].FontSize)) {
						strYpos = mXmlLevelParam[XmlLevelcnt].StartYpos;
						endYpos = mXmlLevelParam[XmlLevelcnt].StartYpos
								+ mXmlLevelParam[XmlLevelcnt].FontSize;

						for (NoofLinescnt = 0; NoofLinescnt < mXmlLevelParam[XmlLevelcnt].Total_lines; NoofLinescnt++) {

							if ((HL_Ypos >= strYpos) && (HL_Ypos <= endYpos)) {
								// CorrectYpos =
								// mXmlLevelParam[XmlLevelcnt].StartYpos;
								CorrectYpos = strYpos;
								XmlLevelcnt = mBookInfo.mXmlLevelCount;
								break;
							}
							strYpos = endYpos;
							endYpos = endYpos
									+ mXmlLevelParam[XmlLevelcnt].FontSize;

						}
					}

				}
			}
		}
		return CorrectYpos;
	}

	public void Annotation_SortOffset(Annotinfo mAnnotInfo[],
			int mTotalNumbAnnot) {
		int i = 0;
		int j = 0;

		int Anno_ID;
		String Anno_Notes;
		int Anno_Offset;
		String Anno_Length;

		for (i = 0; i < (mTotalNumbAnnot - 1); i++) {
			for (j = i + 1; j < mTotalNumbAnnot; j++) {
				if (mAnnotInfo[i].Anno_Offset > mAnnotInfo[j].Anno_Offset) {
					Anno_ID = mAnnotInfo[i].Anno_ID;
					Anno_Notes = mAnnotInfo[i].Anno_Notes;
					Anno_Offset = mAnnotInfo[i].Anno_Offset;
					Anno_Length = mAnnotInfo[i].Anno_Length;

					mAnnotInfo[i].Anno_ID = mAnnotInfo[j].Anno_ID;
					mAnnotInfo[i].Anno_Notes = mAnnotInfo[j].Anno_Notes;
					mAnnotInfo[i].Anno_Offset = mAnnotInfo[j].Anno_Offset;
					mAnnotInfo[i].Anno_Length = mAnnotInfo[j].Anno_Length;

					mAnnotInfo[j].Anno_ID = Anno_ID;
					mAnnotInfo[j].Anno_Notes = Anno_Notes;
					mAnnotInfo[j].Anno_Offset = Anno_Offset;
					mAnnotInfo[j].Anno_Length = Anno_Length;

				}
			}
		}

	}

	/*
	 * For highlightion of annotated text
	 */
	public void highlightAfterAnnot(int mStartXPos, int mStartYPos,
			int mLineCount, int mCharCount) {

		//Selecting which edittext to highlight 
		for (int i = 0; i <= mTempedittextindex; i++) {

			if ((mStartYPos ) < mTempText[i].getBottom()
					&& (mStartYPos ) > mTempText[i].getTop()) {
				mSelectionEditText = mTempText[i];
			}
		}
		
	
		colourString = mSelectionEditText.getText();

		// calculating the text which is to be highlighted
		int mStartCharIndex = calculateCharacterIndexY((mStartXPos),
				(mStartYPos));

		int len = mSelectionEditText.getText().length();
		// int len = mSelectionEditText.getText().length();

		// colour spanning the edittexts
		if (len > (mStartCharIndex + mCharCount + mLineCount)) {
			colourString.setSpan(new BackgroundColorSpan(Color.RED),
					mStartCharIndex,
					(mStartCharIndex + mCharCount + mLineCount),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			colourString.setSpan(new BackgroundColorSpan(Color.RED),
					mStartCharIndex, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

	}

	/******** For testing perpose only : newly added for selection ********/

	OnTouchListener mTextSelectListener_new = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case LAYOUT_ID: {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					mTextSelectionStart_X = (int) event.getRawX();
					mTextSelectionStart_Y = (int) event.getRawY();
				
					

					break;
				}

				case MotionEvent.ACTION_UP: {
					mTextSelectionEnd_X = (int) event.getRawX();
					mTextSelectionEnd_Y = (int) event.getRawY();

					break;
				}

				}

			}
			}

			return true;
		}

	};
	/*
	 * Handling OnClick on Annot Icon on the displayed page
	 */
	OnClickListener AnnotClickd = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub

			String TableName;
			int mLineCount;
			int mCharCount;
			int mStartXPos;
			int mStartYPos;

			DataBaseClass mDataBaseClass = new DataBaseClass(
					Display_Manager.this);
			mDataBaseClass.mDatab = mDataBaseClass.mDatah.getReadableDatabase();

			TableName = Anno_BookName + "_AnnoTable";

			// to HIGHLIGHT
			// to get the line number
			Cursor mAnnotLine = mDataBaseClass.selectLineCntFromAnnotable(v
					.getId(), TableName);
			mAnnotLine.moveToFirst();
			mLineCount = mAnnotLine.getInt(0);

			// to get the text Length
			Cursor mAnnotLength = mDataBaseClass.selectLengthFromAnnotable(v
					.getId(), TableName);
			mAnnotLength.moveToFirst();
			mCharCount = mAnnotLength.getInt(0);

			mStartXPos = v.getLeft();
			mStartYPos = v.getBottom() + 30;
			//mStartYPos = v.getTop();

			highlightAfterAnnot(mStartXPos, mStartYPos, mLineCount, mCharCount);

			// for KeyPad and Handwritten Text

			Bundle mBundle = new Bundle();
			mBundle.putInt("ID", v.getId());
			mBundle.putString("TABLENAME", TableName);

			Intent mIntent = new Intent(Display_Manager.this,
					AnnotImage.class);
			mIntent.putExtras(mBundle);
			startActivity(mIntent);

		}
	};

	/* This code is to return in home using Esc key from Key */
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		switch (KeyCode) {
		case KeyEvent.KEYCODE_BACK:
			mIsGoingHome = 1;
			MyCabinet.mFlag = MyCabinet.SWITCH_NOT_POSSIBLE;
			try {
				// acquire semaphore
				mSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// set flag for normal page operation
			mBookInfo.mObjDifFlag = PAGE_NAVIGATION_START;
			// cancel the thread i.e doInBackgroung task
			if (!mDbopp.cancel(true)) {
				Intent mIntentLibraryBack = null;
				// check the last in which view library was opened
				// if it was in GridView

				// Drope index table
				myDbBook.execSQL("DROP TABLE IF EXISTS IndexTable;");
				// finish the display manager activity
				Display_Manager.this.finish();
				startActivity(new Intent(Display_Manager.this, EpubReader.class));
				// start new activity
			}
			// reset flag
			mBookInfo.mObjDifFlag = PAGE_NAVIGATION_END;
			// Release semaphore
			mSemaphore.release();

			return true;
		}
		return false;

	}
}
