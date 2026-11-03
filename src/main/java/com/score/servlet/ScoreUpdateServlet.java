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
 * 修改成绩控制器（仅教师可访问）
 */
@WebServlet("/scoreUpdate")
public class ScoreUpdateServlet extends HttpServlet {

    private final GradeService gradeService = new GradeService();
    private final UserService userService = new UserService();
    private final SubjectService subjectService = new SubjectService();

    /** GET：跳转到修改成绩表单页（回显原成绩） */
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

        // 编辑表单已整合到 scoreList.jsp 的弹窗中，GET 直接回列表页
        response.sendRedirect(request.getContextPath() + "/scoreQuery");
    }

    /** POST：处理修改成绩 */
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

        Integer id = StringUtil.toInteger(request.getParameter("id"));
        Double score = StringUtil.toDouble(request.getParameter("score"));
        String semester = request.getParameter("semester");

        Grade grade = new Grade();
        grade.setId(id);
        grade.setScore(score);
        grade.setSemester(semester);

        // 业务校验 + 修改（内部自动写操作日志）
        String error = gradeService.updateGrade(grade, loginUser);

        HttpSession session = request.getSession();
        session.setAttribute("msg", error == null ? "成绩修改成功" : error);
        response.sendRedirect(request.getContextPath() + "/scoreQuery");
    }
}
