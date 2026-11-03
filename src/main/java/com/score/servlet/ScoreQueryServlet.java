package com.score.servlet;

import com.score.entity.GradeVO;
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
import java.util.List;
import java.util.Map;

/**
 * 成绩查询与统计控制器
 * 教师：查询全班成绩（多条件筛选）+ 统计计算
 * 学生：仅查询本人成绩（从 Session 取学号，防止越权查看他人成绩）
 */
@WebServlet("/scoreQuery")
public class ScoreQueryServlet extends HttpServlet {

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

        // 取出一次性提示消息（由增删改操作写入 Session）
        HttpSession session = request.getSession();
        Object msg = session.getAttribute("msg");
        if (msg != null) {
            request.setAttribute("msg", msg);
            session.removeAttribute("msg");
        }

        // 按角色分发
        if (userService.isTeacher(loginUser)) {
            handleTeacher(request, response);
        } else {
            handleStudent(request, response, loginUser);
        }
    }

    /** 教师：查询全班成绩 + 统计 */
    private void handleTeacher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 接收筛选参数
        String studentNo = request.getParameter("studentNo");
        String studentName = request.getParameter("studentName");
        Integer subjectId = StringUtil.toInteger(request.getParameter("subjectId"));
        String semester = request.getParameter("semester");
        String className = request.getParameter("className");
        Double minScore = StringUtil.toDouble(request.getParameter("minScore"));
        Double maxScore = StringUtil.toDouble(request.getParameter("maxScore"));

        // 查询成绩列表
        List<GradeVO> gradeList = gradeService.queryGrades(
                studentNo, studentName, subjectId, semester, className, minScore, maxScore);
        request.setAttribute("gradeList", gradeList);

        // 统计计算：仅当指定了科目和学期时计算
        if (subjectId != null && StringUtil.isNotEmpty(semester)) {
            Map<String, Object> stats = gradeService.getStatistics(subjectId, semester);
            request.setAttribute("average", stats.get("average"));
            request.setAttribute("passRate", stats.get("passRate"));
            request.setAttribute("excellentRate", stats.get("excellentRate"));
            request.setAttribute("distribution", stats.get("distribution"));
        }

        // 筛选下拉框数据 + 新增成绩弹窗所需的学生列表
        request.setAttribute("subjectList", subjectService.findAll());
        request.setAttribute("classList", userService.findDistinctClassNames());
        request.setAttribute("semesterList", gradeService.findDistinctSemesters());
        request.setAttribute("studentList", userService.findStudents());

        request.getRequestDispatcher("/scoreList.jsp").forward(request, response);
    }

    /** 学生：仅查询本人成绩 */
    private void handleStudent(HttpServletRequest request, HttpServletResponse response, User loginUser)
            throws ServletException, IOException {
        // 学号从 Session 中的登录用户获取，而不是前端参数，杜绝越权
        String studentNo = loginUser.getUsername();
        String semester = request.getParameter("semester");

        List<GradeVO> gradeList = gradeService.queryStudentGrades(studentNo, semester);
        request.setAttribute("gradeList", gradeList);
        request.setAttribute("semesterList", gradeService.findDistinctSemesters());

        request.getRequestDispatcher("/myScore.jsp").forward(request, response);
    }
}
