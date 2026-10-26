package com.yourname.studyroom.portal.page.admin;

import com.yourname.studyroom.model.enums.BookingStatus;
import com.yourname.studyroom.web.admin.mapper.ReportMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表中心：营收/订单/预约/会员卡聚合 + 订单流水 CSV 导出（纯 Java 流式写出，无脚本）
 */
@Controller
public class AdminReportController {

    private final ReportMapper reportMapper;

    public AdminReportController(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    @GetMapping("/admin/report")
    public String report(@RequestParam(defaultValue = "7") int range, Model model) {
        String from = LocalDate.now().minusDays(range - 1L).format(DateTimeFormatter.ISO_DATE);

        List<Map<String, Object>> daily = reportMapper.selectDailyRevenue(from);
        Map<String, Map<String, Object>> apptByDay = new LinkedHashMap<>();
        for (Map<String, Object> row : reportMapper.selectDailyAppointments(from)) {
            apptByDay.put(String.valueOf(row.get("d")), row);
        }
        // 逐日表：补齐无订单日为 0
        List<Map<String, Object>> dailyRows = new ArrayList<>();
        long totalOrders = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        long totalAppointments = 0;
        LocalDate start = LocalDate.now().minusDays(range - 1L);
        Map<String, Map<String, Object>> dailyByDay = new LinkedHashMap<>();
        for (Map<String, Object> row : daily) {
            dailyByDay.put(String.valueOf(row.get("d")), row);
        }
        for (LocalDate d = start; !d.isAfter(LocalDate.now()); d = d.plusDays(1)) {
            String key = d.format(DateTimeFormatter.ISO_DATE);
            Map<String, Object> row = dailyByDay.get(key);
            long cnt = row == null ? 0 : ((Number) row.get("cnt")).longValue();
            BigDecimal revenue = row == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(row.get("revenue")));
            long appt = apptByDay.containsKey(key) ? ((Number) apptByDay.get(key).get("cnt")).longValue() : 0;
            Map<String, Object> line = new LinkedHashMap<>();
            line.put("date", key);
            line.put("orders", cnt);
            line.put("revenue", revenue);
            line.put("appointments", appt);
            dailyRows.add(line);
            totalOrders += cnt;
            totalRevenue = totalRevenue.add(revenue);
            totalAppointments += appt;
        }
        // 客单价（含 0 单保护）
        BigDecimal avgPrice = totalOrders == 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, java.math.RoundingMode.HALF_UP);

        List<Map<String, Object>> storeRank = reportMapper.selectStoreRevenue(from);
        long maxStoreRevenue = 1;
        for (Map<String, Object> row : storeRank) {
            long r = new BigDecimal(String.valueOf(row.get("revenue"))).longValue();
            maxStoreRevenue = Math.max(maxStoreRevenue, r);
        }
        for (Map<String, Object> row : storeRank) {
            long r = new BigDecimal(String.valueOf(row.get("revenue"))).longValue();
            row.put("percent", Math.max(4, Math.round(r * 100.0 / maxStoreRevenue)));
        }
        model.addAttribute("storeRank", storeRank);
        model.addAttribute("maxStoreRevenue", maxStoreRevenue);

        Map<String, Object> member = reportMapper.selectMemberSummary();
        model.addAttribute("member", member);

        model.addAttribute("range", range);
        model.addAttribute("dailyRows", dailyRows);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("avgPrice", avgPrice);
        return "admin/report";
    }

    /**
     * 订单流水 CSV 导出：UTF-8 BOM 保证 Excel 直接打开不乱码，流式写出不占内存
     */
    @GetMapping("/admin/report/export.csv")
    public void exportCsv(@RequestParam(defaultValue = "30") int range,
                          HttpServletResponse response) throws IOException {
        String from = LocalDate.now().minusDays(range - 1L).format(DateTimeFormatter.ISO_DATE);
        List<Map<String, Object>> rows = reportMapper.selectOrderFlow(from);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv;charset=UTF-8");
        String filename = "order-flow-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + filename);

        PrintWriter writer = response.getWriter();
        // UTF-8 BOM：Excel 识别编码
        writer.write('\ufeff');
        writer.println("订单号,门店,座位,联系人,手机号,状态,开始时间,结束时间,金额,创建时间");
        for (Map<String, Object> row : rows) {
            writer.println(String.join(",",
                    csv(row.get("orderNo")),
                    csv(row.get("storeName")),
                    csv(row.get("seatCode")),
                    csv(row.get("userName")),
                    csv(row.get("phone")),
                    csv(statusName(row.get("status"))),
                    csv(row.get("startDate")),
                    csv(row.get("endDate")),
                    csv(row.get("totalAmount")),
                    csv(row.get("createTime"))));
        }
        writer.flush();
    }

    private String statusName(Object code) {
        if (code == null) {
            return "";
        }
        int c = ((Number) code).intValue();
        for (BookingStatus st : BookingStatus.values()) {
            if (st.getCode() == c) {
                return st.getName();
            }
        }
        return String.valueOf(c);
    }

    /** CSV 字段转义：含逗号/引号/换行时加引号包裹 */
    private String csv(Object value) {
        String v = value == null ? "" : String.valueOf(value);
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }
}
