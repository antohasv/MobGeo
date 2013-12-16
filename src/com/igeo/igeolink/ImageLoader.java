package com.igeo.igeolink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.widget.ImageView;

class PhotoToLoad {
	private String url;
	private ImageView img;
	
	PhotoToLoad(String url, ImageView img) {
		this.url = url;
		this.img = img;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public ImageView getImageView() {
		return this.img;
	}
	
}

public class ImageLoader {
	public  static Map<String, Bitmap> cache = Collections.synchronizedMap(new HashMap<String, Bitmap>());

	private static final int stub_id = R.drawable.camera_a;
	private static Queue<PhotoToLoad> queue  = new LinkedList<PhotoToLoad>();
    Handler handler=new Handler();//handler to display images in UI thread
    
	public ImageLoader(Context context) {
		th.start();		
	}
	
    private static boolean isRadius = false;
    public static void DisplayImage(String url, ImageView imageView, boolean isRadius)
    {
    	isRadius = false;
    	if(cache.containsKey(url)) {
    		if(isRadius)
    			imageView.setImageBitmap(getRoundedCornerBitmap(cache.get(url)));
    		else
    			imageView.setImageBitmap(cache.get(url));
    	} else if (FileCache.getFile(url).exists()) {
    		imageView.setImageBitmap(BitmapFactory.decodeFile(FileCache.getFile(url).getAbsolutePath()));
    	} else {
    		synchronized (queue) {
    			PhotoToLoad p = new PhotoToLoad(url, imageView);
				queue.add(p);
				queue.notifyAll();
				imageView.setImageResource(stub_id);
			}
    	}
    	//File imgFile = new  File(“/sdcard/Images/test_image.jpg”);
    	//if(imgFile.exists()){
    }
    
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	        bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = 12;

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);

	    return output;
	  }
    
    Thread th = new Thread(new Runnable() {
		
		public void run() {
			try {
				synchronized (queue) {
					while(true) {
						if(queue.isEmpty())
							queue.wait();
						PhotoToLoad photo = queue.peek();
						queue.remove();
						Bitmap bitmap = null;
						InputStream in = new java.net.URL(photo.getUrl()).openStream();
						bitmap = BitmapFactory.decodeStream(in);
						cache.put(photo.getUrl(), bitmap);
						BitmapDisplayer bd = new BitmapDisplayer(bitmap, photo);
		                handler.post(bd);
					}
				}
			} catch (Exception e) {
				
			}
		}
	});
    
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p)
        {
        	bitmap=b;
        	photoToLoad=p;
        	try {
        	       FileOutputStream out = new FileOutputStream(FileCache.getFile(p.getUrl()));
        	       b.compress(Bitmap.CompressFormat.PNG, 90, out);
        	} catch (Exception e) {
        	       e.printStackTrace();
        	}

        }
        public void run()
        {
            if(bitmap!=null)
            	if(isRadius)
            		photoToLoad.getImageView().setImageBitmap(getRoundedCornerBitmap(bitmap));
        		else
        			photoToLoad.getImageView().setImageBitmap(bitmap);
            else
                photoToLoad.getImageView().setImageResource(stub_id);
        }
    }
}
