package com.score.entity;

import java.util.Date;

/**
 * 学生成绩实体类，对应数据库表 t_grade
 */
public class Grade {

    private Integer id;         // 主键ID
    private String studentNo;   // 学号（关联用户表账号）
    private Integer subjectId;  // 科目ID
    private Double score;       // 分数
    private String semester;    // 学期
    private Date createTime;    // 录入时间

    // 无参构造
    public Grade() {
    }

    // 有参构造（业务字段，不含自增主键与录入时间）
    public Grade(String studentNo, Integer subjectId, Double score, String semester) {
        this.studentNo = studentNo;
        this.subjectId = subjectId;
        this.score = score;
        this.semester = semester;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
