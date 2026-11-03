package com.score.dao;

import com.score.entity.User;

import java.util.List;

/**
 * 用户数据访问层接口
 */
public interface UserDao {

    /** 新增用户 */
    int insert(User user);

    /** 根据主键删除 */
    int delete(int id);

    /** 修改用户 */
    int update(User user);

    /** 根据主键查询 */
    User findById(int id);

    /** 根据登录账号查询（登录用） */
    User findByUsername(String username);

    /** 查询所有用户 */
    List<User> findAll();

    /** 根据角色查询用户（如查询所有学生） */
    List<User> findByRole(String role);

    /** 查询所有班级名称（筛选下拉框用） */
    List<String> findDistinctClassNames();
}
