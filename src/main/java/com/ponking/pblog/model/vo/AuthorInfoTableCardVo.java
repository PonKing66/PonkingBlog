package com.ponking.pblog.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Ponking
 * @ClassName AuthorInfoTableCardVo
 * @date 2020/4/8--16:41
 * @Des 博客个人信息
 **/
@Data
public class AuthorInfoTableCardVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String city;
    private String avatar;
    private String title;
    private Integer articleCount;
    private Integer tagCount;
    private Integer categoryCount;

    public AuthorInfoTableCardVo(String name, String city, String avatar, String title, Integer articleCount, Integer tagCount, Integer categoryCount) {
        this.name = name;
        this.city = city;
        this.avatar = avatar;
        this.title = title;
        this.articleCount = articleCount;
        this.tagCount = tagCount;
        this.categoryCount = categoryCount;
    }
}
