package cn.itcast.core.service.brand;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {

   /**
    * 品牌查询不分页
    * @return
    */
   List<Brand> findAll();

   /**
    * 品牌分页查询
    * @param pageNum
    * @param pageSize
    * @return
    */
   PageResult findPage(Integer pageNum,Integer pageSize);

   /**
    * 品牌分页条件查询
    * @param pageNum
    * @param pageSize
    * @param brand
    * @return
    */
   PageResult search(Integer pageNum,Integer pageSize,Brand brand);

   /**
    * 添加品牌
    * @param brand
    */
   void add(Brand brand);

   /**
    * 根据id修改回显数据
    * @param id
    * @return
    */
   Brand findOne(Long id);

   /**
    * 更新品牌
    * @param brand
    */
   void update(Brand brand);

   /**
    * 批量删除
    * @param ids
    */
   void delete(Long[] ids);

   /**
    * 新增模板：初始化品牌的下拉框列表数据
    * @return
    */
   List<Map<String,String>> selectOptionList();


}
