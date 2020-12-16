package com.mrguan.tcyhwapp.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * CategoryInfo
 *
 * @author MrGuan
 * @date 2020/11/2
 */
@Data
@Table(name = "category_info")
public class CategoryInfo {
//    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean enable;
    private Integer sort;
    @Transient
    private Date create_time;
}
