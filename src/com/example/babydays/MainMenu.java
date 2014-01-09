package com.example.babydays;



import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu extends ListActivity {
	static final String[] items = new String[]{"Feed", "Sleep", "Diaper", "Milestone" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		final Button viewAct = (Button)findViewById(R.id.babyActivities);
		viewAct.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*Intent intent = new Intent(MainMenu.this, DayActivities.class);
				startActivity(intent);*/
				Toast.makeText(getApplicationContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
			}
		});
		
		
		final ListView lv = (ListView) findViewById(R.id.mainmenu);
		lv.setAdapter(new MainMenuAdapter(this, items));
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
        //TextView curTime = (TextView) dialog.findViewById(R.id.curTime);
        TextView showTime = (TextView) dialog.findViewById(R.id.showTime);
        //TextView showTime--get current date and time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = df.format(c.getTime());
        showTime.setText(formattedDate);
        
        //TextView feedAmount = (TextView) dialog.findViewById(R.id.feedAmount);
        //EditText textOZ = (EditText) dialog.findViewById(R.id.editTextOZ);
        //TextView oz = (TextView) dialog.findViewById(R.id.oz);
        

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
		AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
	    // Get the layout inflater
	    LayoutInflater inflater = MainMenu.this.getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(inflater.inflate(R.layout.dialog_diaper, null))
	    	   .setTitle("Time to change diaper!")
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
	
	public void creatMilestonesDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
	    // Get the layout inflater
	    LayoutInflater inflater = MainMenu.this.getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(inflater.inflate(R.layout.dialop_milstones, null))
	    	   .setTitle("MileStones")
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


}
