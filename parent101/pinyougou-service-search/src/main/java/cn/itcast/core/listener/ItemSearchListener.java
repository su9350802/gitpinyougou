package cn.itcast.core.listener;

import cn.itcast.core.service.search.ItemSearchService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @ClassName ItemSearchListener
 * @Description 监听器
 * @Author Ygkw
 * @Date 20:45 2019/3/28
 * @Version 2.1
 **/
public class ItemSearchListener implements MessageListener {

    @Resource
    private ItemSearchService itemSearchService;

    /**
     * @param message
     * @return void
     * @author 举个栗子
     * @Description 获取消息-消费消息
     * @Date 20:45 2019/3/28
     **/
    @Override
    public void onMessage(Message message) {

        try {
            // 取出消息体
            ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
            String id = activeMQTextMessage.getText();
            System.out.println("消费者service-search获取到的id：" + id);
            // 消费消息
            itemSearchService.addItemToSolr(Long.parseLong(id));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
