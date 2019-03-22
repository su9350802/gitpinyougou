package cn.itcast.core.controller.temp;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.temp.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    /**
     * 模板回显
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public TypeTemplate findOne(Long id) {
        return typeTemplateService.findOne(id);
    }

    /**
     * 新增商品选择三级分类确定模板：加载规格以及规格选项
     * @param id
     * @return
     */
    @RequestMapping("/findBySpecList.do")
    public List<Map> findBySpecList(Long id) {
        return typeTemplateService.findBySpecList(id);
    }
}
