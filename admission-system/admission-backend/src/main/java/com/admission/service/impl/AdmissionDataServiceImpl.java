package com.admission.service.impl;

import com.admission.entity.AdmissionData;
import com.admission.entity.User;
import com.admission.mapper.AdmissionDataMapper;
import com.admission.service.IAdmissionDataService;
import com.admission.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.Arrays;

@Service
public class AdmissionDataServiceImpl extends ServiceImpl<AdmissionDataMapper, AdmissionData>
        implements IAdmissionDataService {

    private static final Logger log = LoggerFactory.getLogger(AdmissionDataServiceImpl.class);

    @Autowired
    private AdmissionDataMapper admissionDataMapper;

    @Autowired
    private IUserService userService;  // 新增：用于获取用户个人分数位次

    @Override
    public IPage<AdmissionData> queryPage(Page<AdmissionData> page,
                                          Integer year,
                                          String yxmc,
                                          String zymc,
                                          String pcmc,
                                          Integer minScore,
                                          Integer maxScore) {
        LambdaQueryWrapper<AdmissionData> wrapper = new LambdaQueryWrapper<>();

        if (year != null) {
            wrapper.eq(AdmissionData::getNf, year);
        }
        if (StringUtils.hasText(yxmc)) {
            wrapper.like(AdmissionData::getYxmc, yxmc);
        }
        if (StringUtils.hasText(zymc)) {
            wrapper.like(AdmissionData::getZymc, zymc);
        }
        if (StringUtils.hasText(pcmc)) {
            wrapper.eq(AdmissionData::getPcmc, pcmc);
        }
        if (minScore != null) {
            wrapper.ge(AdmissionData::getZdf, minScore);
        }
        if (maxScore != null) {
            wrapper.le(AdmissionData::getZdf, maxScore);
        }

        wrapper.orderByDesc(AdmissionData::getNf);
        return this.page(page, wrapper);
    }

    @Override
    public List<Map<String, Object>> getSchoolList(Integer year) {
        return admissionDataMapper.selectSchoolList(year);
    }

    @Override
    public List<String> getMajorList(String yxdm, Integer year) {
        return admissionDataMapper.selectMajorList(yxdm, year);
    }

    @Override
    public Map<String, Object> compareSchools(List<String> yxdms, String zymc, Integer year) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        List<String> legendData = new ArrayList<>();
        // 如果指定了年份，只显示该年份前后各2年的数据（共5年），否则显示默认年份范围
        List<Integer> years;
        if (year != null) {
            years = Arrays.asList(year - 2, year - 1, year, year + 1, year + 2);
        } else {
            years = Arrays.asList(2021, 2022, 2023, 2024, 2025);
        }

        for (String yxdm : yxdms) {
            List<Map<String, Object>> trend = admissionDataMapper.selectTrendData(yxdm, zymc);
            if (!trend.isEmpty()) {
                String schoolName = (String) trend.get(0).get("yxmc");
                legendData.add(schoolName);

                Map<String, Object> series = new HashMap<>();
                series.put("name", schoolName);
                series.put("type", "line");

                List<Integer> scores = new ArrayList<>();
                for (Integer y : years) {
                    final int targetYear = y;
                    Optional<Map<String, Object>> data = trend.stream()
                            .filter(m -> targetYear == (Integer) m.get("nf"))
                            .findFirst();
                    scores.add(data.map(m -> (Integer) m.get("zdf")).orElse(null));
                }
                series.put("data", scores);
                seriesData.add(series);
            }
        }

        result.put("years", years);
        result.put("legend", legendData);
        result.put("series", seriesData);
        return result;
    }

    @Override
    public List<Map<String, Object>> getHotMajors(Integer year, Integer limit) {
        return admissionDataMapper.selectHotMajors(year, limit);
    }

    @Override
    public List<Map<String, Object>> getScoreDistribution(Integer year) {
        LambdaQueryWrapper<AdmissionData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdmissionData::getNf, year);
        wrapper.select(AdmissionData::getZdf);

        List<Integer> scores = this.list(wrapper).stream()
                .map(AdmissionData::getZdf)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, Integer> distribution = new TreeMap<>();
        for (int i = 400; i <= 700; i += 50) {
            final int start = i;
            final int end = i + 50;
            long count = scores.stream()
                    .filter(s -> s >= start && s < end)
                    .count();
            distribution.put(start + "-" + end, (int) count);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        distribution.forEach((k, v) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("range", k);
            map.put("count", v);
            result.add(map);
        });

        return result;
    }

    @Override
    public List<Map<String, Object>> getProvinceHeatmap(Integer year) {
        return admissionDataMapper.selectProvinceDistribution(year);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importExcelData(List<AdmissionData> dataList) {
        int batchSize = 1000;
        for (int i = 0; i < dataList.size(); i += batchSize) {
            List<AdmissionData> batch = dataList.subList(i,
                    Math.min(i + batchSize, dataList.size()));
            this.saveBatch(batch);
        }
    }

    @Override
    @Cacheable(value = "statistics", key = "#year", unless = "#result == null")
    public Map<String, Object> getStatistics(Integer year) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 使用单次查询获取所有统计数据，减少数据库连接次数
            List<AdmissionData> dataList = this.baseMapper.selectList(
                new LambdaQueryWrapper<AdmissionData>()
                    .eq(year != null, AdmissionData::getNf, year)
            );

            // 院校数量：去重统计
            long schoolCount = dataList.stream()
                    .map(AdmissionData::getYxdm)
                    .filter(Objects::nonNull)
                    .distinct()
                    .count();
            result.put("schoolCount", schoolCount);

            // 专业数量：去重统计
            long majorCount = dataList.stream()
                    .map(AdmissionData::getZymc)
                    .filter(Objects::nonNull)
                    .distinct()
                    .count();
            result.put("majorCount", majorCount);

            // 录取人数：求和
            long studentCount = dataList.stream()
                    .map(AdmissionData::getLqs)
                    .filter(Objects::nonNull)
                    .mapToLong(Long::valueOf)
                    .sum();
            result.put("studentCount", studentCount);

            // 平均分：计算平均值
            double avgScore = dataList.stream()
                    .map(AdmissionData::getPjf)
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .average()
                    .orElse(0.0);
            result.put("avgScore", String.format("%.2f", avgScore));

        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            result.put("schoolCount", 0);
            result.put("majorCount", 0);
            result.put("studentCount", 0);
            result.put("avgScore", "0.00");
        }

        return result;
    }

    @Override
    public IPage<AdmissionData> queryPageWithUser(Page<AdmissionData> page, Long userId,
                                                    Integer year, String yxmc, String zymc, String pcmc,
                                                    Integer minScore, Integer maxScore,
                                                    Integer minRank, Integer maxRank) {
        // 先查询用户信息
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 查询基础数据
        IPage<AdmissionData> result = queryPage(page, year, yxmc, zymc, pcmc, minScore, maxScore);
        
        // 根据用户选科筛选数据
        if (user.getXk() != null && !user.getXk().trim().isEmpty()) {
            List<AdmissionData> filteredList = filterByUserSubjects(result.getRecords(), user.getXk());
            result.setRecords(filteredList);
            result.setTotal(filteredList.size());
        }

        return result;
    }

    @Override
    public List<AdmissionData> filterByUserSubjects(List<AdmissionData> dataList, String userSubjects) {
        return dataList.stream()
                .filter(data -> {
                    if (data.getKskmyq() == null || data.getKskmyq().trim().isEmpty()) {
                        return true; // 没有选科要求的数据不过滤
                    }
                    return checkSubjectMatch(data.getKskmyq(), userSubjects);
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkSubjectMatch(String requiredSubjects, String userSubjects) {
        if (requiredSubjects == null || requiredSubjects.trim().isEmpty()) {
            return true;
        }
        if (userSubjects == null || userSubjects.trim().isEmpty()) {
            return false;
        }

        // 将选科字符串转换为集合
        Set<String> requiredSet = Arrays.stream(requiredSubjects.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        
        Set<String> userSet = Arrays.stream(userSubjects.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        // 检查用户选科是否包含所有要求的科目
        return userSet.containsAll(requiredSet);
    }

    @Override
    public Map<String, Object> getSchoolMinScore(List<Map<String, Object>> schools) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> minSeries = new ArrayList<>();
        List<Integer> years = Arrays.asList(2021, 2022, 2023, 2024, 2025);
        
        for (Map<String, Object> school : schools) {
            String yxdm = (String) school.get("yxdm");
            String yxmc = (String) school.get("yxmc");
            
            if (yxdm == null || yxdm.trim().isEmpty()) {
                continue;
            }
            
            // 查询该院校所有年份的数据
            List<Map<String, Object>> allData = admissionDataMapper.selectAllYearDataByYxdm(yxdm);
            
            if (allData.isEmpty()) {
                continue;
            }
            
            // 构建院校数据
            Map<String, Object> schoolData = new HashMap<>();
            schoolData.put("name", yxmc);
            schoolData.put("yxdm", yxdm);
            
            // 按年份分组，找出每年最低分的专业
            List<Map<String, Object>> details = new ArrayList<>();
            for (Integer year : years) {
                final int targetYear = year;
                
                // 找出该年份最低分的记录
                Optional<Map<String, Object>> minScoreRecord = allData.stream()
                        .filter(d -> targetYear == (Integer) d.get("nf"))
                        .min((a, b) -> {
                            Integer scoreA = (Integer) a.get("zdf");
                            Integer scoreB = (Integer) b.get("zdf");
                            if (scoreA == null) return 1;
                            if (scoreB == null) return -1;
                            return scoreA.compareTo(scoreB);
                        });
                
                if (minScoreRecord.isPresent()) {
                    Map<String, Object> record = minScoreRecord.get();
                    Map<String, Object> detail = new HashMap<>();
                    detail.put("year", year);
                    detail.put("score", record.get("zdf"));
                    detail.put("major", record.get("zymc"));
                    details.add(detail);
                }
            }
            
            schoolData.put("details", details);
            minSeries.add(schoolData);
        }
        
        result.put("years", years);
        result.put("minSeries", minSeries);
        
        return result;
    }
}
