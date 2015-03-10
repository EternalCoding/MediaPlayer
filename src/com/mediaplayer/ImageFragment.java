package com.mediaplayer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mediaplayer.ImageFragment.PhotoWallAdapter.BitmapWorkerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

@SuppressLint("ValidFragment")
public class  ImageFragment extends Fragment {
	private static final String TAG ="ImageFragment";
	
	
	View view;
	GestureDetector gd;
	ActionBar actionBar;
	private static byte touchValue = 0;
	
	private GridView mPhotoWall;
	private PhotoWallAdapter adapter;
	
	private Set<BitmapWorkerTask> taskCollection;
	
	public ImageFragment (){
		super();
	}
	
	
	@SuppressWarnings("deprecation")
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanece){
		
		
		 
		
		actionBar = getActivity().getActionBar();
		gd = new GestureDetector(new SimpleGestureDetector());
		view = inflater.inflate(R.layout.img_part, container, false);
		
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
		
		mPhotoWall = (GridView) view.findViewById(R.id.photoWall);
		adapter = new PhotoWallAdapter(this.getActivity(), 0, MusicActivity.mImageList, mPhotoWall);
		mPhotoWall.setAdapter(adapter);
		
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

	
	public class PhotoWallAdapter extends ArrayAdapter<String> implements OnScrollListener{

		private LruCache<String, Bitmap> mMemoryCache;
		private int mFirstVisibleItem, mVisibleItemCount;
		private boolean isFirstEnter = true;
		GridView mPhotoWall;
				
		public PhotoWallAdapter(Context context, int resource, List<String> mImageList, GridView photoWall) {
			super(context, resource, mImageList);
			// TODO Auto-generated constructor stub
			mPhotoWall = photoWall;
			taskCollection = new HashSet<BitmapWorkerTask>();  
			int maxMemory = (int) Runtime.getRuntime().maxMemory();
			int cacheSize = maxMemory/4;
			
			int size = cacheSize/1024;
			Log.i(TAG, "cacheSize :"  + size/1024);
			
			mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
				 
				@Override  
		           protected int sizeOf(String key, Bitmap bitmap) {  
		                return bitmap.getByteCount();  
		            }
			};
			mPhotoWall.setOnScrollListener(this);
			
		}
		
		
		@Override  
	    public View getView(int position, View convertView, ViewGroup parent) {  
	        final String url = getItem(position);  
	        View view;  
	        if (convertView == null) {  
	            view = LayoutInflater.from(getContext()).inflate(R.layout.photo_layout, null);  
	        } else {  
	            view = convertView;  
	        }  
	        final ImageView photo = (ImageView) view.findViewById(R.id.photo);  
	        // ��ImageView����һ��Tag����֤�첽����ͼƬʱ��������  
	        photo.setTag(url);  
	        setImageView(url, photo);  
	        return view;  
	    }  
	  
		
		 private void setImageView(String imageUrl, ImageView imageView) {  
		        Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);  
		        if (bitmap != null) {  
		            imageView.setImageBitmap(bitmap);  
		        } else {  
		            imageView.setImageResource(R.drawable.smiley_28);  
		        }  
		    }  
		
		 
		 public void addBitmapToMemoryCache(String key, Bitmap bitmap) {  
		        if (getBitmapFromMemoryCache(key) == null) {  
		            mMemoryCache.put(key, bitmap);  
		        }  
		    }  
		 
		 public Bitmap getBitmapFromMemoryCache(String key) {  
		        return mMemoryCache.get(key);  
		    }  
		 

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			mFirstVisibleItem = firstVisibleItem;  
	        mVisibleItemCount = visibleItemCount; 
			
	        Log.i(TAG, "isFirstEnter " + isFirstEnter  );
	        Log.i(TAG, "1   mFirstVisibleItem: " + mFirstVisibleItem +", mVisibleItemCount : " + mVisibleItemCount);
	        
	        
	        if (isFirstEnter && visibleItemCount > 0) {  
	        	
	        	Log.i(TAG, "123");
	            loadBitmaps(firstVisibleItem, visibleItemCount);  
	            isFirstEnter = false;  
	        }  
	        
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			
			Log.i(TAG, "2   mFirstVisibleItem: " + mFirstVisibleItem +", mVisibleItemCount : " + mVisibleItemCount);
			
			if (scrollState == SCROLL_STATE_IDLE) {  
				
				loadBitmaps(mFirstVisibleItem, mVisibleItemCount);  
	        } else {
	        	cancelAllTasks(); 
	        }
			
		}

		 private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {  
		        try {  
		            for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {  
		                String imageUrl = MusicActivity.mImageList.get(i);  
		                Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);  
		                if (bitmap == null) {  
		                    
		                	//bitmap = decodeSampledBitmapFromResource(imageUrl, 100, 100);zou 
		                	//if(bitmap != null)   addBitmapToMemoryCache(imageUrl, bitmap);  
		                	
		                	BitmapWorkerTask task = new BitmapWorkerTask(); 
		                	taskCollection.add(task);
		                	task.execute(imageUrl);  
		                	
		                } else {  
		                    ImageView imageView = (ImageView) mPhotoWall.findViewWithTag(imageUrl);  
		                    if (imageView != null && bitmap != null) {  
		                        imageView.setImageBitmap(bitmap);  
		                    }  
		                }  
		            }  
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        }  
		    }
		 
		 
		 
		 
		 
		 
		 
		 public int calculateInSampleSize(BitmapFactory.Options options,  
			        int reqWidth, int reqHeight) {  
			    // ԴͼƬ�ĸ߶ȺͿ��  
			    final int height = options.outHeight;  
			    final int width = options.outWidth;  
			    int inSampleSize = 1;  
			    if (height > reqHeight || width > reqWidth) {  
			        // �����ʵ�ʿ�ߺ�Ŀ���ߵı���  
			        final int heightRatio = Math.round((float) height / (float) reqHeight);  
			        final int widthRatio = Math.round((float) width / (float) reqWidth);  
			        // ѡ���͸�����С�ı�����ΪinSampleSize��ֵ���������Ա�֤����ͼƬ�Ŀ�͸�  
			        // һ��������ڵ���Ŀ��Ŀ�͸ߡ�  
			        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;  
			    }  
			    return inSampleSize;  
			}  
		 
		 public   Bitmap decodeSampledBitmapFromResource(String path,  
			        int reqWidth, int reqHeight) {  
			    // ��һ�ν�����inJustDecodeBounds����Ϊtrue������ȡͼƬ��С  
			    final BitmapFactory.Options options = new BitmapFactory.Options();  
			    options.inJustDecodeBounds = true;  
			    BitmapFactory.decodeFile(path, options);
			    // �������涨��ķ�������inSampleSizeֵ  
			    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);  
			    // ʹ�û�ȡ����inSampleSizeֵ�ٴν���ͼƬ  
			    options.inJustDecodeBounds = false;  
			    return BitmapFactory.decodeFile(path, options);  
			}  
		 
		
		public  class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {  
			  
		        /** 
		         * ͼƬ��URL��ַ 
		         */  
		        private String imageUrl;  
		  
		        @Override  
		        protected Bitmap doInBackground(String... params) {  
		            imageUrl = params[0];  
		            // �ں�̨��ʼ����ͼƬ  
		            Bitmap bitmap = decodeSampledBitmapFromResource(imageUrl, 100, 100); 
		            if (bitmap != null) {  
		                // ͼƬ������ɺ󻺴浽LrcCache��  
		                addBitmapToMemoryCache(imageUrl, bitmap);  
		            }  
		            return bitmap;  
		        }  
		  
		        @Override  
		        protected void onPostExecute(Bitmap bitmap) {  
		            super.onPostExecute(bitmap);  
		            // ����Tag�ҵ���Ӧ��ImageView�ؼ��������غõ�ͼƬ��ʾ������  
		            ImageView imageView = (ImageView) mPhotoWall.findViewWithTag(imageUrl);  
		            if (imageView != null && bitmap != null) {  
		                imageView.setImageBitmap(bitmap);  
		            }  
		            taskCollection.remove(this);  
		        }  
		  
		    }  
		 
		 public void cancelAllTasks() {  
		        if (taskCollection != null) {  
		            for (BitmapWorkerTask task : taskCollection) {  
		                task.cancel(false);  
		            }  
		        }  
		    }  

	}

	
	@Override
	public void onDestroy(){
		
		Log.i(TAG, "MusicFragment onDestroy");
		super.onDestroy();
		adapter.cancelAllTasks();
	
	}
 
	
}
