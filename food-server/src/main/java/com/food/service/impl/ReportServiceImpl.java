package com.food.service.impl;

import com.food.dto.*;
import com.food.entity.Orders;
import com.food.mapper.*;
import com.food.service.ReportService;
import com.food.service.WorkspaceService;
import com.food.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    WorkspaceService workspaceService;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO ordersStatistics(LocalDate begin, LocalDate end) {

        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        // 第一步，首先处理时间，将begin到end范围内的时间全部取出来，拼成一个时间类的字符串
        List<LocalDate> dateList = getDateList(begin, end);
        // 这时候dateList中就存放了begin到end范围内的所有时间，只要将其拼接成字符串即可
        String join = StringUtils.join(dateList, ",");
        turnoverReportVO.setDateList(join);

        // 第二步：处理每天对应的营业额，并将其拼接成字符串
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            // 取出每天的时间，调用数据库查询该天所有已完成订单的金额总数
            // 获取这一天的起始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumTurnoverByMap(map);
            // 若当天没有营业额，则将营业额设置为0
            turnover = turnover ==null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList, ","));
        return turnoverReportVO;
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        // 封装userReportVO时间列表属性
        UserReportVO userReportVO = new UserReportVO();
        List<LocalDate> dateList = getDateList(begin, end);
        String join = StringUtils.join(dateList, ",");
        userReportVO.setDateList(join);

        // 根据每天的时间，查询这一天新增的用户以及这一天之前(包括这一天)的所有用户数量
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            // 获取这一天的起始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // 根据Map获取到这一天之前的所有用户数量
            Integer totalUser = getUserCount(null, endTime);
            totalUserList.add(totalUser);

            // 查询这一天新增的用户数量
            Integer newUser = getUserCount(beginTime, endTime);
            newUserList.add(newUser);
        }
        userReportVO.setNewUserList(StringUtils.join(newUserList, ","));
        userReportVO.setTotalUserList(StringUtils.join(totalUserList, ","));
        return userReportVO;
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        // 封装orderReportVO时间列表属性
        OrderReportVO orderReportVO = new OrderReportVO();
        List<LocalDate> dateList = getDateList(begin, end);
        String join = StringUtils.join(dateList, ",");
        orderReportVO.setDateList(join);

        // 根据每天的时间，查询这一天的总订单数量和有效订单数量(已完成的订单)
        List<Integer> totalOrderList = new ArrayList<>();
        List<Integer> validOrderList = new ArrayList<>();
        for (LocalDate date : dateList) {
            // 获取这一天的起始时间和结束时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // 根据map查询每日的有效订单和总订单数量
            // 每日的总订单
            Integer totalOrder = getOrderCount(beginTime, endTime, null);
            totalOrderList.add(totalOrder);
            // 每日的有效订单
            Integer validOrder = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            validOrderList.add(validOrder);
        }
        // 将每日的总订单数量和有效订单数量装换成字符串封装到orderReportVO中去
        orderReportVO.setOrderCountList(StringUtils.join(totalOrderList, ","));
        orderReportVO.setValidOrderCountList(StringUtils.join(validOrderList, ","));
        // 根据每日订单数和有效订单数，计算该时间范围内的总订单数和总有效订单数
        Integer totalOrderCount = totalOrderList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderList.stream().reduce(Integer::sum).get();

        orderReportVO.setTotalOrderCount(totalOrderCount);
        orderReportVO.setValidOrderCount(validOrderCount);
        // 设置订单完成率
        Double orderCompletionRate = (totalOrderCount == 0 ? 0.0 : (validOrderCount.doubleValue()/totalOrderCount));
        orderReportVO.setOrderCompletionRate(orderCompletionRate);
        return orderReportVO;
    }
    /**
     * 销量排名top10统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO salesTop10Statistics(LocalDate begin, LocalDate end) {
        // 获得当前时间段内的菜品和套餐的销量top10排名
        // 首先需要将begin和end转换成LocalDateTime
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        // 调用数据库获得[beginTime, endTime]区间内的销量前十的菜品或套餐
        List<GoodsSalesDTO> salesTop10List =  orderDetailMapper.getSalesTop10(beginTime, endTime);

        // 需要将salesTop10List列表中的两个属性的所有值进行拼接，然后封装到SalesTop10ReportVO中
        // 首先将salesTop10List列表中的两个值取出来
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : salesTop10List) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }
        // 开始进行封装
        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();
        salesTop10ReportVO.setNameList(StringUtils.join(nameList, ","));
        salesTop10ReportVO.setNumberList(StringUtils.join(numberList, ","));
        return salesTop10ReportVO;
    }

    /**
     * 导出Excel报表接口(导出营业数据)
     */
    @Override
    public void export(HttpServletResponse response) {
        // 准备好近三十天的时间范围
        LocalDate beginDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().minusDays(1);
        // 获取到这三十天的营业数据
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(beginDate, LocalTime.MIN), LocalDateTime.of(endDate, LocalTime.MAX));

        // 将数据写入到excel文件中
        // 首先通过类加载器获取到excel模板文件的输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        // 通过输入流创建excel对象，这样就可以对这个excel进行操作了
        try {
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            // 获取到你需要操作的excel中的哪个标签页
            XSSFSheet sheet = excel.getSheet("Sheet1");
            // 获取到对应的行,已经对应的单元格，然后写入数据(填写时间数据)
            sheet.getRow(1).getCell(1).setCellValue("时间：" + beginDate + "至" + endDate);
            // 开始填写总的运营数据(获取到对应的行)
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            // 开始下一行的填写
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            // 开始这三十天的明细数据的填写了
            for (int i = 0; i < 30; i++) {
                // 获取这一天
                LocalDate date = beginDate.plusDays(i);
                // 获取到这一天的营业数据
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                // 开始填写数据到excel文件中
                // 动态获取到对应的行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            // 数据已经全部写入安东excel对象中后，就可以通过response对象的输出流输出到浏览器上了
            // 先获取到response的输出流
            ServletOutputStream outputStream = response.getOutputStream();
            // 将excel对象写入到该输出流中，返回给浏览器
            excel.write(outputStream);

            // 关闭资源
            outputStream.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 根据开始时间和结束时间，获取该范围内的时间列表
     * @param begin
     * @param end
     * @return
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end){
        // 第一步，首先处理时间，将begin到end范围内的时间全部取出来，拼成一个时间类的字符串
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            // 使用begin一天一天的往上加，直到加到最后一天，即end
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }

    /**
     * 根据map查询用户的数量
     * @param beginTime
     * @param endTime
     * @return
     */
    private Integer getUserCount(LocalDateTime beginTime, LocalDateTime endTime){
        Map map = new HashMap();
        map.put("end", endTime);
        map.put("begin", beginTime);
        Integer userCount = userMapper.countByMap(map);
        return userCount;
    }

    /**
     * 根据map查询订单的数量
     * @param beginTime
     * @param endTime
     * @return
     */
    private Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer status){
        Map map = new HashMap();
        map.put("end", endTime);
        map.put("begin", beginTime);
        map.put("status", status);
        Integer orderCount = orderMapper.countByMap(map);
        return orderCount;
    }
}
