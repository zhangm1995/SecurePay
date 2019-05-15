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
//������Ϣҳ��
public class PersonInfoActivity extends Activity {
	private DBManager dbManager;
	private String userName = "";// �û���
	private String tel = "";// �ֻ���
	private String email="";//����
	EditText nameEditText;
	EditText emailEditText;
	TextView telText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_person);
		Intent intent = getIntent();// ��ȡ������Activity��intent����
		userName = intent.getStringExtra("userName");
		tel = intent.getStringExtra("tel");
		email= intent.getStringExtra("email");
		nameEditText = (EditText) findViewById(R.id.personName);
		emailEditText = (EditText) findViewById(R.id.personEmail);
		telText = (TextView) findViewById(R.id.personTel);
		nameEditText.setText(userName);
		telText.setText(tel);
		emailEditText.setText(email);
		dbManager = new DBManager(this);
		// �޸ĸ�������
		Button updateButton = (Button) findViewById(R.id.updateButton);
		updateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					int state = updateInfo();
					if (state == 1) {
						// �޸ĳɹ�
						Toast.makeText(PersonInfoActivity.this, "�޸ĳɹ�",
								Toast.LENGTH_SHORT).show();
						Intent refreshIntent = new Intent(
								PersonInfoActivity.this, NaviActivity.class);
						startActivity(refreshIntent);
						SettingActivity.SettingActivity.finish();
						NaviActivity.MyNaviActivity.finish();
						finish();
					} else {
						// �޸�ʧ��
						Toast.makeText(PersonInfoActivity.this, "�޸�ʧ��",
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	//
	// if (keyCode == KeyEvent.KEYCODE_BACK
	// && event.getRepeatCount() == 0) {
	// //do something...
	// Intent nextIntent = new Intent(PersonInfoActivity.this,
	// NaviActivity.class);
	// PersonInfoActivity.this.startActivity(nextIntent);
	// // ������Activity
	// finish();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	private int updateInfo() throws Exception {
		// TODO Auto-generated method stub
		// �޸ĺ����Ϣ
		String newName = nameEditText.getText().toString().trim();
		String newEmail = emailEditText.getText().toString().trim();
		String newTel = telText.getText().toString();
		// ʹ��Map��װ�������
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("userName", newName);
		map.put("email", newEmail);
		Log.d("newName", newName);
		map.put("tel", newTel);
		// ���巢�������URL
		String url = HttpUtil.UpdateInfo_URL + "?userName=" + newName + "&email="
				+ newEmail;
		String result = HttpUtil.postRequest(url, map, dbManager);
		Log.d("����������ֵ", result);
		JSONObject json = new JSONObject(result);
		int state = json.getInt("state");// 1�ǳɹ�
		return state;
	}

}
