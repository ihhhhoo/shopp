package com.hyb.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyb.seckill.exception.GlobalException;
import com.hyb.seckill.mapper.UserMapper;
import com.hyb.seckill.pojo.User;
import com.hyb.seckill.service.UserService;
import com.hyb.seckill.util.CookieUtil;
import com.hyb.seckill.util.MD5Util;
import com.hyb.seckill.util.UUIDUtil;
import com.hyb.seckill.vo.LoginVo;
import com.hyb.seckill.vo.RespBean;
import com.hyb.seckill.vo.RespBeanEnum;
import com.hyb.seckill.vo.ValidatorUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author hyb
 * @Date 2025/3/8 17:32
 * @Version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;
    //登录id就是手机号

    @Resource
    private RedisTemplate redisTemplate;
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        // //判断是否为空
        // if(!StringUtils.hasText(mobile) || !StringUtils.hasText(password)){
        //     return RespBean.error(RespBeanEnum.LOGIN_DADA_ISNULL);
        // }
        // //手机校验
        // if(!ValidatorUtil.isMobile(mobile)){
        //     return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        // }
        User user = userMapper.selectById(mobile);
        //查询数据库
        // if(user == null){
        //     // return RespBean.error(RespBeanEnum.MOBILE_NOT_EXIST);
        //     //抛异常让全局处理
        //     throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        // }
        // if(!MD5Util.inputPassToDBPass(password,user.getSlat()).equals(user.getPassword())){
        //     return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        // }
        //生成cookie
        String ticket = UUIDUtil.uuid();
        redisTemplate.opsForValue().set("user:"+ticket,user);
        CookieUtil.setCookie(request,response,"userTicket",ticket);

        return RespBean.success();
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if(!StringUtils.hasText(userTicket)){
            return null;
        }
        User user = (User)redisTemplate.opsForValue().get("user:" + userTicket);
        //如果用户不为null,重新设置cookie
        if(user != null){
            CookieUtil.setCookie(request,response,"userTicket",userTicket);
        }
        return user;
    }


}
