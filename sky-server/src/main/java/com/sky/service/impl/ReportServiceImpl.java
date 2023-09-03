package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        //由于VO返回的都是字符串，故获取begin-end所有日期
        List<LocalDate> dataList = new ArrayList<>();
        dataList.add(begin);

        //循环遍历日期，将其放入list中
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dataList.add(begin);
        }
        //存储当天的营业额
        List<Double> turnoverList = new ArrayList<>();
        //根据时间范围查询出已经完成订单的金额
        for (LocalDate localDate : dataList) {  //查询当天数据
            //设定当天时间的始末
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            //查询所有订单状态为完成且时间在当天的营业额合集
            //使用HashMap（），一次性传输数据
            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByTurnover(map);
            //如果当天无营业额，则会返回空，故要转成0.0
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }


        //将所有结果转成规定的字符传
        String data = StringUtils.join(dataList, ",");      //lang3包中的工具类
        String turnover = StringUtils.join(turnoverList, ",");

        return TurnoverReportVO.builder().dateList(data).turnoverList(turnover).build();
    }
}
