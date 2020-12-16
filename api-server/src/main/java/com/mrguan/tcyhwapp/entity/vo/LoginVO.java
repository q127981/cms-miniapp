package com.mrguan.tcyhwapp.entity.vo;

import lombok.Data;

/**
 * RegisterVO
 *
 * @author MrGuan
 * @date 2020/11/3
 */
@Data
public class LoginVO {
    private Long user_id;
    private String mobile;
    private String nickname;
    private String avatar_url;
    private String token;
}
