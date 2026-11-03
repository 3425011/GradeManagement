package com.score.entity;

import java.util.Date;

/**
 * 操作日志实体类，对应数据库表 t_operation_log
 */
public class OperationLog {

    private Integer id;         // 主键ID
    private String operator;    // 操作人（登录账号）
    private String operation;   // 操作类型
    private String content;     // 操作内容
    private Date createTime;    // 操作时间

    // 无参构造
    public OperationLog() {
    }

    // 有参构造（业务字段，不含自增主键与操作时间）
    public OperationLog(String operator, String operation, String content) {
        this.operator = operator;
        this.operation = operation;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
