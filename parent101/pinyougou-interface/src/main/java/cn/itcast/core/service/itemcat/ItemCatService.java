package cn.itcast.core.service.itemcat;

import cn.itcast.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {

    /**
     * 商品分类的列表查询
     * @param parentId
     * @return
     */
    List<ItemCat> findByParentId(Long parentId);

    /**
     * 保存分类
     * @param itemCat
     */
    void add(ItemCat itemCat);

    /**
     * 新增商品选择三级分类：加载模板
     * @param id
     * @return
     */
    ItemCat findOne(Long id);

    /**
     * 查询所有分类类别
     * @return
     */
    List<ItemCat> findAll();

}
