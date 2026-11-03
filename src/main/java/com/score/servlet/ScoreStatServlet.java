package com.score.servlet;

import com.score.entity.Subject;
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
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 统计分析控制器（仅教师可访问）
 * 计算平均分、及格率、优秀率、分数分布，转发到 scoreStat.jsp 由 ECharts 渲染
 */
@WebServlet("/scoreStat")
public class ScoreStatServlet extends HttpServlet {

    private final GradeService gradeService = new GradeService();
    private final SubjectService subjectService = new SubjectService();
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

        Integer subjectId = StringUtil.toInteger(request.getParameter("subjectId"));
        String semester = request.getParameter("semester");

        List<Subject> subjectList = subjectService.findAll();
        List<String> semesterList = gradeService.findDistinctSemesters();

        // 未选择时默认取第一个科目、最新学期，保证页面首次加载即有统计图
        if (subjectId == null && !subjectList.isEmpty()) {
            subjectId = subjectList.get(0).getId();
        }
        if (StringUtil.isEmpty(semester) && !semesterList.isEmpty()) {
            semester = semesterList.get(0);
        }

        // 计算统计
        if (subjectId != null && StringUtil.isNotEmpty(semester)) {
            Map<String, Object> stats = gradeService.getStatistics(subjectId, semester);
            request.setAttribute("average", stats.get("average"));
            request.setAttribute("passRate", stats.get("passRate"));
            request.setAttribute("excellentRate", stats.get("excellentRate"));
            request.setAttribute("distribution", stats.get("distribution"));
        }

        // 回显当前选中的科目与学期
        request.setAttribute("selectedSubjectId", subjectId);
        request.setAttribute("selectedSemester", semester);
        request.setAttribute("subjectList", subjectList);
        request.setAttribute("semesterList", semesterList);

        request.getRequestDispatcher("/scoreStat.jsp").forward(request, response);
    }
}
