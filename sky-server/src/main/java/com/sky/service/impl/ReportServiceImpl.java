package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceServiceImpl workspaceServiceImpl;

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


    /**
     * 用户统计接口
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //获取begin与end之间的所有日期
        List<LocalDate> dataList = new ArrayList<>();
        dataList.add(begin);

        while (!(begin.equals(end))) {
            begin = begin.plusDays(1);
            dataList.add(begin);
        }
        //获取总用户（查询创建时间在该区间即可）
        List<Integer> totalUserList = new ArrayList<>();
        //获取新用户
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate localDate : dataList) {
            //设定当天日期范围
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            //TODO 查询:新用户则代表当天创建的用户(beginTime<C_T<endTime)，老用户代表非当天创建（C_T < beginTime）
            //查询新用户
            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Integer userSum = userMapper.countByCreateTime(map);
            userSum = userSum == null ? 0 : userSum;
            newUserList.add(userSum);

            //查询用户总数（C_T<endTime）
            map.replace("begin", null);
            Integer allUser = userMapper.countByCreateTime(map);
            allUser = allUser == null ? 0 : allUser;
            totalUserList.add(allUser);
        }

        //将所需信息拼接后封装返回
        String data = StringUtils.join(dataList, ",");
        String user = StringUtils.join(totalUserList, ",");
        String newUser = StringUtils.join(newUserList, ",");

        return UserReportVO.builder().dateList(data).totalUserList(user).newUserList(newUser).build();
    }

    /**
     * 订单统计接口
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        //日期列表获取
        List<LocalDate> dataList = new ArrayList<>();
        dataList.add(begin);
        while (!(begin.equals(end))) {
            begin = begin.plusDays(1);
            dataList.add(begin);
        }

        //每天订单总数
        List<Integer> totalList = new ArrayList<>();
        //总订单数量
        Integer totalOrderCount = 0;
        //每天有效订单
        List<Integer> validOrderCountList = new ArrayList<>();
        //总有效订单数量
        Integer validOrderCount = 0;
        //订单
        for (LocalDate localDate : dataList) {
            //设定当天时间
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            //获取当天订单总数
            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", null);
            Integer total = orderMapper.getOrdersStatistics(map);
            total = total == null ? 0 : total;
            totalList.add(total);
            //记录订单总数
            totalOrderCount = total + totalOrderCount;

            //获取当天有效订单（订单状态为5（完成状态））
            map.replace("status", Orders.COMPLETED);
            Integer validOrder = orderMapper.getOrdersStatistics(map);
            validOrder = validOrder == null ? 0 : validOrder;
            validOrderCountList.add(validOrder);
            //记录有效订单总数
            validOrderCount = validOrderCount + validOrder;
        }

        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = (double) validOrderCount / totalOrderCount;
        }

        //转换成规定格式的字符串
        String data = StringUtils.join(dataList, ",");
        String total = StringUtils.join(totalList, ",");
        String valid = StringUtils.join(validOrderCountList, ",");

        return OrderReportVO.builder()
                .dateList(data)
                .validOrderCountList(valid)
                .orderCountList(total)
                .orderCompletionRate(orderCompletionRate)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .build();
    }

    /**
     * 查询销量排名top10
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        //获取时段信息
        List<LocalDate> dataList = new ArrayList<>();
        dataList.add(begin);
        while (!(begin.equals(end))) {
            begin = begin.plusDays(1);
            dataList.add(begin);
        }

        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        List<GoodsSalesDTO> goodsSalesDTOS = null;
        //查询当天的订单明细表，数量以及name，以数量为降序且分组,limit筛选出10个(使用两张表的笛卡尔积)
        // 查询当天所有的完成后的订单

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        Map map = new HashMap();
        map.put("begin", beginTime);
        map.put("end", endTime);
        //将两张表内连接，按照order_time筛选（已完成订单），并分组统计个数后，limit筛选出前10即可w
        goodsSalesDTOS = orderMapper.getSalesTop10(map);

        for (GoodsSalesDTO goodsSalesDTO : goodsSalesDTOS) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }

        //转换封装
        String name = StringUtils.join(nameList, ",");
        String number = StringUtils.join(numberList, ",");

        return SalesTop10ReportVO.builder().nameList(name).numberList(number).build();
    }

    /**
     * 导出报表
     *
     * @param response
     */
    @Override
    public void exportBussinessData(HttpServletResponse response) {
        //设定时间为当月
        LocalDate begin = LocalDate.now().plusDays(-30);
        LocalDate end = LocalDate.now().plusDays(-1);
        //取得表中所要求的概览数据(限制一天时间)
        BusinessDataVO businessData = workspaceServiceImpl.getBusinessData
                (LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));//设定起始时刻.与结束时刻
        //将模板读入内存中
        try {
            InputStream file = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
            //在内存中新建文档，将file内容引入到该文档中
            XSSFWorkbook excel = new XSSFWorkbook(file);
            //获取名为sheet1的标签页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            //在第二行第二列写入日期范围
            sheet.getRow(1).getCell(1).setCellValue(begin + " --- " + end);

            //四行三列写入对应数据        1。获取所有第四行数据
            XSSFRow row = sheet.getRow(3);
            //2.获取第四列所有单元格并写入数据
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            //获取第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            //写入明细数据
            //根据表格限制，只能输入到从第7行到第37行内
            for (int i = 0; i < 30; i++) {
                //设定每天时间
                LocalDate date = begin.plusDays(i);
                //获取明细信息(每天的信息)
                businessData = workspaceServiceImpl.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                //获取第X行
                row = sheet.getRow(i + 7);
                //获取列数，写入数据
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            //通过输出流将文件下载给客户端浏览器中
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);      //将流文件数据写入到excel中
            //关闭流
            outputStream.flush();
            outputStream.close();
            excel.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //获取模板
    }
}
