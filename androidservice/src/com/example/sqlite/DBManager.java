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
		db.beginTransaction();// ��ʼ����
		try {
			// �Ȱ�֮ǰ��tokenɾ���������һ��,���ݿ���ʼ�ձ���һ������
			db.execSQL("delete from AccessToken where id=1;");
			db.execSQL("insert into AccessToken (id,userId,token) values(1,"
					+ token.getUserId() + ",'" + token.getToken() + "');");
			db.setTransactionSuccessful();// ����ɹ�
		} finally {
			db.endTransaction();// ��������
		}
	}
	
	public void delete() {
		db.beginTransaction();// ��ʼ����
		try {
			// ��֮ǰ��tokenɾ��
			db.execSQL("delete from AccessToken where id=1;");
			db.setTransactionSuccessful();// ����ɹ�
		} finally {
			db.endTransaction();// ��������
		}
	}

	public String select() {
		db.beginTransaction();// ��ʼ����
		Cursor cursor;
		String token = "";
		try {
			cursor = db.rawQuery("select token from AccessToken where id=1",
					null);
			while (cursor.moveToNext()) {
				token = cursor.getString(0);// ��ȡ�ڶ��е�ֵ
			}
			db.setTransactionSuccessful();// ����ɹ�
		} finally {
			db.endTransaction();// ��������
		}
		cursor.close();
		return token;
	}

	// public void addtest(){
	// db.beginTransaction();//��ʼ����
	// try{
	// db.execSQL("insert into info (_id,name) values(1,'zm');");
	// db.setTransactionSuccessful();// ����ɹ�
	// }finally{
	// db.endTransaction();//��������
	// }
	// }

	public void closeDB() {
		db.close();
	}

}
