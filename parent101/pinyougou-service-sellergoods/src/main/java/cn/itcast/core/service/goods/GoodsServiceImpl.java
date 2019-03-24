package cn.itcast.core.service.goods;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.vo.GoodsVo;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private GoodsDescDao goodsDescDao;

    @Resource
    private ItemDao itemDao;

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private BrandDao brandDao;

    @Resource
    private SellerDao sellerDao;

    /**
     * 商品录入
     *
     * @param goodsVo
     */
    @Transactional
    @Override
    public void add(GoodsVo goodsVo) {
        // 保存商品基本信息
        Goods goods = goodsVo.getGoods();
        goods.setAuditStatus("0");       //  商品的审核状态：0-待审核
        goodsDao.insertSelective(goods);  // 返回自增主键的id
        // 保存商品描述信息
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDesc.setGoodsId(goods.getId());
        goodsDescDao.insertSelective(goodsDesc);

        // 保存商品库存信息
        // 分析：判断是否启用规格
        if ("1".equals(goods.getIsEnableSpec())) {
            // 启用规格
            List<Item> itemList = goodsVo.getItemList();
            if (itemList != null && itemList.size() > 0) {
                for (Item item : itemList) {
                    String title = goods.getGoodsName() + " " + goods.getCaption();
                    // 数据
                    String spec = item.getSpec();
                    Map<String, String> map = JSON.parseObject(spec, Map.class);
                    Set<Map.Entry<String, String>> entries = map.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        title += " " + entry.getValue();
                    }
                    item.setTitle(title);

                    // 设置库存属性
                    setAttributeForItem(goods, goodsDesc, item);

                    // 保存
                    itemDao.insertSelective(item);
                }
            }
        } else {
            // 未启用规格
            Item item = new Item();
            item.setTitle(goods.getGoodsName() + " " + goods.getCaption());
            item.setPrice(goods.getPrice());
            item.setNum(9999);
            item.setIsDefault("1");
            item.setSpec("{}");
            setAttributeForItem(goods, goodsDesc, item);
            itemDao.insertSelective(item);

        }
    }

    // 设置库存属性
    private void setAttributeForItem(Goods goods, GoodsDesc goodsDesc, Item item) {
        // 图片
        String itemImages = goodsDesc.getItemImages();
        List<Map> images = JSON.parseArray(itemImages, Map.class);
        if (images != null && images.size() > 0) {
            String image = images.get(0).get("url").toString();
            item.setImage(image);
        }
        item.setCategoryid(goods.getCategory3Id()); // 三级分类id
        item.setStatus("1");        // 库存商品的状态
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setGoodsId(goods.getId());  // spu的id
        item.setSellerId(goods.getSellerId());  // 商家id
        item.setCategory(itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());  // 三级分类名称
        item.setBrand(brandDao.selectByPrimaryKey(goods.getBrandId()).getName());     // 品牌名称
        item.setSeller(sellerDao.selectByPrimaryKey(goods.getSellerId()).getNickName());   // 商家的店铺名称
    }

    /**
     * 查询商品列表信息
     *
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, Goods goods) {
        // 设置分页条件
        PageHelper.startPage(page, rows);
        // 设置查询条件
        GoodsQuery query = new GoodsQuery();
        query.setOrderByClause("id desc");
        if (goods.getSellerId() != null && !"".equals(goods.getSellerId().trim())) {
            query.createCriteria().andSellerIdEqualTo(goods.getSellerId().trim());
        }
        // 查询
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(query);
        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 商品回显
     *
     * @param id
     * @return
     */
    @Override
    public GoodsVo findOne(Long id) {
        GoodsVo goodsVo = new GoodsVo();
        // 商品信息
        Goods goods = goodsDao.selectByPrimaryKey(id);
        goodsVo.setGoods(goods);
        // 商品明细信息
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        goodsVo.setGoodsDesc(goodsDesc);
        // 商品对应的库存信息
        ItemQuery query = new ItemQuery();
        query.createCriteria().andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(query);
        goodsVo.setItemList(itemList);
        return goodsVo;
    }

    /**
     * 商品更新
     *
     * @param goodsVo
     */
    @Transactional
    @Override
    public void update(GoodsVo goodsVo) {
        // 更新商品
        Goods goods = goodsVo.getGoods();
        goodsDao.updateByPrimaryKeySelective(goods);
        // 更新商品明细
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDescDao.updateByPrimaryKeySelective(goodsDesc);
        // 更新商品对应的库存
        // 先删除
        ItemQuery query = new ItemQuery();
        query.createCriteria().andGoodsIdEqualTo(goods.getId());
        itemDao.deleteByExample(query);
        // 在插入
        if ("1".equals(goods.getIsEnableSpec())) {   // 启用规格
            List<Item> itemList = goodsVo.getItemList();
            for (Item item : itemList) {
                String title = goods.getGoodsName();
                item.getSpec();  // 规格属性
                Map<String, String> map = JSON.parseObject(item.getSpec(), Map.class);
                Set<Map.Entry<String, String>> entrySet = map.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    title += " " + entry.getValue();
                }
                item.setTitle(title);
                setAttributeForItem(goods, goodsDesc, item);
                itemDao.insertSelective(item);
            }
        } else {
            // 一个商品对应一个库存
            Item item = new Item();
            item.setTitle(goods.getGoodsName());   // 标题
            item.setNum(9999);  // 库存
            item.setIsDefault("1");  // 是否默认
            item.setStatus("1");
            item.setSpec("{}");  //规格
            setAttributeForItem(goods, goodsDesc, item);
            itemDao.insertSelective(item);
        }
    }

    /**
     * 运营商系统查询待审核的商品列表
     *
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @Override
    public PageResult searchByManager(Integer page, Integer rows, Goods goods) {
        // 设置分页条件
        PageHelper.startPage(page, rows);
        // 设置查询条件
        GoodsQuery query = new GoodsQuery();
        GoodsQuery.Criteria criteria = query.createCriteria();
        query.setOrderByClause("id desc");
        if (goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus().trim())) {
            criteria.andAuditStatusEqualTo(goods.getAuditStatus().trim());
        }
        criteria.andIsDeleteIsNull();   // 查询未删除的商品
        // 查询
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(query);
        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 商品审核
     *
     * @param ids
     * @param status
     */
    @Transactional
    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null && ids.length > 0) {
            Goods goods = new Goods();
            goods.setAuditStatus(status);
            for (Long id : ids) {
                goods.setId(id);
                goodsDao.updateByPrimaryKeySelective(goods);
                if ("1".equals(status)) {
                    // TODO:将商品保存到索引库中
                    // TODO:生成该商品详情的静态页
                }
            }
        }
    }

    /**
     * 删除商品
     *
     * @param ids
     */
    @Transactional
    @Override
    public void delete(Long[] ids) {
        if (ids != null && ids.length > 0) {
            Goods goods = new Goods();
            goods.setIsDelete("1");
            for (Long id : ids) {
                goods.setId(id);
                goodsDao.updateByPrimaryKeySelective(goods);
                // TODO:将商品保存到索引库中
                // TODO:生成该商品详情的静态页
            }
        }
    }
}
