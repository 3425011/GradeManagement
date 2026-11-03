<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.score.entity.User" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    if (!"student".equals(loginUser.getRole())) {
        request.setAttribute("msg", "您无权访问该页面");
        request.getRequestDispatcher("/error.jsp").forward(request, response);
        return;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>我的成绩 - 班级学生成绩管理系统</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f5f6fa; }
        .card { border: none; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,.06); }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
    <div class="container">
        <a class="navbar-brand" href="studentIndex.jsp">📚 成绩管理系统</a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" href="studentIndex.jsp">主页</a></li>
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/scoreQuery">我的成绩</a></li>
            </ul>
            <span class="navbar-text text-white me-3">欢迎，${sessionScope.loginUser.realName}（学生）</span>
            <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/login?action=logout">退出登录</a>
        </div>
    </div>
</nav>

<div class="container">
    <div class="card">
        <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <strong>📋 我的成绩</strong>
            <div class="d-flex gap-2 align-items-center">
                <form action="${pageContext.request.contextPath}/scoreQuery" method="get" class="d-flex gap-2">
                    <select name="semester" class="form-select form-select-sm">
                        <option value="">全部学期</option>
                        <c:forEach var="se" items="${semesterList}">
                            <option value="${se}" ${param.semester == se ? 'selected' : ''}>${se}</option>
                        </c:forEach>
                    </select>
                    <button type="submit" class="btn btn-sm btn-primary">筛选</button>
                </form>
                <a href="${pageContext.request.contextPath}/scoreExport" class="btn btn-sm btn-outline-primary">📤 导出 Excel</a>
            </div>
        </div>
        <div class="card-body table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr><th>科目</th><th>学期</th><th>分数</th><th>录入时间</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="vo" items="${gradeList}">
                        <tr>
                            <td>${vo.subjectName}</td>
                            <td>${vo.semester}</td>
                            <td class="${vo.score < 60 ? 'text-danger fw-bold' : 'text-success'}">
                                <fmt:formatNumber value="${vo.score}" pattern="0.#"/>
                                <c:if test="${vo.score < 60}"> ⚠️ 不及格</c:if>
                            </td>
                            <td><fmt:formatDate value="${vo.createTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty gradeList}">
                        <tr><td colspan="4" class="text-center text-muted py-4">暂无成绩记录</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
