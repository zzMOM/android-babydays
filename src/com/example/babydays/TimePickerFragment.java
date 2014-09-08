package com.example.babydays;


import android.app.TimePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

	public interface TimePickerDialogListener {
	    void onFinishSetTime(String s);
	}
	
	public static TimePickerFragment newInstance(int hour, int minute) {
		TimePickerFragment frag = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        frag.setArguments(args);
        return frag;
	}
	
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int hour = getArguments().getInt("hour");
        int minute = getArguments().getInt("minute");

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, false);
    }


	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		TimeFormatTransfer tf = new TimeFormatTransfer();
		String s = tf.timeFormat24To12(hourOfDay, minute);
		
		TimePickerDialogListener listener = (TimePickerDialogListener) getTargetFragment();
		listener.onFinishSetTime(s);
	}
}
