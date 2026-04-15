-- 智能推荐记录表
CREATE TABLE IF NOT EXISTS recommendation_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    ksh VARCHAR(14) COMMENT '考生号',
    user_score INT COMMENT '用户分数',
    user_rank INT COMMENT '用户位次',
    user_subjects VARCHAR(50) COMMENT '用户选科',
    yxdm VARCHAR(20) COMMENT '院校代码',
    yxmc VARCHAR(100) COMMENT '院校名称',
    zydm VARCHAR(20) COMMENT '专业代码',
    zymc VARCHAR(100) COMMENT '专业名称',
    nf INT COMMENT '年份',
    recommend_type INT COMMENT '推荐类型：1-冲，2-稳，3-保',
    match_score DECIMAL(5,2) COMMENT '匹配分数(0-100)',
    match_reason VARCHAR(255) COMMENT '匹配原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_ksh (ksh),
    INDEX idx_yxdm (yxdm),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智能推荐记录表';
