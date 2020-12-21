
/*this code for showing the listview from database*/
package com.sdg.organizer;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.sdg.DisplayManager.Display_Manager;
import com.sdg.EPUBReader.DataBaseClass;
import com.sdg.EPUBReader.EpubReader;
import com.sdg.EPUBReader.MyCabinet;
import com.android.QuikE.R;

public class NotesListScroll extends ListActivity implements
		ListView.OnScrollListener {

	// Cursor for extracting data from Database
	private Cursor mCursor;

	// Array holds the notes selected in a category
	private int mCurrentDisplayedNotes[];

	// boolean that states whether the flow is coming from cabinet
	boolean isComingFromCabinet = false;

	// Object of database class
	private DataBaseClass mDatabase;

	// this stores the id of the selected category
	int mSelectedCategoryId;

	// Creating the Simple Cursor Adapter for scroll list
	SimpleCursorAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		isComingFromCabinet = false;
		
		Organizer.smListViewOpen = true;
		
		mDatabase = new DataBaseClass(this);

		// Receiving the data from the Organizer
		Bundle mListCategoryBundle = NotesListScroll.this.getIntent().getExtras();
		mSelectedCategoryId = 1;
		if (mListCategoryBundle != null) {

			if (mListCategoryBundle.containsKey("isComingFromCabinet_key")) {
				isComingFromCabinet = true;
			}
			mSelectedCategoryId = mListCategoryBundle
					.getInt("selectedCategoryO_key");

		}
		
		
		// calling populateNoteInList that populates the note names in the list view
		populateNoteInList();

		// Creating a new Simple Cursor adapter and populating table data into
		// it.
		mAdapter = new SimpleCursorAdapter(this, R.layout.listscroll, mCursor,
				new String[] { "NoteName" }, new int[] { R.id.notename });
		mAdapter.notifyDataSetChanged();
		this.setListAdapter(mAdapter);

		getListView().setOnScrollListener(this); 
		ListView mListView = getListView();
		mListView.setTextFilterEnabled(true);

		// Setting on click listener for the items of List
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				
				
				// Condition for checking whether the note is to be deleted 
				if (Organizer.smDeleteNote == true) {

					mDatabase.deleteNote(mCurrentDisplayedNotes[position]);

					// calling populateNoteInList that populates the note names in the list view after deleting note 
					populateNoteInList();

					mAdapter = new SimpleCursorAdapter(NotesListScroll.this,
							R.layout.listscroll, mCursor,
							new String[] { "NoteName" },
							new int[] { R.id.notename });
					NotesListScroll.this.setListAdapter(mAdapter);

					Organizer.smDeleteNote = false;

				} else {

					// checking whether the flow is coming from cabinet 
					if (isComingFromCabinet == true) {

						// Book -> Cabinet -> Organizer -> ListView -> Cabinet

						// intent for calling back the cabinet class
						// with new note number created
						Intent mSelectedNoteIdIntent = new Intent(
								NotesListScroll.this, MyCabinet.class);
						Bundle mSelectedNoteIdBundle = new Bundle();
						mSelectedNoteIdIntent.putExtra("selectedNoteId_key",
								mCurrentDisplayedNotes[position]);
						Display_Manager.mCabinetOpen = 1;
						Organizer.smForCabinetChangeID = mCurrentDisplayedNotes[position];
						Log.e("Organizer.mForCabinetChangeID=", ""
								+ Organizer.smForCabinetChangeID);

						mSelectedNoteIdIntent.putExtras(mSelectedNoteIdBundle);

						// sending the note id back to the cabinet
						// to display in cabbinet area
						setResult(1, mSelectedNoteIdIntent);

						NotesListScroll.this.finish();


					} else {

						// Cursor for extracting noteid , note name and note
						// category from table

						// Sending the selected note id to NoteOrganizer
						Intent mGetListNoteIntent = new Intent(NotesListScroll.this, NoteOrganizer.class);
						Bundle mGetListNoteBundle = new Bundle();
						mGetListNoteBundle.putInt("selectedNoteId_key",
								mCurrentDisplayedNotes[position]);
						mGetListNoteIntent.putExtras(mGetListNoteBundle);
						startActivity(mGetListNoteIntent);

					}

				}
			}

		});

	}

	// This method gets the note name from the data base and populates them into the listview
	private void populateNoteInList() {
		// TODO Auto-generated method stub
		int mCount = 0;

		// since 1 category is always 1 so we will select all notes from the
		// database
		if (mSelectedCategoryId == 1) {
			mCursor = mDatabase.returnAllFromOrganizeHome();

			// setting the flag for not refreshing the category block while
			// going back to organizer
			Organizer.smRefreshCategories = false;

		} else {

			mCursor = mDatabase.selectAllFromOrganizerOnCategory(mSelectedCategoryId);

			// setting the flag for not refreshing the category block while
			// going back to organizer
			Organizer.smRefreshCategories = false;
		}
		mCursor.moveToFirst();

		// Counting the number of antries in the table
		mCount = mCursor.getCount();

		// This array holds the fetched notes for a particular category
		mCurrentDisplayedNotes = new int[mCount];
		for (int i = 0; i < mCount; i++) {
			mCurrentDisplayedNotes[i] = mCursor.getInt(0);
			mCursor.moveToNext();
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_FLING:
			mCursor.requery();

			break;
		}
 
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// closing the cursor
		if (mCursor != null)
			mCursor.close();

	}
	/* This code is to return in home */
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		switch (KeyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			startActivity(new Intent(NotesListScroll.this, EpubReader.class));
			return true;
		}
		return false;

	}
}
