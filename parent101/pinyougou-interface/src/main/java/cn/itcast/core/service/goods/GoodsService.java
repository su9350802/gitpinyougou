package cn.itcast.core.service.goods;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.vo.GoodsVo;

public interface GoodsService {

    /**
     * 商品录入
     * @param goodsVo
     */
    void add(GoodsVo goodsVo);

    /**
     * 查询商品列表信息
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    PageResult search(Integer page, Integer rows, Goods goods);
}
