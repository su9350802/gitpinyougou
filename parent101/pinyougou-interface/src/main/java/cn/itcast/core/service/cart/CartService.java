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

    /**
     * @author 举个栗子
     * @Description 将本地购物车合并到redis中
     * @Date 16:45 2019/4/3
     * @param username
     * @param cartList
     * @return void
     **/
    void mergeCartList(String username,List<Cart> cartList);

    /**
     * @author 举个栗子
     * @Description 从redis中取出购物车
     * @Date 19:45 2019/4/3
     * @param username
     * @return java.util.List<cn.itcast.core.pojo.cart.Cart>
     **/
    List<Cart> findCartListFromRedis(String username);
}
