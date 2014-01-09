package com.example.babydays;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenuAdapter extends BaseAdapter {
	private final Context context;
	private final String[] values;
 
	public MainMenuAdapter(Context context, String[] values) {
		this.context = context;
		this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.menu_imgtext, null);
		TextView textView = (TextView) rowView.findViewById(R.id.items);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
		textView.setText(values[position]);
 
		// Change icon based on name
		String s = values[position];
 
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

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}