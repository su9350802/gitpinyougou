package cn.itcast.core.listener;

import cn.itcast.core.service.search.ItemSearchService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @ClassName ItemDeleteListener
 * @Description 自定义监听器
 * @Author Ygkw
 * @Date 16:28 2019/3/30
 * @Version 2.1
 **/
public class ItemDeleteListener implements MessageListener {


    @Resource
    private ItemSearchService itemSearchService;

    /**
     * @author 举个栗子
     * @Description 获取消息-消费消息
     * @Date 16:30 2019/3/30
      * @param message
     * @return void
     **/
    @Override
    public void onMessage(Message message) {
        try {
            ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
            String id = activeMQTextMessage.getText();
            System.out.println("删除的id:" + id);
            // 消费消息
            itemSearchService.deleteItemFromSolr(Long.parseLong(id));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
