package com.admission.service;

import com.admission.entity.ScoreSegment;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IScoreSegmentService extends IService<ScoreSegment> {
    Integer getRankByScore(Integer score);
}