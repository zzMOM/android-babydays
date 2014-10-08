package com.example.babydays;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper{

	// Logcat tag
    private static final String LOG = "MySQLiteHelper";
    
	// Database Version
    private static final int DATABASE_VERSION = 3;
    // Database Name
    private static final String DATABASE_NAME = "RoutineDB";
    
    // Table Names
    private static final String TABLE_BABY_ACTIVITIES = "baby_activities";//table 1
    private static final String TABLE_BABY_INFO = "baby_info"; //table 2
    
    // baby_activities Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_TYPE = "type";
    private static final String KEY_INFO = "info";
    private static final String[] COLUMNS = {KEY_ID,KEY_DATE,KEY_TIME,KEY_TYPE,KEY_INFO};
    //baby_user Table Columns' names
    private static final String BABY_ID = "id";
    private static final String BABY_PROFILE_PATH = "path";
    private static final String BABY_NAME = "name";
    private static final String BABY_BIRTH_DATE = "birth_day";
    private static final String BABY_BIRTH_TIME = "birth_time";
    private static final String BABY_BIRTH_HEIGHT = "birth_height";
    private static final String BABY_BIRTH_WEIGHT = "birth_weight";
    
 
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create baby_activities table
    	String CREATE_TABLE_BABY_ACTIVITIES = "CREATE TABLE " + TABLE_BABY_ACTIVITIES + "(" 
    						+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
    						+ KEY_DATE + " TEXT," 
    						+ KEY_TIME + " TEXT," 
    						+ KEY_TYPE + " TEXT,"
    						+ KEY_INFO + " TEXT )";
    	
    	// SQL statement to create baby_user table
    	String CREATE_TABLE_BABY_INFO = "CREATE TABLE " + TABLE_BABY_INFO + "(" 
    						+ BABY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
    						+ BABY_PROFILE_PATH + " TEXT,"
    						+ BABY_NAME + " TEXT," 
    						+ BABY_BIRTH_DATE + " TEXT," 
    						+ BABY_BIRTH_TIME + " TEXT,"
    						+ BABY_BIRTH_HEIGHT + " TEXT,"
    						+ BABY_BIRTH_WEIGHT + " TEXT )";
 
        // create baby_activities table
        db.execSQL(CREATE_TABLE_BABY_ACTIVITIES);
     // create baby_udrt table
        db.execSQL(CREATE_TABLE_BABY_INFO);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older baby_activities table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_BABY_ACTIVITIES);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_BABY_INFO);
        
        // create fresh books table
        this.onCreate(db);
    }
    //---------------------------------------------------------------------
 
    /**
     * CRUD operations (create "add", read "get", update, delete)  + get all  + delete all 
     */
 
    
    
    //insert new babyinfo
    public void addBabyInfo(BabyInfo babyInfo){
    	Log.d("addBabyInfo", babyInfo.toString());
    	
    	// 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(BABY_NAME, babyInfo.getName());
        values.put(BABY_PROFILE_PATH, babyInfo.getProfilePath());
        values.put(BABY_NAME, babyInfo.getName());
        values.put(BABY_BIRTH_DATE, babyInfo.getDate());
        values.put(BABY_BIRTH_TIME, babyInfo.getTime());
        values.put(BABY_BIRTH_HEIGHT, babyInfo.getHeight());
        values.put(BABY_BIRTH_WEIGHT,babyInfo.getWeight());
        
 
        // 3. insert
        db.insert(TABLE_BABY_INFO, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
 
        // 4. close
        db.close();
    }
    
    public BabyInfo getBabyInfo(int id){
    	 
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
 
        // 2. build query
        Cursor cursor = 
                db.query(TABLE_BABY_ACTIVITIES, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections 
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
 
        // 3. if we got results get the first one
        BabyInfo babyInfo = new BabyInfo();
        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
 
	        // 4. build babyinfo object
	        babyInfo.setId(Integer.parseInt(cursor.getString(0)));
	        babyInfo.setProfilePath(cursor.getString(1));
	        babyInfo.setName(cursor.getString(2));
	        babyInfo.setDate(cursor.getString(3));
	        babyInfo.setTime(cursor.getString(4));
	        babyInfo.setHeight(cursor.getString(5));
	        babyInfo.setWeight(cursor.getString(6));
        } else {
        	return null;
        }
 
      //close db and cursor
        db.close();
        // 5. return babyActivity
        return babyInfo;
    }
    
 // Updating single babyActivity
    public int updateBabyInfo(BabyInfo babyInfo) {
 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(BABY_PROFILE_PATH, babyInfo.getProfilePath());
        values.put(BABY_NAME, babyInfo.getName());
        values.put(BABY_BIRTH_DATE, babyInfo.getDate());
        values.put(BABY_BIRTH_TIME, babyInfo.getTime());
        values.put(BABY_BIRTH_HEIGHT, babyInfo.getHeight());
        values.put(BABY_BIRTH_WEIGHT,babyInfo.getWeight());
 
        // 3. updating row
        int i = db.update(TABLE_BABY_INFO, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(babyInfo.getId()) }); //selection args
 
        // 4. close
        db.close();
 
        return i;
 
    }
    
 // Get All Books
    public List<BabyInfo> getAllBabyInfo() {
        List<BabyInfo> babyInfoList = new LinkedList<BabyInfo>();
 
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_BABY_INFO;
 
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
 
        // 3. go over each row, build babyActivity and add it to list
        BabyInfo babyInfo = null;
        if (cursor.moveToFirst()) {
            do {
            	babyInfo = new BabyInfo();
            	babyInfo.setId(Integer.parseInt(cursor.getString(0)));
    	        babyInfo.setProfilePath(cursor.getString(1));
    	        babyInfo.setName(cursor.getString(2));
    	        babyInfo.setDate(cursor.getString(3));
    	        babyInfo.setTime(cursor.getString(4));
    	        babyInfo.setHeight(cursor.getString(5));
    	        babyInfo.setWeight(cursor.getString(6));
 
                // Add babyActivity to babyActivities 
            	babyInfoList.add(babyInfo);
            } while (cursor.moveToNext());
        }
 
      //close db and cursor
        cursor.close();
        db.close();
        // return books
        return babyInfoList;
    }
    
    
    
    
    public void addBabyActivity(BabyActivity babyActivity){
        Log.d("addBook", babyActivity.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, babyActivity.getDate()); // get date 
        values.put(KEY_TIME, babyActivity.getTime()); // get time
        values.put(KEY_TYPE, babyActivity.getType()); // get type
        values.put(KEY_INFO, babyActivity.getInfo()); // get info
        
 
        // 3. insert
        db.insert(TABLE_BABY_ACTIVITIES, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
 
        // 4. close
        db.close(); 
    }
    
    
    public BabyActivity getBabyActivity(int id){
 
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
 
        // 2. build query
        Cursor cursor = 
                db.query(TABLE_BABY_ACTIVITIES, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections 
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
 
        // 3. if we got results get the first one
        BabyActivity babyActivity = new BabyActivity();
        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
 
	        // 4. build babyactivity object
	        babyActivity.setId(Integer.parseInt(cursor.getString(0)));
	        babyActivity.setDate(cursor.getString(1));
	        babyActivity.setTime(cursor.getString(2));
	        babyActivity.setType(cursor.getString(3));
	        babyActivity.setInfo(cursor.getString(4));
        } else {
        	return null;
        }
 
        //Log.d("getBabyActivity("+id+")", babyActivity.toString());
      //close db and cursor
        cursor.close();
        db.close();
        // 5. return babyActivity
        return babyActivity;
    }
 
    // Get All Books
    public List<BabyActivity> getAllBabyActivity() {
        List<BabyActivity> babyActivityList = new LinkedList<BabyActivity>();
 
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_BABY_ACTIVITIES;
 
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
 
        // 3. go over each row, build babyActivity and add it to list
        BabyActivity babyActivity = null;
        if (cursor.moveToFirst()) {
            do {
            	babyActivity = new BabyActivity();
            	babyActivity.setId(Integer.parseInt(cursor.getString(0)));
            	babyActivity.setDate(cursor.getString(1));
            	babyActivity.setTime(cursor.getString(2));
            	babyActivity.setType(cursor.getString(3));
            	babyActivity.setInfo(cursor.getString(4));
 
                // Add babyActivity to babyActivities 
            	babyActivityList.add(babyActivity);
            } while (cursor.moveToNext());
        }
 
        //Log.d("getAllBabyActivity()", babyActivityList.toString());
      //close db and cursor
        cursor.close();
        db.close();
        // return books
        return babyActivityList;
    }
 
     // Updating single babyActivity
    public int updateBabyActivity(BabyActivity babyActivity) {
 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, babyActivity.getDate()); // get date 
        values.put(KEY_TIME, babyActivity.getTime()); // get time
        values.put(KEY_TYPE, babyActivity.getType()); // get type
        values.put(KEY_INFO, babyActivity.getInfo()); // get info
 
        // 3. updating row
        int i = db.update(TABLE_BABY_ACTIVITIES, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(babyActivity.getId()) }); //selection args
 
        // 4. close
        db.close();
 
        return i;
 
    }
 
    // Deleting single babyActivity
    public void deleteBabyActivity(BabyActivity babyActivity) {
 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_BABY_ACTIVITIES,
                KEY_ID+" = ?",
                new String[] { String.valueOf(babyActivity.getId()) });
 
        // 3. close
        db.close();
 
        //Log.d("deleteBabyActivity", babyActivity.toString());
 
    }
    
    // Deleting single babyActivity by ID
    public void deleteBabyActivityByID(String id) {
 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_BABY_ACTIVITIES,
                KEY_ID+" = ?",
                new String[] { id });
 
        // 3. close
        db.close();
 
        //Log.d("deleteBabyActivity", babyActivity.toString());
 
    }
    
    //select record by date
    public List<BabyActivity> getBabyActivityByDate(String date){
    	List<BabyActivity> babyActivityList = new LinkedList<BabyActivity>();
    	 
        // 1. build the query
        /*String query = 	"SELECT * FROM "+ TABLE_BABY_ACTIVITIES +
        				" WHERE " + KEY_DATE + "=" + date;--error*/
 
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        
        Cursor cursor = 
                db.query(TABLE_BABY_ACTIVITIES, // a. table
                COLUMNS, // b. column names
                " date = ?", // c. selections 
                new String[] { date }, // d. selections args
                null, // e. group by
                null, // f. having
                KEY_TIME, // g. order by
                null); // h. limit
 
        // 3. go over each row, build babyActivity and add it to list
        BabyActivity babyActivity = null;
        if (cursor.moveToFirst()) {
            do {
            	babyActivity = new BabyActivity();
            	babyActivity.setId(Integer.parseInt(cursor.getString(0)));
            	babyActivity.setDate(cursor.getString(1));
            	babyActivity.setTime(cursor.getString(2));
            	babyActivity.setType(cursor.getString(3));
            	babyActivity.setInfo(cursor.getString(4));
 
                // Add babyActivity to babyActivities 
            	babyActivityList.add(babyActivity);
            } while (cursor.moveToNext());
        }
 
        //Log.d("getBabyActivityByDate()", babyActivityList.toString());
      //close db and cursor
        cursor.close();
        db.close();
        // return books
        return babyActivityList;
    }
    
    /*get sum diaper and feedmilk by date*/
    public List<String> getTotalByDate(String date){
    	/*0-FeedMilk sum, 1-Diaper count*/
    	List<String> babyActivityList = new LinkedList<String>();
 
        // get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        
        //get FeedMilk total amount -- sum
        //substr(info, -2, -2), 8oz->8, 10oz->10
        String[] newColumns1 = {KEY_TYPE, "SUM(SUBSTR(info, -2, -2)) AS sum"};
        Cursor cursor1 = 
                db.query(TABLE_BABY_ACTIVITIES, // a. table
                newColumns1, // b. column names
                " type = 'FeedMilk' and date = ?", // c. selections 
                new String[] { date }, // d. selections args
                KEY_TYPE, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        
        //get Diaper change times -- count
        String[] newColumns2 = {KEY_TYPE, "COUNT(info) AS count"};
        Cursor cursor2 = 
                db.query(TABLE_BABY_ACTIVITIES, // a. table
                newColumns2, // b. column names
                " type = 'Diaper' and date = ?", // c. selections 
                new String[] { date }, // d. selections args
                KEY_TYPE, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        
      //get Diaper change times -- count
        String[] newColumns3 = {KEY_TYPE, "SUM(SUBSTR(info, 1, 2)) + ROUND(SUM(SUBSTR(info, 4, 2))/60-0.4) AS hour"
        						, "SUM(SUBSTR(info, 4, 2))%60 AS min"};
        Cursor cursor3 = 
                db.query(TABLE_BABY_ACTIVITIES, // a. table
                newColumns3, // b. column names
                " type = 'Nap' and date = ?", // c. selections 
                new String[] { date }, // d. selections args
                KEY_TYPE, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        
        // add sum and count to list
        if (cursor1.moveToFirst()) {
            do {
            	babyActivityList.add("Total milk feed amount: " + cursor1.getString(1));
            } while (cursor1.moveToNext());
        }
        
        if (cursor2.moveToFirst()) {
            do {
            	babyActivityList.add("Total diaper change times: " + cursor2.getString(1));
            } while (cursor2.moveToNext());
        }
        if (cursor3.moveToFirst()) {
            do {
            	babyActivityList.add("Total sleep time: " + cursor3.getString(1) + " hours "
            						+ cursor3.getString(2) + " minutes");
            } while (cursor3.moveToNext());
        }
 
        //Log.d("getTotalByDate()", babyActivityList.toString());
      //close db and cursor
        cursor1.close();
        cursor2.close();
        db.close();
        // return books
        return babyActivityList;
    }
    
    /*select record by date and attributes*/
    public List<BabyActivity> getBabyActivityByDateAttr(String date, String attrs){
    	List<BabyActivity> babyActivityList = new LinkedList<BabyActivity>();
    	 
        // 1. build the query
        /*String query = 	"SELECT * FROM "+ TABLE_BABY_ACTIVITIES +
        				" WHERE " + KEY_DATE + "=" + date;--error*/
 
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        
        String whereClause = " date = ? and type = ?";
        String[] whereArgs = new String[] {
        	    date,
        	    attrs
        	};
        
        
        Cursor cursor = 
                db.query(TABLE_BABY_ACTIVITIES, // a. table
                COLUMNS, // b. column names
                whereClause, // c. selections 
                whereArgs, // d. selections args
                null, // e. group by
                null, // f. having
                KEY_TIME, // g. order by
                null); // h. limit
 
        // 3. go over each row, build babyActivity and add it to list
        BabyActivity babyActivity = null;
        if (cursor.moveToFirst()) {
            do {
            	babyActivity = new BabyActivity();
            	babyActivity.setId(Integer.parseInt(cursor.getString(0)));
            	babyActivity.setDate(cursor.getString(1));
            	babyActivity.setTime(cursor.getString(2));
            	babyActivity.setType(cursor.getString(3));
            	babyActivity.setInfo(cursor.getString(4));
 
                // Add babyActivity to babyActivities 
            	babyActivityList.add(babyActivity);
            } while (cursor.moveToNext());
        }
 
        //Log.d("getBabyActivityByDate()", babyActivityList.toString());
      //close db and cursor
        cursor.close();
        db.close();
        // return books
        return babyActivityList;
    }
    
    /*select record by tpye*/
    public List<BabyActivity> getBabyActivityByType(String[] type){
    	List<BabyActivity> babyActivityList = new LinkedList<BabyActivity>();
    	 
        // 1. build the query
        /*String query = 	"SELECT * FROM "+ TABLE_BABY_ACTIVITIES +
        				" WHERE " + KEY_DATE + "=" + date;--error*/
 
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < type.length; i++){
        	if(i == 0){
        		builder.append("type='").append(type[i]).append("'");
        	} else {
        		builder.append(" or type='").append(type[i]).append("'");
        	}
        }
        //String whereClause = builder.toString();
        //String[] whereArgs = type;
        String q = "select * from " + TABLE_BABY_ACTIVITIES + " where " + builder.toString() 
        			+ " order by substr(date,7,4)||'-'||substr(date,1,2)||'-'||substr(date,4,2)";
        Cursor cursor = db.rawQuery(q, null);
        /*Cursor cursor = 
                db.query(TABLE_BABY_ACTIVITIES, // a. table
                COLUMNS, // b. column names
                whereClause, // c. selections 
                whereArgs, // d. selections args
                null, // e. group by
                null, // f. having
                KEY_DATE, // g. order by
                null); // h. limit*/
 
        // 3. go over each row, build babyActivity and add it to list
        BabyActivity babyActivity = null;
        if (cursor.moveToFirst()) {
            do {
            	babyActivity = new BabyActivity();
            	babyActivity.setId(Integer.parseInt(cursor.getString(0)));
            	babyActivity.setDate(cursor.getString(1));
            	babyActivity.setTime(cursor.getString(2));
            	babyActivity.setType(cursor.getString(3));
            	babyActivity.setInfo(cursor.getString(4));
 
                // Add babyActivity to babyActivities 
            	babyActivityList.add(babyActivity);
            } while (cursor.moveToNext());
        }
 
        //Log.d("getBabyActivityByDate()", babyActivityList.toString());
      //close db and cursor
        cursor.close();
        db.close();
        // return books
        return babyActivityList;
    }
    
    /*select record by tpye and output date list*/
    public List<String> getDateListByType(String type){
    	List<String> datelist = new ArrayList<String>();
    	 
        // 1. build the query
        /*String query = 	"SELECT * FROM "+ TABLE_BABY_ACTIVITIES +
        				" WHERE " + KEY_DATE + "=" + date;--error*/
 
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        
        String[] columnNames = new String[]{KEY_DATE};
        String whereClause = "type = ?";
        String[] whereArgs = new String[] {
        	    type
        	};
        
        
        boolean distinct = true;//distinct
		Cursor cursor = 
                db.query(distinct, 
                TABLE_BABY_ACTIVITIES, // a. table
                columnNames, // b. column names
                whereClause, // c. selections 
                whereArgs, // d. selections args
                null, // e. group by
                null, // f. having
                "strftime('%Y-%m-%d', date)", // g. order by
                null); // h. limit
 
        // 3. go over each row, and add it to list
        if (cursor.moveToFirst()) {
            do {
                // Add babyActivity to babyActivities 
            	datelist.add(cursor.getString(0).toString());
            } while (cursor.moveToNext());
        }
 
        //Log.d("getDateList", datelist.toString());
        //close db and cursor
        cursor.close();
        db.close();
        // return books
        return datelist;
    }
    
}