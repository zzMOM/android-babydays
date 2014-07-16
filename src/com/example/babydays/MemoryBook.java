package com.example.babydays;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MemoryBook extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.memory_book);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.memory_book, menu);
		return true;
	}

}
