package cn.itcast.core.controller.addr;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.service.addr.AddrService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName AddrController
 * @Description 收货人地址
 * @Author Ygkw
 * @Date 22:01 2019/4/3
 * @Version 2.1
 **/

@RestController
@RequestMapping("/address")
public class AddrController {

    @Reference
    private AddrService addrService;

    @RequestMapping("/findListByLoginUser.do")
    public List<Address> findListByLoginUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return addrService.findListByLoginUser(userId);
    }

}

