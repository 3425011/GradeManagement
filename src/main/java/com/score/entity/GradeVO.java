package com.score.entity;

import java.util.Date;

/**
 * 成绩视图对象（VO）
 * 说明：不对应数据库表，用于成绩联表查询结果的展示，
 * 在成绩表基础上额外包含学生姓名、班级、科目名称等冗余展示字段，
 * 方便 JSP 页面直接显示，无需在页面再做关联。
 */
public class GradeVO {

    private Integer id;
    private String studentNo;    // 学号
    private String studentName;  // 学生姓名（来自用户表）
    private String className;    // 班级（来自用户表）
    private Integer subjectId;   // 科目ID
    private String subjectName;  // 科目名称（来自科目表）
    private Double score;        // 分数
    private String semester;     // 学期
    private Date createTime;     // 录入时间

    public GradeVO() {
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
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
