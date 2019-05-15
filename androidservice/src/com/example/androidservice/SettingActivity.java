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
		Intent intent = getIntent();// 获取启动该Activity的intent对象
		dbManager = new DBManager(this);
		SettingActivity = this;
		userId = intent.getIntExtra("userId", 0);
		userName = intent.getStringExtra("userName");
		tel = intent.getStringExtra("tel");
		email=intent.getStringExtra("email");
		balance=intent.getDoubleExtra("balance", 0.0);
		isBinding = intent.getIntExtra("isBinding", 2);// 默认值为2，为1表示已绑定

		// 我的资料
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

		// 我的卡包
		View myCard = findViewById(R.id.myCard);
		myCard.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 表示已绑定银行卡，跳转到银行卡详情页面
				Intent cardIntent = new Intent(SettingActivity.this,
						CardActivity.class);
				cardIntent.putExtra("tel", tel);
				cardIntent.putExtra("situation", "setting");//从设置页面入
				cardIntent.putExtra("balance", balance);
				startActivity(cardIntent);
			}
		});

		// 密码
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
		// 退出登录
		Button exitButton = (Button) findViewById(R.id.exitButton);
		exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 退出登录将手机数据库中token删除
				dbManager.delete();
				// 跳转到登录页面
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
