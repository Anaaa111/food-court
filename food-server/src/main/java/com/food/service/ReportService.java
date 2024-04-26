package com.food.service;

import com.food.vo.OrderReportVO;
import com.food.vo.SalesTop10ReportVO;
import com.food.vo.TurnoverReportVO;
import com.food.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO ordersStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO orderStatistics(LocalDate begin, LocalDate end);
    /**
     * 销量排名top10统计
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO salesTop10Statistics(LocalDate begin, LocalDate end);
    /**
     * 导出Excel报表接口(导出营业数据)
     */
    void export(HttpServletResponse response);
}
