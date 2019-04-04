package cn.itcast.core.service.addr;

import cn.itcast.core.dao.address.AddressDao;
import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.AddressQuery;
import com.alibaba.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName AddrServiceImpl
 * @Description 实现收件人地址
 * @Author Ygkw
 * @Date 20:51 2019/4/3
 * @Version 2.1
 **/


@Service
public class AddrServiceImpl implements AddrService {

    @Resource
    private AddressDao addressDao;


    /**
     * @param userId
     * @return java.util.List<cn.itcast.core.pojo.address.Address>
     * @author 举个栗子
     * @Description 获取当前收货人地址列表
     * @Date 21:58 2019/4/3 
     **/

    @Override
    public List<Address> findListByLoginUser(String userId) {
        AddressQuery query = new AddressQuery();
        query.createCriteria().andUserIdEqualTo(userId);
        return addressDao.selectByExample(query);
    }
}

