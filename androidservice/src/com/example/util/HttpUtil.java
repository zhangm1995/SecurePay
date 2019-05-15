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
    // ����HttpClient����
    public static HttpClient httpClient = new DefaultHttpClient();
    public static final String Base_URL="https://192.168.43.223:8443";
    public static final String Login_URL = Base_URL+"/myService/HelloServlet2";// ��¼
    public static final String Detail_URL = Base_URL+"/myService/AccountServlet"; //��ҳ������Ϣ�����׼�¼��
    public static final String UpdateInfo_URL = Base_URL+"/myService/UpdateInfoServlet"; //�޸ĸ�������
   // public static final String DeleteToken_URL = Base_URL+"/myService/DeleteTokenServlet"; //�˳���¼��ɾ��token
    public static final String CheckPassword_URL = Base_URL+"/myService/PasswordServlet"; //У�������Ƿ���ȷ
    public static final String UpdatePassword_URL = Base_URL+"/myService/UpdatePasswordServlet"; //�޸�����
    public static final String SelectUser_URL = Base_URL+"/myService/SelectUserServlet"; //�����û�
    public static final String Pay_URL = Base_URL+"/myService/PayServlet"; //ת��
    public static final String Register_URL = Base_URL+"/myService/RegisterServlet"; //ע��
    public static final String Binding_URL = Base_URL+"/myService/BankServlet"; //�����п�
    public static final String RelieveBinding_URL = Base_URL+"/myService/RelieveServlet"; //��������п�
    public static final String Card_URL = Base_URL+"/myService/CardServlet"; //��ѯ���п�
    public static final String CardPay_URL = Base_URL+"/myService/CardPayServlet"; //��ֵ����
    public static final String SendCode_URL = Base_URL+"/myService/EmailServlet"; //�����ʼ���֤��
    public static final String CheckCode_URL = Base_URL+"/myService/EmailCodeServlet"; //��֤�ʼ���֤��
    
   
    private static String accessToken="";
    
    /**
     * 
     * @param url
     *            ���������URL
     * @return ��������Ӧ�ַ���
     * @throws Exception
     */
    public static String getRequest(String url,DBManager dbManager) throws Exception {
        // ����HttpGet����
        HttpGet get = new HttpGet(url);
        accessToken=dbManager.select();
    	if(!accessToken.equals("")){
    		get.setHeader("accessToken",accessToken);//��token����ͷ��
    	}
        // ����GET����
        HttpResponse httpResponse = httpClient.execute(get);
        // ����������ɹ��ط�����Ӧ
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            // ��ȡ��������Ӧ�ַ���
            String result = EntityUtils.toString(httpResponse.getEntity());
            return result;
        } else {
            Log.d("��������Ӧ����", (new Integer(httpResponse.getStatusLine()
                    .getStatusCode())).toString());
            return null;
        }
    }
 
    /**
     * 
     * @param url
     *            ���������URL
     * @param params
     *            �������
     * @return ��������Ӧ�ַ���
     * @throws Exception
     */
    public static String postRequest(String url, Map<String, String> rawParams,DBManager dbManager)
            throws Exception {
    	
        // ����HttpPost����
        HttpPost post = new HttpPost(url);
        accessToken=dbManager.select();
    	if(!accessToken.equals("")){
        	post.setHeader("accessToken",accessToken);//��token����ͷ��
    	}
        // ������ݲ��������Ƚ϶�Ļ����ԶԴ��ݵĲ������з�װ
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (String key : rawParams.keySet()) {
            // ��װ�������
            params.add(new BasicNameValuePair(key, rawParams.get(key)));
        }
        // �����������
        post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        // ����POST����
        HttpResponse httpResponse = httpClient.execute(post);
        // ����������ɹ��ط�����Ӧ
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            // ��ȡ��������Ӧ�ַ���
            String result = EntityUtils.toString(httpResponse.getEntity());
            return result;
        }
        return null;
    }
    
    public static void initKey(KeyStore trustStore) throws Exception {
        //Ȼ������KeyStore����SSLSocketFactory����
        SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
        //Ȼ��ָ��ʹ��HTTPSЭ�����Ӧ�˿ں�
        Scheme sch = new Scheme("https", socketFactory, 8443);
        httpClient.getConnectionManager().getSchemeRegistry().register(sch);
    }
}