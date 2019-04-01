package cn.itcast.core.service.user;

import cn.itcast.core.pojo.user.User;

/**
 * @ClassName UserService
 * @Description 用户服务
 * @Author Ygkw
 * @Date 20:41 2019/3/30
 * @Version 2.1
 **/
public interface UserService {

    /**
     * @author 举个栗子
     * @Description 发送短信验证码
     * @Date 15:50 2019/3/31
      * @param phone
     * @return void
     **/
    void sendCode(String phone);

    /**
     * @author 举个栗子
     * @Description 用户注册
     * @Date 15:51 2019/3/31
      * @param
     * @return void
     **/
    void add(String smscode, User user);
}
