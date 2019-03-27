package cn.itcast.core.task;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RedisTask
 * @Description 定时任务
 * @Author Ygkw
 * @Date 19:52 2019/3/27
 * @Version 2.1
 **/
@Component
public class RedisTask {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private TypeTemplateDao typeTemplateDao;

    @Resource
    private SpecificationOptionDao specificationOptionDao;

    /**
     * @author 举个栗子
     * @Description 将分类的数据写到缓存中
     * @Date 20:09 2019/3/27
      * @param
     * @return void
     **/
    @Scheduled(cron = "00 20 20 27 03 *")
    public void autoItemToRedis() {
        List<ItemCat> itemCatList = itemCatDao.selectByExample(null);
        if (itemCatList != null && itemCatList.size() > 0) {
            for (ItemCat itemCat : itemCatList) {
                redisTemplate.boundHashOps("itemCatList").put(itemCat.getName(),itemCat.getTypeId());
            }
        }
    }

    /**
     * @author 举个栗子
     * @Description 将模板的数据写到缓存中
     * @Date 20:09 2019/3/27
      * @param
     * @return void
     **/
    @Scheduled(cron = "00 20 20 27 03 *")
    public void autoTempToRedis() {
        List<TypeTemplate> typeTemplateList = typeTemplateDao.selectByExample(null);
        if (typeTemplateList != null && typeTemplateList.size() > 0) {
            for (TypeTemplate typeTemplate : typeTemplateList) {
                String brandIds = typeTemplate.getBrandIds();
                List<Map> brandList = JSON.parseArray(brandIds, Map.class);
                // 品牌结果集写入缓存
                redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(),brandList);
                // 规格结果集写入缓存(规格与规格选项)
                List<Map> specList = findBySpecList(typeTemplate.getId());
                redisTemplate.boundHashOps("specList").put(typeTemplate.getId(),specList);
            }
        }
    }

    public List<Map> findBySpecList(Long id) {
        // 通过模板id获取规格
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        String specIds = typeTemplate.getSpecIds();
        List<Map> specList = JSON.parseArray(specIds, Map.class);
        // 通过规格id获取规格选项
        if (specList != null && specList.size() > 0) {
            for (Map map : specList) {
                Long specId = Long.parseLong(map.get("id").toString());
                // 通过规格id获取规格选项
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                query.createCriteria().andSpecIdEqualTo(specId);
                List<SpecificationOption> options = specificationOptionDao.selectByExample(query);
                map.put("options",options);
            }
        }
        return specList;
    }
}
