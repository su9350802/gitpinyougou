package cn.itcast.core.service.user;

import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.annotation.Resource;
import javax.jms.*;

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
}
