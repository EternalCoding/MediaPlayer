package com.mediaplayer;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MusicActivity extends Activity {
	public final static String TAG = "MusiActivity";
	public final static byte Up = 1;
	public final static byte Down = 2;
	public final static byte Right = 3;
	public final static byte Left = 4;
	
 
	static ActionBar actionBar;
	public static List<SongInfo> mMusicList = new ArrayList<SongInfo>();
	public static List<String> mImageList = new ArrayList<String>();
	public static int currentMusicListItem = 0;
	
	private boolean isSureQuit = false;
 
	ContentResolver cr; 

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		cr = getContentResolver();
		scannerMusic();
		scannerImage();
		
	   actionBar = getActionBar();
	   // 返回箭头（默认不显示）  
       getActionBar().setDisplayHomeAsUpEnabled(true);  
      
       // 左侧图标点击事件使能  
       //getActionBar().setHomeButtonEnabled(true); 
       
       // 使左上角图标(系统)是否显示  
       getActionBar().setDisplayShowHomeEnabled(false);  
       
       // 显示标题 
       getActionBar().setDisplayShowTitleEnabled(false);  
		
       actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
       
       //musicTab
       Tab musicTab = actionBar.newTab().setText(R.string.music_part)
    		   		  .setTabListener(new TabListener<MusicFragment>(this, getString(R.string.music_part), MusicFragment.class));
       actionBar.addTab(musicTab, true);
      
       //vedioTab
       Tab vedioTab = actionBar.newTab().setText(R.string.vedio_part)
    		   		  .setTabListener(new TabListener<VedioFragment>(this, getString(R.string.vedio_part), VedioFragment.class));
       actionBar.addTab(vedioTab);
      
       //imgTab
       Tab imgTab = actionBar.newTab().setText(R.string.image_part)
    		        .setTabListener(new TabListener<ImageFragment>(this, getString(R.string.image_part), ImageFragment.class));
       actionBar.addTab(imgTab);
       
       //readingTab
       Tab readingTab = actionBar.newTab().setText(R.string.reading_part)
    		        .setTabListener(new TabListener<ReadingFragment>(this, getString(R.string.reading_part), ReadingFragment.class));
       actionBar.addTab(readingTab);
       
       
	
	}
	
	public class TabListener<T extends Fragment> implements ActionBar.TabListener{
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;
		
		
		public TabListener(Activity activity, String tag, Class<T> clz){
			
			mActivity = activity;
			mTag = tag;
			mClass = clz;
			
		}
		
		
		
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
			
			if(mFragment == null){
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				
				
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				ft.attach(mFragment);
			}
			
			
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			if(mFragment != null) {
				ft.detach(mFragment);
			}
			
		}
		
	}

	@Override 
	public void onDestroy(){
		
		super.onDestroy();
		
	}
	
	@Override 
	public void onBackPressed(){
		if(isSureQuit){
			super.onBackPressed();
		} else {
			
			isSureQuit = true;
			Toast.makeText(this, getString(R.string.sureQuit), Toast.LENGTH_SHORT).show();
			
			Thread mThread = new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					isSureQuit = false;
				}
				
			});
			mThread.start();
			
		}
		
	}
	
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		 getMenuInflater().inflate(R.menu.music, menu);
		 return true;
	}
	
	
	public class SongInfo {
		
		String songPath;
		String songName;
		String songArtistAblum;
		String songTime;
		int songDuration;
		
		
		public SongInfo(){
			super();
		}
		
		public SongInfo(String songPath, String songName, String songArtistAblum, int songDuration, String songTime){
			super();
			this.songPath = songPath;
			this.songName = songName;
			this.songArtistAblum = songArtistAblum;
			this.songDuration = songDuration;	
			this.songTime = songTime;
		}
		
		public String getSongPath(){
			return this.songPath;
		}
		public String getSongName(){
			return this.songName;
		}
		public String getSongArtistAlbum(){
			return this.songArtistAblum;
		}
		public String getSongTime(){
			return this.songTime;
		}
		public int getSongDuration(){
			return this.songDuration;
		}
	
		
	}
	
	
	public void scannerMusic(){
	
		Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		mMusicList.clear();
		String songPath,songName,songArtist,songAlbum,time;
		int songDuration;
		
		if(cursor.moveToFirst()){
			
			while(!cursor.isAfterLast()){
				 songPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				 songName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
				 songArtist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
				 songAlbum = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
				 songDuration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
				 time = showTime(songDuration);
			    
			    Log.i(TAG, songPath);
			    Log.i(TAG, songArtist + "-" + songAlbum);
			    //Log.i(TAG, songName + time);
			    
			    SongInfo songInfo = new SongInfo(songPath, songName, songArtist + "-" + songAlbum, songDuration, time);
			    mMusicList.add(songInfo);
			    
			    cursor.moveToNext();
			}
			cursor.close();
			Log.i(TAG, "The List Size is:" + mMusicList.size());
			
		}
		
	}
	
	public void scannerImage(){
		
		Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
		mImageList.clear();
		String src;
		if(cursor.moveToFirst()){
			
			while(!cursor.isAfterLast()){
				
				src = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
				mImageList.add(src);
				cursor.moveToNext();
				Log.i(TAG, src);
			}
			
			cursor.close();
			Log.i(TAG, "The List Size is:" + mImageList.size());
		}
		
		
		
	}
	
	
	public String showTime(int time){
		
		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
		
		
	}
	
	
	
}
