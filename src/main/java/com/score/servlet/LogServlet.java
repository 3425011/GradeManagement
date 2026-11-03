package com.score.servlet;

import com.score.entity.OperationLog;
import com.score.entity.User;
import com.score.service.OperationLogService;
import com.score.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 操作日志查询控制器（仅教师可访问）
 */
@WebServlet("/logQuery")
public class LogServlet extends HttpServlet {

    private final OperationLogService logService = new OperationLogService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!userService.isTeacher(loginUser)) {
            request.setAttribute("msg", "您无权访问该页面");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        // 接收筛选参数
        String operator = request.getParameter("operator");
        String operation = request.getParameter("operation");

        List<OperationLog> logList = logService.queryLogs(operator, operation);
        request.setAttribute("logList", logList);
        request.getRequestDispatcher("/logList.jsp").forward(request, response);
    }
}
