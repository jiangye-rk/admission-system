-- sql/admission.sql
CREATE DATABASE IF NOT EXISTS admission_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE admission_db;

-- 录取数据表（根据图片中的表格字段）
CREATE TABLE admission_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    -- 年份和批次
    nf INT COMMENT '年份',
    pcdm VARCHAR(10) COMMENT '批次代码',
    pcmc VARCHAR(50) COMMENT '批次名称',
    
    -- 院校信息
    yxdm VARCHAR(20) COMMENT '院校代码',
    yxmc VARCHAR(100) COMMENT '院校名称',
    zsyxdm VARCHAR(20) COMMENT '招生院校代码',
    
    -- 选考科目和专业
    xkm VARCHAR(50) COMMENT '选考科目',
    zydm VARCHAR(20) COMMENT '专业代码',
    zymc VARCHAR(100) COMMENT '专业名称',
    
    -- 组分数数据
    zzgf INT COMMENT '组最高分',
    zzdf INT COMMENT '组最低分',
    zpjf DECIMAL(5,2) COMMENT '组平均分',
    zpjwc INT COMMENT '组平均位次',
    
    -- 专业计划与录取
    zyjh INT COMMENT '专业计划',
    zylq INT COMMENT '专业录取',
    
    -- 专业分数数据
    zyzgf INT COMMENT '专业最高分',
    zyzdf INT COMMENT '专业最低分',
    zypjf DECIMAL(5,2) COMMENT '专业平均分',
    
    -- 其他字段
    sf985 TINYINT DEFAULT 0 COMMENT '是否985',
    sf211 TINYINT DEFAULT 0 COMMENT '是否211',
    ssmc VARCHAR(50) COMMENT '省市名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_nf_yxdm (nf, yxdm),
    INDEX idx_zymc (zymc),
    INDEX idx_pcdm (pcdm)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='录取数据表';

-- 用户表
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（密文）',
    role VARCHAR(20) DEFAULT 'guest' COMMENT '角色：admin/guest',
    -- 考生信息字段
    ksxm VARCHAR(50) COMMENT '考生姓名',
    ksh VARCHAR(20) UNIQUE COMMENT '14位考生号',
    sjh VARCHAR(11) COMMENT '手机号',
    fs INT COMMENT '分数',
    wc INT COMMENT '位次',
    xk VARCHAR(20) COMMENT '选考科目（逗号分隔，如：物理,化学,生物）',
    gzxx VARCHAR(100) COMMENT '高中学校',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login_time DATETIME,
    INDEX idx_ksh (ksh),
    INDEX idx_sjh (sjh)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入默认管理员
INSERT INTO sys_user (username, password, role, ksxm) VALUES 
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin', '管理员');
-- 密码是 123456 的MD5值

-- 一分一段表（YFYDB2025）
CREATE TABLE score_segment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fs INT NOT NULL COMMENT '分数',
    tfrs INT DEFAULT 0 COMMENT '同分人数',
    ljrs INT DEFAULT 0 COMMENT '累计人数',
    nf INT DEFAULT 2025 COMMENT '年份',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_fs_nf (fs, nf),
    INDEX idx_fs (fs)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='一分一段表';
