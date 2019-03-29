package cn.itcast.core.service.content;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.ad.ContentCategory;

import java.util.List;

public interface ContentCategoryService {

    List<ContentCategory> findAll();

    PageResult findPage(ContentCategory contentCategory, Integer page, Integer rows);

    void add(ContentCategory contentCategory);

    ContentCategory findOne(Long id);

    void update(ContentCategory contentCategory);

    void delAll(Long[] ids);
}
