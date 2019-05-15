package com.example.androidservice;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.model.AccessToken;
import com.example.sqlite.DBManager;
import com.example.util.Encrypt;
import com.example.util.HttpUtil;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
 
public class ConnectService extends IntentService {
    private static final String ACTION_RECV_MSG = "com.example.logindemo.action.RECEIVE_MESSAGE";
    String result ="" ; 
    String flag="";
    int state;
    String userId;
    String tel;
    String userName="";
    String token="";
    DBManager dbManager;
    
    private AccessToken tokenObject=new AccessToken();
  //  List<HashMap<String, Object>> userInfo;//��¼�û���Ϣ
 
    public ConnectService() {
        super("TestIntentService");
        // TODO Auto-generated constructor stub
    }
     
    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        /** 
         * �����ԣ�IntentService�����ǿ��Խ��к�ʱ�Ĳ�����  
         * IntentServiceʹ�ö��еķ�ʽ�������Intent������У� 
         * Ȼ����һ��worker thread(�߳�)����������е�Intent   
         * �����첽��startService����IntentService�ᴦ�����һ��֮���ٴ���ڶ���   
         */ 
    	dbManager=new DBManager(this);
        //ͨ��intent��ȡ���̴߳������û����������ַ���  
        String tel = intent.getStringExtra("usertel");  
        String password = intent.getStringExtra("password");  
        try {
        	doLogin(tel, password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        Log.d("��¼���", flag);  
        Intent broadcastIntent = new Intent(); 
        broadcastIntent.setAction(ACTION_RECV_MSG);    
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);    
        broadcastIntent.putExtra("result", flag); 
        broadcastIntent.putExtra("state", state); 
        if(flag.equals("true")){
        	broadcastIntent.putExtra("userId", userId); 
        	broadcastIntent.putExtra("tel", tel); 
            broadcastIntent.putExtra("userName", userName); 
            broadcastIntent.putExtra("accessToken", token);
        }
        
        sendBroadcast(broadcastIntent);  
    }
     
     // ���巢������ķ���  
    private void doLogin(String tel, String password) throws JSONException  
    {  
    	//������ܴ���
        String enPW=Encrypt.getMD5(password);
        // ʹ��Map��װ�������  
        HashMap<String, String> map = new HashMap<String, String>();  
        map.put("tel", tel);  
        map.put("pw", enPW);  
        // ���巢�������URL  
        String url = HttpUtil.Login_URL + "?tel="+ tel +"&pw=" + enPW;  //GET��ʽ  
        Log.d(url, url);  
        Log.d(tel, tel);  
        Log.d(password, password);  
        try {  
            // ��������  
            result = HttpUtil.postRequest(url, map,dbManager);  //POST��ʽ  
            AnalysisUser(result);
            Log.d("����������ֵ", result);  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        
        if(flag.trim().equals("true")){  
        	//��token���浽���ݿ�
        	tokenObject.setUserId(Integer.parseInt(userId));
        	tokenObject.setToken(token);
        	dbManager.add(tokenObject);
        	Log.d("��token���������ݿ�", "");
        }
           
    }  
    
   
    
    /**
     * ����
     *
     * @throws JSONException
     */
    private  void  AnalysisUser(String jsonStr) throws JSONException {
        /******************* ���� ***********************/
    	        JSONObject jsonObject = new JSONObject(jsonStr);
    	        flag=jsonObject.getString("flag");
    	        state=jsonObject.getInt("state");
    	        if(flag.trim().equals("true")){
    	        	userName=jsonObject.getString("username");
    	        	tel=jsonObject.getString("tel");
        	        userId=jsonObject.getString("id");
        	        token=jsonObject.getString("accessToken");
    	        }
    }
    
    /**
     * ����
     *
     * @throws JSONException
     */
    private static ArrayList<HashMap<String, Object>> Analysis(String jsonStr)
            throws JSONException {
        /******************* ���� ***********************/
        JSONArray jsonArray = null;
        // ��ʼ��list�������
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        jsonArray = new JSONArray(jsonStr);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // ��ʼ��map�������
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", jsonObject.getInt("id"));
            map.put("name", jsonObject.getString("name"));
            list.add(map);
        }
        return list;
    }
}
