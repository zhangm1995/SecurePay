package com.example.androidservice;

import java.util.HashMap;

import org.json.JSONObject;

import com.example.sqlite.DBManager;
import com.example.util.Encrypt;
import com.example.util.HttpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UpdatePasswordActivity extends Activity {
	private DBManager dbManager;
	EditText passwordEditText;//密码框
	EditText passwordEditText2;//密码框2
	Button updateButton;//确定按钮
	String newPassword;//新密码
	String newPassword2;//新密码2
	String type;
	String page;
	String tel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		dbManager = new DBManager(this);
		Intent intent=getIntent();
		page=intent.getStringExtra("page");
		if(page.equals("login")){
			tel=intent.getStringExtra("tel");
		}
		type=intent.getStringExtra("type");
		if(type.equals("login")){
			setContentView(R.layout.activity_update_loginpassword);
		}else{
			setContentView(R.layout.activity_update_password);
		}
		
		passwordEditText=(EditText) findViewById(R.id.newPassword);
		passwordEditText2=(EditText) findViewById(R.id.newPassword2);
		updateButton=(Button) findViewById(R.id.btn_updatePwd);
		updateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				newPassword=passwordEditText.getText().toString().trim();
				newPassword2=passwordEditText2.getText().toString().trim();
				if(newPassword.equals("")){
					Toast.makeText(UpdatePasswordActivity.this, "请设置新密码",Toast.LENGTH_SHORT).show();
				}else if(newPassword2.equals("")){
					Toast.makeText(UpdatePasswordActivity.this, "请确认新密码",Toast.LENGTH_SHORT).show();
				}else if(!newPassword.equals(newPassword2)){
					Toast.makeText(UpdatePasswordActivity.this, "两次输入密码不一致,请重新输入",Toast.LENGTH_SHORT).show();
					passwordEditText.setText("");
					passwordEditText2.setText("");
				}else{
					String enPassword=Encrypt.getMD5(newPassword);//加密后的新密码
					String url=HttpUtil.UpdatePassword_URL+"?password="+enPassword;
					HashMap<String, String> map = new HashMap<String, String>();  
				    map.put("password", enPassword); 
				    map.put("type", type); 
				    map.put("page", page);
				    if(page.equals("login")){
				    	map.put("tel", tel);
				    }
				    try {
						String result=HttpUtil.postRequest(url, map, dbManager);
						JSONObject json=new JSONObject(result);
						int state=json.getInt("state");//1是成功
						if(state==1){//成功，跳转下一页面
							if(page.equals("edit")){
								//从设置页面入
								Toast.makeText(UpdatePasswordActivity.this, "修改成功",Toast.LENGTH_SHORT).show();
								PasswordActivity.passwordActivity.finish();
								Intent intent=new Intent(UpdatePasswordActivity.this,PasswordActivity.class);
								startActivity(intent);
								finish();
							}else if(page.equals("login")){
								//从登陆页面入
								Toast.makeText(UpdatePasswordActivity.this, "修改成功，重新登录",Toast.LENGTH_SHORT).show();
								Intent intent=new Intent(UpdatePasswordActivity.this,MainActivity.class);
								startActivity(intent);
								finish();
							}
							
						}else{
							//失败，提示密码输入错误
							Toast.makeText(UpdatePasswordActivity.this, "修改失败",Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Toast.makeText(UpdatePasswordActivity.this, "修改失败",Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
			}
		});
		
	}
}
