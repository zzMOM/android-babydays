package com.example.babydays;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jjoe64.graphview.BarGraphView;
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
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		String[] type = new String[]{"Height"};
		routine = dbHelper.getBabyActivityByType(type);
		int n = routine.size();
		BabyInfo bi = dbHelper.getBabyInfo(0);
		String birthday = null;
		if(bi == null && n == 0){
			return;
		} else if(bi == null && n!= 0){
			birthday = routine.get(0).getDate().toString();
		} else {
			birthday = bi.getDate().toString();
		}
		
		GraphViewData[] data = new GraphViewData[n];
		String[] xvalue = new String[n];
		String[] yvalue = new String[n];
		
		double maxValueY = 0;
		double maxValueX = 0;
		for(int i = 0; i < n; i++){
			xvalue[i] = routine.get(i).getDate().toString();
			yvalue[i] = routine.get(i).getInfo().toString();
			double diff = 0.0;
			double value = 0.0;
			try{
				Date date1 = df.parse(birthday);
				Date date2 = df.parse(xvalue[i]);
				diff = date2.getTime() - date1.getTime();
				diff = diff / (1000 * 60 * 60 * 24);
				diff = diff / 365;
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
			String feet = yvalue[i].split("feet")[0];
			String inch = yvalue[i].split("feet")[1].split("inch")[0];
			value = Integer.parseInt(feet) + Double.parseDouble(inch) / 12;
			data[i] = new GraphViewData(diff, value);
			maxValueY = Math.max(maxValueY, value);
			maxValueX = Math.max(maxValueX, diff);
		}
		maxValueY = (int)Math.round(maxValueY + 0.5);
		maxValueX = (int)Math.round(maxValueX + 0.5);
		GraphViewSeries series = new GraphViewSeries(data);
			 
		GraphView graphView = new LineGraphView(
		    getActivity() // context
		    , "HeightChart" // heading
		);
		
		graphView.addSeries(series); // data
		//set Y axis
		int intervals = 0;
		while(intervals < maxValueY){
			intervals++;
		}
		graphView.setManualYAxisBounds(maxValueY, 0);//set Y axis max and min
		graphView.getGraphViewStyle().setNumVerticalLabels(intervals);//set Y axis scale
		//set X axis
		intervals = 0;
		while(intervals < maxValueX){
			intervals++;
		}
		String[] horizontalLabel = new String[intervals + 1];
		for(int i = 0; i <= intervals; i++){
			horizontalLabel[i] = i + " year";
		}
		graphView.setHorizontalLabels(horizontalLabel);
			 
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.graph);
		layout.addView(graphView);
	}
}
