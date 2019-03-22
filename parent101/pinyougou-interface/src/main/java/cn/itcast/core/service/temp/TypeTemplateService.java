package cn.itcast.core.service.temp;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {

    /**
     * 模板列表查询
     *
     * @param page
     * @param rows
     * @param typeTemplate
     * @return
     */
    PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate);

    /**
     * 保存模板
     * @param typeTemplate
     */
    void add(TypeTemplate typeTemplate);

    /**
     * 回显模板
     * @param id
     * @return
     */
    TypeTemplate findOne(Long id);

    /**
     * 更新模板
     * @param typeTemplate
     */
    void update(TypeTemplate typeTemplate);

    /**
     * 删除模板
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 新增分类时:加载模板列表
     * @return
     */
    List<TypeTemplate> findAll();

    /**
     * 新增商品选择三级分类确定模板：加载规格以及规格选项
     * @param id
     * @return
     */
    List<Map> findBySpecList(Long id);
}
