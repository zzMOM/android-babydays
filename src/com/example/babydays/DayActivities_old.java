package com.example.babydays;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.*;
import android.widget.FrameLayout.LayoutParams;

/**
 * Show the day activities by date
 * choose date by DatePicker; choose date by increase or decrease day or month
 * filter day activites by type
 * @author weiwu
 *
 */
public class DayActivities_old extends Activity {
	private MySQLiteHelper dbHelper;

	private int year;
	private int month;
	private int day;
 
	static final int DATE_DIALOG_ID = 999;

	private Calendar c;

	//private DatePicker datePicker;	//DatePicker
	//private ScrollView scrollView;
	private TextView dayActivity, dayActivitySummary;
	private TextView showSetDate;	//show DatePicker or <> set date
	private Button setDate;		//show DatePicker dialog
	//private Button preMonth;	//decrease month button
	private Button preDay;		//decrease day button
	private Button nextDay;		//increase day button
	//private Button nextMonth;	//increase month button
	private Button recordFilter;//filter records button
	
	private int width;//text length
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.day_activities_old);
		
		//create database helper
		dbHelper = new MySQLiteHelper(this);
		
		//action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		dayActivitySummary = (TextView)findViewById(R.id.dayActivitySummary);
		dayActivity = (TextView)findViewById(R.id.dayActivity);
		showSetDate = (TextView)findViewById(R.id.showSetDate);
		//scrollView = (ScrollView)findViewById(R.id.scrollView);
		//preMonth = (Button)findViewById(R.id.preMonth);
		preDay = (Button)findViewById(R.id.preDay);
		nextDay = (Button)findViewById(R.id.nextDay);
		//nextMonth = (Button)findViewById(R.id.nextMonth);
		dayActivity.setMovementMethod(new ScrollingMovementMethod());
		/*dayActivity.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if(dayActivity.getLayoutParams().height == LayoutParams.MATCH_PARENT){
					dayActivity.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
				} else {
					dayActivity.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));
				}
				return false;
			}
		});*/
		dayActivity.setOnTouchListener(new OnSwipeTouchListener(this){
			@Override
		    public void onSwipeLeft() {
		        // Whatever
				IncreaseDay(nextDay);
		    }
			
			@Override
			public void onSwipeRight() {
				// TODO Auto-generated method stub
				DecreaseDay(preDay);
			}
		});
		
		
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
	
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		
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
		//showDate.setText(formattedDate);
		getActionBar().setTitle("Today: " + formattedDate);
		
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
		//Log.d("search total", date);
		dayActivitySummary.setText("");
		for(int i = 0; i < totalMilkDiaper.size(); i++){
			dayActivitySummary.append(totalMilkDiaper.get(i).toString() + "\n");
		}
		
		dayActivity.setText("");
		dayActivity.append("ID"  + "\t\t");
		dayActivity.append("Time" + "\t\t\t\t\t\t\t");
		dayActivity.append("Type" + "\t\t\t\t\t\t\t");
		dayActivity.append("Info");
		dayActivity.append("\n\n");
		
		
		//search recodes in sqlite db march date
		List<BabyActivity> activitiesByDate = dbHelper.getBabyActivityByDate(date);
		//Log.d("search date", date);
		
		//time is 24hour format, show with 12hour format
		//date transfer 24hours to 12hours
		String time12 = "";
		SimpleDateFormat h_mm_a = new SimpleDateFormat("h:mma");
		SimpleDateFormat hh_mm = new SimpleDateFormat("HH:mm");
		
		for(int i = 0; i < activitiesByDate.size(); i++){
			//get date from datebase
			String time24 = activitiesByDate.get(i).getTime().toString();
			try {
			    Date t = hh_mm.parse(time24);
			    time12 = h_mm_a.format(t).toString();
			} catch (Exception e) {
			    e.printStackTrace();
			}
			dayActivity.append(activitiesByDate.get(i).getId()  + "\t\t");
			dayActivity.append(time12 + "\t\t");
			StringBuffer b = new StringBuffer(activitiesByDate.get(i).getType().toString());
			b.append("          ");
			b.setLength(10);
			dayActivity.append(b.toString() + "\t\t\t");
			dayActivity.append(activitiesByDate.get(i).getInfo().toString());
			dayActivity.append("\n\n");
		}
	}

	
	public void creatFilterDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(DayActivities_old.this);
	    
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
	           		   
		           		//time is 24hour format, show with 12hour format
		           		//date transfer 24hours to 12hours
		           		String time12 = "";
		           		SimpleDateFormat h_mm_a   = new SimpleDateFormat("h:mma");
		           		SimpleDateFormat hh_mm = new SimpleDateFormat("HH:mm");
	           		
		           	   for(int i = 0; i < activitiesByDate.size(); i++){
			           		//get date from datebase
			       			String time24 = activitiesByDate.get(i).getTime().toString();
			       			try {
			       			    Date t = hh_mm.parse(time24);
			       			    time12 = h_mm_a.format(t).toString();
			       			} catch (Exception e) {
			       			    e.printStackTrace();
			       			}
			       			dayActivity.append(activitiesByDate.get(i).getId()  + "\t\t");
			    			dayActivity.append(time12 + "\t\t\t\t\t");
			    			dayActivity.append(activitiesByDate.get(i).getType().toString() + "\t\t\t\t\t\t");
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
	
	public void manageRecord(View v){
		Intent intent = new Intent(DayActivities_old.this, ManageRecords.class);
		startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		Intent intent = new Intent(this, MainMenuCard.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case android.R.id.home:
			// app icon in action bar clicked; go home
            Intent intent = new Intent(this, MainMenuCard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
