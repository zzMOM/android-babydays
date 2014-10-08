package com.example.babydays;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.example.babydays.FragmentDatePicker.DatePickerDialogListener;
import com.example.babydays.FragmentDiaperDialog.DiaperDialogListener;
import com.example.babydays.FragmentFeedDialog.FeedDialogListener;
import com.example.babydays.FragmentHeightDialog.HeightDialogListener;
import com.example.babydays.FragmentMilestoneDialog.MilestoneDialogListener;
import com.example.babydays.FragmentSleepDialog.SleepDialogListener;
import com.example.babydays.FragmentWeightDialog.WeightDialogListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemLongClickListener;

public class DayActivities extends FragmentActivity implements DatePickerDialogListener
															 , FeedDialogListener
															 , SleepDialogListener
															 , DiaperDialogListener
															 , MilestoneDialogListener
															 , HeightDialogListener
															 , WeightDialogListener{
	private MySQLiteHelper dbHelper;//database
	//calendar
	private Calendar c;
	private int year;
	private int month;
	private int day;
	//datepicker dialog
	static final int DATE_DIALOG_ID = 999;
	private ArrayList<String> activityList;
	private HashMap<Integer, BabyActivity> map;
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
				addSubtractDay(1);
		    }
			
			@Override
			public void onSwipeRight() {
				// TODO Auto-generated method stub
				addSubtractDay(-1);
			}
		});
		
		setDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String date = showSetDate.getText().toString();
				String[] d = date.split("-");
				FragmentDatePicker frag = FragmentDatePicker.newInstance(Integer.parseInt(d[2])
						 												, Integer.parseInt(d[0]) - 1
						 												, Integer.parseInt(d[1]), false);
				frag.show(getSupportFragmentManager(), "DatePicker");
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
	           		   List<BabyActivity> activitiesByDate;
	           		   if(which == 0){
	           			   	showActivityByDate();
	           		   } else {
	           			   	showActivityByDateAndAttr(attr);
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
		String type = map.get(curPosition).getType();
		if(type.equals("FeedMilk")){
			showFeedDialog();
		} else if(type.equals("Nap")){
			showSleepDialog();
		} else if(type.equals("Diaper")){
			showDiaperDialog();
		} else if(type.equals("Milestone")){
			showMilestonesDialog();
		} else if(type.equals("Height")){
			showHeightDialog();
		} else if(type.equals("Weight")){
			showWeightDialog();
		}
	}
	
	private void deleteRecord(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle("Delete activity!")
				.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						int id = map.get(curPosition).getId();
						dbHelper.deleteBabyActivityByID(Integer.toString(id));
						showActivityByDate();
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
		
		showActivityByDate();
	}
	
	private void showActivityByDate(){
		String date = showSetDate.getText().toString();
		//show summary
		showActivitySummary();
		
		//show detail activities list by date
		//search recodes in sqlite db march date
		List<BabyActivity> activitiesByDate = dbHelper.getBabyActivityByDate(date);
		//show detail in listview
		showActivityDetail(activitiesByDate);
	}
	
	private void showActivityByDateAndAttr(String attr){
		String date = showSetDate.getText().toString();
		//show summary
		showActivitySummary();
		
		//show detail activities list by date
		//search recodes in sqlite db march date
		List<BabyActivity> activitiesByDateAttr = dbHelper.getBabyActivityByDateAttr(date, attr);
		//show detail in listview
		showActivityDetail(activitiesByDateAttr);
	}
	
	private void showActivitySummary(){
		String date = showSetDate.getText().toString();
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
		map = new HashMap<Integer, BabyActivity>();
		
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
			map.put(i, activitiesByDate.get(i));
		}
		//array adpater changed
		adapter.notifyDataSetChanged();
	}

	
	private void addSubtractDay(int daysToAdd){
		String date = showSetDate.getText().toString();
		String[] d = date.split("-");
		c.set(Calendar.YEAR, Integer.parseInt(d[2]));
		c.set(Calendar.MONTH, Integer.parseInt(d[0]) - 1);
		c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(d[1]));
		c.add(Calendar.DAY_OF_MONTH, daysToAdd);
		
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		showSetDate.setText(df.format(c.getTime()));
		showActivityByDate();
	}

	@Override
	public void onFinishSetDate(String s) {
		// TODO Auto-generated method stub
		showSetDate.setText(s);
		showActivityByDate();
	}

	//Feed Dialog
		private void showFeedDialog(){
			BabyActivity act = map.get(curPosition);
			DialogFragment frag = FragmentFeedDialog.newInstance(1, act.getDate(), act.getTime(), act.getInfo());
			frag.show(getSupportFragmentManager(), "FeedDialog");
		}
		
		//FeedDialogListener, insert record into database after feed dialog click OK
		@Override
		public void onFinishSetFeed(String date, String time, String type,
				String info) {
			// TODO Auto-generated method stub
			updateCurrentActivity(date, time, type, info);
			showActivityByDate();
		}
		

		//Diaper Dialog
		private void showDiaperDialog(){
			BabyActivity act = map.get(curPosition);
			DialogFragment frag = FragmentDiaperDialog.newInstance(1, act.getDate(), act.getTime(), act.getInfo());
			frag.show(getSupportFragmentManager(), "DiaperDialog");
		}
		
		//DiaperDialogListener, insert record into database after diaper dialog click OK
		@Override
		public void onFinishSetDiaper(String date, String time, String type,
				String info) {
			// TODO Auto-generated method stub
			updateCurrentActivity(date, time, type, info);
			showActivityByDate();
		}
		
		//Milestone Dialog
		private void showMilestonesDialog(){
			BabyActivity act = map.get(curPosition);
			DialogFragment frag = FragmentMilestoneDialog.newInstance(1, act.getDate(), act.getTime(), act.getInfo());
			frag.show(getSupportFragmentManager(), "MilestoneDialog");
		}
		
		//MilestoneDialogListener, insert record into database after milestone dialog click OK
		@Override
		public void onFinishSetMilestone(String date, String time, String type,
				String info) {
			// TODO Auto-generated method stub
			updateCurrentActivity(date, time, type, info);
			showActivityByDate();
		}
		
		//sleep dialog
		private void showSleepDialog(){
			BabyActivity act = map.get(curPosition);
			DialogFragment frag = FragmentSleepDialog.newInstance(1, act.getDate(), act.getTime(), act.getInfo(), true, "");
			frag.show(getSupportFragmentManager(), "SleepDialog");
		}

		@Override
		public void onFinishSetSleep(String date, String time, String type,
				String info, boolean isStart, String start) {
			updateCurrentActivity(date, time, type, info);
			showActivityByDate();
		}
		
		//Height dialog
		private void showHeightDialog(){
			BabyActivity act = map.get(curPosition);
			DialogFragment frag = FragmentHeightDialog.newInstance(act.getDate(), act.getInfo());
			frag.show(getSupportFragmentManager(), "HeightDialog");
		}

		@Override
		public void onFinishSetHeight(String date, String time, String type, String info) {
			updateCurrentActivity(date, time, type, info);
			showActivityByDate();
		}
		
		//Weight dialog
		private void showWeightDialog(){
			BabyActivity act = map.get(curPosition);
			DialogFragment frag = FragmentWeightDialog.newInstance(act.getDate(), act.getInfo());
			frag.show(getSupportFragmentManager(), "WeightDialog");
		}

		@Override
		public void onFinishSetWeight(String date, String time, String type, String info) {
			updateCurrentActivity(date, time, type, info);
			showActivityByDate();
		}

		
		private void  updateCurrentActivity(String date, String time, String type, String info){
			BabyActivity act = map.get(curPosition);
			act.setDate(date);
			act.setType(type);
			act.setTime(time);
			act.setInfo(info);
			dbHelper.updateBabyActivity(act);
		}
		
	
}
