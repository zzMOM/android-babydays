package com.example.babydays;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jjoe64.graphview.GraphViewSeries;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ChartWeightFragment extends Fragment{
	private MySQLiteHelper dbHelper;
	private List<BabyActivity> routine;
	
	ChartWeightFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_chart_weight,
				container, false);
		
		//create database helper
		dbHelper = new MySQLiteHelper(getActivity());
		
		populateGraphView(rootView);
		
		return rootView;
	}
	
	private void populateGraphView(View view){
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		String[] type = new String[]{"Weight"};
		routine = dbHelper.getBabyActivityByType(type);
		int n = routine.size();
		//BabyInfo bi = dbHelper.getBabyInfo(0);
		List<BabyInfo> blist = dbHelper.getAllBabyInfo();
		String birthday = blist.get(0).getDate().toString();
		int startYear = Integer.parseInt(birthday.substring(birthday.length() - 4, birthday.length()));
		String startDay = "01-01-" + startYear;
		
		GraphViewData[] data = new GraphViewData[n];
		String[] xvalue = new String[n];
		String[] yvalue = new String[n];
		
		double maxValueY = 0;
		double maxValueX = 0;
		int lastYear = startYear;
		for(int i = 0; i < n; i++){
			xvalue[i] = routine.get(i).getDate().toString();
			yvalue[i] = routine.get(i).getInfo().toString();
			double diff = 0.0;
			double value = 0.0;
			try{
				Date date1 = df.parse(startDay);
				Date date2 = df.parse(xvalue[i]);
				diff = date2.getTime() - date1.getTime();
				diff = diff / (1000 * 60 * 60 * 24);
				diff = diff / 365;
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
			String lb = yvalue[i].split("lb")[0];
			String oz = yvalue[i].split("lb")[1].split("oz")[0];
			value = Integer.parseInt(lb) + Integer.parseInt(oz) / 12;
			data[i] = new GraphViewData(diff, value);
			maxValueY = Math.max(maxValueY, value);
			maxValueX = Math.max(maxValueX, diff);
			lastYear = Math.max(lastYear, Integer.parseInt(xvalue[i].substring(xvalue[i].length() - 4, xvalue[i].length())));
			xvalue[i] = xvalue[i].substring(0, xvalue[i].length() - 5);//set xvalue format "6-24"
		}
		maxValueY = (int)Math.round(maxValueY + 0.5);
		maxValueX = (int)Math.round(maxValueX + 0.5);
		GraphViewSeries series = new GraphViewSeries(data);
			 
		MyBarGraphView graphView = new MyBarGraphView(
		    getActivity() // context
		    , "Weight Chart" // heading
		);
		
		//set Y axis
		int intervals = 0;
		while(intervals < maxValueY / 10){
			intervals++;;
		}
		graphView.setManualYAxisBounds(maxValueY, 0);//set Y axis max and min
		graphView.getGraphViewStyle().setNumVerticalLabels(intervals);//set Y axis scale
		//set X axis
		String[] horizontalLabel = new String[lastYear - startYear + 1];
		for(int i = 0; i <= lastYear - startYear; i++){
			horizontalLabel[i] = startYear + i + "";
		}
		graphView.setHorizontalLabels(horizontalLabel);
		//graphView.setDrawDataPoints(true);
		//graphView.setDataPointsRadius(15f);
		graphView.setYValue(yvalue);
		graphView.setDrawValuesOnTop(true);
		graphView.setXValue(xvalue);
		graphView.setDrawXValues(true);
		//graphView.setShowHorizontalLabels(false);
		graphView.setMyHorizontalLabel(horizontalLabel);
		graphView.addSeries(series); // data
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.graph);
		layout.addView(graphView);
	}
	
}
