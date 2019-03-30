package cn.itcast.core.service.search;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Resource
    private SolrTemplate solrTemplate;

    @Resource
    private ItemDao itemDao;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 前台系统检索
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        // 创建一个大map，封装所有的结果集
        Map<String, Object> resultMap = new HashMap<>();
        // 处理关键字中包含的空格的问题
        String keywords = searchMap.get("keywords");
        if (keywords != null && !"".equals(keywords)) {
            keywords = keywords.replace(" ", "");
            searchMap.put("keywords", keywords);
        }
        // 1.根据关键字检索并且分页
        // Map<String, Object> map = searchForPage(searchMap);

        // 2.根据关键字检索并且分页  关键字进行高亮
        Map<String, Object> map = searchForHighLightPage(searchMap);
        resultMap.putAll(map);
        // 2.加载分类结果集
        List<String> categoryList = searchForGroupPage(searchMap);
        if (categoryList != null && categoryList.size() > 0) {
            resultMap.put("categoryList", categoryList);
            // 3.默认加载第一个分类下的品牌、规格结果集
            Map<String, Object> brandsAndSpecsMap = findBrandsAndSpecsByCategoryNameWithOne(categoryList.get(0));
            resultMap.putAll(brandsAndSpecsMap);
        }
        return resultMap;
    }

    /**
     * @param id
     * @return void
     * @author 举个栗子
     * @Description 商品上架-保存到索引库
     * @Date 20:37 2019/3/28
     **/
    @Override
    public void addItemToSolr(Long id) {
        ItemQuery itemQuery = new ItemQuery();
        // 条件：根据商品id查询对应的库存，并且库存大于0的
        itemQuery.createCriteria().andGoodsIdEqualTo(id).andStatusEqualTo("1")
                .andIsDefaultEqualTo("1").andNumGreaterThan(0);
        List<Item> items = itemDao.selectByExample(itemQuery);
        if (items != null && items.size() > 0) {
            // 设置动态字段：item_spec_内存32g
            for (Item item : items) {
                // 取出规格
                String spec = item.getSpec();
                Map<String, String> specMap = JSON.parseObject(spec, Map.class);
                item.setSpecMap(specMap);
            }
            solrTemplate.saveBeans(items);
            solrTemplate.commit();
        }
    }

    /**
     * @author 举个栗子
     * @Description 商品下架-从索引库删除数据
     * @Date 16:24 2019/3/30
      * @param id
     * @return void
     **/
    @Override
    public void deleteItemFromSolr(Long id) {
        SimpleQuery query = new SimpleQuery("item_goodsid:" + id);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    // 默认加载第一个分类下的品牌、规格结果集
    private Map<String, Object> findBrandsAndSpecsByCategoryNameWithOne(String categoryName) {
        Map<String, Object> map = new HashMap<>();
        // 通过分类名称获取模板id
        Object typeId = redisTemplate.boundHashOps("itemCatList").get(categoryName);
        // 通过模板id获取品牌结果集
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        map.put("brandList", brandList);
        // 通过模板id获取品牌规格集
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        map.put("specList", specList);
        return map;
    }

    // 查询商品的分类
    private List<String> searchForGroupPage(Map<String, String> searchMap) {
        // 设置关键字条件
        Criteria criteria = new Criteria("item_keywords");
        String keywords = searchMap.get("keywords");
        if (keywords != null && !"".equals(keywords)) {
            criteria.is(keywords);
        }
        SimpleQuery query = new SimpleQuery(criteria);

        // 2、设置分组条件
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        // 3、根据条件查询
        GroupPage<Item> groupPage = solrTemplate.queryForGroupPage(query, Item.class);

        // 4、将结果封装到list中
        List<String> list = new ArrayList<>();
        GroupResult<Item> groupResult = groupPage.getGroupResult("item_category");
        Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();
        for (GroupEntry<Item> groupEntry : groupEntries) {
            String groupValue = groupEntry.getGroupValue();
            list.add(groupValue);
        }
        return list;
    }

    // 根据关键字检索并且分页  关键字进行高亮---条件
    // 高亮：对检索的关键字添加HTML标签（自定义的标签，编写样式）
    private Map<String, Object> searchForHighLightPage(Map<String, String> searchMap) {
        // 1、设置检索关键字
        Criteria criteria = new Criteria("item_keywords");
        String keywords = searchMap.get("keywords");
        if (keywords != null && !"".equals(keywords)) {
            criteria.is(keywords);
        }
        SimpleHighlightQuery query = new SimpleHighlightQuery(criteria);
        filterCriteria(searchMap, query);
        // 2.设置分页条件
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        Integer start = (pageNo - 1) * pageSize;
        query.setOffset(start);    // 起始行
        query.setRows(pageSize);   // 每页显示的条数

        // 添加排序条件
        // 根据新品、价格排序
        String sort = searchMap.get("sort");
        if (sort != null && !"".equals(sort)) {
            if ("ASC".equals(sort)) {
                Sort s = new Sort(Sort.Direction.ASC, "item_" + searchMap.get("sortField"));
                query.addSort(s);
            } else {
                Sort s = new Sort(Sort.Direction.DESC, "item_" + searchMap.get("sortField"));
                query.addSort(s);
            }
        }

        // 3.设置高亮条件
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");    // 标题中包含的关键字需要高亮显示
        highlightOptions.setSimplePrefix("<font color='orange'>");
        highlightOptions.setSimplePostfix("</font>");
        query.setHighlightOptions(highlightOptions);

        // 4.根据条件检索
        HighlightPage<Item> highlightPage = solrTemplate.queryForHighlightPage(query, Item.class);
        // 处理高亮的结果集
        List<HighlightEntry<Item>> highlighted = highlightPage.getHighlighted();
        if (highlighted != null && highlighted.size() > 0) {
            for (HighlightEntry<Item> highlightEntry : highlighted) {
                Item item = highlightEntry.getEntity();  // 普通的结果
                List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
                if (highlights != null && highlights.size() > 0) {
                    String title = highlights.get(0).getSnipplets().get(0);
                    item.setTitle(title);
                }
            }
        }
        // 5.封装结果集到map中
        Map<String, Object> map = new HashMap<>();
        map.put("totalPages", highlightPage.getTotalPages());   // 总页数
        map.put("total", highlightPage.getTotalElements());     // 总条数
        map.put("rows", highlightPage.getContent());            // 结果集
        return map;
    }

    // 根据关键字检索并且分页
    private Map<String, Object> searchForPage(Map<String, String> searchMap) {

        // 1、设置检索的条件
        Criteria criteria = new Criteria("item_keywords");
        String keywords = searchMap.get("keywords");
        if (keywords != null && !"".equals(keywords)) {
            criteria.is(keywords);  // is不是等于某个值。is方法：根据词条进行模糊检索
        }
        SimpleQuery query = new SimpleQuery(criteria);
        // 2.设置分页条件
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        Integer start = (pageNo - 1) * pageSize;
        query.setOffset(start);   // 起始行
        query.setRows(pageSize);  // 每页显示的条数

        // 3.根据条件查询
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);

        //4.封装结果集到map中
        Map<String, Object> map = new HashMap<>();
        map.put("totalPages", scoredPage.getTotalPages());  // 总页
        map.put("total", scoredPage.getTotalElements());    // 总条
        map.put("rows", scoredPage.getContent());            // 结果集
        return map;
    }

    //  条件过滤
    public void filterCriteria(Map<String, String> searchMap, Query query) {

        // 根据分类过滤
        if (searchMap.get("category") != null && !"".equals(searchMap.get("category"))) {
            Criteria criteria = new Criteria("item_category");
            criteria.is(searchMap.get("category"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria);
            query.addFilterQuery(filterQuery);
        }

        // 根据品牌过滤
        if (searchMap.get("brand") != null && !"".equals(searchMap.get("brand"))) {
            Criteria criteria = new Criteria("item_brand");
            criteria.is(searchMap.get("brand"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria);
            query.addFilterQuery(filterQuery);
        }

        // 根据价格过滤
        if (searchMap.get("price") != null && !"".equals(searchMap.get("price"))) {
            String[] prices = searchMap.get("price").split("-");
            Criteria criteria = new Criteria("item_price");
            if (searchMap.get("price").contains("*")) {
                criteria.greaterThan(prices[0]);
            } else {
                criteria.between(prices[0], prices[1], true, true);
            }
            FilterQuery filterQuery = new SimpleFilterQuery(criteria);
            query.addFilterQuery(filterQuery);
        }

        // 根据商品规格过滤
        if (searchMap.get("spec") != null && !"".equals(searchMap.get("spec"))) {
            Map<String, String> specMap = JSON.parseObject(searchMap.get("spec"), Map.class);
            Set<Map.Entry<String, String>> entrySet = specMap.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                Criteria criteria = new Criteria("item_spec_" + entry.getKey());
                criteria.is(entry.getValue());
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                query.addFilterQuery(filterQuery);
            }
        }
    }
}
