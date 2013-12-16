package com.igeo.igeolink;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import requests.PostGetRequests;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FrdProfile extends MapActivity {
	
	private Context context;
	private int frd_id;
	private String str_frd_id;
	private LinearLayout album = null;
	
	private int width;
	private int height;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frd_profile);
		
		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		
		context = this;
		TextView title = (TextView) findViewById(R.id.frd_profile_txt1);
		ImageView m_img = (ImageView) findViewById(R.id.frd_profile_img1);
		TextView loc = (TextView) findViewById(R.id.frd_profile_txt3);
		Button mes = (Button) findViewById(R.id.frd_profile_but1);
		Button albom = (Button) findViewById(R.id.frd_progile_albumBut1);
		
		MapView map = (MapView) findViewById(R.id.frd_profile_map);
		MapController control = map.getController();

		Intent intent = getIntent();
		frd_id = Integer.parseInt(intent.getStringExtra("uid"));
		
		str_frd_id = intent.getStringExtra("uid");
		
		ImageLoader.DisplayImage(CMap.frd_list.get(frd_id).getImageUrl(), m_img, true);
		m_img.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(context, ShowPhoto.class);
				FrdAlbums.setLargePhoto(CMap.frd_list.get(frd_id).getImageUrl(), CMap.frd_list.get(frd_id).getPhotoBig());
				intent.putExtra("photo", CMap.frd_list.get(frd_id).getImageUrl());
				startActivity(intent);
			}
		});
		title.setText(CMap.frd_list.get(frd_id).getLastName() + " " + CMap.frd_list.get(frd_id).getFirstName());
		
		ImageView marker = new ImageView(getApplicationContext());
		GeoPoint geoPoint = new GeoPoint((int)(CMap.frd_loc.get(frd_id).getLatitude()*1E6), (int)(CMap.frd_loc.get(frd_id).getLongitude()*1E6));
		control.animateTo(geoPoint);
		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.mark);
		marker.setImageBitmap(b);
		MapView.LayoutParams mapImage = new MapView.LayoutParams(100, 150, geoPoint, MapView.LayoutParams.BOTTOM_CENTER);
		map.addView(marker, mapImage);
		
		LinearLayout linear = new LinearLayout(this);
		linear.setBackgroundColor(Color.WHITE);
		linear.setOrientation(LinearLayout.HORIZONTAL);
		TextView text1 = new TextView(this);
		text1.setText("State:Moscow");
		linear.addView(text1);
		
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();   
		MapView.LayoutParams mapLocator = new MapView.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 
																				100, 100, MapView.LayoutParams.TOP_LEFT);

		mes.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.frd_profile_but1:
					Intent intent = new Intent(context, FrdMessages.class);
					intent.putExtra("frd_id", frd_id);
					context.startActivity(intent);
					break;
				}
			}
		});
		
		albom.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(context, FrdPhotos.class);
				intent.putExtra("frd_id", frd_id);
				context.startActivity(intent);
			}
		});
		
		album = (LinearLayout) findViewById(R.id.album);
		th.start();
		
		Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
		try {
			List<Address> addr = gc.getFromLocation(CMap.frd_loc.get(frd_id).getLatitude(), CMap.frd_loc.get(frd_id).getLongitude(), 1);
			StringBuilder sb = new StringBuilder();
			if(addr.size() > 0) {
				Address a = addr.get(0);
				sb.append("Country:").append(a.getCountryName()).append("\n");
				sb.append("Address:").append(a.getAddressLine(0)).append("\n");
				loc.setText(sb.toString());
			}
		} catch (Exception e) {
			
		}
		
	}
	
	Thread th = new Thread(new Runnable() {
		
		public void run() {
			try {
				String getAllPhotos = "https://api.vk.com/method/photos.getAll?&access_token=" + VK_Data.token + "&owner_id=" + str_frd_id;
				HttpResponse response = PostGetRequests.getReq(getAllPhotos);
	            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	            String line = "";
	            StringBuffer sb = new StringBuffer();

	            while ((line=in.readLine()) != null) {
	                sb.append(line);
	            }
	            in.close();
	            
	            final int FINISH = 1000;
	            Handler handler = new Handler(){
	            	
	            };
	            final JSONArray arr = (new JSONObject(sb.toString())).getJSONArray("response");
	            runOnUiThread(new Runnable() {
					
					public void run() {
						try {
							int num = arr.getInt(0);
							if(num > 3) {
								LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
								for(int i = 1; i <= 3; i++) {
					                JSONObject obj1 = arr.getJSONObject(i);
					                final String src = obj1.getString("src");
					                FrdAlbums.setLargePhoto(src, obj1.getString("src_big"));
					                ImageView img = new ImageView(context);
					                lp.setMargins(5, 0, 5, 0);
					                img.setLayoutParams(lp);
					                FrdAlbums.DisplayImage(src, img);
					                img.setOnClickListener(new OnClickListener() {
										
										public void onClick(View v) {
											Intent intent = new Intent(context, ShowPhoto.class);
											intent.putExtra("photo", src);
											startActivity(intent);
										}
									});
					                album.addView(img);
					            }
							} else if(num > 0) {
								LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
								for(int i = 1; i <= num; i++) {
					                JSONObject obj1 = arr.getJSONObject(i);
					                final String src = obj1.getString("src");
					                FrdAlbums.setLargePhoto(src, obj1.getString("src_big"));
					                ImageView img = new ImageView(context);
					                lp.setMargins(5, 0, 5, 0);
					                img.setLayoutParams(lp);
					                ImageLoader.DisplayImage(src, img, false);
					                img.setOnClickListener(new OnClickListener() {
										
					                	public void onClick(View v) {
											Intent intent = new Intent(context, ShowPhoto.class);
											intent.putExtra("photo", src);
											startActivity(intent);
										}
									});
					                album.addView(img);
					            }
							}
							
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(), e.getMessage(), 500).show();
						}
					}
				});
	            
			} catch (final Exception e) {
				runOnUiThread(new Runnable() {
					
					public void run() {
						Toast.makeText(getApplicationContext(), "" + e.getMessage(), 500).show();
					}
				});
			}
		}
	});

	@Override
	protected boolean isRouteDisplayed() {
		
		return false;
	}

}
