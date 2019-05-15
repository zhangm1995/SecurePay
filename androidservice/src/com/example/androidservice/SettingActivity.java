package com.example.androidservice;

import java.util.HashMap;

import org.json.JSONObject;

import com.example.sqlite.DBManager;
import com.example.util.Encrypt;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity {
	private DBManager dbManager;
	String userName;
	String tel;
	String email;
	double balance;
	int isBinding;
	int userId;
	static Activity SettingActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		Intent intent = getIntent();// ��ȡ������Activity��intent����
		dbManager = new DBManager(this);
		SettingActivity = this;
		userId = intent.getIntExtra("userId", 0);
		userName = intent.getStringExtra("userName");
		tel = intent.getStringExtra("tel");
		email=intent.getStringExtra("email");
		balance=intent.getDoubleExtra("balance", 0.0);
		isBinding = intent.getIntExtra("isBinding", 2);// Ĭ��ֵΪ2��Ϊ1��ʾ�Ѱ�

		// �ҵ�����
		View myInfo = findViewById(R.id.myInfo);
		myInfo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent infoIntent = new Intent(SettingActivity.this,
						PersonInfoActivity.class);
				infoIntent.putExtra("userName", userName);
				infoIntent.putExtra("tel", tel);
				infoIntent.putExtra("email", email);
				startActivity(infoIntent);
			}
		});

		// �ҵĿ���
		View myCard = findViewById(R.id.myCard);
		myCard.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// ��ʾ�Ѱ����п�����ת�����п�����ҳ��
				Intent cardIntent = new Intent(SettingActivity.this,
						CardActivity.class);
				cardIntent.putExtra("tel", tel);
				cardIntent.putExtra("situation", "setting");//������ҳ����
				cardIntent.putExtra("balance", balance);
				startActivity(cardIntent);
			}
		});

		// ����
		View myPassword = findViewById(R.id.myPassword);
		myPassword.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent pwdIntent = new Intent(SettingActivity.this,
						PasswordActivity.class);
				pwdIntent.putExtra("tel", tel);
				pwdIntent.putExtra("email", email);
				startActivity(pwdIntent);
			}
		});
		// �˳���¼
		Button exitButton = (Button) findViewById(R.id.exitButton);
		exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// �˳���¼���ֻ����ݿ���tokenɾ��
				dbManager.delete();
				// ��ת����¼ҳ��
				Intent reLoginIntent = new Intent(SettingActivity.this,
						MainActivity.class);
				reLoginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(reLoginIntent);
				NaviActivity.MyNaviActivity.finish();
				finish();
			}
		});

	}

}
