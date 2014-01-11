package com.example.babydays;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	//Logcat tag
	private static final String LOG = "DatabaseHelper";
	
	//Database Version
	private static final int DATABASE_VERSION = 3;
	
	//Database Name
	private static final String DATABASE_NAME = "activitiesManager";
	
	//Table Names
	private static final String TABLE_FEED = "feed";
	
	//FEED TABLE column names
	private static final String FEED_ID = "id";
	private static final String FEED_DATE = "date";
	private static final String FEED_TIME = "time";
	private static final String FEED_AMOUNT = "amount";
	
	//Table create statements
	//FEED table create statements
	private static final String CREATE_TABLE_FEED = "CREATE TABLE" + TABLE_FEED + "(" + FEED_ID
			+ " INTEGER PRIMARY KEY," + FEED_DATE + " TEXT," + FEED_TIME + " TEXT," + FEED_AMOUNT
			+ " INTEGER" + ")";
	
	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		//creating required tables
		db.execSQL(CREATE_TABLE_FEED);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		//on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEED);
		
		//create new tables
		onCreate(db);
	}
	
	//add feed activities
	public void addFeedActivities(FeedActivities feedAct){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(FEED_DATE, feedAct.getDate());
		values.put(FEED_TIME, feedAct.getTime());
		values.put(FEED_AMOUNT, feedAct.getAmount());
		
		//Inserting Row
		db.insert(TABLE_FEED, null, values);
		db.close();
	}
	
	//Getting selected items
	public List<FeedActivities> getFeedActivities(int date){
		List<FeedActivities> feedActList= new ArrayList<FeedActivities>();
		//select query where FeedActivities_date = date
		String selectQuery = "SELECT * FROM " + TABLE_FEED + " WHERE" + FEED_DATE + " = " + date;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		//looping through all rows and adding to list
		if(cursor.moveToFirst()){
			do{
				FeedActivities feedAct = new FeedActivities();
				feedAct.setDate(cursor.getString(1));
				feedAct.setTime(cursor.getString(2));
				feedAct.setAmount(Integer.parseInt(cursor.getString(3)));
				//adding to list
				feedActList.add(feedAct);
			} while(cursor.moveToNext());
		}
		return feedActList;
	}
	
	//Getting all items
	public List<FeedActivities> getAllFeedActivities(){
		List<FeedActivities> feedActList= new ArrayList<FeedActivities>();
		//select query where FeedActivities_date = date
		String selectQuery = "SELECT * FROM " + TABLE_FEED;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		//looping through all rows and adding to list
		if(cursor.moveToFirst()){
			do{
				FeedActivities feedAct = new FeedActivities();
				feedAct.setDate(cursor.getString(1));
				feedAct.setTime(cursor.getString(2));
				feedAct.setAmount(Integer.parseInt(cursor.getString(3)));
				//adding to list
				feedActList.add(feedAct);
			} while(cursor.moveToNext());
		}
		return feedActList;
	}
	
	//updating an item
	public int updateFeedActivities(FeedActivities feedAct){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(FEED_DATE, feedAct.getDate());
		values.put(FEED_TIME, feedAct.getTime());
		values.put(FEED_AMOUNT, feedAct.getAmount());
		
		//updating row
		return db.update(TABLE_FEED, values, FEED_ID + " = ?",
				new String[] {String.valueOf(feedAct.getId())});
	}
	
	//deleting single item
	public void deleteFeedActivities(FeedActivities feedAct){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_FEED, FEED_ID + " = ?",
				new String[] {String.valueOf(feedAct.getId())});
		db.close();
	}
}
