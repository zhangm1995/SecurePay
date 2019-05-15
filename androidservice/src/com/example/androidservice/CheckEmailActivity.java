package com.example.androidservice;

import java.util.HashMap;

import org.json.JSONObject;

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
//邮箱验证
public class CheckEmailActivity extends Activity {
	private DBManager dbManager;
	private String page="";//从哪个页面进入
	private String type="";//登陆密码或支付密码
	private String tel = "";// 手机号
	private String email="";//邮箱
	private String code="";//邮箱验证码
	EditText emailEditText;
	EditText telText;
	EditText codeText;
	TextView infoText;
	Button checkEmailButton;
	Button sendCodeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_emailcheck);
		Intent intent = getIntent();// 获取启动该Activity的intent对象
		page=intent.getStringExtra("page");
		type=intent.getStringExtra("type");
		
		emailEditText = (EditText) findViewById(R.id.checkEmail);
		telText = (EditText) findViewById(R.id.checkTel);
		codeText = (EditText) findViewById(R.id.checkCode);
		infoText=(TextView)findViewById(R.id.info);
		if(page.equals("edit")){
			//从个人设置页面入
			tel = intent.getStringExtra("tel");
			email= intent.getStringExtra("email");
			telText.setText(tel);
			emailEditText.setText(email);
		}
		
		dbManager = new DBManager(this);
		
		sendCodeButton=(Button)findViewById(R.id.btn_sendCode);
		sendCodeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					sendCode();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		// 检验验证码
		checkEmailButton=(Button)findViewById(R.id.btn_checkCode);
		checkEmailButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					checkCode();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	private void sendCode()throws Exception {
		String myEmail = emailEditText.getText().toString().trim();
		String myTel = telText.getText().toString();
		if(myEmail.equals("")){
			Toast.makeText(CheckEmailActivity.this, "邮箱地址不能为空",
					Toast.LENGTH_SHORT).show();
		}else if(myTel.equals("")){
			Toast.makeText(CheckEmailActivity.this, "手机号不能为空",
					Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(CheckEmailActivity.this, "请不要重复点击，正在发送验证码...",
					Toast.LENGTH_SHORT).show();
			infoText.setText("请不要重复点击，正在发送验证码...");
			// 使用Map封装请求参数
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("email", myEmail);
			map.put("tel", myTel);
			map.put("page", page);
			map.put("type", type);
			// 定义发送请求的URL
			String url = HttpUtil.SendCode_URL ;
			String result = HttpUtil.postRequest(url, map, dbManager);
			Log.d("服务器返回值", result);
			JSONObject json = new JSONObject(result);
			int state = json.getInt("state");// 1是成功
			if(state==1){
				//发送验证码成功
				Toast.makeText(CheckEmailActivity.this, "验证码发送成功，注意查收邮件",
						Toast.LENGTH_SHORT).show();
				infoText.setText("验证码发送成功，注意查收邮件");
			} else if(state==2){
				// 修改失败
				Toast.makeText(CheckEmailActivity.this, "邮箱地址错误",
						Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(CheckEmailActivity.this, "验证码发送失败",
						Toast.LENGTH_SHORT).show();
			}
		}
		
	}

	private void checkCode() throws Exception {
		// TODO Auto-generated method stub
		// 检验验证码
		code = codeText.getText().toString().trim();
		String myEmail = emailEditText.getText().toString().trim();
		String myTel = telText.getText().toString();
		if(code.equals("")){
			Toast.makeText(CheckEmailActivity.this, "验证码不能为空",
					Toast.LENGTH_SHORT).show();
		}else if(myEmail.equals("")){
			Toast.makeText(CheckEmailActivity.this, "邮箱地址不能为空",
					Toast.LENGTH_SHORT).show();
		}else if(myTel.equals("")){
			Toast.makeText(CheckEmailActivity.this, "手机号不能为空",
					Toast.LENGTH_SHORT).show();
		}else{
			// 使用Map封装请求参数
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("code",  code);
			map.put("email", myEmail);
			map.put("tel", myTel);
			map.put("page", page);
			// 定义发送请求的URL
			String url = HttpUtil.CheckCode_URL ;
			String result = HttpUtil.postRequest(url, map, dbManager);
			Log.d("服务器返回值", result);
			JSONObject json = new JSONObject(result);
			int state = json.getInt("state");// 1是成功
			if(state==1){
				//验证成功，跳转修改页面
				Toast.makeText(CheckEmailActivity.this, "验证成功",
						Toast.LENGTH_SHORT).show();
				Intent passwordIntent = new Intent(
						CheckEmailActivity.this, UpdatePasswordActivity.class);
				passwordIntent.putExtra("type",type);
				passwordIntent.putExtra("page",page);
				if(page.equals("login")){
					passwordIntent.putExtra("tel",myTel);
				}
				startActivity(passwordIntent);
				finish();
			} else {
				// 修改失败
				Toast.makeText(CheckEmailActivity.this, "验证失败",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
