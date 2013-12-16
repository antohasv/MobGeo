package com.igeo.igeolink;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import requests.PostGetRequests;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class VK_Regestration extends Activity implements OnClickListener,AnimationListener {
	
    private static final int client_id = 3187335;
	private static final String client_secret = "cOEi2gfah8dFxnGgsenP";
	
	private static final int CONNECTED = 400;
	private static final int EXCEPTION = 401;
	private static final int SUCCESS = 402;
    
	private EditText phone;
	private EditText lsname;
	private EditText pass;
	private EditText _code;
	
	private String sid;
	
	public String url1;
	public String url2;
	
	public String text = "";
	public Exception exp = null;
	
	private ViewFlipper mFlipper;
	public Animation in;
	public Animation out;
	
	private Context context;
	private Dialog busyDialog = null;
	
	private Button but1;
	private Button but2;
	private Button but3;
	
	
	public static  int k = 0;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.vk_registration);
		
		context = this;
		mFlipper = (ViewFlipper) findViewById(R.id.reg_flipper);
		
		phone = (EditText) findViewById(R.id.reg_phone);
		lsname = (EditText) findViewById(R.id.reg_lsname);
		pass = (EditText) findViewById(R.id.reg_pass);
		_code = (EditText) findViewById(R.id.reg_sid);

		but1 = (Button) findViewById(R.id.vkreg_but1);
		but1.setOnClickListener(this);
		
		but2 = (Button) findViewById(R.id.vkreg_but2);
		but2.setOnClickListener(this);		
		
		but3 = (Button) findViewById(R.id.vkreg_back);
		but3.setOnClickListener(this);		
		
	}
	
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.vkreg_but1:
			phone.setEnabled(false);
			lsname.setEnabled(false);
			pass.setEnabled(false);
			but1.setEnabled(false);
			if(!phone.equals("") || !lsname.equals("") || !pass.equals("")) {
				String []parts = lsname.getText().toString().split(",");
				if(parts.length == 2) {
					url1 = "https://api.vk.com/method/auth.signup?phone=" + phone.getText().toString() + "&first_name=" + "Антон" + 
							"&last_name=" + "Светлов" + "&client_id=" + client_id + "&client_secret=" + client_secret + 
								"&sex=" + "2" + "&password=" + pass.getText().toString() + "&test_mode=1";	
					if(isOnline()) {
						th1.start();
					} else {
						Toast.makeText(getApplicationContext(), "Network unavailable, repeat again!", 500).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "Uncorrect field LastName, FirstName", 500).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "Please full all fields", 500).show();
			}
			break;
		case R.id.vkreg_but2:
			if(sid != null) {
				url2 = "https://api.vk.com/method/auth.confirm?phone=" + phone.getText().toString() + "&code=" + _code.getText().toString() + 
						"&client_id=" + client_id + "&client_secret=" + client_secret + "&test_mode=1" ;
				if(isOnline()) {
					but2.setEnabled(false);
					th2.start();
				} else {
					Toast.makeText(getApplicationContext(), "Network unavailable, repeat again!", 500).show();
				}
				Toast.makeText(getApplicationContext(), "Get code...", 500).show();
				
			} else {
				Toast.makeText(getApplicationContext(), "Please full fields", 500).show();
			}
				
			break;
		case R.id.vkreg_back:
				
				finish();
			break;
		}
	}
	
	public Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			case CONNECTED:
				Toast.makeText(getApplicationContext(), text, 500).show();
				try {
					JSONObject obj = new JSONObject(text);
					sid = obj.getJSONObject("response").getString("sid");
					if(sid != null) {
						in = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
			            out = AnimationUtils.loadAnimation(context, R.anim.push_left_out);
			            in.setAnimationListener((AnimationListener) context);
			            out.setAnimationListener((AnimationListener) context);
			            mFlipper.setInAnimation(in);
			            mFlipper.setOutAnimation(out);
			            mFlipper.showNext();
					} else {
						Toast.makeText(getApplicationContext(), "Repeat again!", 500).show();
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), 500).show();
				}
				break;
			case EXCEPTION:
				Toast.makeText(getApplicationContext(), "Exception:" + exp.getMessage(), 500).show();
				break;
			case SUCCESS:
				try {
					Toast.makeText(getApplicationContext(), text, 500).show();
					JSONObject obj = (new JSONObject(text)).getJSONObject("response");
					
					if(obj.getInt("success") == 1) {
						Toast.makeText(getApplicationContext(), "Success", 500).show();
						Intent intent = new Intent();
						intent.putExtra("uid", obj.getInt("uid"));
						intent.putExtra("phone", phone.getText().toString());
						intent.putExtra("pass", pass.getText().toString());
						setResult(1001, intent);
						finish();
					}
				} catch (JSONException e) {
					
				}
				
				
				break;
			}
		}
	};

	Thread th1 = new Thread(new Runnable() {
			
			public void run() {
				try {
					HttpResponse res = PostGetRequests.getReq(url1);
					BufferedReader reader = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
					String line = "";
					while((line = reader.readLine()) != null) {
						text += line;
					}
					handler.sendEmptyMessage(CONNECTED);
				} catch (Exception e) {
					exp = e;
					handler.sendEmptyMessage(EXCEPTION);
				}
			}
		});
	
	Thread th2 = new Thread(new Runnable() {
		
		public void run() {
			try {
				HttpResponse res = PostGetRequests.getReq(url2);
				BufferedReader reader = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
				String line = "";
				text = "";
				while((line = reader.readLine()) != null) {
					text += line;
				}
				
				JSONObject obj = new JSONObject(text);
				JSONObject obj1 = obj.getJSONObject("response");
				
				handler.sendEmptyMessage(SUCCESS);
			} catch (Exception e) {
				exp = e;
				handler.sendEmptyMessage(EXCEPTION);
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

	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	

}
