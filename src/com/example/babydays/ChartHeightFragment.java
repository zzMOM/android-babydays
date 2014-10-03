package com.example.babydays;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ChartHeightFragment extends Fragment{
	private MySQLiteHelper dbHelper;
	private List<BabyActivity> routine;
	
	
	ChartHeightFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_chart_height,
				container, false);
		
		//create database helper
		dbHelper = new MySQLiteHelper(getActivity());
		
		populateGraphView(rootView);
		
		return rootView;
	}
	
	private void populateGraphView(View view){
		/*SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		
		routine = dbHelper.getBabyActivityByType("Height");
		int n = routine.size();
		String birthday = dbHelper.getBabyInfo(0).getDate().toString();
		if(birthday.length() == 0 && n == 0){
			return;
		} else if(birthday.length() == 0){
			birthday = routine.get(0).getDate().toString();
		}
		
		GraphViewData[] data = new GraphViewData[n];
		String[] horizontalLabel = new String[n];
		String[] verticalLabel = new String[n];
		
		for(int i = 0; i < n; i++){
			horizontalLabel[i] = routine.get(i).getDate().toString();
			verticalLabel[i] = routine.get(i).getInfo().toString();
			double diff = 0.0;
			double value = 0.0;
			try{
				Date date1 = df.parse(birthday);
				Date date2 = df.parse(horizontalLabel[i]);
				diff = date2.getDate() - date1.getDate();
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String feet = verticalLabel[i].split("feed")[0];
			String inch = verticalLabel[i].split("feed")[1].split("inch")[0];
			value = Integer.parseInt(feet) + Integer.parseInt(inch) / 12;
			data[i] = new GraphViewData(diff, value);
		}
		GraphViewSeries series = new GraphViewSeries(data);*/
		GraphViewSeries series = new GraphViewSeries(new GraphViewData[] {
			    new GraphViewData(1, 2.0d)
			    , new GraphViewData(2, 1.5d)
			    , new GraphViewData(3, 2.5d)
			    , new GraphViewData(4, 1.0d)
			});
			 
		GraphView graphView = new LineGraphView(
		    getActivity() // context
		    , "GraphViewDemo" // heading
		);
		
		graphView.addSeries(series); // data
		
		//graphView.setHorizontalLabels(horizontalLabel);
		//graphView.setVerticalLabels(verticalLabel);
			 
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.graph);
		layout.addView(graphView);
	}
}
