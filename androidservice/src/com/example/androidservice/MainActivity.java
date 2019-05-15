package com.example.androidservice;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import com.example.sqlite.DBManager;
import com.example.util.HttpUtil;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//��¼����
public class MainActivity extends Activity {
	private static final String ACTION_RECV_MSG = "com.example.logindemo.action.RECEIVE_MESSAGE";
    private Button loginBtn;
    private Button regBtn;
    private Button forgetBtn;
    private EditText et_usertel;
    private EditText et_password;
    private String userTel;
    private String passWord;
    private MessageReceiver receiver ;
    private DBManager dbManager;
    public String token;
    static Activity LoginActivity;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	//����һ��KeyStoreʵ��
			KeyStore trustStore = KeyStore.getInstance("BKS");
			//������Դ�е�keystore
			trustStore.load(
                    getBaseContext().getResources().openRawResource(
                            R.raw.server_trust6), "123456".toCharArray());
        	HttpUtil.initKey(trustStore);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int sig=getSignature("com.example.androidservice");
        Log.d("����������ֵ", sig+"");
        if(sig!=-1399940094){
        	Toast.makeText(MainActivity.this, "��⵽���򱻴۸�",Toast.LENGTH_SHORT).show();
        }else{
        	dbManager=new DBManager(this);
            token=dbManager.select();
            if(token.equals("")){
            	//û��token����ת����¼ҳ��
            	requestWindowFeature(Window.FEATURE_NO_TITLE);
            	setContentView(R.layout.activity_main);
            	if (android.os.Build.VERSION.SDK_INT > 9) {
            	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            	    StrictMode.setThreadPolicy(policy);
            	}
            	 initView();
            	 LoginActivity=this;
                 //��̬ע��receiver    
                 IntentFilter filter = new IntentFilter(ACTION_RECV_MSG);    
                 filter.addCategory(Intent.CATEGORY_DEFAULT);    
                 receiver = new MessageReceiver();    
                 registerReceiver(receiver, filter); 
            }else{
            	//��token�����õ�½
            	Intent nextIntent = new Intent(MainActivity.this, NaviActivity.class);  
                startActivity(nextIntent); 
                finish();
            }
        }
        
    }
     
    private void initView() {
        // TODO Auto-generated method stub
        et_usertel = (EditText)findViewById(R.id.et_user);
        et_password =( EditText)findViewById(R.id.et_psw);
        //��¼
        loginBtn = (Button)findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if(matchLoginMsg())
                {
                    // ���У��ɹ�  
                    Intent msgIntent = new Intent(MainActivity.this, ConnectService.class);  
                    msgIntent.putExtra("usertel", et_usertel.getText().toString().trim());  
                    msgIntent.putExtra("password", et_password.getText().toString().trim());  
                    startService(msgIntent); 
                }
                 
            }
        });
        //ע��
        regBtn = (Button)findViewById(R.id.RegisButton);
        regBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	Intent regIntent = new Intent(MainActivity.this, RegisterActivity.class);
            	startActivity(regIntent);
            }
        });
      //��������
        forgetBtn = (Button)findViewById(R.id.btn_forgetLogin);
        forgetBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	Intent forgetIntent = new Intent(MainActivity.this, CheckEmailActivity.class);
            	forgetIntent.putExtra("page", "login");
            	forgetIntent.putExtra("type","login");
            	startActivity(forgetIntent);
            }
        });
    }
     
    protected boolean matchLoginMsg() {
        // TODO Auto-generated method stub
        userTel = et_usertel.getText().toString().trim();
        passWord = et_password.getText().toString().trim();
        if(userTel.equals(""))
        {
            Toast.makeText(MainActivity.this, "�˺Ų���Ϊ��",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(passWord.equals(""))
        {
            Toast.makeText(MainActivity.this, "���벻��Ϊ��",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    //���չ㲥��    
    public class MessageReceiver extends BroadcastReceiver {    
        @Override   
        public void onReceive(Context context, Intent intent) {    
           String result = intent.getStringExtra("result");
           String userId=intent.getStringExtra("userId");
           String tel=intent.getStringExtra("tel");
           String userName=intent.getStringExtra("userName");
           String accessToken=intent.getStringExtra("accessToken");
           int state= intent.getIntExtra("state",0);
           Log.i("MessageReceiver", result);  
        // �����¼�ɹ�  
            if (result.equals("true")){  
                // ����Main Activity  
                Intent nextIntent = new Intent(MainActivity.this, NaviActivity.class);  
                nextIntent.putExtra("result", result); 
                nextIntent.putExtra("userId", userId); 
                nextIntent.putExtra("tel", tel); 
                nextIntent.putExtra("userName", userName); 
                nextIntent.putExtra("accessToken", accessToken);
                MainActivity.this.startActivity(nextIntent);  
                // ������Activity  
                finish();  
                //ע���㲥������  
                context.unregisterReceiver(this);  
            }else if(state==0) {
				// ʧ�ܣ���ʾ�����������
				Toast.makeText(MainActivity.this, "����������Σ�3Сʱ�ھܾ���¼",
						Toast.LENGTH_SHORT).show();
			}else if(state==2) {
				// ʧ�ܣ���ʾ�����������
				Toast.makeText(MainActivity.this, "���������������",
						Toast.LENGTH_SHORT).show();
			}else if(state==3) {
				// ʧ�ܣ���ʾ�����������
				Toast.makeText(MainActivity.this, "�˺Ų�����",
						Toast.LENGTH_SHORT).show();
			}else{  
                Toast.makeText(MainActivity.this," �û����������������������!",Toast.LENGTH_SHORT).show(); 
            }  
               
        }    
    }    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public int getSignature(String packageName){
    	PackageManager pm=this.getPackageManager();
    	PackageInfo pi=null;
    	int sig=0;
    	try {
			pi=pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
			Signature[] s=pi.signatures;
	    	sig=s[0].hashCode();//����ǩ����Hashֵ
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			sig=0;//����ʱ����0
			e.printStackTrace();
		}
		return sig;
    }
}
