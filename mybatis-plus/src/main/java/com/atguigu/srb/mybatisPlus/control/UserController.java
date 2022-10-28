package com.atguigu.srb.mybatisPlus.control;

import com.atguigu.srb.mybatisPlus.pojo.entity.User;
import com.atguigu.srb.mybatisPlus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @RequestMapping("/all")
    public List<User> findAll(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String token = request.getHeader("token");
        List<User> n = userService.getUserByName("n");
        System.out.println(token);
        return  n;
    }
}
