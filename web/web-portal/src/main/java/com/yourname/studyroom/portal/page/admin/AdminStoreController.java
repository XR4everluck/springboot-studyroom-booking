package com.yourname.studyroom.portal.page.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.model.entity.CityInfo;
import com.yourname.studyroom.model.entity.DistrictInfo;
import com.yourname.studyroom.model.entity.GraphInfo;
import com.yourname.studyroom.model.entity.ProvinceInfo;
import com.yourname.studyroom.model.enums.ItemType;
import com.yourname.studyroom.model.enums.ReleaseStatus;
import com.yourname.studyroom.web.admin.service.CityInfoService;
import com.yourname.studyroom.web.admin.service.DistrictInfoService;
import com.yourname.studyroom.web.admin.service.FacilityInfoService;
import com.yourname.studyroom.web.admin.service.FeeValueService;
import com.yourname.studyroom.web.admin.service.LabelInfoService;
import com.yourname.studyroom.web.admin.service.ProvinceInfoService;
import com.yourname.studyroom.web.admin.service.StudyStoreService;
import com.yourname.studyroom.web.admin.vo.graph.GraphVo;
import com.yourname.studyroom.web.admin.vo.store.StoreDetailVo;
import com.yourname.studyroom.web.admin.vo.store.StoreSubmitVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理端门店管理：列表、新增/编辑、发布状态、删除
 */
@Controller
@RequestMapping("/admin/store")
public class AdminStoreController {

    private final StudyStoreService studyStoreService;
    private final DistrictInfoService districtInfoService;
    private final CityInfoService cityInfoService;
    private final ProvinceInfoService provinceInfoService;
    private final FacilityInfoService facilityInfoService;
    private final LabelInfoService labelInfoService;
    private final FeeValueService feeValueService;

    public AdminStoreController(StudyStoreService studyStoreService,
                                DistrictInfoService districtInfoService,
                                CityInfoService cityInfoService,
                                ProvinceInfoService provinceInfoService,
                                FacilityInfoService facilityInfoService,
                                LabelInfoService labelInfoService,
                                FeeValueService feeValueService) {
        this.studyStoreService = studyStoreService;
        this.districtInfoService = districtInfoService;
        this.cityInfoService = cityInfoService;
        this.provinceInfoService = provinceInfoService;
        this.facilityInfoService = facilityInfoService;
        this.labelInfoService = labelInfoService;
        this.feeValueService = feeValueService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") long current,
                       @RequestParam(defaultValue = "8") long size,
                       Model model) {
        model.addAttribute("page", studyStoreService.pageItem(new Page<>(current, size), new com.yourname.studyroom.web.admin.vo.store.StoreQueryVo()));
        return "admin/store-list";
    }

    /**
     * 新增/编辑表单：编辑时回显已选设施、标签、杂费与封面图
     */
    @GetMapping({"/form", "/form/{id}"})
    public String form(@PathVariable(required = false) Long id, Model model) {
        StoreSubmitVo store = new StoreSubmitVo();
        List<Long> facilityIds = List.of();
        List<Long> labelIds = List.of();
        List<Long> feeValueIds = List.of();
        String imageUrl = null;
        if (id != null) {
            StoreDetailVo detail = studyStoreService.getDetailById(id);
            store.setId(detail.getId());
            store.setName(detail.getName());
            store.setIntroduction(detail.getIntroduction());
            store.setDistrictId(detail.getDistrictId());
            store.setAddressDetail(detail.getAddressDetail());
            store.setPhone(detail.getPhone());
            store.setBusinessHours(detail.getBusinessHours());
            store.setIsRelease(detail.getIsRelease());
            facilityIds = detail.getFacilityInfoList() == null ? List.of()
                    : detail.getFacilityInfoList().stream().map(f -> f.getId()).toList();
            labelIds = detail.getLabelInfoList() == null ? List.of()
                    : detail.getLabelInfoList().stream().map(l -> l.getId()).toList();
            feeValueIds = detail.getFeeValueVoList() == null ? List.of()
                    : detail.getFeeValueVoList().stream().map(f -> f.getId()).toList();
            if (detail.getGraphVoList() != null && !detail.getGraphVoList().isEmpty()) {
                imageUrl = detail.getGraphVoList().get(0).getUrl();
            }
        }
        model.addAttribute("store", store);
        model.addAttribute("facilityIds", facilityIds);
        model.addAttribute("labelIds", labelIds);
        model.addAttribute("feeValueIds", feeValueIds);
        model.addAttribute("imageUrl", imageUrl);
        // 区县下拉选项：id -> 「省 / 市 / 区」完整路径标签（服务端拼装，页面无需脚本联动）
        java.util.Map<Long, String> districtLabels = new java.util.LinkedHashMap<>();
        for (DistrictInfo d : districtInfoService.list()) {
            StringBuilder label = new StringBuilder(d.getName() == null ? "" : d.getName());
            CityInfo city = d.getCityId() == null ? null : cityInfoService.getById(d.getCityId().longValue());
            if (city != null) {
                label.insert(0, city.getName() + " / ");
                ProvinceInfo province = city.getProvinceId() == null ? null
                        : provinceInfoService.getById(city.getProvinceId().longValue());
                if (province != null) {
                    label.insert(0, province.getName() + " / ");
                }
            }
            districtLabels.put(d.getId().longValue(), label.toString());
        }
        model.addAttribute("districtLabels", districtLabels);
        model.addAttribute("facilities", facilityInfoService.list());
        model.addAttribute("labels", labelInfoService.list());
        model.addAttribute("feeValues", feeValueService.list());
        return "admin/store-form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String name,
                       @RequestParam(required = false) String introduction,
                       @RequestParam(required = false) String phone,
                       @RequestParam(required = false) String businessHours,
                       @RequestParam(required = false) String addressDetail,
                       @RequestParam Long districtId,
                       @RequestParam(required = false) String imageUrl,
                       @RequestParam(required = false) List<Long> facilityIds,
                       @RequestParam(required = false) List<Long> labelIds,
                       @RequestParam(required = false) List<Long> feeValueIds,
                       RedirectAttributes ra) {
        StoreSubmitVo store = new StoreSubmitVo();
        store.setId(id);
        store.setName(name);
        store.setIntroduction(introduction);
        store.setPhone(phone);
        store.setBusinessHours(businessHours);
        store.setAddressDetail(addressDetail);
        store.setIsRelease(ReleaseStatus.RELEASED);
        // 由区县反推出城市与省份（表单只让用户选区县，避免三联动需要脚本）
        DistrictInfo district = districtInfoService.getById(districtId);
        if (district != null) {
            store.setDistrictId(district.getId().longValue());
            store.setDistrictName(district.getName());
            CityInfo city = district.getCityId() == null ? null
                    : cityInfoService.getById(district.getCityId().longValue());
            if (city != null) {
                store.setCityId(city.getId().longValue());
                store.setCityName(city.getName());
                ProvinceInfo province = city.getProvinceId() == null ? null
                        : provinceInfoService.getById(city.getProvinceId().longValue());
                if (province != null) {
                    store.setProvinceId(province.getId().longValue());
                    store.setProvinceName(province.getName());
                }
            }
        }
        store.setFacilityInfoIds(facilityIds);
        store.setLabelInfoIds(labelIds);
        store.setFeeValueIds(feeValueIds);
        if (imageUrl != null && !imageUrl.isBlank()) {
            GraphVo graph = new GraphVo();
            graph.setName("门店封面");
            graph.setUrl(imageUrl.trim());
            List<GraphVo> graphs = new ArrayList<>();
            graphs.add(graph);
            store.setGraphVoList(graphs);
        }
        studyStoreService.saveOrUpdateStore(store);
        ra.addFlashAttribute("msg", "门店已保存");
        return "redirect:/admin/store";
    }

    @PostMapping("/release/{id}")
    public String release(@PathVariable Long id,
                          @RequestParam boolean release,
                          RedirectAttributes ra) {
        StoreSubmitVo patch = new StoreSubmitVo();
        patch.setId(id);
        patch.setIsRelease(release ? ReleaseStatus.RELEASED : ReleaseStatus.NOT_RELEASED);
        studyStoreService.updateById(patch);
        ra.addFlashAttribute("msg", release ? "门店已发布" : "门店已下架");
        return "redirect:/admin/store";
    }

    /**
     * 删除确认页（无 JS 架构下的防误删中转）
     */
    @GetMapping("/delete/{id}/confirm")
    public String deleteConfirm(@PathVariable Long id, Model model) {
        com.yourname.studyroom.model.entity.StudyStore store = studyStoreService.getById(id);
        model.addAttribute("confirmTitle", "删除门店");
        model.addAttribute("confirmMessage", store == null
                ? "该门店不存在，可能已被删除。"
                : "确认删除门店「" + store.getName() + "」？删除前需先清空门店下的座位；门店的标签、设施、图片关联将一并清除。");
        model.addAttribute("confirmAction", "/admin/store/delete/" + id);
        model.addAttribute("confirmButtonText", "确认删除门店");
        model.addAttribute("cancelUrl", "/admin/store");
        return "confirm";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            studyStoreService.removeStoreById(id);
            ra.addFlashAttribute("msg", "门店已删除");
        } catch (BookingException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/store";
    }
}
