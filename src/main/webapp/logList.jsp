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
    <title>操作日志 - 班级学生成绩管理系统</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f5f6fa; }
        .card { border: none; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,.06); }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
    <div class="container">
        <a class="navbar-brand" href="teacherIndex.jsp">📚 成绩管理系统</a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" href="teacherIndex.jsp">主页</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/scoreQuery">成绩管理</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/scoreStat">统计分析</a></li>
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/logQuery">操作日志</a></li>
            </ul>
            <span class="navbar-text text-white me-3">欢迎，${sessionScope.loginUser.realName}（教师）</span>
            <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/login?action=logout">退出登录</a>
        </div>
    </div>
</nav>

<div class="container">
    <div class="card">
        <div class="card-header bg-white"><strong>🕒 操作日志</strong></div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/logQuery" method="get" class="row g-3 mb-3">
                <div class="col-md-4">
                    <input type="text" name="operator" class="form-control" value="${param.operator}" placeholder="按操作人筛选">
                </div>
                <div class="col-md-4">
                    <select name="operation" class="form-select">
                        <option value="">全部类型</option>
                        <option value="LOGIN" ${param.operation == 'LOGIN' ? 'selected' : ''}>登录</option>
                        <option value="ADD" ${param.operation == 'ADD' ? 'selected' : ''}>新增成绩</option>
                        <option value="MODIFY" ${param.operation == 'MODIFY' ? 'selected' : ''}>修改成绩</option>
                        <option value="DELETE" ${param.operation == 'DELETE' ? 'selected' : ''}>删除成绩</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <button type="submit" class="btn btn-primary">查询</button>
                    <a href="${pageContext.request.contextPath}/logQuery" class="btn btn-outline-secondary">重置</a>
                </div>
            </form>

            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr><th>操作人</th><th>操作类型</th><th>操作内容</th><th>操作时间</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="log" items="${logList}">
                        <tr>
                            <td>${log.operator}</td>
                            <td><span class="badge bg-secondary">${log.operation}</span></td>
                            <td>${log.content}</td>
                            <td><fmt:formatDate value="${log.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty logList}">
                        <tr><td colspan="4" class="text-center text-muted py-4">暂无日志</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
