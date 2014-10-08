package com.example.babydays;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.AttributeSet;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

public class MyBarGraphView extends BarGraphView{
	private boolean drawValuesOnTop;
	private boolean drawXValues;
	private int valuesOnTopColor = Color.RED;
	private String[] yvalue;
	private String[] xvalue;
	private String[] horizontalLabel;
	
	public MyBarGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public MyBarGraphView(Context context, String title) {
		super(context, title);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void drawHorizontalLabels(Canvas canvas, float border,
			float horstart, float height, String[] horlabels, float graphwidth) {
		// horizontal labels + lines
		paint.setTextAlign(Align.CENTER);

		int hors = horlabels.length;
		float barwidth = graphwidth/horlabels.length;
		float textOffset = 0;//barwidth/2;
		for (int i = 0; i < horlabels.length; i++) {
			// lines
			float x = ((graphwidth / hors) * i) + horstart;
			paint.setColor(graphViewStyle.getGridColor());
			canvas.drawLine(x, height - border, x, border, paint);

            if(getShowHorizontalLabels()) {
                // text
                x = barwidth*i + textOffset + horstart;
                paint.setColor(graphViewStyle.getHorizontalLabelsColor());
                canvas.drawText(horlabels[i], x, height - 4, paint);
            }
		}
	}
	
	@Override
	public void drawSeries(Canvas canvas, GraphViewDataInterface[] values, float graphwidth, float graphheight,
			float border, double minX, double minY, double diffX, double diffY,
			float horstart, GraphViewSeriesStyle style) {
		float colwidth = graphwidth / (values.length);
		float inwidth = graphwidth / horizontalLabel.length;
		paint.setStrokeWidth(style.thickness);

		float offset = 0;

		// draw data
		for (int i = 0; i < values.length; i++) {
			float valY = (float) (values[i].getY() - minY);
			float ratY = (float) (valY / diffY);
			float y = graphheight * ratY;

			// hook for value dependent color
			if (style.getValueDependentColor() != null) {
				paint.setColor(style.getValueDependentColor().get(values[i]));
			} else {
				paint.setColor(style.color);
			}

			float left = (float) ((values[i].getX() * inwidth) + horstart - offset - colwidth / 20);//left + colwidth/3
			float top = (border - y) + graphheight;
			float right = (float) ((values[i].getX() * inwidth) + horstart - offset + colwidth / 20);//right - colwidth/3
			canvas.drawRect(left, top, right, graphheight + border - 1, paint);

			// -----Set values on top of graph---------
			if (drawValuesOnTop) {
				top -= 4;
				if (top<=border) top+=border+4;
				paint.setTextAlign(Align.CENTER);
				paint.setColor(valuesOnTopColor );
				canvas.drawText(yvalue[i], (left+right)/2, top, paint);
			}
			
			// -----Set X values x axis---------
			if(drawXValues){
				paint.setTextAlign(Align.CENTER);
				paint.setColor(valuesOnTopColor );
				canvas.drawText(xvalue[i], (left+right)/2, graphheight + border + 25, paint);
			}
		}
	}
	
	
	public boolean getDrawValuesOnTop() {
		return drawValuesOnTop;
	}

	public int getValuesOnTopColor() {
		return valuesOnTopColor;
	}

	/**
	 * You can set the flag to let the GraphView draw the values on top of the bars
	 * @param drawValuesOnTop
	 */
	public void setDrawValuesOnTop(boolean drawValuesOnTop) {
		this.drawValuesOnTop = drawValuesOnTop;
	}

	public void setValuesOnTopColor(int valuesOnTopColor) {
		this.valuesOnTopColor = valuesOnTopColor;
	}
	
	public void setDrawXValues(boolean drawXValues) {
		this.drawXValues = drawXValues;
	}
	
	public boolean getDrawXValue(){
		return drawXValues;
	}

	public void setYValue(String[] yvalue){
		this.yvalue = yvalue;
	}
	
	public String[] getYValue(){
		return yvalue;
	}
	
	public void setXValue(String[] xvalue){
		this.xvalue = xvalue;
	}
	
	public String[] getXValue(){
		return xvalue;
	}
	
	public void setMyHorizontalLabel(String[] honrizontalLabel){
		this.horizontalLabel = honrizontalLabel;
	}
	
	public String[] getMyHorizontalLabel(){
		return horizontalLabel;
	}
}
