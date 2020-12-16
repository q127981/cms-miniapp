package com.mrguan.tcyhwapp.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * IndexVO
 *
 * @author MrGuan
 * @date 2020/11/2
 */
@Data
public class IndexVO {
    private Long id;
    private String title;
    private String photo_url;
    private Integer read;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date create_time;
    private String source;
    private Integer comment_count;
    private LinkVO link;
}
