package com.ponking.pblog.controller.api;




import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ponking.pblog.common.cache.Cache;
import com.ponking.pblog.common.constants.AuthConstants;
import com.ponking.pblog.model.params.PBlogProperties;
import com.ponking.pblog.common.exception.GlobalException;
import com.ponking.pblog.common.util.JwtUtil;
import com.ponking.pblog.common.util.MD5Util;
import com.ponking.pblog.model.result.R;
import com.ponking.pblog.model.result.ResultCode;
import com.ponking.pblog.model.entity.User;
import com.ponking.pblog.model.params.UserInfo;
import com.ponking.pblog.model.vo.LoginVo;
import com.ponking.pblog.service.IUserService;
import com.ponking.pblog.shiro.JwtToken;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Ponking
 * @ClassName LoginController
 * @date 2020/3/14--23:13
 **/
@RestController
@Slf4j
public class ApiAdminLoginController {


    @Autowired
    private Cache<String,Object> cache;

    @Autowired
    private IUserService userService;


    @Autowired
    private PBlogProperties config;


    /**
     * 认证未通过
     * @return
     */
    @GetMapping("/unauth")
    public R unauthorized() {
        return R.failed(ResultCode.Unauthorized);
    }

    /**
     * 登录操作
     * @loginForm
     * @return token值
     */
    @PostMapping("/login")
    public R login(@RequestBody LoginVo loginForm, HttpServletRequest request) {
        if(StringUtils.isEmpty(loginForm.getUsername())){
            throw new GlobalException("用户名不能为空");
        }
        if(StringUtils.isEmpty(loginForm.getPassword())){
            throw new GlobalException("密码不能为空");
        }
        // 验证码 todo 可以将验证码放置redis缓存 @see DefaultCapctch
        String rightCode = (String) request.getSession().getAttribute("rightCode");
        String tryCode = loginForm.getCode();
        if (!rightCode.equals(tryCode)) {
            throw new CredentialsException("验证码错误");
        }

        // 是否存在该用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username",loginForm.getUsername());
        User dbUser = userService.getById(1L);
        if(StringUtils.isEmpty(dbUser)){
            throw new UnknownAccountException("不存在该用户");
        }

        // TODO 账号是否已被禁用

        // 判断密码是否正确
        String pwd = MD5Util.encrypt(loginForm.getPassword(),dbUser.getSalt());
        if(!pwd.equals(dbUser.getPassword())){
            throw new CredentialsException("密码错误");
        }
        // 生成jwtToken
        String jwtToken = JwtUtil.generateToken(dbUser.getUsername());
        log.info("login success...");
        // 用于注销登录(若已在其他地方登录，刷新token)
        if(cache.get(AuthConstants.JWT_TOKEN_CACHE_PREFIX+dbUser.getUsername())!=null){
            cache.remove(AuthConstants.JWT_TOKEN_CACHE_PREFIX+dbUser.getUsername());
        }
        cache.put(AuthConstants.JWT_TOKEN_CACHE_PREFIX+dbUser.getUsername(),jwtToken);

        Map<String,String> data = new HashMap<>();
        data.put("token",jwtToken);
        return R.success(data);
    }

    /**
     * 获取权限
     * @param token
     * @return
     */
    @GetMapping("/info")
    public R userInfo(@RequestParam String token){
        JwtToken jwtToken = new JwtToken(token);
        Subject subject = SecurityUtils.getSubject();
        subject.login(jwtToken);

        // 没有抛异常执行下一步
        UserInfo info = new UserInfo();
        // todo 模拟权限
        String [] roles = {"templates/admin"};
        info.setRoles(roles);
        info.setIntroduction(config.getAuthorDescription());
        info.setAvatar(config.getBlogAvatar());

        Claims claims = JwtUtil.parseJWT(token);
        info.setName(claims.getSubject());
        return R.success(info);
    }

    @PostMapping("/logout")
    public R logout(HttpServletRequest request){
        // TODO 日后更新为redis存储
        String token = request.getHeader("Authorization");
        cache.remove(token);
        return R.success();
    }

}