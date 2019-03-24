package cn.itcast.core.controller.content;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.service.content.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    @RequestMapping("/findAll.do")
    public List<Content> findAll() {
        List<Content> contentList = contentService.findAll();
        return contentList;
    }

    @RequestMapping("/add.do")
    public Result add(@RequestBody Content content) throws Exception {
        try {
            contentService.add(content);
            return new Result(true, "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败!");
        }
    }

    @RequestMapping("/findOne.do")
    public Content findOne(Long id) {
        return contentService.findOne(id);
    }

    @RequestMapping("/update.do")
    public Result update(@RequestBody Content content) {
        try {
            contentService.update(content);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    @RequestMapping("/delete.do")
    public Result delete(Long[] ids) {
        try {
            contentService.delAll(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody Content content, Integer page, Integer rows) throws Exception {
        PageResult pageResult = contentService.findPage(content, page, rows);
        return pageResult;
    }
}
