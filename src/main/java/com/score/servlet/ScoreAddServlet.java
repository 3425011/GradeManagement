package com.score.servlet;

import com.score.entity.Grade;
import com.score.entity.User;
import com.score.service.GradeService;
import com.score.service.SubjectService;
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
 * 新增成绩控制器（仅教师可访问）
 */
@WebServlet("/scoreAdd")
public class ScoreAddServlet extends HttpServlet {

    private final GradeService gradeService = new GradeService();
    private final UserService userService = new UserService();
    private final SubjectService subjectService = new SubjectService();

    /** GET：跳转到录入成绩表单页 */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        // 权限拦截：未登录
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        // 权限拦截：学生无权访问
        if (!userService.isTeacher(loginUser)) {
            request.setAttribute("msg", "您无权访问该页面");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        // 新增表单已整合到 scoreList.jsp 的弹窗中，GET 直接回列表页
        response.sendRedirect(request.getContextPath() + "/scoreQuery");
    }

    /** POST：处理新增成绩 */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

        // 接收并封装参数
        String studentNo = request.getParameter("studentNo");
        Integer subjectId = StringUtil.toInteger(request.getParameter("subjectId"));
        Double score = StringUtil.toDouble(request.getParameter("score"));
        String semester = request.getParameter("semester");
        Grade grade = new Grade(studentNo, subjectId, score, semester);

        // 业务校验 + 新增（内部自动写操作日志）
        String error = gradeService.addGrade(grade, loginUser);

        // 结果提示：存 Session 作为一次性消息，重定向后仍能显示
        HttpSession session = request.getSession();
        session.setAttribute("msg", error == null ? "成绩录入成功" : error);
        response.sendRedirect(request.getContextPath() + "/scoreQuery");
    }
}
