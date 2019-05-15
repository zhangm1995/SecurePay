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
//校验支付密码
public class CheckPasswordActivity extends Activity {
	private DBManager dbManager;
	static Activity checkPasswordActivity;
	String type="";
	String tel;
	String email;
	String oldPassword;
	EditText oldEditText;
	Button checkBtn;
	Button forgetBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_check_paypwd);
		dbManager = new DBManager(this);
		checkPasswordActivity=this;
		Intent intent=getIntent();
		type=intent.getStringExtra("type");
		tel = intent.getStringExtra("tel");
		email=intent.getStringExtra("email");
		
		oldEditText=(EditText)findViewById(R.id.oldPayword);
		//忘记密码
		forgetBtn=(Button)findViewById(R.id.btn_forget);
		forgetBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(CheckPasswordActivity.this,CheckEmailActivity.class);
				intent.putExtra("page", "edit");
				intent.putExtra("type", "pay");
				intent.putExtra("tel", tel);
				intent.putExtra("email", email);
				startActivity(intent);
				finish();
			}
		});	
		//忘记密码
		checkBtn=(Button)findViewById(R.id.btn_checkPwd);
		checkBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				oldPassword=oldEditText.getText().toString().trim();
				if (oldPassword.equals("")) {
					Toast.makeText(CheckPasswordActivity.this, "密码不能为空",
							Toast.LENGTH_SHORT).show();
				} else {
					String enPassword = Encrypt.getMD5(oldPassword);// 加密后的密码
					String url = HttpUtil.CheckPassword_URL;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("password", enPassword);
					map.put("type", type);
					try {
						String result = HttpUtil.postRequest(url, map,
								dbManager);
						JSONObject json = new JSONObject(result);
						int state = json.getInt("state");
						//0 连续输错三次，3小时内拒绝登录
						//1密码校验成功
						//2密码校验失败，重新输入
						//3账号不存在
						if (state == 1) {// 成功，跳转下一页面
							Toast.makeText(CheckPasswordActivity.this, "原密码校验成功",
									Toast.LENGTH_SHORT).show();
							Intent payPwdIntent=new Intent(CheckPasswordActivity.this,UpdatePasswordActivity.class);
							payPwdIntent.putExtra("type", type);
							payPwdIntent.putExtra("page", "edit");
							startActivity(payPwdIntent);
							finish();
						} else if(state==0) {
							// 失败，提示密码输入错误
							Toast.makeText(CheckPasswordActivity.this, "连续输错三次，3小时内拒绝修改",
									Toast.LENGTH_SHORT).show();
						}else if(state==2) {
							// 失败，提示密码输入错误
							Toast.makeText(CheckPasswordActivity.this, "密码错误，重新输入",
									Toast.LENGTH_SHORT).show();
						}else if(state==3) {
							// 失败，提示密码输入错误
							Toast.makeText(CheckPasswordActivity.this, "账号不存在",
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Toast.makeText(CheckPasswordActivity.this, "原密码校验失败",
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
			}
		});

	}
	
}
