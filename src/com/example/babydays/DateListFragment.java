package com.example.babydays;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


public class DateListFragment extends Fragment implements OnItemClickListener{
	protected List<String> datelist, spinnerlist;
	protected ListView dateListView;
	private ArrayAdapter<String> adapter;
	protected Spinner memoryDateListSpinner;
	private String spinnerValue;
	private boolean mDualPane;
    private int mCurCheckPosition = 0;

    
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.fragment_date_list, container, false);
		
		//dateListView
		dateListView = (ListView) view.findViewById(R.id.dateListView);
		datelist = new ArrayList<String>();
		datelist.add("123");
		adapter = new ArrayAdapter<String>(getActivity(), 
					android.R.layout.simple_list_item_1, datelist);
		dateListView.setAdapter(adapter);
	    
	    //set up memoryBookSpinner
  		memoryDateListSpinner = (Spinner) view.findViewById(R.id.memoryDateListSpinner);
  		spinnerlist = new ArrayList<String>();//spinner value list
  		spinnerlist.add("Milestone");
  		spinnerlist.add("Diary");
  		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, spinnerlist);
  		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
  		memoryDateListSpinner.setAdapter(adapter);
  		
  		memoryDateListSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

  			@Override
  			public void onItemSelected(AdapterView<?> parent, View view, 
  		            int pos, long id) {
  				// TODO Auto-generated method stub
  				String type = spinnerlist.get(pos);
  				MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
  				List<String> list = dbHelper.getDateListByType(type); 				
  				setDatelist(list);
  				showDetails(mCurCheckPosition);
  			}

  			@Override
  			public void onNothingSelected(AdapterView<?> arg0) {
  				// TODO Auto-generated method stub
  				
  			}
  		});
  		
	    
	    return view;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            DetailLandFragment details = new DetailLandFragment();
            getFragmentManager().beginTransaction().add(R.id.detailFragLand, details).commit();
        }
		
		if(this.dateListView != null){
			this.dateListView.setOnItemClickListener(this);
			this.dateListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
		
		// Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailFrag = getActivity().findViewById(R.id.detailFragLand);
		mDualPane =  detailFrag != null && detailFrag.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
        	dateListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            
            
        }
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("curChoice", mCurCheckPosition);
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		showDetails(position);
	}
	
	private void showDetails(int mCurCheckPosition){
		if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
			dateListView.setItemChecked(mCurCheckPosition, true);

            // Check what fragment is currently shown, replace if needed.
            DetailLandFragment details = (DetailLandFragment)
                    getFragmentManager().findFragmentById(R.id.detailFragLand);
            if (details == null || mCurCheckPosition == 0 || !details.getSelectedDate().equals(datelist.get(mCurCheckPosition))) {
                // Make new fragment to show this selection.
                details = DetailLandFragment.newInstance(datelist.get(mCurCheckPosition), 
                		spinnerlist.get(memoryDateListSpinner.getSelectedItemPosition()));
                
                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.detailFragLand, details);
                
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        }
    }
	
	private void setDatelist(List<String> list){
		datelist = list;
		//update listview
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, datelist);
		dateListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		//dateListView.invalidateViews();
	}
	
}
