/* 文件名: CasAuthController.java
 *
 * 作者: yesj (yesj@enlink.cn)
 * 描述:
 *
 * Copyright @2020 Enlink, All Rights Reserved.
 */
package com.auth2.server.controller;

import com.auth2.server.utils.CasUtils;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/cas")
public class CasAuthController {

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Autowired
    private UserInfoRepository userInfoRepository;

    /**
     * @param request
     * @param response
     * @param serviceUrl
     * @return
     */
    @RequestMapping("/login")
    public String getTicket(HttpServletRequest request, HttpServletResponse response
            , @RequestParam(value = "service", required = false) String serviceUrl) throws IOException {

        // 测试地址：http://192.168.101.46:8080/cas/login?service=https%3a%2f%2fwww.baidu.com

        // 1. 校验有没有登录过
        String username = (String) request.getSession().getAttribute("username");
        if(StringUtils.isEmpty(username)){
            request.getSession().setAttribute("serviceUrl", serviceUrl);
            return "caslogin";
        }

        // ticket没有过期，再次返回
        String ticket = (String) request.getSession().getAttribute(username);

        if (ticket != null) {
            response.sendRedirect(serviceUrl + "&ticket=" + ticket);
            return "caslogin";
        }

        // 生成ST
        ticket = CasUtils.genServiceTicket();

        // 1.1 登录过，直接生成ST，返回给service
        if(StringUtils.isEmpty(serviceUrl)){
            // serviceUrl为空，直接跳到登录页；
            return "index";
        }
        response.sendRedirect(serviceUrl + "?ticket=" + ticket);
        return "caslogin";

//        // TODO 校验service是否注册
//        // 默认已注册

        // 校验用户是否有service的权限
    }

    /**
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/caslogin")
    public String login(@RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "password", required = false) String password,
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
        System.out.println(username + " : " + password);
//        String username = (String) request.getParameterMap().get("username")[0];
//        String password = (String) request.getParameterMap().get("password")[0];
        if (username == null || password == null) {
            return "caslogin";
        }
        // 用户名和密码校验成功，重定向
        UserInfo userInfo = userInfoRepository.getByUsername(username);
        if (userInfo == null || !password.equals(userInfo.getPassword())) {
            return "caslogin";
        }
        String serviceUrl = request.getSession().getAttribute("serviceUrl").toString();
        String serviceTicket = CasUtils.genServiceTicket();
        request.getSession().setAttribute("username", userInfo.getPreferredUsername());
        request.getSession().setAttribute(userInfo.getPreferredUsername(), serviceTicket);
        response.sendRedirect(serviceUrl + "?ticket=" + serviceTicket);
        return "caslogin";
    }

    @RequestMapping("/p3/serviceValidate")
    public void serviceValidateP3(HttpServletRequest request, HttpServletResponse response
            , @RequestParam(value = "ticket") String ticket
            , @RequestParam(value = "service") String serviceUrl) {

        response.addHeader("Content-Type", "application/xml;charset=UTF-8");
        response.addHeader("Content-Language", "zh-CN");

        String s = "<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>\n" +
                "    <cas:authenticationSuccess>\n" +
                "        <cas:user>" + "zhangsan" + "</cas:user>\n" +
                "           <cas:attributes>\n" +
                "               <cas:nickname>"+"sb"+"</cas:nickname>\n" +
                "               <cas:username>"+"zhangsan"+"</cas:username>\n" +
                "               <cas:telephone>"+"12345612345"+"</cas:telephone>\n" +
                "               <cas:email>"+"aaa.com"+"</cas:email>\n" +
                "           </cas:attributes>"+
                "    </cas:authenticationSuccess>\n" +
                "</cas:serviceResponse>";

        try (PrintWriter out = response.getWriter()) {
            out.write(s);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
