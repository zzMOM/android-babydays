package com.example.babydays;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.babydays.FragmentDiaperDialog.DiaperDialogListener;
import com.example.babydays.FragmentFeedDialog.FeedDialogListener;
import com.example.babydays.FragmentHeightDialog.HeightDialogListener;
import com.example.babydays.FragmentMilestoneDialog.MilestoneDialogListener;
import com.example.babydays.FragmentSleepDialog.SleepDialogListener;
import com.example.babydays.FragmentWeightDialog.WeightDialogListener;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

public class MainMenuCard extends FragmentActivity implements FeedDialogListener
															, DiaperDialogListener
															, MilestoneDialogListener
															, SleepDialogListener
															, HeightDialogListener
															, WeightDialogListener{
	//SharedPreferences to record nap start or not
	private static final String NAP_CLOCK = "napclock";
	private SharedPreferences mPrefsStart;
	private boolean isStart = false;//isStart to record whether nap clock started
	private static final String START_TIME = "starttime";
	private SharedPreferences mPrefsTime;
	//private DateTimePickerClass dateTimePickerClass;
	private TimeFormatTransfer tf;
	
	private GridView gridView;
	private Button viewAct, summaryButton, memoryButton;
	private SimpleDateFormat df;
	private Calendar c;
	private MySQLiteHelper dbHelper;
	private boolean doubleBackToExitPressedOnce = false;
	private int clicktype = 0;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu_card);
		
		//show action bar
		ActionBar actionBar = getActionBar();
		actionBar.show();
		actionBar.setTitle("Baby Days");//change action bar title
		//actionBar.setIcon(icon);
		
		//create database helper
		dbHelper = new MySQLiteHelper(this);
		//time format
		tf = new TimeFormatTransfer();
		
		//date and time format
		c = Calendar.getInstance();
        df = new SimpleDateFormat("MM-dd-yyyy HH:mm");
		        
		// Restore preferences
		mPrefsStart = getSharedPreferences(NAP_CLOCK, 0);
		isStart = mPrefsStart.getBoolean(NAP_CLOCK, false);
		Log.e("default nap clock", Boolean.toString(isStart));
		
		mPrefsTime = getSharedPreferences(START_TIME, 0);
		//Log.e("default start time", mPrefsTime.getString(START_TIME, df.format(c.getTime())));
		
		
		viewAct = (Button)findViewById(R.id.babyActivities);
		viewAct.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainMenuCard.this, DayActivities.class);
				startActivity(intent);
				//Toast.makeText(getApplicationContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
			}
		});
		
		summaryButton = (Button)findViewById(R.id.summary);//chart button
		summaryButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainMenuCard.this, Charts.class);
				startActivity(intent);
			}
		});
		
		memoryButton = (Button) findViewById(R.id.babyMemoryBook);
		memoryButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainMenuCard.this, MemoryBook.class);
				startActivity(intent);
			}
		});
		
		gridView = (GridView) findViewById(R.id.gridView);
		gridView.setAdapter(new ImageAdapter(this));
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				clicktype = 0;//onclick
				switch(position){
				case 0://feed
					showFeedDialog();
					break;
				case 1: //sleep
					showSleepDialog();
					break;
				case 2: //Diaper
					showDiaperDialog();
					break;
				case 3: //milestone
					showMilestonesDialog();
					break;
				case 4:
					openDiary();
					break;
				case 5:
					showHeightDialog();
					break;
				case 6:
					showWeightDialog();
					break;
				}
				
			}
		});
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				clicktype = 1;//onclick
				switch(position){
				case 0://feed
					showFeedDialog();
					break;
				case 1: //sleep
					showSleepDialog();
					break;
				case 2: //Diaper
					showDiaperDialog();
					break;
				case 3: //milestone
					showMilestonesDialog();
					break;
				case 4:
					openDiary();
					break;
				case 5:
					showHeightDialog();
					break;
				case 6:
					showWeightDialog();
					break;
				}
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu_card, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.action_profile:
			Intent i = new Intent(this, Profile.class);
			startActivity(i);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (doubleBackToExitPressedOnce) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_HOME);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(intent);
	        return;
	    }

	    this.doubleBackToExitPressedOnce = true;
	    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

	    new Handler().postDelayed(new Runnable() {

	        @Override
	        public void run() {
	            doubleBackToExitPressedOnce = false;                       
	        }
	    }, 2000);
	}
	
	
	
	public void openDiary(){
		Intent intent = new Intent(this, Diary.class);
		startActivity(intent);
	}
	

	private void insertCurrentActivity(String date, String time, String type, String info){
		//get date to insert into database-TABLE baby_activities
		String t = tf.hour12to24(time);
        dbHelper.addBabyActivity(new BabyActivity(date, time, type, info));
	}
	
	//Feed Dialog
	private void showFeedDialog(){
		DialogFragment frag = FragmentFeedDialog.newInstance(clicktype, "", "", "");
		frag.show(getSupportFragmentManager(), "FeedDialog");
	}
	
	//FeedDialogListener, insert record into database after feed dialog click OK
	@Override
	public void onFinishSetFeed(String date, String time, String type,
			String info) {
		// TODO Auto-generated method stub
		insertCurrentActivity(date, time, type, info);
		isStart = mPrefsStart.getBoolean(NAP_CLOCK, false);
		if(isStart && clicktype == 0){
			updateNapStatusAndDatabaseRecord();
		}
	}
	

	//Diaper Dialog
	private void showDiaperDialog(){
		DialogFragment frag = FragmentDiaperDialog.newInstance(clicktype, "", "", "");
		frag.show(getSupportFragmentManager(), "DiaperDialog");
	}
	
	//DiaperDialogListener, insert record into database after diaper dialog click OK
	@Override
	public void onFinishSetDiaper(String date, String time, String type,
			String info) {
		// TODO Auto-generated method stub
		insertCurrentActivity(date, time, type, info);
		if(isStart && clicktype == 0){
			updateNapStatusAndDatabaseRecord();
		}
	}
	
	//Milestone Dialog
	private void showMilestonesDialog(){
		DialogFragment frag = FragmentMilestoneDialog.newInstance(clicktype, "", "", "");
		frag.show(getSupportFragmentManager(), "MilestoneDialog");
	}
	
	//MilestoneDialogListener, insert record into database after milestone dialog click OK
	@Override
	public void onFinishSetMilestone(String date, String time, String type,
			String info) {
		// TODO Auto-generated method stub
		insertCurrentActivity(date, time, type, info);
	}
	
	//sleep dialog
	private void showSleepDialog(){
		String start = mPrefsTime.getString(START_TIME, "0");
		isStart = mPrefsStart.getBoolean(NAP_CLOCK, false);
		DialogFragment frag = FragmentSleepDialog.newInstance(clicktype, "", "", "", isStart, start);
		frag.show(getSupportFragmentManager(), "SleepDialog");
	}

	@Override
	public void onFinishSetSleep(String date, String time, String type,
			String info, boolean isStart, String start) {
		if(info.length() == 0){//case1: sleep clock start, isStart reset to true, start reset
			//record current status info and change SharedPreferences status
	 	    Editor editorStart = mPrefsStart.edit();
			//stop the clock, set NAP_CLOCK false
	  	   	editorStart.putBoolean(NAP_CLOCK, isStart);
	  	   	editorStart.commit();//SharedPreferences modified
	  	   	
	  	   	//start clock, set START_TIME start
     	   Editor editorTime = mPrefsTime.edit();
     	   editorTime.putString(START_TIME, start);
     	   editorTime.commit();
		} else {
			if(start.length() == 0){//case 2: current sleep activity stop, isStart reset, insert current activity to db
				Editor editorStart = mPrefsStart.edit();
				//stop the clock, set NAP_CLOCK false
		  	   	editorStart.putBoolean(NAP_CLOCK, isStart);
		  	   	editorStart.commit();//SharedPreferences modified
			}
			//case 3: insert new activity, not current activity, 
			//no change to start and isStart, insert activity to db
			insertCurrentActivity(date, time, type, info);
		}
		
	}
	
	//Height Dialog
	private void showHeightDialog(){
		DialogFragment frag = FragmentHeightDialog.newInstance("", "");
		frag.show(getSupportFragmentManager(), "HeightDialog");
	}
	
	@Override
	public void onFinishSetHeight(String date, String time, String type, String info) {
		insertCurrentActivity(date, time, type, info);
	}
	
	//Weight Dialog
	private void showWeightDialog(){
		DialogFragment frag = FragmentWeightDialog.newInstance("", "");
		frag.show(getSupportFragmentManager(), "WeightDialog");
	}
	
	@Override
	public void onFinishSetWeight(String date, String time, String type, String info) {
		insertCurrentActivity(date, time, type, info);
	}
	
	/*
	 * if nap stated and then diaper or eat happened, 
	 * stop nap clock and record nap status
	 */
	private void updateNapStatusAndDatabaseRecord(){
		 String info;
		 Editor editorStart = mPrefsStart.edit();
		//stop the clock, set NAP_CLOCK false
  	   	editorStart.putBoolean(NAP_CLOCK, false);
  	   	editorStart.commit();//SharedPreferences modified
	  	   	
		 String start = mPrefsTime.getString(START_TIME, "0");
		 String[] s = start.split(" ");
		 String date = s[0];
		 String time = s[1];
		 String type = "Nap";
		 
		 //calculate the difference between start and stop
		 Date startTime = null;
			try {
				startTime = df.parse(start);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 Date currentTime = null;
		 c = Calendar.getInstance();
			try {
				currentTime = df.parse(df.format(c.getTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 long diff = currentTime.getTime() - startTime.getTime();
		 //Log.e("current time and start time", currentTime + " " + startTime);
		 
		 long timeInSeconds = diff / 1000;
		 int hours, min;
		 hours = (int) (timeInSeconds / 3600);
		 timeInSeconds = timeInSeconds - (hours * 3600);
		 min = (int) (timeInSeconds / 60);
		 String h, m;
		 h = hours < 10 ? "0" + hours : hours + "";
		 m = min < 10 ? "0" + min : min + "";
		 info = h + "h" + m + "min";
		 
		 insertCurrentActivity(date, time, type, info);
	}

}
