package com.mrguan.tcyhwapp.entity.vo;

import com.mrguan.tcyhwapp.entity.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * IndexListVO
 *
 * @author MrGuan
 * @date 2020/11/2
 */
@Getter
@Setter
public class IndexListVO extends PageInfo<IndexVO> {
    private List<SliderVO> slider;

    public IndexListVO(Long count, Integer page, List<IndexVO> list, List<SliderVO> slider) {
        super(count, page, list);
        this.slider = slider;
    }
}
