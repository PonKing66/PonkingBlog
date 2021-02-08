package com.ponking.pblog.web.front;

import com.ponking.pblog.common.params.PBlogProperties;
import com.ponking.pblog.model.dto.ArticleDTO;
import com.ponking.pblog.model.entity.Article;
import com.ponking.pblog.service.IArticleService;
import com.ponking.pblog.service.ICategoryService;
import com.ponking.pblog.service.ILinkService;
import com.ponking.pblog.service.ITagService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Ponking
 * @ClassName ArticleDetailControllerAbstract
 * @date 2020/4/8--14:58
 **/
@Controller
public class ArticleDetailController extends AbstractBaseController {


    public ArticleDetailController(IArticleService articleService, ICategoryService categoryService, ITagService tagService, ILinkService linkService, PBlogProperties config) {
        super(articleService, categoryService, tagService, linkService, config);
    }

    @RequestMapping("/articles/{id}")
    public String content(Model model, @PathVariable("id") Long id) {
        ArticleDTO article = articleService.getArticleInfoById(id);
        Article articleUpdateVisit = new Article();

        /*更新阅读数*/
        BeanUtils.copyProperties(article, articleUpdateVisit);
        articleUpdateVisit.setVisits(articleUpdateVisit.getVisits() + 1);
        articleService.updateById(articleUpdateVisit);

        model.addAttribute("article", article);
        getBlogTableCardInfo(model);
        return "blog/detail/front_article_detail";
    }
}
