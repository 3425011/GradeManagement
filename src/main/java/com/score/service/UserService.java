package com.score.service;

import com.score.dao.UserDao;
import com.score.dao.UserDaoImpl;
import com.score.entity.User;
import com.score.util.StringUtil;

import java.util.List;

/**
 * 用户业务层：登录校验、角色判断、学生查询
 */
public class UserService {

    private final UserDao userDao = new UserDaoImpl();

    /** 登录校验：成功返回用户对象，失败返回 null */
    public User login(String username, String password) {
        if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
            return null;
        }
        User user = userDao.findByUsername(username);
        if (user != null && password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    /** 权限判断：是否为教师 */
    public boolean isTeacher(User user) {
        return user != null && "teacher".equals(user.getRole());
    }

    /** 权限判断：是否为学生 */
    public boolean isStudent(User user) {
        return user != null && "student".equals(user.getRole());
    }

    /** 根据账号查询用户（导入时校验学号是否存在） */
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    /** 查询所有学生（录入成绩下拉框用） */
    public List<User> findStudents() {
        return userDao.findByRole("student");
    }

    /** 查询所有班级名称（筛选下拉框用） */
    public List<String> findDistinctClassNames() {
        return userDao.findDistinctClassNames();
    }
}
