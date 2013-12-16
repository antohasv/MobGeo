package com.igeo.igeolink;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.net.Socket;

import authorization.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import authorization.VK_Authorization;


public class Auth extends Activity implements OnClickListener,Runnable{
	
	private static final int CONNECTED = 100;
	private static final int UNCORRECT_PASSWORD = 101;
	private static final int UNABLE_NETWORK = 102;
	private static final int REPEAT_AGAIN = 103;
	private static final int START_BAR = 104;
	
	private final String ACCESS_TOKEN="access_token";
	private final String USER_ID="user_id";
	private final String IGEO_VK="igeolink_vk";
	
	private EditText ed_email;
	private EditText ed_pass;
	
	private String token = null;
	private String pass = null;
	private int user_id = 0;
	
	private String text = "";
	
	private ProgressDialog mProgressDialog;
	
	private SharedPreferences sPref;
	private Context context;
	
	private Socket sock = null;
	private String response = null;
	
	private Dialog busyDialog = null;
	
	final int SAVE_DATA = 1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);
		context = this;
		Button but = (Button) findViewById(R.id.vkauth_but1);
		but.setOnClickListener(this);
		
		Button rbut = (Button) findViewById(R.id.vkauth_reg);
		rbut.setOnClickListener(this);
		
		ed_email = (EditText) findViewById(R.id.vkauth_ed1);
		ed_pass = (EditText) findViewById(R.id.vkauth_ed2);
		
		
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	
	 Handler handler = new Handler() {
	    	public void handleMessage(android.os.Message msg) {
	    		switch(msg.what) {
	    		case START_BAR:
	    			
	    			dismissBusyDialog();
	    			showBusyDialog("Loading...");
	    			break;
	    		case CONNECTED:
	    			
	    			/*
	    			sPref = getSharedPreferences(IGEO_VK, MODE_PRIVATE);
	    		    Editor ed = sPref.edit();
	    		    Toast.makeText(getApplicationContext(), "User_ID:" + user_id + "ACCESS_TOKEN:" + token, 500).show();
	    		    ed.putString(USER_ID, user_id);
	    		    ed.putString(ACCESS_TOKEN, token);
	    		    ed.commit();
	    			*/
	    			//Send user_id and password on the Server
	    		    
	    			//Toast.makeText(getApplicationContext(), "User_ID:" + user_id + "ACCESS_TOKEN:" + token, 500).show();
	    			
	    		    VK_Data.token = token;
	    		    VK_Data.user_id = user_id;
	    		    VK_Data.password = pass;
	    		    busyDialog.dismiss();
	    		    //Toast.makeText(getApplicationContext(), "User_ID:" + user_id + "ACCESS_TOKEN:" + token, 500).show();
	    		    //Toast.makeText(getApplicationContext(), "Server" + response, 500).show();
	    			Intent intent = new Intent(context, CMap.class);
	    			finish();
	    			context.startActivity(intent);
	    			break;
	    		case UNCORRECT_PASSWORD:
	    			busyDialog.dismiss();
	    			user_id = 0;
	    			token = null;
	    			Toast.makeText(getApplicationContext(), "Uncorrect Login or Password", 500).show();    			
	    			break;
	    		case UNABLE_NETWORK:
	    			busyDialog.dismiss();
	    			Toast.makeText(getApplicationContext(), R.string.serverIP + "", 500).show();
	    			//Toast.makeText(getApplicationContext(), "Unable Network", 500).show(); 
	    		case REPEAT_AGAIN:
	    			busyDialog.dismiss();
	    			Toast.makeText(getApplicationContext(), "Repeat_Again", 500).show();
	    		}
	    	}
	    };

	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.vkauth_but1:
			if(!ed_email.getText().toString().equals("") || !ed_pass.getText().equals("")) {
				if(isOnline()) {
					handler.sendEmptyMessage(START_BAR);
					Thread th1 = new Thread(this);
					th1.start();
				} else {
					Toast.makeText(getApplicationContext(), "Network unavailable", 500).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "Некоректно введены данные", 500).show();
			}
			break;
		case R.id.vkauth_reg:
			Intent intent = new Intent(this, VK_Regestration.class);
			this.startActivityForResult(intent, 10001);
			break;
		}
		
	}

	public void run() {
		VK_Authorization vk ; 
		vk = new  VK_Authorization(ed_email.getText().toString(), ed_pass.getText().toString());
		if(vk.getUserID() == null && vk.getToken() == null) {
			switch(vk.getError()) {
			case 1:
				handler.sendEmptyMessage(UNCORRECT_PASSWORD);
				break;
			case 2:
				handler.sendEmptyMessage(UNABLE_NETWORK);
				break;
			}
		} else if(vk.getError() == 0) {
			
			user_id = Integer.parseInt(vk.getUserID());
			token = vk.getToken();
			pass = ed_pass.getText().toString();
			try {
				String data = "func=auth&name=" + user_id + "&pass=" + pass;
				sock = new Socket("192.168.0.100", 1050);
				DataOutputStream out = new DataOutputStream(sock.getOutputStream());
				DataInputStream in = new DataInputStream(sock.getInputStream());
				out.writeUTF(data);
				out.flush();
				response = in.readUTF();
				handler.sendEmptyMessage(CONNECTED);
			} catch(Exception e) {
				handler.sendEmptyMessage(UNABLE_NETWORK);
			}
			
		} else {
			handler.sendEmptyMessage(REPEAT_AGAIN);
		}	
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    	if(resultCode == 1001) {
	    		ed_email.setText(data.getStringExtra("phone"));
	    		ed_pass.setText(data.getStringExtra("pass"));
	    		Toast.makeText(getApplicationContext(), "UID:" + data.getIntExtra("uid", 0) + "", 500).show();
	    	}
	}
	   
}



