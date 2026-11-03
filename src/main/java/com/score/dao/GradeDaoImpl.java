package com.score.dao;

import com.score.entity.Grade;
import com.score.entity.GradeVO;
import com.score.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩数据访问层实现类
 * 包含基础 CRUD 与统计方法（平均分、及格率、优秀率、分数分布）
 */
public class GradeDaoImpl implements GradeDao {

    /** 将联表查询结果映射为 GradeVO */
    private GradeVO mapVO(ResultSet rs) throws SQLException {
        GradeVO vo = new GradeVO();
        vo.setId(rs.getInt("id"));
        vo.setStudentNo(rs.getString("student_no"));
        vo.setStudentName(rs.getString("real_name"));
        vo.setClassName(rs.getString("class_name"));
        vo.setSubjectId(rs.getInt("subject_id"));
        vo.setSubjectName(rs.getString("subject_name"));
        vo.setScore(rs.getDouble("score"));
        vo.setSemester(rs.getString("semester"));
        vo.setCreateTime(rs.getTimestamp("create_time"));
        return vo;
    }

    /** 将成绩表结果映射为 Grade */
    private Grade mapGrade(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setId(rs.getInt("id"));
        grade.setStudentNo(rs.getString("student_no"));
        grade.setSubjectId(rs.getInt("subject_id"));
        grade.setScore(rs.getDouble("score"));
        grade.setSemester(rs.getString("semester"));
        grade.setCreateTime(rs.getTimestamp("create_time"));
        return grade;
    }

    @Override
    public int insert(Grade grade) {
        String sql = "INSERT INTO t_grade(student_no, subject_id, score, semester) VALUES(?,?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, grade.getStudentNo());
            ps.setInt(2, grade.getSubjectId());
            ps.setDouble(3, grade.getScore());
            ps.setString(4, grade.getSemester());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(ps, conn);
        }
        return 0;
    }

    @Override
    public int delete(int id) {
        String sql = "DELETE FROM t_grade WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(ps, conn);
        }
        return 0;
    }

    @Override
    public int update(Grade grade) {
        String sql = "UPDATE t_grade SET score=?, semester=? WHERE id=?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDouble(1, grade.getScore());
            ps.setString(2, grade.getSemester());
            ps.setInt(3, grade.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(ps, conn);
        }
        return 0;
    }

    @Override
    public Grade findById(int id) {
        String sql = "SELECT * FROM t_grade WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapGrade(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return null;
    }

    @Override
    public List<Grade> findAll() {
        String sql = "SELECT * FROM t_grade ORDER BY id";
        List<Grade> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapGrade(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    @Override
    public List<GradeVO> queryGrades(String studentNo, String studentName, Integer subjectId,
                                     String semester, String className, Double minScore, Double maxScore) {
        // 动态拼接 SQL（WHERE 1=1 模式），根据传入条件追加筛选
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT g.id, g.student_no, u.real_name, u.class_name, ")
           .append("g.subject_id, s.subject_name, g.score, g.semester, g.create_time ")
           .append("FROM t_grade g ")
           .append("LEFT JOIN t_user u ON g.student_no = u.username ")
           .append("LEFT JOIN t_subject s ON g.subject_id = s.id ")
           .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (studentNo != null && !studentNo.isEmpty()) {
            sql.append("AND g.student_no = ? ");
            params.add(studentNo);
        }
        if (studentName != null && !studentName.isEmpty()) {
            sql.append("AND u.real_name LIKE ? ");
            params.add("%" + studentName + "%");
        }
        if (subjectId != null) {
            sql.append("AND g.subject_id = ? ");
            params.add(subjectId);
        }
        if (semester != null && !semester.isEmpty()) {
            sql.append("AND g.semester = ? ");
            params.add(semester);
        }
        if (className != null && !className.isEmpty()) {
            sql.append("AND u.class_name = ? ");
            params.add(className);
        }
        if (minScore != null) {
            sql.append("AND g.score >= ? ");
            params.add(minScore);
        }
        if (maxScore != null) {
            sql.append("AND g.score <= ? ");
            params.add(maxScore);
        }
        sql.append("ORDER BY g.semester DESC, g.student_no, g.subject_id");
        return queryGradeVO(sql.toString(), params);
    }

    @Override
    public List<GradeVO> queryByStudentNo(String studentNo, String semester) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT g.id, g.student_no, u.real_name, u.class_name, ")
           .append("g.subject_id, s.subject_name, g.score, g.semester, g.create_time ")
           .append("FROM t_grade g ")
           .append("LEFT JOIN t_user u ON g.student_no = u.username ")
           .append("LEFT JOIN t_subject s ON g.subject_id = s.id ")
           .append("WHERE g.student_no = ? ");
        List<Object> params = new ArrayList<>();
        params.add(studentNo);
        if (semester != null && !semester.isEmpty()) {
            sql.append("AND g.semester = ? ");
            params.add(semester);
        }
        sql.append("ORDER BY g.semester DESC, g.subject_id");
        return queryGradeVO(sql.toString(), params);
    }

    /** 执行联表查询并返回 GradeVO 列表 */
    private List<GradeVO> queryGradeVO(String sql, List<Object> params) {
        List<GradeVO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapVO(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    @Override
    public boolean exists(String studentNo, Integer subjectId, String semester) {
        String sql = "SELECT COUNT(*) FROM t_grade WHERE student_no = ? AND subject_id = ? AND semester = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, studentNo);
            ps.setInt(2, subjectId);
            ps.setString(3, semester);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return false;
    }

    @Override
    public Double getAverageScore(Integer subjectId, String semester) {
        return getSingleDouble(
                "SELECT ROUND(AVG(score), 2) FROM t_grade WHERE subject_id = ? AND semester = ?",
                subjectId, semester);
    }

    @Override
    public Double getPassRate(Integer subjectId, String semester) {
        return getSingleDouble(
                "SELECT ROUND(SUM(CASE WHEN score >= 60 THEN 1 ELSE 0 END) / COUNT(*) * 100, 2) "
                        + "FROM t_grade WHERE subject_id = ? AND semester = ?",
                subjectId, semester);
    }

    @Override
    public Double getExcellentRate(Integer subjectId, String semester) {
        return getSingleDouble(
                "SELECT ROUND(SUM(CASE WHEN score >= 85 THEN 1 ELSE 0 END) / COUNT(*) * 100, 2) "
                        + "FROM t_grade WHERE subject_id = ? AND semester = ?",
                subjectId, semester);
    }

    @Override
    public Map<String, Integer> getScoreDistribution(Integer subjectId, String semester) {
        // 固定四个分数段，保持顺序
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("0-59", 0);
        map.put("60-74", 0);
        map.put("75-84", 0);
        map.put("85-100", 0);

        String sql = "SELECT CASE WHEN score < 60 THEN '0-59' "
                + "WHEN score < 75 THEN '60-74' "
                + "WHEN score < 85 THEN '75-84' ELSE '85-100' END AS score_range, "
                + "COUNT(*) AS cnt FROM t_grade WHERE subject_id = ? AND semester = ? GROUP BY score_range";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, subjectId);
            ps.setString(2, semester);
            rs = ps.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("score_range"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return map;
    }

    @Override
    public List<String> findDistinctSemesters() {
        String sql = "SELECT DISTINCT semester FROM t_grade ORDER BY semester DESC";
        List<String> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    /** 执行返回单个数值的统计 SQL，结果为空时返回 0.0 */
    private Double getSingleDouble(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                double value = rs.getDouble(1);
                return rs.wasNull() ? 0.0 : value;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return 0.0;
    }
}
