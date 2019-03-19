package cn.itcast.core.service.spec;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import cn.itcast.core.vo.SpecVo;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class SpecServiceImpl implements SpecService {

    // 注入dao
    @Resource
    private SpecificationDao specificationDao;

    @Resource
    private SpecificationOptionDao specificationOptionDao;

    /**
     * 规格列表查询
     *
     * @param page
     * @param rows
     * @param specification
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {
        // 1.设置分页条件
        PageHelper.startPage(page, rows);
        // 2.设置查询条件
        SpecificationQuery query = new SpecificationQuery();
        if (specification.getSpecName() != null && !"".equals(specification.getSpecName().trim())) {
            query.createCriteria().andSpecNameLike("%" + specification.getSpecName().trim() + "%");
        }
        query.setOrderByClause("id desc");
        // 3.查询
        Page<Specification> p = (Page<Specification>) specificationDao.selectByExample(query);
        // 4.封装结果
        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 保存规格
     *
     * @param specVo
     */
    @Transactional
    @Override
    public void add(SpecVo specVo) {
        // 保存规格：插入数据后需要返回自增主键的id
        Specification specification = specVo.getSpecification();
        specificationDao.insertSelective(specification);  // 返回自增主键id
        // 保存规格选项：外键spec_id
        List<SpecificationOption> specificationOptionList = specVo.getSpecificationOptionList();
        if (specificationOptionList != null && specificationOptionList.size() > 0) {
            for (SpecificationOption specificationOption : specificationOptionList) {
                // 设置外键：specId
                specificationOption.setSpecId(specification.getId());
                // specificationOptionDao.insertSelective(specificationOption);
            }
            // 插入：报表数据导入到数据库中（一条条插入：内存溢出） 批量溢出
            specificationOptionDao.insertSelectives(specificationOptionList);
        }
    }

    /**
     * 规格回显
     *
     * @param id
     * @return
     */
    @Override
    public SpecVo findOne(Long id) {
        // 获取规格
        Specification specification = specificationDao.selectByPrimaryKey(id);
        // 获取规格选项
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        query.createCriteria().andSpecIdEqualTo(id);
        List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(query);
        // 封装到Vo中
        SpecVo specVo = new SpecVo();
        specVo.setSpecification(specification);
        specVo.setSpecificationOptionList(specificationOptionList);
        return specVo;
    }

    /**
     * 更新规格
     *
     * @param specVo
     */
    @Transactional
    @Override
    public void update(SpecVo specVo) {
        // 更新规格
        Specification specification = specVo.getSpecification();
        specificationDao.updateByPrimaryKeySelective(specification);
        // 更新规格选项
        // 先删除：根据外键删除
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        query.createCriteria().andSpecIdEqualTo(specification.getId());
        specificationOptionDao.deleteByExample(query);
        // 再插入
        List<SpecificationOption> specificationOptionList = specVo.getSpecificationOptionList();
        if (specificationOptionList != null && specificationOptionList.size() > 0) {
            for (SpecificationOption specificationOption : specificationOptionList) {
                // 设置外键：specId
                specificationOption.setSpecId(specification.getId());
            }
            // 批量插入
            specificationOptionDao.insertSelectives(specificationOptionList);
        }
    }

    /**
     * 删除规格
     *
     * @param ids
     */
    @Transactional
    @Override
    public void delete(Long[] ids) {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                // 删除规格选项
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                query.createCriteria().andSpecIdEqualTo(id);
                specificationOptionDao.deleteByExample(query);
                // 删除规格
                specificationDao.deleteByPrimaryKey(id);
            }
        }
    }

    /**
     * 新增模板：初始化规格的下拉框列表数据
     * @return
     */
    @Override
    public List<Map<String, String>> selectOptionList() {
        return specificationDao.selectOptionList();
    }
}
