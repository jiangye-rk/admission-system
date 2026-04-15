package com.admission.mapper;

import com.admission.entity.ScoreSegment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

public interface ScoreSegmentMapper extends BaseMapper<ScoreSegment> {
    @Select("SELECT cumulative_num FROM score_segment WHERE score = #{score}")
    Integer selectRankByScore(Integer score);
}