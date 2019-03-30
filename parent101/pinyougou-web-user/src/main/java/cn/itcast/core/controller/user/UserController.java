package cn.itcast.core.controller.user;

import cn.itcast.core.entity.Result;
import cn.itcast.core.service.user.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName UserController
 * @Description 用户个人中心系统
 * @Author Ygkw
 * @Date 20:52 2019/3/30
 * @Version 2.1
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * @author 举个栗子
     * @Description 短信发送
     * @Date 20:53 2019/3/30
      * @param
     * @return cn.itcast.core.entity.Result
     **/
    @RequestMapping("/sendCode.do")
    public Result sendCode(String phone) {
        try {
            userService.sendCode(phone);
            return new Result(true,"短信发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"短信发送失败");
        }
    }
}
