package cn.itcast.core.service.brand;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;

import java.util.List;

public interface BrandService {

   List<Brand> findAll();

   public PageResult findPage(Integer pageNum,Integer pageSize);

   public PageResult search(Integer pageNum,Integer pageSize,Brand brand);

   void add(Brand brand);
}
