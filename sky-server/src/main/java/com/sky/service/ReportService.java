package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {
    /**
     * 营业额统计接口
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计接口
     * @param begin
     * @param end
     * @return
     */
    UserReportVO userStatisticsTimeStamp(LocalDate begin, LocalDate end);

    /**
     * 订单统计接口
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO ordersStatisticsTimeStamp(LocalDate begin, LocalDate end);

    /**
     * 查询销量排名top10接口
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO saleStatisticsTimeStamp(LocalDate begin, LocalDate end);

    /**
     * 导出运营数据
     * @param response
     */
    void exportBusinessData(HttpServletResponse response);
}
