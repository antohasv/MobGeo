package com.igeo.igeolink;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

public class ImageMap {
	public static Map<String, Bitmap> cache = Collections.synchronizedMap(new HashMap<String, Bitmap>());
	
}
