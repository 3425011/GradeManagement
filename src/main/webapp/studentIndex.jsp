<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.score.entity.User" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
    <title>学生主页 - 班级学生成绩管理系统</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f5f6fa; }
        .nav-card { border: none; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,.06); transition: transform .15s; }
        .nav-card:hover { transform: translateY(-4px); }
        .nav-card .icon { font-size: 2.5rem; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
    <div class="container">
        <a class="navbar-brand" href="studentIndex.jsp">📚 成绩管理系统</a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link active" href="studentIndex.jsp">主页</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/scoreQuery">我的成绩</a></li>
            </ul>
            <span class="navbar-text text-white me-3">欢迎，${sessionScope.loginUser.realName}（学生）</span>
            <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/login?action=logout">退出登录</a>
        </div>
    </div>
</nav>

<div class="container">
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body">
            <h4 class="mb-1">你好，${sessionScope.loginUser.realName} 同学 👋</h4>
            <p class="text-muted mb-0">学号：${sessionScope.loginUser.username}，班级：${sessionScope.loginUser.className}</p>
        </div>
    </div>

    <div class="row g-4">
        <div class="col-md-6">
            <a href="${pageContext.request.contextPath}/scoreQuery" class="text-decoration-none">
                <div class="card nav-card h-100">
                    <div class="card-body text-center p-5">
                        <div class="icon mb-2">📋</div>
                        <h5 class="text-primary">我的成绩</h5>
                        <p class="text-muted mb-0">查看本人各科成绩，不及格科目自动标红提醒</p>
                    </div>
                </div>
            </a>
        </div>
        <div class="col-md-6">
            <div class="card nav-card h-100">
                <div class="card-body text-center p-5">
                    <div class="icon mb-2">🔒</div>
                    <h5 class="text-secondary">隐私保护</h5>
                    <p class="text-muted mb-0">系统严格隔离数据，您只能查看本人成绩，无法查看他人信息</p>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
