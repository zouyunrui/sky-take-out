package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     *
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);

    /**
     * 查询历史订单
     *
     * @param ordersPageQueryDTO
     * @return
     */


    List<Orders> queryList(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 订单搜索（分页查询）
     *
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单数据
     *
     * @param id
     * @return
     */
    @Select("select *from orders where id=#{id}")
    Orders getById(Long id);

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    @Select("SELECT \n" +
            "    SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END) AS confirmed, -- 待派送数量\n" +
            "    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) AS toBeConfirmed, -- 待接单数量\n" +
            "    SUM(CASE WHEN status = 4 THEN 1 ELSE 0 END) AS deliveryInProgress,  -- 派送中数量\n" +
            "    SUM(CASE WHEN status = 5 THEN 1 ELSE 0 END) AS completed,  -- 已完成数量\n" +
            "    SUM(CASE WHEN status = 6 THEN 1 ELSE 0 END) AS cancelled  -- 已取消数量\n" +
            "FROM orders;")
    OrderStatisticsVO countBysStatus();

    @Select("select * from orders where status=#{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 查询每天的营业额
     * @param map
     * @return
     */
    Double turnoverStatisticsByBeginEnd(Map map);

    /**
     * 查询每天新增的订单
     * 查询每天订单总数
     * @param map
     * @return
     */
    Long orderStatisticsMap(Map<String, Object> map);



    /**
     * 查询商品的销量
     *
     * @param map
     * @return
     */
    List<OrderDetail> saleStatisticsMap(Map<String, Object> map);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
