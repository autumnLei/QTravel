package com.lei;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.db.ConnectDB;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String name = request.getParameter("name");
		String password = request.getParameter("password");
		
		String temp = null;
		int num = 0;
		try {
			conn = ConnectDB.openConnect();
			String sql = "select * from user where name=? and password=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, password);
			rs = ps.executeQuery();
			if (rs.next()) {
				num=1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectDB.closeConn(rs, ps, conn);
			if(num == 1){
				try {
					conn = ConnectDB.openConnect();
					String sql = "select * from user_info where name=?";
					ps = conn.prepareStatement(sql);
					ps.setString(1, name);
					ResultSet rs = ps.executeQuery();
						if (rs.next()) {
							temp = rs.getString(1);
						}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					ConnectDB.closeConn(rs, ps, conn);
				}
				System.out.println(temp);
				if(temp==null)
					temp = "";
				response.getWriter().write(temp);
			}else{
				response.getWriter().write(num + "");
			}
		}
		
	}

}
