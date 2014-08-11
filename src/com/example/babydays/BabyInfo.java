package com.example.babydays;

public class BabyInfo {
	private int id;
	private String profilePath;
	private String name;
	private String date;
	private String time;
	private String height;
	private String weight;
	
	public BabyInfo(){}
	
	public BabyInfo(String profilePath, String name, String date, String time, String height, String weight){
		super();
		this.profilePath = profilePath;
		this.name = name;
		this.date = date;
		this.time = time;
		this.height = height;
		this.weight = weight;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setProfilePath(String path){
		this.profilePath = path;
	}
	
	public String getProfilePath(){
		return profilePath;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setDate(String date){
		this.date = date;
	}
	
	public String getDate(){
		return date;
	}
	
	public void setTime(String time){
		this.time = time;
	}
	
	public String getTime(){
		return time;
	}
	
	public void setHeight(String height){
		this.height = height;
	}
	
	public String getHeight(){
		return height;
	}
	
	public void setWeight(String weight){
		this.weight = weight;
	}
	
	public String getWeight(){
		return weight;
	}
	
	public String toString() {
        return "[id=" + id + ", path=" + profilePath + ", name=" + name + ", date=" + date + ", time=" + time
                + ", height=" + height + ", weight=" + weight + "]";
    }
}
