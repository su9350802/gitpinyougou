package cn.itcast.core.service.order;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.utils.uniquekey.IdWorker;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @ClassName OrderServiceImpl
 * @Description 实现订单操作
 * @Author Ygkw
 * @Date 16:39 2019/4/4
 * @Version 2.1
 **/
@Service
public class OrderServiceImpl implements OrderService {
    
    @Resource
    private OrderDao orderDao;
    
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private IdWorker idWorker;

    @Resource
    private ItemDao itemDao;

    @Resource
    private OrderItemDao orderItemDao;

    /**
     * @param username
     * @param order
     * @return void
     * @author 举个栗子
     * @Description 提交订单
     * @Date 16:38 2019/4/4
     **/
    @Transactional
    @Override
    public void add(String username, Order order) {
        
        // 保存订单：根据商家进行分类
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("BUYER_CART").get(username);
        if (cartList != null && cartList.size() > 0) {
            for (Cart cart : cartList) {    // 根据商家进行分类
                long orderId = idWorker.nextId();
                order.setOrderId(orderId);  // 主键
                double payment = 0f;        // 订单的总金额(购买的该商家下的商品的总金额)
                order.setPaymentType("1");  // 支付方式：在线支付
                order.setStatus("1");       // 订单状态：待付款
                order.setCreateTime(new Date());   // 订单创建日期
                order.setUserId(username);      // 订单用户
                order.setSellerId(cart.getSellerId());  // 商家id

                // 保存订单明细(购物项)
                List<OrderItem> orderItemList = cart.getOrderItemList();
                if (orderItemList != null && orderItemList.size() > 0) {
                    for (OrderItem orderItem : orderItemList) {
                        // 订单明细
                        long id = idWorker.nextId();
                        orderItem.setId(id);    // 订单明细的主键
                        Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                        orderItem.setGoodsId(item.getGoodsId());    // spu的id
                        orderItem.setOrderId(orderId);          // 订单id
                        orderItem.setTitle(item.getTitle());    // 商品标题
                        orderItem.setPrice(item.getPrice());    // 单价
                        // 订单明细的小计
                        double totalFee = item.getPrice().doubleValue() * orderItem.getNum();
                        payment += totalFee;
                        orderItem.setTotalFee(new BigDecimal(totalFee));    // 明细小计
                        orderItem.setPicPath(item.getImage());      // 图片
                        orderItem.setSellerId(item.getSellerId());  // 商家id

                        orderItemDao.insertSelective(orderItem);
                    }
                }

                // 总金额 = 该商家的订单明细的价格
                order.setPayment(new BigDecimal(payment));
                orderDao.insertSelective(order);
            }

            // 清空购物车
            redisTemplate.boundHashOps("BUYER_CART").delete(username);
        }
    }
}
