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
    <title>成绩管理 - 班级学生成绩管理系统</title>
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
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/scoreQuery">成绩管理</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/scoreStat">统计分析</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/logQuery">操作日志</a></li>
            </ul>
            <span class="navbar-text text-white me-3">欢迎，${sessionScope.loginUser.realName}（教师）</span>
            <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/login?action=logout">退出登录</a>
        </div>
    </div>
</nav>

<div class="container">
    <c:if test="${not empty msg}">
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            ${msg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- 筛选表单 -->
    <div class="card mb-4">
        <div class="card-header bg-white"><strong>🔍 筛选查询</strong></div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/scoreQuery" method="get" class="row g-3">
                <div class="col-md-3">
                    <label class="form-label">学号</label>
                    <input type="text" name="studentNo" class="form-control" value="${param.studentNo}" placeholder="精确学号">
                </div>
                <div class="col-md-3">
                    <label class="form-label">姓名</label>
                    <input type="text" name="studentName" class="form-control" value="${param.studentName}" placeholder="模糊搜索姓名">
                </div>
                <div class="col-md-2">
                    <label class="form-label">班级</label>
                    <select name="className" class="form-select">
                        <option value="">全部</option>
                        <c:forEach var="c" items="${classList}">
                            <option value="${c}" ${param.className == c ? 'selected' : ''}>${c}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">科目</label>
                    <select name="subjectId" class="form-select">
                        <option value="">全部</option>
                        <c:forEach var="s" items="${subjectList}">
                            <option value="${s.id}" ${param.subjectId == s.id ? 'selected' : ''}>${s.subjectName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">学期</label>
                    <select name="semester" class="form-select">
                        <option value="">全部</option>
                        <c:forEach var="se" items="${semesterList}">
                            <option value="${se}" ${param.semester == se ? 'selected' : ''}>${se}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">最低分</label>
                    <input type="number" name="minScore" class="form-control" value="${param.minScore}" min="0" max="100" placeholder="0">
                </div>
                <div class="col-md-2">
                    <label class="form-label">最高分</label>
                    <input type="number" name="maxScore" class="form-control" value="${param.maxScore}" min="0" max="100" placeholder="100">
                </div>
                <div class="col-md-2 d-flex align-items-end gap-2">
                    <button type="submit" class="btn btn-primary">查询</button>
                    <a href="${pageContext.request.contextPath}/scoreQuery" class="btn btn-outline-secondary">重置</a>
                </div>
            </form>
        </div>
    </div>

    <!-- 成绩表格 -->
    <div class="card">
        <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <strong>📋 成绩列表</strong>
            <div class="d-flex gap-2">
                <button type="button" class="btn btn-success btn-sm" data-bs-toggle="modal" data-bs-target="#importModal">📥 导入 Excel</button>
                <button type="button" class="btn btn-outline-primary btn-sm" onclick="exportExcel()">📤 导出 Excel</button>
                <button type="button" class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#addModal">＋ 新增成绩</button>
            </div>
        </div>
        <div class="card-body table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>学号</th><th>姓名</th><th>班级</th><th>科目</th><th>学期</th><th>分数</th><th>录入时间</th><th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="vo" items="${gradeList}">
                        <tr>
                            <td>${vo.studentNo}</td>
                            <td>${vo.studentName}</td>
                            <td>${vo.className}</td>
                            <td>${vo.subjectName}</td>
                            <td>${vo.semester}</td>
                            <td class="${vo.score < 60 ? 'text-danger fw-bold' : ''}">
                                <fmt:formatNumber value="${vo.score}" pattern="0.#"/>
                            </td>
                            <td><fmt:formatDate value="${vo.createTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td>
                                <button type="button" class="btn btn-sm btn-outline-warning edit-btn"
                                        data-id="${vo.id}" data-score="${vo.score}" data-semester="${vo.semester}"
                                        data-student="${vo.studentName}" data-studentno="${vo.studentNo}"
                                        data-subject="${vo.subjectName}"
                                        data-bs-toggle="modal" data-bs-target="#editModal">编辑</button>
                                <a href="${pageContext.request.contextPath}/scoreDelete?id=${vo.id}"
                                   class="btn btn-sm btn-outline-danger"
                                   onclick="return confirm('确定删除「${vo.studentName} - ${vo.subjectName} - ${vo.score}分」这条成绩吗？');">删除</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty gradeList}">
                        <tr><td colspan="8" class="text-center text-muted py-4">暂无成绩记录</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- 新增成绩弹窗 -->
<div class="modal fade" id="addModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/scoreAdd" method="post">
                <div class="modal-header">
                    <h5 class="modal-title">新增成绩</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">学生 <span class="text-danger">*</span></label>
                        <select name="studentNo" class="form-select" required>
                            <option value="">请选择学生</option>
                            <c:forEach var="stu" items="${studentList}">
                                <option value="${stu.username}">${stu.realName}（${stu.username}）</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">科目 <span class="text-danger">*</span></label>
                        <select name="subjectId" class="form-select" required>
                            <option value="">请选择科目</option>
                            <c:forEach var="s" items="${subjectList}">
                                <option value="${s.id}">${s.subjectName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">学期 <span class="text-danger">*</span></label>
                        <input type="text" name="semester" class="form-control" list="semesterOptions" placeholder="如 2025-2026-1" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">分数（0~100）<span class="text-danger">*</span></label>
                        <input type="number" name="score" class="form-control" min="0" max="100" step="0.5" placeholder="请输入分数" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                    <button type="submit" class="btn btn-primary">保存</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- 编辑成绩弹窗 -->
<div class="modal fade" id="editModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/scoreUpdate" method="post">
                <input type="hidden" name="id" id="editId">
                <div class="modal-header">
                    <h5 class="modal-title">修改成绩</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">学生 / 科目</label>
                        <input type="text" class="form-control" id="editInfo" disabled>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">学期 <span class="text-danger">*</span></label>
                        <input type="text" name="semester" id="editSemester" class="form-control" list="semesterOptions" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">分数（0~100）<span class="text-danger">*</span></label>
                        <input type="number" name="score" id="editScore" class="form-control" min="0" max="100" step="0.5" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                    <button type="submit" class="btn btn-primary">保存修改</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- 批量导入成绩弹窗 -->
<div class="modal fade" id="importModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/scoreImport" method="post" enctype="multipart/form-data">
                <div class="modal-header">
                    <h5 class="modal-title">批量导入成绩（Excel）</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">选择文件（.xlsx）</label>
                        <input type="file" name="file" class="form-control" accept=".xlsx" required>
                    </div>
                    <div class="alert alert-light border small mb-0">
                        表格列顺序须为：<strong>学号、科目名称、学期、分数</strong>（第一行为表头）。
                        可先 <a href="${pageContext.request.contextPath}/scoreImport?action=template">下载模板</a> 按格式填写。
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                    <button type="submit" class="btn btn-success">开始导入</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- 学期候选（供新增/编辑输入框联想） -->
<datalist id="semesterOptions">
    <c:forEach var="se" items="${semesterList}">
        <option value="${se}"></option>
    </c:forEach>
</datalist>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // 编辑弹窗回显：点击编辑按钮时，把该行数据填入弹窗
    document.querySelectorAll('.edit-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            document.getElementById('editId').value = this.dataset.id;
            document.getElementById('editScore').value = this.dataset.score;
            document.getElementById('editSemester').value = this.dataset.semester;
            document.getElementById('editInfo').value = this.dataset.student + '（' + this.dataset.studentno + '） / ' + this.dataset.subject;
        });
    });

    // 导出 Excel：把当前筛选条件拼到导出链接上，做到“看到什么导出什么”
    function exportExcel() {
        var params = new URLSearchParams();
        ['studentNo', 'studentName', 'className', 'subjectId', 'semester', 'minScore', 'maxScore'].forEach(function (name) {
            var el = document.querySelector('[name="' + name + '"]');
            if (el && el.value) {
                params.append(name, el.value);
            }
        });
        var qs = params.toString();
        location.href = '${pageContext.request.contextPath}/scoreExport' + (qs ? '?' + qs : '');
    }
</script>
</body>
</html>
