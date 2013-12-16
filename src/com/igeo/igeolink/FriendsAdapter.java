package com.igeo.igeolink;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<VK_Friends> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public FriendsAdapter(Activity a, ArrayList<VK_Friends> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView text=(TextView)vi.findViewById(R.id.list_title);
        ImageView image=(ImageView)vi.findViewById(R.id.list_image);
        TextView duration = (TextView)vi.findViewById(R.id.duration);
        TextView status = (TextView)vi.findViewById(R.id.list_status);
        TextView url = (TextView)vi.findViewById(R.id.frd_url);
        
        text.setText(data.get(position).getLastName() + " " + data.get(position).getFirstName());
        imageLoader.DisplayImage(data.get(position).getImageUrl(), image, true);
        status.setText(data.get(position).getImageUrl());
        url.setText(data.get(position).getImageUrl());
        
        if(data.get(position).getOnline() == 0)
     	   duration.setVisibility(View.GONE);
        else 
     	   duration.setVisibility(View.VISIBLE);
        
        return vi;
    }
}
