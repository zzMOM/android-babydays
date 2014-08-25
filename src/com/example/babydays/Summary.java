package com.example.babydays;

import java.util.Calendar;
import java.util.List;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.*;

public class Summary extends Activity {

	private MySQLiteHelper dbHelper;
	private Button firstButton;
	private Button prevButton;
	private Button nextButton;
	private Button lastButton;
	private Button readSample;
	private ImageView chartImageView;
	
	private int recordIndex;
	private List<BabyActivity> routine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.summary);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		//create database helper
		dbHelper = new MySQLiteHelper(this);
		routine = dbHelper.getAllBabyActivity();
		
		//actionbar
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		chartImageView = (ImageView)findViewById(R.id.chartImageView);
		chartImageView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if(chartImageView.getLayoutParams().width == LayoutParams.MATCH_PARENT){
					chartImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					chartImageView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));
				} else {
					chartImageView.setScaleType(ImageView.ScaleType.FIT_XY);
					chartImageView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
				}
				return false;
			}
		});
		
		
		//set recordIndex to be the last record
		recordIndex = routine.size() - 1;
		
		firstButton = (Button)findViewById(R.id.firstButton);
		prevButton = (Button)findViewById(R.id.prevButton);
		nextButton = (Button)findViewById(R.id.nextButton);
		lastButton = (Button)findViewById(R.id.lastButton);
		
		firstButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(recordIndex != 0){
					recordIndex = 0;
					createChartOfDayActivity(true);
				}
			}
		});
		
		lastButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(recordIndex != routine.size() - 1){
					recordIndex = routine.size() - 1;
					createChartOfDayActivity(false);
				}
				
			}
		});
		
		prevButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(recordIndex > 0){
					if(recordIndex > routine.size() - 1){
						recordIndex = routine.size() - 1;
					}
					createChartOfDayActivity(false);
				}
			}
		});
		
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(recordIndex < routine.size() - 1){
					if(recordIndex < 0){
						recordIndex = 0;
					}
					createChartOfDayActivity(true);
				}
			}
		});
		
		//readSample button
		/*readSample = (Button)findViewById(R.id.readSample);
		readSample.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(readSample.getText().equals("Read Sample")){
					
					//set button text to "Clear Sample" and other button unclick
					readSample.setText("Clear Sample");
					firstButton.setClickable(false);
					nextButton.setClickable(false);
					prevButton.setClickable(false);
					lastButton.setClickable(false);
				} else {
					//show default chart
					createChartOfDayActivity(false);
					//reset button text and other button clickable
					readSample.setText("Read Sample");
					firstButton.setClickable(true);
					nextButton.setClickable(true);
					prevButton.setClickable(true);
					lastButton.setClickable(true);
				}
			}
		});*/
		
		//default show chart
		createChartOfDayActivity(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.summary, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case android.R.id.home:
			// app icon in action bar clicked; go home
            Intent intent = new Intent(this, MainMenuCard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void createChartOfDayActivity(boolean byDateAscOrDesc){
		int windowwidth = getResources().getDisplayMetrics().widthPixels;//phone window width in pixels
		int windowheight = getResources().getDisplayMetrics().heightPixels;//phone window height in pixels
		
		// We'll be creating an image that is 100 pixels wide and 200 pixels tall.
		//int width = 800;
		//int height = 450;
		int w = windowwidth;
		/*if(windowwidth < 1500){
			w = windowwidth * 3 / 2;
		}*/
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


private void drawChartBySample(Paint paint, Canvas canvas, int w, int h, int gapx, int gapy, int ystart){
		
		String prevDate = "";
		int count = 0;
		int i = recordIndex;
		int y = 0;
		float endTime = 0;
		String endDate = "";
		boolean endFlag = false;
		float lastx = 0;
		float lasty = 0;
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
			
			float j = Integer.parseInt(time.substring(0, 2)) + (float)Integer.parseInt(time.substring(3, 5)) / 60;
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
			} else if(type.equals("Nap") && info.equals("End")){
				endFlag = true;
				endTime = x;
				endDate = date;
			} else if(type.equals("Nap") && info.equals("Start")){
				paint.setColor(Color.YELLOW);
				paint.setStrokeWidth(10);
				if(!endFlag){
					canvas.drawLine(x, y, ystart + gapx * 24, y, paint);//draw current to 12:00AM
				} else if(endFlag && date.equals(endDate)){//start end in the same day
					canvas.drawLine(x, y, endTime, y, paint);
				} else if(endFlag && !date.equals(endDate) && endTime == lastx){//start end in different day
					canvas.drawLine(ystart, lasty, lastx, lasty, paint);//draw 12:00am to lastx,lasty
					canvas.drawLine(x, y, ystart + gapx * 24, y, paint);//draw current to 12:00AM
				}
				//reset
				endFlag = false;
				endTime = 0;
				endDate = "";
			}
			
			lastx = x;
			lasty = y;
			i--;
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
}
