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
//银行卡
public class CardPayActivity extends Activity {
	private DBManager dbManager;
	String payTel;//上个intent传来tel
	double balance;//上个intent传来balance
	String cardId;//上个intent传来 选择的银行卡号
	String situation;//上个intent传来 充值还是提现
	double cardBalance;//上个intent传来 银行卡余额，充值时判断
	TextView cardpayTitle;//页面标题
	TextView telTextView;//账号tel
	TextView balanceTextView;//账号余额
	TextView cardIdTextView;//银行卡号
	TextView passwordTitle;//密码标题
	EditText moneyEditText;//输入转账金额
	EditText passwordEditText;//输入密码
	Button payButton;
	String money;
	String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cardpay);
		dbManager = new DBManager(this);

		Intent intent = getIntent();
		// 通过intent获取主线程传来的用户名和密码字符串
		payTel = intent.getStringExtra("payTel");
		balance = intent.getDoubleExtra("balance", 0);
		cardId=intent.getStringExtra("cardId");
		situation=intent.getStringExtra("situation");
		cardBalance=intent.getDoubleExtra("cardBalance", 0.0);

		cardpayTitle=(TextView) findViewById(R.id.cardpayTitle);
		passwordTitle=(TextView) findViewById(R.id.cardpayPasswordTitle);
		
		telTextView = (TextView) findViewById(R.id.myTel);
		telTextView.setText("账号：" + payTel);
		
		balanceTextView = (TextView) findViewById(R.id.myBalance);
		balanceTextView.setText("余额：" + balance+"元");
		
		cardIdTextView = (TextView) findViewById(R.id.selectedCard);
		cardIdTextView.setText(cardId);

		//转账金额
		moneyEditText = (EditText) findViewById(R.id.money);
		//密码
		passwordEditText = (EditText) findViewById(R.id.cardPassword);
		payButton = (Button) findViewById(R.id.cardPayButton);
		if(situation.equals("withdraw")){
			//提现
			cardpayTitle.setText("提  现");
			passwordTitle.setText("账户支付密码:");
			payButton.setText("提   现");
		}
		payButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 取转账金额
				money = moneyEditText.getText().toString().trim();
				password = passwordEditText.getText().toString().trim();
				if (password.equals("")) {
					// 如果密码为空
					Toast.makeText(CardPayActivity.this, "请输入密码",
							Toast.LENGTH_SHORT).show();
				} else if (situation.equals("withdraw")&&Double.valueOf(money)> balance) {
					// 如果是提现，并且提现金额大于余额，提示信息
					Toast.makeText(CardPayActivity.this, "账户余额不足",
							Toast.LENGTH_SHORT).show();
				} else if (situation.equals("recharge")&&Double.valueOf(money)> cardBalance) {
					// 如果是充值，并且充值金额大于银行卡余额，提示信息
					Toast.makeText(CardPayActivity.this, "银行卡余额不足",
							Toast.LENGTH_SHORT).show();
				}else {
					// TODO Auto-generated method stub
					// 转账金额合法，弹出安全键盘输入支付密码
					payMoney();
				}
			}
		});

	}

	protected void payMoney() {
		// TODO Auto-generated method stub
		int state;
		String info;
		// 使用Map封装请求参数
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("cardId", cardId);
		map.put("payMoney", money);
		map.put("situation", situation);
		map.put("payWord", Encrypt.getMD5(password));
		// 定义发送请求的URL
		String url = HttpUtil.CardPay_URL; 
		try {
			// 发送请求
			String result = HttpUtil.postRequest(url, map, dbManager); 
			Log.d("服务器返回值", result);
			JSONObject json = new JSONObject(result);
			state = json.getInt("state");// 1是成功
			info=json.getString("info");
			if (state == 1) {
				Toast.makeText(CardPayActivity.this, info, Toast.LENGTH_SHORT)
						.show();
				Intent refreshIntent = new Intent(CardPayActivity.this,
						NaviActivity.class);
				startActivity(refreshIntent);
				CardActivity.cardActivity.finish();
				NaviActivity.MyNaviActivity.finish();
				finish();
			} else if(state==0) {
				// 失败，提示密码输入错误
				Toast.makeText(CardPayActivity.this, "连续输错三次，3小时内禁止操作",
						Toast.LENGTH_SHORT).show();
			}else if(state==2) {
				// 失败，提示密码输入错误
				Toast.makeText(CardPayActivity.this, "密码错误，重新输入",
						Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(CardPayActivity.this, "错误："+info, Toast.LENGTH_SHORT)
						.show();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(CardPayActivity.this, "转账失败", Toast.LENGTH_SHORT).show();
		}

	}

}
