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
//付款页面
public class PayActivity extends Activity {
	private DBManager dbManager;
	int payId;
	String payName;
	String payTel;
	double balance;
	TextView telTextView;
	TextView nameTextView;
	EditText moneyEditText;
	EditText passwordEditText;
	Button payButton;
	String money;
	String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pay);
		dbManager = new DBManager(this);

		Intent intent = getIntent();
		// 通过intent获取主线程传来的用户名和密码字符串
		payId = intent.getIntExtra("payId", 0);
		payName = intent.getStringExtra("payName");
		payTel = intent.getStringExtra("payTel");
		balance = intent.getDoubleExtra("balance", 0);

		telTextView = (TextView) findViewById(R.id.payTel);
		nameTextView = (TextView) findViewById(R.id.payName);
		telTextView.setText("收款人账号：" + payTel);
		nameTextView.setText("收款人：" + payName);

		moneyEditText = (EditText) findViewById(R.id.payMoney);
		passwordEditText = (EditText) findViewById(R.id.payWord);
		payButton = (Button) findViewById(R.id.payButton);
		payButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 取转账金额
				money = moneyEditText.getText().toString().trim();
				password = passwordEditText.getText().toString().trim();
				if (password.equals("")) {
					// 如果密码为空
					Toast.makeText(PayActivity.this, "请输入密码",
							Toast.LENGTH_SHORT).show();
				} else if (Double.valueOf(money)> balance) {
					// 如果转账金额大于余额，提示信息
					Toast.makeText(PayActivity.this, "账户余额不足",
							Toast.LENGTH_SHORT).show();
				} else {
					// TODO Auto-generated method stub
					// 转账金额合法，弹出安全键盘输入支付密码
					payMoney();
				}
			}
		});

	}
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		 
//        if (keyCode == KeyEvent.KEYCODE_BACK
//                 && event.getRepeatCount() == 0) {
//            //do something...
//        	Intent nextIntent = new Intent(PayActivity.this, NaviActivity.class);
//        	PayActivity.this.startActivity(nextIntent);  
//            // 结束该Activity  
//            finish();  
//             return true;
//         }
//         return super.onKeyDown(keyCode, event);
//     }

	protected void payMoney() {
		// TODO Auto-generated method stub
		int state;
		// 使用Map封装请求参数
		HashMap<String, String> map = new HashMap<String, String>();
		String id=String.valueOf(payId);
		map.put("payId", id);
		map.put("payTel", payTel);
		map.put("payMoney", money);
		map.put("payWord", Encrypt.getMD5(password));
		// 定义发送请求的URL
		String url = HttpUtil.Pay_URL; // GET方式
		try {
			// 发送请求
			String result = HttpUtil.postRequest(url, map, dbManager); // GET方式
			Log.d("服务器返回值", result);
			JSONObject json = new JSONObject(result);
			state = json.getInt("state");// 1是成功
			if (state == 1) {
				Toast.makeText(PayActivity.this, "转账成功", Toast.LENGTH_SHORT)
						.show();
				Intent refreshIntent = new Intent(PayActivity.this,
						NaviActivity.class);
				startActivity(refreshIntent);
				NaviActivity.MyNaviActivity.finish();
				finish();
			} else if(state==0) {
				// 失败，提示密码输入错误
				Toast.makeText(PayActivity.this, "连续输错三次，3小时内禁止操作",
						Toast.LENGTH_SHORT).show();
			}else if(state==2) {
				// 失败，提示密码输入错误
				Toast.makeText(PayActivity.this, "密码错误，重新输入",
						Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(PayActivity.this, "转账失败", Toast.LENGTH_SHORT).show();
		}

	}

}
