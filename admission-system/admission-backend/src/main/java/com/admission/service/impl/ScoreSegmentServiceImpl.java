package com.admission.service.impl;

import com.admission.entity.ScoreSegment;
import com.admission.mapper.ScoreSegmentMapper;
import com.admission.service.IScoreSegmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ScoreSegmentServiceImpl extends ServiceImpl<ScoreSegmentMapper, ScoreSegment>
        implements IScoreSegmentService {
    @Override
    public Integer getRankByScore(Integer score) {
        Integer rank = baseMapper.selectRankByScore(score);
        if (rank == null) {
            ScoreSegment segment = lambdaQuery()
                    .ge(ScoreSegment::getFs, score)
                    .orderByAsc(ScoreSegment::getFs)
                    .last("limit 1")
                    .one();
            if (segment != null) rank = segment.getLjrs();
        }
        return rank;
    }
}
