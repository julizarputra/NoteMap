package com.lutka.notemap;

import java.io.IOException;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class NoteActivity extends SherlockActivity
{
	
	public final static String EXTRA_NOTE_TITLE = "noteTitle",
			EXTRA_NOTE_CONTENT ="noteContent", EXTRA_NOTE_SUBTITLE = "noteSubTitle"; 
	
	public final static float EXTRA_CAMERA_ZOOM = 10;

	Note currentNote;
	
	// this  method is run when the note is created
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{		
		super.onCreate(savedInstanceState);
		currentNote = MapActivity.instance.getOpenedNote();
		setContentView(R.layout.activity_note);
		// Show the Up button in the action bar.
		setupActionBar();
		
		
		Intent intent = getIntent();
		
		if(intent != null)
		{
			Bundle bundle = intent.getExtras();
			
			if(bundle != null)
			{
				String noteTitle = bundle.getString(EXTRA_NOTE_TITLE);
				String noteDescription = bundle.getString(EXTRA_NOTE_CONTENT);
				String noteSubTitle = bundle.getString(EXTRA_NOTE_SUBTITLE);
				setTitle(noteTitle);
				EditText subTitle = (EditText) findViewById(R.id.etSubTitle);
				subTitle.setText(noteSubTitle);
				EditText editText = (EditText) findViewById(R.id.etContent);
				editText.setText(noteDescription);
				
				// to set the cursor at the end of the word
				editText.setSelection(noteDescription.length());
				subTitle.setSelection(subTitle.length());
			}
		}
	}
	
	void updateIcon()
	{
		ActionBar actionBar = getSupportActionBar();
		Drawable drawable = null;
		try 
		{
			drawable = currentNote.getPinDrawable(this);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(drawable == null) actionBar.setIcon(R.drawable.ic_launcher);
		else actionBar.setIcon(drawable);
	}
	
	void showPinDialog()
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.pin_selection_dialog, null);
		GridView gridIcons = (GridView) dialogView.findViewById(R.id.gridIcons);
		gridIcons.setAdapter(new PinAdapter(this, Note.pinIds));
		
		AlertDialog.Builder builder = new Builder(this);
		builder.setView(dialogView).setTitle(R.string.change_note_pin);		
		builder.setView(dialogView).setNegativeButton(android.R.string.cancel, null);
		final Dialog dialog = builder.create();
		
		gridIcons.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) 
			{
				Integer pinName = (Integer) adapterView.getItemAtPosition(position);
				currentNote.setPin(pinName);
				updateIcon();
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	private void setupActionBar()
	{		
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);	
			updateIcon();
	}
	
	//menu z sherlock
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getSupportMenuInflater().inflate(R.menu.note, menu);
		return true;
	}
	

	// this method is used for menu; in this case when icon on a top left is pressed - 
	//what should it happen	 ; menu z sherlock
	@Override  
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				onBackPressed();
				break;
				
			case R.id.saveNote: // save
				saveNote(); 
				finish(); 
				break;
				
			case R.id.undoChanges: // undo
				setResult(RESULT_CANCELED);
				finish(); 
				break;
				
			case R.id.changePin: // change pin
				showPinDialog(); 
				break;
				
			case R.id.deleteNote: 
				deleteNoteWindow();				
				break;	
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void deleteNoteWindow()
	{		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.delete_dialog_title);
		alert.setMessage(R.string.delete_note_dialog);

		alert.setPositiveButton(android.R.string.cancel,null); 
		alert.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() 
		{
		    public void onClick(DialogInterface dialog, int whichButton) 
		    {
		    	Intent intent = new Intent();
				intent.putExtra(EXTRA_NOTE_TITLE, "");
				intent.putExtra(EXTRA_NOTE_CONTENT, "");
				intent.putExtra(EXTRA_NOTE_SUBTITLE, "");
				setResult(RESULT_OK, intent);
				finish();
		    }
		});
		alert.show();
	}
	
	// what happen when back Button is pressed - the note should be saved
	@Override
	public void onBackPressed()
	{
		saveNote();
		finish();
//		super.onBackPressed();
	}
	
	// the actions taken to save note
	
	/**
	 * Saves note changes as a result, which will be send later to the previous activity
	 * 
	 */
	public void saveNote()
	{
		// intent has a bundle and by intent.putExtra it allows to put values into bundle
		Intent intent = new Intent(); 
		//puts (saves) title into bundle
		intent.putExtra(EXTRA_NOTE_TITLE, getTitle().toString());
		
		EditText editTextSubtitle = (EditText) findViewById(R.id.etSubTitle);
		intent.putExtra(EXTRA_NOTE_SUBTITLE, editTextSubtitle.getText().toString());
		
		// gets the current content of a note
		EditText editText = (EditText) findViewById(R.id.etContent);		
		//puts(saves) content into bundle
		intent.putExtra(EXTRA_NOTE_CONTENT, editText.getText().toString());
		//when I go to new window the first activity has to return something for it in the order to work
		setResult(RESULT_OK, intent);
	}

}
