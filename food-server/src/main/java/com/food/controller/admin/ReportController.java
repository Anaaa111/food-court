package com.food.controller.admin;

import com.food.result.Result;
import com.food.service.ReportService;
import com.food.vo.OrderReportVO;
import com.food.vo.SalesTop10ReportVO;
import com.food.vo.TurnoverReportVO;
import com.food.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("admin/report")
@Slf4j
@Api(tags = "报表相关接口")
public class ReportController {

    @Autowired
    ReportService reportService;


    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("turnoverStatistics")
    @ApiOperation("营业额统计")
    // @DateTimeFormat功能：将一个日期字符串转化为对应的Date类型
    public Result<TurnoverReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){

        log.info("营业额统计:{},{}", begin, end);
        TurnoverReportVO turnoverReportVO = reportService.ordersStatistics(begin, end);
        return Result.success(turnoverReportVO);
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){

        log.info("用户统计:{},{}", begin, end);
        UserReportVO userReportVO = reportService.userStatistics(begin, end);
        return Result.success(userReportVO);
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> orderStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){

        log.info("订单统计:{},{}", begin, end);
        OrderReportVO orderReportVO = reportService.orderStatistics(begin, end);
        return Result.success(orderReportVO);
    }

    /**
     * 销量排名top10统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("top10")
    @ApiOperation("销量排名top10统计")
    public Result<SalesTop10ReportVO> salesTop10Statistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){

        log.info("销量排名top10统计:{},{}", begin, end);
        SalesTop10ReportVO salesTop10ReportVO = reportService.salesTop10Statistics(begin, end);
        return Result.success(salesTop10ReportVO);
    }

    /**
     * 导出Excel报表接口(导出营业数据)
     */
    @GetMapping("export")
    @ApiOperation("导出Excel报表接口(导出营业数据)")
    public void export(HttpServletResponse response){
        // 之所以要使用response是因为我们需要将excel文件输出到浏览器中，所以就要用到response的输出流对象
        reportService.export(response);
    }
}
