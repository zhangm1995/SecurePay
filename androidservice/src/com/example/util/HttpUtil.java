package com.example.util;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.example.sqlite.DBManager;
 
import android.content.Entity;
import android.util.Log;
 
public class HttpUtil {
    // 创建HttpClient对象
    public static HttpClient httpClient = new DefaultHttpClient();
    public static final String Base_URL="https://192.168.43.223:8443";
    public static final String Login_URL = Base_URL+"/myService/HelloServlet2";// 登录
    public static final String Detail_URL = Base_URL+"/myService/AccountServlet"; //首页各项信息，交易记录等
    public static final String UpdateInfo_URL = Base_URL+"/myService/UpdateInfoServlet"; //修改个人资料
   // public static final String DeleteToken_URL = Base_URL+"/myService/DeleteTokenServlet"; //退出登录，删除token
    public static final String CheckPassword_URL = Base_URL+"/myService/PasswordServlet"; //校验密码是否正确
    public static final String UpdatePassword_URL = Base_URL+"/myService/UpdatePasswordServlet"; //修改密码
    public static final String SelectUser_URL = Base_URL+"/myService/SelectUserServlet"; //查找用户
    public static final String Pay_URL = Base_URL+"/myService/PayServlet"; //转账
    public static final String Register_URL = Base_URL+"/myService/RegisterServlet"; //注册
    public static final String Binding_URL = Base_URL+"/myService/BankServlet"; //绑定银行卡
    public static final String RelieveBinding_URL = Base_URL+"/myService/RelieveServlet"; //解除绑定银行卡
    public static final String Card_URL = Base_URL+"/myService/CardServlet"; //查询银行卡
    public static final String CardPay_URL = Base_URL+"/myService/CardPayServlet"; //充值提现
    public static final String SendCode_URL = Base_URL+"/myService/EmailServlet"; //发送邮件验证码
    public static final String CheckCode_URL = Base_URL+"/myService/EmailCodeServlet"; //验证邮件验证码
    
   
    private static String accessToken="";
    
    /**
     * 
     * @param url
     *            发送请求的URL
     * @return 服务器响应字符串
     * @throws Exception
     */
    public static String getRequest(String url,DBManager dbManager) throws Exception {
        // 创建HttpGet对象。
        HttpGet get = new HttpGet(url);
        accessToken=dbManager.select();
    	if(!accessToken.equals("")){
    		get.setHeader("accessToken",accessToken);//将token放在头部
    	}
        // 发送GET请求
        HttpResponse httpResponse = httpClient.execute(get);
        // 如果服务器成功地返回响应
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            // 获取服务器响应字符串
            String result = EntityUtils.toString(httpResponse.getEntity());
            return result;
        } else {
            Log.d("服务器响应代码", (new Integer(httpResponse.getStatusLine()
                    .getStatusCode())).toString());
            return null;
        }
    }
 
    /**
     * 
     * @param url
     *            发送请求的URL
     * @param params
     *            请求参数
     * @return 服务器响应字符串
     * @throws Exception
     */
    public static String postRequest(String url, Map<String, String> rawParams,DBManager dbManager)
            throws Exception {
    	
        // 创建HttpPost对象。
        HttpPost post = new HttpPost(url);
        accessToken=dbManager.select();
    	if(!accessToken.equals("")){
        	post.setHeader("accessToken",accessToken);//将token放在头部
    	}
        // 如果传递参数个数比较多的话可以对传递的参数进行封装
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (String key : rawParams.keySet()) {
            // 封装请求参数
            params.add(new BasicNameValuePair(key, rawParams.get(key)));
        }
        // 设置请求参数
        post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        // 发送POST请求
        HttpResponse httpResponse = httpClient.execute(post);
        // 如果服务器成功地返回响应
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            // 获取服务器响应字符串
            String result = EntityUtils.toString(httpResponse.getEntity());
            return result;
        }
        return null;
    }
    
    public static void initKey(KeyStore trustStore) throws Exception {
        //然后利用KeyStore创建SSLSocketFactory对象
        SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
        //然后指定使用HTTPS协议和相应端口号
        Scheme sch = new Scheme("https", socketFactory, 8443);
        httpClient.getConnectionManager().getSchemeRegistry().register(sch);
    }
}