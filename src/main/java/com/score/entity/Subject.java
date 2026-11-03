package com.score.entity;

import java.util.Date;

/**
 * 科目实体类，对应数据库表 t_subject
 */
public class Subject {

    private Integer id;          // 主键ID
    private String subjectName;  // 科目名称
    private String subjectCode;  // 科目编码
    private Date createTime;     // 创建时间

    // 无参构造
    public Subject() {
    }

    // 有参构造（业务字段，不含自增主键与创建时间）
    public Subject(String subjectName, String subjectCode) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
