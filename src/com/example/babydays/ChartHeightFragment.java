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
		String[] type = new String[]{"Height"};
		routine = dbHelper.getBabyActivityByType(type);
		if(routine.size() != 0){
			populateGraphView(rootView);
		}
		
		return rootView;
	}
	
	private void populateGraphView(View view){
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		
		int n = routine.size();
		
		GraphViewData[] data = new GraphViewData[n];
		String[] xvalue = new String[n];
		String[] yvalue = new String[n];
		
		String s = routine.get(0).getDate().toString();
		int startYear = Integer.parseInt(s.substring(s.length() - 4, s.length()));
		String startDay = "01-01-" + startYear;
		s = routine.get(n - 1).getDate().toString();
		int lastYear = Integer.parseInt(s.substring(s.length() - 4, s.length()));
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
			String feet = yvalue[i].split("feet")[0];
			String inch = yvalue[i].split("feet")[1].split("inch")[0];
			value = Integer.parseInt(feet) + Double.parseDouble(inch) / 12;
			if(Integer.parseInt(feet) == 0){//if feet is 0, set yvalue format xinche
				yvalue[i] = inch + "\"";
			} else {
				yvalue[i] = feet + "'" + inch + "\"";
			}
			data[i] = new GraphViewData(diff, value);
			xvalue[i] = xvalue[i].substring(0, xvalue[i].length() - 5);//set xvalue format "6-24"
		}
		double maxValueY = data[n - 1].getY();
		GraphViewSeries series = new GraphViewSeries(data);
			 
		MyBarGraphView graphView = new MyBarGraphView(
		    getActivity() // context
		    , "Height Chart" // heading
		);
		
		//set Y axis
		int intervals = (int) Math.round(maxValueY + 0.5);
		String[] ylabels = new String[intervals + 1];
		for(int i = 0; i <= intervals; i++){
			ylabels[intervals - i] = i + "'";
		}
		//graphView.setManualYAxisBounds(maxValueY, 0);//set Y axis max and min
		graphView.getGraphViewStyle().setNumVerticalLabels(intervals + 1);//set Y axis scale
		graphView.setVerticalLabels(ylabels);
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
		graphView.setMyVerticalLabel(ylabels);
		graphView.addSeries(series); // data
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.graph);
		layout.addView(graphView);
	}
}
