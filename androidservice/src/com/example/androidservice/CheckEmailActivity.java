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
//������֤
public class CheckEmailActivity extends Activity {
	private DBManager dbManager;
	private String page="";//���ĸ�ҳ�����
	private String type="";//��½�����֧������
	private String tel = "";// �ֻ���
	private String email="";//����
	private String code="";//������֤��
	EditText emailEditText;
	EditText telText;
	EditText codeText;
	TextView infoText;
	Button checkEmailButton;
	Button sendCodeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_emailcheck);
		Intent intent = getIntent();// ��ȡ������Activity��intent����
		page=intent.getStringExtra("page");
		type=intent.getStringExtra("type");
		
		emailEditText = (EditText) findViewById(R.id.checkEmail);
		telText = (EditText) findViewById(R.id.checkTel);
		codeText = (EditText) findViewById(R.id.checkCode);
		infoText=(TextView)findViewById(R.id.info);
		if(page.equals("edit")){
			//�Ӹ�������ҳ����
			tel = intent.getStringExtra("tel");
			email= intent.getStringExtra("email");
			telText.setText(tel);
			emailEditText.setText(email);
		}
		
		dbManager = new DBManager(this);
		
		sendCodeButton=(Button)findViewById(R.id.btn_sendCode);
		sendCodeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					sendCode();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		// ������֤��
		checkEmailButton=(Button)findViewById(R.id.btn_checkCode);
		checkEmailButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					checkCode();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	private void sendCode()throws Exception {
		String myEmail = emailEditText.getText().toString().trim();
		String myTel = telText.getText().toString();
		if(myEmail.equals("")){
			Toast.makeText(CheckEmailActivity.this, "�����ַ����Ϊ��",
					Toast.LENGTH_SHORT).show();
		}else if(myTel.equals("")){
			Toast.makeText(CheckEmailActivity.this, "�ֻ��Ų���Ϊ��",
					Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(CheckEmailActivity.this, "�벻Ҫ�ظ���������ڷ�����֤��...",
					Toast.LENGTH_SHORT).show();
			infoText.setText("�벻Ҫ�ظ���������ڷ�����֤��...");
			// ʹ��Map��װ�������
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("email", myEmail);
			map.put("tel", myTel);
			map.put("page", page);
			map.put("type", type);
			// ���巢�������URL
			String url = HttpUtil.SendCode_URL ;
			String result = HttpUtil.postRequest(url, map, dbManager);
			Log.d("����������ֵ", result);
			JSONObject json = new JSONObject(result);
			int state = json.getInt("state");// 1�ǳɹ�
			if(state==1){
				//������֤��ɹ�
				Toast.makeText(CheckEmailActivity.this, "��֤�뷢�ͳɹ���ע������ʼ�",
						Toast.LENGTH_SHORT).show();
				infoText.setText("��֤�뷢�ͳɹ���ע������ʼ�");
			} else if(state==2){
				// �޸�ʧ��
				Toast.makeText(CheckEmailActivity.this, "�����ַ����",
						Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(CheckEmailActivity.this, "��֤�뷢��ʧ��",
						Toast.LENGTH_SHORT).show();
			}
		}
		
	}

	private void checkCode() throws Exception {
		// TODO Auto-generated method stub
		// ������֤��
		code = codeText.getText().toString().trim();
		String myEmail = emailEditText.getText().toString().trim();
		String myTel = telText.getText().toString();
		if(code.equals("")){
			Toast.makeText(CheckEmailActivity.this, "��֤�벻��Ϊ��",
					Toast.LENGTH_SHORT).show();
		}else if(myEmail.equals("")){
			Toast.makeText(CheckEmailActivity.this, "�����ַ����Ϊ��",
					Toast.LENGTH_SHORT).show();
		}else if(myTel.equals("")){
			Toast.makeText(CheckEmailActivity.this, "�ֻ��Ų���Ϊ��",
					Toast.LENGTH_SHORT).show();
		}else{
			// ʹ��Map��װ�������
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("code",  code);
			map.put("email", myEmail);
			map.put("tel", myTel);
			map.put("page", page);
			// ���巢�������URL
			String url = HttpUtil.CheckCode_URL ;
			String result = HttpUtil.postRequest(url, map, dbManager);
			Log.d("����������ֵ", result);
			JSONObject json = new JSONObject(result);
			int state = json.getInt("state");// 1�ǳɹ�
			if(state==1){
				//��֤�ɹ�����ת�޸�ҳ��
				Toast.makeText(CheckEmailActivity.this, "��֤�ɹ�",
						Toast.LENGTH_SHORT).show();
				Intent passwordIntent = new Intent(
						CheckEmailActivity.this, UpdatePasswordActivity.class);
				passwordIntent.putExtra("type",type);
				passwordIntent.putExtra("page",page);
				if(page.equals("login")){
					passwordIntent.putExtra("tel",myTel);
				}
				startActivity(passwordIntent);
				finish();
			} else {
				// �޸�ʧ��
				Toast.makeText(CheckEmailActivity.this, "��֤ʧ��",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
