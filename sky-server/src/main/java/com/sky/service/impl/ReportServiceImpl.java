package com.sky.service.impl;

import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 营业额统计接口
     *
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO turnoverStatistics(String begin, String end) {
        //格式化日期
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate beginDate = LocalDate.parse(begin, formatter);
        LocalDate point = LocalDate.parse(begin, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);
        //创建日期列表
        Map<String, String> map = new LinkedHashMap<>();
        map.put(point.toString(), "0");
        while (!point.isEqual(endDate)) {
            point = point.plusDays(1);
            map.put(point.toString(), "0");
        }
        //查询数据
        //秉着左开右闭原则，所以结束日期加一天。
        List<TurnoverReportVO> turnoverReportVOS = orderMapper.queryAmountByOrderTime(beginDate, endDate.plusDays(1));
        for (TurnoverReportVO turnoverReportVO : turnoverReportVOS) {
            map.put(turnoverReportVO.getDateList(), turnoverReportVO.getTurnoverList());
        }
        //封装数据
        StringBuilder dateList = new StringBuilder();
        StringBuilder turnoverList = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            dateList.append(entry.getKey()).append(",");
            turnoverList.append(entry.getValue()).append(",");
        }
        //去除尾部逗号
        if (dateList.length() > 0 && turnoverList.length() > 0) {

            dateList.deleteCharAt(dateList.length() - 1);
            turnoverList.deleteCharAt(turnoverList.length() - 1);
        }
        return TurnoverReportVO.builder()
                .dateList(String.valueOf(dateList))
                .turnoverList(String.valueOf(turnoverList))
                .build();


    }

    /**
     * 用户统计接口
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //生成日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.isEqual(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //生成对应日期列表
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        //查询数据
        //先查询第一天前的所有用户数量
        Integer total = userMapper.queryTotalUserByCreateTimeBefore(begin);
        total = total == null ? 0 : total;
        for (LocalDate date : dateList) {
            //查询当天新增用户
            Integer add = userMapper.queryTotalUserByCreateTime(date);
            //add为null为没有新增用户
            add = add == null ? 0 : add;
            //总用户累加
            total += add;
            //存入列表
            newUserList.add(add);
            totalUserList.add(total);
        }

        //封装结果
        return UserReportVO.builder()
                .dateList(StringUtil.join(",", dateList))
                .newUserList(StringUtil.join(",",newUserList))
                .totalUserList(StringUtil.join(",",totalUserList))
                .build();
    }
}