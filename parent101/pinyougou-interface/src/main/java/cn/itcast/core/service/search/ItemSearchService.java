package cn.itcast.core.service.search;

import java.util.Map;

public interface ItemSearchService {

    /**
     * 前台系统检索
     * @param searchMap
     * @return
     */
    Map<String, Object> search(Map<String, String> searchMap);

    /**
     * @author 举个栗子
     * @Description 商品上架-保存到索引库
     * @Date 20:36 2019/3/28
      * @param id
     * @return void
     **/
    void addItemToSolr(Long id);
}
