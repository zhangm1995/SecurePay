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
//У��֧������
public class CheckPasswordActivity extends Activity {
	private DBManager dbManager;
	static Activity checkPasswordActivity;
	String type="";
	String tel;
	String email;
	String oldPassword;
	EditText oldEditText;
	Button checkBtn;
	Button forgetBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_check_paypwd);
		dbManager = new DBManager(this);
		checkPasswordActivity=this;
		Intent intent=getIntent();
		type=intent.getStringExtra("type");
		tel = intent.getStringExtra("tel");
		email=intent.getStringExtra("email");
		
		oldEditText=(EditText)findViewById(R.id.oldPayword);
		//��������
		forgetBtn=(Button)findViewById(R.id.btn_forget);
		forgetBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(CheckPasswordActivity.this,CheckEmailActivity.class);
				intent.putExtra("page", "edit");
				intent.putExtra("type", "pay");
				intent.putExtra("tel", tel);
				intent.putExtra("email", email);
				startActivity(intent);
				finish();
			}
		});	
		//��������
		checkBtn=(Button)findViewById(R.id.btn_checkPwd);
		checkBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				oldPassword=oldEditText.getText().toString().trim();
				if (oldPassword.equals("")) {
					Toast.makeText(CheckPasswordActivity.this, "���벻��Ϊ��",
							Toast.LENGTH_SHORT).show();
				} else {
					String enPassword = Encrypt.getMD5(oldPassword);// ���ܺ������
					String url = HttpUtil.CheckPassword_URL;
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("password", enPassword);
					map.put("type", type);
					try {
						String result = HttpUtil.postRequest(url, map,
								dbManager);
						JSONObject json = new JSONObject(result);
						int state = json.getInt("state");
						//0 ����������Σ�3Сʱ�ھܾ���¼
						//1����У��ɹ�
						//2����У��ʧ�ܣ���������
						//3�˺Ų�����
						if (state == 1) {// �ɹ�����ת��һҳ��
							Toast.makeText(CheckPasswordActivity.this, "ԭ����У��ɹ�",
									Toast.LENGTH_SHORT).show();
							Intent payPwdIntent=new Intent(CheckPasswordActivity.this,UpdatePasswordActivity.class);
							payPwdIntent.putExtra("type", type);
							payPwdIntent.putExtra("page", "edit");
							startActivity(payPwdIntent);
							finish();
						} else if(state==0) {
							// ʧ�ܣ���ʾ�����������
							Toast.makeText(CheckPasswordActivity.this, "����������Σ�3Сʱ�ھܾ��޸�",
									Toast.LENGTH_SHORT).show();
						}else if(state==2) {
							// ʧ�ܣ���ʾ�����������
							Toast.makeText(CheckPasswordActivity.this, "���������������",
									Toast.LENGTH_SHORT).show();
						}else if(state==3) {
							// ʧ�ܣ���ʾ�����������
							Toast.makeText(CheckPasswordActivity.this, "�˺Ų�����",
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Toast.makeText(CheckPasswordActivity.this, "ԭ����У��ʧ��",
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
			}
		});

	}
	
}
