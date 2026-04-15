package com.admission.service.impl;

import com.admission.dto.RecommendationRequest;
import com.admission.dto.RecommendationResult;
import com.admission.entity.AdmissionData;
import com.admission.entity.RecommendationRecord;
import com.admission.entity.User;
import com.admission.mapper.AdmissionDataMapper;
import com.admission.mapper.RecommendationRecordMapper;
import com.admission.service.IAdmissionDataService;
import com.admission.service.IRecommendationService;
import com.admission.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl extends ServiceImpl<RecommendationRecordMapper, RecommendationRecord>
        implements IRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IAdmissionDataService admissionDataService;

    @Autowired
    private AdmissionDataMapper admissionDataMapper;

    @Autowired
    private RecommendationRecordMapper recommendationRecordMapper;

    private static final int TYPE_CHONG = 1;
    private static final int TYPE_WEN = 2;
    private static final int TYPE_BAO = 3;

    @Override
    public List<RecommendationResult> generateRecommendations(Long userId, RecommendationRequest request) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Integer userScore = request.getScore() != null ? request.getScore() : user.getFs();
        Integer userRank = request.getRank() != null ? request.getRank() : user.getWc();
        String userSubjects = request.getSubjects() != null ? request.getSubjects() : user.getXk();
        Integer year = request.getYear() != null ? request.getYear() : 2024;

        if (userScore == null || userRank == null) {
            throw new RuntimeException("请完善您的分数和位次信息");
        }

        LambdaQueryWrapper<AdmissionData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdmissionData::getNf, year);

        if (StringUtils.hasText(request.getPcmc())) {
            wrapper.eq(AdmissionData::getPcmc, request.getPcmc());
        }

        List<AdmissionData> allData = admissionDataMapper.selectList(wrapper);

        if (userSubjects != null && !userSubjects.trim().isEmpty()) {
            allData = admissionDataService.filterByUserSubjects(allData, userSubjects);
        }

        List<RecommendationResult> results = new ArrayList<>();

        for (AdmissionData data : allData) {
            RecommendationResult result = calculateMatch(data, userScore, userRank, year);
            if (result != null && result.getMatchScore() > 0) {
                results.add(result);
            }
        }

        results.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));

        saveRecommendationBatch(userId, user.getKsh(), results.subList(0, Math.min(50, results.size())));

        return results;
    }

    private RecommendationResult calculateMatch(AdmissionData data, Integer userScore, Integer userRank, Integer year) {
        Integer minScore = data.getZyzdf() != null ? data.getZyzdf() : data.getZdf();
        Integer minRank = data.getZyzdfwc() != null ? data.getZyzdfwc() : data.getZdfwc();

        if (minScore == null || minRank == null) {
            return null;
        }

        int scoreDiff = userScore - minScore;
        int rankDiff = minRank - userRank;

        int recommendType;
        double matchScore;
        String matchReason;

        if (scoreDiff >= 20 || rankDiff >= 5000) {
            recommendType = TYPE_BAO;
            matchScore = Math.min(100, 60 + (scoreDiff / 5.0) + (rankDiff / 1000.0));
            matchReason = "您的分数和位次优势明显，录取概率较高";
        } else if (scoreDiff >= 0 || rankDiff >= 0) {
            recommendType = TYPE_WEN;
            matchScore = Math.min(85, 50 + (scoreDiff / 3.0) + (rankDiff / 500.0));
            matchReason = "您的分数和位次与往年录取数据匹配，有一定录取机会";
        } else if (scoreDiff >= -15 || rankDiff >= -3000) {
            recommendType = TYPE_CHONG;
            matchScore = Math.max(30, 50 + scoreDiff * 1.5 + rankDiff / 200.0);
            matchReason = "您的分数和位次略低于往年数据，可作为冲刺志愿";
        } else {
            return null;
        }

        RecommendationResult result = new RecommendationResult();
        result.setYxdm(data.getYxdm());
        result.setYxmc(data.getYxmc());
        result.setZydm(data.getZydm());
        result.setZymc(data.getZymc());
        result.setNf(data.getNf());
        result.setPcmc(data.getPcmc());
        result.setZdf(data.getZdf());
        result.setZdfwc(data.getZdfwc());
        result.setZyzdf(data.getZyzdf());
        result.setZyzdfwc(data.getZyzdfwc());
        result.setZjhs(data.getZjhs());
        result.setLqs(data.getLqs());
        result.setSf985(data.getSf985());
        result.setSf211(data.getSf211());
        result.setSsmc(data.getSsmc());
        result.setRecommendType(recommendType);
        result.setMatchScore(Math.round(matchScore * 100.0) / 100.0);
        result.setMatchReason(matchReason);
        result.setScoreDiff(scoreDiff);
        result.setRankDiff(rankDiff);

        return result;
    }

    @Override
    public IPage<RecommendationResult> getRecommendationsWithPage(Long userId, RecommendationRequest request) {
        List<RecommendationResult> allResults = generateRecommendations(userId, request);

        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        int total = allResults.size();

        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<RecommendationResult> pageList = fromIndex < total ?
                allResults.subList(fromIndex, toIndex) : new ArrayList<>();

        IPage<RecommendationResult> page = new Page<>(pageNum, pageSize, total);
        page.setRecords(pageList);

        return page;
    }

    @Override
    public List<RecommendationRecord> getUserRecommendationHistory(Long userId, Integer limit) {
        return recommendationRecordMapper.selectRecentByUserId(userId, limit != null ? limit : 10);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRecommendationBatch(Long userId, String ksh, List<RecommendationResult> results) {
        if (results == null || results.isEmpty()) {
            return;
        }

        List<RecommendationRecord> records = results.stream().map(result -> {
            RecommendationRecord record = new RecommendationRecord();
            record.setUserId(userId);
            record.setKsh(ksh);
            record.setYxdm(result.getYxdm());
            record.setYxmc(result.getYxmc());
            record.setZydm(result.getZydm());
            record.setZymc(result.getZymc());
            record.setNf(result.getNf());
            record.setRecommendType(result.getRecommendType());
            record.setMatchScore(result.getMatchScore());
            record.setMatchReason(result.getMatchReason());
            record.setCreateTime(new Date());
            return record;
        }).collect(Collectors.toList());

        this.saveBatch(records);
    }

    @Override
    public Map<String, Object> getRecommendationStatistics(Long userId) {
        Map<String, Object> result = new HashMap<>();

        List<Map<String, Object>> typeDistribution = recommendationRecordMapper.selectTypeDistribution(userId);
        result.put("typeDistribution", typeDistribution);

        long totalCount = this.count(new LambdaQueryWrapper<RecommendationRecord>().eq(RecommendationRecord::getUserId, userId));
        result.put("totalCount", totalCount);

        return result;
    }

    @Override
    public List<RecommendationResult> getSchoolRecommendations(Long userId, String yxdm, Integer year) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Integer userScore = user.getFs();
        Integer userRank = user.getWc();

        if (userScore == null || userRank == null) {
            throw new RuntimeException("请完善您的分数和位次信息");
        }

        LambdaQueryWrapper<AdmissionData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdmissionData::getYxdm, yxdm);
        if (year != null) {
            wrapper.eq(AdmissionData::getNf, year);
        }

        List<AdmissionData> schoolData = admissionDataMapper.selectList(wrapper);

        String userSubjects = user.getXk();
        if (userSubjects != null && !userSubjects.trim().isEmpty()) {
            schoolData = admissionDataService.filterByUserSubjects(schoolData, userSubjects);
        }

        List<RecommendationResult> results = new ArrayList<>();
        for (AdmissionData data : schoolData) {
            RecommendationResult result = calculateMatch(data, userScore, userRank, data.getNf());
            if (result != null) {
                results.add(result);
            }
        }

        results.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        return results;
    }

    @Override
    public Map<String, Object> analyzeAdmissionProbability(Long userId, String yxdm, String zymc, Integer year) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Integer userScore = user.getFs();
        Integer userRank = user.getWc();

        if (userScore == null || userRank == null) {
            throw new RuntimeException("请完善您的分数和位次信息");
        }

        LambdaQueryWrapper<AdmissionData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdmissionData::getYxdm, yxdm);
        wrapper.eq(AdmissionData::getZymc, zymc);
        if (year != null) {
            wrapper.eq(AdmissionData::getNf, year);
        }

        List<AdmissionData> historyData = admissionDataMapper.selectList(wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("userScore", userScore);
        result.put("userRank", userRank);
        result.put("historyData", historyData);

        if (!historyData.isEmpty()) {
            AdmissionData latest = historyData.get(0);
            Integer minScore = latest.getZyzdf() != null ? latest.getZyzdf() : latest.getZdf();
            Integer minRank = latest.getZyzdfwc() != null ? latest.getZyzdfwc() : latest.getZdfwc();

            int scoreDiff = userScore - minScore;
            int rankDiff = minRank - userRank;

            double probability;
            String suggestion;

            if (scoreDiff >= 20 || rankDiff >= 5000) {
                probability = 0.85;
                suggestion = "录取概率很高，建议作为保底志愿";
            } else if (scoreDiff >= 5 || rankDiff >= 1000) {
                probability = 0.65;
                suggestion = "录取概率较高，建议作为稳妥志愿";
            } else if (scoreDiff >= -10 || rankDiff >= -2000) {
                probability = 0.45;
                suggestion = "有一定录取机会，建议作为冲刺志愿";
            } else {
                probability = 0.20;
                suggestion = "录取概率较低，建议谨慎填报";
            }

            result.put("probability", probability);
            result.put("suggestion", suggestion);
            result.put("scoreDiff", scoreDiff);
            result.put("rankDiff", rankDiff);
        }

        return result;
    }
}
