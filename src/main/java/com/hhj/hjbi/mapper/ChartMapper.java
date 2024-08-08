package com.hhj.hjbi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhj.hjbi.model.entity.Chart;


import java.util.List;
import java.util.Map;

/**
* @author 13360
* @description 针对表【chart(图表信息表)】的数据库操作Mapper
* @createDate 2024-07-14 10:50:24
*/
public interface ChartMapper extends BaseMapper<Chart> {


    List<Map<String,Object>> queryChartData(String querySql);
}




