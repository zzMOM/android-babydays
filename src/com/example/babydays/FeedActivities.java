package com.example.babydays;

public class FeedActivities {
	int id;
	String date;
	String time;
	int amount;
	
	//constructors
	FeedActivities(){}
	
	public FeedActivities(String date, String time, int amount){
		this.date = date;
		this.time = time;
		this.amount = amount;
	}
	
	//setters
	public void setDate(String date){
		this.date = date;
	}
	
	public void setTime(String time){
		this.time = time;
	}
	
	public void setAmount(int amount){
		this.amount = amount;
	}
	
	//getter
	public int getId(){
		return id;
	}
	public String getDate(){
		return date;
	}
	
	public String getTime(){
		return time;
	}
	
	public int getAmount(){
		return amount;
	}
}
