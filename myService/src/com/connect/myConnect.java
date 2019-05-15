package com.connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

public class myConnect {

	/**
	 * @param args
	 */
	protected static String dbClassName="com.mysql.jdbc.Driver";
	protected static String dbUrl="jdbc:mysql://localhost:3306/myProject?characterEncoding=GBK";
	protected static String dbUser = "root";
	protected static String dbPwd = "660816";
	private static Connection conn=null;
	
	private myConnect(){
		try{
			if(conn==null){
				Class.forName(dbClassName).newInstance();
				conn=DriverManager.getConnection(dbUrl,dbUser,dbPwd);
			}else return;
		}catch(Exception e){
			e.printStackTrace();//��ӡ����쳣
		}
	}
	static ResultSet executeQuery(String sql) {
		try {
			if (conn == null)
				new myConnect();
			/*TYPE_SCROLL_SENSITIVE���� TYPE_SCROLL_INSENSITIVE һ���������ڼ�¼�ж�λ��
			���������ܵ������û��������ĵ�Ӱ�졣����û���ִ�����ѯ֮��ɾ��һ����¼��
			�Ǹ���¼���� ResultSet ����ʧ�����Ƶģ�������ֵ�ĸ���Ҳ����ӳ�� ResultSet �С�
			�ڶ����������� ResultSet �Ĳ����ԣ��ò���ȷ���Ƿ���Ը��� ResultSet��
			CONCUR_UPDATABLE��ָ�����Ը��� ResultSet */
			return conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE).executeQuery(sql);//����һ��resultset
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static int executeUpdateId(String sql) {
		try {
			if (conn == null)
				new myConnect();
			Statement stmt=conn.createStatement();
			stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();  
			int keyValue = -1;  
			if (rs.next()) {  
			    keyValue = rs.getInt(1);  
			}  
			return keyValue;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return -1;
		}
	}

	public static int executeUpdate(String sql) {
		try {
			if (conn == null)
				new myConnect();
			return conn.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return -1;
		}
	}

	public static void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn = null;
		}
	}

}
