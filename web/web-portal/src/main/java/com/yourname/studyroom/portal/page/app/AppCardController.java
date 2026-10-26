package com.yourname.studyroom.portal.page.app;

import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.model.entity.CardRechargeRecord;
import com.yourname.studyroom.model.entity.MemberCard;
import com.yourname.studyroom.web.admin.service.StudyStoreService;
import com.yourname.studyroom.web.app.service.MemberCardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户端会员卡：我的会员卡列表、办卡/充值
 * （卡归属强制以登录用户为准，不能替他人充值或办卡）
 */
@Controller
public class AppCardController {

    private final MemberCardService appMemberCardService;
    private final StudyStoreService adminStudyStoreService;

    public AppCardController(MemberCardService appMemberCardService,
                             StudyStoreService adminStudyStoreService) {
        this.appMemberCardService = appMemberCardService;
        this.adminStudyStoreService = adminStudyStoreService;
    }

    @GetMapping("/app/card")
    public String list(Model model) {
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        List<MemberCard> cards = appMemberCardService.listUsableByUserId(userId);
        model.addAttribute("cards", cards);
        model.addAttribute("stores", adminStudyStoreService.list());
        return "app/card";
    }

    @PostMapping("/app/card/recharge")
    public String recharge(@RequestParam(required = false) Long cardId,
                           @RequestParam(required = false) Long storeId,
                           @RequestParam(required = false) String cardType,
                           @RequestParam Integer quantity,
                           @RequestParam BigDecimal amount,
                           RedirectAttributes ra) {
        if (quantity == null || quantity <= 0 || amount == null || amount.signum() < 0) {
            ra.addFlashAttribute("error", "充值额度或金额不合法");
            return "redirect:/app/card";
        }
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        MemberCard card;
        if (cardId != null) {
            // 给已有卡充值：加载完整实体并校验归属
            MemberCard existing = appMemberCardService.getById(cardId);
            if (existing == null || !userId.equals(existing.getUserId())) {
                ra.addFlashAttribute("error", "会员卡不存在");
                return "redirect:/app/card";
            }
            card = existing;
        } else {
            card = new MemberCard();
            card.setUserId(userId);
            card.setStoreId(storeId);
            card.setCardType(convertCardType(cardType));
            if (card.getCardType() == null) {
                ra.addFlashAttribute("error", "卡类型不合法");
                return "redirect:/app/card";
            }
        }
        CardRechargeRecord record = new CardRechargeRecord();
        record.setRechargeQuantity(quantity);
        record.setAmount(amount);
        appMemberCardService.recharge(card, record);
        ra.addFlashAttribute("msg", "会员卡已开通/充值成功");
        return "redirect:/app/card";
    }

    private com.yourname.studyroom.model.enums.CardType convertCardType(String code) {
        if (code == null) {
            return null;
        }
        return switch (code) {
            case "1" -> com.yourname.studyroom.model.enums.CardType.TIMES;
            case "2" -> com.yourname.studyroom.model.enums.CardType.HOURS;
            case "3" -> com.yourname.studyroom.model.enums.CardType.MONTH;
            default -> null;
        };
    }
}
