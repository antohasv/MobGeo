package com.igeo.igeolink;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBMessage extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "test_message1";
	public static final String TABLE_NAME = "message";
	
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_USERID = "user_id";
	public static final String COLUMN_FRDID = "frd_id";
	public static final String COLUMN_TEXT = "text";
	
	private static String create_table = "create table " + TABLE_NAME + "(" +
		      COLUMN_ID + " integer primary key autoincrement not null, " +
		      COLUMN_USERID + " integer not null, " +
		      COLUMN_FRDID + " integer not null, " +
		      COLUMN_TEXT + " text" +
		    ");";
	
	public DBMessage(Context context) {
		super(context, DB_NAME, null, 1);
	}
	
	public void addData(int user_id, int frd_id, String text) {
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(create_table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	

}
