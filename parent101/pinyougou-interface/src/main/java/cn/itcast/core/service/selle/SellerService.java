package cn.itcast.core.service.selle;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.seller.Seller;

public interface SellerService {

    /**
     * 商家申请驻入
     * @param seller
     */
    void add(Seller seller);

    /**
     * 商家审核查询
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    PageResult search(Integer page,Integer rows,Seller seller);

    /**
     * 回显商家
     * @param sellerId
     * @return
     */
    Seller findOne(String sellerId);

    /**
     * 审核商家
     * @param sellerId
     * @param status
     */
    void updateStatus(String sellerId,String status);
}
