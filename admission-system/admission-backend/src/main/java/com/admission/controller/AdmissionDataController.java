package com.admission.controller;

import com.admission.common.PageResult;
import com.admission.common.Result;
import com.admission.entity.AdmissionData;
import com.admission.service.IAdmissionDataService;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class AdmissionDataController {
    @Autowired
    private IAdmissionDataService admissionDataService;
    @Autowired
    private HttpServletRequest request;

    @GetMapping("/query")
    public Result<PageResult<AdmissionData>> query(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String yxmc,
            @RequestParam(required = false) String zymc,
            @RequestParam(required = false) String pcmc,
            @RequestParam(required = false) Integer minScore,
            @RequestParam(required = false) Integer maxScore) {
        Page<AdmissionData> page = new Page<>(pageNum, pageSize);
        IPage<AdmissionData> result = admissionDataService.queryPage(page, year, yxmc, zymc, pcmc, minScore, maxScore);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords(), result.getCurrent(), result.getSize()));
    }

    @GetMapping("/queryWithUser")
    public Result<PageResult<AdmissionData>> queryWithUser(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String yxmc,
            @RequestParam(required = false) String zymc,
            @RequestParam(required = false) String pcmc,
            @RequestParam(required = false) Integer minScore,
            @RequestParam(required = false) Integer maxScore,
            @RequestParam(required = false) Integer minRank,
            @RequestParam(required = false) Integer maxRank) {
        Page<AdmissionData> page = new Page<>(pageNum, pageSize);
        IPage<AdmissionData> result = admissionDataService.queryPageWithUser(
                page, userId, year, yxmc, zymc, pcmc, minScore, maxScore, minRank, maxRank);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords(), result.getCurrent(), result.getSize()));
    }

    @PostMapping("/export")
    public void export(@RequestBody List<Long> ids, HttpServletResponse response) throws IOException {
        String username = (String) request.getAttribute("username");
        List<AdmissionData> list = admissionDataService.listByIds(ids);
        String fileName = username + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xls";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        EasyExcel.write(response.getOutputStream(), AdmissionData.class).sheet("数据").doWrite(list);
        // 保存到服务器目录
        String savePath = "D:/upload/export/";
        File dir = new File(savePath);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(savePath + fileName);
        EasyExcel.write(file, AdmissionData.class).sheet("数据").doWrite(list);
    }

    @GetMapping("/schools")
    public Result<List<Map<String, Object>>> getSchools(@RequestParam Integer year) {
        return Result.success(admissionDataService.getSchoolList(year));
    }

    @GetMapping("/majors")
    public Result<List<String>> getMajors(@RequestParam String yxdm, @RequestParam Integer year) {
        return Result.success(admissionDataService.getMajorList(yxdm, year));
    }

    @PostMapping("/compare")
    public Result<Map<String, Object>> compare(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<String> yxdms = (List<String>) params.get("yxdms");
        String zymc = (String) params.get("zymc");
        Integer year = (Integer) params.get("year");
        return Result.success(admissionDataService.compareSchools(yxdms, zymc, year));
    }

    @GetMapping("/hot")
    public Result<List<Map<String, Object>>> getHotMajors(@RequestParam Integer year, @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(admissionDataService.getHotMajors(year, limit));
    }

    @GetMapping("/distribution")
    public Result<List<Map<String, Object>>> getScoreDistribution(@RequestParam Integer year) {
        return Result.success(admissionDataService.getScoreDistribution(year));
    }

    @GetMapping("/province")
    public Result<List<Map<String, Object>>> getProvinceHeatmap(@RequestParam Integer year) {
        return Result.success(admissionDataService.getProvinceHeatmap(year));
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(@RequestParam(required = false) Integer year) {
        return Result.success(admissionDataService.getStatistics(year));
    }

    @PostMapping("/schoolMinScore")
    public Result<Map<String, Object>> getSchoolMinScore(@RequestBody List<Map<String, Object>> schools) {
        return Result.success(admissionDataService.getSchoolMinScore(schools));
    }
}