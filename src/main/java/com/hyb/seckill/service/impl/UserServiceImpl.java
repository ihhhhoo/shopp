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
        if (null == user) {//说明用户不存在
            //return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        //如果用户存在，则比对密码!!
        //注意，我们从loginVo取出的密码是中间密码(即客户端经过一次加密加盐处理的密码)
        if (!MD5Util.midPassToDBPass(password, user.getSlat()).equals(user.getPassword())) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }


        //生成cookie
        String ticket = UUIDUtil.uuid();
        //为了实现分布式Session, 把登录的用户存放到Redis
        System.out.println("使用的 redisTemplate->" + redisTemplate.hashCode());
        redisTemplate.opsForValue().set("user:" + ticket, user);
        CookieUtil.setCookie(request,response,"userTicket",ticket);
        //这里我们需要返回userTicket
        return RespBean.success(ticket);
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

    @Override
    public RespBean updatePassword(String userTicket, String password,
                                   HttpServletRequest request, HttpServletResponse response) {
            //更新用户密码, 同时删除用户在 Redis 的缓存对象
        User user = getUserByCookie(userTicket, request, response);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSlat()));
        int i = userMapper.updateById(user);
        if (i == 1) {
            //删除 redis
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }



}
