package com.admission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;

@TableName("sys_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String role;
    private Integer status;
    private Date createTime;
    private Date lastLoginTime;

    // 考生信息字段
    private String ksxm;      // 考生姓名
    private String ksh;       // 14位考生号
    private String sjh;       // 手机号
    private Integer fs;       // 分数
    private Integer wc;       // 位次
    private String xk;        // 选考科目（逗号分隔）
    private String gzxx;      // 高中学校

    // ========== Getters and Setters ==========
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(Date lastLoginTime) { this.lastLoginTime = lastLoginTime; }

    public String getKsxm() { return ksxm; }
    public void setKsxm(String ksxm) { this.ksxm = ksxm; }

    public String getKsh() { return ksh; }
    public void setKsh(String ksh) { this.ksh = ksh; }

    public String getSjh() { return sjh; }
    public void setSjh(String sjh) { this.sjh = sjh; }

    public Integer getFs() { return fs; }
    public void setFs(Integer fs) { this.fs = fs; }

    public Integer getWc() { return wc; }
    public void setWc(Integer wc) { this.wc = wc; }

    public String getXk() { return xk; }
    public void setXk(String xk) { this.xk = xk; }

    public String getGzxx() { return gzxx; }
    public void setGzxx(String gzxx) { this.gzxx = gzxx; }
}
