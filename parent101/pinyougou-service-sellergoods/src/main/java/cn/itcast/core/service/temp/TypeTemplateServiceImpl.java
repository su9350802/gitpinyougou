package cn.itcast.core.service.temp;

import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Resource
    private TypeTemplateDao typeTemplateDao;

    /**
     * 模板列表查询
     *
     * @param page
     * @param rows
     * @param typeTemplate
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) {
        // 1.设置分页条件
        PageHelper.startPage(page, rows);
        // 2.设置查询条件
        TypeTemplateQuery query = new TypeTemplateQuery();
        if (typeTemplate.getName() != null && !"".equals(typeTemplate.getName().trim())) {
            query.createCriteria().andNameLike("%" + typeTemplate.getName().trim() + "%");
        }
        // 3.查询
        Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(query);
        // 4.封装结果集
        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 保存模板
     *
     * @param typeTemplate
     */
    @Transactional
    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    /**
     * 回显模板
     *
     * @param id
     * @return
     */
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    /**
     * 更新模板
     *
     * @param typeTemplate
     */
    @Transactional
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }

    /**
     * 删除模板
     *
     * @param ids
     */
    @Transactional
    @Override
    public void delete(Long[] ids) {
        if (ids != null && ids.length > 0) {
            typeTemplateDao.deleteByPrimaryKeys(ids);
        }
    }
}
