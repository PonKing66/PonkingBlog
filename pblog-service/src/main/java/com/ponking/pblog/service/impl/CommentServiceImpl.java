package com.ponking.pblog.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ponking.pblog.common.constants.AuthConstants;
import com.ponking.pblog.dao.CommentMapper;
import com.ponking.pblog.model.dto.CommentDTO;
import com.ponking.pblog.model.entity.Comment;
import com.ponking.pblog.model.vo.ArticleCommentsVO;
import com.ponking.pblog.service.ICommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author peng
 * @since 2020-10-04
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {


    @Override
    public List<ArticleCommentsVO> getCommentByArticleId(Long id) {
        List<ArticleCommentsVO> articleCommentsVOS = baseMapper.selectList(new QueryWrapper<Comment>().eq("article_id", id))
                .stream()
                .map(item -> {
                    ArticleCommentsVO vo = new ArticleCommentsVO();
                    BeanUtils.copyProperties(item, vo);
                    return vo;
                }).collect(Collectors.toList());
        return getTreeCommentsOfParent(articleCommentsVOS);
    }

    @Override
    public void addArticleComment(CommentDTO commentDto, HttpServletRequest request) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDto, comment);
        String ip = request.getRemoteHost();
        String userAgent = request.getHeader("User-Agent");
        comment.setIp(ip);
        comment.setUserAgent(userAgent);
        comment.setStatus(0);
        comment.setEmailMd5(comment.getEmail());
        // 是否为博主评论  0：否  1：是
        String token = request.getHeader(AuthConstants.JWT_TOKEN_HEARER_NAME);
        if(token == null){
            comment.setIsAdmin(0);
        }else {
//
//            String username = JwtUtils.getAccount(token);
//            Object tokenOfCache = cache.get(AuthConstants.JWT_TOKEN_CACHE_PREFIX + username);
//            if(tokenOfCache.equals(token)){
//                comment.setIsAdmin(1);
//                comment.setAuthor(username);
//            }
//            comment.setIsAdmin(0);
        }
        baseMapper.insert(comment);
    }

    public List<ArticleCommentsVO> getTreeCommentsOfParent(List<ArticleCommentsVO> source) {
        List<ArticleCommentsVO> parents = new ArrayList<>();
        for (ArticleCommentsVO vo : source) {
            if (vo.getParentId() == 0) {
                parents.add(vo);
                vo.setChildren(getTreeCommentsOfChildren(vo, source));
            }
        }
        return parents;
    }

    public List<ArticleCommentsVO> getTreeCommentsOfChildren(ArticleCommentsVO parent, List<ArticleCommentsVO> source) {
        List<ArticleCommentsVO> parents = new ArrayList<>();
        for (ArticleCommentsVO vo : source) {
            if (vo.getParentId().equals(parent.getId())) {
                parents.add(vo);
                vo.setChildren(getTreeCommentsOfChildren(vo, source));
            }
        }
        return parents;
    }
}
