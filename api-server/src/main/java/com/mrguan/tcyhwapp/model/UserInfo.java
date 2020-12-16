package com.mrguan.tcyhwapp.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * UserInfo
 *
 * @author MrGuan
 * @date 2020/11/3
 */
@Data
@Table(name = "user_info")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean enable;
    private String nickname;
    private String avatar_url;
    private String open_id;
    private Integer gender;
    @Transient
    private Date create_time;
    private String password;
    private String phone_number;
}
