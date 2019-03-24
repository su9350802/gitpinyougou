package cn.itcast.core.service.content;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentDao contentDao;

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
        contentDao.insertSelective(content);
    }

    @Override
    public Content findOne(Long id) {
        return contentDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Content content) {
        contentDao.updateByPrimaryKeySelective(content);
    }

    @Override
    public void delAll(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                contentDao.deleteByPrimaryKey(id);
            }
        }
    }

    /**
     * 首页大广告轮播图
     * @param categoryId
     * @return
     */
    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        ContentQuery query = new ContentQuery();
        query.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
        query.setOrderByClause("sort_order desc");
        return contentDao.selectByExample(query);
    }
}
