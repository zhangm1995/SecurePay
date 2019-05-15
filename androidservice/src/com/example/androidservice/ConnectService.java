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
  //  List<HashMap<String, Object>> userInfo;//登录用户信息
 
    public ConnectService() {
        super("TestIntentService");
        // TODO Auto-generated constructor stub
    }
     
    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        /** 
         * 经测试，IntentService里面是可以进行耗时的操作的  
         * IntentService使用队列的方式将请求的Intent加入队列， 
         * 然后开启一个worker thread(线程)来处理队列中的Intent   
         * 对于异步的startService请求，IntentService会处理完成一个之后再处理第二个   
         */ 
    	dbManager=new DBManager(this);
        //通过intent获取主线程传来的用户名和密码字符串  
        String tel = intent.getStringExtra("usertel");  
        String password = intent.getStringExtra("password");  
        try {
        	doLogin(tel, password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        Log.d("登录结果", flag);  
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
     
     // 定义发送请求的方法  
    private void doLogin(String tel, String password) throws JSONException  
    {  
    	//密码加密传输
        String enPW=Encrypt.getMD5(password);
        // 使用Map封装请求参数  
        HashMap<String, String> map = new HashMap<String, String>();  
        map.put("tel", tel);  
        map.put("pw", enPW);  
        // 定义发送请求的URL  
        String url = HttpUtil.Login_URL + "?tel="+ tel +"&pw=" + enPW;  //GET方式  
        Log.d(url, url);  
        Log.d(tel, tel);  
        Log.d(password, password);  
        try {  
            // 发送请求  
            result = HttpUtil.postRequest(url, map,dbManager);  //POST方式  
            AnalysisUser(result);
            Log.d("服务器返回值", result);  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        
        if(flag.trim().equals("true")){  
        	//将token保存到数据库
        	tokenObject.setUserId(Integer.parseInt(userId));
        	tokenObject.setToken(token);
        	dbManager.add(tokenObject);
        	Log.d("将token保存至数据库", "");
        }
           
    }  
    
   
    
    /**
     * 解析
     *
     * @throws JSONException
     */
    private  void  AnalysisUser(String jsonStr) throws JSONException {
        /******************* 解析 ***********************/
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
     * 解析
     *
     * @throws JSONException
     */
    private static ArrayList<HashMap<String, Object>> Analysis(String jsonStr)
            throws JSONException {
        /******************* 解析 ***********************/
        JSONArray jsonArray = null;
        // 初始化list数组对象
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        jsonArray = new JSONArray(jsonStr);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // 初始化map数组对象
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", jsonObject.getInt("id"));
            map.put("name", jsonObject.getString("name"));
            list.add(map);
        }
        return list;
    }
}
