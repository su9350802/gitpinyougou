package cn.itcast.core.service.user;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.utils.md5.MD5Util;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName UserServiceImpl
 * @Description 用户服务实现
 * @Author Ygkw
 * @Date 20:44 2019/3/30
 * @Version 2.1
 **/
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource
    private Destination smsDestination;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserDao userDao;

    /**
     * @author 举个栗子
     * @Description 获取短信验证码
     * @Date 20:47 2019/3/30
      * @param phone
     * @return void
     **/
    @Override
    public void sendCode(final String phone) {

        // 验证码
        final String code = RandomStringUtils.randomNumeric(6);
        System.out.println("验证码：" + code);
        // 保存验证码到redis中
        // 使用的数据结构：String  set key(phone) value(code)
        redisTemplate.boundValueOps(phone).set(code);
        // 设置验证码的失效时间
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.MINUTES);
        // 将数据发送到mq中
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                // 封装消息体
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phoneNumbers", phone);
                mapMessage.setString("signName", "阮文");
                mapMessage.setString("templateCode", "SMS_140720901");
                mapMessage.setString("templateParam", "{\"code\":\""+code+"\"}");
                return mapMessage;
            }
        });
    }

    /**
     * @author 举个栗子
     * @Description 用户注册
     * @Date 15:52 2019/3/31
      * @param smscode
     * @param user
     * @return void
     **/
    @Transactional
    @Override
    public void add(String smscode, User user) {

        // 判断填写的验证码是否正确
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        if (code != null && !"".equals(smscode) && smscode.equals(code)) {
            // 验证码正确
            // 保存用户
            String password = MD5Util.MD5Encode(user.getPassword(), null);
            user.setPassword(password);
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userDao.insertSelective(user);
        } else {
            throw new RuntimeException("验证码不正确");
        }
    }
}
