package com.igeo.igeolink;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.igeo.igeolink.ImageLoader.BitmapDisplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class FrdAlbums {
	private static Map<String, Bitmap> album = Collections.synchronizedMap(new HashMap<String, Bitmap>());
	private static Map<String, String> largePhoto = Collections.synchronizedMap(new HashMap<String, String>());
	private static Queue<PhotoToLoad> queue = new LinkedList<PhotoToLoad>();
	private static Activity activity;
	
	public FrdAlbums(Context context) {
		activity = (Activity) context;
		loadAlbum.start();
	}
	
	final static int stub_id = R.drawable.camera_a;
    private static boolean isRadius = false;
    public static void DisplayImage(String url, ImageView imageView)
    {
    	isRadius = false;
    	if(album.containsKey(url)) {
    		imageView.setImageBitmap(album.get(url));
    	} else {
    		synchronized (queue) {
    			PhotoToLoad p = new PhotoToLoad(url, imageView);
				queue.add(p);
				queue.notifyAll();
				imageView.setImageResource(stub_id);
			}
    	}
    }
	
	Thread loadAlbum = new Thread(new Runnable() {
		
		public void run() {
			try {
				synchronized (queue) {
					while(true) {
						if(queue.isEmpty())
							queue.wait();
						final PhotoToLoad photo = queue.peek();
						queue.remove();
						Bitmap bitmap = null;
						InputStream in = new java.net.URL(photo.getUrl()).openStream();
						bitmap = BitmapFactory.decodeStream(in);
						album.put(photo.getUrl(), bitmap);
						final Bitmap b = bitmap;
						activity.runOnUiThread(new Runnable() {
							
							public void run() {
								photo.getImageView().setImageBitmap(b);
							}
						});
					}
				}
			} catch (Exception e) {}
		}
	});
	
	public static void setLargePhoto(String small, String large) {
		largePhoto.put(small, large);
	}
	
	public static String getLargePhoto(String small) {
		return largePhoto.get(small);
	}
	
	public static Bitmap getBitmapById(String big_img_id) {
		if(album.containsKey(big_img_id))
			return album.get(big_img_id);
		return null;
	}
	
}
