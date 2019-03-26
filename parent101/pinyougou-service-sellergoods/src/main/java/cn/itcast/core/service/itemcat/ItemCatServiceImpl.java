package cn.itcast.core.service.itemcat;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 商品分类的列表查询
     *
     * @param parentId
     * @return
     */
    @Override
    public List<ItemCat> findByParentId(Long parentId) {

        // 将分类的数据写到缓存中
        List<ItemCat> itemCatList = itemCatDao.selectByExample(null);
        if (itemCatList != null && itemCatList.size() > 0) {
            for (ItemCat itemCat : itemCatList) {
                redisTemplate.boundHashOps("itemCatList").put(itemCat.getName(),itemCat.getTypeId());
            }
        }

        // 设置查询条件
        ItemCatQuery query = new ItemCatQuery();
        query.createCriteria().andParentIdEqualTo(parentId);
        List<ItemCat> itemCats = itemCatDao.selectByExample(query);
        return itemCats;
    }


    /**
     * 保存分类
     *
     * @param itemCat
     */
    @Override
    public void add(ItemCat itemCat) {
        itemCatDao.insertSelective(itemCat);
    }

    /**
     * 新增商品选择三级分类：加载模板
     *
     * @param id
     * @return
     */
    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    /**
     * 查询所有分类类别
     *
     * @return
     */
    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }
}
