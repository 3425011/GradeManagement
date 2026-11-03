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
    <title>统计分析 - 班级学生成绩管理系统</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js"></script>
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
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/scoreStat">统计分析</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/logQuery">操作日志</a></li>
            </ul>
            <span class="navbar-text text-white me-3">欢迎，${sessionScope.loginUser.realName}（教师）</span>
            <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/login?action=logout">退出登录</a>
        </div>
    </div>
</nav>

<div class="container">
    <!-- 筛选 -->
    <div class="card mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/scoreStat" method="get" class="row g-3 align-items-end">
                <div class="col-md-4">
                    <label class="form-label">科目</label>
                    <select name="subjectId" class="form-select">
                        <c:forEach var="s" items="${subjectList}">
                            <option value="${s.id}" ${selectedSubjectId == s.id ? 'selected' : ''}>${s.subjectName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-4">
                    <label class="form-label">学期</label>
                    <select name="semester" class="form-select">
                        <c:forEach var="se" items="${semesterList}">
                            <option value="${se}" ${selectedSemester == se ? 'selected' : ''}>${se}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-4">
                    <button type="submit" class="btn btn-primary">查询统计</button>
                </div>
            </form>
        </div>
    </div>

    <!-- 统计卡片 -->
    <div class="row g-4 mb-4">
        <div class="col-md-4">
            <div class="card text-center">
                <div class="card-body py-4">
                    <div class="text-muted">平均分</div>
                    <div class="display-6 text-primary"><fmt:formatNumber value="${average}" pattern="0.##"/></div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-center">
                <div class="card-body py-4">
                    <div class="text-muted">及格率（≥60）</div>
                    <div class="display-6 text-success"><fmt:formatNumber value="${passRate}" pattern="0.##"/>%</div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-center">
                <div class="card-body py-4">
                    <div class="text-muted">优秀率（≥85）</div>
                    <div class="display-6 text-warning"><fmt:formatNumber value="${excellentRate}" pattern="0.##"/>%</div>
                </div>
            </div>
        </div>
    </div>

    <!-- 图表 -->
    <div class="card">
        <div class="card-header bg-white"><strong>📊 成绩分布柱状图</strong></div>
        <div class="card-body">
            <div id="chart" style="width: 100%; height: 420px;"></div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // 从后端统计结果（distribution Map）中取出分数段与人数，交给 ECharts 渲染
    var categories = [];
    var counts = [];
    <c:forEach var="entry" items="${distribution}">
    categories.push('${entry.key}');
    counts.push(${entry.value});
    </c:forEach>

    var chart = echarts.init(document.getElementById('chart'));
    chart.setOption({
        title: { text: '分数段人数分布', left: 'center' },
        tooltip: { trigger: 'axis' },
        grid: { left: 60, right: 30, top: 60, bottom: 40 },
        xAxis: { type: 'category', data: categories, name: '分数段' },
        yAxis: { type: 'value', name: '人数', minInterval: 1 },
        series: [{
            name: '人数',
            type: 'bar',
            data: counts,
            barWidth: '45%',
            itemStyle: { color: '#4e73df', borderRadius: [6, 6, 0, 0] },
            label: { show: true, position: 'top' }
        }]
    });
    window.addEventListener('resize', function () { chart.resize(); });
</script>
</body>
</html>
