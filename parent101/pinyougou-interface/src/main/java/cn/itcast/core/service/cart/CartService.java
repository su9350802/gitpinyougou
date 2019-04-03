package cn.itcast.core.service.cart;

import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;

import java.util.List;

/**
 * @ClassName CartService
 * @Description 购物车接口
 * @Author Ygkw
 * @Date 16:06 2019/4/2
 * @Version 2.1
 **/
public interface CartService {

    /**
     * @author 举个栗子
     * @Description 获取到商家id
     * @Date 16:07 2019/4/2
     * @param id
     * @return cn.itcast.core.pojo.item.Item
     **/
    Item findOne(Long id);

    /**
     * @author 举个栗子
     * @Description 填充购物车中的数据
     * @Date 17:21 2019/4/2
     * @param cartList
     * @return java.util.List<cn.itcast.core.pojo.cart.Cart>
     **/
    List<Cart> autoDataToCart(List<Cart> cartList);
}
