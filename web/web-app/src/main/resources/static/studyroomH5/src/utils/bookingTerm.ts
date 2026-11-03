import dayjs from "dayjs";
import type { TermInfoInterface } from "@/api/search/types";

/**
 * 解析门店营业时间（"HH:mm-HH:mm"）得到单日营业小时数，解析失败按 8 小时计。
 */
export function getBusinessHoursPerDay(businessHours?: string): number {
  if (!businessHours) return 8;
  const [start, end] = businessHours.split("-");
  const toMinutes = (value: string) => {
    const [hour, minute] = value.split(":");
    return Number(hour) * 60 + Number(minute);
  };
  const minutes = toMinutes(end) - toMinutes(start);
  if (!Number.isFinite(minutes) || minutes <= 0) return 8;
  return Math.max(1, Math.round(minutes / 60));
}

/**
 * 预订时长折算小时数：小时按原值，天/月按单日营业小时数折算。
 */
export function getTermHours(
  term: TermInfoInterface,
  businessHoursPerDay: number
): number {
  const count = Number(term.monthCount);
  if (term.unit === "小时") return count;
  if (term.unit === "天") return count * businessHoursPerDay;
  return count * businessHoursPerDay * 30;
}

/**
 * 预订时长占用的自然日数：小时预订占用当天，天/月按对应天数。
 */
export function getTermDays(term: TermInfoInterface): number {
  const count = Number(term.monthCount);
  if (term.unit === "小时") return 1;
  if (term.unit === "天") return count;
  return count * 30;
}

/**
 * 按开始日期与预订时长推算结束日期（结束日期不含当天，与续期时以旧结束日为新开始日相衔接）。
 */
export function getTermEndDate(startDate: string, term: TermInfoInterface): string {
  if (term.unit === "月") {
    return dayjs(startDate).add(Number(term.monthCount), "month").format("YYYY-MM-DD");
  }
  return dayjs(startDate)
    .add(getTermDays(term), "day")
    .format("YYYY-MM-DD");
}
