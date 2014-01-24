package com.example.babydays;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class Diary extends Activity {
	ImageButton shareButton;
	EditText diaryText;
	Button cancelButton;
	Button okButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary);
		
		diaryText = (EditText) findViewById(R.id.diaryText);
		diaryText.setFocusableInTouchMode(true);
		diaryText.requestFocus();
		
		shareButton = (ImageButton) findViewById(R.id.diaryShare);
		shareButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareIt();
			}
		});
		
		cancelButton = (Button) findViewById(R.id.diaryCancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelDialog();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.diary, menu);
		return true;
	}
	
	public void shareIt(){
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String shareBody = diaryText.getText().toString();
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Diary Today");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
	
	public void cancelDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(Diary.this);
		LayoutInflater inflater = Diary.this.getLayoutInflater();
		builder.setMessage("Do you want to delete all the text in text area? If yes, you can't recover it.");
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				diaryText.setText("");
				diaryText.requestFocus();
			}
		});
		
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		builder.show();
	}

}
