package com.mediaplayer;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;;

@SuppressLint("ValidFragment")
public class VedioFragment extends Fragment {
	private static final String TAG ="VedioFragment";
	
	
	View view;
	GestureDetector gd;
	ActionBar actionBar;
	private static byte touchValue = 0;
	
	public VedioFragment (){
		super();
	}
	
	
	@SuppressWarnings("deprecation")
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanece){
		
		actionBar = getActivity().getActionBar();
		gd = new GestureDetector(new SimpleGestureDetector());
		view = inflater.inflate(R.layout.vedio_part, container, false);
		view.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				gd.onTouchEvent(event);
				return true;
			}
			
		});
		return view;
	}
	
	public class SimpleGestureDetector extends GestureDetector.SimpleOnGestureListener{
		

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.i(TAG, "Toucharea  DoubleClick");
			//vibrator.vibrate(30L);
			 
			return super.onDoubleTap(e);
		}
		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
		return super.onDoubleTapEvent(e);
		}
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float x, float y) {
			
			//vibrator.vibrate(30L);
			float X = e2.getX() - e1.getX();
			float Y = e2.getY() - e1.getY();
			
			if(X > 0 ){
				if(Y >= 0) {
					if(X > Y) {
						 
						touchValue = MusicActivity.Left;
						Log.i(TAG, "Toucharea touch event: Left "  );
					}
					else{
						touchValue = MusicActivity.Up;
						Log.i(TAG, "Toucharea touch event: Up  " );
					}
				}else{
					if(X > Math.abs(Y)) {
						touchValue = MusicActivity.Left;
						Log.i(TAG, "Toucharea touch event: Left " );
					}
					else {
						touchValue = MusicActivity.Right;
						Log.i(TAG, "Toucharea touch event: Right " );
					}
				}
			}else {
				
				if(Y >= 0){
					if(Math.abs(X) > Y) {
						touchValue = MusicActivity.Right;
						Log.i(TAG, "Toucharea touch event: Right ,"  );
						
					}
					else {
						touchValue = MusicActivity.Up;
						Log.i(TAG, "Toucharea touch event: Up ,"  );
					}
				}else {
					if(Math.abs(X) > Math.abs(Y)) {
						touchValue = MusicActivity.Right;
						Log.i(TAG, "Toucharea touch event: Right ,"  );
					}
					else {
						touchValue = MusicActivity.Down;
						Log.i(TAG, "Toucharea touch event: Down ,"  );
					}
				}	
			}
			
			int targetTab = 0;
			targetTab = actionBar.getSelectedNavigationIndex();
			
			Log.i(TAG, "the currented selected tab " + targetTab);
			
			switch(touchValue){
			
				case MusicActivity.Left:
					
					if(targetTab != 0){
						actionBar.setSelectedNavigationItem(targetTab - 1);
					}
					
					break;
				case MusicActivity.Right:
					if(targetTab != 3){
						actionBar.setSelectedNavigationItem(targetTab + 1);
					}
					
					break;
			}
			
			return true;
		}
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			//vibrator.vibrate(30L);
			Log.i(TAG, "Toucharea SingleClick");
			 
		return super.onSingleTapConfirmed(e);
		}
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
		return super.onSingleTapUp(e);
		}
		
	}

	

}
