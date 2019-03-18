package cn.itcast.core.service.brand;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Resource
    private BrandDao brandDao;

    @Override
    public List<Brand> findAll() {
        // 根据条件查询
        List<Brand> brands = brandDao.selectByExample(null);
        return brands;
    }

    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 进行查询
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(null);

        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        // 创建result
        return pageResult;
    }


    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Brand brand) {
        // 1.设置分页条件
        PageHelper.startPage(pageNum, pageSize);
        // 2.设置查询条件：封装查询条件对象
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();

        // 拼接sql语句
        if (brand.getName() != null && !"".equals(brand.getName().trim())) {
            criteria.andNameLike("%" + brand.getName().trim() + "%");
        }
        if (brand.getFirstChar() != null && !"".equals(brand.getFirstChar().trim())) {
            criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
        }

        // 根据id排序
        brandQuery.setOrderByClause("id desc");

        // 3.进行查询
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);
        // 4.将结果封装到PageResult中
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    @Override
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
    }

    /**
     * 根据id修改回显数据
     *
     * @param id
     * @return
     */
    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    /**
     * 更新品牌
     *
     * @param brand
     */
    @Transactional
    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Transactional
    @Override
    public void delete(Long[] ids) {
        if (ids != null && ids.length > 0) {
            // for (Long id : ids) {
            //     brandDao.deleteByPrimaryKey(id);
            // }
            brandDao.deleteByPrimaryKeys(ids);
        }
    }
}
