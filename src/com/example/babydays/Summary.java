package com.example.babydays;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.os.Bundle;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Summary extends Activity {

	private MySQLiteHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.summary);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		//create database helper
		dbHelper = new MySQLiteHelper(this);
		List<BabyActivity> routine = dbHelper.getAllBabyActivity();
		
		
		/*for(int i = 0; i < routine.size(); i++){
			summaryText.append(routine.get(i).toString());
			summaryText.append("\n");
		}*/
		
		createChartOfDayActivity(routine);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.summary, menu);
		return true;
	}
	
	
	
	private void createChartOfDayActivity(List<BabyActivity> routine){
		// We'll be creating an image that is 100 pixels wide and 200 pixels tall.
		int width = 800;
		int height = 450;
		 
		// Create a bitmap with the dimensions we defined above, and with a 16-bit pixel format. We'll
		// get a little more in depth with pixel formats in a later post.
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		 
		// Create a new canvas to draw on, and link it to the bitmap that we created above. Any drawing
		// operations performed on the canvas will have an immediate effect on the pixel data of the
		// bitmap.
		Canvas canvas = new Canvas(bitmap);
		 
		// Fill the entire canvas with a red color.
		canvas.drawColor(Color.BLACK);
		
		/*-------------------------------------------draw x and y axis--------------------------------*/
		// Create a paint object for us to draw with, and set our drawing color to blue.
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		
		// canvas draw x and y coordination, (0, 0) left bottom corner--> (30, 400) 
		canvas.drawLine(30, 0, 30, 400, paint);//y
		canvas.drawLine(30, 400, 900, 400, paint);//x
		//x
		for(int i = 0; i <= 24; i++){
			canvas.drawLine(30 + 30 * i, 390, 30 + 30 * i, 400, paint);
			String x;
			if(i == 0){
				x = "12:00AM";
			} else if(i < 12){
				x = Integer.toString(i) + ":00AM";
			} else if(i == 12){
				x = "12:00PM";
			} else if(i == 24){
				x = "12:00AM";
			} else {
				x = Integer.toString(i - 12) + ":00PM";
			}
			canvas.save();
			canvas.rotate(-45, 30 * i, 440);
			canvas.drawText(x, 30 * i, 440, paint);
			canvas.restore();
		}
		for(int i = 1; i <= 7; i++){
			canvas.drawLine(20, 400 - 50 * i, 30, 400 - 50 * i, paint);
		}
		
		/*--------------------------------------draw chart-----------------------------------------*/
		String prevDate = "";
		int count = 0;
		int i = routine.size() - 1;
		int y = 0;
		float endTime = 0;
		String endDate = "";
		boolean endFlag = false;
		float lastx = 0;
		while(count <= 4){
			String date = routine.get(i).getDate().toString();
			String time = routine.get(i).getTime().toString();
			String type = routine.get(i).getType().toString();
			String info = routine.get(i).getInfo().toString();
			
			//draw date on y axis
			if(!date.equals(prevDate)){
				paint.setColor(Color.WHITE);
				count++;
				y = 400 - 50 * count;
				canvas.drawText(date, 0, y, paint);
				prevDate = date;
			}
			
			//date transfer 12hours to 24hours
			String time24 = "";
			SimpleDateFormat h_mm_a   = new SimpleDateFormat("h:mma");
			SimpleDateFormat hh_mm = new SimpleDateFormat("HH:mm");

			try {
			    Date t = h_mm_a.parse(time);
			    time24 = hh_mm.format(t).toString();
			} catch (Exception e) {
			    e.printStackTrace();
			    i--;
			    continue;
			}
			
			float j = Integer.parseInt(time24.substring(0, 2)) + (float)Integer.parseInt(time24.substring(3, 5)) / 60;
			float x = 30 + 30 * j;
			if(type.equals("FeedMilk")){
				paint.setColor(Color.RED);
				canvas.drawText("F", x, y, paint);
			} else if(type.equals("Diaper")){
				paint.setColor(Color.GREEN);
				canvas.drawText("D", x, y, paint);
			} else if(type.equals("Nap") && info.equals("End")){
				endFlag = true;
				endTime = x;
				endDate = date;
			} else if(type.equals("Nap") && info.equals("Start")){
				paint.setColor(Color.YELLOW);
				paint.setStrokeWidth(5);
				if(endFlag && date.equals(endDate)){
					canvas.drawLine(x, y, endTime, y, paint);
				} else {
					canvas.drawLine(x, y, lastx-5, y, paint);
				}
				//reset
				endFlag = false;
				endTime = 0;
				endDate = "";
			}
			
			lastx = x;
			i--;
		}
		
		 
		// In order to display this image in our activity, we need to create a new ImageView that we
		// can display.
		ImageView chartView = (ImageView)findViewById(R.id.chartImageView);
		 
		// Set this ImageView's bitmap to the one we have drawn to.
		chartView.setImageBitmap(bitmap);
		
		
	}

}
