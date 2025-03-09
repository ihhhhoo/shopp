package com.hyb.seckill.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @Author hyb
 * @Date 2025/3/8 21:08
 * @Version 1.0
 */
public class CookieUtil {
    //得到Cookie的值 不编码
    public static String getCookieValue(HttpServletRequest request, String
            cookieName) {
        return getCookieValue(request, cookieName, false);
    }


    public static String getCookieValue(HttpServletRequest request,
                                        String cookieName,
                                        boolean isDecoder) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (int i = 0; i < cookieList.length; i++) {
                if (isDecoder) {//如果需要解码则用URLDecoder.decode进行解码
                    retValue = URLDecoder.decode(cookieList[i].getValue(), "UTF-8");
                } else {
                    retValue = cookieList[i].getValue();
                }
                break;

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return retValue;
    }

    public static String getCookieValue(HttpServletRequest request, String
            cookieName, String encodeString) {//根据encodeString进行解码
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (int i = 0; i < cookieList.length; i++) {
                if (cookieList[i].getName().equals(cookieName)) {
                    retValue = URLDecoder.decode(cookieList[i].getValue(),
                            encodeString);
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    /**
     * 设置 Cookie 的值 不设置生效时间默认浏览器关闭即失效,也不编码
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response,
                                 String cookieName, String cookieValue) {
        setCookie(request, response, cookieName, cookieValue, -1);
    }

    /**
     * 设置 Cookie 的值 在指定时间内生效,但不编码
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse
            response, String cookieName, String cookieValue, int cookieMaxage) {
        setCookie(request, response, cookieName, cookieValue, cookieMaxage, false);
    }

    /**
     * 设置 Cookie 的值 不设置生效时间,但编码
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse
            response, String cookieName,
                                 String cookieValue, boolean isEncode) {
        setCookie(request, response, cookieName, cookieValue, -1, isEncode);
    }

    /**
     * 设置 Cookie 的值 在指定时间内生效, 编码参数
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse
            response, String cookieName,
                                 String cookieValue, int cookieMaxage, boolean
                                         isEncode) {
        doSetCookie(request, response, cookieName, cookieValue, cookieMaxage,
                isEncode);
    }

    /**
     * 设置 Cookie 的值 在指定时间内生效, 编码参数(指定编码)
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse
            response, String cookieName,
                                 String cookieValue, int cookieMaxage, String
                                         encodeString) {
        doSetCookie(request, response, cookieName, cookieValue, cookieMaxage,
                encodeString);
    }

    /**
     * 删除 Cookie 带 cookie 域名
     */
    public static void deleteCookie(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String cookieName) {
        doSetCookie(request, response, cookieName, "", -1, false);
    }

    /**
     * 设置 Cookie 的值，并使其在指定时间内生效
     *
     * @param cookieMaxage cookie 生效的最大秒数
     */
    private static final void doSetCookie(HttpServletRequest request,
                                          HttpServletResponse response,
                                          String cookieName, String cookieValue,
                                          int cookieMaxage, boolean isEncode) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else if (isEncode) {
                cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            if (cookieMaxage > 0) {
                cookie.setMaxAge(cookieMaxage);
            }
//
            if (null != request) {// 设置域名的 cookie
//
                String domainName = getDomainName(request);
//
                if (!"localhost".equals(domainName)) {
//
                    cookie.setDomain(domainName);
//
                }
//
            }
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置 Cookie 的值，并使其在指定时间内生效
     *
     * @param cookieMaxage cookie 生效的最大秒数
     */
    private static final void doSetCookie(HttpServletRequest request,
                                          HttpServletResponse response,
                                          String cookieName, String cookieValue,
                                          int cookieMaxage, String encodeString) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else {
                cookieValue = URLEncoder.encode(cookieValue, encodeString);
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            if (cookieMaxage > 0) {
                cookie.setMaxAge(cookieMaxage);
            }
            if (null != request) {// 设置域名的 cookie
                String domainName = getDomainName(request);
                System.out.println(domainName);
                if (!"localhost".equals(domainName)) {
                    cookie.setDomain(domainName);
                }
            }
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到 cookie 的域名
     */
    private static final String getDomainName(HttpServletRequest request) {
        String domainName = null;
        // 通过 request 对象获取访问的 url 地址
        String serverName = request.getRequestURL().toString();
        if ("".equals(serverName)) {
            domainName = "";
        } else {
            // 将 url 地下转换为小写
            serverName = serverName.toLowerCase();
            // 如果 url 地址是以 http://开头 将 http://截取
            if (serverName.startsWith("http://")) {
                serverName = serverName.substring(7);
            }
            int end = serverName.length();
            // 判断 url 地址是否包含"/"
            if (serverName.contains("/")) {
                 //得到第一个"/"出现的位置
                end = serverName.indexOf("/");
            }
            // 截取
            serverName = serverName.substring(0, end);
// 根据"."进行分割
            final String[] domains = serverName.split("\\.");
            int len = domains.length;
            if (len > 3) {
// www.abc.com.cn
                domainName = domains[len - 3] + "." + domains[len - 2] + "." +
                        domains[len - 1];
            } else if (len > 1) {
// abc.com or abc.cn
                domainName = domains[len - 2] + "." + domains[len - 1];
            } else {
                domainName = serverName;
            }
        }
        if (domainName.indexOf(":") > 0) {
            String[] ary = domainName.split("\\:");
            domainName = ary[0];
        }
        return domainName;
    }


}
