package com.igeo.igeolink;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import requests.PostGetRequests;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.GridView;
import android.widget.Toast;

class LoadGallery {
	private String src;
	private String src_big;
	private String text;
	private int date;
	
	public LoadGallery(String src, String src_big, String text, int date) {
		this.src = src;
		this.src_big = src_big;
		this.text = text;
		this.date = date;
	}
	
	public String getSrc() {
		return src;
	}
	
	public String getBigSrc() {
		return src_big;
	}
	
	public String getText() {
		return text;
	}
	
	public int getDate() {
		return date;
	}
}

public class FrdPhotos extends Activity {
	private Context context;
	private int frd_id;
	private ArrayList<LoadGallery>  loadGallery = new ArrayList<LoadGallery>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frd_photos);
		Intent intent = getIntent();
		frd_id = intent.getIntExtra("frd_id", 1);
		context = this;
		loadPhotos.start();
	}
	private String txt ;
	Thread loadPhotos = new Thread(new Runnable() {
		
		public void run() {
			try {
				String getAllPhotos = "https://api.vk.com/method/photos.getAll?&access_token=" + VK_Data.token + "&owner_id=" + frd_id;
				HttpResponse response = PostGetRequests.getReq(getAllPhotos);
	            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	            String line = "";
	            StringBuffer sb = new StringBuffer();

	            while ((line=in.readLine()) != null) {
	                sb.append(line);
	            }
	            in.close();
	            
	            
	            final JSONArray arr = (new JSONObject(sb.toString())).getJSONArray("response");
	            int num = arr.getInt(0);
	            String text = "";
	            for(int i = 1; i < arr.length(); i++) {
	                JSONObject obj1 = arr.getJSONObject(i);
	                //text += obj1.getString("src") + obj1.getString("src_big") + obj1.getString("text") + obj1.getInt("created");
	                loadGallery.add(new LoadGallery(obj1.getString("src"), obj1.getString("src_big"), obj1.getString("text"), obj1.getInt("created")));
	            }
	            //txt += text;
	            handler.sendEmptyMessage(SUCCESS);
			} catch (final Exception e) {
				runOnUiThread(new Runnable() {
					
					public void run() {
						Toast.makeText(getApplicationContext(), e.getMessage(), 500).show();
					}
				});
			}
		}
	});
	
	private static final int SUCCESS = 1000;
	Handler handler = new Handler(){
		 public void handleMessage(android.os.Message msg) {
			 switch(msg.what) {
			 case SUCCESS:
				 Toast.makeText(getApplicationContext(), "LOAD" + txt, 500).show();
				 GridView gridView = (GridView)findViewById(R.id.gridView);
				 GridAdapter adapter = new GridAdapter(context, loadGallery);
				 gridView.setAdapter(adapter);
				 break;
			 }
		 }
	};

}
