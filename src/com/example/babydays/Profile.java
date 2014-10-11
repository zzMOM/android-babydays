package com.example.babydays;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class Profile extends Activity{
	private ImageView profilePhoto;
	private TextView babyName, birthDate, birthTime, birthHeight, birthWeight;
	/*private SharedPreferences mPrefsInfo;
	private static final String BABY_INFO = " , , , , , ";
	Editor infoEditor;*/
	
	private static final int REQUEST_CODE = 1;
	static final int DATE_DIALOG_ID = 100;
	static final int TIME_DIALOG_ID = 999;
	private Calendar c;

	private Bitmap mBitmap;
	private Uri uri;
	private MySQLiteHelper dbHelper;
	private BabyInfo babyInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//dbHelper;
		dbHelper = new MySQLiteHelper(this);
		
		profilePhoto = (ImageView) findViewById(R.id.profilePhoto);
		babyName = (TextView) findViewById(R.id.babyName);
		birthDate = (TextView) findViewById(R.id.birthDate);
		birthTime = (TextView) findViewById(R.id.birthTime);
		birthHeight = (TextView) findViewById(R.id.birthHeight);
		birthWeight = (TextView) findViewById(R.id.birthWeight);
		
		List<BabyInfo> babyInfoList = dbHelper.getAllBabyInfo();
		babyInfo = babyInfoList.get(0);
		//if babyInfo path value not null, load saved image
		if(babyInfo.getProfilePath().length() > 0){
			loadImageFromStorage(babyInfo.getProfilePath());//load image
		}
		babyName.setText(babyInfo.getName());
		birthDate.setText(babyInfo.getDate());
		birthTime.setText(babyInfo.getTime());
		birthHeight.setText(babyInfo.getHeight());
		birthWeight.setText(babyInfo.getWeight());
		
		//mPrefsInfo = getSharedPreferences(BABY_INFO, 0);
		//initial
		/*Editor editor = mPrefsInfo.edit();
		editor.putString(BABY_INFO, " , , , , , ");
		editor.commit();*/
		/*String str = mPrefsInfo.getString(BABY_INFO, " , , , , , ");
		Log.e("BABY_INFO", str);
		String[] info = str.split(",");
		
		loadImageFromStorage(info[0]);//load image
		babyName.setText(info[1]);
		birthDate.setText(info[2]);
		birthTime.setText(info[3]);
		birthHeight.setText(info[4]);
		birthWeight.setText(info[5]);*/
		
		babyName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setName();
			}
		});
		
		
		birthDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
		});
		birthTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(TIME_DIALOG_ID);
			}
		});
		
		birthHeight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setHeight();
			}
		});
		
		birthWeight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setWeight();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	public void pickProfilePhoto(View view){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "Select..."), REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

			uri = data.getData();

			try {
				InputStream stream = getContentResolver().openInputStream(uri);

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;

				BitmapFactory.decodeStream(stream, null, options);
				stream.close();

				int w = options.outWidth;
				int h = options.outHeight;

				int displayW = getResources().getDisplayMetrics().widthPixels;
				int displayH = getResources().getDisplayMetrics().heightPixels;

				int sample = 1;

				while (w > displayW * sample || h > displayH * sample) {
					sample = sample * 2;
				}

				options.inJustDecodeBounds = false;
				options.inSampleSize = sample;

				stream = getContentResolver().openInputStream(uri);
				Bitmap bm = BitmapFactory.decodeStream(stream, null, options);
				stream.close();
				if (mBitmap != null) {
					mBitmap.recycle();
				}
				// Make a mutable bitmap...
				mBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),Bitmap.Config.ARGB_8888);
				Canvas c = new Canvas(mBitmap);
				c.drawBitmap(bm, 0, 0, null);

				bm.recycle();

				profilePhoto.setImageBitmap(mBitmap);
				
				//save mBitmap
				String path = saveToInternalSorage(mBitmap);
				path = path.trim() + '/' + "profile.jpg";
				babyInfo.setProfilePath(path);
				/*String s = mPrefsInfo.getString(BABY_INFO, "");
				infoEditor = mPrefsInfo.edit();
				infoEditor.putString(BABY_INFO, updateSharedPref(s, path, 0));
				infoEditor.commit();*/
			} catch (Exception e) {
			}

		}
	}
	
	private String saveToInternalSorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
         // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {           

            fos = new FileOutputStream(mypath);

       // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("path", directory.getAbsolutePath());
        return directory.getAbsolutePath();
    }
	
	private void loadImageFromStorage(String path)
	{

	    /*try {
	        File f=new File(path, "profile.jpg");
	        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
	        profilePhoto.setImageBitmap(b);
	    } 
	    catch (FileNotFoundException e) 
	    {
	        e.printStackTrace();
	    }*/
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        profilePhoto.setImageBitmap(bitmap);
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
        c = Calendar.getInstance();
		switch (id) {
		case DATE_DIALOG_ID:
			int year = c.get(Calendar.YEAR);
	        int month = c.get(Calendar.MONTH);
	        int day = c.get(Calendar.DAY_OF_MONTH);
	        //Log.e("year month day", year + " " + month + " " + day);
		    // set date picker as current date
		    return new DatePickerDialog(this, datePickerListener, 
                         year, month, day);
		case TIME_DIALOG_ID:
			//use current date as default date show in the DatePicker
			int hour = c.get(Calendar.HOUR);
			int min = c.get(Calendar.MINUTE);
			return new TimePickerDialog(this, timePickerListener, hour, min, false);
		}
		return null;
	}
	
	private DatePickerDialog.OnDateSetListener datePickerListener 
    		= new DatePickerDialog.OnDateSetListener() {
    	
				@Override
				public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
					// set selected date into textview
					String m, d;
					m = selectedMonth + 1 < 10? "0" + (selectedMonth + 1) : (selectedMonth + 1) + "";
					d = selectedDay < 10? "0" + selectedDay : selectedDay + "";
					String newStr = new StringBuilder().append(m)
							   .append("-").append(d).append("-").append(selectedYear)
							   .toString();
					birthDate.setText(newStr);
					babyInfo.setDate(newStr);
					
					
					/*String s = mPrefsInfo.getString(BABY_INFO, "");
					
					//update mPrefsInfo
					infoEditor = mPrefsInfo.edit();
					infoEditor.putString(BABY_INFO, updateSharedPref(s, newStr, 2));
					infoEditor.commit();
					
					birthDate.setText(newStr);*/
				}
			};
			
			
	
	private TimePickerDialog.OnTimeSetListener timePickerListener
			= new TimePickerDialog.OnTimeSetListener() {
				
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					// set selected time to textview
					String timePick24 = new StringBuilder().append(hourOfDay).append(":").append(minute).toString();
					String a_p = "";
					if(hourOfDay > 12){
						hourOfDay -= 12;
						a_p = "PM";
					} else {
						a_p = "AM";
					}
					String h, m;
					h = hourOfDay < 10? "0" + hourOfDay : hourOfDay + "";
					m = minute < 10? "0" + minute : minute + "";
					String newStr = new StringBuilder().append(h).append(":").append(m).append(a_p).toString();
					babyInfo.setTime(newStr);
					birthTime.setText(newStr);
					/*String s = mPrefsInfo.getString(BABY_INFO, "");
					
					//update mPrefsInfo
					infoEditor = mPrefsInfo.edit();
					infoEditor.putString(BABY_INFO, updateSharedPref(s, newStr, 3));
					infoEditor.commit();
					
					birthTime.setText(newStr);*/
				}
			};
	
	private void setName(){
		AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
		builder.setTitle("Edit baby name");
		final EditText inputText = new EditText(this);
		inputText.setEms(15);
		builder.setView(inputText);
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String newStr = inputText.getText().toString();
				babyInfo.setName(newStr);
				babyName.setText(newStr);
				/*String s = mPrefsInfo.getString(BABY_INFO, "");
				//update mPrefsInfo
				infoEditor = mPrefsInfo.edit();
				infoEditor.putString(BABY_INFO, updateSharedPref(s, newStr, 1));
				infoEditor.commit();
				
				babyName.setText(newStr);*/
			}
		});
		builder.show();
	}
	
	private void setHeight(){
		AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
		builder.setTitle("Edit baby birth height");
		final EditText inputText = new EditText(this);
		inputText.setEms(15);
		builder.setView(inputText);
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String newStr = inputText.getText().toString();
				babyInfo.setHeight(newStr);
				birthHeight.setText(newStr);
				/*String s = mPrefsInfo.getString(BABY_INFO, "");
				//update mPrefsInfo
				infoEditor = mPrefsInfo.edit();
				infoEditor.putString(BABY_INFO, updateSharedPref(s, newStr, 4));
				infoEditor.commit();
				
				birthHeight.setText(newStr);*/
			}
		});
		builder.show();
	}
	
	private void setWeight(){
		AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
		builder.setTitle("Edit baby birth weight");
		final EditText inputText = new EditText(this);
		inputText.setEms(15);
		builder.setView(inputText);
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String newStr = inputText.getText().toString();
				babyInfo.setWeight(newStr);
				birthWeight.setText(newStr);
				/*String s = mPrefsInfo.getString(BABY_INFO, "");
				//update mPrefsInfo
				infoEditor = mPrefsInfo.edit();
				infoEditor.putString(BABY_INFO, updateSharedPref(s, newStr, 5));
				infoEditor.commit();
				
				birthWeight.setText(newStr);*/
			}
		});
		builder.show();
	}
	
	private void updateBabyInfo(){
		dbHelper.updateBabyInfo(babyInfo);
	}
	
	/*private String updateSharedPref(String s, String newStr, int index){
		String[] arrays = s.split(",");
		arrays[index] = newStr;
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < arrays.length; i++){
			if(i != 0){
				b.append(",");
			}
			b.append(" ").append(arrays[i]).append(" ");
		}
		return b.toString();
	}*/

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		updateBabyInfo();
		super.onBackPressed();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case android.R.id.home:
			// app icon in action bar clicked; go home
			updateBabyInfo();
            Intent intent = new Intent(this, MainMenuCard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
