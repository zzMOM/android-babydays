package com.example.babydays;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormatTransfer {
	
	TimeFormatTransfer(){}
	
	//time is 24hour format, show with 12hour format
	//date transfer 24hours to 12hours
	public String hour24to12(String time24){
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
	
	public String hour12to24(String time12){
		String time24 = "";

		SimpleDateFormat h_mm_a   = new SimpleDateFormat("h:mma");
		SimpleDateFormat hh_mm_ss = new SimpleDateFormat("HH:mm:ss");

		try {
		    Date t = h_mm_a.parse(time12);
		    time24 = hh_mm_ss.format(t);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return time24;
	}
	
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
