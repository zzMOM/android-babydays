package com.example.babydays;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemSelectedListener;

public class ManageRecords extends Activity {
	private Spinner manageSpinner, typeSpinner;
	private Button pickDate, pickTime, cancelRecord, okRecord;
	private ImageButton searchID;
	private EditText recordsIDEdit;
	private TextView errorMessage, showIDDate, showIDTime, showIDType, infoText, infoTextNew;
	
	private MySQLiteHelper dbHelper;
	private Calendar c;
	private BabyActivity oldActivity, newActivity;
	
	private ArrayList<Integer> mSelectedItems;
	static final int DATE_DIALOG_ID = 100;
	static final int TIME_DIALOG_ID = 999;
	
	private String timePick24 = "";//used for save timePick value in 24-hour format

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_records);
		
		dbHelper = new MySQLiteHelper(this);
		//date and time format
		c = Calendar.getInstance();
		
		pickDate = (Button) findViewById(R.id.changeDate);
        pickTime = (Button) findViewById(R.id.changeTime);
    	searchID = (ImageButton) findViewById(R.id.searchID);
    	recordsIDEdit = (EditText) findViewById(R.id.recordsIDEdit);
    	infoText = (TextView) findViewById(R.id.infoText);
    	infoTextNew = (TextView) findViewById(R.id.infoTextNew);
    	showIDDate = (TextView) findViewById(R.id.showIDDate);
    	showIDTime = (TextView) findViewById(R.id.showIDTime);
    	showIDType = (TextView) findViewById(R.id.showIDType);
    	cancelRecord = (Button) findViewById(R.id.cancelRecord);
    	okRecord = (Button) findViewById(R.id.okRecord);
    	errorMessage = (TextView) findViewById(R.id.errorMessage);
    	//idResultLayout = (LinearLayout) dialog.findViewById(R.id.idResultLayout);
    	
    	//manageSpinner function
        manageSpinner = (Spinner) findViewById(R.id.manageSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.manageSp, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        manageSpinner.setAdapter(adapter);
        
        manageSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, 
		            int pos, long id) {
				//change status
				reset();
				manageSpinnerStatus(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        //typeSpinner function
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, 
		            int pos, long id) {
				switch(pos){
				case 0:		//pick type
					break;
				case 1:		//FeedMilk
					editFeed();
					break;
				case 2:		//Sleep
					editSleep();
					break;
				case 3:		//Diaper
					editDiaper();
					break;
				case 4:		//Milestone
					editMilestone();
					break;
				}
				
			}
			
			

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        	
		});
        
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
        
        
        //recordsIDEdit press enter
        recordsIDEdit.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if((actionId == EditorInfo.IME_ACTION_DONE) || (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) &&
		                (event.getAction() == KeyEvent.ACTION_DOWN )){
					doSearch();
				}
				return false;
			}
		});
        
        //searchID button
        searchID.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doSearch();
			}
		});
	
        //cancel button
        cancelRecord.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				reset();
			}
		});
        
        //ok button
        okRecord.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//get manageSpinner value
				int pos = manageSpinner.getSelectedItemPosition();
				switch(pos){
				case 0:	//edit
					editRecord();
					break;
				case 1:	//insert
					insertNewRecord();
					break;
				case 2:	//delet
					deleteRecord();
					break;
				}
			}
		});
	}
	
	private void manageSpinnerStatus(int pos){
		pickDate.setText("Pick Date");
		pickTime.setText("Pick Time");
		typeSpinner.setSelection(0);
		switch(pos){
		case 0:		//edit
			recordsIDEdit.setEnabled(true);
			searchID.setEnabled(true);
			pickDate.setEnabled(false);
			pickTime.setEnabled(false);
			typeSpinner.setEnabled(false);
			okRecord.setEnabled(false);
			break;
		case 1:		//insert
			recordsIDEdit.setEnabled(false);
			searchID.setEnabled(false);
			pickDate.setEnabled(true);
			pickTime.setEnabled(true);
			typeSpinner.setEnabled(true);
			okRecord.setEnabled(true);
			break;
		case 2:		//delete
			recordsIDEdit.setEnabled(true);
			searchID.setEnabled(true);
			pickDate.setEnabled(false);
			pickTime.setEnabled(false);
			typeSpinner.setEnabled(false);
			okRecord.setEnabled(false);
			break;
		}
	}
	
	private String convertHourFormat24to12(String time24){
		//date transfer 24hours to 12hours
		String time12 = "";
		SimpleDateFormat h_mm_a = new SimpleDateFormat("h:mma");
		SimpleDateFormat hh_mm = new SimpleDateFormat("HH:mm");

		try {
		    Date t = hh_mm.parse(time24);
		    time12 = h_mm_a.format(t).toString();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return time12;
	}
	
	private void reset(){
		//clear all
		errorMessage.setText("");
		recordsIDEdit.setText("");
		showIDDate.setText("");
		showIDTime.setText("");
		showIDType.setText("");
		infoText.setText("");
		infoTextNew.setText("");
		int pos = manageSpinner.getSelectedItemPosition();
		//change status
		manageSpinnerStatus(pos);
	}
	
	
	private void doSearch(){
		if(recordsIDEdit.getText().toString().length() == 0){
			return;
		}
		
		int id = Integer.parseInt(recordsIDEdit.getText().toString());
		//search by ID
		oldActivity = dbHelper.getBabyActivity(id);
		//check whether id exists
		if(oldActivity == null){
			recordsIDEdit.setText("");
			errorMessage.setText("Can't find record");
			return;
		}
		//enable okRecord button
		okRecord.setEnabled(true);
		//show current ID values
		showIDDate.setText(oldActivity.getDate().toString());
		String time24 = oldActivity.getTime().toString();
		showIDTime.setText(convertHourFormat24to12(time24));
		showIDType.setText(oldActivity.getType().toString());
		infoText.setText(oldActivity.getInfo().toString());
		
		//change components status by manageSpinner selection
		int manageSpinnerPos = manageSpinner.getSelectedItemPosition();
		switch(manageSpinnerPos){
		case 0:		//edit
			recordsIDEdit.setEnabled(false);
			searchID.setEnabled(false);
			pickDate.setEnabled(true);
			pickTime.setEnabled(true);
			typeSpinner.setEnabled(true);
			break;
		default:
			break;
			
		}
	}
	
	private void editFeed(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ManageRecords.this);
	    builder.setTitle("Edit Feed");
	    //set up input
	    builder.setMessage("Enter amount: (oz)");
	    final EditText textOZ = new EditText(ManageRecords.this);
	    textOZ.setInputType(InputType.TYPE_CLASS_NUMBER);
	    builder.setView(textOZ);
	    // Add action buttons
	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   //get input and show in infoText
	                   String info = textOZ.getText().toString() + "oz";
	                   infoTextNew.setText("");
	                   infoTextNew.setText(info);
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
	
	private void editSleep(){
		AlertDialog.Builder builder = new AlertDialog.Builder(ManageRecords.this);
	    builder.setTitle("Edit Nap Time");
	    //set up input
	    builder.setMessage("Enter duration hours and minutes");
	    final LinearLayout ll = new LinearLayout(ManageRecords.this);
	    ll.setOrientation(LinearLayout.HORIZONTAL);
	    ll.setBackgroundColor(Color.LTGRAY);
	    final EditText hour = new EditText(this); 
	    final TextView h = new TextView(this);
	    final EditText minute = new EditText(this);
	    final TextView m = new TextView(this);
	    hour.setInputType(InputType.TYPE_CLASS_NUMBER);
	    hour.setEms(5);
	    minute.setInputType(InputType.TYPE_CLASS_NUMBER);
	    minute.setEms(5);
	    h.setText("h");
	    m.setText("min");
	    ll.addView(hour);
	    ll.addView(h);
	    ll.addView(minute);
	    ll.addView(m);
	    builder.setView(ll);
	    // Add action buttons
	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   //get input and show in infoText
	            	   infoTextNew.setText("");
	            	   String hh = hour.getText().toString();
	            	   String mm = minute.getText().toString();
	            	   if(hh.length() == 1){
	            		   hh = "0" + hh;
	            	   } else if(hh.length() == 0){
	            		   hh = "00";
	            	   }
	            	   if(mm.length() == 1){
	            		   mm = "0" + mm;
	            	   } else if(mm.length() == 0){
	            		   mm = "00";
	            	   }
	                   String info = hh + "h" + mm + "min";
	                   infoTextNew.setText(info);
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
	
	private void editDiaper(){
		mSelectedItems = new ArrayList<Integer>();
		AlertDialog.Builder builder = new AlertDialog.Builder(ManageRecords.this);
	    builder//.setView(inflater.inflate(R.layout.dialog_diaper, null))
	    	   .setTitle("Edit Diaper Change!")
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
	                   StringBuffer info = new StringBuffer("");
	                   Resources res = getResources();
	                   String[] diaperSelectedItems = res.getStringArray(R.array.diaper);
	                   for(int i = 0; i < mSelectedItems.size(); i++){
	                	   info.append(diaperSelectedItems[(Integer) mSelectedItems.get(i)] + " ");
	                   }
	                   infoTextNew.setText("");
	                   infoTextNew.setText(info.toString());
	                   
	                   dialog.dismiss();
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   
	               }
	           });      
	    builder.show();
	}
	
	private void editMilestone(){
		mSelectedItems = new ArrayList<Integer>();
		AlertDialog.Builder builder = new AlertDialog.Builder(ManageRecords.this);
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
	                   StringBuffer info = new StringBuffer("");
	                   Resources res = getResources();
	                   String[] milestoneSelectedItems = res.getStringArray(R.array.mileStone);
	                   for(int i = 0; i < mSelectedItems.size(); i++){
	                	   info.append(milestoneSelectedItems[(Integer) mSelectedItems.get(i)] + " ");
	                   }
	                   
	                   infoTextNew.setText("");
	                   infoTextNew.setText(info.toString());
	                   
	                   dialog.dismiss();
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   
	               }
	           });      
	    builder.show();
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
					pickDate.setText(new StringBuilder().append(m)
								   .append("-").append(d).append("-").append(selectedYear)
								   .toString());
				}
			};
			
			
	
	private TimePickerDialog.OnTimeSetListener timePickerListener
			= new TimePickerDialog.OnTimeSetListener() {
				
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					// set selected time to textview
					timePick24 = new StringBuilder().append(hourOfDay).append(":").append(minute).toString();
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
					pickTime.setText(new StringBuilder().append(h).append(":").append(m).append(a_p).toString());
				}
			};
	
	
			
	
	
			
	private void editRecord(){
		newActivity = new BabyActivity(oldActivity);
		String d = pickDate.getText().toString();
		if(!d.equals("Pick Date")){
			newActivity.setDate(d);
		}
		String t = pickTime.getText().toString();
		if(!t.equals("Pick Time")){
			newActivity.setTime(timePick24);
		}
		if(typeSpinner.getSelectedItemPosition() != 0){
			Resources res = getResources();
			String[] typeItems = res.getStringArray(R.array.typeSp);
			newActivity.setType(typeItems[typeSpinner.getSelectedItemPosition()]);
		}
		String i = infoTextNew.getText().toString();
		if(i.length() != 0){
			newActivity.setInfo(i);
		}
		
		//Log.e("newActivity", newActivity.getId() + " " + newActivity.getDate().toString() + " " + newActivity.getTime() + " "
				//+ newActivity.getType().toString() + " " + newActivity.getInfo().toString());
		
		//update
		dbHelper.updateBabyActivity(newActivity);
		reset();
	}
	
	private void insertNewRecord(){
		Boolean isMissing = false;
		newActivity = new BabyActivity();
		String d = pickDate.getText().toString();
		if(d.equals("Pick Date")){
			isMissing = true;
			pickDate.setTextColor(Color.RED);
		} else {
			newActivity.setDate(d);
			pickDate.setTextColor(Color.BLACK);
		}
		String t = pickTime.getText().toString();
		if(t.equals("Pick Time")){
			isMissing = true;
			pickTime.setTextColor(Color.RED);
		} else {
			newActivity.setTime(timePick24);
			pickTime.setTextColor(Color.BLACK);
		}
		TextView textView = (TextView) typeSpinner.getChildAt(0);
		if(typeSpinner.getSelectedItemPosition() == 0){
			isMissing = true;
			textView.setTextColor(Color.RED);
		} else {
			Resources res = getResources();
			String[] typeItems = res.getStringArray(R.array.typeSp);
			newActivity.setType(typeItems[typeSpinner.getSelectedItemPosition()]);
			textView.setTextColor(Color.BLACK);
		}
		String i = infoTextNew.getText().toString();
		if(i.length() != 0){
			newActivity.setInfo(i);
		}
		
		//update
		if(isMissing){
			errorMessage.setText("Missing infomation!");
			return;
		}
		dbHelper.addBabyActivity(newActivity);
		reset();
	}
	
	private void deleteRecord(){
		dbHelper.deleteBabyActivity(oldActivity);
		reset();
	}
			
			
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_records, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		Intent intent = new Intent(this, DayActivities.class);
		startActivity(intent);
	}

}
