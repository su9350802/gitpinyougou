package cn.itcast.core.controller.user;

import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.user.UserService;
import cn.itcast.core.utils.checkphone.PhoneFormatCheckUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
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
     * @param
     * @return cn.itcast.core.entity.Result
     * @author 举个栗子
     * @Description 短信发送
     * @Date 20:53 2019/3/30
     **/
    @RequestMapping("/sendCode.do")
    public Result sendCode(String phone) {
        try {
            // 校验手机号是否合法
            boolean phoneLegal = PhoneFormatCheckUtils.isPhoneLegal(phone);
            if (!phoneLegal) {
                return new Result(false, "手机号不合法");
            }
            userService.sendCode(phone);
            return new Result(true, "短信发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "短信发送失败");
        }
    }

    @RequestMapping("/add.do")
    public Result add(String smscode, @RequestBody User user) {
        try {
            userService.add(smscode, user);
            return new Result(true, "注册成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "注册失败");
        }
    }
}
