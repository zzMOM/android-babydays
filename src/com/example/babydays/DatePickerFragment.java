package com.example.babydays;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	public interface DatePickerDialogListener {
	    void onFinishSetDate(String s);
	}
	
	public static DatePickerFragment newInstance(int year, int month, int day) {
		DatePickerFragment frag = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        frag.setArguments(args);
        return frag;
	}
	
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int year = getArguments().getInt("year");
        int month = getArguments().getInt("month");
        int day = getArguments().getInt("day");

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


	@Override
	public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
		// Do something with the date chosen by the user
		// set selected date into textview
		String m, d;
		m = selectedMonth + 1 < 10? "0" + (selectedMonth + 1) : (selectedMonth + 1) + "";
		d = selectedDay < 10? "0" + selectedDay : selectedDay + "";
		String s = new StringBuilder().append(m)
					   .append("-").append(d).append("-").append(selectedYear)
					   .toString();
		DatePickerDialogListener listener = (DatePickerDialogListener) getTargetFragment();
		listener.onFinishSetDate(s);
	}
}
