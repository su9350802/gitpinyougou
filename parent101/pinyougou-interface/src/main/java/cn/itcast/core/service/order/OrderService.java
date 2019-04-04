package cn.itcast.core.service.order;

import cn.itcast.core.pojo.order.Order;

/**
 * @ClassName OrderService
 * @Description 订单操作
 * @Author Ygkw
 * @Date 16:37 2019/4/4
 * @Version 2.1
 **/
public interface OrderService {

    /**
     * @author 举个栗子
     * @Description 提交订单
     * @Date 16:38 2019/4/4
      * @param username
     * @param order
     * @return void
     **/
    void add(String username, Order order);
}
