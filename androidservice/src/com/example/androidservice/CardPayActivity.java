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
//���п�
public class CardPayActivity extends Activity {
	private DBManager dbManager;
	String payTel;//�ϸ�intent����tel
	double balance;//�ϸ�intent����balance
	String cardId;//�ϸ�intent���� ѡ������п���
	String situation;//�ϸ�intent���� ��ֵ��������
	double cardBalance;//�ϸ�intent���� ���п�����ֵʱ�ж�
	TextView cardpayTitle;//ҳ�����
	TextView telTextView;//�˺�tel
	TextView balanceTextView;//�˺����
	TextView cardIdTextView;//���п���
	TextView passwordTitle;//�������
	EditText moneyEditText;//����ת�˽��
	EditText passwordEditText;//��������
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
		// ͨ��intent��ȡ���̴߳������û����������ַ���
		payTel = intent.getStringExtra("payTel");
		balance = intent.getDoubleExtra("balance", 0);
		cardId=intent.getStringExtra("cardId");
		situation=intent.getStringExtra("situation");
		cardBalance=intent.getDoubleExtra("cardBalance", 0.0);

		cardpayTitle=(TextView) findViewById(R.id.cardpayTitle);
		passwordTitle=(TextView) findViewById(R.id.cardpayPasswordTitle);
		
		telTextView = (TextView) findViewById(R.id.myTel);
		telTextView.setText("�˺ţ�" + payTel);
		
		balanceTextView = (TextView) findViewById(R.id.myBalance);
		balanceTextView.setText("��" + balance+"Ԫ");
		
		cardIdTextView = (TextView) findViewById(R.id.selectedCard);
		cardIdTextView.setText(cardId);

		//ת�˽��
		moneyEditText = (EditText) findViewById(R.id.money);
		//����
		passwordEditText = (EditText) findViewById(R.id.cardPassword);
		payButton = (Button) findViewById(R.id.cardPayButton);
		if(situation.equals("withdraw")){
			//����
			cardpayTitle.setText("��  ��");
			passwordTitle.setText("�˻�֧������:");
			payButton.setText("��   ��");
		}
		payButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ȡת�˽��
				money = moneyEditText.getText().toString().trim();
				password = passwordEditText.getText().toString().trim();
				if (password.equals("")) {
					// �������Ϊ��
					Toast.makeText(CardPayActivity.this, "����������",
							Toast.LENGTH_SHORT).show();
				} else if (situation.equals("withdraw")&&Double.valueOf(money)> balance) {
					// ��������֣��������ֽ���������ʾ��Ϣ
					Toast.makeText(CardPayActivity.this, "�˻�����",
							Toast.LENGTH_SHORT).show();
				} else if (situation.equals("recharge")&&Double.valueOf(money)> cardBalance) {
					// ����ǳ�ֵ�����ҳ�ֵ���������п�����ʾ��Ϣ
					Toast.makeText(CardPayActivity.this, "���п�����",
							Toast.LENGTH_SHORT).show();
				}else {
					// TODO Auto-generated method stub
					// ת�˽��Ϸ���������ȫ��������֧������
					payMoney();
				}
			}
		});

	}

	protected void payMoney() {
		// TODO Auto-generated method stub
		int state;
		String info;
		// ʹ��Map��װ�������
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("cardId", cardId);
		map.put("payMoney", money);
		map.put("situation", situation);
		map.put("payWord", Encrypt.getMD5(password));
		// ���巢�������URL
		String url = HttpUtil.CardPay_URL; 
		try {
			// ��������
			String result = HttpUtil.postRequest(url, map, dbManager); 
			Log.d("����������ֵ", result);
			JSONObject json = new JSONObject(result);
			state = json.getInt("state");// 1�ǳɹ�
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
				// ʧ�ܣ���ʾ�����������
				Toast.makeText(CardPayActivity.this, "����������Σ�3Сʱ�ڽ�ֹ����",
						Toast.LENGTH_SHORT).show();
			}else if(state==2) {
				// ʧ�ܣ���ʾ�����������
				Toast.makeText(CardPayActivity.this, "���������������",
						Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(CardPayActivity.this, "����"+info, Toast.LENGTH_SHORT)
						.show();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(CardPayActivity.this, "ת��ʧ��", Toast.LENGTH_SHORT).show();
		}

	}

}
