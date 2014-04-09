package com.example.babydays;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class DayActivities extends Activity {
	private MySQLiteHelper dbHelper;
	
	private TextView showDate;
	private DatePicker datePicker;
	private Button setDate;
 
	private int year;
	private int month;
	private int day;
 
	static final int DATE_DIALOG_ID = 999;

	private TextView dayActivity;

	private Calendar c;

	private TextView showSetDate;
	private Button preMonth;
	private Button preDay;
	private Button nextDay;
	private Button nextMonth;
	private Button recordFilter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.day_activities);
		
		//create database helper
		dbHelper = new MySQLiteHelper(this);
		//get all records
		List<BabyActivity> routine = dbHelper.getAllBabyActivity();
		
		dayActivity = (TextView)findViewById(R.id.dayActivity);
		showDate = (TextView) findViewById(R.id.showDate);
		showSetDate = (TextView)findViewById(R.id.showSetDate);
		preMonth = (Button)findViewById(R.id.preMonth);
		preDay = (Button)findViewById(R.id.preDay);
		nextDay = (Button)findViewById(R.id.nextDay);
		nextMonth = (Button)findViewById(R.id.nextMonth);
		
		
		setDate = (Button)findViewById(R.id.setDate);
		setDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		//set current date on dayActivity textview field
		//set default day activities according to current date
		setCurrentDateAndActivities();
		
		recordFilter = (Button)findViewById(R.id.recordFilter);
		recordFilter.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				creatFilterDialog();
			}
		});
	}
	
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DATE_DIALOG_ID:
		   // set date picker as current date
		   return new DatePickerDialog(this, datePickerListener, 
                         year, month, day);
		}
		return null;
	}
	
	private DatePickerDialog.OnDateSetListener datePickerListener= new DatePickerDialog.OnDateSetListener() {
		
		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			year = selectedYear;
			month = monthOfYear;
			day = dayOfMonth;
 
			showDateChanged();
 
		}
	};


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.day_activities, menu);
		return true;
	}
	
	// display current date
	//set default day activities according to current date
	public void setCurrentDateAndActivities() {
		c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		//get current time in sqlite record format
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		String formattedDate = df.format(c.getTime());
		showSetDate.setText(formattedDate);
		showDate.setText(formattedDate);
		//show search by date
		showSearchByDateResult(formattedDate);
	}
	
	public void DecreaseMonth(View v){
		//month is 0 base, if 0, change to 12, and year decrease 1
		if(month == 0){
			year--;
			month = 11;
		} else {
			month--;
		}
		c.set(year, month, day);
		/*in case: month show 2, and day show 30---error*/
		if(day > c.getActualMaximum(Calendar.DAY_OF_MONTH)){
			day = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		c.set(year, month, day);
		
		showDateChanged();
	}
	
	public void DecreaseDay(View v){
		//if day is 1, month decrease and day to the last day of month
		if(day == 1){
			DecreaseMonth(v);
			c.set(year, month, day);
			day = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		} else {
			day--;
		}
		c.set(year, month, day);
		
		showDateChanged();
	}
	
	public void IncreaseMonth(View v){
		if(month == 11){
			year++;
			month = 0;
		} else {
			month++;
		}
		c.set(year, month, day);
		
		/*in case: month show 2, and day show 30*/
		if(day > c.getActualMaximum(Calendar.DAY_OF_MONTH)){
			day = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		c.set(year, month, day);
		
		showDateChanged();
	}
	
	public void IncreaseDay(View v){
		if(day == c.getActualMaximum(Calendar.DAY_OF_MONTH)){
			IncreaseMonth(v);
			day = 1;
		} else {
			day++;
		} 
		c.set(year, month, day);
		
		showDateChanged();
	}
	
	private void showDateChanged(){
		// set selected date into textview
		// Month is 0 based, just add 1
		StringBuffer buffer = new StringBuffer();
		String m = null, d = null;
		int monthOfYear = month + 1;
		if(monthOfYear < 10){
			m = "0" + monthOfYear;
		} else {
			m = "" + monthOfYear;
		}
		if(day < 10){
			d = "0" + day;
		} else {
			d = "" + day;
		}
		buffer.append(m).append("-").append(d).append("-").append(year);
		showSetDate.setText(buffer.toString());
		
		showSearchByDateResult(buffer.toString());
		
	}
	
	private void showSearchByDateResult(String date){
		dayActivity.setText("");
		
		//show total of FeedMilk and Diaper times
		List<String> totalMilkDiaper = dbHelper.getTotalByDate(date);
		Log.d("search total", date);
		for(int i = 0; i < totalMilkDiaper.size(); i++){
			dayActivity.append(totalMilkDiaper.get(i).toString() + "\n");
		}
		dayActivity.append("---------------------------------------------------------\n\n");
		
		
		//search recodes in sqlite db march date
		List<BabyActivity> activitiesByDate = dbHelper.getBabyActivityByDate(date);
		Log.d("search date", date);
		
		for(int i = 0; i < activitiesByDate.size(); i++){
			dayActivity.append(activitiesByDate.get(i).getTime().toString() + "\t\t");
			dayActivity.append(activitiesByDate.get(i).getType().toString() + "\t\t");
			dayActivity.append(activitiesByDate.get(i).getInfo().toString());
			dayActivity.append("\n\n");
		}
	}

	
	public void creatFilterDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(DayActivities.this);
	    // Get the layout inflater
	    LayoutInflater inflater = DayActivities.this.getLayoutInflater();
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setTitle("Pick one filter item!")
	           .setItems(R.array.recordFilter, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	                   // The 'which' argument contains the index position
	                   // of the selected item
	            
	            	   //get string-array items using getResources().getStringArray
	            	   String[] recordItems = getResources().getStringArray(R.array.recordFilter);
	            	   String attr = recordItems[which];
	            	   
	            	   //set the button text to selected item 
	            	   recordFilter.setText(attr);
	            	   
	            	   //get current time in sqlite record format
	           		   SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
	           		   String formattedDate = df.format(c.getTime());
	           		   
	           		   dayActivity.setText("");
	           		
	            	   //search recodes in sqlite db march date and type or date
	           		   List<BabyActivity> activitiesByDate;
	           		   if(which == 0){
	           			   	activitiesByDate = dbHelper.getBabyActivityByDate(formattedDate);
	           		   } else {
	           			   	activitiesByDate = dbHelper.getBabyActivityByDateAttr(formattedDate, attr);
	           		   }
		           	   for(int i = 0; i < activitiesByDate.size(); i++){
		           			dayActivity.append(activitiesByDate.get(i).getTime().toString() + "\t\t");
		           			dayActivity.append(activitiesByDate.get(i).getType().toString() + "\t\t");
		           			dayActivity.append(activitiesByDate.get(i).getInfo().toString());
		           			dayActivity.append("\n\n");
		           	   }
	               }
	           });
	       
	    builder.show();
	}
	
	//reset the result to current date without filter
	public void resetResults(View v){
		setCurrentDateAndActivities();
		recordFilter.setText("Record Filter");
	}
}
