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
//����ҳ��
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
		// ͨ��intent��ȡ���̴߳������û����������ַ���
		payId = intent.getIntExtra("payId", 0);
		payName = intent.getStringExtra("payName");
		payTel = intent.getStringExtra("payTel");
		balance = intent.getDoubleExtra("balance", 0);

		telTextView = (TextView) findViewById(R.id.payTel);
		nameTextView = (TextView) findViewById(R.id.payName);
		telTextView.setText("�տ����˺ţ�" + payTel);
		nameTextView.setText("�տ��ˣ�" + payName);

		moneyEditText = (EditText) findViewById(R.id.payMoney);
		passwordEditText = (EditText) findViewById(R.id.payWord);
		payButton = (Button) findViewById(R.id.payButton);
		payButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ȡת�˽��
				money = moneyEditText.getText().toString().trim();
				password = passwordEditText.getText().toString().trim();
				if (password.equals("")) {
					// �������Ϊ��
					Toast.makeText(PayActivity.this, "����������",
							Toast.LENGTH_SHORT).show();
				} else if (Double.valueOf(money)> balance) {
					// ���ת�˽���������ʾ��Ϣ
					Toast.makeText(PayActivity.this, "�˻�����",
							Toast.LENGTH_SHORT).show();
				} else {
					// TODO Auto-generated method stub
					// ת�˽��Ϸ���������ȫ��������֧������
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
//            // ������Activity  
//            finish();  
//             return true;
//         }
//         return super.onKeyDown(keyCode, event);
//     }

	protected void payMoney() {
		// TODO Auto-generated method stub
		int state;
		// ʹ��Map��װ�������
		HashMap<String, String> map = new HashMap<String, String>();
		String id=String.valueOf(payId);
		map.put("payId", id);
		map.put("payTel", payTel);
		map.put("payMoney", money);
		map.put("payWord", Encrypt.getMD5(password));
		// ���巢�������URL
		String url = HttpUtil.Pay_URL; // GET��ʽ
		try {
			// ��������
			String result = HttpUtil.postRequest(url, map, dbManager); // GET��ʽ
			Log.d("����������ֵ", result);
			JSONObject json = new JSONObject(result);
			state = json.getInt("state");// 1�ǳɹ�
			if (state == 1) {
				Toast.makeText(PayActivity.this, "ת�˳ɹ�", Toast.LENGTH_SHORT)
						.show();
				Intent refreshIntent = new Intent(PayActivity.this,
						NaviActivity.class);
				startActivity(refreshIntent);
				NaviActivity.MyNaviActivity.finish();
				finish();
			} else if(state==0) {
				// ʧ�ܣ���ʾ�����������
				Toast.makeText(PayActivity.this, "����������Σ�3Сʱ�ڽ�ֹ����",
						Toast.LENGTH_SHORT).show();
			}else if(state==2) {
				// ʧ�ܣ���ʾ�����������
				Toast.makeText(PayActivity.this, "���������������",
						Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(PayActivity.this, "ת��ʧ��", Toast.LENGTH_SHORT).show();
		}

	}

}
