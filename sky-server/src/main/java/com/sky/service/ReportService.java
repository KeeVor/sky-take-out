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
    TurnoverReportVO turnoverStatistics(String begin, String end);


    /**
     * 用户统计接口
     * @param begin
     * @param end
     * @return
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计接口
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);

    /**
     * 查询销量排行top10
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO selectTop10(LocalDate begin, LocalDate end);

    /**
     * 导出Excel报表
     * @param response
     */
    void exportData(HttpServletResponse response);
}
