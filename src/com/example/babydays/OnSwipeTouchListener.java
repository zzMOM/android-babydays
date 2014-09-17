package com.example.babydays;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeTouchListener implements OnTouchListener{
	private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {
    }
    
    public void onSwipeBottom(){
    	
    }
    
    public void onSwipeTop(){
    	
    }

    private final class GestureListener extends SimpleOnGestureListener {

    	private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        	try {
	            float diffX = e2.getX() - e1.getX();
	            float diffY = e2.getY() - e1.getY();
	            if (Math.abs(diffX) > Math.abs(diffY)) {
	                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
	                    if (diffX > 0) {
	                        onSwipeRight();
	                    } else {
	                        onSwipeLeft();
	                    }
	                }
	                return true;
	            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
	                    if (diffY > 0) {
	                        onSwipeBottom();
	                    } else {
	                        onSwipeTop();
	                    }
	                return true;
	            }
	        } catch (Exception exception) {
	            exception.printStackTrace();
	        }
            return false;
    	}
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return gestureDetector.onTouchEvent(event);
	}

}
