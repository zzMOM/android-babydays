package com.example.babydays;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.*;

/**
 * The first welcome page
 * @author weiwu
 *
 */
public class BabyDays extends Activity {
	//declare TimerTask variable
	private TimerTask delayTask;
	//declare Timer variable
	private Timer myTimer;
	private ImageView profileImageFirst;
	private MySQLiteHelper dbHelper;
	private BabyInfo babyInfo;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baby_days);
		
		profileImageFirst = (ImageView) findViewById(R.id.profileImageFirst);
		
		dbHelper = new MySQLiteHelper(this);
		List<BabyInfo> babyInfoList = dbHelper.getAllBabyInfo();
		//if babyInfoList size is 0, initialize null and insert column
		if(babyInfoList.size() == 0){
			babyInfo = new BabyInfo("","","","","","");
			dbHelper.addBabyInfo(babyInfo);
		} 
		
		babyInfoList = dbHelper.getAllBabyInfo();
		babyInfo = babyInfoList.get(0);
		//if babyInfo path value not null, load saved image
		if(babyInfo.getProfilePath().length() > 0){
			String path = babyInfo.getProfilePath();
		    BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
	        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
	        profileImageFirst.setImageBitmap(bitmap);
		}
		
		myTimer = new Timer();
        delayTask = new TimerTask(){

			@Override
			public void run() {
		
				//define a new Intent for MainMenu Activity
				Intent intent = new Intent(BabyDays.this, MainMenuCard.class);
				//start Activity
				startActivity(intent);
			}
        };
        //delay 1 second
        myTimer.schedule(delayTask, 1000);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.baby_days, menu);
		return true;
	}

}
