package com.score.servlet;

import com.score.entity.User;
import com.score.service.GradeService;
import com.score.service.UserService;
import com.score.util.ExcelUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 成绩批量导入控制器（Excel，仅教师可访问）
 * GET  action=template 下载导入模板；POST 接收上传的 .xlsx 并批量入库。
 */
@WebServlet("/scoreImport")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,   // 超过 1MB 先写入临时文件
        maxFileSize = 5 * 1024 * 1024,     // 单个文件最大 5MB
        maxRequestSize = 6 * 1024 * 1024   // 整个请求最大 6MB
)
public class ScoreImportServlet extends HttpServlet {

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

        // 下载导入模板
        if ("template".equals(request.getParameter("action"))) {
            String fileName = "成绩导入模板.xlsx";
            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"import-template.xlsx\"; filename*=UTF-8''" + encoded);
            ExcelUtil.writeImportTemplate(response.getOutputStream());
            return;
        }

        response.sendRedirect(request.getContextPath() + "/scoreQuery");
    }

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

        String msg;
        Part filePart = request.getPart("file");
        if (filePart == null || filePart.getSize() == 0) {
            msg = "请先选择要上传的 Excel 文件";
        } else {
            try (InputStream in = filePart.getInputStream()) {
                Map<String, Object> result = gradeService.importGrades(in, loginUser);
                int success = (Integer) result.get("success");
                int fail = (Integer) result.get("fail");
                @SuppressWarnings("unchecked")
                List<String> errors = (List<String>) result.get("errors");
                msg = buildResultMsg(success, fail, errors);
            } catch (Exception e) {
                e.printStackTrace();
                msg = "导入失败：" + e.getMessage();
            }
        }

        // 结果提示存入 Session，重定向回列表页后一次性显示
        HttpSession session = request.getSession();
        session.setAttribute("msg", msg);
        response.sendRedirect(request.getContextPath() + "/scoreQuery");
    }

    /** 组装导入结果提示（最多展示前 5 条错误，避免提示过长） */
    private String buildResultMsg(int success, int fail, List<String> errors) {
        if (errors == null || errors.isEmpty()) {
            return "导入完成：成功 " + success + " 条";
        }
        // 一条都没成功且没有失败行，通常是文件整体读取出错，直接展示错误
        if (success == 0 && fail == 0) {
            return String.join("；", errors);
        }
        StringBuilder sb = new StringBuilder("导入完成：成功 ").append(success)
                .append(" 条，失败 ").append(fail).append(" 条");
        int show = Math.min(errors.size(), 5);
        for (int i = 0; i < show; i++) {
            sb.append("；").append(errors.get(i));
        }
        if (errors.size() > show) {
            sb.append("；……等共 ").append(errors.size()).append(" 个问题");
        }
        return sb.toString();
    }
}
