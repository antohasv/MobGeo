package com.igeo.igeolink;

import java.io.File;

import android.content.Context;
import android.widget.Toast;

public class LocationsCache {
	
	public static File locCache = null;
	
	public LocationsCache(Context context) {	
		
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			locCache = new File(android.os.Environment.getExternalStorageDirectory(),"IGeo/Locations");
        else
        	locCache = context.getCacheDir();
        
		if(!locCache.exists())
        	locCache.mkdirs();
        
        Toast.makeText(context, locCache.getAbsolutePath(), 500).show();
	}

	public static File getFile(int uid){
	        String filename = String.valueOf(uid);
	        File f = new File(locCache, filename);
	        return f;
	    }
}
