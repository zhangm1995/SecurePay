package com.example.sqlite;

import com.example.model.AccessToken;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;
	public DBManager(Context context){
		helper=new DBHelper(context);
		db=helper.getWritableDatabase();
	}

	public void add(AccessToken token) {
		db.beginTransaction();// 开始事务
		try {
			// 先把之前的token删除，再添加一条,数据库中始终保持一条数据
			db.execSQL("delete from AccessToken where id=1;");
			db.execSQL("insert into AccessToken (id,userId,token) values(1,"
					+ token.getUserId() + ",'" + token.getToken() + "');");
			db.setTransactionSuccessful();// 事务成功
		} finally {
			db.endTransaction();// 结束事务
		}
	}
	
	public void delete() {
		db.beginTransaction();// 开始事务
		try {
			// 把之前的token删除
			db.execSQL("delete from AccessToken where id=1;");
			db.setTransactionSuccessful();// 事务成功
		} finally {
			db.endTransaction();// 结束事务
		}
	}

	public String select() {
		db.beginTransaction();// 开始事务
		Cursor cursor;
		String token = "";
		try {
			cursor = db.rawQuery("select token from AccessToken where id=1",
					null);
			while (cursor.moveToNext()) {
				token = cursor.getString(0);// 获取第二列的值
			}
			db.setTransactionSuccessful();// 事务成功
		} finally {
			db.endTransaction();// 结束事务
		}
		cursor.close();
		return token;
	}

	// public void addtest(){
	// db.beginTransaction();//开始事务
	// try{
	// db.execSQL("insert into info (_id,name) values(1,'zm');");
	// db.setTransactionSuccessful();// 事务成功
	// }finally{
	// db.endTransaction();//结束事务
	// }
	// }

	public void closeDB() {
		db.close();
	}

}
