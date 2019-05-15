package com.security;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {
	String receiverEmail;//收件人地址
	String verifyCode;//验证码
	String userName;//收件人昵称
	String tel;//电话
	String type;
	public Email(String userName,String tel,String receiverEmail,String verifyCode,String type){
		this.userName=userName;
		this.tel=tel;
		this.receiverEmail=receiverEmail;
		this.verifyCode=verifyCode;
		if(type.equals("login")){
			this.type="登陆";
		}else if(type.equals("pay")){
			this.type="支付";
		}
	}
	public int sendEmail(){
		Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.port", "587");//使用465或587端口
        props.put("mail.smtp.auth", "true");//设置使用验证
        props.put("mail.smtp.starttls.enable","true");//使用 STARTTLS安全连接
        try {
            PopupAuthenticator auth = new PopupAuthenticator();
            Session session = Session.getInstance(props, auth);
            session.setDebug(true);//打印Debug信息
            MimeMessage message = new MimeMessage(session);
            Address addressFrom = new InternetAddress(PopupAuthenticator.mailuser + "@qq.com", "");//第一个参数为发送方电子邮箱地址；第二个参数为发送方邮箱地址的标签
            Address addressTo = new InternetAddress(receiverEmail, "");//第一个参数为接收方电子邮箱地址；第二个参数为接收方邮箱地址的标签
            message.setSubject("重置"+type+"密码");
            message.setText("尊敬的用户"+userName+",您的账号"+tel+"正在重置"+type+"密码，您的验证码是"+verifyCode);
            message.setFrom(addressFrom);
            message.addRecipient(Message.RecipientType.TO, addressTo);
            message.saveChanges();
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.qq.com", PopupAuthenticator.mailuser, PopupAuthenticator.password);
            transport.send(message);
            transport.close();
            System.out.println("发送成功");
            return 1;
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("发送失败");
            return 0;
        }
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
    }

	class PopupAuthenticator extends Authenticator {
	    public static final String mailuser = "458732368";//发送方邮箱'@'符号前的内容:1453296946@qq.com
	    public static final String password = "iuzouglbjpgfbiib";//成功开启IMAP/SMTP服务，在第三方客户端登录时，腾讯提供的密码。注意不是邮箱密码

	    public PasswordAuthentication getPasswordAuthentication() {
	        return new PasswordAuthentication(mailuser, password);
	    }
		}

}
