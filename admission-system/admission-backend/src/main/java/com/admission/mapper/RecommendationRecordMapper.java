package com.admission.mapper;

import com.admission.entity.RecommendationRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface RecommendationRecordMapper extends BaseMapper<RecommendationRecord> {

    @Select("SELECT * FROM recommendation_record WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{limit}")
    List<RecommendationRecord> selectRecentByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    @Select("SELECT recommend_type, COUNT(*) as count FROM recommendation_record " +
            "WHERE user_id = #{userId} GROUP BY recommend_type")
    List<Map<String, Object>> selectTypeDistribution(@Param("userId") Long userId);
}
