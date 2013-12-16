package com.igeo.igeolink;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

public class FrdLayout extends FrameLayout {

	private ImageView img;
	private TextView txt;
	
	public FrdLayout(final Context context, int defStyle) {
		super(context);
		final LinearLayout layout = new LinearLayout(context);
		layout.setVisibility(VISIBLE);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.frd_layout, layout);
		img = (ImageView) v.findViewById(R.id.frd_layout_img);
		txt = (TextView) v.findViewById(R.id.frd_layout_text);
		
		
		img.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				//Toast.makeText(context, txt.getText(), 500).show();
			}
		});
		
		img.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				switch(action) {
				case MotionEvent.ACTION_DOWN:
					//layout.setBackgroundResource(R.drawable.balloon_overlay_focused);
					Intent intent1 = new Intent(context, FrdProfile.class);
					intent1.putExtra("uid", txt.getText().toString());
					context.startActivity(intent1);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					//layout.setBackgroundResource(R.drawable.balloon_overlay_unfocused);
					break;
				}
				return false;
			}
		});
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.NO_GRAVITY;

		addView(layout, params);
	}
	
	public void setData(String urlBitmap, int uid) {		
		ImageLoader.DisplayImage(urlBitmap, img, true);
		txt.setText(uid + "");
	}

}
