package com.itheima.service;

import java.util.Map;
/**
 * 获得运营统计数据
 * Map数据格式：
 *      todayNewMember -> number
 *      totalMember -> number
 *      thisWeekNewMember -> number
 *      thisMonthNewMember -> number
 *      todayOrderNumber -> number
 *      todayVisitsNumber -> number
 *      thisWeekOrderNumber -> number
 *      thisWeekVisitsNumber -> number
 *      thisMonthOrderNumber -> number
 *      thisMonthVisitsNumber -> number
 *      hotSetmeals -> List<Setmeal>
 */
public interface ReportService {

    public Map<String, Object> getBusinessReportData() throws Exception;
}
