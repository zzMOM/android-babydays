package com.example.babydays;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class Diary extends Activity {
	private static final int REQUEST_CODE = 1;
	ImageButton shareButton;
	EditText diaryText;
	Button cancelButton;
	Button okButton;
	private Bitmap mBitmap;
	private ImageView shareImage0;
	private Uri uri;
	
	private SimpleDateFormat df;
	private Calendar c;
	private MySQLiteHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary);
		
		diaryText = (EditText) findViewById(R.id.diaryText);
		diaryText.setFocusableInTouchMode(true);
		diaryText.requestFocus();
		
		//date and time format
		c = Calendar.getInstance();
		df = new SimpleDateFormat("MM-dd-yyyy hh:mma");
		
		//create database helper
		dbHelper = new MySQLiteHelper(this);
		
		//share the diary text and attach a photo via email, facebook, twitter etc.
		shareButton = (ImageButton) findViewById(R.id.diaryShare);
		shareButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareIt();
			}
		});
		
		//cancel writing - delete all text
		cancelButton = (Button) findViewById(R.id.diaryCancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelDialog();
				
			}
		});
		
		okButton = (Button)findViewById(R.id.diaryOK);
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//get date to insert into database-TABLE baby_activities
                String formattedDate = df.format(c.getTime());
                String[] s = formattedDate.split(" ");
                String date = s[0];
                String time = s[1];
                String type = "Diary";
                String info = diaryText.getText().toString();
            	dbHelper.addBabyActivity(new BabyActivity(date, time, type, info));
            	
            	diaryText.setText("");
				diaryText.requestFocus();
        	    
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.diary, menu);
		return true;
	}
	
	
	public void cancelDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(Diary.this);
		builder.setMessage("Do you want to delete all the text in text area? If yes, you can't recover it.");
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				diaryText.setText("");
				diaryText.requestFocus();
				dialog.dismiss();
			}
		});
		
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.show();
	}
	
	public void choosePhoto(View v){
		// Do some Intent magic to open the Gallery? Yes!
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "Select..."), REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

			uri = data.getData();

			Toast.makeText(getApplicationContext(), uri.toString(),Toast.LENGTH_LONG).show();
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
				/*TextPaint tp = new TextPaint();
				tp.setTextSize(20);
				tp.setColor(0xffffffff); // AARRGGBB
				// 0xff....... Fully opaque
				// 0x00....... Fully transparent (useless!)
				c.drawText("photo copy", bm.getWidth() - 100, bm.getHeight() - 100, tp);*/

				bm.recycle();

				shareImage0 = (ImageView) findViewById(R.id.shareImage0);
				shareImage0.setImageBitmap(mBitmap);
			} catch (Exception e) {
			}

		}
	}

	public void shareIt() {
		String diaryStr = diaryText.getText().toString();
		
		if (mBitmap == null && diaryStr == "") {
			return;
		}
		
		if (mBitmap == null && !(diaryStr == "")) {
			Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			String shareBody = diaryText.getText().toString();
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Diary Today");
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			startActivity(Intent.createChooser(sharingIntent, "Share via"));
			return;
		}
		
		/*
		//this part will make a copy of the original photo, put a stamp on photo, and save on device
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		path.mkdirs();

		// Note, for display purposes
		// SimpleDateFormat.getTimeInstance()
		// getDateTimeInstance() or getDateIntance
		// are more appropriate.
		// For filenames we can use the following specification
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		String filename = "Imagen_" + timestamp + ".jpg";
		// Alternatively ... use System.currentTimeMillis()

		// Creating a new File object in Java does not create a new
		// file on the device. The file object just represents
		// a location or path that may or may not exist
		File file = new File(path, filename);
		FileOutputStream stream;
		try {
			// This can fail if the external storage is mounted via USB
			stream = new FileOutputStream(file);
			mBitmap.compress(CompressFormat.JPEG, 100, stream);
			stream.close();
		} catch (Exception e) {
			return; // Do not continue
		}
		
		Uri uri = Uri.fromFile(file);
		
		// Tell Android that a new public picture exists
		
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intent.setData(uri);
		sendBroadcast(intent);*/

		// Send the public picture file to my friend... 
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("*/*");
		share.putExtra(Intent.EXTRA_TEXT, diaryStr);
		share.putExtra(Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(share, "Share using..."));
	}

}
