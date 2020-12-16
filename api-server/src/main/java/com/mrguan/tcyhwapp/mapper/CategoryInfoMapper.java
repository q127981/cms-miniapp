package com.mrguan.tcyhwapp.mapper;

import com.mrguan.tcyhwapp.entity.vo.CommonVO;
import com.mrguan.tcyhwapp.model.CategoryInfo;
import com.mrguan.tcyhwapp.utils.MyBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
//@CacheNamespace(flushInterval = 300000L)//缓存五分钟过期
public interface CategoryInfoMapper extends MyBaseMapper<CategoryInfo> {

    @Select("SELECT id,name FROM category_info where enable=1 order by sort")
    List<CommonVO> getCategory();
}
