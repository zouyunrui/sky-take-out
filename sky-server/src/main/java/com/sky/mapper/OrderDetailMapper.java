package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单明显数据
     *
     * @param orderDetailList
     */

    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单id查询订单明细表的数据
     *
     * @param ordersId
     * @return
     */
    @Select("select * from order_detail where order_id=#{ordersId}")
    List<OrderDetail> getByOrderId(Long ordersId);
}
