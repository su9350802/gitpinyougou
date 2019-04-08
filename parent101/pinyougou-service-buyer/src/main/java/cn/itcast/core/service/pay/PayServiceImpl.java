package cn.itcast.core.service.pay;


import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.utils.httpclient.HttpClient;
import cn.itcast.core.utils.uniquekey.IdWorker;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName PayServiceImpl
 * @Description 生成支付二维码
 * @Author Ygkw
 * @Date 12:26 2019/4/5
 * @Version 2.1
 **/
@Service
public class PayServiceImpl implements PayService {

   @Resource
   private IdWorker idWorker;

    @Value("${appid}")
    private String appid;       // 微信公众账号或开放平台APP的唯一标识

    @Value("${partner}")
    private String partner;     // 财付通平台的商户账号

    @Value("${partnerkey}")
    private String partnerkey;  // 财付通平台的商户密钥

    @Value("${notifyurl}")
    private String notifyurl;   // 回调地址

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private PayLogDao payLogDao;

    /**
     * @return java.util.Map<java.lang.String , java.lang.String>
     * @author 举个栗子
     * @Description 生成支付二维码
     * @Date 12:25 2019/4/5
     **/
    @Override
    public Map<String, String> createNative(String username) throws Exception {

        PayLog payLog = (PayLog) redisTemplate.boundHashOps("payLog").get(username);

        String out_trade_no = payLog.getOutTradeNo();

        // 调用微信统一下单的接口地址
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        Map<String,String> data = new HashMap<>();

        // 公众账号ID appid
        data.put("appid",appid);

        // 商户号 mch_id
        data.put("mch_id",partner);

        // 随机字符串 nonce_str
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        // 签名	sign

        // 商品描述	body
        data.put("body","品优购订单支付");

        // 商户订单号 out_trade_no
        // data.put("out_trade_no",String.valueOf(out_trade_no));
        data.put("out_trade_no",payLog.getOutTradeNo());

        // 标价金额	total_fee
        data.put("total_fee","1");      // 支付金额

        // 终端IP spbill_create_ip
        data.put("spbill_create_ip","123.12.12.123");

        // 通知地址	notify_url
        data.put("notify_url",notifyurl);

        // 交易类型	trade_type
        data.put("trade_type", "NATIVE");

        // 将map数据转成xml
        String xmlParam = WXPayUtil.generateSignedXml(data, partnerkey);

        /// 通过httpclient模拟浏览器发送请求
        HttpClient httpClient = new HttpClient(url);
        httpClient.setXmlParam(xmlParam);   //请求需要的数据
        httpClient.setHttps(true);          // https的请求
        httpClient.post();                  // post提交
        // 成功调用后：响应数据
        String strXML = httpClient.getContent();
        // 将xml转成map
        Map<String,String> map = WXPayUtil.xmlToMap(strXML);
        map.put("out_trade_no",out_trade_no);
        map.put("total_fee","1");   // 展示金额
        return map;
    }

    /**
     * @author 举个栗子
     * @Description 查询支付状态
     * @Date 15:51 2019/4/5
      * @param out_trade_no
     * @return java.util.Map<java.lang.String,java.lang.String>
     **/
    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) throws Exception {

        // 查询订单的接口地址
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";

        Map<String,String> data = new HashMap<>();
        // 公众账号ID bappid
        data.put("appid",appid);
        // 商户号 mch_id
        data.put("mch_id",partner);
        // 商户订单号
        data.put("out_trade_no", out_trade_no);
        // 随机字符串 nonce_str
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        // 签名	sign

        // 将map转成xml
        String xmlParam = WXPayUtil.generateSignedXml(data,partnerkey);
        HttpClient httpClient = new HttpClient(url);
        httpClient.setXmlParam(xmlParam);   // 请求需要的数据
        httpClient.setHttps(true);          // https请求
        httpClient.post();                  // post提交

        // 响应结果（xml转成map）
        String strXML = httpClient.getContent();
        Map<String,String> map = WXPayUtil.xmlToMap(strXML);

        // 如果支付成功，更新交易日志
        String tradeState = map.get("trade_state");
        if ("SUCCESS".equals(tradeState)) {
            PayLog payLog = new PayLog();
            payLog.setOutTradeNo(out_trade_no);     // 主键
            payLog.setPayTime(new Date());          // 支付完成日期
            payLog.setTradeState("1");              // 交易状态：支付成功
            payLog.setTransactionId(map.get("transaction_id"));
            payLogDao.updateByPrimaryKeySelective(payLog);

            // TODO 删除缓存中的交易日志

        }
        return map;
    }
}
