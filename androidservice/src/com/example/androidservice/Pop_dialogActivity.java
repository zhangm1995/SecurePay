package com.example.androidservice;

import java.util.HashMap;

import org.json.JSONObject;

import com.example.sqlite.DBManager;
import com.example.util.Encrypt;

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
//弹出对话框
public class Pop_dialogActivity extends Activity {
//	private DBManager dbManager;
//	EditText passwordEditText;//密码框
//	Button checkButton;//确定按钮
//	String oldPassword;//原密码
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.pop_dialog);
//		dbManager = new DBManager(this);
//		passwordEditText=(EditText) findViewById(R.id.oldPassword);
//		checkButton=(Button) findViewById(R.id.btn_save_pop);
//		checkButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				oldPassword=passwordEditText.getText().toString().trim();
//				if(oldPassword.equals("")){
//					Toast.makeText(Pop_dialogActivity.this, "密码不能为空",Toast.LENGTH_SHORT).show();
//				}else{
//					String enPassword=Encrypt.getMD5(oldPassword);//加密后的密码
//					String url=HttpUtil.CheckPassword_URL+"?password="+enPassword;
//					HashMap<String, String> map = new HashMap<String, String>();  
//				    map.put("password", enPassword); 
//				    try {
//						String result=HttpUtil.postRequest(url, map, dbManager);
//						JSONObject json=new JSONObject(result);
//						int state=json.getInt("state");//1是成功
//						if(state==1){//成功，跳转下一页面
//							//失败，提示密码输入错误
//							Toast.makeText(Pop_dialogActivity.this, "原密码校验成功",Toast.LENGTH_SHORT).show();
//							Intent updateIntent= new Intent(Pop_dialogActivity.this,UpdatePasswordActivity.class);
//							Pop_dialogActivity.this.startActivity(updateIntent);
//							finish();
//						}else{
//							//失败，提示密码输入错误
//							Toast.makeText(Pop_dialogActivity.this, "原密码校验失败",Toast.LENGTH_SHORT).show();
//						}
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						Toast.makeText(Pop_dialogActivity.this, "原密码校验失败",Toast.LENGTH_SHORT).show();
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//		
//	}
}
