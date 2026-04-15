package com.admission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@TableName("score_segment")
public class ScoreSegment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer fs;           // 分数
    private Integer tfrs;         // 同分人数
    private Integer ljrs;         // 累计人数
    private Integer nf;           // 年份
    private Date createTime;      // 创建时间

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getFs() { return fs; }
    public void setFs(Integer fs) { this.fs = fs; }

    public Integer getTfrs() { return tfrs; }
    public void setTfrs(Integer tfrs) { this.tfrs = tfrs; }

    public Integer getLjrs() { return ljrs; }
    public void setLjrs(Integer ljrs) { this.ljrs = ljrs; }

    public Integer getNf() { return nf; }
    public void setNf(Integer nf) { this.nf = nf; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
