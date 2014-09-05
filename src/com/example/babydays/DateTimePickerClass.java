package com.example.babydays;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class DateTimePickerClass {
	private String dateText = "";
	private String timeText = "";
	
	DateTimePickerClass(){}
	
	public void setDate(String s){
		dateText = s;
	}
	
	public void setTime(String s){
		timeText = s;
	}
	
	public String getDate(){
		return dateText;
	}
	
	public String getTime(){
		return timeText;
	}

	public DatePickerDialog.OnDateSetListener datePickerListener 
		= new DatePickerDialog.OnDateSetListener() {
	
		@Override
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
			// set selected date into textview
			String m, d;
			m = selectedMonth + 1 < 10? "0" + (selectedMonth + 1) : (selectedMonth + 1) + "";
			d = selectedDay < 10? "0" + selectedDay : selectedDay + "";
			String s = new StringBuilder().append(m)
						   .append("-").append(d).append("-").append(selectedYear)
						   .toString();
			setDate(s);
		}
	};
	
	
	
	public TimePickerDialog.OnTimeSetListener timePickerListener
		= new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// set selected time to textview
			String s = timeFormat24To12(hourOfDay, minute);
			setTime(s);
		}
	};
	
	public String timeFormat24To12(int hour, int minute){
		String a_p = "";
		if(hour > 12){
			hour -= 12;
			a_p = "PM";
		} else {
			a_p = "AM";
		}
		String h, m;
		h = hour < 10? "0" + hour : hour + "";
		m = minute < 10? "0" + minute : minute + "";
		return new StringBuilder().append(h).append(":").append(m).append(a_p).toString();
	}
	
	public String timeFormat12To24(int hour, int minute, String a_p){
		if(a_p.equals("PM")){
			hour += 12;
		} 
		String h, m;
		h = hour < 10? "0" + hour : hour + "";
		m = minute < 10? "0" + minute : minute + "";
		return new StringBuilder().append(h).append(":").append(m).toString();
	}
}
