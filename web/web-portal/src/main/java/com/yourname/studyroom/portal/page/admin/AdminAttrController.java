package com.yourname.studyroom.portal.page.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yourname.studyroom.model.entity.AttrKey;
import com.yourname.studyroom.model.entity.AttrValue;
import com.yourname.studyroom.web.admin.service.AttrKeyService;
import com.yourname.studyroom.web.admin.service.AttrValueService;
import com.yourname.studyroom.web.admin.vo.attr.AttrKeyVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 管理端座位属性管理：属性名/属性值的增删改
 */
@Controller
@RequestMapping("/admin/attr")
public class AdminAttrController {

    private final AttrKeyService attrKeyService;
    private final AttrValueService attrValueService;
    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    public AdminAttrController(AttrKeyService attrKeyService,
                               AttrValueService attrValueService,
                               org.springframework.data.redis.core.StringRedisTemplate redisTemplate) {
        this.attrKeyService = attrKeyService;
        this.attrValueService = attrValueService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 属性数据被座位详情缓存引用：任何增删改后清除座位详情缓存，保证用户端及时生效
     */
    private void evictSeatDetailCache() {
        java.util.Set<String> keys = redisTemplate.keys("app:seat:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @GetMapping
    public String list(Model model) {
        List<AttrKeyVo> list = attrKeyService.listAttrInfo();
        model.addAttribute("attrKeys", list);
        return "admin/attr";
    }

    @PostMapping("/key/save")
    public String saveKey(@RequestParam(required = false) Long id,
                          @RequestParam String name,
                          RedirectAttributes ra) {
        AttrKey attrKey = new AttrKey();
        attrKey.setId(id);
        attrKey.setName(name);
        attrKeyService.saveOrUpdate(attrKey);
        evictSeatDetailCache();
        ra.addFlashAttribute("msg", "属性名已保存");
        return "redirect:/admin/attr";
    }

    @PostMapping("/key/delete/{id}")
    public String deleteKey(@PathVariable Long id, RedirectAttributes ra) {
        attrKeyService.removeById(id);
        LambdaQueryWrapper<AttrValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrValue::getAttrKeyId, id);
        attrValueService.remove(wrapper);
        evictSeatDetailCache();
        ra.addFlashAttribute("msg", "属性名及其属性值已删除");
        return "redirect:/admin/attr";
    }

    @PostMapping("/value/save")
    public String saveValue(@RequestParam(required = false) Long id,
                            @RequestParam Long attrKeyId,
                            @RequestParam String name,
                            RedirectAttributes ra) {
        AttrValue attrValue = new AttrValue();
        attrValue.setId(id);
        attrValue.setAttrKeyId(attrKeyId);
        attrValue.setName(name);
        attrValueService.saveOrUpdate(attrValue);
        evictSeatDetailCache();
        ra.addFlashAttribute("msg", "属性值已保存");
        return "redirect:/admin/attr";
    }

    @PostMapping("/value/delete/{id}")
    public String deleteValue(@PathVariable Long id, RedirectAttributes ra) {
        attrValueService.removeById(id);
        evictSeatDetailCache();
        ra.addFlashAttribute("msg", "属性值已删除");
        return "redirect:/admin/attr";
    }
}
