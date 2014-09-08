package com.example.babydays;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.example.babydays.DatePickerFragment.DatePickerDialogListener;
import com.example.babydays.TimePickerFragment.TimePickerDialogListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class MilestoneDialogFragment extends DialogFragment implements DatePickerDialogListener
																	,TimePickerDialogListener{
	private ArrayList<Integer> mSelectedItems;
	private ImageButton pickDate, pickTime;
	private EditText showDate, showTime;
	private ViewStub dateTimeStub;
	private TimeFormatTransfer tf;
	private SimpleDateFormat df;
	private Calendar c;
	private int clicktype;
	
	public interface MilestoneDialogListener {
	    void onFinishSetMilestone(String date, String time, String type, String info);
	}
	
	public static MilestoneDialogFragment newInstance(int clicktype) {
		MilestoneDialogFragment frag = new MilestoneDialogFragment();
        Bundle args = new Bundle();
        args.putInt("clicktype", clicktype);
        frag.setArguments(args);
        return frag;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		mSelectedItems = new ArrayList<Integer>();
		//get clicktype from bundle
		clicktype = getArguments().getInt("clicktype");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Set dialog title
        if(clicktype == 0){
        	builder.setTitle("Milestone!");
        } else {
        	builder.setTitle("Insert a new milestone!");
        }
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
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
	                   
	                   StringBuffer info = new StringBuffer();
	                   Resources res = getResources();
	                   String[] milestoneSelectedItems = res.getStringArray(R.array.mileStone);
	                   for(int i = 0; i < mSelectedItems.size(); i++){
	                	   info.append(milestoneSelectedItems[(Integer) mSelectedItems.get(i)] + " ");
	                   }
	                   if(info.toString() != ""){
	                	   MilestoneDialogListener listener = (MilestoneDialogListener) getActivity();
	                   	   listener.onFinishSetMilestone(showDate.getText().toString()
	                   							       , showTime.getText().toString(), type, info.toString());
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
	    
        return dialog;
	}
	
	private void setDateTimeMergePart(View inflatedView){
		// set values for date_time_merge.xml components
		//use inflatedView to get view from viewstub
        showTime = (EditText) inflatedView.findViewById(R.id.showTime);
        showDate = (EditText) inflatedView.findViewById(R.id.showDate);
        c = Calendar.getInstance();
        df = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        String formattedDate = df.format(c.getTime());
        String[] s = formattedDate.split(" ");
        showDate.setText(s[0]);
        String[] st = s[1].split(":");
        tf = new TimeFormatTransfer();
		showTime.setText(tf.timeFormat24To12(Integer.parseInt(st[0]), Integer.parseInt(st[1])));
        
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
        
        DatePickerFragment frag = DatePickerFragment.newInstance(year, month, day);
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
