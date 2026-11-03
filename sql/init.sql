-- =====================================================================
--  班级学生成绩管理系统  数据库初始化脚本
--  数据库：grade_management    字符集：utf8mb4
--  说明：可直接在 Navicat 或 MySQL 命令行中一次性运行
--  账号密码均为明文 123456（作业要求）
-- =====================================================================

-- 1. 建库
CREATE DATABASE IF NOT EXISTS grade_management
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE grade_management;

-- 2. 按外键依赖顺序删除旧表（便于重复运行）
DROP TABLE IF EXISTS t_operation_log;
DROP TABLE IF EXISTS t_grade;
DROP TABLE IF EXISTS t_subject;
DROP TABLE IF EXISTS t_user;

-- =====================================================================
-- 3. 建表
-- =====================================================================

-- 3.1 用户表（教师 + 学生）
CREATE TABLE t_user (
  id          INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  username    VARCHAR(50)  NOT NULL                COMMENT '登录账号：学号(学生)/工号(教师)',
  password    VARCHAR(50)  NOT NULL                COMMENT '登录密码（作业要求暂存明文）',
  real_name   VARCHAR(50)  NOT NULL                COMMENT '真实姓名',
  role        VARCHAR(20)  NOT NULL DEFAULT 'student' COMMENT '角色：teacher=教师 student=学生',
  class_name  VARCHAR(50)  DEFAULT NULL            COMMENT '班级（仅学生）',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 3.2 科目表
CREATE TABLE t_subject (
  id           INT         NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  subject_name VARCHAR(50) NOT NULL                COMMENT '科目名称',
  subject_code VARCHAR(30) DEFAULT NULL            COMMENT '科目编码',
  create_time  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_subject_name (subject_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科目表';

-- 3.3 学生成绩表
CREATE TABLE t_grade (
  id          INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  student_no  VARCHAR(50)  NOT NULL                COMMENT '学号（关联用户表账号）',
  subject_id  INT          NOT NULL                COMMENT '科目ID（关联科目表）',
  score       DECIMAL(5,1) NOT NULL                COMMENT '分数（0~100）',
  semester    VARCHAR(20)  NOT NULL                COMMENT '学期（如 2025-2026-1）',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_stu_sub_sem (student_no, subject_id, semester),
  KEY idx_subject (subject_id),
  KEY idx_semester (semester),
  CONSTRAINT fk_grade_student FOREIGN KEY (student_no) REFERENCES t_user (username) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_grade_subject FOREIGN KEY (subject_id) REFERENCES t_subject (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生成绩表';

-- 3.4 操作日志表
-- 说明：日志是历史审计记录，操作人账号删除后仍要保留，故存账号快照、不加外键
CREATE TABLE t_operation_log (
  id          INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  operator    VARCHAR(50)  NOT NULL                COMMENT '操作人（登录账号）',
  operation   VARCHAR(20)  NOT NULL                COMMENT '操作类型：ADD/MODIFY/DELETE/IMPORT/EXPORT/LOGIN',
  content     VARCHAR(500) DEFAULT NULL            COMMENT '操作内容',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (id),
  KEY idx_operator (operator),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- =====================================================================
-- 4. 测试数据
-- =====================================================================

-- 4.1 教师账号（2个，密码 123456）
INSERT INTO t_user (username, password, real_name, role) VALUES
('T001', '123456', '张老师', 'teacher'),
('T002', '123456', '李老师', 'teacher');

-- 4.2 学生账号（5个，密码 123456）
INSERT INTO t_user (username, password, real_name, role, class_name) VALUES
('2021001', '123456', '张三', 'student', '软件2101班'),
('2021002', '123456', '李四', 'student', '软件2101班'),
('2021003', '123456', '王五', 'student', '软件2101班'),
('2021004', '123456', '赵六', 'student', '软件2101班'),
('2021005', '123456', '孙七', 'student', '软件2101班');

-- 4.3 科目（3门）
INSERT INTO t_subject (subject_name, subject_code) VALUES
('Java高级程序设计', 'JAVA'),
('数据库原理',        'DB'),
('计算机网络',        'NET');

-- 4.4 学生成绩（5人 × 3科 × 3学期 = 45条，含低分<60 与优秀>=85，便于演示统计/趋势/预警）
-- 科目ID：1=Java高级程序设计  2=数据库原理  3=计算机网络
-- 张三 2021001
INSERT INTO t_grade (student_no, subject_id, score, semester) VALUES
('2021001', 1, 78, '2024-2025-1'),
('2021001', 2, 65, '2024-2025-1'),
('2021001', 3, 55, '2024-2025-1'),
('2021001', 1, 82, '2024-2025-2'),
('2021001', 2, 70, '2024-2025-2'),
('2021001', 3, 62, '2024-2025-2'),
('2021001', 1, 88, '2025-2026-1'),
('2021001', 2, 75, '2025-2026-1'),
('2021001', 3, 68, '2025-2026-1');

-- 李四 2021002
INSERT INTO t_grade (student_no, subject_id, score, semester) VALUES
('2021002', 1, 92, '2024-2025-1'),
('2021002', 2, 85, '2024-2025-1'),
('2021002', 3, 78, '2024-2025-1'),
('2021002', 1, 90, '2024-2025-2'),
('2021002', 2, 88, '2024-2025-2'),
('2021002', 3, 80, '2024-2025-2'),
('2021002', 1, 95, '2025-2026-1'),
('2021002', 2, 90, '2025-2026-1'),
('2021002', 3, 83, '2025-2026-1');

-- 王五 2021003
INSERT INTO t_grade (student_no, subject_id, score, semester) VALUES
('2021003', 1, 45, '2024-2025-1'),
('2021003', 2, 60, '2024-2025-1'),
('2021003', 3, 70, '2024-2025-1'),
('2021003', 1, 50, '2024-2025-2'),
('2021003', 2, 62, '2024-2025-2'),
('2021003', 3, 72, '2024-2025-2'),
('2021003', 1, 58, '2025-2026-1'),
('2021003', 2, 66, '2025-2026-1'),
('2021003', 3, 75, '2025-2026-1');

-- 赵六 2021004
INSERT INTO t_grade (student_no, subject_id, score, semester) VALUES
('2021004', 1, 88, '2024-2025-1'),
('2021004', 2, 72, '2024-2025-1'),
('2021004', 3, 66, '2024-2025-1'),
('2021004', 1, 90, '2024-2025-2'),
('2021004', 2, 78, '2024-2025-2'),
('2021004', 3, 70, '2024-2025-2'),
('2021004', 1, 92, '2025-2026-1'),
('2021004', 2, 80, '2025-2026-1'),
('2021004', 3, 74, '2025-2026-1');

-- 孙七 2021005
INSERT INTO t_grade (student_no, subject_id, score, semester) VALUES
('2021005', 1, 60, '2024-2025-1'),
('2021005', 2, 55, '2024-2025-1'),
('2021005', 3, 82, '2024-2025-1'),
('2021005', 1, 63, '2024-2025-2'),
('2021005', 2, 58, '2024-2025-2'),
('2021005', 3, 85, '2024-2025-2'),
('2021005', 1, 65, '2025-2026-1'),
('2021005', 2, 60, '2025-2026-1'),
('2021005', 3, 88, '2025-2026-1');

-- 4.5 操作日志示例（可选，便于演示日志列表页）
INSERT INTO t_operation_log (operator, operation, content) VALUES
('T001', 'LOGIN',  '教师登录系统'),
('T001', 'ADD',    '录入成绩：张三 Java高级程序设计 88分'),
('T001', 'MODIFY', '修改成绩：李四 数据库原理 85→90分');
