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
//ѡ������ҳ��
public class PasswordActivity extends Activity {
	private DBManager dbManager;
	static Activity passwordActivity;
	String type="";
	String tel;
	String email;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_password);
		passwordActivity=this;
		dbManager = new DBManager(this);
		Intent intent=getIntent();
		tel = intent.getStringExtra("tel");
		email=intent.getStringExtra("email");

		//��¼����
		View myInfo = findViewById(R.id.loginPwd);
		myInfo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				type="login";
				showAlertDialog(v);
			}
		});

		//֧������
		View myCard = findViewById(R.id.payPwd);
		myCard.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				type="pay";
				Intent intent=new Intent (PasswordActivity.this,CheckPasswordActivity.class);
				intent.putExtra("type", type);
				intent.putExtra("tel", tel);
				intent.putExtra("email", email);
				startActivity(intent);
			}
		});

	}
	
	public void showAlertDialog(View view) {

		final CustomDialog.Builder builder = new CustomDialog.Builder(this);
		if(type.equals("login")){
			builder.setMessage("������ԭ��¼����");
			builder.setTitle("������ԭ��¼����");
		}
		builder.setInputType(1);
		
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// ������Ĳ�������
				String oldPassword = builder.getData();
				if (oldPassword.equals("")) {
					Toast.makeText(PasswordActivity.this, "���벻��Ϊ��",
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
						int state = json.getInt("state");// 1�ǳɹ�
						if (state == 1) {// �ɹ�����ת��һҳ��
							Toast.makeText(PasswordActivity.this, "ԭ����У��ɹ�",
									Toast.LENGTH_SHORT).show();
							Intent loginPwdIntent=new Intent(PasswordActivity.this,UpdatePasswordActivity.class);
							loginPwdIntent.putExtra("type", type);
							startActivity(loginPwdIntent);
						} else if(state==0) {
							// ʧ�ܣ���ʾ�����������
							Toast.makeText(PasswordActivity.this, "����������Σ�3Сʱ�ھܾ��޸�",
									Toast.LENGTH_SHORT).show();
						}else if(state==2) {
							// ʧ�ܣ���ʾ�����������
							Toast.makeText(PasswordActivity.this, "���������������",
									Toast.LENGTH_SHORT).show();
						}else if(state==3) {
							// ʧ�ܣ���ʾ�����������
							Toast.makeText(PasswordActivity.this, "�˺Ų�����",
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Toast.makeText(PasswordActivity.this, "ԭ����У��ʧ��",
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();

	}

}
