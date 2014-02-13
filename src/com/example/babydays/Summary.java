package com.example.babydays;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class Summary extends Activity {

	private MySQLiteHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.summary);
		
		//create database helper
		dbHelper = new MySQLiteHelper(this);
		List<BabyActivity> routine = dbHelper.getAllBabyActivity();
		
		TextView summaryText = (TextView)findViewById(R.id.summaryText);
		
		for(int i = 0; i < routine.size(); i++){
			summaryText.append(routine.get(i).toString());
			summaryText.append("\n");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.summary, menu);
		return true;
	}

}
