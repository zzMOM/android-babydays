package com.example.babydays;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.babydays.DatePickerFragment.DatePickerDialogListener;
import com.example.babydays.TimePickerFragment.TimePickerDialogListener;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class FeedDialogFragment extends DialogFragment implements DatePickerDialogListener
																 ,TimePickerDialogListener{
	private ViewStub dateTimeStub;
	private ImageButton pickDate, pickTime;
	private EditText showDate, showTime;
	private SimpleDateFormat df;
	private Calendar c;
	private EditText textOZ;
	
	private TimeFormatTransfer tf;
	private int clicktype;
	
	public interface FeedDialogListener {
	    void onFinishSetFeed(String date, String time, String type, String info);
	}
	
	public static FeedDialogFragment newInstance(int clicktype, String date, String time, String info) {
		FeedDialogFragment frag = new FeedDialogFragment();
        Bundle args = new Bundle();
        args.putInt("clicktype", clicktype);
        args.putString("date", date);
        args.putString("time", time);
        args.putString("info", info);
        frag.setArguments(args);
        return frag;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		clicktype = getArguments().getInt("clicktype");
		
		// Create custom dialog object
        final Dialog dialog = new Dialog(getActivity());
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_feed);
        // Set dialog title
        if(clicktype == 0){
        	dialog.setTitle("Time to feed!");
        } else {
        	dialog.setTitle("Insert a new feed activity");
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
                String type = "FeedMilk";
                String info = "";
                if(textOZ.getText().toString().length() > 0){
                	info = textOZ.getText().toString() + "oz";
                	//insertCurrentActivity(type, info);
                	FeedDialogListener listener = (FeedDialogListener) getActivity();
                	listener.onFinishSetFeed(showDate.getText().toString()
                							, showTime.getText().toString(), type, info);
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
        
        return dialog;
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
        	textOZ.setText(info.substring(0, info.length() - 2));
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
        
        DatePickerFragment frag = DatePickerFragment.newInstance(year, month, day, true);
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
		TimePickerFragment frag = TimePickerFragment.newInstance(hour, minute);
		frag.setTargetFragment(this, 1);
		frag.show(fm, "TimePicker");
	}

}
