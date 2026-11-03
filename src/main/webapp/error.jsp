<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>提示 - 班级学生成绩管理系统</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f5f6fa; }
    </style>
</head>
<body>
<div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
    <div class="card text-center border-0 shadow" style="max-width: 480px; border-radius: 12px;">
        <div class="card-body p-5">
            <div style="font-size: 4rem;">🚫</div>
            <h4 class="mb-3">操作失败 / 无权访问</h4>
            <p class="text-muted">
                <c:choose>
                    <c:when test="${not empty msg}">${msg}</c:when>
                    <c:when test="${not empty param.msg}">${param.msg}</c:when>
                    <c:otherwise>您没有权限访问该页面，或发生了错误。</c:otherwise>
                </c:choose>
            </p>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">返回登录</a>
            <a href="javascript:history.back()" class="btn btn-outline-secondary">返回上一页</a>
        </div>
    </div>
</div>
</body>
</html>
