package com.admission.controller;

import com.admission.common.Result;
import com.admission.service.IScoreSegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/score")
public class ScoreSegmentController {
    @Autowired
    private IScoreSegmentService scoreSegmentService;

    @GetMapping("/rank")
    public Result<Map<String, Integer>> getRank(@RequestParam Integer score) {
        Integer rank = scoreSegmentService.getRankByScore(score);
        Map<String, Integer> result = new HashMap<>();
        result.put("rank", rank != null ? rank : 0);
        return Result.success(result);
    }
}