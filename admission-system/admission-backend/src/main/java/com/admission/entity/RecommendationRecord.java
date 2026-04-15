package com.admission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;

@TableName("recommendation_record")
public class RecommendationRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String ksh;

    private Integer userScore;
    private Integer userRank;
    private String userSubjects;

    private String yxdm;
    private String yxmc;
    private String zydm;
    private String zymc;
    private Integer nf;

    private Integer recommendType;
    private Double matchScore;
    private String matchReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getKsh() { return ksh; }
    public void setKsh(String ksh) { this.ksh = ksh; }

    public Integer getUserScore() { return userScore; }
    public void setUserScore(Integer userScore) { this.userScore = userScore; }

    public Integer getUserRank() { return userRank; }
    public void setUserRank(Integer userRank) { this.userRank = userRank; }

    public String getUserSubjects() { return userSubjects; }
    public void setUserSubjects(String userSubjects) { this.userSubjects = userSubjects; }

    public String getYxdm() { return yxdm; }
    public void setYxdm(String yxdm) { this.yxdm = yxdm; }

    public String getYxmc() { return yxmc; }
    public void setYxmc(String yxmc) { this.yxmc = yxmc; }

    public String getZydm() { return zydm; }
    public void setZydm(String zydm) { this.zydm = zydm; }

    public String getZymc() { return zymc; }
    public void setZymc(String zymc) { this.zymc = zymc; }

    public Integer getNf() { return nf; }
    public void setNf(Integer nf) { this.nf = nf; }

    public Integer getRecommendType() { return recommendType; }
    public void setRecommendType(Integer recommendType) { this.recommendType = recommendType; }

    public Double getMatchScore() { return matchScore; }
    public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }

    public String getMatchReason() { return matchReason; }
    public void setMatchReason(String matchReason) { this.matchReason = matchReason; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
