package cn.itcast.core.controller.order;

import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.order.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName OrderController
 * @Description 订单操作
 * @Author Ygkw
 * @Date 17:08 2019/4/4
 * @Version 2.1
 **/
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/add.do")
    public Result add(@RequestBody Order order) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            orderService.add(username, order);
            return new Result(true, "下单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "下单失败");
        }

    }
}
