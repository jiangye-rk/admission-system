package com.admission.controller;

import com.admission.common.Result;
import com.admission.dto.RecommendationRequest;
import com.admission.dto.RecommendationResult;
import com.admission.entity.RecommendationRecord;
import com.admission.entity.User;
import com.admission.service.IRecommendationService;
import com.admission.service.IUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {

    private static final Logger log = LoggerFactory.getLogger(RecommendationController.class);

    @Autowired
    private IRecommendationService recommendationService;

    @Autowired
    private IUserService userService;

    @PostMapping("/generate")
    public Result<List<RecommendationResult>> generateRecommendations(
            @RequestBody RecommendationRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            List<RecommendationResult> results = recommendationService.generateRecommendations(userId, request);
            return Result.success(results);
        } catch (Exception e) {
            log.error("生成推荐失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/page")
    public Result<IPage<RecommendationResult>> getRecommendationsWithPage(
            @RequestBody RecommendationRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            IPage<RecommendationResult> page = recommendationService.getRecommendationsWithPage(userId, request);
            return Result.success(page);
        } catch (Exception e) {
            log.error("分页获取推荐失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/history")
    public Result<List<RecommendationRecord>> getRecommendationHistory(
            @RequestParam(defaultValue = "10") Integer limit,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            List<RecommendationRecord> history = recommendationService.getUserRecommendationHistory(userId, limit);
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取推荐历史失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getRecommendationStatistics(HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            Map<String, Object> statistics = recommendationService.getRecommendationStatistics(userId);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取推荐统计失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/school/{yxdm}")
    public Result<List<RecommendationResult>> getSchoolRecommendations(
            @PathVariable String yxdm,
            @RequestParam(required = false) Integer year,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            List<RecommendationResult> results = recommendationService.getSchoolRecommendations(userId, yxdm, year);
            return Result.success(results);
        } catch (Exception e) {
            log.error("获取院校推荐失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/probability")
    public Result<Map<String, Object>> analyzeAdmissionProbability(
            @RequestParam String yxdm,
            @RequestParam String zymc,
            @RequestParam(required = false) Integer year,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            Map<String, Object> result = recommendationService.analyzeAdmissionProbability(userId, yxdm, zymc, year);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分析录取概率失败", e);
            return Result.error(e.getMessage());
        }
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            throw new RuntimeException("用户未登录");
        }
        User user = userService.getByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user.getId();
    }
}
