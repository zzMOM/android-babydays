package com.example.babydays;

import java.util.List;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.os.Bundle;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
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
		int height = 500;
		 
		// Create a bitmap with the dimensions we defined above, and with a 16-bit pixel format. We'll
		// get a little more in depth with pixel formats in a later post.
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
		 
		// Create a new canvas to draw on, and link it to the bitmap that we created above. Any drawing
		// operations performed on the canvas will have an immediate effect on the pixel data of the
		// bitmap.
		Canvas canvas = new Canvas(bitmap);
		 
		// Fill the entire canvas with a red color.
		canvas.drawColor(Color.BLACK);
		
		// Create a paint object for us to draw with, and set our drawing color to blue.
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		
		// canvas draw x and y coordination, (0, 0) left bottom corner--> (30, 450) 
		canvas.drawLine(30, 0, 30, 450, paint);//y
		canvas.drawLine(30, 450, 900, 450, paint);//x
		//x
		for(int i = 0; i <= 24; i++){
			canvas.drawLine(30 + 30 * i, 440, 30 + 30 * i, 450, paint);
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
			canvas.rotate(-45, 30 * i, 470);
			canvas.drawText(x, 30 * i, 470, paint);
			canvas.restore();
		}
		for(int i = 1; i <= 7; i++){
			canvas.drawLine(20, 450 - 50 * i, 30, 450 - 50 * i, paint);
		}
		 
		
		 
		// In order to display this image in our activity, we need to create a new ImageView that we
		// can display.
		ImageView chartView = (ImageView)findViewById(R.id.chartImageView);
		 
		// Set this ImageView's bitmap to the one we have drawn to.
		chartView.setImageBitmap(bitmap);
		
		
	}

}
