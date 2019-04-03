package cn.itcast.core.service.cart;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;

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
     * @author 举个栗子
     * @Description 填充购物车中的数据
     * @Date 17:21 2019/4/2
     * @param cartList
     * @return java.util.List<cn.itcast.core.pojo.cart.Cart>
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
}
