package com.mrguan.tcyhwapp.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * ContentInfo
 *
 * @author MrGuan
 * @date 2020/11/2
 */
@Data
@Table(name = "content_info")
public class ContentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String photo_url;
    private int read_count;
    @Transient
    private Date create_time;
    private String source;
    private Integer comment_count;
    private boolean enable;
    private boolean slider;
    private Integer sort;
    private Long category_id;
    private String content;
    private String des;
    private String source_url;
    private Integer like_count;

}
