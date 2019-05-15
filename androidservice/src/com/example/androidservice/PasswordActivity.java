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
//选择密码页面
public class PasswordActivity extends Activity {
	private DBManager dbManager;
	static Activity passwordActivity;
	String type="";
	String tel;
	String email;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_password);
		passwordActivity=this;
		dbManager = new DBManager(this);
		Intent intent=getIntent();
		tel = intent.getStringExtra("tel");
		email=intent.getStringExtra("email");

		//登录密码
		View myInfo = findViewById(R.id.loginPwd);
		myInfo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				type="login";
				showAlertDialog(v);
			}
		});

		//支付密码
		View myCard = findViewById(R.id.payPwd);
		myCard.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				type="pay";
				Intent intent=new Intent (PasswordActivity.this,CheckPasswordActivity.class);
				intent.putExtra("type", type);
				intent.putExtra("tel", tel);
				intent.putExtra("email", email);
				startActivity(intent);
			}
		});

	}
	
	public void showAlertDialog(View view) {

		final CustomDialog.Builder builder = new CustomDialog.Builder(this);
		if(type.equals("login")){
			builder.setMessage("请输入原登录密码");
			builder.setTitle("请输入原登录密码");
		}
		builder.setInputType(1);
		
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// 设置你的操作事项
				String oldPassword = builder.getData();
				if (oldPassword.equals("")) {
					Toast.makeText(PasswordActivity.this, "密码不能为空",
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
						int state = json.getInt("state");// 1是成功
						if (state == 1) {// 成功，跳转下一页面
							Toast.makeText(PasswordActivity.this, "原密码校验成功",
									Toast.LENGTH_SHORT).show();
							Intent loginPwdIntent=new Intent(PasswordActivity.this,UpdatePasswordActivity.class);
							loginPwdIntent.putExtra("type", type);
							startActivity(loginPwdIntent);
						} else if(state==0) {
							// 失败，提示密码输入错误
							Toast.makeText(PasswordActivity.this, "连续输错三次，3小时内拒绝修改",
									Toast.LENGTH_SHORT).show();
						}else if(state==2) {
							// 失败，提示密码输入错误
							Toast.makeText(PasswordActivity.this, "密码错误，重新输入",
									Toast.LENGTH_SHORT).show();
						}else if(state==3) {
							// 失败，提示密码输入错误
							Toast.makeText(PasswordActivity.this, "账号不存在",
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Toast.makeText(PasswordActivity.this, "原密码校验失败",
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();

	}

}
