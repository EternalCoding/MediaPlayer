package com.mediaplayer;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public  class MusicFragment extends Fragment implements View.OnClickListener{
	private static final String TAG="MusicFragment";
	
	View view; 
	GestureDetector gd;
	ActionBar actionBar;
	private static byte touchValue = 0;
	
	
	private ImageView nextImgView, prevImgView, playModeImgView;
	private ImageButton btnPlayOrPause;
	private TimerTask checkSongTime;
	private Timer updateTimer;
	private int currentPosition;
	
	public static int playMode = 0;
	public static TextView songName, songInfo, currentSongTime, endSongTime;
	public static SeekBar songProgressBar;
	
	
	public MusicFragment(){
		
	}
	
	 
	@SuppressWarnings("deprecation")
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanece){
		
		Log.i(TAG, "MsuciFragment created!");
		actionBar = getActivity().getActionBar();
		
		
		gd = new GestureDetector(new SimpleGestureDetector());
		view =  inflater.inflate(R.layout.music_part, container, false);
		
		
		nextImgView = (ImageView)view.findViewById(R.id.next);
		prevImgView = (ImageView)view.findViewById(R.id.prev);
		playModeImgView = (ImageView)view.findViewById(R.id.playMode);
		btnPlayOrPause = (ImageButton)view.findViewById(R.id.playOrPaused);
		
		songName = (TextView)view.findViewById(R.id.songName);
		songInfo = (TextView)view.findViewById(R.id.songInfo);
		currentSongTime = (TextView)view.findViewById(R.id.startTime);
		endSongTime = (TextView)view.findViewById(R.id.endTime);
		songProgressBar = (SeekBar)view.findViewById(R.id.songTimeLine);
		
		playModeImgView.setImageResource(R.drawable.img_appwidget_playmode_repeat + playMode);
		songName.setText(MusicActivity.mMusicList.get(MusicActivity.currentMusicListItem).getSongName());
		songInfo.setText(MusicActivity.mMusicList.get(MusicActivity.currentMusicListItem).getSongArtistAlbum());
		
		
		
		
		if(PlayerService.mediaPlayer != null) {
			
			Log.i(TAG, "Current: " + showTime(PlayerService.mediaPlayer.getCurrentPosition()));
			endSongTime.setText(MusicActivity.mMusicList.get(MusicActivity.currentMusicListItem).getSongTime());
			currentSongTime.setText(showTime(PlayerService.mediaPlayer.getCurrentPosition()));
			songProgressBar.setMax(PlayerService.mediaPlayer.getDuration());//设置播放进度条的最大值
			songProgressBar.setProgress(PlayerService.mediaPlayer.getCurrentPosition());
			if(PlayerService.mediaPlayer.isPlaying())
				btnPlayOrPause.setImageResource(R.drawable.img_music_pause_pressed);
			else 
				btnPlayOrPause.setImageResource(R.drawable.img_music_play_pressed);
		}
		
		songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				PlayerService.mediaPlayer.start(); 
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				PlayerService.mediaPlayer.pause(); 
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if(fromUser){
					PlayerService.mediaPlayer.seekTo(progress);  
				}
				
			}
		});
		
		
		checkSongTime = new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(PlayerService.mediaPlayer != null){
					
					
					getActivity().runOnUiThread(new Runnable() {
			            @Override
			            public void run() {
			            	
			            	currentPosition = PlayerService.mediaPlayer.getCurrentPosition();
			            	songProgressBar.setProgress(currentPosition);
			            	currentSongTime.setText(showTime(currentPosition));
			            }
			        });
					
				}
			}};
		
		updateTimer = new Timer();
		updateTimer.schedule(checkSongTime, 0, 1000);
		
		nextImgView.setOnClickListener(this);
		prevImgView.setOnClickListener(this);
		playModeImgView.setOnClickListener(this);
		btnPlayOrPause.setOnClickListener(this);
		
	
		
		
		
		/*
		view.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				gd.onTouchEvent(event);
				return true;
			}
			
		}); 
		*/
		
		return view;
	}
	
	
	 
	
	
	
	
	public class SimpleGestureDetector extends GestureDetector.SimpleOnGestureListener{
		
		/*
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
		*/
		
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
		
		/*
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
		*/
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		
		case R.id.playMode:
			playMode =  ++playMode % 4;
			playModeImgView.setImageResource(R.drawable.img_appwidget_playmode_repeat + playMode);
			break;
		
		case R.id.playOrPaused:
			if(PlayerService.mediaPlayer != null) {
			
				if(PlayerService.mediaPlayer.isPlaying()){
					 
					PlayerService.mediaPlayer.pause();
					btnPlayOrPause.setImageResource(R.drawable.img_music_play_pressed);
				}else{
					 
					PlayerService.mediaPlayer.start();
					btnPlayOrPause.setImageResource(R.drawable.img_music_pause_pressed);
					}	
			} else {
				playMusic(AppConstant.PlayerMsg.FRIST_START);
				btnPlayOrPause.setImageResource(R.drawable.img_music_pause_pressed);
			}
			break;
			
		case R.id.next:
			currentSongTime.setText("00:00");
			songProgressBar.setProgress(0);
			
			switch(playMode){
			
			case AppConstant.PlayerMsg.PLAYMODE_REPEATONE:
				playMusic(AppConstant.PlayerMsg.CHECK_SONG);
				break;
			
			case  AppConstant.PlayerMsg.PLAYMODE_REPEAT:
				if( ++MusicActivity.currentMusicListItem == MusicActivity.mMusicList.size()) {
					MusicActivity.currentMusicListItem = 0;
				}
				
				Log.i(TAG, "Next clicked the selected song  : " + MusicActivity.currentMusicListItem);
				playMusic(AppConstant.PlayerMsg.CHECK_SONG);
				break;
				
			case AppConstant.PlayerMsg.PLAYMODE_SEQUENCE:
				
				if(++MusicActivity.currentMusicListItem > MusicActivity.mMusicList.size()){
					
					MusicActivity.currentMusicListItem--;
					
					Toast.makeText(getActivity(), "顺序播放，已经是歌单最后一首", Toast.LENGTH_SHORT).show();
				} 
					playMusic(AppConstant.PlayerMsg.CHECK_SONG);
				break;
			case AppConstant.PlayerMsg.PLAYMODE_RANDOM:
				Random random = new Random();
				MusicActivity.currentMusicListItem = random.nextInt(MusicActivity.mMusicList.size());
				playMusic(AppConstant.PlayerMsg.CHECK_SONG);
				
				break;
			
		}
			btnPlayOrPause.setImageResource(R.drawable.img_music_pause_pressed);
			break;
		case R.id.prev:
			
			currentSongTime.setText("00:00");
			songProgressBar.setProgress(0);
			
			switch(playMode){
			
			case AppConstant.PlayerMsg.PLAYMODE_REPEATONE:
				playMusic(AppConstant.PlayerMsg.CHECK_SONG);
				break;
			
			case  AppConstant.PlayerMsg.PLAYMODE_REPEAT:
				if(--MusicActivity.currentMusicListItem < 0) {
					MusicActivity.currentMusicListItem = MusicActivity.mMusicList.size() - 1;
					
				}
				
				Log.i(TAG, "prev clicked the selected song  : " + MusicActivity.currentMusicListItem);
				playMusic(AppConstant.PlayerMsg.CHECK_SONG);
				
				break;
				
			case AppConstant.PlayerMsg.PLAYMODE_SEQUENCE:
				
				if(--MusicActivity.currentMusicListItem < 0){
					
					MusicActivity.currentMusicListItem++;
					
					Toast.makeText(getActivity(), "顺序播放，已经是歌单第一首", Toast.LENGTH_SHORT).show();
				} 
					playMusic(AppConstant.PlayerMsg.CHECK_SONG);
					
				
				break;
			case AppConstant.PlayerMsg.PLAYMODE_RANDOM:
				
				Random random = new Random();
				MusicActivity.currentMusicListItem = random.nextInt(MusicActivity.mMusicList.size());
				playMusic(AppConstant.PlayerMsg.CHECK_SONG);
				break;
			
		}
			btnPlayOrPause.setImageResource(R.drawable.img_music_pause_pressed);
			break;
			
			
		}
		
		
	}
	
	public String showTime(int time){
		
		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
		
	}
	
	public void playMusic(int action){
		
		Log.i(TAG,"START SERVICE");
			 

				Intent intent = new Intent(getActivity(), PlayerService.class);
				intent.putExtra("CMD", action);
				intent.putExtra("ITEM", MusicActivity.currentMusicListItem);
				getActivity().startService(intent);
			 
		
	}
	
	@Override
	public void onDestroy(){
		
		Log.i(TAG, "MusicFragment onDestroy");
		super.onDestroy();
		updateTimer.cancel();
	}
	
	
}