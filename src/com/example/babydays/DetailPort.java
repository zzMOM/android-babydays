package com.example.babydays;

import java.util.ArrayList;
import java.util.List;

import android.support.annotation.Nullable;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.os.Build;

public class DetailPort extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_port);
		
		/*if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			// If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
			finish();
			return;
		}
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.detailFragPort, new DetailPortFragment()).commit();
		}*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail_port, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class DetailPortFragment extends Fragment {
		private TextView memoryText;
		private List<BabyActivity> resultActivities;
		private MySQLiteHelper dbHelper;
		private Spinner memoryBookSpinner;
		private ArrayList<String> spinnerlist;

		public DetailPortFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_detail_port,
					container, false);
			
			//search from database
			dbHelper = new MySQLiteHelper(getActivity());
		    
		    memoryText = (TextView) rootView.findViewById(R.id.memoryText);
		    //set up memoryBookSpinner
	  		memoryBookSpinner = (Spinner) rootView.findViewById(R.id.memoryBookSpinner);
	  		spinnerlist = new ArrayList<String>();//spinner value list
	  		spinnerlist.add("Milestone");
	  		spinnerlist.add("Diary");
	  		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_text, spinnerlist);
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
			
			return rootView;
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
}
