package com.example.babydays;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.babydays.FragmentDatePicker.DatePickerDialogListener;

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
import android.widget.LinearLayout;


public class FragmentHeightDialog  extends DialogFragment implements DatePickerDialogListener{
	private ViewStub dateTimeStub;
	private ImageButton pickDate;;
	private SimpleDateFormat df;
	private Calendar c;
	private EditText showDate, feetText, inchText;
	
	public interface HeightDialogListener {
		void onFinishSetHeight(String date, String time, String type, String info);
	}
	
	public static FragmentHeightDialog newInstance(String date, String info) {
		FragmentHeightDialog frag = new FragmentHeightDialog();
        Bundle args = new Bundle();
        args.putString("date", date);
        args.putString("info", info);
        frag.setArguments(args);
        return frag;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Create custom dialog object
        final Dialog dialog = new Dialog(getActivity());
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_height);
        // Set dialog title
        dialog.setTitle("Insert a new height");
        
        dateTimeStub = (ViewStub) dialog.findViewById(R.id.dateTimeStub);
        dateTimeStub.setLayoutResource(R.layout.date_time_merge);
        feetText = (EditText) dialog.findViewById(R.id.feet);
        inchText = (EditText) dialog.findViewById(R.id.inch);
        View inflatedView = dateTimeStub.inflate();
        setDateTimeMergePart(inflatedView);

        Button okButton = (Button) dialog.findViewById(R.id.ok);
        // if decline button is clicked, close the custom dialog
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "Height";
                String info = "";
                int f = feetText.getText().toString().length();
                int i = inchText.getText().length();
                if(f > 0 || i > 0){
                	info = (f == 0 ? 0 + "" : feetText.getText().toString()) + "feet"
                			+ (i == 0 ? 0 + "" : inchText.getText().toString()) + "inch";
                	//insertCurrentActivity(type, info);
                	HeightDialogListener listener = (HeightDialogListener) getActivity();
                	listener.onFinishSetHeight(showDate.getText().toString()
                							, "", type, info);
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
        showDate = (EditText) inflatedView.findViewById(R.id.showDate);
        dateTimeStub.setVisibility(View.VISIBLE);
        LinearLayout timell = (LinearLayout) inflatedView.findViewById(R.id.timell);
        timell.setVisibility(View.GONE);
        
        c = Calendar.getInstance();
        df = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        
        String date = getArguments().getString("date");
		String info = getArguments().getString("info");
		
		//set initial value for dialog
        String formattedDate = df.format(c.getTime());
        String[] s = formattedDate.split(" ");
        if(date.length() == 0){//set showDate current date
        	showDate.setText(s[0]);
        } else {//set showDate specific value
        	showDate.setText(date);
        }
        
        if(info.length() > 0){//set info sepcific value
        	feetText.setText(info.split("feet")[0]);
        	inchText.setText(info.split("feet")[1].split("inch")[0]);
        }
        
        
        pickDate = (ImageButton) inflatedView.findViewById(R.id.pickDate);
        
        //pickDate button and pickTime button
        pickDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDatePickerDialog();
			}
		});
	}

	//DatePickerDialogListener, when date picker finish, return value and show in showDate EditText
	@Override
	public void onFinishSetDate(String s) {
		// TODO Auto-generated method stub
		showDate.setText(s);
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

}
