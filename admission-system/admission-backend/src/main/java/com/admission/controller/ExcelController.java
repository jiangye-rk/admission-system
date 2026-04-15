package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.AdmissionData;
import com.admission.service.IAdmissionDataService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {
    private static final Logger log = LoggerFactory.getLogger(ExcelController.class);
    @Autowired
    private IAdmissionDataService admissionDataService;

    @PostMapping("/import")
    public Result<String> importExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return Result.error("文件为空");
        List<AdmissionData> dataList = new ArrayList<>();

        try {
            EasyExcel.read(file.getInputStream(), ExcelData.class, new ReadListener<ExcelData>() {
                @Override
                public void invoke(ExcelData data, AnalysisContext context) {
                    AdmissionData entity = convertToEntity(data);
                    if (entity != null) dataList.add(entity);
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("Excel解析完成，共{}条数据", dataList.size());
                }
            }).sheet().doRead();

            admissionDataService.importExcelData(dataList);
            return Result.success("导入成功，共" + dataList.size() + "条数据");
        } catch (IOException e) {
            log.error("Excel导入失败", e);
            return Result.error("导入失败：" + e.getMessage());
        }
    }

    private AdmissionData convertToEntity(ExcelData data) {
        AdmissionData entity = new AdmissionData();
        try {
            // 年份和批次
            entity.setNf(parseInteger(data.getNf()));
            entity.setPcmc(data.getPcmc());
            
            // 院校信息
            entity.setYxdm(data.getYxdm());
            entity.setYxmc(data.getYxmc());
            
            // 选考科目和专业
            entity.setKskmyq(data.getKskmyq());
            entity.setZydm(data.getZydm());
            entity.setZymc(data.getZymc());
            
            // 组分数数据
            entity.setZgf(parseInteger(data.getZgf()));
            entity.setZdf(parseInteger(data.getZdf()));
            entity.setPjf(parseBigDecimal(data.getPjf()));
            entity.setZdfwc(parseInteger(data.getZdfwc()));
            
            // 组位次数据
            entity.setZzdwc(parseInteger(data.getZzdwc()));
            entity.setZpjwc(parseInteger(data.getZpjwc()));
            entity.setZzwf(parseInteger(data.getZzwf()));
            entity.setZzwwc(parseInteger(data.getZzwwc()));
            
            // 专业计划与录取
            entity.setZjhs(parseInteger(data.getZjhs()));
            entity.setLqs(parseInteger(data.getLqs()));
            
            // 专业分数数据
            entity.setZyzdf(parseInteger(data.getZyzdf()));
            entity.setZyzdfwc(parseInteger(data.getZyzdfwc()));
            entity.setZyzdwc(parseInteger(data.getZyzdwc()));
            entity.setZypjf(parseBigDecimal(data.getZypjf()));
            entity.setZypjwc(parseInteger(data.getZypjwc()));
            entity.setZyzwf(parseInteger(data.getZyzwf()));
            entity.setZyzwwc(parseInteger(data.getZyzwwc()));
            
            // 其他字段
            entity.setSf985(parseInteger(data.getSf985()));
            entity.setSf211(parseInteger(data.getSf211()));
            entity.setSfbz(data.getSfbz());
            entity.setSsmc(data.getSsmc());
        } catch (Exception e) {
            log.error("数据转换失败", e);
            return null;
        }
        return entity;
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            try {
                return (int) Double.parseDouble(value.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static class ExcelData {
        // 年份和批次
        @ExcelProperty("年份")
        private String nf;
        @ExcelProperty("批次名称")
        private String pcmc;
        
        // 院校信息
        @ExcelProperty("院校代码")
        private String yxdm;
        @ExcelProperty("院校名称")
        private String yxmc;
        
        // 选考科目和专业
        @ExcelProperty("考试科目要求")
        private String kskmyq;
        @ExcelProperty("专业代码")
        private String zydm;
        @ExcelProperty("专业名称")
        private String zymc;
        
        // 组分数数据
        @ExcelProperty("最高分")
        private String zgf;
        @ExcelProperty("最低分")
        private String zdf;
        @ExcelProperty("平均分")
        private String pjf;
        @ExcelProperty("最低分位次")
        private String zdfwc;
        
        // 组位次数据
        @ExcelProperty("组最低位次")
        private String zzdwc;
        @ExcelProperty("组平均位次")
        private String zpjwc;
        @ExcelProperty("组中位分")
        private String zzwf;
        @ExcelProperty("组中位位次")
        private String zzwwc;
        
        // 专业计划与录取
        @ExcelProperty("专业计划")
        private String zjhs;
        @ExcelProperty("专业录取")
        private String lqs;
        
        // 专业分数数据
        @ExcelProperty("专业最低分")
        private String zyzdf;
        @ExcelProperty("专业最低分位次")
        private String zyzdfwc;
        @ExcelProperty("专业最低位次")
        private String zyzdwc;
        @ExcelProperty("专业平均分")
        private String zypjf;
        @ExcelProperty("专业平均位次")
        private String zypjwc;
        @ExcelProperty("专业中位分")
        private String zyzwf;
        @ExcelProperty("专业中位位次")
        private String zyzwwc;
        
        // 其他字段
        @ExcelProperty("是否985")
        private String sf985;
        @ExcelProperty("是否211")
        private String sf211;
        @ExcelProperty("收费标准")
        private String sfbz;
        @ExcelProperty("省市名称")
        private String ssmc;

        // getters and setters
        public String getNf() { return nf; }
        public void setNf(String nf) { this.nf = nf; }
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
        public String getZgf() { return zgf; }
        public void setZgf(String zgf) { this.zgf = zgf; }
        public String getZdf() { return zdf; }
        public void setZdf(String zdf) { this.zdf = zdf; }
        public String getPjf() { return pjf; }
        public void setPjf(String pjf) { this.pjf = pjf; }
        public String getZdfwc() { return zdfwc; }
        public void setZdfwc(String zdfwc) { this.zdfwc = zdfwc; }
        public String getZzdwc() { return zzdwc; }
        public void setZzdwc(String zzdwc) { this.zzdwc = zzdwc; }
        public String getZpjwc() { return zpjwc; }
        public void setZpjwc(String zpjwc) { this.zpjwc = zpjwc; }
        public String getZzwf() { return zzwf; }
        public void setZzwf(String zzwf) { this.zzwf = zzwf; }
        public String getZzwwc() { return zzwwc; }
        public void setZzwwc(String zzwwc) { this.zzwwc = zzwwc; }
        public String getZjhs() { return zjhs; }
        public void setZjhs(String zjhs) { this.zjhs = zjhs; }
        public String getLqs() { return lqs; }
        public void setLqs(String lqs) { this.lqs = lqs; }
        public String getZyzdf() { return zyzdf; }
        public void setZyzdf(String zyzdf) { this.zyzdf = zyzdf; }
        public String getZyzdfwc() { return zyzdfwc; }
        public void setZyzdfwc(String zyzdfwc) { this.zyzdfwc = zyzdfwc; }
        public String getZyzdwc() { return zyzdwc; }
        public void setZyzdwc(String zyzdwc) { this.zyzdwc = zyzdwc; }
        public String getZypjf() { return zypjf; }
        public void setZypjf(String zypjf) { this.zypjf = zypjf; }
        public String getZypjwc() { return zypjwc; }
        public void setZypjwc(String zypjwc) { this.zypjwc = zypjwc; }
        public String getZyzwf() { return zyzwf; }
        public void setZyzwf(String zyzwf) { this.zyzwf = zyzwf; }
        public String getZyzwwc() { return zyzwwc; }
        public void setZyzwwc(String zyzwwc) { this.zyzwwc = zyzwwc; }
        public String getSf985() { return sf985; }
        public void setSf985(String sf985) { this.sf985 = sf985; }
        public String getSf211() { return sf211; }
        public void setSf211(String sf211) { this.sf211 = sf211; }
        public String getSfbz() { return sfbz; }
        public void setSfbz(String sfbz) { this.sfbz = sfbz; }
        public String getSsmc() { return ssmc; }
        public void setSsmc(String ssmc) { this.ssmc = ssmc; }
    }
}