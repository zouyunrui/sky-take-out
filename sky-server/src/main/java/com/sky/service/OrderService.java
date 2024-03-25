package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.*;

import java.time.LocalDate;

public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 查询历史订单
     * @param ordersDTO
     * @return
     */

    PageResult queryList(OrdersPageQueryDTO ordersDTO);

    /**
     * 查询订单详情
     * @return
     */
    OrderVO orderDetail(Long id);

    /**
     *取消订单
     * @param id
     */
    void cancelOrder(Long id) throws Exception;

    /**
     * 再来一单
     * @param id
     */
    void reprtiton(Long id);


    //PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
    /**
     * 分页查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    OrderStatisticsVO statisticsByStatus();

    /**
     * 完成订单
     * @param id
     */
    void complete(Long id);

    /**
     * 取消订单
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 接单
     * @param id
     */
    void confirm(OrdersConfirmDTO id);

    /**
     * 客户催单
     * @param id
     */
    void reminder(Long id);

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
}
