package cn.itcast.core.service.addr;

import cn.itcast.core.pojo.address.Address;

import java.util.List;


/**
 * @ClassName AddrService
 * @Description 收件人地址
 * @Author Ygkw
 * @Date 20:27 2019/4/3
 * @Version 2.1
 **/

public interface AddrService {

    List<Address> findListByLoginUser(String userId);
}

