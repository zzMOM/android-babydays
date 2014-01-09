package com.example.babydays;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MobileArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;
 
	public MobileArrayAdapter(Context context, String[] values) {
		super(context, R.layout.menu_imgtext, values);
		this.context = context;
		this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.menu_imgtext, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.items);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
		textView.setText(values[position]);
 
		// Change icon based on name
		String s = values[position];
 
		System.out.println(s);
 
		if (s.equals("Feed")) {
			imageView.setImageResource(R.drawable.bottle);
		} else if (s.equals("Sleep")) {
			imageView.setImageResource(R.drawable.sleep);
		} else if (s.equals("Diaper")) {
			imageView.setImageResource(R.drawable.diaper);
		} else if (s.equals("Milestone")){
			imageView.setImageResource(R.drawable.milestones);
		}
 
		return rowView;
	}
}