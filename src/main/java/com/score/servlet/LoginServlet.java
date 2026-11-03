package com.score.servlet;

import com.score.entity.User;
import com.score.service.OperationLogService;
import com.score.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录控制器
 * GET：显示登录页 / 处理退出登录
 * POST：校验账号密码，成功后把用户与角色存入 Session
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final OperationLogService logService = new OperationLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 退出登录：销毁 Session 后回到登录页
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        // 已登录则直接进入成绩查询主界面
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser != null) {
            response.sendRedirect(request.getContextPath() + "/scoreQuery");
            return;
        }
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userService.login(username, password);
        if (user != null) {
            // 登录成功：保存登录用户与角色到 Session
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", user);
            session.setAttribute("role", user.getRole());
            logService.addLog(user.getUsername(), "LOGIN", "登录系统");
            // 按角色跳转到对应主页
            if ("teacher".equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/teacherIndex.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/studentIndex.jsp");
            }
        } else {
            // 登录失败：转发回登录页并提示
            request.setAttribute("errorMsg", "账号或密码错误");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
