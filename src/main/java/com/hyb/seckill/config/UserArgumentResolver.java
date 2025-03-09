package com.hyb.seckill.config;

import com.hyb.seckill.pojo.User;
import com.hyb.seckill.service.UserService;
import com.hyb.seckill.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author hyb
 * @Date 2025/3/9 14:31
 * @Version 1.0
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver  {

    @Autowired
    private UserService userService;

    //如果这个方法返回true才会执行下面的resolveArgument方法
    //返回false不执行
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //获取参数是否是user类型
        Class<?> aClass = parameter.getParameterType();
        return aClass == User.class;
    }

    //类似拦截器 将传入的参数取出cookie值，然后获取User对象
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        String ticket  = CookieUtil.getCookieValue(request, "userTicket");

        if(!StringUtils.hasText(ticket )){
            return null;
        }
        //根据cookie-ticket到Redis获取User
        return userService.getUserByCookie(ticket,request,response);
    }
}
