package com.yicj.boot.controller;

import com.yicj.boot.entity.UserInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @RequestMapping("/getUserInfo")
    public UserInfo getUserInfo(){
        UserInfo userInfo = new UserInfo() ;
        userInfo.setDept(1);
        userInfo.setId("1001");
        userInfo.setName("yicj");
        return userInfo ;
    }

}
