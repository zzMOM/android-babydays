package com.example.babydays;

import java.util.Calendar;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

public class ChartDayActivityDetailFragment extends Fragment{
	private MySQLiteHelper dbHelper;
	private ImageView chartImageView;
	private int recordIndex;
	private List<BabyActivity> routine;
	
	ChartDayActivityDetailFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chart_day_activity_details,
				container, false);
		
		//create database helper
		dbHelper = new MySQLiteHelper(getActivity());
		//routine = dbHelper.getAllBabyActivity();
		String[] type = new String[]{"FeedMilk", "Diaper", "Sleep"};
		routine = dbHelper.getBabyActivityByType(type);
		
		//set recordIndex to be the last record
		recordIndex = routine.size() - 1;
		
		chartImageView = (ImageView)rootView.findViewById(R.id.chartImageView);
		chartImageView.setOnTouchListener(new OnSwipeTouchListener(getActivity()){
			
			@Override
			public void onSwipeBottom(){
		    	getPrevious();
		    }
		    @Override
		    public void onSwipeTop(){
		    	getNext();
		    }
		});
		
		//default show chart
		createChartOfDayActivity(false);
		
		return rootView;
	}
	
	private void createChartOfDayActivity(boolean byDateAscOrDesc){
		int windowwidth = getResources().getDisplayMetrics().widthPixels;//phone window width in pixels
		int windowheight = getResources().getDisplayMetrics().heightPixels;//phone window height in pixels
		//Log.e("width height", windowwidth+" " + windowheight);
		int w = windowwidth;
		int h = windowheight;
		 
		// Create a bitmap with the dimensions we defined above, and with a 16-bit pixel format. We'll
		// get a little more in depth with pixel formats in a later post.
		Bitmap bitmap = Bitmap.createBitmap(w, h, Config.RGB_565);
		// Create a new canvas to draw on, and link it to the bitmap that we created above. Any drawing
		// operations performed on the canvas will have an immediate effect on the pixel data of the
		// bitmap.
		Canvas canvas = new Canvas(bitmap);
		 
		// Fill the entire canvas with a red color.
		canvas.drawColor(Color.BLACK);
		
		// Create a paint object for us to draw with, and set our drawing color.
		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setFakeBoldText(true);
		paint.setTextSize(30);
		paint.setStrokeWidth(1);
		
		//y axis start 100
		int ystart = 100;
		/*-------------------------------------------draw x and y axis--------------------------------*/
		paint.setColor(Color.WHITE);
		paint.setFakeBoldText(false);
		paint.setTextSize(25);
		
		Paint dashpaint = new Paint();
		dashpaint.setColor(Color.WHITE);
		dashpaint.setStyle(Style.STROKE);
		dashpaint.setPathEffect(new DashPathEffect(new float[] {10, 20}, 0));
		
		// canvas draw x and y coordination, (0, 0) left bottom corner--> (30, 400) 
		int gapx = (w - ystart - 60) / 24;
		int gapy = (h - ystart - 60) / 7;
		canvas.drawLine(ystart, 0, ystart, h - 60, paint);//canvas.drawLine(30, 0, 30, 400, paint);//y
		canvas.drawLine(ystart, h - 60, w - 60, h - 60, paint);//canvas.drawLine(30, 400, 900, 400, paint);//x
		canvas.drawLine(ystart, 60, w - 60 , 60, dashpaint);//canvas.drawLine(30, 30, 900, 30, dashpaint);//top dash line
		//x
		for(int i = 0; i <= 24; i++){
			//canvas.drawLine(30 + 30 * i, 390, 30 + 30 * i, 400, paint);
			canvas.drawLine(ystart + gapx * i, h - 70, ystart + gapx * i, h - 60, paint);
			String x = "";
			if(i == 0){
				x = "12:00a";
			} else if(i < 12 && i % 2 == 0){
				x = Integer.toString(i) + ":00a";
			} else if(i == 12){
				x = "12:00p";
			} else if(i == 24){
				x = "12:00a";
			} else if(i > 12 && i % 2 == 0){
				x = Integer.toString(i - 12) + ":00p";
			}
			canvas.save();
			//canvas.rotate(-45, 30 * i, 440);
			//canvas.drawText(x, 30 * i, 440, paint);
			//canvas.rotate(-45, ystart - 30 + gapx * i, h - 10);
			canvas.drawText(x, ystart - 30 + gapx * i, h - 10, paint);
			canvas.restore();
			
			//draw dashline on 6:00AM, 12:00PM, 6:00PM
			if(i == 6 || i == 12 || i == 18 || i == 24){
				//canvas.drawLine(30 + 30 * i, 30, 30 + 30 * i, 400, dashpaint);
				//canvas.drawText(x, 10 + 30 * i, 20, paint);
				canvas.drawLine(ystart + gapx * i, 30, ystart + gapx * i, h - 60, dashpaint);
				canvas.drawText(x, 40 + gapx * i, 50, paint);
			}
		}
		for(int i = 1; i <= 7; i++){
			//canvas.drawLine(20, 400 - 50 * i, 30, 400 - 50 * i, paint);
			canvas.drawLine(ystart - 10, h - 60 - gapy * i, ystart, h - 60 - gapy * i, paint);
		}
		
		/*--------------------------------------draw chart-----------------------------------------*/
		if(byDateAscOrDesc){
			drawChartByDateAsc(paint, canvas, w, h, gapx, gapy, ystart);
		} else {
			drawChartByDateDesc(paint, canvas, w, h, gapx, gapy, ystart);
		}
		 
		// Set this ImageView's bitmap to the one we have drawn to.
		chartImageView.setImageBitmap(bitmap);
		
		
	}
	
	private void drawChartByDateDesc(Paint paint, Canvas canvas, int w, int h, int gapx, int gapy, int ystart){
		
		String prevDate = "";
		int count = 0;
		int i = recordIndex;
		int y = 0;
		Bitmap pee = BitmapFactory.decodeResource(getResources(), R.drawable.dropwater);
		Bitmap poo = BitmapFactory.decodeResource(getResources(), R.drawable.poo);
		Bitmap bottle = BitmapFactory.decodeResource(getResources(), R.drawable.bottle2);
		while(i >= 0){//start from last activity
			String date = routine.get(i).getDate().toString();
			String time = routine.get(i).getTime().toString();
			String type = routine.get(i).getType().toString();
			String info = routine.get(i).getInfo().toString();
			
			//draw date on y axis when start a new date
			if(!date.equals(prevDate)){
				paint.setColor(Color.WHITE);
				count++;
				y = h - 60 - gapy * count;
				canvas.drawText(date.substring(0, 5), 10, y, paint);
				prevDate = date;
			}
			
			String[] tsplit = time.split(":");
			float j = Integer.parseInt(tsplit[0]) + (float)Integer.parseInt(tsplit[1]) / 60;
			float x = ystart + gapx * j;
			if(type.equals("FeedMilk")){
				//paint.setColor(Color.RED);
				//canvas.drawText("F", x, y, paint);
				canvas.drawBitmap(bottle, x - 10,  y, paint);
			} else if(type.equals("Diaper")){
				//paint.setColor(Color.GREEN);
				//canvas.drawText("D", x, y, paint);
				if(info.length() > 4){
					canvas.drawBitmap(poo, x - 10,  y, paint);
				} else {
					canvas.drawBitmap(pee, x - 10, y, paint);
				}
			} else if(type.equals("Nap")){
				StringBuilder endDateb = new StringBuilder();
				StringBuilder endTimeb = new StringBuilder();
				getNapEndDateTime(date, time, info, endDateb, endTimeb);
				String endDate = endDateb.toString();
				String endTime = endTimeb.toString();
				
				paint.setColor(Color.YELLOW);
				paint.setStrokeWidth(10);
				tsplit = endTime.split(":");
				j = Integer.parseInt(tsplit[0]) + (float)Integer.parseInt(tsplit[1]) / 60;
				float endx = ystart + gapx * j;
				if(endDate.equals(date)){
					//draw same day
					canvas.drawLine(x, y, endx, y, paint);
				} else {
					//draw two different day
					//date to 12:00am
					canvas.drawLine(x, y, ystart + gapx * 24, y, paint);//draw current to 12:00AM
					//12:00am to endDate
					int endy = h - 60 - gapy * (count - 1);
					canvas.drawLine(ystart, endy, endx, endy, paint);//draw 12:00am to enddate endtime
				}
				
				
			}
			
			i--;
			if(count >=7){
				break;//current the bitmap just can show 7 days.
			}
				
		}
		recordIndex = i;
		Log.e("recordIndex", Integer.toString(recordIndex));
	}
	
	private void drawChartByDateAsc(Paint paint, Canvas canvas, int w, int h, int gapx, int gapy, int ystart){
		
		String prevDate = "";
		int count = 0;
		int i = recordIndex;
		int y = 0;
		Bitmap pee = BitmapFactory.decodeResource(getResources(), R.drawable.dropwater);
		Bitmap poo = BitmapFactory.decodeResource(getResources(), R.drawable.poo);
		Bitmap bottle = BitmapFactory.decodeResource(getResources(), R.drawable.bottle2);
		while(i < routine.size()){//start from last activity
			String date = routine.get(i).getDate().toString();
			String time = routine.get(i).getTime().toString();
			String type = routine.get(i).getType().toString();
			String info = routine.get(i).getInfo().toString();
			
			//draw date on y axis when start a new date
			if(!date.equals(prevDate)){
				paint.setColor(Color.WHITE);
				count++;
				y = (h - 60) - gapy * (8 - count);
				canvas.drawText(date.substring(0, 5), 10, y, paint);
				prevDate = date;
			}
			
			
			String[] tsplit = time.split(":");
			float j = Integer.parseInt(tsplit[0]) + (float)Integer.parseInt(tsplit[1]) / 60;
			float x = ystart + gapx * j;
			if(type.equals("FeedMilk")){
				/*paint.setColor(Color.RED);
				canvas.drawText("F", x, y, paint);*/
				canvas.drawBitmap(bottle, x - 10,  y, paint);
			} else if(type.equals("Diaper")){
				/*paint.setColor(Color.GREEN);
				canvas.drawText("D", x, y, paint);*/
				if(info.length() > 4){
					canvas.drawBitmap(poo, x - 10,  y, paint);
				} else {
					canvas.drawBitmap(pee, x - 10, y, paint);
				}
			} else if(type.equals("Nap")){
				StringBuilder endDateb = new StringBuilder();
				StringBuilder endTimeb = new StringBuilder();
				getNapEndDateTime(date, time, info, endDateb, endTimeb);
				String endDate = endDateb.toString();
				String endTime = endTimeb.toString();
				
				paint.setColor(Color.YELLOW);
				paint.setStrokeWidth(10);
				tsplit = endTime.split(":");
				j = Integer.parseInt(tsplit[0]) + (float)Integer.parseInt(tsplit[1]) / 60;
				float endx = ystart + gapx * j;
				if(endDate.equals(date)){
					//draw same day
					canvas.drawLine(x, y, endx, y, paint);
				} else {
					//draw two different day
					//date to 12:00am
					canvas.drawLine(x, y, ystart + gapx * 24, y, paint);//draw current to 12:00AM
					//12:00am to endDate
					int endy = h - 60 - gapy * (count + 1);
					canvas.drawLine(ystart, endy, endx, endy, paint);//draw 12:00am to enddate endtime
				}
				
				
			}
			
			i++;
			if(count >=7){
				break;//current the bitmap just can show 7 days.
			}
				
		}
		recordIndex = i;
		Log.e("recordIndex", Integer.toString(recordIndex));
	}


	private void getNapEndDateTime(String date, String time, String info, StringBuilder endDate, StringBuilder endTime){
		Calendar cal = Calendar.getInstance();
		String[] d = date.split("-");
		String[] t = time.split(":");
		cal.set(Integer.parseInt(d[2]), Integer.parseInt(d[0]) - 1, Integer.parseInt(d[1]), 
				Integer.parseInt(t[0]), Integer.parseInt(t[1]));
		Log.e("set time: ", cal.getTime().toString());
		cal.add(Calendar.MINUTE, Integer.parseInt(info.substring(3, 5)));
		cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(info.substring(0, 2)));
		Log.e("time after add ", cal.get(Calendar.MINUTE) + "  " + cal.get(Calendar.HOUR_OF_DAY));
		
		//get endDate endTime
		String m, day;
		m = cal.get(Calendar.MONTH) + 1 < 10? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1) + "";
		day = cal.get(Calendar.DAY_OF_MONTH) < 10? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH) + "";
		endDate.append(m).append("-").append(day).append("-")
				.append(cal.get(Calendar.YEAR)).toString();
		String hour, min;
		hour = cal.get(Calendar.HOUR_OF_DAY) < 10? "0" + cal.get(Calendar.HOUR_OF_DAY) : cal.get(Calendar.HOUR_OF_DAY) + "";
		min = cal.get(Calendar.MINUTE) < 10? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE) + "";
		endTime.append(hour).append(":").append(min).toString();
	}
	
	private void getPrevious(){
		if(recordIndex > 0){
			if(recordIndex > routine.size() - 1){
				recordIndex = routine.size() - 1;
			}
			createChartOfDayActivity(false);
		}
	}
	
	private void getNext(){
		if(recordIndex < routine.size() - 1){
			if(recordIndex < 0){
				recordIndex = 0;
			}
			createChartOfDayActivity(true);
		}
	}
}
