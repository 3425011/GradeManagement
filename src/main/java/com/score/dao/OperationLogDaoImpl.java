package com.score.dao;

import com.score.entity.OperationLog;
import com.score.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志数据访问层实现类
 */
public class OperationLogDaoImpl implements OperationLogDao {

    private OperationLog mapRow(ResultSet rs) throws SQLException {
        OperationLog log = new OperationLog();
        log.setId(rs.getInt("id"));
        log.setOperator(rs.getString("operator"));
        log.setOperation(rs.getString("operation"));
        log.setContent(rs.getString("content"));
        log.setCreateTime(rs.getTimestamp("create_time"));
        return log;
    }

    @Override
    public int insert(OperationLog log) {
        String sql = "INSERT INTO t_operation_log(operator, operation, content) VALUES(?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, log.getOperator());
            ps.setString(2, log.getOperation());
            ps.setString(3, log.getContent());
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
        String sql = "DELETE FROM t_operation_log WHERE id = ?";
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
    public OperationLog findById(int id) {
        String sql = "SELECT * FROM t_operation_log WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return null;
    }

    @Override
    public List<OperationLog> findAll() {
        String sql = "SELECT * FROM t_operation_log ORDER BY create_time DESC, id DESC";
        List<OperationLog> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }

    @Override
    public List<OperationLog> queryLogs(String operator, String operation) {
        StringBuilder sql = new StringBuilder("SELECT * FROM t_operation_log WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (operator != null && !operator.isEmpty()) {
            sql.append("AND operator = ? ");
            params.add(operator);
        }
        if (operation != null && !operation.isEmpty()) {
            sql.append("AND operation = ? ");
            params.add(operation);
        }
        sql.append("ORDER BY create_time DESC, id DESC");

        List<OperationLog> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, ps, conn);
        }
        return list;
    }
}
