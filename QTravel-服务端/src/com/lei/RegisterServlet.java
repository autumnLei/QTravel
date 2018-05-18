package com.lei;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.db.ConnectDB;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
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
		// TODO Auto-generated method stub
		
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		
		int num = 0;
		try {
			conn = ConnectDB.openConnect();
			String sql= "select * from user where name=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			rs = ps.executeQuery();
			if(rs.next()){
				num = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ConnectDB.closeConn(rs, ps, conn);
		}
		if (num == 1){
			num=2;
			//response.sendRedirect("login.jsp");
			System.out.println(name+"已经被注册了");
			response.getWriter().write(num + "");	
		}
		else {
			try {
				conn = ConnectDB.openConnect();
				String sql = "insert into user(name,password) values(?,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, name);
				ps.setString(2, password);
				num = ps.executeUpdate();
				if (num == 1) {
					System.out.println(name+"注册成功！");
				} else {
					num = 0;
					System.out.println(name+"注册失败！");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ConnectDB.closeConn(rs, ps, conn);
			}
			
			response.getWriter().write(num + "");
		}
		
	}

}
