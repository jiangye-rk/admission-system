package com.admission.service;

import com.admission.dto.RecommendationRequest;
import com.admission.dto.RecommendationResult;
import com.admission.entity.RecommendationRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface IRecommendationService extends IService<RecommendationRecord> {

    List<RecommendationResult> generateRecommendations(Long userId, RecommendationRequest request);

    IPage<RecommendationResult> getRecommendationsWithPage(Long userId, RecommendationRequest request);

    List<RecommendationRecord> getUserRecommendationHistory(Long userId, Integer limit);

    void saveRecommendationBatch(Long userId, String ksh, List<RecommendationResult> results);

    Map<String, Object> getRecommendationStatistics(Long userId);

    List<RecommendationResult> getSchoolRecommendations(Long userId, String yxdm, Integer year);

    Map<String, Object> analyzeAdmissionProbability(Long userId, String yxdm, String zymc, Integer year);
}
