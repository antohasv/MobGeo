package com.igeo.igeolink;

import android.os.Bundle;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class IGeoLink extends Activity implements OnClickListener{

	private Context context;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igeo_link);
        context = this;
        Button but = (Button) findViewById(R.id.main_but1);
        but.setOnClickListener(this);
    }

	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.main_but1:
			Intent intent = new Intent(context, Auth.class);
			context.startActivity(intent);
			finish();
			break;
		}
		
	}

}
