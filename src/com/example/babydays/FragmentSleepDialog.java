package com.example.babydays;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.babydays.FragmentDatePicker.DatePickerDialogListener;
import com.example.babydays.FragmentTimePicker.TimePickerDialogListener;

public class FragmentSleepDialog extends DialogFragment implements DatePickerDialogListener
														,TimePickerDialogListener{
	private ViewStub dateTimeStub;
	private ImageButton pickDate, pickTime;
	private EditText showDate, showTime, hourEdit, minuteEdit;
	private SimpleDateFormat df;
	private Calendar c;
	private TimeFormatTransfer tf;
	private int clicktype;
	private boolean isStart;
	private String start;
	
	
	public interface SleepDialogListener {
	    void onFinishSetSleep(String date, String time, String type, String info
	    					, boolean isStart, String start);
	}
	
	public static FragmentSleepDialog newInstance(int clicktype, String date, String time, String info
												, boolean isStart, String start) {
		FragmentSleepDialog frag = new FragmentSleepDialog();
        Bundle args = new Bundle();
        args.putInt("clicktype", clicktype);
        args.putBoolean("isStart", isStart);
        args.putString("start", start);
        args.putString("date", date);
        args.putString("time", time);
        args.putString("info", info);
        frag.setArguments(args);
        return frag;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		df = new SimpleDateFormat("MM-dd-yyyy HH:mm");
		clicktype = getArguments().getInt("clicktype");
		isStart = getArguments().getBoolean("isStart");
		start = getArguments().getString("start");
		
		if(clicktype == 0){
			 return currentSleepDialog();
		} else {
			return durationSleepDialog();
		}
    }
	
	private Dialog durationSleepDialog(){
		// Create custom dialog object
        final Dialog dialog = new Dialog(getActivity());
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_sleep);
        // Set dialog title
        dialog.setTitle("Insert a new sleep activity");
        
        hourEdit = (EditText) dialog.findViewById(R.id.hourEdit);
        minuteEdit = (EditText) dialog.findViewById(R.id.minuteEdit);
        dateTimeStub = (ViewStub) dialog.findViewById(R.id.dateTimeStub);
        dateTimeStub.setLayoutResource(R.layout.date_time_merge);
        View inflatedView = dateTimeStub.inflate();
        setDateTimeMergePart(inflatedView);
        
        Button okButton = (Button) dialog.findViewById(R.id.ok);
        // if decline button is clicked, close the custom dialog
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String hh = hourEdit.getText().toString();
         	    String mm = minuteEdit.getText().toString();
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
                String type = "Nap";
                TimeFormatTransfer tf = new TimeFormatTransfer();
            	String time = tf.hour12to24(showTime.getText().toString());
                SleepDialogListener listener = (SleepDialogListener) getActivity();
            	 listener.onFinishSetSleep(showDate.getText().toString()
										 , time, type, info, isStart, "");
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
		return dialog;
	}
	
	private Dialog currentSleepDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    //check SharedPreferences and edit if nap status change
	    //if isStart is true, means nap is started
	    //only choice is nap stop
	    String message;
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
		                   if(isStart){		//nap end, stop clock
		                	   updateNapStatusAndDatabaseRecord();
		                   } else {//nap start!
		                	   c = Calendar.getInstance();
		                	   String currentTime = df.format(c.getTime());
		                	   
		                	   isStart = true;
		                	   SleepDialogListener listener = (SleepDialogListener) getActivity();
		                	   listener.onFinishSetSleep("", "", "", "", isStart, currentTime);
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
		
		return builder.create();
	}
	
	
	
	/*
	 * if nap stated and then diaper or eat happened, 
	 * stop nap clock and record nap status
	 */
	private void updateNapStatusAndDatabaseRecord(){
		 String info;
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
		 
		 //nap end, insert activity record, and set isStart to false
		 isStart = false;
		 SleepDialogListener listener = (SleepDialogListener) getActivity();
		 listener.onFinishSetSleep(date, time, type, info, isStart, "");
	}
	
	
	private void setDateTimeMergePart(View inflatedView){
		// set values for date_time_merge.xml components
		//use inflatedView to get view from viewstub
        showTime = (EditText) inflatedView.findViewById(R.id.showTime);
        showDate = (EditText) inflatedView.findViewById(R.id.showDate);
        c = Calendar.getInstance();
        df = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        
        String date = getArguments().getString("date");
		String time = getArguments().getString("time");
		String info = getArguments().getString("info");
		
		//set initial value for dialog
        String formattedDate = df.format(c.getTime());
        String[] s = formattedDate.split(" ");
        if(date.length() == 0){//set showDate current date
        	showDate.setText(s[0]);
        } else {//set showDate specific value
        	showDate.setText(date);
        }
        if(time.length() == 0){//set showTime current time
	        String[] st = s[1].split(":");
	        tf = new TimeFormatTransfer();
			showTime.setText(tf.timeFormat24To12(Integer.parseInt(st[0]), Integer.parseInt(st[1])));
        } else {//set showTime specific value
        	showTime.setText(time);
        }
        if(info.length() > 0){//set info sepcific value
        	hourEdit.setText(info.substring(0, 2));
        	minuteEdit.setText(info.substring(3, 5));
        }
        
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
				showDatePickerDialog();
			}
		});
        pickTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showTimePickerDialog();
			}
		});
	}

	//DatePickerDialogListener, when date picker finish, return value and show in showDate EditText
	@Override
	public void onFinishSetDate(String s) {
		// TODO Auto-generated method stub
		showDate.setText(s);
	}

	@Override
	public void onFinishSetTime(String s) {
		// TODO Auto-generated method stub
		showTime.setText(s);
	}	
	
	
	public void showDatePickerDialog(){
		String date = showDate.getText().toString();
		String[] d = date.split("-");
		int year = Integer.parseInt(d[2]);
        int month = Integer.parseInt(d[0]) - 1;
        int day = Integer.parseInt(d[1]);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment pre = fm.findFragmentByTag("dialog");
        if(pre != null){
        	ft.remove(pre);
        }
        ft.addToBackStack(null);
        
        FragmentDatePicker frag = FragmentDatePicker.newInstance(year, month, day, true);
        frag.setTargetFragment(this, 0);
		frag.show(fm, "DatePicker");
	}
	
	public void showTimePickerDialog(){
		//get current time in showTime EditText, for set TimePickerDialog default Time
		String time12 = showTime.getText().toString();
		tf = new TimeFormatTransfer();
		String time24 = tf.hour12to24(time12);
		String[] t = time24.split(":");
		int hour = Integer.parseInt(t[0]);
		int minute = Integer.parseInt(t[1]);
		
		FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment pre = fm.findFragmentByTag("dialog");
        if(pre != null){
        	ft.remove(pre);
        }
        ft.addToBackStack(null);
		FragmentTimePicker frag = FragmentTimePicker.newInstance(hour, minute);
		frag.setTargetFragment(this, 1);
		frag.show(fm, "TimePicker");
	}
}
