package com.gyf.bookstore.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;

import com.gyf.bookstore.domain.User;
import com.gyf.bookstore.exception.UserException;
import com.gyf.bookstore.service.UserService;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {

	

	public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// 1.获取请求参数
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		UserService us = new UserService();
		try {
			// 2.登录成功
			User user = us.login(username, password);
			String path;
			if ("管理员".equals(user.getRole())) {
				path = "/admin/login/home.jsp";
			} else {// 普通用户
				path = "/index.jsp";
			}
			request.getSession().setAttribute("user", user);
			request.getRequestDispatcher(path).forward(request, response);
		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 3.登录失败转回登录页面
			request.setAttribute("user_msg", e.getMessage());
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}

	}

	
	/*** 注册*/
	public void register(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// 验证码
		String checkcode = request.getParameter("checkcode");
		String checkcode_session = (String) request.getSession().getAttribute("checkcode_session");
		if (!checkcode_session.equals(checkcode)) {
			request.setAttribute("ckcode_msg", "验证码不正确");
			request.getRequestDispatcher("/register.jsp").forward(request, response);
			return;
		}

		// 创建User对象
		User user = new User();
		try {
			// 把参数封装成Bean
			BeanUtils.populate(user, request.getParameterMap());

			// 设置一个激活码
			user.setActiveCode(UUID.randomUUID().toString());
			user.setRole("普通用户");
			// 注册
			UserService us = new UserService();
			us.register(user);

			// 注册成功跳转到成功界面
			request.getRequestDispatcher("/registersuccess.jsp").forward(request, response);
			System.out.println(user);
		} catch (UserException e) {
			e.printStackTrace();
			// 用户注册失败
			request.setAttribute("user_msg", e.getMessage() + ":用户名存在");
			request.getRequestDispatcher("/register.jsp").forward(request, response);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// Bean封装失败
			e.printStackTrace();
		}
	}
	
	/**
	 * 退出
	 */
	public void logout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getSession().invalidate();
		response.sendRedirect(request.getContextPath() + "/index.jsp");
	}
}
