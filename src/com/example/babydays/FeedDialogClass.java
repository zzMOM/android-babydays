package com.example.babydays;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.DialogFragment;
import android.view.View;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FeedDialogClass extends DialogFragment{
	private boolean isStart;
	private int clicktype;
	private ArrayList<String> record;
	
	private EditText textOZ;
	
	FeedDialogClass(){}
	
	public void createFeedDialog(int ct, boolean is) {
		isStart = is;
		clicktype = ct;
		
		
		// Create custom dialog object
        final Dialog dialog = new Dialog(getActivity());
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_feed);
        // Set dialog title
        if(clicktype == 0){
        	dialog.setTitle("Time to feed!");
        } else {
        	dialog.setTitle("Insert a feed activity");
        }
        
        ViewStub dateTimeStub = (ViewStub) dialog.findViewById(R.id.dateTimeStub);
        dateTimeStub.setLayoutResource(R.layout.date_time_merge);
        View inflatedView = dateTimeStub.inflate();
        //setDateTimeMergePart(inflatedView);
        
        textOZ = (EditText) dialog.findViewById(R.id.editTextOZ);

        Button okButton = (Button) dialog.findViewById(R.id.ok);
        // if decline button is clicked, close the custom dialog
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	//check and change nap status
            	if(isStart && clicktype == 0){//onclick
            		//updateNapStatusAndDatabaseRecord();
            	}
            	
                String type = "FeedMilk";
                String info = "";
                if(textOZ.getText().toString().length() > 0){
                	info = textOZ.getText().toString() + "oz";
                	ArrayList<String> re = new ArrayList<String>();
                	re.add(type);
                	re.add(info);
                	setRecord(re);
                }
            	
                // Close dialog
                dialog.dismiss();
            }
        });
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
        // if decline button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
        dialog.show();
    }
	
	public ArrayList<String> getRecord(){
		return record;
	}
	
	public void setRecord(ArrayList<String> re){
		record = re;
	}
}
