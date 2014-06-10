package com.example.babydays;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenuAdapter extends BaseAdapter {
	private final Integer[] imageId;
	private final String[] values;
	private LayoutInflater inflater;
	
 
	public MainMenuAdapter(Context context, String[] values, Integer[] imageId) {
		this.values = values;
		this.imageId = imageId;
		inflater = LayoutInflater.from(context);
	}
	
	static class ViewHolder {
		TextView textView;
		ImageView imageView;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return values.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return values[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			//get layout from .xml
			convertView = inflater.inflate(R.layout.menu_imgtext, null);
			//set value into textview
			holder.textView = (TextView) convertView.findViewById(R.id.items);
			(holder.textView).setTextSize(30);
			(holder.textView).setHeight(150);
			(holder.textView).setMinimumHeight(150);
			(holder.textView).setGravity(Gravity.CENTER_VERTICAL);
			//set image based on selected text
			holder.imageView = (ImageView) convertView.findViewById(R.id.img);
			(holder.imageView).setMinimumHeight(150);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.textView.setText(values[position]);
		holder.imageView.setImageResource(imageId[position]);
		
		return convertView;
	}
}