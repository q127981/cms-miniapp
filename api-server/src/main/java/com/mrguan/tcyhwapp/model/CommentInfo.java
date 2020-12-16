package com.mrguan.tcyhwapp.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * CommontInfo
 *
 * @author MrGuan
 * @date 2020/11/3
 */
@Table(name = "comment_info")
@Data
public class CommentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long user_id;
    private String content;
    @Transient
    private Date create_time;
    private Integer reply_count;
    private Integer like_count;

    private Long pid;
    private Long c_id;
}
