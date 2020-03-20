package com.auth2.auth_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author baizh@enlink.cn
 * @date 2020/3/2
 */
@Controller
public class HelloController {

    @GetMapping("/hello/world")
    public String hello() {

        return "hello world";
    }

//    @GetMapping("/login")
//    public String login() {
//
//        return "member/list";
//    }
}
