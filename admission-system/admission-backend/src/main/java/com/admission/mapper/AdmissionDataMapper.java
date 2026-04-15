package com.admission.mapper;

import com.admission.entity.AdmissionData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface AdmissionDataMapper extends BaseMapper<AdmissionData> {

    @Select("SELECT DISTINCT yxdm, yxmc FROM admission_data WHERE nf = #{year} ORDER BY yxdm")
    List<Map<String, Object>> selectSchoolList(@Param("year") Integer year);

    @Select("SELECT DISTINCT zymc FROM admission_data WHERE yxdm = #{yxdm} AND nf = #{year}")
    List<String> selectMajorList(@Param("yxdm") String yxdm, @Param("year") Integer year);

    @Select("SELECT nf, yxmc, zdf, zdfwc, pjf, lqs FROM admission_data " +
            "WHERE yxdm = #{yxdm} AND zymc = #{zymc} ORDER BY nf")
    List<Map<String, Object>> selectTrendData(@Param("yxdm") String yxdm,
                                               @Param("zymc") String zymc);

    @Select("SELECT zymc, SUM(lqs) as total_lqs, AVG(zypjf) as avg_pjf " +
            "FROM admission_data WHERE nf = #{year} GROUP BY zymc " +
            "ORDER BY total_lqs DESC LIMIT #{limit}")
    List<Map<String, Object>> selectHotMajors(@Param("year") Integer year,
                                               @Param("limit") Integer limit);

    @Select("SELECT ssmc, SUM(lqs) as total FROM admission_data " +
            "WHERE nf = #{year} GROUP BY ssmc")
    List<Map<String, Object>> selectProvinceDistribution(@Param("year") Integer year);

    // 查询某院校所有年份的数据（用于最低分极值计算）
    @Select("SELECT nf, yxmc, zymc, zdf FROM admission_data " +
            "WHERE TRIM(yxdm) = TRIM(#{yxdm}) AND nf BETWEEN 2021 AND 2025 " +
            "ORDER BY nf, zdf")
    List<Map<String, Object>> selectAllYearDataByYxdm(@Param("yxdm") String yxdm);
}