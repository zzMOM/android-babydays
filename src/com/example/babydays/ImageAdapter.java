package com.example.babydays;

import com.example.babydays.R.color;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter{

	private Context mContext;
	static final String[] items = new String[]{"Feed", "Nap", "Diaper", "Milestone", "Diary"};
	static final Integer[] imageId = {	R.drawable.bottle,
        								R.drawable.sleep,
        								R.drawable.diaper,
        								R.drawable.milestones,
        								R.drawable.diary};
	static final Integer[] colorValue = { Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, color.gray};
	
	public ImageAdapter(Context c){
		mContext = c;
	}
	
	@Override
	public int getCount() {
		return imageId.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	//create a new ImageView for each item
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View grid;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if(convertView == null){
			grid = new View(mContext);
			grid = inflater.inflate(R.layout.gridview_adapter, null);
			TextView itemLabel = (TextView)grid.findViewById(R.id.gridItemLabel);
			ImageView itemImage = (ImageView)grid.findViewById(R.id.gridItemImage);
			itemLabel.setText(items[position]);
			itemImage.setImageResource(imageId[position]);
			grid.setBackgroundColor(colorValue[position]);
		} else {
			grid = (View) convertView;
		}
		return grid;
	}

}
