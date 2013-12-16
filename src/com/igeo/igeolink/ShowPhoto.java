package com.igeo.igeolink;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ShowPhoto extends Activity{
	private Context context;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_photo);
		
		context = this;
		Intent intent = getIntent();
		String src = intent.getStringExtra("photo");
		String big_src = FrdAlbums.getLargePhoto(src);
		
		final ImageView img = (ImageView) findViewById(R.id.showphoto_img1);
		FrdAlbums.DisplayImage(big_src, img);
		
		File file = null;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            file = new File(android.os.Environment.getExternalStorageDirectory(),"IGeo/SavedImage");
        else
            file = context.getCacheDir();
        if(!file.exists())
            file.mkdirs();
        
		img.setScaleType(ScaleType.MATRIX);
		final PointF start = new PointF();
		final PointF mid = new PointF();
		/*
		img.setOnTouchListener(new OnTouchListener() {
			
			private int mode = 0;
			private Matrix matrix = new Matrix();
			private Matrix savedMatrix = new Matrix();
			private static final int DRAG = 100;
			private static final int ZOOM = 101;
			private static final int NONE = 0;
			
			private static final float MIN_ZOOM = 1.0f;
			private static final float MAX_ZOOM = 5.0f;
			float oldDist = 1f;
			
			
			private float dx; // postTranslate X distance
			private float dy; // postTranslate Y distance
			private float[] matrixValues = new float[9];
			float matrixX = 0; // X coordinate of matrix inside the ImageView
			float matrixY = 0; // Y coordinate of matrix inside the ImageView
			float width = 0; // width of drawable
			float height = 0; // height of drawable
			
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch(action & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					if(oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
				        matrix.set(savedMatrix);
				        matrix.getValues(matrixValues);
				        matrixX = matrixValues[2];
				        matrixY = matrixValues[5];
				        width = matrixValues[0] * (((ImageView) img).getDrawable().getIntrinsicWidth());
				        height = matrixValues[4] * (((ImageView) img).getDrawable().getIntrinsicHeight());

				        dx = event.getX() - start.x;
				        dy = event.getY() - start.y;

				        //if image will go outside left bound
				        if (matrixX + dx < 0){
				            dx = -matrixX;
				        }
				        //if image will go outside right bound
				        if(matrixX + dx + width > img.getWidth()){
				            dx = img.getWidth() - matrixX - width;
				        }
				        //if image will go oustside top bound
				        if (matrixY + dy < 0){
				            dy = -matrixY;
				        }
				        //if image will go outside bottom bound
				        if(matrixY + dy + height > img.getHeight()){
				            dy = img.getHeight() - matrixY - height;
				        }
				        matrix.postTranslate(dx, dy);   
				    } else if (mode == ZOOM) {
						float newDist = spacing(event);
						if(newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, mid.x, mid.y);
							Log.d("TOOOOP", "Scale:" + scale + "MID.X:" + mid.x + "MID.Y:" + mid.y);
						}
					}
					break;
				}
				
				img.setImageMatrix(matrix);
				return true;
			}
			
			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}
			
			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x/2, y/2);
			}
		});
		*/
		final RelativeLayout upPanel = (RelativeLayout)findViewById(R.id.showPhotoUpPanel);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.showphoto_linear);
		
		/*
		 * 
		 * if(upPanel.getVisibility() == View.GONE)
					upPanel.setVisibility(View.VISIBLE);
				else 
					upPanel.setVisibility(View.GONE);
		 */
		TextView nums = (TextView)findViewById(R.id.showPhotoNums);
		nums.setText("1 of 10");
		ImageView back = (ImageView)findViewById(R.id.showPhotoBack);
		back.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
		
		Button save = (Button)findViewById(R.id.showPhotoSave);
		
		//BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
		Bitmap bitmap = null;
		bitmap = FrdAlbums.getBitmapById(big_src);
		if(bitmap != null)
			storeImage(BitmapFactory.decodeResource(getResources(), R.drawable.camera_a), file);
		save.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Toast.makeText(getApplicationContext(), "save", 500).show();
			}
		});
		
	}
	
	private void storeImage(Bitmap bitmap, File file) {
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), 500).show();
		}
	}
}
