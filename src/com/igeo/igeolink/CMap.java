package com.igeo.igeolink;

import java.io.BufferedReader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.crypto.spec.OAEPParameterSpec;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import requests.PostGetRequests;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.MapView.LayoutParams;

public class CMap extends MapActivity  implements AnimationListener, OnTouchListener, OnClickListener {

	private Context map_context = null;
	private Activity map_activity = this;

	private int user_id = 0;
	private String pass = null;
	private String token = null;

	private Button but_frd;
	private Button but_map;
	private Button but_set;

	private Drawable empty ;
	private Drawable focus;
	private Drawable line;

	private ViewFlipper mFlipper;
	public Animation in;
	public Animation out;

	private MapView mapView;
	private MapController control;
	private List<Overlay> mapOverlays ;

	private ListView lv;

	private boolean but_state[] = new boolean[]{false, true};

	private Dialog busyDialog;

	public static  Map<Integer ,VK_Friends> frd_list = null;
	public static  Map<Integer, FrdLocations> frd_loc = null;

	private FileCache _file = null;
	private LocationsCache locCache = null;

	private ImageLoader imgLoader = null; 

	//Menu: Friends Map
	private void MapMenu() {	

		empty = this.getResources().getDrawable(R.drawable.top);
		line  = this.getResources().getDrawable(R.drawable.toptest);
		focus = this.getResources().getDrawable(R.drawable.topfocus);

		but_frd = (Button)findViewById(R.id.mapmenubut1);
		but_frd.setOnClickListener(this);
		but_frd.setOnTouchListener(this);

		but_map = (Button)findViewById(R.id.mapmenubut2);
		but_map.setOnClickListener(this);
		but_map.setOnTouchListener(this);

		but_map.setPressed(true);
		but_map.setBackgroundDrawable(line);

		butState(1);
	}


	private void butState(int index)
	{
		for (int i = 0; i < but_state.length; i++) {
			but_state[i] = false;
		}
		but_state[index] = true;
	}

	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.mapmenubut1:
			if (but_state[0] == false) {
				but_map.setBackgroundDrawable(empty);
				but_frd.setBackgroundDrawable(line);

				in = AnimationUtils.loadAnimation(map_context, R.anim.push_right_in);
				out = AnimationUtils.loadAnimation(map_context, R.anim.push_right_out);
				in.setAnimationListener((AnimationListener) map_context);
				out.setAnimationListener((AnimationListener) map_context);
				mFlipper.setInAnimation(in);
				mFlipper.setOutAnimation(out);
				mFlipper.showNext();
				butState(0);
			}

			break;
		case R.id.mapmenubut2:
			if (but_state[1] == false) {
				but_frd.setBackgroundDrawable(empty);
				but_map.setBackgroundDrawable(line);

				in = AnimationUtils.loadAnimation(map_context, R.anim.push_left_in);
				out = AnimationUtils.loadAnimation(map_context, R.anim.push_left_out);
				in.setAnimationListener(this);
				out.setAnimationListener((AnimationListener) map_context);
				mFlipper.setInAnimation(in);
				mFlipper.setOutAnimation(out);
				mFlipper.showNext();
				butState(1);
			}
			break;	
		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		Button but = (Button)v;
		switch(action)
		{
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_DOWN:
			but.setBackgroundDrawable(focus);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			but.setBackgroundDrawable(line);
			break;

		}
		return false;
	}

	private class MyQueue {
		private Queue<String> queue = new LinkedList<String>();

		public synchronized String getValue() throws Exception {
			if(queue.isEmpty()) 
				wait();
			String q = queue.peek();
			queue.remove();
			return q;
		}

		public synchronized void putValue(String s) {
			queue.add(s);
			notifyAll();
		}
	}

	private class MyAsyncTask extends AsyncTask<String, String, String> {
		private MyQueue queue;

		public MyAsyncTask(MyQueue que) {
			this.queue = que;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				Socket s = new Socket("192.168.0.100", 1050);
				DataInputStream in = new DataInputStream(s.getInputStream());
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				String result = "";
				while(true) {
					result = queue.getValue();
					out.writeUTF(result);
					out.flush();
					result = in.readUTF();
					publishProgress(result);
				}
			} catch (Exception e) {
				return null;
			}
		}

		//Cache for Locations

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			String res = values[0];
			try {
				if(res != null) {
					//result=location&data={"response":[{"uid":8414943,"lat":51.5175,"lng":-0.0899537},{"uid":5120485,"lat":7655480000,"lng":5853320000000},{"uid":18521121,"lat":51.5174,"lng":-0.086623}]}
					String []parts = res.split("&");
					String []data = parts[0].split("=");
					if(data[0].equals("result") && data[1].equals("location")) {
						data = parts[1].split("=");
						if(data[0].equals("data")) {
							Toast.makeText(getApplicationContext(),  data[1], 500).show();
							JSONObject obj = new JSONObject(data[1]);
							JSONArray arr = obj.getJSONArray("response");
							for(int i = 0; i < arr.length(); i++) {
								JSONObject _obj = arr.getJSONObject(i);
								frd_loc.put(_obj.getInt("uid"), new FrdLocations(_obj.getDouble("lat"), _obj.getDouble("lng")));
								updateFrdMarkers(_obj.getInt("uid"), _obj.getDouble("lat"), _obj.getDouble("lng"));
							}
						}
					}
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),  "Error:" + e.getMessage(), 500).show();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result == null)
				Toast.makeText(getApplicationContext(), "Network unavailable", 500).show();
		}
	}


	private class MyLocation {
		private MyQueue queue = null;
		private static final long minTime = 500;
		private static final float minDistance = 1;

		private LocationListener listener = new LocationListener() {

			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub

			}

			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			public void onLocationChanged(Location location) {
				if(location != null) {
					String s = "func=myLoc&name=" + VK_Data.user_id + "&pass=" + VK_Data.password 
							+ "&lat=" + location.getLatitude() + "&lng=" + location.getLongitude();
					queue.putValue(s);
				}
			}
		};

		public MyLocation(MyQueue q) {
			this.queue = q;

			LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Criteria cr = new Criteria();
			cr.setAccuracy(Criteria.ACCURACY_FINE);
			cr.setBearingRequired(false);
			cr.setAltitudeRequired(false);
			cr.setCostAllowed(true);
			cr.setPowerRequirement(Criteria.POWER_LOW);
			String provider = manager.getBestProvider(cr, true);

			Location loc = manager.getLastKnownLocation(provider);
			queue.putValue("func=myLoc&name=" + VK_Data.user_id + "&pass=" + VK_Data.password 
					+ "&lat=" + loc.getLatitude() + "&lng=" + loc.getLongitude());
			manager.requestLocationUpdates(provider, minTime, minDistance, listener);
		}
	}

	private class LoadFriends {
		private MyQueue queue;
		private String url_getFrd = "https://api.vk.com/method/friends.get?uid=" + VK_Data.user_id +
				"&fields=uid,first_name,last_name,nickname,photo_medium,photo_big&name_case=nom&order=hints&access_token=" + VK_Data.token;

		public LoadFriends(MyQueue que) {
			this.queue = que;
			th.start();
		}

		Thread th = new Thread(new Runnable() {

			public void run() {
				String data = "{\"response\":[";
				try {
					String text = "";
					HttpResponse res = PostGetRequests.getReq(url_getFrd);	
					BufferedReader reader  = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
					String line = "";
					while((line = reader.readLine()) != null) {
						text += line;
					}

					int z = 0;
					JSONObject obj = new JSONObject(text);
					JSONArray arr = obj.getJSONArray("response");

					for(int i = 0; i < arr.length(); i++) {
						if(z==0) z++; else data += ","; 
						JSONObject obj1 = arr.getJSONObject(i);
						data += "{\"uid\":" + obj1.getInt("uid") + "}";
						frd_list.put(obj1.getInt("uid"), new VK_Friends(obj1.getString("first_name"), obj1.getString("last_name"), obj1.getString("photo_medium"), obj1.getString("photo_big"),obj1.getInt("online")));
					}

					data += "]}";

					queue.putValue("func=getFrd&name=" + VK_Data.user_id + "&pass=" + VK_Data.password + "&data=" + data);
				} catch (Exception e) {
					map_activity.runOnUiThread(new Runnable() {

						public void run() {
							Toast.makeText(map_context, "Can't Load friends", 500).show();
						}
					});
				}
			}
		});
	}

	private static final int ID_NOTIFY = 100;

	Thread launchLongPoll = new Thread(new Runnable() {

		private Exception exp;

		public void run() {
			try {
				String line = "";
				String url_poll = "https://api.vk.com/method/messages.getLongPollServer?&access_token=" + VK_Data.token;
				HttpResponse res = PostGetRequests.getReq(url_poll);
				BufferedReader in = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
				StringBuffer sb = new StringBuffer();

				while ((line = in.readLine()) != null) {
					sb.append(line);
				}
				in.close();

				JSONObject obj = (new JSONObject(sb.toString())).getJSONObject("response");
				int ts = obj.getInt("ts");

				String url = "http://" + obj.getString("server") + "?act=a_check&key=" + obj.getString("key") + "&wait=25&mode=2";

				while(true) {

					res = PostGetRequests.getReq(url + "&ts=" + ts);
					in = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
					line = "";
					sb = new StringBuffer();

					while ((line=in.readLine()) != null) {
						sb.append(line);
					}
					in.close();
					final JSONObject obj1 = new JSONObject(sb.toString());
					ts = obj1.getInt("ts");
					runOnUiThread(new Runnable() {

						public void run() {
							try {
								JSONArray arr = obj1.getJSONArray("updates");
								for(int i = 0; i < arr.length(); i++) {
									JSONArray arr1 = arr.getJSONArray(i);
									if(arr1.length() == 8) {
										if(arr1.getInt(0) == 4) {
											if((arr1.getInt(2) & 2) == 0) {
												//Write Friends
												mess.setData(arr1, arr1.getInt(3));
												Notification notification = new Notification(R.drawable.iconmail, arr1.getString(6), System.currentTimeMillis());
												notification.number = 3;

												Intent intent = new Intent(map_context, FrdMessages.class);
												intent.putExtra("frd_id", arr1.getInt(3));
												PendingIntent pIntent = PendingIntent.getActivity(map_context, 0, intent, 0);
												notification.setLatestEventInfo(map_context, frd_list.get(arr1.getInt(3)).getLastName() + " " + frd_list.get(arr1.getInt(3)).getFirstName(), arr1.getString(6), pIntent);
												notification.when = arr1.getLong(4);
												notification.flags |= Notification.FLAG_AUTO_CANCEL;

												manager.notify(arr1.getInt(3), notification);

												vibrator.vibrate(500);

											} else if((arr1.getInt(2) & 2) == 2) {
												//Write user
												mess.setData(arr1, arr1.getInt(3));
											}
										}
									} else if(arr1.length() == 3) {
										if(arr1.getInt(0) == 61) {
											mess.setData(arr1, arr1.getInt(1));
										}
									}
								}
							} catch (Exception e) {

							}
						}
					});
				}

			} catch (final Exception e) {
				runOnUiThread(new Runnable() {

					public void run() {
						try {
							Toast.makeText(getApplicationContext(), "Error", 500).show();
						} catch (Exception e) {

						}
					}
				});
			}
		}
	});


	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public interface Observer {
		public void update(JSONArray updates, int frd_id);
	}

	public interface Observable {
		public void registerObserver(Observer o);
		public void removeObserver(Observer o);
		public void notifyObservers();
	}

	class Messanger implements Observable {
		private ArrayList<Observer> observers = new ArrayList<Observer>();
		//private HashMap<Integer, Observer> observers = new HashMap<Integer, CMap.Observer>();
		private JSONArray jarr;
		private int frd_id;
		private int time;

		public Messanger() {

		}

		public int getSize() {
			return observers.size();
		}
		public void setData(JSONArray jarr, int frd_id) {
			this.jarr = jarr;
			this.frd_id = frd_id;
			notifyObservers();
		}

		public void registerObserver(Observer o) {
			observers.add(o);
		}

		public void removeObserver(Observer o) {
			int i = observers.indexOf(o);
			if(i >= 0) observers.remove(i);
		}

		public void notifyObservers() {
			for(Observer o : observers) {
				o.update(this.jarr, this.frd_id);
			}
		}
	}

	public void updateFrdMarkers(Integer uid, double lat, double lng) {

		FrdLayout m_layout = new FrdLayout(this, 1);
		GeoPoint point = new GeoPoint((int)(lat*1E6), (int)(lng*1E6));
		control.animateTo(point);
		m_layout.setData(frd_list.get(uid).getImageUrl(), uid);
		MapView.LayoutParams params = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,point,MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;
		mapView.addView(m_layout,params);
		mapView.invalidate();
		//Toast.makeText(map_context, text, 500).show();
	}

	public static DB db;
	public static Messanger mess;
	private Vibrator vibrator = null;
	private static FrdAlbums frdAlbum = null; 
	public static NotificationManager manager;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		map_context = this;

		frd_list = new HashMap<Integer, VK_Friends>();
		frd_loc = new HashMap<Integer, FrdLocations>();

		_file = new FileCache(this);
		imgLoader = new ImageLoader(this);

		frdAlbum = new FrdAlbums(this);

		locCache = new LocationsCache(this);

		mapView = (MapView) findViewById(R.id.map);
		control = mapView.getController();
		mapOverlays = mapView.getOverlays();	

		mFlipper = (ViewFlipper) findViewById(R.id.flipper);
		lv = (ListView)findViewById(R.id.listView1);

		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		/*	Load Menu	*/
		MapMenu();

		//dismissBusyDialog();
		//showBusyDialog("Loading...");

		if(isOnline()) 
			Toast.makeText(getApplicationContext(), "Online", 500).show();
		else
			Toast.makeText(getApplicationContext(), "Offline", 500).show();

		MyQueue q = new MyQueue();
		MyAsyncTask task = new MyAsyncTask(q);
		task.execute(new String[]{"A", "B", "C"});
		MyLocation loc = new MyLocation(q);
		LoadFriends load_frd = new LoadFriends(q);

		/* Launch MessageTask
		 */
		launchLongPoll.start();

		user_id = VK_Data.user_id;
		pass = VK_Data.password;
		token = VK_Data.token;

		db = new DB(this);
		db.open();
		mess = new Messanger();

	}


	public void showBusyDialog(String message) {
		busyDialog = new Dialog(this, R.style.lightbox_dialog);
		busyDialog.setContentView(R.layout.lightbox_dialog);
		((TextView)busyDialog.findViewById(R.id.dialogText)).setText(message);
		busyDialog.show();
	}

	public void dismissBusyDialog() {
		if (busyDialog != null)
			busyDialog.dismiss();

		busyDialog = null;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub

	}
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub

	}
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub

	}

}
