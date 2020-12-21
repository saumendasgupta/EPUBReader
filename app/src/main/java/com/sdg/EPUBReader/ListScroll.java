
/*this code for showing the listview from database*/
package com.sdg.EPUBReader;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.sdg.DisplayManager.Display_Manager;
import com.android.QuikE.R;

public class ListScroll extends ListActivity  {
	/* this Cursor is use to connect with the database */
	Cursor mCursor;
	/* this variable will calculate the no of data in the databases */
	static int mIndex;
	/* this String is to store the book Path from the Database */
	static String bookdata[];
	/* this String is to store the book title from the Database */
	static String mTitleData[];
	/* these Variable is used for Coloum index of BookInfoTable */
	private int BOOK_PATH = 1;
	/* this variable is to indiacte coloum no book's author name */
	private int BOOK_AUTHOR = 2;
	/* this variable is to indicate title of book's name */
	private int BOOK_TITLE = 3;
	/* this macro will use to extract the type of epub */
	private int TYPE = 4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* First Create the object of the database class and access the Database */
		final DataBaseClass mDb = new DataBaseClass(this);
		/* get a handler */
		mDb.mDatab = mDb.mDatah.getReadableDatabase();
		/* get a cursor */
		mCursor = mDb.returnCursor();
		/* move the cursor to the first position */
		mCursor.moveToFirst();
		/* get the no of element in the database */
		mIndex = mCursor.getCount();
		/* initialize the book data String */
		bookdata = new String[mIndex];
		/* initialize the book title String */
		mTitleData = new String[mIndex];
		/* store the name of the book and the title */
		for (int i = 0; i < mIndex; i++) {
			/* store the sd card book path */
			bookdata[i] = mCursor.getString(2);
			/* store the title */
			mTitleData[i] = mCursor.getString(1);
			/* move the cursor to the next position */
			mCursor.moveToNext();
		}
		/* close the cursor */
		mCursor.close();
		/*
		 * this adapter will get the data from the database and show in the
		 * listview manner
		 */
		this.setListAdapter(new AdapterForLibMain(this));
		/* get the list view */
		ListView mListView = getListView();
		mListView.setTextFilterEnabled(true);
		/* on click of the listview item it will open the specific book */
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				/* Extract the fiel path according to the click on list */
				Cursor mExtractFilePathCursor;
				/* get a cursor */
				mExtractFilePathCursor = mDb.returnCursorForSpecificPos();
				/* move the cursor to the specific position */
				mExtractFilePathCursor.moveToPosition(position);
				/* store the sd card book path */
				String mFilePath = mExtractFilePathCursor.getString(BOOK_PATH);
				/* store the book's author name */
				String mExtractedAuthor = mExtractFilePathCursor
						.getString(BOOK_AUTHOR);
				/* store the book's title */
				String mExtractedTitle = mExtractFilePathCursor
						.getString(BOOK_TITLE);
				/* get the type of epub,Book ao magazine */
				String mBookOrMagazine = mExtractFilePathCursor.getString(TYPE);
				/* close the cursor */
				mExtractFilePathCursor.close();

				/* create the intent and call the bookview page activity */
				Intent intent = new Intent(ListScroll.this,
						Display_Manager.class);
				/* create a bundle */
				Bundle mBundle = new Bundle();
				/* put the book path as extra in the bundle */
				mBundle.putString("BookPath", mFilePath);
				intent.putExtras(mBundle);
				/* insert into Resect table about this book */
				/* First check if it is book or magazine */
				/* if type is book insert in recent book table */
				if (mBookOrMagazine.equalsIgnoreCase("Book")) {
					mDb.recentBookOpendInsert(mFilePath, mExtractedAuthor,
							mExtractedTitle,
							DataBaseClass.RECENT_BOOK_DATABASE_TABLE);
					/* insert into active window table also */
					mDb
							.insertInActiveWindow(mExtractedTitle, mFilePath,
									"Book");

				}
				/* or if type is magazine insert in recent magazine table */
				else if (mBookOrMagazine.equalsIgnoreCase("Magazine")) {
					mDb.recentBookOpendInsert(mFilePath, mExtractedAuthor,
							mExtractedTitle,
							DataBaseClass.RECENT_MAGAZINE_DATABASE_TABLE);
					/* insert into active window table also */
					mDb.insertInActiveWindow(mExtractedTitle, mFilePath,
							"Magazine");

				}

				/* finish this activity first */
				ListScroll.this.finish();

				/* start book reading activity */
				startActivity(intent);

			}
		});
	}

	/*
	 * this adapter will collect all the books name from the database and show
	 * it in the listview
	 */
	private static class AdapterForLibMain extends BaseAdapter {
		/* get a generaic layout */
		private LayoutInflater mInflater;
		/* this bitmap is to show the images for books */
		private Bitmap mIcon[];
		/*
		 * this array will store some predefined images from where we will
		 * collect some random images
		 */
		
		private Integer[] mThumbIds = { R.drawable.thumbnailviewoption03 };

		public AdapterForLibMain(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			Bitmap mIcon1[] = new Bitmap[mIndex];
			int random_number;
			/* store the images of the books and select some random number */
			for (int j = 0; j < mIndex; j++) {
				/* generate random number */
				random_number = (int) (Math.random() * mThumbIds.length);
				/* store random images */
				mIcon1[j] = BitmapFactory.decodeResource(
						context.getResources(), mThumbIds[random_number]);
			}

			mIcon = mIcon1;
		}

		/* retun the no of element in database */
		public int getCount() {
			return mIndex;

		}

		/* return clicked position */
		public Object getItem(int position) {
			return position;
		}

		/* return clicked element id* */
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.imageanddataforlist,
						null);
				/* create a object of the viewholder class */
				holder = new ViewHolder();
				/* initialize the textview */
				holder.text = (TextView) convertView.findViewById(R.id.text);
				/* initialize the text vieew for the title */
				holder.title = (TextView) convertView
						.findViewById(R.id.listtitletextbox);
				holder.text.setPadding(0, 15, 10, 5);
				/* set the color */
				holder.text.setTextColor(Color.BLACK);
				/* set the text size */
				holder.text.setTextSize(18);
				/* set the text padding */
				holder.title.setPadding(0, 15, 10, 5);
				/* set the text color* */
				holder.title.setTextColor(Color.BLACK);
				/* set the text size */
				holder.title.setTextSize(14);

				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.icon.setPadding(15, 0, 0, 0);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			/* set the icon appropriately */
			holder.icon.setImageBitmap(mIcon[position]);
			/* set the text appropriately */
			holder.text.setText(bookdata[position]);
			/* set the title appropriately */
			holder.title.setText(mTitleData[position]);

			return convertView;
		}

		/* this class will be helpful to show two text and one image in listview */
		static class ViewHolder {
			TextView text;
			TextView title;
			ImageView icon;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		/* close the cursor */
		mCursor.close();
	}


}
