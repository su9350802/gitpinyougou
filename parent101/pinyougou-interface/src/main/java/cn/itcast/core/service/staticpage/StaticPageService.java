package cn.itcast.core.service.staticpage;

/**
 * @ClassName StaticPageService
 * @Description 生成静态页的接口
 * @Author Ygkw
 * @Date 15:23 2019/3/28
 * @Version 2.1
 **/
public interface StaticPageService {

    /**
     * @author 举个栗子
     * @Description 获取静态页的方法
     * @Date 15:26 2019/3/28
      * @param id 商品id
     * @return void
     **/
    void getHtml(Long id);
}
