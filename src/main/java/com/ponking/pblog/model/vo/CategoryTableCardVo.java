package com.ponking.pblog.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Ponking
 * @ClassName CategoryTableCardVo
 * @date 2020/4/8--10:38
 **/
@Data
public class CategoryTableCardVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer count;
    private String path;
}
