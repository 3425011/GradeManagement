package com.score.dao;

import com.score.entity.Grade;
import com.score.entity.GradeVO;

import java.util.List;
import java.util.Map;

/**
 * 成绩数据访问层接口
 */
public interface GradeDao {

    /** 新增成绩 */
    int insert(Grade grade);

    /** 根据主键删除 */
    int delete(int id);

    /** 修改成绩（分数、学期） */
    int update(Grade grade);

    /** 根据主键查询 */
    Grade findById(int id);

    /** 查询所有成绩 */
    List<Grade> findAll();

    /** 教师端：多条件筛选联表查询成绩（含学生姓名、科目名） */
    List<GradeVO> queryGrades(String studentNo, String studentName, Integer subjectId,
                              String semester, String className, Double minScore, Double maxScore);

    /** 学生端：按学号查询本人成绩 */
    List<GradeVO> queryByStudentNo(String studentNo, String semester);

    /** 判断同一学生同科同学期是否已有成绩（拦截重复录入） */
    boolean exists(String studentNo, Integer subjectId, String semester);

    /** 统计：计算平均分 */
    Double getAverageScore(Integer subjectId, String semester);

    /** 统计：计算及格率（>=60） */
    Double getPassRate(Integer subjectId, String semester);

    /** 统计：计算优秀率（>=85） */
    Double getExcellentRate(Integer subjectId, String semester);

    /** 统计：分数分布（各分数段人数） */
    Map<String, Integer> getScoreDistribution(Integer subjectId, String semester);

    /** 查询所有学期（筛选下拉框用） */
    List<String> findDistinctSemesters();
}
