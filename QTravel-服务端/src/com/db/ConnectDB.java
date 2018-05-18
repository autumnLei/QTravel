package com.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;

public class ConnectDB {
	/**
	 * 链接数据库
	 */
	public static Connection openConnect(){
		
		Connection connect = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/qtravel";
			String mysql_name = "root";
			String mysql_password = "970723";
			connect = DriverManager.getConnection(url, mysql_name, mysql_password);
		}catch (Exception e) {
			e.printStackTrace();
		}
		//
		return connect;
	}
	
	public static void closeConn(ResultSet rs, PreparedStatement ps, Connection conn) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
