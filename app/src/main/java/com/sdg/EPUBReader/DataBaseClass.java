
package com.sdg.EPUBReader;

import java.io.ByteArrayOutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;
import android.util.Log;

public class DataBaseClass implements BaseColumns {

	public static final String WEBVIEW_DATABASE = "webiew.db";
	/*
	 * the below strings are DataBase name For Book and Tables Used in the whole
	 * application
	 */
	public static final String DATABASE_NAME = "BookDataBase.db";
	public static final String DATABASE_TABLE = "BookInfoTable";
	public static final String RECENT_BOOK_DATABASE_TABLE = "RecentBookInfoTable";
	public static final String RECENT_MAGAZINE_DATABASE_TABLE = "RecentMagazineInfoTable";
	public static final String RECENT_NOTE_TABLE = "RecentNoteTable";
	public static final String KEY_FILENAME = "FileName";
	public static final String KEY_AUTHORNAME = "Author";
	public static final String KEY_TITLE = "Title";
	public static final String KEY_BOOKMARK = "BookMark";
	public static final String KEY_PAGENO = "PageNo";
	public static final String KEY_ZOOMLEVEL = "ZoomLevel";
	public static final String KEY_XMLINDEX = "XmlIndex";
	public static final String KEY_LINENO = "LineNo";
	public static final String KEY_BOOKPRESENT = "BookPresent";

	// Tablenames and Database name to be created

	private static final String NOTE_DATABASE_TABLE = "NoteDataTable";
	private static final String CATEGORY_TABLE = "CategoryTable";
	private static final String ORGANIZER_HOME = "OrganizerHome";
	private static final String ACTIVE_WINDOW = "ActiveWindowTable";
	private static final String NOTEDATATYPE = "Datatype";
	private static final String NOTEIMAGEDATA = "Imagedata";
	// from table OrganizerHome
	private static final String KEY_NOTENAME = "NoteName";
	private static final String KEY_CATEGORYID = "CategoryId";

	// from table CategoryTable
	private static final String KEY_CATEGORYNAME = "CategoryName";

	// from table NoteDataTable
	private static final String KEY_NOTEID = "NoteId";
	private static final String KEY_DATATYPE = "Datatype";
	private static final String KEY_TEXTDATA = "Textdata";
	private static final String KEY_IMAGEDATA = "Imagedata";

	// from ActiveWindowTable
	public static final String KEY_TYPE = "Type";
	public static final String KEY_ELEMENT_NAME = "ElementName";
	public static final String KEY_NOTE_ID = "NoteId";
	// from AnnotationTable
	private static final String ANNOT_ID = "AnnotId";
	private static final String ANNOT_TEXT = "AnnotText";
	private static final String ANNOT_IMAGE = "AnnotImage";
	private static final String CHAPTER_ID = "ChapterId";
	private static final String CONTINU_FLAG = "ContinuFlag";
	private static final String XML_LEVEL = "XmlLevel";
	private static final String OFFSET = "OffSet";
	private static final String TEXT_LENGTH = "TextLength";
	private static final String LINE_NUMB = "LineNumb";

	private static final String IMAGEDATA = "ImageData";
	private static final String TYPE = "Type";
	private static final String DATEFIELD = "DateField";
	private static final String ISCHECHKED = "IsChecked";
	private static final String CABINET_TABLE = "CabinetInfoTable";

	// Initial count of the category when the system is booted up
	private static final int INITIAL_CATEGORY_COUNT = 5;
	public static boolean CABINET_CHECKED=false;

	public SQLiteDatabase mDatab = null;
	public SQLiteDatabase mForRecentTable = null;
	public DatabaseHelper mDatah;

	private boolean opened = false;

	public class DatabaseHelper extends SQLiteOpenHelper {
		// to create DATABASE
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, 1);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

		}
	}

	public SQLiteDatabase getHandle() {
		return openDB();
	}

	private SQLiteDatabase openDB() {
		if (!opened)
			mDatab = mDatah.getWritableDatabase();
		opened = true;
		return mDatab;
	}

	private void closeDB() {
		if (opened)
			mDatah.close();
		opened = false;
	}

	public DataBaseClass(Context context) {
		mDatah = new DatabaseHelper(context);
		mDatah.getWritableDatabase();

	}

	/* This Function will get author and book name from the Database */
	public Cursor returnCursor() {
		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = mDatab.rawQuery(
				"SELECT _id,Author,Title ,Type FROM " + DATABASE_TABLE
						+ " ORDER BY Type;", null);

		return mCursorForSelect;

	}

	/*
	 * This Function will check if any book is deleted from the database and
	 * then delete that book from Database
	 */
	public Cursor ReturnCursorForDelete() {

		mDatab = mDatah.getWritableDatabase();
		Cursor mCursorForDelete = mDatab.rawQuery("DELETE FROM "
				+ DATABASE_TABLE + " WHERE " + KEY_BOOKPRESENT + " =0;", null);

		return mCursorForDelete;

	}

	/*
	 * after deleteing the deleted books info from database this function will
	 * update the database
	 */
	public void UpdateBookPresent() {

		mDatab = mDatah.getWritableDatabase();
		mDatab.execSQL("UPDATE  " + DATABASE_TABLE + " SET BookPresent=0;");

	}

	/* This Function will quary for a specific position books title and author */
	public Cursor returnCursorForSpecificPos() {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = mDatab.rawQuery(
				"SELECT _id,FileName,Author,Title, Type FROM " + DATABASE_TABLE
						+ "  ;", null);

		return mCursorForSelect;
	}

	/*
	 * If any books open from home page or from Library then this function will
	 * entry that in RecentBook Table also
	 */
	public void recentBookOpendInsert(String mFilePath,
			String mExtractedAuthor, String mExtractedTitle, String recentTable) {

		int mFlag = 0;

		mFilePath = filterString(mFilePath);
		mExtractedAuthor = filterString(mExtractedAuthor);
		mExtractedTitle = filterString(mExtractedTitle);

		mForRecentTable = mDatah.getWritableDatabase();
		Cursor mCursorForSelect = mDatab.rawQuery("SELECT * FROM "
				+ recentTable + "  ;", null);
		int mCheck = mCursorForSelect.getCount();

		if (mCheck < 3) {
			mCheck++;
			try {
				mForRecentTable.execSQL("INSERT INTO " + recentTable
						+ " (FileName,Author,Title,LastUsed) VALUES ( '"
						+ mFilePath + "' , ' " + mExtractedAuthor + " ', '"
						+ mExtractedTitle + " '," + mCheck + " );");
			} catch (SQLException e) {

			}
		} else {
			mForRecentTable.execSQL("UPDATE " + recentTable
					+ " SET LastUsed=LastUsed-1;");
			try {
				Cursor mCursorForCheck = mDatab.rawQuery(
						"SELECT * FROM " + recentTable + " where  FileName='"
								+ mFilePath + "';", null);
				int mcount = mCursorForCheck.getCount();
				if (!(mcount > 0)) {
					try {
						mForRecentTable.execSQL("UPDATE " + recentTable
								+ " SET FileName= '" + mFilePath
								+ "',Author= ' " + mExtractedAuthor
								+ " ',Title= ' " + mExtractedTitle
								+ " ',LastUsed = 3 WHERE LastUsed= 0 ;");
					} catch (Exception e) {
						Log.e("MAK", "MAK" + e);
					}
				}

				// mForRecentTable.execSQL("UPDATE "+RESENT_DATABASE_TABLE+" SET
				// LastUsed = 3 WHERE FileName = '"+mFilePath+"';");
				mForRecentTable.execSQL("UPDATE " + recentTable
						+ " SET LastUsed = 3 WHERE LastUsed = 0;");
				// mForRecentTable.execSQL("UPDATE "+RESENT_DATABASE_TABLE+" SET
				// LastUsed = 1 WHERE LastUsed= 0;");

			} catch (SQLException e) {

			}
		}

		mCursorForSelect.close();

	}

	/* this function will retrive the Book title from the recent book table */
	public Cursor recentBookOpendRetrive(String recentTable) {

		mForRecentTable = mDatah.getReadableDatabase();

		Cursor mCursorForSelectRecentBook = mDatab.rawQuery("SELECT * FROM "
				+ recentTable + " ORDER BY LastUsed DESC;", null);

		return mCursorForSelectRecentBook;

	}

	/*
	 * If any book is in Recent Book Table but that book is already deleted or
	 * not
	 */
	public Cursor returnCursorForBookAvail(String BookPath) {

		mDatab = mDatah.getReadableDatabase();

		BookPath = filterString(BookPath);

		Cursor mCursorForBookAvail = mDatab.rawQuery("SELECT * FROM "
				+ DATABASE_TABLE + " where FileName= '" + BookPath + "' ;",
				null);
		int x = mCursorForBookAvail.getCount();

		return mCursorForBookAvail;
	}

	/*
	 * this function will extract the content from book to database and store it
	 * CabinetInfoTable
	 */
	public void insertExtractedData(int mTime, String mExtractedData,
			String Type) {

		mDatab = mDatah.getWritableDatabase();

		String NewString = filterString(mExtractedData);
		if (Type == "Text")
			mDatab
					.execSQL("INSERT INTO CabinetInfoTable (TextData,DateField,isChecked,Type) values ( '"
							+ NewString + "', " + mTime + ",0 ,'Text');");

	}

	

	/*
	 * this function will select all Textdata from CabinetInfoTable and return
	 * the cursor from cabinet table
	 */
	public Cursor returnCursorForCabinet() {
		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = mDatab.rawQuery(
				"SELECT _id,TextData,ImageData FROM CabinetInfoTable ;", null);

		return mCursorForSelect;
	}

	/*
	 * this function will select all Textdata from CabinetInfoTable and return
	 * the cursor from cabinet table
	 */
	public Cursor returnCursorForCabinetText() {
		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = mDatab.rawQuery(
				"SELECT TextData,_id FROM CabinetInfoTable where Type='Text';",
				null);

		return mCursorForSelect;
	}

	/*
	 * this function will select all Imagedata from CabinetInfoTable and return
	 * the cursor from cabinet table
	 */
	public Cursor returnCursorForCabinetImage() {
		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = mDatab
				.rawQuery(
						"SELECT ImageData,_id FROM CabinetInfoTable where Type='Image';",
						null);

		return mCursorForSelect;
	}

	/* this function will insert the open book in Active window table */
	public void insertInActiveWindow(String mElementName, String mFilePath,
			String mType) {

		mDatab = mDatah.getWritableDatabase();

		mFilePath = filterString(mFilePath);
		mElementName = filterString(mElementName);
		mType = filterString(mType);
		try {
			mDatab
					.execSQL("INSERT INTO ActiveWindowTable (ElementName,FilePath,Type) values ('"
							+ mElementName
							+ "','"
							+ mFilePath
							+ "', '"
							+ mType
							+ "');");
		} catch (SQLException e) {

			Log.e("Data Already exist", "" + e);
		}

	}

	/*
	 * this two function is used to check first if any book is already in active
	 * window
	 */
	public void updateActiveWindow(String mElementName, String mFilePath) {

		mDatab = mDatah.getWritableDatabase();

		mFilePath = filterString(mFilePath);
		mElementName = filterString(mElementName);

		mDatab.execSQL("UPDATE ActiveWindowTable set ElementName='"
				+ mElementName + "' ,FilePath='" + mFilePath
				+ "' where Type='Book';");

	}

	/*
	 * this two function is used to check first if any book is already in active
	 * window
	 */
	public void updateActiveWindowForBrowser(String mElementName,
			String mFilePath) {

		mDatab = mDatah.getWritableDatabase();

		mFilePath = filterString(mFilePath);
		mElementName = filterString(mElementName);

		mDatab.execSQL("UPDATE ActiveWindowTable set ElementName='"
				+ mElementName + "' ,FilePath='" + mFilePath
				+ "' where Type='Browser';");

	}

	/* this function will check in active window if any book is already present */
	public Cursor bookPresentInActiveTable() {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = mDatab.rawQuery(
				"SELECT ElementName FROM ActiveWindowTable WHERE Type='Book';",
				null);

		return mCursorForSelect;

	}

	/* this function will check in active window if any book is already present */
	public Cursor BrowserPresentInActiveTable() {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = mDatab
				.rawQuery(
						"SELECT ElementName FROM ActiveWindowTable WHERE Type='Browser';",
						null);

		return mCursorForSelect;

	}

	/* this will heck in active window table if any magazine id already there */
	public Cursor MagazinePresentInActiveTable() {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = mDatab
				.rawQuery(
						"SELECT ElementName FROM ActiveWindowTable WHERE Type='Magazine';",
						null);

		return mCursorForSelect;

	}

	/* to select all from data from Active Window Table */
	public Cursor selectAllFromActiveWindow() {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = mDatab.rawQuery(
				"SELECT * FROM ActiveWindowTable ;", null);

		return mCursorForSelect;
	}

	/* to slelet last NoteId from ActiveWindow table */
	public Cursor selectLastNoteIdFromActiveWindow() {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = mDatab
				.rawQuery(
						"SELECT NoteId FROM ActiveWindowTable where Type='Note';",
						null);

		return mCursorForSelect;
	}

	/* to select which is clicked from active window,if it is a book or a note */
	public Cursor selectTypeActiveWindow(String mElement) {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = null;
		mElement = filterString(mElement);
		try {
			mCursorForSelect = mDatab.rawQuery(
					"SELECT Type,FilePath,NoteId FROM ActiveWindowTable WHERE ElementName='"
							+ mElement + "' ;", null);
		} catch (SQLException e) {
			Log.e("SQLITE ERROR", "" + e);
		}
		return mCursorForSelect;
	}

	/* to delete a specific item from active window */
	public void deleteSpecificActivity(String mPosition) {

		mDatab = mDatah.getReadableDatabase();
		mDatab.execSQL("DELETE FROM ActiveWindowTable where ElementName= '"
				+ mPosition + "';");

	}

	/* to delete a specific book from active window */
	public void deleteSpecificBook() {

		mDatab = mDatah.getReadableDatabase();

		mDatab.execSQL("DELETE FROM ActiveWindowTable where Type='Book';");

	}

	public int insertInNoteTable(String mExtractedText) {

		int mFlag;
		mDatab = mDatah.getReadableDatabase();

		mExtractedText = filterString(mExtractedText);

		Cursor mDataPresent = mDatab.rawQuery(
				"SELECT * FROM OrganizerHome where NoteName='" + mExtractedText
						+ ".note' and CategoryId=1;", null);
		if (mDataPresent.getCount() > 0) {
			mFlag = 0;
		} else {

			mDatab
					.execSQL("INSERT INTO OrganizerHome (NoteName,CategoryId) values ('"
							+ mExtractedText + ".note" + "',1);");
			Cursor mLastNoteId = mDatab.rawQuery(
					"Select _id from OrganizerHome where NoteName='"
							+ mExtractedText + ".note';", null);
			mLastNoteId.moveToFirst();
			int mLastNoteNo = mLastNoteId.getInt(0);
			/*declare a cursor*/
			Cursor mInsertCursor = null;
			/*first chech if any one element is selected*/
			Cursor mCursorForisChecked = mDatab.rawQuery(
					"SELECT * FROM CabinetInfoTable where isChecked=1;", null);
			/*if atleast one element is checked*/
			if(mCursorForisChecked.getCount()>0){
			 mInsertCursor = mDatab
					.rawQuery(
							"Select TextData,ImageData from CabinetInfoTable where isChecked=1",
							null);
			 /*indicate that data saved after make some checkbox*/
			 CABINET_CHECKED=true;
			}
			/*otherwise select all from cabinet*/
			else{
				mInsertCursor = mDatab
				.rawQuery(
						"Select TextData,ImageData from CabinetInfoTable where isChecked=0",
						null);
				/*indicate that data saved when no check box was selected*/
				CABINET_CHECKED=false;
			}
			mInsertCursor.moveToFirst();

			for (int i = 0; i < mInsertCursor.getCount(); i++) {
				// ----------------------------------------------
				if (mInsertCursor.getString(0) != null) {
					mDatab = mDatah.getWritableDatabase();
					String NewString = mInsertCursor.getString(0);
					NewString = filterString(NewString);
					mDatab
							.execSQL("INSERT INTO NoteDataTable (NoteId,Datatype,Textdata) values ("
									+ mLastNoteNo
									+ ",'text','"
									+ NewString
									+ "');");

				} else if (mInsertCursor.getBlob(1) != null) {
					mDatab = mDatah.getWritableDatabase();
					byte[] image = mInsertCursor.getBlob(1);
					ContentValues cv = new ContentValues();
					cv.put(KEY_NOTEID, mLastNoteNo);
					cv.put(NOTEIMAGEDATA, image);
					cv.put(NOTEDATATYPE, "Image");
					mDatab.insert(NOTE_DATABASE_TABLE, null, cv);
					cv.clear();
				}
				mInsertCursor.moveToNext();
			}

			mFlag = 1;
		}

		return mFlag;
	}

	/*
	 * this function select the text present in a particular note and will
	 * return the cursor from Organizer table
	 */
	public Cursor returnCursorForOrganizer(int mNoteId) {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = mDatab.rawQuery(
				"SELECT _id,Textdata,Imagedata FROM NoteDataTable where NoteId="
						+ mNoteId + ";", null);

		return mCursorForSelect;
	}

	/* when save a new note this function will return the NoteName */
	public Cursor returnTextForOrganizer(int mNoteId) {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = mDatab
				.rawQuery("SELECT NoteName FROM OrganizerHome where _id="
						+ mNoteId + ";", null);

		return mCursorForSelect;
	}

	/*
	 * after saving some text from cabinet to note this function will update the
	 * cabinet table
	 */
	public void updateCabinetTableAfterSave() {

		mDatab = mDatah.getReadableDatabase();
		/* code to be added to add in NoteTable */
		mDatab.execSQL("DELETE FROM CabinetInfoTable where isChecked=1;");
		// update the cabinet page after data inserted in mynote
		Cursor mCursor = mDatab.rawQuery("select _id from CabinetInfoTable ;",
				null);
		mCursor.moveToFirst();
		for (int i = 1; i <= mCursor.getCount(); i++) {
			int mTemp = mCursor.getInt(0);
			mDatab.execSQL("UPDATE CabinetInfoTable SET _id=" + i
					+ " WHERE _id=" + mTemp + " ;");
			mCursor.moveToNext();

		}

	}

	/* drop the index table when it is needed */
	public void dropIndexTable() {

		mDatab = mDatah.getReadableDatabase();
		mDatab.execSQL("DROP TABLE IF EXISTS " + EpubReader.INDEX_TABLE + " ;");

	}

	/* this function will delete saved note from cabinet */
	public void deleteFromCabinetAfterSave(boolean mCheckBoxSelected) {

		mDatab = mDatah.getReadableDatabase();
		if(mCheckBoxSelected==true){
		mDatab.execSQL("DELETE FROM CabinetInfoTable where isChecked=1");
		}
		else {
			mDatab.execSQL("DELETE FROM CabinetInfoTable where isChecked=0");
		}

	}

	/* this function will create all the table at the start of the application */
	public void createAllTable() {

		mDatab = mDatah.getReadableDatabase();
		try{
		mDatab
				.execSQL("CREATE TABLE IF NOT EXISTS ThemeIDTable ( ThemeID VARCHAR,ColorID integer) ;");
		// create BookInfoTable
		mDatab
				.execSQL("CREATE TABLE IF NOT EXISTS BookInfoTable ( _id integer PRIMARY KEY , FileName VARCHAR UNIQUE , Author VARCHAR, "
						+ "Title VARCHAR,ChapterNo INT(4), BookMark VARCHAR,PageNo INT(10),"
						+ " ZoomLevel INT(10), XmlLevel VARCHAR, BookOpen INI(2), BookPresent INT(2),"
						+ "Type VARCHAR );");
		// create RecentBook Table
		mDatab
				.execSQL("create table if not exists RecentBookInfoTable (_id integer PRIMARY KEY ,FileName VARCHAR UNIQUE, Author VARCHAR , "
						+ "Title VARCHAR, LastUsed INT );");
		// create RecentMagazine Table
		mDatab
				.execSQL("create table if not exists RecentMagazineInfoTable (_id integer PRIMARY KEY ,FileName VARCHAR UNIQUE, Author VARCHAR , "
						+ "Title VARCHAR, LastUsed INT );");

		// create Cabinet Table
		mDatab
				.execSQL("create table if not exists CabinetInfoTable (_id integer primary key,Type VARCHAR, TextData VARCHAR,"
						+ "ImageData BLOB,DateField DATE, isChecked INT);");

		// create Index Table
		mDatab
				.execSQL("CREATE TABLE IF NOT EXISTS IndexTable (PageNo INT,ChapterNo INT,XmlLevel VARCHAR,LineNo INT,ZoomLevel FLOAT);");

		// create ActiveWindow table
		
			mDatab
					.execSQL("CREATE TABLE IF NOT EXISTS ActiveWindowTable (ElementName VARCHAR  ,NoteId INT UNIQUE, FilePath TEXT UNIQUE,Type VARCHAR );");
			mDatab
					.execSQL("CREATE TABLE IF NOT EXISTS CurrentIndexTable"
							+ " (PageNo INT,ChapterNo INT,XmlLevel VARCHAR,LineNo INT,ZoomLevel FLOAT);");
			mDatab
					.execSQL("CREATE TABLE IF NOT EXISTS PreviousIndexTable"
							+ " (PageNo INT,ChapterNo INT,XmlLevel VARCHAR,LineNo INT,ZoomLevel FLOAT);");

			mDatab
					.execSQL("CREATE TABLE IF NOT EXISTS BookMarkTable"
							+ " (BookName VARCHAR,PageNo INT,ChapterNo INT,XmlLevel VARCHAR,LineNo INT,ZoomLevel FLOAT,TocClicked INT);");
		
		// Creating table in database table name - RecentNoteTable
		mDatab
				.execSQL("create table if not exists RecentNoteTable (NoteId INT UNIQUE, NoteName VARCHAR, Position INT);");

		// Creating table in database table name - Organizer_home
		mDatab.execSQL("create table if not exists " + ORGANIZER_HOME
				+ "(_id integer primary key, " + KEY_NOTENAME + " VARCHAR , "
				+ KEY_CATEGORYID + " INT );");

		// Creating table in database table name - CategoryTable
		mDatab.execSQL("create table if not exists " + CATEGORY_TABLE
				+ "(_id integer primary key," + KEY_CATEGORYNAME
				+ " VARCHAR );");

		// Creating table in database table name - NoteDataTable
		mDatab.execSQL("create table if not exists " + NOTE_DATABASE_TABLE
				+ " (_id integer primary key," + KEY_NOTEID + " INT , "
				+ KEY_DATATYPE + " VARCHAR ," + KEY_TEXTDATA + " VARCHAR ,"
				+ KEY_IMAGEDATA + " blob );");
		}catch(SQLException e){
			
		}

	}

	/*
	 * this function will return the theme id from theme table and application
	 * will start with that preselected theme
	 */
	public Cursor returnThemeId() {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCursor = mDatab.rawQuery(
				"SELECT ThemeID,ColorID FROM ThemeIDTable ;", null);

		return mCursor;
	}

	/*
	 * this function will insert default theme as 0 i.e first theme as default
	 * if no theme is selected
	 */
	public void insertThemeId() {

		mDatab = mDatah.getReadableDatabase();
		mDatab
				.execSQL("INSERT INTO ThemeIDTable (ThemeID,ColorID) VALUES ('0',0);");

	}

	/* this function will insert the extracted data from book to note */
	public void insertNoteDataInNoteTable(int mLastNoteId, String noteData,String Type) {

		mDatab = mDatah.getReadableDatabase();
		if(Type.equalsIgnoreCase("Text")){
		String NewString = filterString(noteData);

		mDatab
				.execSQL("INSERT INTO NoteDataTable (NoteId,Datatype,Textdata) values ("
						+ mLastNoteId + ",'text','" + NewString + "');");
		}
		if(Type.equalsIgnoreCase("Image")){
			
			/* first create a output stream */
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			/* first decode the image from the file path */
			Bitmap mCopyImage = BitmapFactory.decodeFile(noteData);
			/* compress the image first */
			mCopyImage.compress(Bitmap.CompressFormat.PNG, 100, out);
			/* change it to the byte array */
			byte[] outputByte = out.toByteArray();
				

				ContentValues cv = new ContentValues();
				cv.put(KEY_NOTEID, mLastNoteId);
				cv.put(KEY_IMAGEDATA, outputByte);
				cv.put(KEY_DATATYPE, "image");
				mDatab.insert(NOTE_DATABASE_TABLE, null, cv);
				cv.clear();

			
		}

	}

	/* this function will get the recent note data */
	public Cursor recentNoteOpendRetrive() {

		mForRecentTable = mDatah.getReadableDatabase();

		Cursor mCursorForSelectRecentNote = mDatab.rawQuery("SELECT * FROM "
				+ RECENT_NOTE_TABLE + " ORDER BY Position DESC;", null);

		return mCursorForSelectRecentNote;

	}

	/* Method to delete the content after the text is dragged to Note */
	public void deleteAfterDragToNote(int mDeletePosition) {

		mDatab = mDatah.getReadableDatabase();
		mDatab.execSQL("DELETE FROM CabinetInfoTable WHERE _id ="
				+ mDeletePosition + ";");
		// update the cabinet page after data inserted in mynote
		Cursor mCursor = mDatab.rawQuery("select _id from CabinetInfoTable ;",
				null);
		mCursor.moveToFirst();
		for (int i = 1; i <= mCursor.getCount(); i++) {
			int mTemp = mCursor.getInt(0);
			mDatab.execSQL("UPDATE CabinetInfoTable SET _id=" + i
					+ " WHERE _id=" + mTemp + " ;");
			mCursor.moveToNext();

		}

	}

	/*
	 * this function will be usefull to show cabinet checkbox when it will
	 * refresh
	 */
	public Cursor isCheckedInCabinet(int position) {

		mDatab = mDatah.getReadableDatabase();
		position++;
		Cursor mCursorIsSelected = mDatab.rawQuery(
				"SELECT isChecked from CabinetInfoTable where _id=" + position
						+ ";", null);

		return mCursorIsSelected;
	}

	/* Method to insert data into note table */
	public int insertInNoteTableFromRadial(String mExtractedText,
			String noteData) {

		int mFlag;
		mDatab = mDatah.getReadableDatabase();

		mExtractedText = filterString(mExtractedText);

		Cursor mDataPresent = mDatab.rawQuery(
				"SELECT * FROM OrganizerHome where NoteName='" + mExtractedText
						+ "' and CategoryId=1;", null);
		if (mDataPresent.getCount() > 0) {
			mFlag = 0;
		} else {

			mDatab
					.execSQL("INSERT INTO OrganizerHome (NoteName,CategoryId) values ('"
							+ mExtractedText + ".note" + "',1);");
			Cursor mLastNoteId = mDatab.rawQuery(
					"Select _id from OrganizerHome where NoteName='"
							+ mExtractedText + ".note';", null);
			mLastNoteId.moveToFirst();
			int mLastNoteNo = mLastNoteId.getInt(0);

			noteData = filterString(noteData);

			mDatab
					.execSQL("INSERT INTO NoteDataTable (NoteId,Datatype,Textdata) values ("
							+ mLastNoteNo + ",'text','" + noteData + "');");

			mFlag = 1;
		}

		return mFlag;
	}

	/*
	 * this function will delete the activewindow and indextable whem applicatio
	 * destroy
	 */
	public void dropTables() {

		mDatab = mDatah.getReadableDatabase();
		mDatab.execSQL("DROP TABLE IF EXISTS ActiveWindowTable");
		mDatab.execSQL("DROP TABLE IF EXISTS IndexTable");

	}

	/*
	 * this function will reset the cabinet checkbox when application going to
	 * close
	 */
	public void resetCabinetCheckBox() {

		mDatab = mDatah.getReadableDatabase();
		mDatab.execSQL("UPDATE CabinetInfoTable set isChecked=0");

	}

	/* to check atleast one element is checked in cabinet for save as */
	public Cursor selectCheckedAllFromCabinet() {

		mDatab = mDatah.getReadableDatabase();

		Cursor mCursorForisChecked = mDatab.rawQuery(
				"SELECT * FROM CabinetInfoTable where isChecked=1;", null);

		return mCursorForisChecked;
	}

	/* this function will check the selected theme id */
	public Cursor selectThemeId() {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForTheme = mDatab.rawQuery(
				"Select ThemeId ,ColorID from ThemeIDTable ;", null);

		return mCursorForTheme;
	}

	/* after changing the theme this function will update the theme in database */
	public void updateThemeId(int position, int color) {

		mDatab = mDatah.getReadableDatabase();
		mDatab.execSQL("UPDATE ThemeIDTable SET ThemeID=" + position
				+ ",ColorID=" + color + ";");

	}

	/*
	 * The data base operation functions for the whole organizer logic .....
	 */
	public Cursor returnAllFromOrganizeHome() {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCategoryCursor = mDatab.rawQuery("SELECT * FROM "
				+ ORGANIZER_HOME + " ;", null);

		return mCategoryCursor;
	}

	// database function for fetching all category name
	public Cursor returnAllCategoryName() {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCategoryCursor = mDatab.rawQuery("SELECT CategoryName FROM "
				+ CATEGORY_TABLE + ";", null);

		return mCategoryCursor;
	}

	// // database function for returning category id based on its name
	public Cursor returnCategoryName(String mSelectedCategory) {

		mDatab = mDatah.getReadableDatabase();

		mSelectedCategory = filterString(mSelectedCategory);

		Cursor mTempCursor = mDatab.rawQuery(
				"SELECT _id FROM CategoryTable WHERE CategoryName = '"
						+ mSelectedCategory + "';", null);

		return mTempCursor;
	}

	// database function for returning all note ids from the OrganizerHOmeTable
	public Cursor returnAllNoteId() {

		mDatab = mDatah.getReadableDatabase();
		Cursor mLastNoteCursor = mDatab.rawQuery("SELECT MAX(_id) FROM "
				+ ORGANIZER_HOME + ";", null);

		return mLastNoteCursor;
	}

	// database function for inserting into organizer home table
	public void insertIntoOrganizerHome(String mNoteDefaultName,
			int mLastNoteId, int mCategoryDefaultId) {

		mDatab = mDatah.getReadableDatabase();

		mNoteDefaultName = filterString(mNoteDefaultName);

		mDatab.execSQL("INSERT INTO " + ORGANIZER_HOME + "(" + KEY_NOTENAME
				+ "," + KEY_CATEGORYID + ") VALUES ( '" + mNoteDefaultName
				+ mLastNoteId + ".note','" + mCategoryDefaultId + "');");

	}

	// database function for selecting all note based on a particular category
	public Cursor selectAllFromOrganizerOnCategory(int mSelectedCategoryId) {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCascadeViewCursor = mDatab.rawQuery("SELECT * FROM "
				+ ORGANIZER_HOME + " WHERE CategoryId=" + mSelectedCategoryId
				+ ";", null);

		return mCascadeViewCursor;
	}

	// // database function for checking note present in active window table
	public Cursor notePresentInActiveTable() {

		mDatab = mDatah.getReadableDatabase();
		Cursor mActiveNoteCursor = mDatab.rawQuery(
				"SELECT ElementName FROM ActiveWindowTable WHERE Type='Note';",
				null);

		return mActiveNoteCursor;

	}

	// // database function for inserting entry in active window table
	public void insertNoteInActiveTable(String tempNoteName, int mSelectedNoteId) {

		mDatab = mDatah.getReadableDatabase();

		tempNoteName = filterString(tempNoteName);
		try {
			mDatab.execSQL("INSERT INTO " + ACTIVE_WINDOW + " ("
					+ KEY_ELEMENT_NAME + ", " + KEY_NOTE_ID + ", " + KEY_TYPE
					+ ") values ('" + tempNoteName + "'," + mSelectedNoteId
					+ ",'Note');");
		} catch (SQLException e) {
			Log.e("Note Name Already Exists", "" + e);
		}

	}

	// database function for updating note entry in active window table
	public void updateNoteInActiveTable(String tempNoteName, int mSelectedNoteId) {

		tempNoteName = filterString(tempNoteName);

		mDatab = mDatah.getReadableDatabase();
		mDatab.execSQL("UPDATE ActiveWindowTable SET " + KEY_ELEMENT_NAME
				+ " = '" + tempNoteName + "', " + KEY_NOTE_ID + " = "
				+ mSelectedNoteId + " WHERE " + KEY_TYPE + " = 'Note';");

	}

	// database function for inserting data into recent note table

	public void insertInRecentNoteTable(int selectedNoteId,
			String tempNoteName, int mRecentNoteCount) {

		mDatab = mDatah.getReadableDatabase();

		tempNoteName = filterString(tempNoteName);

		mDatab.execSQL("INSERT INTO " + RECENT_NOTE_TABLE
				+ " (NoteId, NoteName,Position) VALUES ( '" + selectedNoteId
				+ "' , ' " + tempNoteName + " '," + mRecentNoteCount + " );");

	}

	// database function for selecting note from recent note table
	public Cursor selectFromRecentNote(String tempNoteName) {

		tempNoteName = filterString(tempNoteName);

		mDatab = mDatah.getReadableDatabase();
		Cursor mRecentNoteCursor = mDatab.rawQuery("SELECT * FROM "
				+ RECENT_NOTE_TABLE + " where  NoteName='" + tempNoteName
				+ "';", null);

		return mRecentNoteCursor;

	}

	// database function for updating the data in recent note table
	public void updateDataRecentNoteTable(int selectedNoteId,
			String tempNoteName) {

		mDatab = mDatah.getReadableDatabase();

		tempNoteName = filterString(tempNoteName);

		mDatab.execSQL("UPDATE " + RECENT_NOTE_TABLE + " SET NoteId= "
				+ selectedNoteId + ",NoteName= ' " + tempNoteName
				+ "',Position = 3 WHERE Position = 0 ;");

	}

	// database function for updating the recent note table
	public void updateRecentNoteTable() {

		mDatab = mDatah.getReadableDatabase();
		mDatab.execSQL("UPDATE " + RECENT_NOTE_TABLE
				+ " SET Position=Position-1;");

	}

	// database function for updating the recent note table by checking the
	// position
	public void updateRecentNoteTableCheck() {

		mDatab = mDatah.getReadableDatabase();
		mDatab.execSQL("UPDATE " + RECENT_NOTE_TABLE
				+ " SET Position = 3 WHERE Position = 0;");

	}

	// database function for updating the category in the OrganizerHOme tABLE
	public void updateOrganizerHomeCategory(int mCurrentCategortyId,
			int mCurrentNoteId) {

		mDatab = mDatah.getReadableDatabase();
		mDatab
				.execSQL("UPDATE  " + ORGANIZER_HOME + " SET CategoryId = "
						+ mCurrentCategortyId + " WHERE _id = "
						+ mCurrentNoteId + " ;");

	}

	// database function for inserting the data into the note data table
	public void insertIntoNoteDataTable(int mCurrentNoteId, String mDataType,
			String mTextDataString) {

		mDatab = mDatah.getReadableDatabase();

		// if data to be inserted in NoteDataTable is string
		if (mDataType == "text") {
			mTextDataString = filterString(mTextDataString);
			mDataType = filterString(mDataType);

			mDatab.execSQL("INSERT INTO " + NOTE_DATABASE_TABLE + "("
					+ KEY_NOTEID + "," + KEY_DATATYPE + "," + KEY_TEXTDATA
					+ ") VALUES ( " + mCurrentNoteId + ",'" + mDataType + "','"
					+ mTextDataString + "');");
		}
		// if data to be inserted in NOteDataTable is image
		else if (mDataType == "image") {
			Cursor mGetNoteImageData = mDatab.rawQuery(
					"SELECT ImageData FROM CabinetInfoTable WHERE _id >="
							+ MyCabinet.mDeletePosition
							+ " AND Type = 'Image';", null);
			mGetNoteImageData.moveToFirst();
			byte[] mTempByteImage = mGetNoteImageData.getBlob(0);

			ContentValues cv = new ContentValues();
			cv.put(KEY_NOTEID, mCurrentNoteId);
			cv.put(KEY_IMAGEDATA, mTempByteImage);
			cv.put(KEY_DATATYPE, "image");
			mDatab.insert(NOTE_DATABASE_TABLE, null, cv);
			cv.clear();

		}

	}

	// database function for fetching the note data based on note id ...
	public Cursor getNoteData(int mCurrentNoteId) {

		mDatab = mDatah.getReadableDatabase();
		Cursor mGetNoteDataCursor = mDatab.rawQuery("SELECT _id,"
				+ KEY_TEXTDATA + " , " + KEY_IMAGEDATA + " from "
				+ NOTE_DATABASE_TABLE + " WHERE " + KEY_NOTEID + " = "
				+ mCurrentNoteId + ";", null);

		return mGetNoteDataCursor;

	}

	// database function for inserting the new category in the category table

	public void insertIntoCategoryTable(String mNewCategoryName) {

		mDatab = mDatah.getReadableDatabase();

		mNewCategoryName = filterString(mNewCategoryName);

		mDatab.execSQL("INSERT INTO " + CATEGORY_TABLE + "(" + KEY_CATEGORYNAME
				+ ") VALUES ( '" + mNewCategoryName + "');");

	}

	// database function for updating the cabinet info table
	public void updateCabinetTable() {

		mDatab = mDatah.getReadableDatabase();
		mDatab.execSQL("UPDATE CabinetInfoTable set isChecked=0");

	}

	// this method is for returning the notes names for scrolling
	public Cursor returnNoteNameForScrolling(int smFrontNoteNumber) {
		// TODO Auto-generated method stub

		mDatab = mDatah.getReadableDatabase();
		Cursor mCategoryCursor = mDatab.rawQuery("SELECT * FROM "
				+ ORGANIZER_HOME + " WHERE _id >=" + smFrontNoteNumber + ";",
				null);

		return mCategoryCursor;
	}

	// this method is for inserting the initial predefined categories in the
	// category table
	public int insertInitialCategories() {

		mDatab = mDatah.getReadableDatabase();

		try {
			mDatab.execSQL(" INSERT INTO " + CATEGORY_TABLE + " ("

			+ KEY_CATEGORYNAME + ") VALUES ('All Notes');");
			mDatab.execSQL(" INSERT INTO " + CATEGORY_TABLE + " ("
					+ KEY_CATEGORYNAME + ") VALUES ('Food');");
			mDatab.execSQL(" INSERT INTO " + CATEGORY_TABLE + " ("
					+ KEY_CATEGORYNAME + ") VALUES ('Travel');");
			mDatab.execSQL(" INSERT INTO " + CATEGORY_TABLE + " ("
					+ KEY_CATEGORYNAME + ") VALUES ('Projects');");
			mDatab.execSQL(" INSERT INTO " + CATEGORY_TABLE + " ("
					+ KEY_CATEGORYNAME + ") VALUES ('Music');");
			// TODO Auto-generated method stub
		} catch (SQLException e) {

		}

		return INITIAL_CATEGORY_COUNT;
	}

	/* functions added fro EpubReader class */
	public void insertInBookDataBase(String fileName, String AuthorName,
			String BookTitle, String Type) {

		mDatab = mDatah.getReadableDatabase();

		BookTitle = filterString(BookTitle);
		AuthorName = filterString(AuthorName);
		fileName = filterString(fileName);

		try {
			mDatab
					.execSQL(" INSERT INTO BookInfoTable (FileName , Author,Title,BookPresent,Type) VALUES ( '"
							+ fileName
							+ "', '"
							+ AuthorName
							+ "', '"
							+ BookTitle + "', '1','" + Type + "' )  ;");
		} catch (SQLException e) {
		}

	}

	public Cursor searchASpecificBook(String mTemp) {

		mTemp = filterString(mTemp);

		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorSearch = null;
		try {
			mCursorSearch = mDatab.rawQuery(
					"SELECT * from BookInfoTable where FileName='" + mTemp
							+ "';", null);
		} catch (SQLException e) {
		}

		return mCursorSearch;
	}

	public void updateInBookDataBase(String mTemp) {

		mDatab = mDatah.getReadableDatabase();

		mTemp = filterString(mTemp);

		try {
			mDatab
					.execSQL("UPDATE BookInfoTable SET BookPresent='1' WHERE FileName='"
							+ mTemp + "';");
		} catch (SQLException e) {
		}

	}

	public void deleteInBookDataBase() {

		mDatab = mDatah.getReadableDatabase();
		try {
			/* if any book is not present in the database then delete that book */
			mDatab.execSQL("DELETE FROM BookInfoTable WHERE BookPresent =0;");
		} catch (SQLException e) {

		}

	}

	public Cursor selectBookFromActiveWindow(String Type) {

		mDatab = mDatah.getReadableDatabase();
		Cursor mActiveWindowCursor = mDatab.rawQuery("SELECT ElementName FROM "
				+ ACTIVE_WINDOW + " WHERE Type='" + Type + "';", null);

		return mActiveWindowCursor;

	}

	public void updateIsCheckedInCabinetToSet(int mPosition) {

		mDatab = mDatah.getReadableDatabase();
		mPosition++;
		mDatab.execSQL("UPDATE CabinetInfoTable set isChecked=1 where _id="
				+ mPosition + " and isChecked=0;");

	}

	public void updateIsCheckedInCabinetToUnSet(int mPosition) {

		mDatab = mDatah.getReadableDatabase();
		mDatab.execSQL("UPDATE CabinetInfoTable set isChecked=0 where _id="
				+ (mPosition + 1) + " and isChecked=1;");

	}

	public Cursor selectTextDataFromCabinet(int position) {

		mDatab = mDatah.getReadableDatabase();
		position++;
		Cursor mGetText = mDatab.rawQuery(
				"SELECT TextData, ImageData FROM CabinetInfoTable where _id="
						+ position + " ;", null);

		return mGetText;

	}

	// This method is for filtering the data ... removing "'"
	public String filterString(String mExtractedData) {
		String tempString[];
		String NewString;

		tempString = mExtractedData.split("'");
		int i = 1;
		int mak = tempString.length;
		NewString = tempString[0];
		while (tempString.length > i) {

			NewString += "''" + tempString[i];
			i++;
		}

		return NewString;

	}

	/* this function will get the full text for the cabinet */
	public Cursor getFullText(int position) {

		mDatab = mDatah.getReadableDatabase();
		position++;
		Cursor mCursorIsSelected = mDatab.rawQuery(
				"SELECT TextData from CabinetInfoTable where _id=" + position
						+ ";", null);

		return mCursorIsSelected;
	}

	/* this function will get the full text from the note */
	public String getFullTextForNote(int position, int NoteId) {

		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = mDatab.rawQuery(
				"SELECT Textdata FROM NoteDataTable where NoteId=" + NoteId
						+ ";", null);
		// String NoteFullText=
		mCursorForSelect.moveToPosition(position);
		String NoteFullText = mCursorForSelect.getString(0);
		return NoteFullText;
	}

	public Cursor getNoteID(String mCabinetText) {
		// TODO Auto-generated method stub
		int NoteID;
		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorNoteID = mDatab.rawQuery(
				"Select _id from OrganizerHome where NoteName='" + mCabinetText
						+ "';", null);
		return mCursorNoteID;

	}

	/************* proto 3 Changes *******************/

	/* this function will insert the extracted data from book to note */
	public void insertAnnotDataInAnnotTable(int annotid, String annotext,
			byte[] image, int chaptid, int contiFlg, String xmllevel,
			int offset, int textLen, int LineNumb, String BookName) {

		String TableName = BookName + "_AnnoTable";
		TableName = "'" + TableName + "'";
		mDatab = mDatah.getReadableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(ANNOT_ID, annotid);
		cv.put(ANNOT_TEXT, annotext);
		cv.put(ANNOT_IMAGE, image);
		cv.put(CHAPTER_ID, chaptid);
		cv.put(CONTINU_FLAG, contiFlg);
		cv.put(XML_LEVEL, xmllevel);
		cv.put(OFFSET, offset);
		cv.put(TEXT_LENGTH, textLen);
		cv.put(LINE_NUMB, LineNumb);
		mDatab.insert(TableName, null, cv);
		cv.clear();
	}

	public Cursor getAnnotInfoByXMLlevel(String mXMLlevel, int ChaptID,
			String TableName) {
		mDatab = mDatah.getReadableDatabase();

		Cursor mActiveWindowCursor = mDatab.rawQuery("SELECT * FROM '"
				+ TableName + "' WHERE XmlLevel = '" + mXMLlevel
				+ "' AND ChapterId = " + ChaptID + " and ContinuFlag = 1 ",
				null);

		return mActiveWindowCursor;

	}

	public Cursor AnnotCount_XMLlevel_FromAnnotable(String mXMLlevel,
			int mChaptID, String TableName) {
		mDatab = mDatah.getReadableDatabase();
		Cursor mActiveWindowCursor = mDatab.rawQuery("SELECT count(*) FROM '"
				+ TableName + "' WHERE XmlLevel = '" + mXMLlevel
				+ "' and ChapterId = " + mChaptID + " and ContinuFlag = 1 ",
				null);

		return mActiveWindowCursor;

	}

	public Cursor selectAnnot_IDFromAnnotable(String TableName) {
		mDatab = mDatah.getReadableDatabase();
		Cursor mActiveWindowCursor = mDatab.rawQuery("SELECT AnnotId FROM '"
				+ TableName + "' ", null);
		return mActiveWindowCursor;

	}

	public Cursor selectTextFromAnnotable(int ID, String TableName) {
		mDatab = mDatah.getReadableDatabase();
		Cursor mActiveWindowCursor = mDatab.rawQuery("SELECT AnnotText FROM '"
				+ TableName + "' WHERE AnnotId = " + ID, null);

		return mActiveWindowCursor;

	}

	public Cursor selectLineCntFromAnnotable(int ID, String TableName) {
		mDatab = mDatah.getReadableDatabase();
		Cursor mActiveWindowCursor = mDatab.rawQuery(
				"SELECT SUM(LineNumb) FROM '" + TableName
						+ "' WHERE AnnotId = " + ID, null);

		return mActiveWindowCursor;
	}

	public Cursor selectLengthFromAnnotable(int ID, String TableName) {
		mDatab = mDatah.getReadableDatabase();
		Cursor mActiveWindowCursor = mDatab.rawQuery(
				"SELECT SUM(TextLength) FROM '" + TableName
						+ "' WHERE AnnotId = " + ID, null);

		return mActiveWindowCursor;

	}

	public Cursor selectBlobFromAnnotable(int ID, String TableName) {
		mDatab = mDatah.getReadableDatabase();
		Cursor mActiveWindowCursor = mDatab.rawQuery("SELECT AnnotImage FROM '"
				+ TableName + "' WHERE AnnotId = " + ID, null);
		return mActiveWindowCursor;

	}

	public void CreateAnnoTable(String BookName) {
		String TableName = new String();
		TableName = BookName + "_AnnoTable";
		// Creating table in database table name - AnnotationTable
		mDatab = mDatah.getReadableDatabase();
		mDatab.execSQL("create table if not exists '" + TableName
				+ "' (_id integer primary key, " + ANNOT_ID + " INT , "
				+ ANNOT_TEXT + " VARCHAR , " + ANNOT_IMAGE + " blob , "
				+ CHAPTER_ID + " INT , " + CONTINU_FLAG + " INT , " + XML_LEVEL
				+ " VARCHAR , " + OFFSET + " INT , " + TEXT_LENGTH + " INT , "
				+ LINE_NUMB + " INT );");
	}

	public Cursor selectIdFromAnnotable(String BookName) {
		mDatab = mDatah.getReadableDatabase();

		String TableName = new String();
		TableName = BookName + "_AnnoTable";
		Cursor mActiveWindowCursor = mDatab.rawQuery(
				"SELECT max(AnnotId) FROM '" + TableName + "' ", null);
		return mActiveWindowCursor;

	}

	// method for deleting the note from database and
	// removing its entry from the recent table and active window table
	public void deleteNote(int mSelectedNoteId) {
		mDatab = mDatah.getReadableDatabase();
		try {
			/* if any book is not present in the database then delete that book */
			mDatab.execSQL("DELETE FROM OrganizerHome WHERE _id = "
					+ mSelectedNoteId + ";");
			mDatab.execSQL("DELETE FROM NoteDataTable WHERE NoteId = "
					+ mSelectedNoteId + ";");
			mDatab.execSQL("DELETE FROM ActiveWindowTable WHERE NoteId = "
					+ mSelectedNoteId + ";");
			mDatab.execSQL("DELETE FROM RecentNoteTable WHERE NoteId = "
					+ mSelectedNoteId + ";");

		} catch (SQLException e) {

		}
	}

	public Cursor getOffsetInfo(String mXMLlevel, int ChaptID, String TableName) {
		mDatab = mDatah.getReadableDatabase();

		Cursor mActiveWindowCursor = mDatab.rawQuery("SELECT OffSet FROM '"
				+ TableName + "' WHERE XmlLevel = '" + mXMLlevel
				+ "' AND ChapterId = " + ChaptID + " and ContinuFlag = 1 ",
				null);

		return mActiveWindowCursor;

	}

	public Cursor OffsetCount(String mXMLlevel, int ChaptID, String BookName) {
		mDatab = mDatah.getReadableDatabase();
		String TableName = new String();
		TableName = BookName + "_AnnoTable";

		Cursor mActiveWindowCursor = mDatab.rawQuery("SELECT count(*) FROM '"
				+ TableName + "' WHERE XmlLevel = '" + mXMLlevel
				+ "' AND ChapterId = " + ChaptID + " and ContinuFlag = 1 ",
				null);

		return mActiveWindowCursor;

	}

	public Cursor getNoOfTextAndImage(String TextOrImage) {
		mDatab = mDatah.getReadableDatabase();
		Cursor mCursorForSelect = null;
		if (TextOrImage.equalsIgnoreCase("Text"))
			mCursorForSelect = mDatab.rawQuery(
					"SELECT Type FROM CabinetInfoTable where Type='Text';",
					null);
		else if (TextOrImage.equalsIgnoreCase("Image"))
			mCursorForSelect = mDatab.rawQuery(
					"SELECT Type FROM CabinetInfoTable where Type='Image' ;",
					null);
		return mCursorForSelect;
	}

	public void insertExtractedImage(int mTime, byte[] image, String type2) {
		mDatab = mDatah.getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put(IMAGEDATA, image);
		cv.put(DATEFIELD, mTime);
		cv.put(ISCHECHKED, 0);
		cv.put(TYPE, "Image");
		mDatab.insert(CABINET_TABLE, null, cv);
		cv.clear();

	}

	public void bookOpened(String mBookPath, String BookStatus) {
		mDatab = mDatah.getWritableDatabase();
		if (BookStatus.equalsIgnoreCase("BookOpen")) {
			mDatab.execSQL("UPDATE " + DATABASE_TABLE
					+ " set BookOpen=1 WHERE FileName='" + mBookPath + "'");
		}
		if (BookStatus.equalsIgnoreCase("BookClosed")) {
			mDatab.execSQL("UPDATE " + DATABASE_TABLE
					+ " set BookOpen=0 WHERE FileName='" + mBookPath + "'");
		}

	}

}
