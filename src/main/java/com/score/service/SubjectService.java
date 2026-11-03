package com.score.service;

import com.score.dao.SubjectDao;
import com.score.dao.SubjectDaoImpl;
import com.score.entity.Subject;

import java.util.List;

/**
 * 科目业务层
 */
public class SubjectService {

    private final SubjectDao subjectDao = new SubjectDaoImpl();

    /** 查询所有科目（下拉框用） */
    public List<Subject> findAll() {
        return subjectDao.findAll();
    }

    /** 根据主键查询科目 */
    public Subject findById(int id) {
        return subjectDao.findById(id);
    }
}
