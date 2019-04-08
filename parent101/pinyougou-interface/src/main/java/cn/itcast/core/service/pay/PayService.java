package cn.itcast.core.service.pay;

import java.util.Map;

/**
 * @ClassName PayService
 * @Description 支付接口
 * @Author Ygkw
 * @Date 12:24 2019/4/5
 * @Version 2.1
 **/
public interface PayService {

    /**
     * @author 举个栗子
     * @Description 生成支付二维码
     * @Date 12:25 2019/4/5
     * @param
     * @return java.util.Map<java.lang.String,java.lang.String>
     **/
    Map<String,String> createNative(String username) throws Exception;

    /**
     * @author 举个栗子
     * @Description 查询支付状态
     * @Date 15:51 2019/4/5
     * @param out_trade_no
     * @return java.util.Map<java.lang.String,java.lang.String>
     **/
     Map<String,String> queryPayStatus(String out_trade_no) throws Exception;
}
