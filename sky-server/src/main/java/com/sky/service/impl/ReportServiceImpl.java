package com.sky.service.impl;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final WorkspaceService workspaceService;

    @Override
/**
 * 计算给定时间范围内的营业额统计报告
 *
 * @param begin 统计开始日期
 * @param end   统计结束日期（包含）
 * @return 营业额统计报告
 */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        // 创建日期列表，包含给定范围内的所有日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 创建营业额列表，存储每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            // 构建当天的起始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 查询当天完成状态的订单营业额
            Map<String, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.turnoverStatisticsByBeginEnd(map);

            // 如果当天没有营业额，则设为0
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        // 构建营业额统计报告对象并返回
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))  // 使用StringUtils.join方法将日期列表转换为字符串
                .turnoverList(StringUtils.join(turnoverList, ","))  // 使用StringUtils.join方法将营业额列表转换为字符串
                .build();
    }

    @Override
/**
 * 计算给定时间范围内的用户统计报告
 *
 * @param begin 统计开始日期
 * @param end   统计结束日期（包含）
 * @return 用户统计报告
 */
    public UserReportVO userStatisticsTimeStamp(LocalDate begin, LocalDate end) {
        // 创建日期列表，包含给定范围内的所有日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 创建新增用户列表，存储每天的新增用户
        List<Long> newUserList = new ArrayList<>();
        // 创建用户总量列表，存储每天的用户总数
        List<Long> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            // 构建当天的起始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 查询当天新增用户数量
            Map<String, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Long newUser = userMapper.userStatisticsMap(map);
            // 如果当天没有新增用户，则设为0
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);

            // 查询当天用户总数
            Map<String, Object> mapTotal = new HashMap<>();
            mapTotal.put("end", endTime);
            Long totalUser = userMapper.userStatisticsMap(mapTotal);
            // 如果当天没有用户，则设为0
            totalUser = totalUser == null ? 0 : totalUser;
            totalUserList.add(totalUser);
        }

        // 构建用户统计报告对象并返回
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))  // 使用StringUtils.join方法将日期列表转换为字符串
                .newUserList(StringUtils.join(newUserList, ","))  // 使用StringUtils.join方法将新增用户列表转换为字符串
                .totalUserList(StringUtils.join(totalUserList, ","))  // 使用StringUtils.join方法将用户总数列表转换为字符串
                .build();
    }


    @Override
/**
 * 计算给定时间范围内的订单统计报告
 *
 * @param begin 统计开始日期
 * @param end   统计结束日期（包含）
 * @return 订单统计报告
 */
    public OrderReportVO ordersStatisticsTimeStamp(LocalDate begin, LocalDate end) {

        // 创建日期列表，包含给定范围内的所有日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 创建新增订单列表，存储每天的新增订单
        List<String> orderCountList = new ArrayList<>();
        // 创建订单总量列表，存储每天的订单总数
        List<Integer> totalOrderCount = new ArrayList<>();
        // 创建有效订单列表，存储每天的有效订单
        List<String> validOrderCountList = new ArrayList<>();
        // 创建有效订单总数列表，存储每天的有效订单总数
        List<Integer> validOrderCount = new ArrayList<>();
        // 创建订单完成率列表，存储每天的订单完成率
        List<Double> orderCompletionRate = new ArrayList<>();

        for (LocalDate date : dateList) {
            // 构建当天的起始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 查询当天新增订单数量
            Map<String, Object> map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Long orderCount = orderMapper.orderStatisticsMap(map);
            // 如果当天没有新增订单，则设为0
            orderCount = orderCount == null ? 0 : orderCount;
            orderCountList.add(String.valueOf(orderCount));

            // 查询当天有效订单数量
            map.put("status", Orders.COMPLETED);
            Integer validOrderCountA = Math.toIntExact(orderMapper.orderStatisticsMap(map));
            // 如果当天没有有效订单，则设为0
            validOrderCountA = validOrderCountA == null ? 0 : validOrderCountA;
            validOrderCountList.add(String.valueOf(validOrderCountA));

            // 查询当天有效订单总数
            map.remove("begin");
            Long validOrder = orderMapper.orderStatisticsMap(map);
            // 如果当天没有订单，则设为0
            validOrder = validOrder == null ? 0 : validOrder;
            validOrderCount.add(Math.toIntExact(validOrder));

            // 查询当天订单总数
            map.remove("status"); // 移除status参数以获取订单总数
            Integer totalOrder = Math.toIntExact(orderMapper.orderStatisticsMap(map));
            // 如果当天没有订单，则设为0
            totalOrder = totalOrder == null ? 0 : totalOrder;
            totalOrderCount.add(totalOrder);
        }

        for (int i = 0; i < totalOrderCount.size(); i++) {
            Integer total = totalOrderCount.get(i);
            Integer valid = validOrderCount.get(i);

            // 检查订单总数是否为零，避免除以零的情况
            double completionRate;
            if (total != 0) {
                completionRate = (double) valid / total;
            } else {
                completionRate = 0; // 或者设置其他默认值
            }

            orderCompletionRate.add(completionRate);
        }

        // 构建订单统计报告对象并返回
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ",")) // 使用StringUtils.join方法将日期列表转换为字符串
                .orderCountList(String.join(",", orderCountList)) // 使用String.join方法将新增订单列表转换为字符串
                .totalOrderCount(totalOrderCount.stream().mapToInt(Integer::intValue).sum()) // 计算订单总数
                .validOrderCountList(String.join(",", validOrderCountList)) // 使用String.join方法将有效订单列表转换为字符串
                .validOrderCount(validOrderCount.stream().mapToInt(Integer::intValue).sum()) // 计算有效订单总数
                .orderCompletionRate(orderCompletionRate.get(orderCompletionRate.size() - 1))  // 使用orderCompletionRate列表构建double数组
                .build();
    }

    @Override
    public SalesTop10ReportVO saleStatisticsTimeStamp(LocalDate begin, LocalDate end) {
        // 创建日期列表，包含给定范围内的所有日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 创建商品销量映射，用于保存每个商品名称对应的销量
        Map<String, Integer> salesMap = new HashMap<>();

        for (LocalDate date : dateList) {
            // 构建当天的起始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 查询商品销量
            Map<String, Object> map = new HashMap<>();
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);

            List<OrderDetail> list = orderMapper.saleStatisticsMap(map);

            for (OrderDetail orderDetail : list) {
                if (orderDetail != null) {
                    String productName = orderDetail.getName();
                    int quantity = orderDetail.getNumber();

                    // 如果商品名称已经在映射中存在，则将销量累加到已有的销量上，否则将商品名称和销量添加到映射中
                    if (salesMap.containsKey(productName)) {
                        salesMap.put(productName, salesMap.get(productName) + quantity);
                    } else {
                        salesMap.put(productName, quantity);
                    }
                }
            }
        }

        // 将商品名称和销量分别保存到列表中
        List<String> nameList = new ArrayList<>(salesMap.keySet());
        List<Integer> numberList = new ArrayList<>(salesMap.values());

        // 构建用户统计报告对象并返回
        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(nameList, ","))  // 使用StringUtils.join方法将商品名称列表转换为字符串
                .numberList(StringUtils.join(numberList, ","))  // 使用StringUtils.join方法将销量列表转换为字符串
                .build();
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        // 获取时间范围，过去30天的起始日期到昨天
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        // 获取业务数据
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));

        // 加载Excel模板文件
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);

            // 获取工作表
            XSSFSheet sheet = excel.getSheet("Sheet1");

            // 填充概览数据
            sheet.getRow(1).getCell(1).setCellValue("时间:" + dateBegin + "至" + dateEnd);
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            // 填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i); // 逐天填充数据
                BusinessDataVO businessDataDay = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(String.valueOf(date));
                row.getCell(2).setCellValue(businessDataDay.getTurnover());
                row.getCell(3).setCellValue(businessDataDay.getValidOrderCount());
                row.getCell(4).setCellValue(businessDataDay.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataDay.getUnitPrice());
                row.getCell(6).setCellValue(businessDataDay.getNewUsers());
            }

            // 将Excel写入输出流并关闭资源
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            out.close();
            excel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
