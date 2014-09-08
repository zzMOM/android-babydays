package com.example.babydays;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.*;
import android.widget.AdapterView.OnItemLongClickListener;

public class DayActivities extends Activity {
	private MySQLiteHelper dbHelper;//database
	//calendar
	private Calendar c;
	private int year;
	private int month;
	private int day;
	//datepicker dialog
	static final int DATE_DIALOG_ID = 999;
	private ArrayList<String> activityList;
	private HashMap<Integer, Integer> map;
	private int curPosition = 0;
	private TimeFormatTransfer tf;
	
	private EditText showSetDate;
	private ImageButton setDate, recordFilter, reset;
	private TextView dayActivitySummary;
	private ListView dayActivityLV;
	private LinearLayout ll2;
	private ArrayAdapter<String> adapter;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_day_activities);
		
		//create database helper
		dbHelper = new MySQLiteHelper(this);
		//time format
		tf = new TimeFormatTransfer();
		
		
		//action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		dayActivitySummary = (TextView)findViewById(R.id.dayActivitySummary);
		showSetDate = (EditText)findViewById(R.id.showSetDate);
		setDate = (ImageButton)findViewById(R.id.setDate);
		recordFilter = (ImageButton)findViewById(R.id.recordFilter);
		reset = (ImageButton)findViewById(R.id.reset);
		dayActivityLV = (ListView)findViewById(R.id.dayActivityList);
		
		//initialize listview null
		activityList = new ArrayList<String>();
		activityList.add("");
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activityList);
		dayActivityLV.setAdapter(adapter);
		dayActivityLV.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				curPosition = position;
				manageRecordDialog();
				return false;
			}
		});
		
		ll2 = (LinearLayout)findViewById(R.id.ll2);
		ll2.setOnTouchListener(new OnSwipeTouchListener(this) {
			
			@Override
		    public void onSwipeLeft() {
		        // Whatever
				increaseDay();
		    }
			
			@Override
			public void onSwipeRight() {
				// TODO Auto-generated method stub
				decreaseDay();
			}
		});
		
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
		
		recordFilter.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				creatFilterDialog();
			}
		});
		
		reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setCurrentDateAndActivities();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.day_activities, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/// TODO Auto-generated method stub
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
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		Intent intent = new Intent(this, MainMenuCard.class);
		startActivity(intent);
	}

	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DATE_DIALOG_ID:
			// set date picker as current date
			//datepickerdialog default value
			String date = showSetDate.getText().toString();
			String[] d = date.split("-");
			month = Integer.parseInt(d[0]) - 1;
			day = Integer.parseInt(d[1]);
			year = Integer.parseInt(d[2]);
			return new DatePickerDialog(this, datePickerListener, 
                         year, month, day);
		}
		return null;
	}
	
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		
		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
			// set selected date into textview
			String m, d;
			m = selectedMonth + 1 < 10? "0" + (selectedMonth + 1) : (selectedMonth + 1) + "";
			d = selectedDay < 10? "0" + selectedDay : selectedDay + "";
			String date = new StringBuilder().append(m)
					   .append("-").append(d).append("-").append(selectedYear).toString();
			showSetDate.setText(date);
			showActivityByDate(date);
		}
	};
	
	public void creatFilterDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(DayActivities.this);
	    
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
	           		
	            	   //search recodes in sqlite db march date and type or date
	            	   String date = showSetDate.getText().toString();
	           		   List<BabyActivity> activitiesByDate;
	           		   if(which == 0){
	           			   	showActivityByDate(date);
	           		   } else {
	           			   	showActivityByDateAndAttr(date, attr);
	           		   }
	           		   
		           	   
	               }
	           });
	       
	    builder.show();
	}
	
	public void manageRecordDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(DayActivities.this);
	           
		builder.setItems(R.array.manageRecordDialogArray, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	                   // The 'which' argument contains the index position
	                   // of the selected item
	            
	           		   if(which == 0){
	           			   	editRecordDialog();
	           		   } else {
	           			   	deleteRecord();
	           		   }
	           		   
		           	   
	               }
	           });
	       
	    builder.show();
	}
	
	private void editRecordDialog(){
		
	}
	
	private void deleteRecord(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle("Delete activity!")
				.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						int id = map.get(curPosition);
						dbHelper.deleteBabyActivityByID(Integer.toString(id));
						showActivityByDate(showSetDate.getText().toString());
					}
				})
				.setNegativeButton("NO", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}); 
				
		builder.show();
	}
	
	
	// display current date
	//set default day activities list by current date
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
		
		showActivityByDate(formattedDate);
	}
	
	private void showActivityByDate(String date){
		//show summary
		showActivitySummary(date);
		
		//show detail activities list by date
		//search recodes in sqlite db march date
		List<BabyActivity> activitiesByDate = dbHelper.getBabyActivityByDate(date);
		//show detail in listview
		showActivityDetail(activitiesByDate);
	}
	
	private void showActivityByDateAndAttr(String date, String attr){
		//show summary
		showActivitySummary(date);
		
		//show detail activities list by date
		//search recodes in sqlite db march date
		List<BabyActivity> activitiesByDateAttr = dbHelper.getBabyActivityByDateAttr(date, attr);
		//show detail in listview
		showActivityDetail(activitiesByDateAttr);
	}
	
	private void showActivitySummary(String date){
		//show total of FeedMilk and Diaper times
		List<String> totalMilkDiaper = dbHelper.getTotalByDate(date);
		//Log.d("search total", date);
		dayActivitySummary.setText("");
		for(int i = 0; i < totalMilkDiaper.size(); i++){
			dayActivitySummary.append(totalMilkDiaper.get(i).toString() + "\n");
		}
	}
	
	
	private void showActivityDetail(List<BabyActivity> activitiesByDate){
		//hashmap for activities' id in database and in activitiesList
		//key-activitiesList ID, value- dateabase ID
		map = new HashMap<Integer, Integer>();
		
		activityList.clear();
		for(int i = 0; i < activitiesByDate.size(); i++){
			//not show activities in type 
			String type = activitiesByDate.get(i).getType().toString();
			if(type.equals("milestones") || type.equals("diary")){
				continue;
			}
			//get date from datebase
			String time24 = activitiesByDate.get(i).getTime().toString();
			String time12 = tf.hour24to12(time24);
			activityList.add(new StringBuffer(time12 + "\t\t\t" + type + "\t\t\t\t"
					+ activitiesByDate.get(i).getInfo().toString()).toString());
			map.put(i, activitiesByDate.get(i).getId());
		}
		//array adpater changed
		adapter.notifyDataSetChanged();
	}

	
	public void decreaseMonth(){
		//month is 0 base, if 0, change to 12, and year decrease 1
		if(month == 0){
			year--;
			month = 11;
		} else {
			month--;
		}
		c.set(year, month, day);
		if(day > c.getActualMaximum(Calendar.DAY_OF_MONTH)){
			day = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		c.set(year, month, day);
		
		showDateChanged();
	}
	
	public void decreaseDay(){
		//if day is 1, month decrease and day to the last day of month
		if(day == 1){
			decreaseMonth();
			c.set(year, month, day);
			day = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		} else {
			day--;
		}
		c.set(year, month, day);
		
		showDateChanged();
	}
	
	public void increaseMonth(){
		if(month == 11){
			year++;
			month = 0;
		} else {
			month++;
		}
		c.set(year, month, day);
		
		if(day > c.getActualMaximum(Calendar.DAY_OF_MONTH)){
			day = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		c.set(year, month, day);
		
		showDateChanged();
	}
	
	public void increaseDay(){
		if(day == c.getActualMaximum(Calendar.DAY_OF_MONTH)){
			increaseMonth();
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
		
		showActivityByDate(buffer.toString());
		
	}
	
}
