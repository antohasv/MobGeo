package com.igeo.igeolink;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.igeo.igeolink.CMap.Messanger;
import com.igeo.igeolink.CMap.Observer;

import requests.PostGetRequests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;

public class FrdMessages  extends Activity implements OnClickListener, Observer, Runnable{
	
	private ListView list;
	private Button butSend;
	private EditText messField;
	private TextView title;
	private ImageView photo;
	private ImageView back;
	
	private Context context;
	private Cursor cursor;
	private int frd_id;
	
	private Queue<MessManager> queue = new LinkedList<MessManager>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vk_mess);
		
		context= this;
		CMap.mess.registerObserver(this);
		Intent  intent = this.getIntent();
		
		frd_id = intent.getIntExtra("frd_id", 1);
		CMap.manager.cancel(frd_id);
		list = (ListView) findViewById(R.id.frd_mess_list);
		butSend = (Button) findViewById(R.id.frd_mess_submit);
		title = (TextView) findViewById(R.id.frd_mess_title);
		photo = (ImageView) findViewById(R.id.frd_mess_img);
		back = (ImageView) findViewById(R.id.frd_mess_back);
		messField = (EditText) findViewById(R.id.frd_mess_text);
		butSend.setOnClickListener(this);
		
		title.setText(CMap.frd_list.get(frd_id).getLastName() + " " + CMap.frd_list.get(frd_id).getFirstName());
		ImageLoader.DisplayImage(CMap.frd_list.get(frd_id).getImageUrl(), photo, true);
		back.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
		
		senderMess.start();//vk 10 last ten messages
		ArrayList<Message> messages = new ArrayList<Message>();
		cursor = CMap.db.getFrdMessages(frd_id);//Get Last 10 messages
		startManagingCursor(cursor);
		if(cursor.moveToFirst()) {
			do {
				if(cursor.getInt(cursor.getColumnIndex(DB.COLUMN_OUT)) == 1)
					messages.add(new Message(cursor.getString(cursor.getColumnIndex(DB.COLUMN_TXT)), true));
				else 
					messages.add(new Message(cursor.getString(cursor.getColumnIndex(DB.COLUMN_TXT)), false));
			} while(cursor.moveToNext());
		}
		
		list.setAdapter(new AwesomeAdapter(context, messages));
		list.setSelection(list.getCount() - 1);
	  }
	
	private class  MessManager {
		private int frd_id;
		private String message;
		
		public MessManager(int frd_id, String message) {
			this.frd_id = frd_id;
			this.message = message;
		}
		
		public int getFrdId() {
			return frd_id;
		}
		
		public String getMessage() {
			return this.message;
		}
	}
	
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.frd_mess_submit:
			String text = messField.getText().toString();
			if(!text.equals("")) {
				Toast.makeText(getApplicationContext(), text, 500).show();
				synchronized (queue) {
					queue.add(new MessManager(frd_id, text));
					queue.notifyAll();
				}
			}
			break;
		}	
	}
	  
	public void addMessage(String message, int whoWrite, int date, boolean isRead) {
		if(whoWrite == 0) 
			CMap.db.addRec(VK_Data.user_id, frd_id, message, date, 0, isRead);
		else if(whoWrite == 1) 
			CMap.db.addRec(VK_Data.user_id, frd_id, message, date, 1, isRead);
		cursor.requery();
		list.setSelection(list.getCount() - 1);
	}
	
	/* 10 Last Messages and Launch Thread Sender Messages*/
	Thread senderMess = new Thread(new Runnable() {
		
		public void run() {
			String url = "";
			String line = "";
			HttpResponse res = null;
			BufferedReader in = null;
			StringBuffer sb = null;
			JSONObject obj = null;
			
			try {
				url = "https://api.vk.com/method/messages.getHistory?&access_token=" + VK_Data.token + "&uid=" + frd_id + "&count=10";
				res = PostGetRequests.getReq(url);
				in = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
				sb = new StringBuffer();
	
	            while ((line = in.readLine()) != null) {
	                sb.append(line);
	            }
	            in.close();
	            
	           
	            obj = new JSONObject(sb.toString());
	            JSONArray arr = obj.getJSONArray("response");
	            
	            for(int i = arr.length() - 1 ; i > 0 ; i--) {
	                JSONObject obj1 = arr.getJSONObject(i);
	                final String body = obj1.getString("body");
	                final int uid = obj1.getInt("uid");
	                final int date = obj1.getInt("date");
	                final int read_state = obj1.getInt("read_state");
	                runOnUiThread(new Runnable() {
						
						public void run() {
							boolean t = false;
							if(read_state == 1)
								t = true;
							if(VK_Data.user_id == uid) 
								addMessage(body, 1, date, t);
							else 
								addMessage(body, 0, date, t);
						}
					});
	            }
				
	            /*Send Message*/
				synchronized (queue) {
					while(true) {
		            	if(queue.isEmpty()) 
		            		queue.wait();
						CMap.manager.cancel(frd_id);	
		            	final MessManager q = queue.peek();
		            	queue.remove();
		            	url = "https://api.vk.com/method/messages.send?&access_token=" + VK_Data.token + "&uid=" + q.getFrdId() + "&message=" + q.getMessage();
		            	res = PostGetRequests.getReq(url);
						in = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
						sb = new StringBuffer();
			
			            while ((line = in.readLine()) != null) {
			                sb.append(line);
			            }
			            in.close();
			            
			            obj = new JSONObject(sb.toString());
			            runOnUiThread(new Runnable() {
							
							public void run() {
								Toast.makeText(getApplicationContext(), "Go", 500).show();
								messField.setText("");
							}
						});
		            }
				}
			} catch (Exception e) {
				
			}
		}
	});
	
	String getFrdMes = "https://api.vk.com/method/photos.getById?&access_token=" + VK_Data.token;
	public void update(JSONArray arr, int frd_id) {
		try {
			if(arr.getInt(0) == 4) {
				if(this.frd_id == frd_id) {
					JSONObject att = arr.getJSONObject(7);
					if((arr.getInt(2) & 2) == 0) {
						 if(att.length() != 0) {
			                    if(att.getString("attach1_type").equals("photo")) {
			                    	getFrdMes += "&photos=" + att.getString("attach1");
			                    	loadFile.start(); 
			                    }
			                } else {
			                	addMessage(arr.getString(6), 0, arr.getInt(4), true);
			                }
					} else {
						 if(att.length() != 0) {
			                    if(att.getString("attach1_type").equals("photo")) {
			                    	getFrdMes += "&photos=" + att.getString("attach1");
			                    	loadFile.start(); 
			                    }
			                } else {
			                	addMessage(arr.getString(6), 1, arr.getInt(4), true);
			                }
					}
				}
			} else if(arr.getInt(0) == 61) {
				Toast.makeText(getApplicationContext(), "Write...", 500).show();
				if(!isType)
            		isType = true;
            	else 
            		if(pThread != null) 
                		pThread.interrupt();
            	Thread th = new Thread(this);
        		pThread = th;
        		th.start();
			}
		} catch(Exception e) {
			Toast.makeText(getApplicationContext(), "Error:" + e.getMessage() + "", 500).show();
		}
	}
	
	private boolean isType = false;
	private Thread pThread = null;
	public void run() {
		try {
			Thread.sleep(10000);
			runOnUiThread(new Runnable() {
				
				public void run() {
					Toast.makeText(getApplicationContext(), "OK", 500).show();
					isType = false;
				}
			});
		} catch (final Exception e) { }
	}
	
	Thread loadFile = new Thread(new Runnable() {
		
		public void run() {
			try {
				HttpResponse response = PostGetRequests.getReq(getFrdMes);
	            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	            String line = "";
	            StringBuffer sb = new StringBuffer();

	           while ((line=in.readLine()) != null) {
	               sb.append(line);
	           }
	           in.close();
	           Toast.makeText(getApplicationContext(), "Info:" + sb.toString(), 500).show();
	           final JSONObject obj3 = (new JSONObject(sb.toString())).getJSONArray("response").getJSONObject(0);
	           if(obj3.length() != 0) {
	        	   runOnUiThread(new Runnable() {
					
					public void run() {
				          try {
							Toast.makeText(getApplicationContext(), obj3.getString("src") + "", 500).show();
						} catch (JSONException e) {
							e.printStackTrace();
						}
			               
					}
				});
	           } 
			} catch (final Exception e) {
				 runOnUiThread(new Runnable() {
						
						public void run() {
								Toast.makeText(getApplicationContext(), e.getMessage(), 500).show();  
						}
					});
			}
	}});
	
}
