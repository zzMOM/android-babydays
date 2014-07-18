package com.example.babydays;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MemoryBook extends Activity {
	private Spinner memoryBookSpinner;
	private TextView memoryText;
	private MySQLiteHelper dbHelper;
	private ArrayList<String> spinnerlist;
	private List<BabyActivity> resultActivities;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.memory_book);
		
		//get database
		dbHelper = new MySQLiteHelper(this);
		resultActivities = new ArrayList<BabyActivity>();
		
		//textView
		memoryText = (TextView) findViewById(R.id.memoryText);
		
		//set up memoryBookSpinner
		memoryBookSpinner = (Spinner) findViewById(R.id.memoryBookSpinner);
		spinnerlist = new ArrayList<String>();//spinner value list
		spinnerlist.add("Milestone");
		spinnerlist.add("Diary");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerlist);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		memoryBookSpinner.setAdapter(adapter);
		
		memoryBookSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, 
		            int pos, long id) {
				// TODO Auto-generated method stub
				String type = spinnerlist.get(pos);
				resultActivities = dbHelper.getBabyActivityByType(type);
				printResult(resultActivities);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.memory_book, menu);
		return true;
	}
	
	private void printResult(List<BabyActivity> resultActivities){
		//clear 
		memoryText.setText("");
		
		//String date
		String lastdate = "";
		String curdate = "";
		
		for(int i = 0; i < resultActivities.size(); i++){
			curdate = resultActivities.get(i).getDate().toString();
			if(!curdate.equals(lastdate) && i != 0){
				memoryText.append("\n\n");
			}
			memoryText.append( curdate + "\t\t");
			memoryText.append(resultActivities.get(i).getTime().toString() + "\n");
			memoryText.append(resultActivities.get(i).getInfo().toString() + "\n");
			
			lastdate = curdate;
		}
	}

}
