package com.admission.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@TableName("admission_data")
public class AdmissionData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    @ExcelProperty("ID")
    private Long id;
    
    // 年份和批次
    @ExcelProperty("年份")
    private Integer nf;
    @ExcelProperty("批次名称")
    private String pcmc;
    
    // 院校信息
    @ExcelProperty("院校代码")
    private String yxdm;
    @ExcelProperty("院校名称")
    private String yxmc;
    
    // 选考科目和专业
    @ExcelProperty("选考科目")
    private String kskmyq;
    @ExcelProperty("专业代码")
    private String zydm;
    @ExcelProperty("专业名称")
    private String zymc;
    
    // 组分数数据
    @ExcelProperty("组最高分")
    private Integer zgf;
    @ExcelProperty("组最低分")
    private Integer zdf;
    @ExcelProperty("组平均分")
    private BigDecimal pjf;
    @ExcelProperty("最低分位次")
    private Integer zdfwc;
    
    // 专业计划与录取
    @ExcelProperty("专业计划")
    private Integer zjhs;
    @ExcelProperty("专业录取")
    private Integer lqs;
    
    // 专业分数数据
    @ExcelProperty("专业最低分")
    private Integer zyzdf;
    @ExcelProperty("专业最低分位次")
    private Integer zyzdfwc;
    @ExcelProperty("专业最低位次")
    private Integer zyzdwc;
    @ExcelProperty("专业平均分")
    private BigDecimal zypjf;
    @ExcelProperty("专业平均位次")
    private Integer zypjwc;
    @ExcelProperty("专业中位分")
    private Integer zyzwf;
    @ExcelProperty("专业中位位次")
    private Integer zyzwwc;
    
    // 组位次数据
    @ExcelProperty("组最低位次")
    private Integer zzdwc;
    @ExcelProperty("组平均位次")
    private Integer zpjwc;
    @ExcelProperty("组中位分")
    private Integer zzwf;
    @ExcelProperty("组中位位次")
    private Integer zzwwc;
    
    // 其他字段
    @ExcelProperty("是否985")
    private Integer sf985;
    @ExcelProperty("是否211")
    private Integer sf211;
    @ExcelProperty("收费标准")
    private String sfbz;
    @ExcelProperty("省市名称")
    private String ssmc;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    // ========== Getters and Setters ==========
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getNf() { return nf; }
    public void setNf(Integer nf) { this.nf = nf; }
    
    public String getPcmc() { return pcmc; }
    public void setPcmc(String pcmc) { this.pcmc = pcmc; }
    
    public String getYxdm() { return yxdm; }
    public void setYxdm(String yxdm) { this.yxdm = yxdm; }
    
    public String getYxmc() { return yxmc; }
    public void setYxmc(String yxmc) { this.yxmc = yxmc; }
    
    public String getKskmyq() { return kskmyq; }
    public void setKskmyq(String kskmyq) { this.kskmyq = kskmyq; }
    
    public String getZydm() { return zydm; }
    public void setZydm(String zydm) { this.zydm = zydm; }
    
    public String getZymc() { return zymc; }
    public void setZymc(String zymc) { this.zymc = zymc; }
    
    public Integer getZgf() { return zgf; }
    public void setZgf(Integer zgf) { this.zgf = zgf; }
    
    public Integer getZdf() { return zdf; }
    public void setZdf(Integer zdf) { this.zdf = zdf; }
    
    public BigDecimal getPjf() { return pjf; }
    public void setPjf(BigDecimal pjf) { this.pjf = pjf; }
    
    public Integer getZdfwc() { return zdfwc; }
    public void setZdfwc(Integer zdfwc) { this.zdfwc = zdfwc; }
    
    public Integer getZjhs() { return zjhs; }
    public void setZjhs(Integer zjhs) { this.zjhs = zjhs; }
    
    public Integer getLqs() { return lqs; }
    public void setLqs(Integer lqs) { this.lqs = lqs; }
    
    public Integer getZyzdf() { return zyzdf; }
    public void setZyzdf(Integer zyzdf) { this.zyzdf = zyzdf; }
    
    public Integer getZyzdfwc() { return zyzdfwc; }
    public void setZyzdfwc(Integer zyzdfwc) { this.zyzdfwc = zyzdfwc; }
    
    public Integer getZyzdwc() { return zyzdwc; }
    public void setZyzdwc(Integer zyzdwc) { this.zyzdwc = zyzdwc; }
    
    public BigDecimal getZypjf() { return zypjf; }
    public void setZypjf(BigDecimal zypjf) { this.zypjf = zypjf; }
    
    public Integer getZypjwc() { return zypjwc; }
    public void setZypjwc(Integer zypjwc) { this.zypjwc = zypjwc; }
    
    public Integer getZyzwf() { return zyzwf; }
    public void setZyzwf(Integer zyzwf) { this.zyzwf = zyzwf; }
    
    public Integer getZyzwwc() { return zyzwwc; }
    public void setZyzwwc(Integer zyzwwc) { this.zyzwwc = zyzwwc; }
    
    public Integer getZzdwc() { return zzdwc; }
    public void setZzdwc(Integer zzdwc) { this.zzdwc = zzdwc; }
    
    public Integer getZpjwc() { return zpjwc; }
    public void setZpjwc(Integer zpjwc) { this.zpjwc = zpjwc; }
    
    public Integer getZzwf() { return zzwf; }
    public void setZzwf(Integer zzwf) { this.zzwf = zzwf; }
    
    public Integer getZzwwc() { return zzwwc; }
    public void setZzwwc(Integer zzwwc) { this.zzwwc = zzwwc; }
    
    public Integer getSf985() { return sf985; }
    public void setSf985(Integer sf985) { this.sf985 = sf985; }
    
    public Integer getSf211() { return sf211; }
    public void setSf211(Integer sf211) { this.sf211 = sf211; }
    
    public String getSfbz() { return sfbz; }
    public void setSfbz(String sfbz) { this.sfbz = sfbz; }
    
    public String getSsmc() { return ssmc; }
    public void setSsmc(String ssmc) { this.ssmc = ssmc; }
    
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
