package com.admission.service;

import com.admission.entity.AdmissionData;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface IAdmissionDataService extends IService<AdmissionData> {
    IPage<AdmissionData> queryPage(Page<AdmissionData> page, Integer year, String yxmc, String zymc, String pcmc,
                                    Integer minScore, Integer maxScore);
    List<Map<String, Object>> getSchoolList(Integer year);
    List<String> getMajorList(String yxdm, Integer year);
    Map<String, Object> compareSchools(List<String> yxdms, String zymc, Integer year);
    List<Map<String, Object>> getHotMajors(Integer year, Integer limit);
    List<Map<String, Object>> getScoreDistribution(Integer year);
    List<Map<String, Object>> getProvinceHeatmap(Integer year);
    void importExcelData(List<AdmissionData> dataList);
    Map<String, Object> getStatistics(Integer year);
    Map<String, Object> getSchoolMinScore(List<Map<String, Object>> schools);

    // 新增带用户过滤的查询
    IPage<AdmissionData> queryPageWithUser(Page<AdmissionData> page, Long userId,
                                            Integer year, String yxmc, String zymc, String pcmc,
                                            Integer minScore, Integer maxScore,
                                            Integer minRank, Integer maxRank);

    // 根据用户选科筛选数据
    List<AdmissionData> filterByUserSubjects(List<AdmissionData> dataList, String userSubjects);

    // 检查选科是否符合要求
    boolean checkSubjectMatch(String requiredSubjects, String userSubjects);
}
