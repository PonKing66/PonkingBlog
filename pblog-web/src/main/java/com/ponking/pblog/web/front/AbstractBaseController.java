package com.ponking.pblog.web.front;

import com.ponking.pblog.common.params.PBlogProperties;
import com.ponking.pblog.model.entity.Link;
import com.ponking.pblog.model.vo.*;
import com.ponking.pblog.service.IArticleService;
import com.ponking.pblog.service.ICategoryService;
import com.ponking.pblog.service.ILinkService;
import com.ponking.pblog.service.ITagService;
import org.springframework.ui.Model;

import java.util.List;

/**
 * @author Ponking
 * @ClassName AbstractBaseController
 * @date 2020/4/8--14:24
 **/
public abstract class AbstractBaseController {

    protected final IArticleService articleService;

    protected final ICategoryService categoryService;

    protected final ITagService tagService;

    protected final ILinkService linkService;

    protected final PBlogProperties config;


    protected static final String TABLE_CARD_INFO_PREFIX = "TableCardInfo";

    public AbstractBaseController(IArticleService articleService,
                                  ICategoryService categoryService,
                                  ITagService tagService,
                                  ILinkService linkService,
                                  PBlogProperties config) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.linkService = linkService;
        this.config = config;
    }


    /**
     * 左右侧栏信息
     *
     * @param model
     */
    protected void getBlogTableCardInfo(Model model) {
        List<CategoryTableCardVO> categoryTableCardVOList = categoryService.listCategoryColumnInfo();
        List<TagTableCardVO> tagTableCardVOList = tagService.listTagColumnInfo();
        List<ArchiveTableCartVO> archiveTableCartVOList = articleService.listArchiveColumnInfo();
        List<ArticleTopTableCardVO> articleTopTableCardVOList = articleService.listArticleTopColumn();
        List<Link> links = linkService.list();

        String name = config.getBlogAuthor();
        String city = config.getAuthorCity();
        String title = config.getBlogTitle();
        String avatar = config.getBlogAvatar();
        /**
         * 统计信息
         */
        int articleCount = articleService.count();
        int tagCount = tagService.count();
        int cateCount = categoryService.count();
        AuthorInfoTableCardVO person = new AuthorInfoTableCardVO(name, city, avatar, title, articleCount, tagCount, cateCount);

        model.addAttribute("info", person);
        model.addAttribute("categories", categoryTableCardVOList);
        model.addAttribute("tags", tagTableCardVOList);
        model.addAttribute("archives", archiveTableCartVOList);
        model.addAttribute("newArticles", articleTopTableCardVOList);
        model.addAttribute("links", links);
    }
}
