package com.example.babydays;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
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
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baby_days);
		
		profileImageFirst = (ImageView) findViewById(R.id.profileImageFirst);
		
		Profile p = new Profile();
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String path = null;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        profileImageFirst.setImageBitmap(bitmap);
		
		myTimer = new Timer();
        delayTask = new TimerTask(){

			@Override
			public void run() {
		
				//define a new Intent for MainMenu Activity
				Intent intent = new Intent(BabyDays.this, MainMenu.class);
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
