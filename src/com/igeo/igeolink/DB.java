package com.igeo.igeolink;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.IsolatedContext;

public class DB {
  
	  private static final String DB_NAME = "mydbtoa";
	  private static final int DB_VERSION = 1;
	  private static final String DB_TABLE = "mytabtoa";
	  
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_USER_ID = "user_id";
	  public static final String COLUMN_FRD_ID = "frd_id";
	  public static final String COLUMN_TXT = "txt";
	  public static final String COLUMN_TIME = "time";
	  public static final String COLUMN_OUT = "out";//Which message 1 - my 0 - friend
	  public static final String COLUMN_ISREAD = "is_read";//1- true 0 - false 
	  
	  private static final String DB_CREATE = 
	    "create table " + DB_TABLE + "(" +
	      COLUMN_ID + " integer primary key autoincrement, " +
	      COLUMN_USER_ID + " integer, " +
	      COLUMN_FRD_ID + " integer, " +
	      COLUMN_TXT + " text, " +
	      COLUMN_TIME + " timestamp, " +
	      COLUMN_OUT + " integer, " +
	      COLUMN_ISREAD + " integer " +
	    ");";
	  
	  private final Context mCtx;
	  
	  
	  private DBHelper mDBHelper;
	  private SQLiteDatabase mDB;
	  
	  public DB(Context ctx) {
	    mCtx = ctx;
	  }
	  
	  public void open() {
	    mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
	    mDB = mDBHelper.getWritableDatabase();
	  }
	  
	  public void close() {
	    if (mDBHelper!=null) mDBHelper.close();
	  }
	  
	  public Cursor getAllData() {
	    return mDB.query(DB_TABLE, null, null, null, null, null, null);
	  }
	  
	  public Cursor getTheLatestTen(int frd_id) {
		  return mDB.rawQuery("select * from " + DB_TABLE + " order by " + COLUMN_ID + " desc limit 0,10", null);
	  }
	  
	  public Cursor getFrdMessages(int frd_id) {
		  return mDB.rawQuery("select * from " + DB_TABLE + " where " + COLUMN_FRD_ID +" = " + frd_id, null);
	  }
	   
	  
	  public int getCountFrdMess(int frd_id) {
		  Cursor c = mDB.rawQuery("select count(" + COLUMN_ID + ") from " + DB_TABLE + " where " + COLUMN_FRD_ID + " = " + frd_id, null);
		  c.moveToFirst();
		  return c.getInt(0);
	  }
	  
	  public void addRec(int user_id, int frd_id, String txt, int time, int wm, boolean is_read) {
	    ContentValues cv = new ContentValues();
	    cv.put(COLUMN_USER_ID, user_id);
	    cv.put(COLUMN_FRD_ID, frd_id);
	    cv.put(COLUMN_TXT, txt);
	    cv.put(COLUMN_TIME, time);
	    cv.put(COLUMN_OUT, wm);
	    if (is_read)
	    	cv.put(COLUMN_ISREAD, 1);
	    else 
	    	cv.put(COLUMN_ISREAD, 0);
	    mDB.insert(DB_TABLE, null, cv);
	  }
	  
	  public void delRec(long id) {
	    mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
	  }
	  
	  private class DBHelper extends SQLiteOpenHelper {
	
	    public DBHelper(Context context, String name, CursorFactory factory,
	        int version) {
	      super(context, name, factory, version);
	    }
	
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	      db.execSQL(DB_CREATE);
	    }
	
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    }
	  }
}