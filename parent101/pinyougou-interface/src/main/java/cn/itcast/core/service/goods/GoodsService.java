package cn.itcast.core.service.goods;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.vo.GoodsVo;

public interface GoodsService {

    /**
     * 商品录入
     *
     * @param goodsVo
     */
    void add(GoodsVo goodsVo);

    /**
     * 查询商品列表信息
     *
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    PageResult search(Integer page, Integer rows, Goods goods);

    /**
     * 商品回显
     *
     * @param id
     * @return
     */
    GoodsVo findOne(Long id);

    /**
     * 商品更新
     *
     * @param goodsVo
     */
    void update(GoodsVo goodsVo);

    /**
     * 运营商系统查询待审核的商品列表
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    PageResult searchByManager(Integer page, Integer rows, Goods goods);

    /**
     * 商品审核
     * @param ids
     * @param status
     */
    void updateStatus(Long[] ids,String status);

    /**
     * 删除商品
     * @param ids
     */
    void delete(Long[] ids);
}
