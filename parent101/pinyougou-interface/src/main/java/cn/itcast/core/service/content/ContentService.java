package cn.itcast.core.service.content;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.ad.Content;

import java.util.List;

public interface ContentService {

    List<Content> findAll();

    PageResult findPage(Content content, Integer pageNum, Integer pageSize);

    void add(Content content);

    Content findOne(Long id);

    void update(Content content);

    void delAll(Long[] ids);

    /**
     * 首页大广告轮播图
     * @param categoryId
     * @return
     */
    List<Content> findByCategoryId(Long categoryId);
}
