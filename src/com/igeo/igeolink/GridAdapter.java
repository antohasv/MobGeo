package com.igeo.igeolink;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


public class GridAdapter extends BaseAdapter {
	private ArrayList<LoadGallery>  loadGallery = null;
	private Context context;
	
	public GridAdapter(Context context, ArrayList<LoadGallery> gallery) {
		loadGallery = gallery;
		this.context = context;
	}
	public int getCount() {
		return loadGallery.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup viewGroup) {
		ImageView img;
		if (view == null) {
			 img = new ImageView(context);
			 img.setLayoutParams(new GridView.LayoutParams(85, 85));
			 img.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			img = (ImageView)view;
		}
		final LoadGallery gallery = loadGallery.get(position);
		img.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				Intent intent = new Intent(context, ShowPhoto.class);
				FrdAlbums.setLargePhoto(gallery.getSrc(), gallery.getBigSrc());
				intent.putExtra("photo", gallery.getSrc());
				context.startActivity(intent);
			}
		});
		FrdAlbums.DisplayImage(loadGallery.get(position).getBigSrc(), img);
		return img;
	}

}
