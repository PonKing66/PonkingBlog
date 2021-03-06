package com.ponking.pblog.service.impl;

import com.ponking.pblog.model.entity.User;
import com.ponking.pblog.mapper.UserMapper;
import com.ponking.pblog.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author peng
 * @since 2020-03-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
