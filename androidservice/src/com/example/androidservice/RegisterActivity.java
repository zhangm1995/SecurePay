package com.example.androidservice;

import java.util.HashMap;

import org.json.JSONObject;

import com.example.model.AccessToken;
import com.example.sqlite.DBManager;
import com.example.util.Encrypt;
import com.example.util.HttpUtil;

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

//注册
public class RegisterActivity extends Activity {
	private DBManager dbManager;
	private String userName = "";// 用户名
	private String tel = "";// 手机号
	private String email="";//邮箱地址
	private String loginPassword = "";// 登录密码
	private String payPassword = "";// 支付密码
	EditText nameEditText;
	EditText telEditText;
	EditText emailEditText;
	EditText loginPasswordEdit;
	EditText payPasswordEdit;
	Button registerButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		dbManager = new DBManager(this);
		nameEditText = (EditText) findViewById(R.id.registerName);
		telEditText = (EditText) findViewById(R.id.registerTel);
		emailEditText= (EditText) findViewById(R.id.registerEmail);
		loginPasswordEdit = (EditText) findViewById(R.id.registerPassword);
		payPasswordEdit = (EditText) findViewById(R.id.payPassword);
		// 注册
		registerButton = (Button) findViewById(R.id.registerButton);
		registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					register();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	private void register() throws Exception {
		// TODO Auto-generated method stub
		// 注册信息
		userName = nameEditText.getText().toString().trim();
		email=emailEditText.getText().toString().trim();
		tel = telEditText.getText().toString();
		loginPassword = loginPasswordEdit.getText().toString();
		payPassword = payPasswordEdit.getText().toString();

		if (tel.equals("")) {
			Toast.makeText(RegisterActivity.this, "手机号不能为空", Toast.LENGTH_SHORT)
					.show();
		}else if (email.equals("")) {
			Toast.makeText(RegisterActivity.this, "邮箱地址不能为空", Toast.LENGTH_SHORT)
			.show();
		} else if (userName.equals("")) {
			Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT)
					.show();
		} else if (loginPassword.equals("")) {
			Toast.makeText(RegisterActivity.this, "登录密码不能为空",
					Toast.LENGTH_SHORT).show();
		} else if (payPassword.equals("")) {
			Toast.makeText(RegisterActivity.this, "支付密码不能为空",
					Toast.LENGTH_SHORT).show();
		} else if (loginPassword.equals(payPassword)) {
			Toast.makeText(RegisterActivity.this, "登录密码和支付密码不能相同",
					Toast.LENGTH_SHORT).show();
		} else {
			String encryptPW = Encrypt.getMD5(loginPassword);
			String encryptPayPW = Encrypt.getMD5(payPassword);
			// 使用Map封装请求参数
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", userName);
			map.put("tel", tel);
			map.put("email", email);
			map.put("loginPassword", encryptPW);
			map.put("payPassword", encryptPayPW);
			// 定义发送请求的URL
			String url = HttpUtil.Register_URL + "?name=" + userName;
			String result = HttpUtil.postRequest(url, map, dbManager);
			Log.d("服务器返回值", result);
			JSONObject json = new JSONObject(result);
			int state = json.getInt("state");// 0注册失败，1注册成功，2该账号已存在

			if (state == 1) {
				// 注册成功，跳转
				int id = json.getInt("id");
				String accessToken = json.getString("accessToken");

				AccessToken tokenObject = new AccessToken();
				tokenObject.setUserId(id);
				tokenObject.setToken(accessToken);
				dbManager.add(tokenObject);
				Log.d("将token保存至数据库", "");

				Toast.makeText(RegisterActivity.this, "注册成功",
						Toast.LENGTH_SHORT).show();
				Intent detailIntent = new Intent(RegisterActivity.this,
						NaviActivity.class);
				startActivity(detailIntent);
				MainActivity.LoginActivity.finish();
				finish();
			} else if (state == 2) {
				Toast.makeText(RegisterActivity.this, "该账号已存在",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RegisterActivity.this, "注册失败",
						Toast.LENGTH_SHORT).show();
			}

		}
	}
}
