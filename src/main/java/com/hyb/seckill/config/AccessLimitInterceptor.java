package com.hyb.seckill.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyb.seckill.pojo.User;
import com.hyb.seckill.service.UserService;
import com.hyb.seckill.util.CookieUtil;
import com.hyb.seckill.vo.RespBean;
import com.hyb.seckill.vo.RespBeanEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @Author hyb
 * @Date 2025/3/13 9:47
 * @Version 1.0
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    //装配需要的组件
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate redisTemplate;

    //1得到user对象并放入到ThreadLocal中 2处理@Accesslimit
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){//如果要处理的是方法
            //获得先登录的对象
            User user = getUser(request, response);
            //存入到ThreadLocal中
            UserContext.setUser(user);
            //处理注解
            //把handler 转成 HandlerMethod
            HandlerMethod hm = (HandlerMethod) handler;
            //获得目标方法的注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null){
                //目标方法没有该接口
                return true;//放行
            }
            //获取注解的值
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            if(needLogin){//需要登录
                if(user == null){
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;//返回
                }
            }
            String uri = request.getRequestURI();
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
            if(count == null){
                valueOperations.set(uri + ":" + user.getId(),1,
                        second, TimeUnit.SECONDS);
            }else if(count < maxCount){
                valueOperations.increment(uri + ":" + user.getId());
            }else {//在刷接口
                render(response, RespBeanEnum.ACCESS_LIMIT_REACHE);
                return false;
            }
        }
        return true;
    }

    //构建返回对象 ----流的形式
    public void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        //构建respBean
        RespBean error = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(error));
        out.flush();
        out.close();
    }

    public User getUser(HttpServletRequest request, HttpServletResponse response){
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if(!StringUtils.hasText(ticket)){
            return null;//说明该用户没有登录
        }
        User user = userService.getUserByCookie(ticket, request, response);
        return user;
    }
}
