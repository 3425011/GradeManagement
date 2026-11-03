package com.score.dao;

import com.score.entity.OperationLog;

import java.util.List;

/**
 * 操作日志数据访问层接口
 */
public interface OperationLogDao {

    /** 新增日志 */
    int insert(OperationLog log);

    /** 根据主键删除 */
    int delete(int id);

    /** 根据主键查询 */
    OperationLog findById(int id);

    /** 查询所有日志 */
    List<OperationLog> findAll();

    /** 按操作人、操作类型筛选日志 */
    List<OperationLog> queryLogs(String operator, String operation);
}
