package com.example.babydays;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class Profile extends Activity{
	private ImageView profilePhoto;
	private TextView babyName, birthDate, birthTime, birthHeight, birthWeight;
	private SharedPreferences mPrefsInfo;
	private static final String BABY_INFO = " , , , , ";
	Editor infoEditor;
	
	static final int DATE_DIALOG_ID = 100;
	static final int TIME_DIALOG_ID = 999;
	private Calendar c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		profilePhoto = (ImageView) findViewById(R.id.profilePhoto);
		babyName = (TextView) findViewById(R.id.babyName);
		birthDate = (TextView) findViewById(R.id.birthDate);
		birthTime = (TextView) findViewById(R.id.birthTime);
		birthHeight = (TextView) findViewById(R.id.birthHeight);
		birthWeight = (TextView) findViewById(R.id.birthWeight);
		
		mPrefsInfo = getSharedPreferences(BABY_INFO, 0);
		String str = mPrefsInfo.getString(BABY_INFO, " , , , , ");
		Log.e("BABY_INFO", str);
		String[] info = str.split(",");
		
		babyName.setText(info[0]);
		birthDate.setText(info[1]);
		birthTime.setText(info[2]);
		birthHeight.setText(info[3]);
		birthWeight.setText(info[4]);
		
		babyName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setName();
			}
		});
		
		
		birthDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
		});
		birthTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(TIME_DIALOG_ID);
			}
		});
		
		birthHeight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setHeight();
			}
		});
		
		birthWeight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setWeight();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	public void pickProfilePhoto(View view){
		Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePicture, 0);//zero can be replaced with any action code
		Intent pickPhoto = new Intent(Intent.ACTION_PICK,
		           android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch(requestCode) {
		case 0:
		    if(resultCode == RESULT_OK){  
		        Uri selectedImage = data.getData();
		        profilePhoto.setImageURI(selectedImage);
		    }
		    break; 
		case 1:
		    if(resultCode == RESULT_OK){  
		        Uri selectedImage = data.getData();
		        profilePhoto.setImageURI(selectedImage);
		    }
		    break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
        c = Calendar.getInstance();
		switch (id) {
		case DATE_DIALOG_ID:
			int year = c.get(Calendar.YEAR);
	        int month = c.get(Calendar.MONTH);
	        int day = c.get(Calendar.DAY_OF_MONTH);
	        //Log.e("year month day", year + " " + month + " " + day);
		    // set date picker as current date
		    return new DatePickerDialog(this, datePickerListener, 
                         year, month, day);
		case TIME_DIALOG_ID:
			//use current date as default date show in the DatePicker
			int hour = c.get(Calendar.HOUR);
			int min = c.get(Calendar.MINUTE);
			return new TimePickerDialog(this, timePickerListener, hour, min, false);
		}
		return null;
	}
	
	private DatePickerDialog.OnDateSetListener datePickerListener 
    		= new DatePickerDialog.OnDateSetListener() {
    	
				@Override
				public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
					// set selected date into textview
					String m, d;
					m = selectedMonth + 1 < 10? "0" + (selectedMonth + 1) : (selectedMonth + 1) + "";
					d = selectedDay < 10? "0" + selectedDay : selectedDay + "";
					String newStr = new StringBuilder().append(m)
							   .append("-").append(d).append("-").append(selectedYear)
							   .toString();
					
					String s = mPrefsInfo.getString(BABY_INFO, "");
					
					//update mPrefsInfo
					infoEditor = mPrefsInfo.edit();
					infoEditor.putString(BABY_INFO, updateSharedPref(s, newStr, 1));
					infoEditor.commit();
					
					birthDate.setText(newStr);
				}
			};
			
			
	
	private TimePickerDialog.OnTimeSetListener timePickerListener
			= new TimePickerDialog.OnTimeSetListener() {
				
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					// set selected time to textview
					String timePick24 = new StringBuilder().append(hourOfDay).append(":").append(minute).toString();
					String a_p = "";
					if(hourOfDay > 12){
						hourOfDay -= 12;
						a_p = "PM";
					} else {
						a_p = "AM";
					}
					String h, m;
					h = hourOfDay < 10? "0" + hourOfDay : hourOfDay + "";
					m = minute < 10? "0" + minute : minute + "";
					String newStr = new StringBuilder().append(h).append(":").append(m).append(a_p).toString();
					
					String s = mPrefsInfo.getString(BABY_INFO, "");
					
					//update mPrefsInfo
					infoEditor = mPrefsInfo.edit();
					infoEditor.putString(BABY_INFO, updateSharedPref(s, newStr, 2));
					infoEditor.commit();
					
					birthTime.setText(newStr);
				}
			};
	
	private void setName(){
		AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
		builder.setTitle("Edit baby name");
		final EditText inputText = new EditText(this);
		inputText.setEms(15);
		builder.setView(inputText);
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String s = mPrefsInfo.getString(BABY_INFO, "");
				String newStr = inputText.getText().toString();
				
				//update mPrefsInfo
				infoEditor = mPrefsInfo.edit();
				infoEditor.putString(BABY_INFO, updateSharedPref(s, newStr, 0));
				infoEditor.commit();
				
				babyName.setText(newStr);
			}
		});
		builder.show();
	}
	
	private void setHeight(){
		AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
		builder.setTitle("Edit baby birth height");
		final EditText inputText = new EditText(this);
		inputText.setEms(15);
		builder.setView(inputText);
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String s = mPrefsInfo.getString(BABY_INFO, "");
				String newStr = inputText.getText().toString();
				
				//update mPrefsInfo
				infoEditor = mPrefsInfo.edit();
				infoEditor.putString(BABY_INFO, updateSharedPref(s, newStr, 3));
				infoEditor.commit();
				
				birthHeight.setText(newStr);
			}
		});
		builder.show();
	}
	
	private void setWeight(){
		AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
		builder.setTitle("Edit baby birth weight");
		final EditText inputText = new EditText(this);
		inputText.setEms(15);
		builder.setView(inputText);
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String s = mPrefsInfo.getString(BABY_INFO, "");
				String newStr = inputText.getText().toString();
				
				//update mPrefsInfo
				infoEditor = mPrefsInfo.edit();
				infoEditor.putString(BABY_INFO, updateSharedPref(s, newStr, 4));
				infoEditor.commit();
				
				birthWeight.setText(newStr);
			}
		});
		builder.show();
	}
	
	private String updateSharedPref(String s, String newStr, int index){
		String[] arrays = s.split(",");
		arrays[index] = newStr;
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < arrays.length; i++){
			if(i != 0){
				b.append(",");
			}
			b.append(" ").append(arrays[i]).append(" ");
		}
		return b.toString();
	}

}
