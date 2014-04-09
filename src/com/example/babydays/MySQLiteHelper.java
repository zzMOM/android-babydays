package com.example.babydays;

import java.sql.Date;
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
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "RoutineDB";
    
    // Table Names
    private static final String TABLE_BABY_ACTIVITIES = "baby_activities";
    
    // baby_activities Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_TYPE = "type";
    private static final String KEY_INFO = "info";
    private static final String[] COLUMNS = {KEY_ID,KEY_DATE,KEY_TIME,KEY_TYPE,KEY_INFO};
    
 
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
    	
    	/*String CREATE_TABLE_BABY_ACTIVITIES = "CREATE TABLE baby_activities ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                "date TEXT, "+
                "time TEXT, "+
                "type TEXT, "+
                "info   TEXT )";*/
 
        // create baby_activities table
        db.execSQL(CREATE_TABLE_BABY_ACTIVITIES);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older baby_activities table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BABY_ACTIVITIES);
 
        // create fresh books table
        this.onCreate(db);
    }
    //---------------------------------------------------------------------
 
    /**
     * CRUD operations (create "add", read "get", update, delete)  + get all  + delete all 
     */
 
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
        if (cursor != null)
            cursor.moveToFirst();
 
        // 4. build babyactivity object
        BabyActivity babyActivity = new BabyActivity();
        babyActivity.setId(Integer.parseInt(cursor.getString(0)));
        babyActivity.setDate(cursor.getString(1));
        babyActivity.setTime(cursor.getString(2));
        babyActivity.setType(cursor.getString(3));
        babyActivity.setInfo(cursor.getString(4));
 
        Log.d("getBabyActivity("+id+")", babyActivity.toString());
 
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
 
        Log.d("getAllBabyActivity()", babyActivityList.toString());
 
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
 
        Log.d("deleteBabyActivity", babyActivity.toString());
 
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
                null, // g. order by
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
 
        Log.d("getBabyActivityByDate()", babyActivityList.toString());
 
        // return books
        return babyActivityList;
    }
    
    //select record by date and attributes
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
                null, // g. order by
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
 
        Log.d("getBabyActivityByDate()", babyActivityList.toString());
 
        // return books
        return babyActivityList;
    }
    

}