package com.example.babydays;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Profile extends Activity{
	private ImageView profilePhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		profilePhoto = (ImageView) findViewById(R.id.profilePhoto);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	public void pickProfilePhoto(View view){
		Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePicture, 0);//zero can be replaced with any action code
		Intent pickPhoto = new Intent(Intent.ACTION_PICK,
		           android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch(requestCode) {
		case 0:
		    if(resultCode == RESULT_OK){  
		        Uri selectedImage = data.getData();
		        profilePhoto.setImageURI(selectedImage);
		    }
		    break; 
		case 1:
		    if(resultCode == RESULT_OK){  
		        Uri selectedImage = data.getData();
		        profilePhoto.setImageURI(selectedImage);
		    }
		    break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
