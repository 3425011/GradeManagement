package com.score.service;

import com.score.dao.OperationLogDao;
import com.score.dao.OperationLogDaoImpl;
import com.score.entity.OperationLog;

import java.util.List;

/**
 * 操作日志业务层：记录日志、查询日志
 */
public class OperationLogService {

    private final OperationLogDao logDao = new OperationLogDaoImpl();

    /** 新增一条操作日志 */
    public void addLog(String operator, String operation, String content) {
        OperationLog log = new OperationLog(operator, operation, content);
        logDao.insert(log);
    }

    /** 按操作人、操作类型筛选日志 */
    public List<OperationLog> queryLogs(String operator, String operation) {
        return logDao.queryLogs(operator, operation);
    }
}
