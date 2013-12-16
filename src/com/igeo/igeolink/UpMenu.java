package com.igeo.igeolink;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;


public class UpMenu extends View{

	public UpMenu(Context context) {
		super(context);
		//init();
		// TODO Auto-generated constructor stub
	}
	
	public UpMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		//init();
		// TODO Auto-generated constructor stub
	}

	public UpMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//init();
		// TODO Auto-generated constructor stub
	}

	
	private void init()
	{
		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();   
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;	
	}
	
	@Override
	public void onDraw(Canvas c)
	{
		Paint paint=new Paint();
		Bitmap up=BitmapFactory.decodeResource(getResources(), R.drawable.upmenu_fon);
		
		for(int i=0;i<getWidth();i+=up.getWidth())
		{
			c.drawBitmap(up, i, 0,paint);
		}
		
	}
	
	
	/*
	@Override
	public boolean onTouchEvent (MotionEvent event)
	{
		int action=event.getAction();
		int actionCode=action&MotionEvent.ACTION_MASK;
		float x=event.getX();
		float y=event.getY();
		Bitmap bitmap;
		
		switch(actionCode)
		{
		case MotionEvent.ACTION_DOWN:
			for(int i=0;i<el.length;i++)
			{
				if(el[i].Test(x, y))
				{
					switch(i)
					{
					case 0:
						bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.traffic_green);
						el[i].setBitmap(bitmap);
						
						break;
					case 1:
						bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.traffic_yellow);
						el[i].setBitmap(bitmap);
						break;
					case 2:
						bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.traffic_red);
						el[i].setBitmap(bitmap);
						
						break;
					
					}
				}
			}
			super.invalidate();
			break;
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_UP:
			for(int i=0;i<el.length;i++)
			{
				if(el[i].isTouch())
				{
					switch(i)
					{
					case 0:
						bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.light);
						el[i].setBitmap(bitmap);
						break;
					case 1:
						bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.light);
						el[i].setBitmap(bitmap);
						break;
					case 2:
						bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.light);
						el[i].setBitmap(bitmap);
						break;
					}
				}
				el[i].setTouch(false);
			}
			super.invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		}
		
		return true;
	}
	*/
}
