package com.admission.dto;

public class RecommendationRequest {
    private Integer score;
    private Integer rank;
    private String subjects;
    private Integer year;
    private String pcmc;
    private Integer riskLevel;
    private Integer pageNum;
    private Integer pageSize;

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public String getSubjects() { return subjects; }
    public void setSubjects(String subjects) { this.subjects = subjects; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getPcmc() { return pcmc; }
    public void setPcmc(String pcmc) { this.pcmc = pcmc; }

    public Integer getRiskLevel() { return riskLevel; }
    public void setRiskLevel(Integer riskLevel) { this.riskLevel = riskLevel; }

    public Integer getPageNum() { return pageNum != null ? pageNum : 1; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }

    public Integer getPageSize() { return pageSize != null ? pageSize : 10; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
