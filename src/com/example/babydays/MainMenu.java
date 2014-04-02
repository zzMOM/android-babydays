package com.example.babydays;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu extends Activity {
	static final String[] items = new String[]{"Feed", "Sleep", "Diaper", "Milestone", "Diary"};
	static final Integer[] imageId = {	R.drawable.bottle,
        								R.drawable.sleep,
        								R.drawable.diaper,
        								R.drawable.milestones,
        								R.drawable.diary};
	Button viewAct;
	ListView lv;
	MediaPlayer littlestar;
	private int backButtonCount = 0;
	private SimpleDateFormat df;
	private Calendar c;
	private EditText textOZ;
	private MySQLiteHelper dbHelper;
	private CheckBox wetCheckBox;
	private CheckBox poopyCheckBox;
	private List mSelectedItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		//create database helper
		dbHelper = new MySQLiteHelper(this);
		
		//date and time format
		c = Calendar.getInstance();
        df = new SimpleDateFormat("MM-dd-yyyy hh:mma");
		
		viewAct = (Button)findViewById(R.id.babyActivities);
		viewAct.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainMenu.this, DayActivities.class);
				startActivity(intent);
				//Toast.makeText(getApplicationContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
			}
		});
		
		
		lv = (ListView) findViewById(R.id.itemLists);
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        //lv.setAdapter(adapter);
		lv.setAdapter(new MainMenuAdapter(this, items, imageId));
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// When clicked, show a toast with the TextView text
			    //Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
				
				switch(position){
				case 0:
					createFeedDialog();
	                break;
				case 1:
					creatSleepDialog();
	                break;
				case 2:
					creatDiaperDialog();
	                break;
				case 3:
					creatMilestonesDialog();
	                break;
				case 4:
					openDiary();
					break;
				}
				    
			}
		});
 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	
	public void createFeedDialog() {
		// Create custom dialog object
        final Dialog dialog = new Dialog(MainMenu.this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_feed);
        // Set dialog title
        dialog.setTitle("It's time to feed!");

        // set values for custom dialog components - text, edit text and button
        TextView showTime = (TextView) dialog.findViewById(R.id.showTime);
        String formattedDate = df.format(c.getTime());
        showTime.setText(formattedDate);
        
        textOZ = (EditText) dialog.findViewById(R.id.editTextOZ);

        dialog.show();
         
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
        // if decline button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
        Button okButton = (Button) dialog.findViewById(R.id.ok);
        // if decline button is clicked, close the custom dialog
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	//get date to insert into database-TABLE baby_activities
                String formattedDate = df.format(c.getTime());
                String[] s = formattedDate.split(" ");
                String date = s[0];
                String time = s[1];
                String type = "FeedMilk";
                String info = textOZ.getText().toString() + "oz";
            	dbHelper.addBabyActivity(new BabyActivity(date, time, type, info));
            	
                // Close dialog
                dialog.dismiss();
            }
        });
    }
	
	public void creatSleepDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
	    // Get the layout inflater
	    LayoutInflater inflater = MainMenu.this.getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(inflater.inflate(R.layout.dialog_sleep, null))
	    	   .setTitle("Time to sleep!")
	    // Add action buttons
	           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   
	               }
	           });      
	    builder.show();
	}
	
	public void creatDiaperDialog(){
		mSelectedItems = new ArrayList();
		AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
	    // Get the layout inflater
	    LayoutInflater inflater = MainMenu.this.getLayoutInflater();
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder//.setView(inflater.inflate(R.layout.dialog_diaper, null))
	    	   .setTitle("Time to change diaper!")
	    		// Specify the list array, the items to be selected by default (null for none),
	    		// and the listener through which to receive callbacks when items are selected
	           .setMultiChoiceItems(R.array.diaper, null,
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
	           	       //get date to insert into database-TABLE baby_activities
	                   String formattedDate = df.format(c.getTime());
	                   String[] s = formattedDate.split(" ");
	                   String date = s[0];
	                   String time = s[1];
	                   String type = "Diaper";
	                   
	                   StringBuffer info = new StringBuffer("");
	                   Resources res = getResources();
	                   String[] diaperSelectedItems = res.getStringArray(R.array.diaper);
	                   for(int i = 0; i < mSelectedItems.size(); i++){
	                	   info.append(diaperSelectedItems[(Integer) mSelectedItems.get(i)] + " ");
	                   }
	                   if(info.toString() != ""){
	                	   dbHelper.addBabyActivity(new BabyActivity(date, time, type, info.toString()));
	                   }
	                   
	                   dialog.dismiss();
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   
	               }
	           });      
	    builder.show();
	}
	
	public void creatMilestonesDialog(){
		mSelectedItems = new ArrayList();
		AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
	    // Get the layout inflater
	    LayoutInflater inflater = MainMenu.this.getLayoutInflater();

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
	            	 //get date to insert into database-TABLE baby_activities
	                   String formattedDate = df.format(c.getTime());
	                   String[] s = formattedDate.split(" ");
	                   String date = s[0];
	                   String time = s[1];
	                   String type = "Milestone";
	                   
	                   StringBuffer info = new StringBuffer("");
	                   Resources res = getResources();
	                   String[] milestoneSelectedItems = res.getStringArray(R.array.mileStone);
	                   for(int i = 0; i < mSelectedItems.size(); i++){
	                	   info.append(milestoneSelectedItems[(Integer) mSelectedItems.get(i)] + " ");
	                   }
	                   if(info.toString() != ""){
	                	   dbHelper.addBabyActivity(new BabyActivity(date, time, type, info.toString()));
	                   }
	                   
	                   dialog.dismiss();
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   
	               }
	           });      
	    builder.show();
	}
	
	public void openDiary(){
		Intent intent = new Intent(MainMenu.this, Diary.class);
		startActivity(intent);
	}

	//background music
	/*
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		littlestar = MediaPlayer.create(this, R.raw.littlestar);
		littlestar.start();
		littlestar.setLooping(true);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		littlestar.stop();
		littlestar.release();
		super.onPause();
	}*/
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		/*AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
	    // Get the layout inflater
	    LayoutInflater inflater = MainMenu.this.getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setTitle("Exit")
	    	   .setMessage("Do you want to exit app?")
	    // Add action buttons
	           .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   	Intent intent = new Intent(Intent.ACTION_MAIN);
   						intent.addCategory(Intent.CATEGORY_HOME);
   						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
   						startActivity(intent);
	               }
	           })
	           .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   
	               }
	           });      
	    builder.show();*/
		if(backButtonCount  >= 1)
	    {
	        Intent intent = new Intent(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_HOME);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(intent);
	    }
	    else
	    {
	        Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
	        backButtonCount++;
	    }
	}

}
