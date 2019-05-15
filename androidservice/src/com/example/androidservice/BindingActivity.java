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

//绑定银行卡
public class BindingActivity extends Activity {
	private DBManager dbManager;
	private String tel = "";// 手机号
	private String cardId;//银行卡号
	private String password = "";// 支付密码
	private String times;//第几次添加
	TextView telTextView;
	EditText cardIdEditText;
	EditText passwordEdit;
	Button bindingButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_binding);
		dbManager = new DBManager(this);
		
		Intent intent=getIntent();
		tel=intent.getStringExtra("tel");
		times=intent.getStringExtra("times");
		
		telTextView=(TextView) findViewById(R.id.bindingTel);
		telTextView.setText(tel);
		cardIdEditText = (EditText) findViewById(R.id.cardID);
		passwordEdit = (EditText) findViewById(R.id.cardPassword);
		// 绑定
		bindingButton = (Button) findViewById(R.id.bindingButton);
		bindingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					binding();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	private void binding() throws Exception {
		// TODO Auto-generated method stub
		// 绑定信息
		cardId = cardIdEditText.getText().toString().trim();
		password = passwordEdit.getText().toString();

		if (tel.equals("")) {
			Toast.makeText(BindingActivity.this, "手机号不能为空", Toast.LENGTH_SHORT)
					.show();
		} else if (cardId.equals("")) {
			Toast.makeText(BindingActivity.this, "银行卡号不能为空", Toast.LENGTH_SHORT)
					.show();
		} else if (password.equals("")) {
			Toast.makeText(BindingActivity.this, "密码不能为空",
					Toast.LENGTH_SHORT).show();
		} else {
			String encryptPW = Encrypt.getMD5(password);
			// 使用Map封装请求参数
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("tel", tel);
			map.put("cardId", cardId);
			map.put("password", encryptPW);
			// 定义发送请求的URL
			String url = HttpUtil.Binding_URL ;
			String result = HttpUtil.postRequest(url, map, dbManager);
			Log.d("服务器返回值", result);
			JSONObject json = new JSONObject(result);
			int state = json.getInt("state");// 0失败，1成功，2电话和银行卡信息不一致

			if (state == 1) {
				// 绑定成功，跳转
				Toast.makeText(BindingActivity.this, "绑定成功",
						Toast.LENGTH_SHORT).show();
				NaviActivity.isBinding++;
				if(times.equals("one")){
					//第一次添加
					Intent cardIntent = new Intent(BindingActivity.this,
							CardActivity.class);
					cardIntent.putExtra("tel", tel);
					cardIntent.putExtra("situation", "setting");//从设置页面入
					startActivity(cardIntent);
					finish();
				}else{
					//多次添加
					Intent cardIntent = new Intent(BindingActivity.this,
							CardActivity.class);
					cardIntent.putExtra("tel", tel);
					cardIntent.putExtra("situation", "setting");//从设置页面入
					CardActivity.cardActivity.finish();
					startActivity(cardIntent);
					finish();
				}
				
			} else if (state == 2) {
				Toast.makeText(BindingActivity.this, "银行卡信息错误",
						Toast.LENGTH_SHORT).show();
			}else if (state == 3) {
				Toast.makeText(BindingActivity.this, "已绑定该银行卡，无需再次绑定",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(BindingActivity.this, "密码错误，绑定失败",
						Toast.LENGTH_SHORT).show();
			}

		}
	}
}
