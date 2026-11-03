package com.score.servlet;

import com.score.entity.GradeVO;
import com.score.entity.User;
import com.score.service.GradeService;
import com.score.service.OperationLogService;
import com.score.service.UserService;
import com.score.util.ExcelUtil;
import com.score.util.StringUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 成绩导出控制器（Excel）
 * 教师：按当前筛选条件导出全班成绩；学生：仅导出本人成绩（学号取自 Session，防止越权）。
 */
@WebServlet("/scoreExport")
public class ScoreExportServlet extends HttpServlet {

    private final GradeService gradeService = new GradeService();
    private final UserService userService = new UserService();
    private final OperationLogService logService = new OperationLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<GradeVO> gradeList;
        if (userService.isTeacher(loginUser)) {
            // 复用成绩查询的同一套筛选参数，保证“看到什么就导出什么”
            gradeList = gradeService.queryGrades(
                    request.getParameter("studentNo"),
                    request.getParameter("studentName"),
                    StringUtil.toInteger(request.getParameter("subjectId")),
                    request.getParameter("semester"),
                    request.getParameter("className"),
                    StringUtil.toDouble(request.getParameter("minScore")),
                    StringUtil.toDouble(request.getParameter("maxScore")));
            logService.addLog(loginUser.getUsername(), "EXPORT",
                    "导出成绩 Excel（" + gradeList.size() + " 条）");
        } else {
            // 学生：学号从 Session 取，只导出本人成绩
            gradeList = gradeService.queryStudentGrades(loginUser.getUsername(), request.getParameter("semester"));
        }

        // 设置响应头，让浏览器以附件形式下载 .xlsx（filename* 解决中文文件名乱码）
        String fileName = "成绩导出_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"grades.xlsx\"; filename*=UTF-8''" + encoded);

        ExcelUtil.writeGradeList(gradeList, response.getOutputStream());
    }
}
