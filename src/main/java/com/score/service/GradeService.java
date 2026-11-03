package com.score.service;

import com.score.dao.GradeDao;
import com.score.dao.GradeDaoImpl;
import com.score.entity.Grade;
import com.score.entity.GradeVO;
import com.score.entity.Subject;
import com.score.entity.User;
import com.score.util.ExcelUtil;
import com.score.util.StringUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩业务层：业务校验（0~100、非空、重复拦截）+ 调用 DAO + 记录操作日志
 */
public class GradeService {

    private final GradeDao gradeDao = new GradeDaoImpl();
    private final OperationLogService logService = new OperationLogService();
    private final UserService userService = new UserService();
    private final SubjectService subjectService = new SubjectService();

    /** 新增成绩。返回 null 表示成功，否则返回错误提示信息 */
    public String addGrade(Grade grade, User operator) {
        String error = validate(grade);
        if (error != null) {
            return error;
        }
        // 重复拦截：同一学生同科同学期不允许重复录入
        if (gradeDao.exists(grade.getStudentNo(), grade.getSubjectId(), grade.getSemester())) {
            return "该学生该科目该学期已有成绩，请勿重复录入";
        }
        int rows = gradeDao.insert(grade);
        if (rows > 0) {
            logService.addLog(operator.getUsername(), "ADD",
                    "录入成绩：学号 " + grade.getStudentNo() + "，科目ID " + grade.getSubjectId()
                            + "，分数 " + grade.getScore() + "，学期 " + grade.getSemester());
            return null;
        }
        return "成绩录入失败";
    }

    /** 修改成绩。返回 null 表示成功，否则返回错误提示信息 */
    public String updateGrade(Grade grade, User operator) {
        if (grade == null || grade.getId() == null) {
            return "参数错误";
        }
        if (grade.getScore() == null || grade.getScore() < 0 || grade.getScore() > 100) {
            return "分数必须在 0~100 之间";
        }
        int rows = gradeDao.update(grade);
        if (rows > 0) {
            logService.addLog(operator.getUsername(), "MODIFY",
                    "修改成绩：ID " + grade.getId() + "，分数改为 " + grade.getScore());
            return null;
        }
        return "成绩修改失败";
    }

    /** 删除成绩。返回 null 表示成功，否则返回错误提示信息 */
    public String deleteGrade(int id, User operator) {
        int rows = gradeDao.delete(id);
        if (rows > 0) {
            logService.addLog(operator.getUsername(), "DELETE", "删除成绩：ID " + id);
            return null;
        }
        return "成绩删除失败";
    }

    /** 业务校验：非空 + 分数 0~100 */
    private String validate(Grade grade) {
        if (grade == null) {
            return "参数错误";
        }
        if (StringUtil.isEmpty(grade.getStudentNo())) {
            return "学号不能为空";
        }
        if (grade.getSubjectId() == null) {
            return "请选择科目";
        }
        if (StringUtil.isEmpty(grade.getSemester())) {
            return "请选择学期";
        }
        if (grade.getScore() == null) {
            return "请输入分数";
        }
        if (grade.getScore() < 0 || grade.getScore() > 100) {
            return "分数必须在 0~100 之间";
        }
        return null;
    }

    /** 教师端：多条件筛选查询成绩 */
    public List<GradeVO> queryGrades(String studentNo, String studentName, Integer subjectId,
                                     String semester, String className, Double minScore, Double maxScore) {
        return gradeDao.queryGrades(studentNo, studentName, subjectId, semester, className, minScore, maxScore);
    }

    /** 学生端：查询本人成绩 */
    public List<GradeVO> queryStudentGrades(String studentNo, String semester) {
        return gradeDao.queryByStudentNo(studentNo, semester);
    }

    /** 根据 ID 查询成绩（修改表单回显用） */
    public Grade findById(int id) {
        return gradeDao.findById(id);
    }

    /** 统计：平均分、及格率、优秀率、分数分布 */
    public Map<String, Object> getStatistics(Integer subjectId, String semester) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("average", gradeDao.getAverageScore(subjectId, semester));
        stats.put("passRate", gradeDao.getPassRate(subjectId, semester));
        stats.put("excellentRate", gradeDao.getExcellentRate(subjectId, semester));
        stats.put("distribution", gradeDao.getScoreDistribution(subjectId, semester));
        return stats;
    }

    /** 查询所有学期（筛选下拉框用） */
    public List<String> findDistinctSemesters() {
        return gradeDao.findDistinctSemesters();
    }

    /**
     * 批量导入成绩（Excel）。
     * 逐行解析并校验（学号存在、科目匹配、分数 0~100、重复拦截），
     * 返回结果集：success=成功条数，fail=失败条数，errors=逐条错误提示。
     */
    public Map<String, Object> importGrades(InputStream in, User operator) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        int success = 0;
        int fail = 0;

        try {
            List<List<String>> rows = ExcelUtil.readFirstSheet(in);
            if (rows.size() <= 1) {
                errors.add("文件中没有数据行（请至少填写一行成绩）");
                result.put("success", success);
                result.put("fail", fail);
                result.put("errors", errors);
                return result;
            }

            // 科目名称/编码 -> ID 映射（导入时按名称或编码匹配科目）
            Map<String, Integer> subjectMap = new HashMap<>();
            for (Subject s : subjectService.findAll()) {
                if (StringUtil.isNotEmpty(s.getSubjectName())) {
                    subjectMap.put(s.getSubjectName().trim(), s.getId());
                }
                if (StringUtil.isNotEmpty(s.getSubjectCode())) {
                    subjectMap.put(s.getSubjectCode().trim(), s.getId());
                }
            }

            // 第 0 行为表头，从第 1 行开始解析数据
            for (int i = 1; i < rows.size(); i++) {
                int rowNo = i + 1; // Excel 中的实际行号
                List<String> cells = rows.get(i);
                String studentNo = getCell(cells, 0);
                String subjectName = getCell(cells, 1);
                String semester = getCell(cells, 2);
                String scoreStr = getCell(cells, 3);

                // 跳过整行为空的空白行
                if (StringUtil.isEmpty(studentNo) && StringUtil.isEmpty(subjectName)
                        && StringUtil.isEmpty(semester) && StringUtil.isEmpty(scoreStr)) {
                    continue;
                }
                if (StringUtil.isEmpty(studentNo)) {
                    fail++;
                    errors.add("第" + rowNo + "行：学号不能为空");
                    continue;
                }
                if (userService.findByUsername(studentNo) == null) {
                    fail++;
                    errors.add("第" + rowNo + "行：学号「" + studentNo + "」不存在");
                    continue;
                }
                Integer subjectId = subjectMap.get(subjectName);
                if (subjectId == null) {
                    fail++;
                    errors.add("第" + rowNo + "行：科目「" + subjectName + "」不存在");
                    continue;
                }
                Double score = StringUtil.toDouble(scoreStr);
                if (score == null) {
                    fail++;
                    errors.add("第" + rowNo + "行：分数「" + scoreStr + "」不是数字");
                    continue;
                }

                Grade grade = new Grade();
                grade.setStudentNo(studentNo);
                grade.setSubjectId(subjectId);
                grade.setScore(score);
                grade.setSemester(semester);

                // 复用新增时的业务校验与重复拦截
                String error = validate(grade);
                if (error == null && gradeDao.exists(grade.getStudentNo(), grade.getSubjectId(), grade.getSemester())) {
                    error = "该学生该科目该学期已有成绩";
                }
                if (error != null) {
                    fail++;
                    errors.add("第" + rowNo + "行：" + error);
                    continue;
                }
                if (gradeDao.insert(grade) > 0) {
                    success++;
                } else {
                    fail++;
                    errors.add("第" + rowNo + "行：写入数据库失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.add("读取 Excel 文件失败，请确认上传的是 .xlsx 文件且格式正确");
        }

        result.put("success", success);
        result.put("fail", fail);
        result.put("errors", errors);

        // 记录一条 IMPORT 汇总日志
        logService.addLog(operator.getUsername(), "IMPORT",
                "批量导入成绩：成功 " + success + " 条，失败 " + fail + " 条");
        return result;
    }

    /** 取一行中的第 index 列，越界或为 null 返回空串 */
    private String getCell(List<String> cells, int index) {
        return (cells != null && index < cells.size() && cells.get(index) != null) ? cells.get(index) : "";
    }
}
