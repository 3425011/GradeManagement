<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    // 已登录则直接进入系统
    if (session.getAttribute("loginUser") != null) {
        response.sendRedirect(request.getContextPath() + "/scoreQuery");
        return;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>登录 - 班级学生成绩管理系统</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #4e73df 0%, #36b9cc 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .login-card { width: 100%; max-width: 400px; border: none; border-radius: 14px; }
    </style>
</head>
<body>
<div class="container d-flex justify-content-center">
    <div class="card shadow login-card">
        <div class="card-body p-4 p-md-5">
            <h3 class="text-center mb-1">班级学生成绩管理系统</h3>
            <p class="text-center text-muted mb-4">学生成绩管理 · 统计 · 分析</p>

            <c:if test="${not empty errorMsg}">
                <div class="alert alert-danger py-2" role="alert">${errorMsg}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="mb-3">
                    <label class="form-label">账号（学号 / 工号）</label>
                    <input type="text" name="username" class="form-control" placeholder="请输入学号或工号" required>
                </div>
                <div class="mb-4">
                    <label class="form-label">密码</label>
                    <input type="password" name="password" class="form-control" placeholder="请输入密码" required>
                </div>
                <button type="submit" class="btn btn-primary w-100">登 录</button>
            </form>

            <p class="text-center text-muted small mt-3 mb-0">
                测试账号：教师 T001 ／ 学生 2021001（密码均为 123456）
            </p>
        </div>
    </div>
</div>
</body>
</html>
