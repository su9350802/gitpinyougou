package cn.itcast.core.controller.login;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName LoginController
 * @Description 显示登录人
 * @Author Ygkw
 * @Date 20:35 2019/3/31
 * @Version 2.1
 **/
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/name.do")
    public Map<String,String> name() {
        Map<String,String> map = new HashMap<>();
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("loginName",loginName);
        return map;
    }
}
