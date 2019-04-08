package cn.itcast.core.controller.pay;

import cn.itcast.core.entity.Result;
import cn.itcast.core.service.pay.PayService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName PayController
 * @Description 生成二维码
 * @Author Ygkw
 * @Date 12:30 2019/4/5
 * @Version 2.1
 **/
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;

    /**
     * @author 举个栗子
     * @Description 生成支付页面需要的数据
     * @Date 12:32 2019/4/5
     * @param
     * @return java.util.Map<java.lang.String,java.lang.String>
     **/
    @RequestMapping("/createNative.do")
    public Map<String, String> createNative() throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return payService.createNative(username);
    }

    @RequestMapping("/queryPayStatus.do")
    public Result queryPayStatus(String out_trade_no) {

        // 定义一个计数器
        int time = 0;
        try {
            while (true) {
                Map<String, String> map = payService.queryPayStatus(out_trade_no);
                // 判断是否支付成功
                String tradeState = map.get("trade_state");
                if ("SUCCESS".equals(tradeState)) {
                    // 支付成功
                    return new Result(true,"支付成功");
                } else {
                    // 支付中....
                    Thread.sleep(5000);
                    time++;
                }
                // 默认地址url的失效时间是2个小时。设置失效：30分钟
                if (time > 360) {
                    return new Result(true,"二维码超时");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"支付失败");
        }
    }
}
