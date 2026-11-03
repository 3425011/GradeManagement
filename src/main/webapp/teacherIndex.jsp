<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.score.entity.User" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    if (!"teacher".equals(loginUser.getRole())) {
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
    <title>教师主页 - 班级学生成绩管理系统</title>
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
        <a class="navbar-brand" href="teacherIndex.jsp">📚 成绩管理系统</a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link active" href="teacherIndex.jsp">主页</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/scoreQuery">成绩管理</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/scoreStat">统计分析</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/logQuery">操作日志</a></li>
            </ul>
            <span class="navbar-text text-white me-3">欢迎，${sessionScope.loginUser.realName}（教师）</span>
            <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/login?action=logout">退出登录</a>
        </div>
    </div>
</nav>

<div class="container">
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body">
            <h4 class="mb-1">你好，${sessionScope.loginUser.realName} 老师 👋</h4>
            <p class="text-muted mb-0">欢迎使用班级学生成绩管理系统，请选择下方功能开始操作。</p>
        </div>
    </div>

    <div class="row g-4">
        <div class="col-md-4">
            <a href="${pageContext.request.contextPath}/scoreQuery" class="text-decoration-none">
                <div class="card nav-card h-100">
                    <div class="card-body text-center p-4">
                        <div class="icon mb-2">📝</div>
                        <h5 class="text-primary">成绩管理</h5>
                        <p class="text-muted mb-0">录入、修改、删除成绩，支持筛选与搜索</p>
                    </div>
                </div>
            </a>
        </div>
        <div class="col-md-4">
            <a href="${pageContext.request.contextPath}/scoreStat" class="text-decoration-none">
                <div class="card nav-card h-100">
                    <div class="card-body text-center p-4">
                        <div class="icon mb-2">📊</div>
                        <h5 class="text-primary">统计分析</h5>
                        <p class="text-muted mb-0">平均分、及格率、优秀率与成绩分布图</p>
                    </div>
                </div>
            </a>
        </div>
        <div class="col-md-4">
            <a href="${pageContext.request.contextPath}/logQuery" class="text-decoration-none">
                <div class="card nav-card h-100">
                    <div class="card-body text-center p-4">
                        <div class="icon mb-2">🕒</div>
                        <h5 class="text-primary">操作日志</h5>
                        <p class="text-muted mb-0">查看所有成绩操作记录，方便追溯</p>
                    </div>
                </div>
            </a>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
