package com.example.babydays;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.internal.Card.OnLongCardClickListener;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardGridView;
import it.gmariotti.cardslib.library.view.CardListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainMenuCard extends Activity {
	static final String[] items = new String[]{"Feed", "Nap", "Diaper", "Milestone", "Diary"};
	static final Integer[] imageId = {	R.drawable.bottle,
        								R.drawable.sleep,
        								R.drawable.diaper,
        								R.drawable.milestones,
        								R.drawable.diary};
	//SharedPreferences to record nap start or not
	private static final String NAP_CLOCK = "napclock";
	private SharedPreferences mPrefsStart;
	private boolean isStart = false;//isStart to record whether nap clock started
	private static final String START_TIME = "starttime";
	private SharedPreferences mPrefsTime;
	private DateTimePickerClass dateTimePickerClass;
	
	private ViewStub dateTimeStub;
	private CardGridView cardGridMenu;
	private ImageButton pickDate, pickTime;
	private EditText showDate, showTime;
	private Button viewAct, summaryButton, memoryButton;
	private SimpleDateFormat df;
	private Calendar c;
	private EditText textOZ;
	private MySQLiteHelper dbHelper;
	private ArrayList<Integer> mSelectedItems;
	private boolean doubleBackToExitPressedOnce = false;
	private int clicktype = 0;
	
	static final int DATE_DIALOG_ID = 100;
	static final int TIME_DIALOG_ID = 999;
	static final int FEED_DIALOG_ID = 1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu_card);
		
		//show action bar
		ActionBar actionBar = getActionBar();
		actionBar.show();
		actionBar.setTitle("Baby Days");//change action bar title
		//actionBar.setIcon(icon);
		
		//initialize DateTimePickerClass
		dateTimePickerClass = new DateTimePickerClass();
		
		//create database helper
		dbHelper = new MySQLiteHelper(this);
		
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
				Intent intent = new Intent(MainMenuCard.this, Summary.class);
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
		
		
		cardGridMenu = (CardGridView) findViewById(R.id.cardGridMenu);
		//create Card list
		ArrayList<Card> cards = new ArrayList<Card>();
		for(int i = 0; i < items.length; i++){
			Card c = new Card(this);
			//add header
			CardHeader cheader = new CardHeader(this);
			cheader.setTitle(items[i]);
			c.addCardHeader(cheader);
			//add card thumb nail
			CardThumbnail thumb = new CardThumbnail(this);
			thumb.setDrawableResource(imageId[i]);
			c.addCardThumbnail(thumb);
			//set on click listener
			c.setOnClickListener(new OnCardClickListener() {
				
				@Override
				public void onClick(Card card, View arg1) {
					// TODO Auto-generated method stub
					clicktype = 0;//onclick
					if(card.getCardHeader().getTitle().toString().equals(items[0])){
						createFeedDialog();
					} else if(card.getCardHeader().getTitle().toString().equals(items[1])){
						creatSleepDialog();
					} else if(card.getCardHeader().getTitle().toString().equals(items[2])){
						creatDiaperDialog();
					} else if(card.getCardHeader().getTitle().toString().equals(items[3])){
						creatMilestonesDialog();
					} else if(card.getCardHeader().getTitle().toString().equals(items[4])){
						openDiary();
					}
				}
			});
			//set on long click listener
			c.setOnLongClickListener(new OnLongCardClickListener() {
				
				@Override
				public boolean onLongClick(Card card, View arg1) {
					// TODO Auto-generated method stub
					clicktype = 1;//onlongclick
					if(card.getCardHeader().getTitle().toString().equals(items[0])){
						createFeedDialog();
					} else if(card.getCardHeader().getTitle().toString().equals(items[1])){
						creatSleepDialog();
					} else if(card.getCardHeader().getTitle().toString().equals(items[2])){
						creatDiaperDialog();
					} else if(card.getCardHeader().getTitle().toString().equals(items[3])){
						creatMilestonesDialog();
					} else if(card.getCardHeader().getTitle().toString().equals(items[4])){
						openDiary();
					}
					return false;
				}
			});
			
			cards.add(c);
		}
		CardArrayAdapter cardAdapter = new CardArrayAdapter(this, cards);
		if(cardGridMenu != null){
			cardGridMenu.setAdapter(cardAdapter);
		}
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
	
	/*
	 * if nap stated and then diaper or eat happened, 
	 * stop nap clock and record nap status
	 */
	private void updateNapStatusAndDatabaseRecord(){
		String info;
		//record current status info and change SharedPreferences status
 	    Editor editorStart = mPrefsStart.edit();
		//stop the clock, set NAP_CLOCK false
  	   	editorStart.putBoolean(NAP_CLOCK, false);
  	   	editorStart.commit();//SharedPreferences modified
  	   	Log.e("put napclock to be", Boolean.toString(mPrefsStart.getBoolean(NAP_CLOCK, false)));
  	   
  	   	//get date and insert into database-TABLE baby_activities
         String formatedDate = mPrefsTime.getString(START_TIME, "0");
         String[] s = formatedDate.split(" ");
         String date = s[0];
         String time = s[1];
         String type = "Nap";
         
         //calculate the difference between start and stop
         Date startTime = null;
			try {
				startTime = df.parse(formatedDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         Date currentTime = null;
			try {
				currentTime = df.parse(df.format(c.getTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         long diff = currentTime.getTime() - startTime.getTime();
         Log.e("current time and start time", currentTime + " " + startTime);
         
         long timeInSeconds = diff / 1000;
         int hours, min;
         hours = (int) (timeInSeconds / 3600);
         timeInSeconds = timeInSeconds - (hours * 3600);
         min = (int) (timeInSeconds / 60);
         String h, m;
         h = hours < 10 ? "0" + hours : hours + "";
         m = min < 10 ? "0" + min : min + "";
         info = h + "h" + m + "min";
         
     	   dbHelper.addBabyActivity(new BabyActivity(date, time, type, info));
	}
	
	public void createFeedDialog() {
		// Create custom dialog object
        final Dialog dialog = new Dialog(this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_feed);
        // Set dialog title
        if(clicktype == 0){
        	dialog.setTitle("Time to feed!");
        } else {
        	dialog.setTitle("Insert a feed activity");
        }
        
        dateTimeStub = (ViewStub) dialog.findViewById(R.id.dateTimeStub);
        dateTimeStub.setLayoutResource(R.layout.date_time_merge);
        View inflatedView = dateTimeStub.inflate();
        setDateTimeMergePart(inflatedView);
        
        textOZ = (EditText) dialog.findViewById(R.id.editTextOZ);

        Button okButton = (Button) dialog.findViewById(R.id.ok);
        // if decline button is clicked, close the custom dialog
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	//check and change nap status
            	isStart = mPrefsStart.getBoolean(NAP_CLOCK, false);
            	if(isStart && clicktype == 0){//onclick
            		updateNapStatusAndDatabaseRecord();
            	}
            	
                String type = "FeedMilk";
                String info = "";
                if(textOZ.getText().toString().length() > 0){
                	info = textOZ.getText().toString() + "oz";
                	insertCurrentActivity(type, info);
                }
            	
                // Close dialog
                dialog.dismiss();
            }
        });
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
        // if decline button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
        dialog.show();
    }
	
	
	
	public void creatSleepDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    
	    //check SharedPreferences and edit if nap status change
	    //if isStart is true, means nap is started
	    //only choice is nap stop
	    String message;
	    isStart = mPrefsStart.getBoolean(NAP_CLOCK, false);
	    if(isStart){
	    	message = "Nap Stop!";
	    } else {
	    	message = "Nap Start!";
	    }
	    
	    builder.setTitle("Time to sleep!")
	    	   .setMessage(message)
	    		// Add action buttons
	           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int id) {
		            	   isStart = mPrefsStart.getBoolean(NAP_CLOCK, false);
		            	   Log.e("nap clock isStart is ", Boolean.toString(isStart));
		                   if(isStart){		//isStart true, then stop clock
		                	   updateNapStatusAndDatabaseRecord();
		                   } else {
		                	   //record current status info and change SharedPreferences status
			            	   Editor editorStart = mPrefsStart.edit();
		                	   //start nap, set NAP_CLOCK true;
		                	   editorStart.putBoolean(NAP_CLOCK, true);
		                	   editorStart.commit();//SharedPreferences modified
		                	   Log.e("put napclock to be", Boolean.toString(mPrefsStart.getBoolean(NAP_CLOCK, false)));
		                	   
		                	   //start clock, set START_TIME current clock time
		                	   Editor editorTime = mPrefsTime.edit();
		                	   c = Calendar.getInstance();
		                	   String currentTime = df.format(c.getTime());
		                	   editorTime.putString(START_TIME, currentTime);
		                	   editorTime.commit();
			                   Log.e("start time current", mPrefsTime.getString(START_TIME, "0"));
		                	   
			                   
		                   }
		               	   		               	
		                   // Close dialog
		                   dialog.dismiss();
		               }
		           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	// Close dialog
		                   dialog.dismiss();
		               }
		           });      
	    builder.show();
	}
	
	public void creatDiaperDialog(){
		mSelectedItems = new ArrayList<Integer>();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		// Set dialog title
        if(clicktype == 0){
        	builder.setTitle("Time to change diaper!");
        } else {
        	builder.setTitle("Insert a new diaper activity!");
        }
        
        LayoutInflater inflater = getLayoutInflater();
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
        final View dateTimeView = inflater.inflate(R.layout.dialog_diaper, null);
	    builder.setView(dateTimeView);
	    		// Specify the list array, the items to be selected by default (null for none),
	    		// and the listener through which to receive callbacks when items are selected
	    builder.setMultiChoiceItems(R.array.diaper, null,
	                      new DialogInterface.OnMultiChoiceClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which, boolean isChecked) {
	                   if (isChecked) {
	                       // If the user checked the item, add it to the selected items
	                       mSelectedItems.add(which);
	                   } else if (mSelectedItems.contains(which)) {
	                       // Else, if the item is already in the array, remove it 
	                       mSelectedItems.remove(Integer.valueOf(which));
	                   }
	               }
	           })
	       
	    		// Add action buttons
	    		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	 //check and change nap status
	               	 isStart = mPrefsStart.getBoolean(NAP_CLOCK, false);
	               	 if(isStart && clicktype == 0){//onclick
	               		 updateNapStatusAndDatabaseRecord();
	               	 }
	               	
	                   String type = "Diaper";
	                   
	                   StringBuffer info = new StringBuffer("");
	                   Resources res = getResources();
	                   String[] diaperSelectedItems = res.getStringArray(R.array.diaper);
	                   for(int i = 0; i < mSelectedItems.size(); i++){
	                	   info.append(diaperSelectedItems[(Integer) mSelectedItems.get(i)] + " ");
	                   }
	                   if(info.toString() != ""){
	                	   insertCurrentActivity(type, info.toString());;
	                   }
	                   
	                   dialog.dismiss();
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   dialog.dismiss();
	               }
	           });  
	    
	    AlertDialog dialog = builder.create();
	    dateTimeStub = (ViewStub) dateTimeView.findViewById(R.id.dateTimeStub);
        dateTimeStub.setLayoutResource(R.layout.date_time_merge);
        View inflatedView = dateTimeStub.inflate();
        setDateTimeMergePart(inflatedView);
	    dialog.show();
	}
	
	public void creatMilestonesDialog(){
		mSelectedItems = new ArrayList<Integer>();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		// Set dialog title
        if(clicktype == 0){
        	builder.setTitle("Milestone!");
        } else {
        	builder.setTitle("Insert a new milestone!");
        }
        
        LayoutInflater inflater = getLayoutInflater();
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
        final View dateTimeView = inflater.inflate(R.layout.dialog_milstones, null);
	    builder.setView(dateTimeView);
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder//.setView(inflater.inflate(R.layout.dialop_milstones, null))
	    	   .setTitle("MileStones")
	    	   // Specify the list array, the items to be selected by default (null for none),
	    		// and the listener through which to receive callbacks when items are selected
	           .setMultiChoiceItems(R.array.mileStone, null,
	                      new DialogInterface.OnMultiChoiceClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which, boolean isChecked) {
	                   if (isChecked) {
	                       // If the user checked the item, add it to the selected items
	                       mSelectedItems.add(which);
	                   } else if (mSelectedItems.contains(which)) {
	                       // Else, if the item is already in the array, remove it 
	                       mSelectedItems.remove(Integer.valueOf(which));
	                   }
	               }
	           })
	           // Add action buttons
	           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   String type = "Milestone";
	                   
	                   StringBuffer info = new StringBuffer("");
	                   Resources res = getResources();
	                   String[] milestoneSelectedItems = res.getStringArray(R.array.mileStone);
	                   for(int i = 0; i < mSelectedItems.size(); i++){
	                	   info.append(milestoneSelectedItems[(Integer) mSelectedItems.get(i)] + " ");
	                   }
	                   if(info.toString() != ""){
	                	   insertCurrentActivity(type, info.toString());
	                   }
	                   
	                   dialog.dismiss();
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   
	               }
	           });      
	    AlertDialog dialog = builder.create();
	    dateTimeStub = (ViewStub) dateTimeView.findViewById(R.id.dateTimeStub);
        dateTimeStub.setLayoutResource(R.layout.date_time_merge);
        View inflatedView = dateTimeStub.inflate();
        setDateTimeMergePart(inflatedView);
	    dialog.show();
	}
	
	
	
	public void openDiary(){
		Intent intent = new Intent(this, Diary.class);
		startActivity(intent);
	}
	
	private void setDateTimeMergePart(View inflatedView){
		// set values for date_time_merge.xml components
		//use inflatedView to get view from viewstub
        showTime = (EditText) inflatedView.findViewById(R.id.showTime);
        showDate = (EditText) inflatedView.findViewById(R.id.showDate);
        c = Calendar.getInstance();
        String formattedDate = df.format(c.getTime());
        String[] s = formattedDate.split(" ");
        showDate.setText(s[0]);
        String[] st = s[1].split(":");
		showTime.setText(dateTimePickerClass.timeFormat24To12(Integer.parseInt(st[0]), Integer.parseInt(st[1])));
        
        pickDate = (ImageButton) inflatedView.findViewById(R.id.pickDate);
        pickTime = (ImageButton) inflatedView.findViewById(R.id.pickTime);
        if(clicktype == 0){//onclick, current activity
        	dateTimeStub.setVisibility(View.GONE);
        } else {//onlongclick, insert new activity
        	dateTimeStub.setVisibility(View.VISIBLE);
        }
        //pickDate button and pickTime button
        pickDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
		});
        pickTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(TIME_DIALOG_ID);
			}
		});
        
        showDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
		});
        showTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(TIME_DIALOG_ID);
			}
		});
	}
	
	private void insertCurrentActivity(String type, String info){
		//get date to insert into database-TABLE baby_activities
 	    String date = showDate.getText().toString();
 	    String t = showTime.getText().toString();
 	    String time = dateTimePickerClass.timeFormat12To24(Integer.parseInt(t.substring(0, 2)), Integer.parseInt(t.substring(3, 5)), t.substring(5, 7));
        dbHelper.addBabyActivity(new BabyActivity(date, time, type, info));
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
        c = Calendar.getInstance();
		switch (id) {
		case DATE_DIALOG_ID:
			int year = c.get(Calendar.YEAR);
	        int month = c.get(Calendar.MONTH);
	        int day = c.get(Calendar.DAY_OF_MONTH);
		    return new DatePickerDialog(this, dateTimePickerClass.datePickerListener, 
                         year, month, day);
		case TIME_DIALOG_ID:
			//use current date as default date show in the DatePicker
			int hour = c.get(Calendar.HOUR);
			int min = c.get(Calendar.MINUTE);
			return new TimePickerDialog(this, dateTimePickerClass.timePickerListener, hour, min, false);
		}
		return null;
	}

}
