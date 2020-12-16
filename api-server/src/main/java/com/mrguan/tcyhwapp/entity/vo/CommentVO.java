package com.mrguan.tcyhwapp.entity.vo;

import com.mrguan.tcyhwapp.entity.PageInfo;
import lombok.Data;

import java.util.Date;

/**
 * CommentVO
 *
 * @author MrGuan
 * @date 2020/11/3
 */
@Data
public class CommentVO {
    private  Long id;
    private  Long pid;
    private String nickname;
    private String avatar_url;
    private String content;
//    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date create_time;
    private Integer reply_count;
    private Integer like_count;
    private Integer is_like;
    private String parent_nickname;
    private PageInfo<CommentVO> reply;

}
