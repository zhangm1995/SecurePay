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

//�����п�
public class BindingActivity extends Activity {
	private DBManager dbManager;
	private String tel = "";// �ֻ���
	private String cardId;//���п���
	private String password = "";// ֧������
	private String times;//�ڼ������
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
		// ��
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
		// ����Ϣ
		cardId = cardIdEditText.getText().toString().trim();
		password = passwordEdit.getText().toString();

		if (tel.equals("")) {
			Toast.makeText(BindingActivity.this, "�ֻ��Ų���Ϊ��", Toast.LENGTH_SHORT)
					.show();
		} else if (cardId.equals("")) {
			Toast.makeText(BindingActivity.this, "���п��Ų���Ϊ��", Toast.LENGTH_SHORT)
					.show();
		} else if (password.equals("")) {
			Toast.makeText(BindingActivity.this, "���벻��Ϊ��",
					Toast.LENGTH_SHORT).show();
		} else {
			String encryptPW = Encrypt.getMD5(password);
			// ʹ��Map��װ�������
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("tel", tel);
			map.put("cardId", cardId);
			map.put("password", encryptPW);
			// ���巢�������URL
			String url = HttpUtil.Binding_URL ;
			String result = HttpUtil.postRequest(url, map, dbManager);
			Log.d("����������ֵ", result);
			JSONObject json = new JSONObject(result);
			int state = json.getInt("state");// 0ʧ�ܣ�1�ɹ���2�绰�����п���Ϣ��һ��

			if (state == 1) {
				// �󶨳ɹ�����ת
				Toast.makeText(BindingActivity.this, "�󶨳ɹ�",
						Toast.LENGTH_SHORT).show();
				NaviActivity.isBinding++;
				if(times.equals("one")){
					//��һ�����
					Intent cardIntent = new Intent(BindingActivity.this,
							CardActivity.class);
					cardIntent.putExtra("tel", tel);
					cardIntent.putExtra("situation", "setting");//������ҳ����
					startActivity(cardIntent);
					finish();
				}else{
					//������
					Intent cardIntent = new Intent(BindingActivity.this,
							CardActivity.class);
					cardIntent.putExtra("tel", tel);
					cardIntent.putExtra("situation", "setting");//������ҳ����
					CardActivity.cardActivity.finish();
					startActivity(cardIntent);
					finish();
				}
				
			} else if (state == 2) {
				Toast.makeText(BindingActivity.this, "���п���Ϣ����",
						Toast.LENGTH_SHORT).show();
			}else if (state == 3) {
				Toast.makeText(BindingActivity.this, "�Ѱ󶨸����п��������ٴΰ�",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(BindingActivity.this, "������󣬰�ʧ��",
						Toast.LENGTH_SHORT).show();
			}

		}
	}
}
