package cn.itcast.core.service.cart;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName CartServiceImpl
 * @Description 实现购物车
 * @Author Ygkw
 * @Date 16:11 2019/4/2
 * @Version 2.1
 **/
@Service
public class CartServiceImpl implements CartService {

    @Resource
    private ItemDao itemDao;

    @Resource
    private SellerDao sellerDao;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * @param id
     * @return cn.itcast.core.pojo.item.Item
     * @author 举个栗子
     * @Description 获取到商家id
     * @Date 16:07 2019/4/2
     **/
    @Override
    public Item findOne(Long id) {
        return itemDao.selectByPrimaryKey(id);
    }

    /**
     * @param cartList
     * @return java.util.List<cn.itcast.core.pojo.cart.Cart>
     * @author 举个栗子
     * @Description 填充购物车中的数据
     * @Date 17:21 2019/4/2
     **/
    @Override
    public List<Cart> autoDataToCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            Seller seller = sellerDao.selectByPrimaryKey(cart.getSellerId());
            cart.setSellerName(seller.getNickName());   // 填充店铺名称
            // 填充购物项的数据
            List<OrderItem> orderItemList = cart.getOrderItemList();
            if (orderItemList != null && orderItemList.size() > 0) {
                for (OrderItem orderItem : orderItemList) {
                    Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                    orderItem.setPicPath(item.getImage());   // 填充商品图片
                    orderItem.setTitle(item.getTitle());     // 填充商品标题
                    orderItem.setPrice(item.getPrice());     // 填充商品单价
                    BigDecimal totalFee = new BigDecimal(item.getPrice().doubleValue() * orderItem.getNum());
                    orderItem.setTotalFee(totalFee);        // 填充商品小计
                }
            }
        }
        return cartList;
    }

    /**
     * @param username
     * @param newCartList
     * @return void
     * @author 举个栗子
     * @Description 将本地购物车合并到redis中
     * @Date 16:45 2019/4/3
     **/
    @Override
    public void mergeCartList(String username, List<Cart> newCartList) {

        // 从redis中取出老车
        List<Cart> oldCartList = (List<Cart>) redisTemplate.boundHashOps("BUYER_CART").get(username);
        // 从新车合并到老车中
        oldCartList = newCartListMergeOldCartList(newCartList, oldCartList);
        // 将老车保存到redis中
        redisTemplate.boundHashOps("BUYER_CART").put(username, oldCartList);
    }

    /**
     * @author 举个栗子
     * @Description 从redis中取出购物车
     * @Date 19:45 2019/4/3
     * @param username
     * @return java.util.List<cn.itcast.core.pojo.cart.Cart>
     **/
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("BUYER_CART").get(username);
        return cartList;
    }

    // 将新车合并到老车中
    private List<Cart> newCartListMergeOldCartList(List<Cart> newCartList, List<Cart> oldCartList) {
        if (newCartList != null) {
            if (oldCartList != null) {
                // 新车、老车都不为空，进行合并
                // 合并：判断是否属于同一个商家
                for (Cart newCart : newCartList) {
                    int sellerIndexOf = oldCartList.indexOf(newCart);
                    if (sellerIndexOf != -1) {
                        // 属于同一个商家
                        List<OrderItem> newOrderItemList = newCart.getOrderItemList();   // 新车购物项
                        List<OrderItem> oldOrderItemList = oldCartList.get(sellerIndexOf).getOrderItemList();  // 老车购物项

                        // 继续判断是否是同款商品
                        for (OrderItem newOrderItem : newOrderItemList) {
                            int itemIndexOf = oldOrderItemList.indexOf(newOrderItem);
                            if (itemIndexOf != -1) {
                                // 同款商品，合并数量
                                OrderItem oldOrderItem = oldOrderItemList.get(itemIndexOf);   // 老车购物项
                                oldOrderItem.setNum(oldOrderItem.getNum() + newOrderItem.getNum());  // 合并数量
                            } else {
                                // 不是同款商品，直接加入到该商家的购物项中
                                oldOrderItemList.add(newOrderItem);
                            }
                        }
                    } else {
                        // 不属于同一个商家，可以直接装车
                        oldCartList.add(newCart);
                    }
                }
            } else {
                // 新车不为空，老车为空，直接返回新车
                return newCartList;
            }
        } else {
            // 如果新车为null,直接返回老车
            return oldCartList;
        }
        return oldCartList;
    }
}
