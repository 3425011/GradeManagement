package com.score.servlet;

import com.score.entity.User;
import com.score.service.GradeService;
import com.score.service.UserService;
import com.score.util.StringUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 删除成绩控制器（仅教师可访问）
 * 说明：此处用 GET 处理删除（配合前端 confirm 确认框）；更规范的做法是用 POST 提交删除
 */
@WebServlet("/scoreDelete")
public class ScoreDeleteServlet extends HttpServlet {

    private final GradeService gradeService = new GradeService();
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

        Integer id = StringUtil.toInteger(request.getParameter("id"));
        String error = id == null ? "参数错误" : gradeService.deleteGrade(id, loginUser);

        HttpSession session = request.getSession();
        session.setAttribute("msg", error == null ? "成绩删除成功" : error);
        response.sendRedirect(request.getContextPath() + "/scoreQuery");
    }
}
