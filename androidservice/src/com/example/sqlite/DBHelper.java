package com.example.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper  extends SQLiteOpenHelper{
	public static final String DB_NAME = "test.db";
	public static final String DB_TABLE_NAME = "AccessToken";
	private static final int DB_VERSION=1;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//´´½¨±í
		db.execSQL("CREATE TABLE IF NOT EXISTS AccessToken (id identity(1,1) primary key, userId int,token VARCHAR)");
		Log.i("WIRELESSQA", "create table");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("ALTER TABLE info ADD COLUMN other STRING"); 
		 Log.i("WIRELESSQA", "update sqlite "+oldVersion+"---->"+newVersion);
	}

}
