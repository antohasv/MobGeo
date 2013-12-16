package com.igeo.igeolink;

import java.io.File;

import android.content.Context;
import android.widget.Toast;

public class FileCache {
	
	private static File cacheDir;
	
	public FileCache(Context context) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),"IGeo/Img");
        else
            cacheDir = context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
        Toast.makeText(context, cacheDir.getAbsolutePath(), 500).show();
	}
	
	 public static File getFile(String url){
	        String filename = String.valueOf(url);
	        File f = new File(cacheDir, filename);
	        return f;
	    }
	 
}
