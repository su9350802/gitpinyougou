package cn.itcast.core.controller.cart;

import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.cart.CartService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CartController
 * @Description 商品加入购物车模块
 * @Author Ygkw
 * @Date 15:06 2019/4/2
 * @Version 2.1
 **/
@RestController
@RequestMapping("/cart")
public class CartController {


    @Reference
    private CartService cartService;

    /*response.setHeader("Access-Control-Allow-Origin","http://localhost:9003");
    // 携带cookie
     response.setHeader("Access-Control-Allow-Credentials", "true");*/

    @RequestMapping("/addGoodsToCartList.do")
    @CrossOrigin(origins = {"http://localhost:9003"})
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {

        try {

            // 将商品加入购物车具体的业务实现

            // 1、定义一个空车的集合
            List<Cart> cartList = null;
            // 2、判断本地是否有车子

            // 定义一个开关（标记）
            boolean flag = false;

            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    // cookie的数据：key-value(json串)
                    if ("BUYER_CART".equals(cookie.getName())) {
                        // 3、有：从cookie中取出来
                        flag=true;
                        String text = cookie.getValue();
                        // 解码
                        String urlString = URLDecoder.decode(text, "UTF-8");
                        // 车对象json串
                        cartList = JSON.parseArray(urlString, Cart.class);
                        break;
                    }
                }
            }
            // 4、没有：第一次，创建一个新车
            if (cartList == null) {
                cartList = new ArrayList<>();
            }
            // =有车了=

            // 填充数据
            Cart cart = new Cart();
            Item item = cartService.findOne(itemId);
            cart.setSellerId(item.getSellerId());   // 商家id
            List<OrderItem> orderItemList = new ArrayList<>();
            OrderItem orderItem = new OrderItem();
            orderItem.setItemId(itemId);
            orderItem.setNum(num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);  // 购物项：库存id，以及购买数量

            // 5、将商品进行装车
            // 5-1、判断是否属于同一个商家（判断sellerId是否一样）
            int sellerIndexOf = cartList.indexOf(cart);
            if (sellerIndexOf != -1) {
                // 属于同一个商家：继续判断是否属于同款商品（库存id）
                // 取出之前购物项的数据
                List<OrderItem> oldOrderItemList = cartList.get(sellerIndexOf).getOrderItemList();
                // 判断本次的购物项在之前是否存在
                int itemIndexOf = oldOrderItemList.indexOf(orderItem);
                if (itemIndexOf != -1) {
                    // 同款商品，合并数量
                    OrderItem oldOrderItem = oldOrderItemList.get(itemIndexOf);
                    oldOrderItem.setNum(oldOrderItem.getNum() + num);
                } else {
                    // 同商家但不是同款商品
                    // 将购物项加入之前的购物项集中
                    oldOrderItemList.add(orderItem);
                }

            } else {
                // 不属于同一个商家：直接装车
                cartList.add(cart);
            }
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            // anonymousUser
            if (!"anonymousUser".equals(username)) {
                // 已登录
                // 6-1、将车子保存到cookie中
                cartService.mergeCartList(username, cartList);
                // 同步本地的购物车，需要清空本地的购物车
                if (flag) {
                    Cookie cookie = new Cookie("BUYER_CART", null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");   // cookie共享
                    response.addCookie(cookie);
                }
            } else {
                // 未登录
                // 6-2、将车子保存到cookie中
                String encode = URLEncoder.encode(JSON.toJSONString(cartList), "utf-8");
                Cookie cookie = new Cookie("BUYER_CART", encode);
                cookie.setMaxAge(60 * 60);
                cookie.setPath("/");   // cookie共享
                response.addCookie(cookie);
            }
            return new Result(true, "成功加入购物车");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "加入购物车失败");
        }
    }


    @RequestMapping("/findCartList.do")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) throws Exception {
        List<Cart> cartList = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("BUYER_CART".equals(cookie.getName())) {
                    String text = cookie.getValue();
                    String urlString = URLDecoder.decode(text, "UTF-8");
                    cartList = JSON.parseArray(urlString, Cart.class);
                    break;
                }
            }
        }
        // 判断用户是否登录
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            // 场景：未登录将商品加入购物车
            // 如果用户登录成功跳转到该页面 --> 【我的购物车】 --> 将本地的购物车同步到redis中
            if (cartList != null) {
                // 同步
                cartService.mergeCartList(username,cartList);
                // 清空本地cookie
                Cookie cookie = new Cookie("BUYER_CART", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");   // cookie共享
                response.addCookie(cookie);
            }
        }
        // 已登录，从redis中获取购物
        cartList = cartService.findCartListFromRedis(username);
        if (cartList != null) {
            // 填充数据
            cartList = cartService.autoDataToCart(cartList);
        }
        return cartList;
    }
}
