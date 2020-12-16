package com.mrguan.tcyhwapp.entity;

//import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @Auther: MrGuan
 * @Date: 2019/9/26 17:32
 * @Description:
 */
@Data
//@ApiModel("分页数据列表")
@AllArgsConstructor
public class PageInfo<T> {

    private Long count;
    private Integer page;

    private List<T> list;

}
