package com.score.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类，对应数据库表 t_user（教师 + 学生）
 * 实现 Serializable 是因为登录后需要把 User 对象存入 HttpSession
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;        // 主键ID
    private String username;   // 登录账号：学号(学生)/工号(教师)
    private String password;   // 登录密码
    private String realName;   // 真实姓名
    private String role;       // 角色：teacher=教师 student=学生
    private String className;  // 班级（仅学生）
    private Date createTime;   // 创建时间

    // 无参构造
    public User() {
    }

    // 有参构造（业务字段，不含自增主键与创建时间）
    public User(String username, String password, String realName, String role, String className) {
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.role = role;
        this.className = className;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
