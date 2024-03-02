package com.wv.wvojcodesendbox.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wv.wvojcodesendbox.model.domain.User;
import com.wv.wvojcodesendbox.service.UserService;
import com.wv.wvojcodesendbox.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 21192
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-03-01 22:18:13
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




