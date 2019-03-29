package cn.itcast.core.service.content;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Resource
    private ContentDao contentDao;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public List<Content> findAll() {
        List<Content> contentList = contentDao.selectByExample(null);
        return contentList;
    }

    @Override
    public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Content> page = (Page<Content>) contentDao.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(Content content) {
        // 缓存同步：清空缓存
        clearCache(content.getCategoryId());
        contentDao.insertSelective(content);
    }

    // 清空缓存的数据
    private void clearCache(Long categoryId) {
        redisTemplate.boundHashOps("content").delete(categoryId);
    }

    @Override
    public Content findOne(Long id) {
        return contentDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Content content) {
        // 需要判断广告的分类是否发生改变
        // 如果分类发生改变：删除之前数据、删除现在更新的分类数据
        Long newCategoryId = content.getCategoryId();
        Long oldCategoryId = contentDao.selectByPrimaryKey(content.getId()).getCategoryId();
        if (newCategoryId != oldCategoryId) {
            // 分类发生改变
            clearCache(newCategoryId);
            clearCache(oldCategoryId);
        } else {
            clearCache(newCategoryId);
        }
        contentDao.updateByPrimaryKeySelective(content);
    }

    @Override
    public void delAll(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                // 缓存同步：清空缓存
                clearCache(contentDao.selectByPrimaryKey(id).getCategoryId());
                contentDao.deleteByPrimaryKey(id);
            }
        }
    }

    /**
     * 首页大广告轮播图
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        // 采用Redis的哪种数据结构
        // String set key value  key:categoryId value:数据
        // Redis优化之一：减少与redis客户端交互的次数(n个广告分类，交互n次)
        // 优化：hash  hset ket (map)filed value  交互一次
        // 首先判断缓存中是否有数据
        List<Content> list = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
        synchronized (this) {
            if (list == null) {
                // 继续判断缓存中是否有数据
                list = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
                if (list == null) {
                    // 缓存中没有，从数据库中查询
                    ContentQuery query = new ContentQuery();  // 设置条件：根据广告分类查询，并且是可用的
                    query.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
                    query.setOrderByClause("sort_order desc");
                    list = contentDao.selectByExample(query);
                    // 数据放入缓存
                    redisTemplate.boundHashOps("content").put(categoryId, list);
                }
            }
        }
        return list;
    }
}
