package com.mediaplayer;

import java.io.IOException;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android.media.MediaPlayer;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener{
	private static final String TAG = "PlayerSerice";
	
	public static MediaPlayer mediaPlayer = null;
	
	private int CMD;
 
	
	@Override
	public void onCreate(){
		super.onCreate();
		
		Log.i(TAG,"Service Created");
		
		if(mediaPlayer != null){
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
			
		}
	    
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
	}
	
	@SuppressLint("HandlerLeak")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		 
		Log.i(TAG, "CMD COMING");
		
		int item = 0;
		
		try{
		
			CMD = (int)intent.getIntExtra("CMD", AppConstant.PlayerMsg.PLAY);	
			item = ((int)intent.getIntExtra("ITEM", MusicActivity.currentMusicListItem));
		
		} catch (NullPointerException e ){
			e.printStackTrace();
		}  catch (IndexOutOfBoundsException e ){
			e.printStackTrace();
		}   
		
		Log.i(TAG, "The List Size:" + MusicActivity.mMusicList.size() + " the item: " + item);	
		
		playMusic(item);
		
		
		/*
		final Thread mThread = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int currentPosition = 0;
				
				int total = mediaPlayer.getDuration();
				
				Log.i(TAG,"Total: " + total);
				
				
				 while(mediaPlayer != null && currentPosition <= total){
					 
					 try{
						 Thread.sleep(1000);
						 if(mediaPlayer != null){ 
							
							 currentPosition = mediaPlayer.getCurrentPosition();
							
						 }
					 }catch(InterruptedException  e){
						 
						 e.printStackTrace();
					 }
					 
					 //Log.i(TAG, "Current: " + currentPosition);
					 MusicFragment.songProgressBar.setProgress(currentPosition);
					 
					 //MusicFragment.currentSongTime.setText(showTime(currentPosition)); 
				 }	
			}
			});
			mThread.start();	
		 */
		
		return super.onStartCommand(intent, flags, startId);
		
	}
	
	
	
	public void playMusic(int item){
		
		 try {   
	             
	              
	            mediaPlayer.reset();               
	            mediaPlayer.setDataSource(MusicActivity.mMusicList.get(item).getSongPath());           
	            mediaPlayer.prepare();                    
	            mediaPlayer.start();              
	            mediaPlayer.setLooping(false);           
	              
	            MusicFragment.songProgressBar.setMax(MusicActivity.mMusicList.get(item).getSongDuration());//设置播放进度条的最大值    
	            MusicFragment.songName.setText(MusicActivity.mMusicList.get(item).getSongName());
	            MusicFragment.songInfo.setText(MusicActivity.mMusicList.get(item).getSongArtistAlbum());
	            MusicFragment.endSongTime.setText(MusicActivity.mMusicList.get(item).getSongTime());
	            
	            
	        } catch (IllegalArgumentException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        } catch (SecurityException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        } catch (IllegalStateException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  catch (NullPointerException e ){
				e.printStackTrace();
			}  catch (IndexOutOfBoundsException e ){
				e.printStackTrace();
				 
				 
			}          
		
		
	}
	
	
	
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
		Log.e(TAG,"F U C K ");
		switch(MusicFragment.playMode){
		
			case AppConstant.PlayerMsg.PLAYMODE_REPEATONE:
				playMusic(MusicActivity.currentMusicListItem);
				break;
			case  AppConstant.PlayerMsg.PLAYMODE_REPEAT:
				if(++MusicActivity.currentMusicListItem == MusicActivity.mMusicList.size()) {
					
					MusicActivity.currentMusicListItem = 0;
				} 
				 playMusic(MusicActivity.currentMusicListItem);
				
				break;
			case AppConstant.PlayerMsg.PLAYMODE_SEQUENCE:
				
				if(++MusicActivity.currentMusicListItem > MusicActivity.mMusicList.size()){
					mediaPlayer.pause();
					MusicActivity.currentMusicListItem--;
					
					Toast.makeText(this, "顺序播放，已经是歌单最后一首", Toast.LENGTH_SHORT).show();
				
				} else {
					playMusic(MusicActivity.currentMusicListItem);
					
				}
				break;
			case AppConstant.PlayerMsg.PLAYMODE_RANDOM:
				
				Random random = new Random();
				MusicActivity.currentMusicListItem = random.nextInt(MusicActivity.mMusicList.size());
				playMusic(MusicActivity.currentMusicListItem);
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
	
	
	
	
}
