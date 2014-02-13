package com.example.babydays;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
		//set current date on dayActivity textview field
		//set default day activities according to current date
		setCurrentDateAndActivities();
		
		
	}

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
		
		//search recodes in sqlite db march date
		List<BabyActivity> activitiesByDate = dbHelper.getBabyActivityByDate(formattedDate);
		for(int i = 0; i < activitiesByDate.size(); i++){
			dayActivity.append(activitiesByDate.get(i).getTime().toString() + "\t\t");
			dayActivity.append(activitiesByDate.get(i).getType().toString() + "\t\t");
			dayActivity.append(activitiesByDate.get(i).getInfo().toString());
			dayActivity.append("\n");
		}
	}
	
	public void DecreaseMonth(View v){
		month--;
		
		// set current date into textview
		showSetDate.setText(new StringBuilder()
			// Month is 0 based, just add 1
			.append(month + 1).append("-").append(day).append("-")
			.append(year).append(" "));
	}
	
	public void DecreaseDay(View v){
		day--;
		
		// set current date into textview
		showSetDate.setText(new StringBuilder()
			// Month is 0 based, just add 1
			.append(month + 1).append("-").append(day).append("-")
			.append(year).append(" "));
	}
	
	public void IncreaseMonth(View v){
		month++;
		
		// set current date into textview
		showSetDate.setText(new StringBuilder()
			// Month is 0 based, just add 1
			.append(month + 1).append("-").append(day).append("-")
			.append(year).append(" "));
	}
	
	public void IncreaseDay(View v){
		day++;
		
		// set current date into textview
		showSetDate.setText(new StringBuilder()
			// Month is 0 based, just add 1
			.append(month + 1).append("-").append(day).append("-")
			.append(year).append(" "));
	}

}
