package com.example.babydays;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import java.util.*;


public class BabyDays extends Activity {
	//declare TimerTask variable
	private TimerTask delayTask;
	//declare Timer variable
	private Timer myTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baby_days);
		
		myTimer = new Timer();
        delayTask = new TimerTask(){

			@Override
			public void run() {
		
				//define a new Intent for the second Activity
				Intent intent = new Intent(BabyDays.this, MainMenu.class);
				//start Activity
				startActivity(intent);
			}
        };
        myTimer.schedule(delayTask, 2000);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.baby_days, menu);
		return true;
	}

}
