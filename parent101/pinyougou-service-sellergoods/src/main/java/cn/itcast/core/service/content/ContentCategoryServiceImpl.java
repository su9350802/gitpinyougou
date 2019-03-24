package cn.itcast.core.service.content;

import cn.itcast.core.dao.ad.ContentCategoryDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.ad.ContentCategory;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Resource
    private ContentCategoryDao contentCategoryDao;

    @Override
    public List<ContentCategory> findAll() {
        return contentCategoryDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(ContentCategory contentCategory, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        Page<ContentCategory> p = (Page<ContentCategory>) contentCategoryDao.selectByExample(null);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public void add(ContentCategory contentCategory) {
        contentCategoryDao.insertSelective(contentCategory);
    }

    @Override
    public ContentCategory findOne(Long id) {
        return contentCategoryDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(ContentCategory contentCategory) {
        contentCategoryDao.updateByPrimaryKeySelective(contentCategory);
    }

    @Override
    public void delAll(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                contentCategoryDao.deleteByPrimaryKey(id);
            }
        }
    }
}
