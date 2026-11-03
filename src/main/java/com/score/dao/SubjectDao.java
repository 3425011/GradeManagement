package com.score.dao;

import com.score.entity.Subject;

import java.util.List;

/**
 * 科目数据访问层接口
 */
public interface SubjectDao {

    /** 新增科目 */
    int insert(Subject subject);

    /** 根据主键删除 */
    int delete(int id);

    /** 修改科目 */
    int update(Subject subject);

    /** 根据主键查询 */
    Subject findById(int id);

    /** 查询所有科目 */
    List<Subject> findAll();
}
