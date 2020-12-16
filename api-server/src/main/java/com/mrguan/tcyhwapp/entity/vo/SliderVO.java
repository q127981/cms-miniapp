package com.mrguan.tcyhwapp.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * SliderVO
 *
 * @author MrGuan
 * @date 2020/11/2
 */
@Data
public class SliderVO {
    private Long id;
    private String title;
    private String photo_url;
    private int read;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date create_time;
    private String source;

}
