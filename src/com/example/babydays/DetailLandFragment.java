package com.example.babydays;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailLandFragment extends Fragment {
	private TextView memoryText;
	//private List<BabyActivity> resultActivities;
	private MySQLiteHelper dbHelper;
	private static boolean isUpdate = false;

	
	/**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static DetailLandFragment newInstance(String date, String type) {
    	isUpdate = true;
        DetailLandFragment f = new DetailLandFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("date", date);
        args.putString("type", type);
        f.setArguments(args);

        return f;
    }
    
    public String getSelectedDate() {
        return getArguments().get("date").toString();
    }
    
    public String getSelectedType(){
    	return getArguments().get("type").toString();
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_detail_land, container, false);
	    dbHelper = new MySQLiteHelper(getActivity());
	    memoryText = (TextView) view.findViewById(R.id.memoryText);
	    
	    if(!isUpdate){
	    	return view;
	    }
	    
	    List<BabyActivity> resultActivities = dbHelper.getBabyActivityByDateAttr(getSelectedDate(), getSelectedType());
		printResult(resultActivities);
		
		Log.e("detail", "onCreateView");
	    return view;
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
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		Log.e("detail", "onAttach");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.e("detail", "onCreate");
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.e("detail", "onStart");
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("detail", "onResume");
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e("detail", "onStop");
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("detail", "onDestroy");
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e("detail", "onPause");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.e("detail", "onActivityCreated");
	}
	
}
