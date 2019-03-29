package cn.itcast.core.service.staticpage;


import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName StaticPageServiceImpl
 * @Description 实现静态页接口
 * @Author Ygkw
 * @Date 15:28 2019/3/28
 * @Version 2.1
 **/
public class StaticPageServiceImpl implements StaticPageService,ServletContextAware {

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private GoodsDescDao goodsDescDao;

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private ItemDao itemDao;

    private Configuration configuration;

    // 注入FreeMarkerConfigurer，好处：获取configuration 指定模板位置
    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.configuration = freeMarkerConfigurer.getConfiguration();
    }

    private ServletContext servletContext;
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * @param id 商品id
     * @return void
     * @author 举个栗子
     * @Description 获取静态页的方法
     * @Date 15:26 2019/3/28
     **/
    @Override
    public void getHtml(Long id) {

        try {
            // 1.创建Configuration并且指定模板的位置
            // 2.获取该位置下的模板
            Template template = configuration.getTemplate("item.ftl");
            // 3.准备数据
            Map<String, Object> dataModel = getDataModel(id);
            String pathname = "/" + id + ".html";
            String path = servletContext.getRealPath(pathname);
            File file = new File(path);
            // 4.模板 + 数据 = 输出
            template.process(dataModel,new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取模板需要的数据
    private Map<String, Object> getDataModel(Long id) {
        Map<String ,Object> map = new HashMap<>();

        // 商品基本信息
        Goods goods = goodsDao.selectByPrimaryKey(id);
        map.put("goods",goods);

        // 商品描述信息
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        map.put("goodsDesc",goodsDesc);

        // 商品分类信息
        ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
        ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
        ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());
        map.put("itemCat1",itemCat1);
        map.put("itemCat2",itemCat2);
        map.put("itemCat3",itemCat3);

        // 商品库存信息
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        map.put("itemList",itemList);
        return map;
    }

}
