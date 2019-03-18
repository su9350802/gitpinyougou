package cn.itcast.core.service.spec;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.vo.SpecVo;

public interface SpecService {

    /**
     * 规格列表查询
     * @param page
     * @param rows
     * @param specification
     * @return
     */
    PageResult search(Integer page, Integer rows, Specification specification);

    /**
     * 保存规格
     * @param specVo
     */
    void add(SpecVo specVo);

    /**
     * 规格回显
     * @param id
     * @return
     */
    SpecVo findOne(Long id);

    /**
     * 更新规格
     * @param specVo
     */
    void update(SpecVo specVo);

    /**
     * 删除规格
     * @param ids
     */
    void delete(Long[] ids);
}
