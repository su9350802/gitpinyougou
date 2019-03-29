package cn.itcast.core.listener;

import cn.itcast.core.service.staticpage.StaticPageService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @ClassName PageListener
 * @Description 监听器
 * @Author Ygkw
 * @Date 12:10 2019/3/29
 * @Version 2.1
 **/
public class PageListener implements MessageListener {

    @Resource
    private StaticPageService staticPageService;

    /**
     * @param message
     * @return void
     * @author 举个栗子
     * @Description 获取消息-消费消息
     * @Date 12:13 2019/3/29
     **/
    @Override
    public void onMessage(Message message) {

        try {
            // 获取消息
            ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
            String id = activeMQTextMessage.getText();
            System.out.println("消费者service-page获取id:" + id);

            // 消费消息
            staticPageService.getHtml(Long.parseLong(id));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
