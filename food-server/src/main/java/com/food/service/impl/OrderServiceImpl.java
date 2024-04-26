package com.food.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.food.constant.MessageConstant;
import com.food.context.BaseContext;
import com.food.dto.*;
import com.food.entity.AddressBook;
import com.food.entity.OrderDetail;
import com.food.entity.Orders;
import com.food.entity.ShoppingCart;
import com.food.exception.AddressBookBusinessException;
import com.food.exception.OrderBusinessException;
import com.food.exception.ShoppingCartBusinessException;
import com.food.mapper.AddressBookMapper;
import com.food.mapper.OrderDetailMapper;
import com.food.mapper.OrderMapper;
import com.food.mapper.ShoppingCartMapper;
import com.food.result.PageResult;
import com.food.service.OrderService;
import com.food.vo.OrderStatisticsVO;
import com.food.vo.OrderSubmitVO;
import com.food.vo.OrderVO;
import com.food.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    AddressBookMapper addressBookMapper;
    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    WebSocketServer webSocketServer;

    // 检查配送范围
    // @Value("${sky.shop.address}")
    // private String shopAddress;
    //
    // @Value("${sky.baidu.ak}")
    // private String ak;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 第一步：处理业务异常，如地址薄可能为空，未设置地址，购物车信息可能为空，这样都是不能下单的
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        // 检查用户的收获地址是否超出配送范围
        // checkOutOfRange(addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartItem = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartItem == null && shoppingCartItem.size() == 0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        // 第二步：往订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        // 设置订单号(利用时间戳设置)
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(BaseContext.getCurrentId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        // 插入
        orderMapper.insert(orders);
        // 第三步：往订单详情表插入n条数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartItem) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        // 批量插入
        orderDetailMapper.insertBatch(orderDetailList);
        // 加入订单以后，需要将购物车清空
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(BaseContext.getCurrentId());
        shoppingCartMapper.delete(cart);
        // 封装VO数据，进行返回
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setId(orders.getId());
        orderSubmitVO.setOrderTime(orders.getOrderTime());
        orderSubmitVO.setOrderAmount(orders.getAmount());
        orderSubmitVO.setOrderNumber(orders.getNumber());
        return orderSubmitVO;
    }

    /**
     * 支付成功
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {
        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);
        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
        // 支付成功后，修改数据库为待接单状态，并往客户端(管理端的客户端)推送消息
        // 登录管理端的客户端后，前端就发送了websocket请求,就已经建立了一个长连接
        // 这里直接往客服端发送消息即可
        // type:1表示来单提醒，2为催单提醒
        // orderId,该订单的id
        // content：推送的内容
        Map map = new HashMap();
        map.put("type", 1);
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号：" + outTradeNo);
        String message = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(message);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    public OrderVO getOrderDetail(Long id) {
        // 根据id查询订单
        Orders order = orderMapper.getById(id);
        // 获取订单id
        Long orderId = order.getId();
        // 根据订单id，获取该订单的详情
        List<OrderDetail> orderDetails = orderDetailMapper.getOrderId(orderId);
        // 封装返回数据
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    /**
     * 分页查询历史订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult PageQueryOrder(int page, int pageSize, Integer status) {
        // 设置分页参数
        PageHelper.startPage(page, pageSize);
        // 封装成DTO，用户端和管理端都是能用的
        OrdersPageQueryDTO pageQueryDTO = new OrdersPageQueryDTO();
        pageQueryDTO.setUserId(BaseContext.getCurrentId());
        pageQueryDTO.setStatus(status);
        // 分页查询
        Page<Orders> ordersList = orderMapper.orderPageQuery(pageQueryDTO);
        // 开始封装成ordersVo对象并返回
        List<OrderVO> list = new ArrayList<>();
        if(ordersList != null && ordersList.getTotal() > 0){
            // 有分页数据
            List<Orders> orders = ordersList.getResult();
            for (Orders order : orders) {
                // 根据order的id获取对应的订单详情
                Long orderId = order.getId();
                List<OrderDetail> orderDetails = orderDetailMapper.getOrderId(orderId);
                // 开始封装
                OrderVO orderVo = new OrderVO();
                BeanUtils.copyProperties(order, orderVo);
                orderVo.setOrderDetailList(orderDetails);
                list.add(orderVo);
            }
        }
        return new PageResult(ordersList.getTotal(), list);
    }

    /**
     * 取消订单
     * @param id
     */
    @Override
    public void cancelOrder(Long id) {
        // 首先通过id查询订单，查看它的订单状态
        Orders orders = orderMapper.getById(id);
        // 检查该订单是否存在
        if (orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Integer status = orders.getStatus();
        // 若是在已接单和已配送的状态下，需要用户联系商家，后面的状态则不能取消订单
        if (status > 2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders new_orders = new Orders();
        new_orders.setId(orders.getId());
        // 若待接单状态下，需要给用户退款
        if (status == Orders.TO_BE_CONFIRMED){
            // 直接模拟退款，就是将付款状态设置为已退款
            new_orders.setPayStatus(Orders.REFUND);
        }
        // 现在就可已取消订单了
        new_orders.setStatus(Orders.CANCELLED);
        // 设置取消原因和取消时间
        new_orders.setCancelReason("用户取消");
        new_orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(new_orders);
    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    public void repetitionOrder(Long id) {
        //根据id查询该订单的详情信息
        List<OrderDetail> orderDetails = orderDetailMapper.getOrderId(id);
        // 将该订单详情信息信息转换为购物车信息
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart, "id");
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartList.add(shoppingCart);
        }
        // 将购物车信息批量插入
        shoppingCartMapper.insertBatch(shoppingCartList);

    }

    /**
     * 管理端的分页查询
     * @param pageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO pageQueryDTO) {
        // 设置分页参数
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());

        Page<Orders> ordersList = orderMapper.orderPageQuery(pageQueryDTO);

        // 还需要返回订单中的菜品详情信息(以字符串形式返回)
        // 所以，我们需要将分页数据重新封装成VO
        // 开始封装成ordersVo对象并返回
        List<OrderVO> list = new ArrayList<>();

        if(ordersList != null && ordersList.getTotal() > 0){
            // 有分页数据,将分页的订单数据取出来，赋给OrderVO对象
            List<Orders> orders = ordersList.getResult();
            for (Orders order : orders) {
                // 开始封装
                OrderVO orderVo = new OrderVO();
                BeanUtils.copyProperties(order, orderVo);
                // 为OrderVO中没有被赋值的属性进行设置，即orderDetails属性
                // 生成orderDishes(字符串形式的菜品，通过订单id获取该订单的所有菜品，然后将所有菜品和数量拼接成字符串)
                List<OrderDetail> orderDetails = orderDetailMapper.getOrderId(order.getId());
                String orderDishes = "";
                for (OrderDetail orderDetail : orderDetails) {
                    String orderDish = orderDetail.getName() + "*" + orderDetail.getNumber() + ";";
                    orderDishes = orderDishes + orderDish;
                }
                orderVo.setOrderDishes(orderDishes);
                list.add(orderVo);
            }
        }
        return new PageResult(ordersList.getTotal(), list);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    public OrderStatisticsVO statistics() {
        // 查询各个状态下的订单数量
        Integer toBeConfirmed = orderMapper.getcountByStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.getcountByStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.getcountByStatus(Orders.DELIVERY_IN_PROGRESS);
        // 封装OrderStatisticsVO
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = new Orders();
        orders.setId(ordersConfirmDTO.getId());
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        // 只有订单为未接单状态才能拒单(后端也应该判断一下)
        Orders old_order = orderMapper.getById(ordersRejectionDTO.getId());
        Integer status = old_order.getStatus();
        if (old_order == null || status != Orders.TO_BE_CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = new Orders();
        // 若支付状态为已支付，则需要退款
        if (old_order.getPayStatus() == Orders.PAID){
            // 微信退款接口
            // 修改数据库中状态为已退款
            log.info("申请退款！！");
            orders.setPayStatus(Orders.REFUND);
        }
        // 拒单，将状态改成已取消，并设置拒绝原因
        orders.setId(ordersRejectionDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO
     */
    public void cancel(OrdersCancelDTO ordersCancelDTO){
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());
        // 已经取消的订单和已经完成的订单不能在执行取消订单操作
        if (ordersDB == null || ordersDB.getStatus() == Orders.COMPLETED || ordersDB.getStatus() == Orders.CANCELLED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 申请退款
        Orders orders = new Orders();
        if (ordersDB.getPayStatus() == Orders.PAID) {
            log.info("申请退款");
            orders.setPayStatus(Orders.REFUND);
        }

        // 管理端取消订单需要退款，根据订单id更新订单状态、取消原因、取消时间
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void delivery(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);
        // 只有待派送的订单才能派送
        if (ordersDB == null || ordersDB.getStatus() != Orders.CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void complete(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);
        // 只有派送中的订单才能进行完成订单
        if (ordersDB == null || ordersDB.getStatus() != Orders.DELIVERY_IN_PROGRESS){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.COMPLETED);
        // 完成订单需要设置送达时间
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 客户催单
     * @param id
     */
    public void reminder(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);
        // 只有待接单的订单才能进行催单操作
        if (ordersDB == null || ordersDB.getStatus() != Orders.TO_BE_CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 往管理端的客户端推送消息，完成催单
        // 2为催单提醒
        Map map = new HashMap();
        map.put("type", 2);
        map.put("orderId", id);
        map.put("content", "订单号：" + ordersDB.getNumber());
        String message = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(message);
    }

    // /**
    //  * 检查客户的收货地址是否超出配送范围
    //  * @param address
    //  */
    // private void checkOutOfRange(String address) {
    //     Map map = new HashMap();
    //     map.put("address",shopAddress);
    //     map.put("output","json");
    //     map.put("ak",ak);
    //
    //     //获取店铺的经纬度坐标
    //     String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);
    //
    //     JSONObject jsonObject = JSON.parseObject(shopCoordinate);
    //     if(!jsonObject.getString("status").equals("0")){
    //         throw new OrderBusinessException("店铺地址解析失败");
    //     }
    //
    //     //数据解析
    //     JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
    //     String lat = location.getString("lat");
    //     String lng = location.getString("lng");
    //     //店铺经纬度坐标
    //     String shopLngLat = lat + "," + lng;
    //
    //     map.put("address",address);
    //     //获取用户收货地址的经纬度坐标
    //     String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);
    //
    //     jsonObject = JSON.parseObject(userCoordinate);
    //     if(!jsonObject.getString("status").equals("0")){
    //         throw new OrderBusinessException("收货地址解析失败");
    //     }
    //
    //     //数据解析
    //     location = jsonObject.getJSONObject("result").getJSONObject("location");
    //     lat = location.getString("lat");
    //     lng = location.getString("lng");
    //     //用户收货地址经纬度坐标
    //     String userLngLat = lat + "," + lng;
    //
    //     map.put("origin",shopLngLat);
    //     map.put("destination",userLngLat);
    //     map.put("steps_info","0");
    //
    //     //路线规划
    //     String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);
    //
    //     jsonObject = JSON.parseObject(json);
    //     if(!jsonObject.getString("status").equals("0")){
    //         throw new OrderBusinessException("配送路线规划失败");
    //     }
    //
    //     //数据解析
    //     JSONObject result = jsonObject.getJSONObject("result");
    //     JSONArray jsonArray = (JSONArray) result.get("routes");
    //     Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");
    //
    //     if(distance > 5000){
    //         //配送距离超过5000米
    //         throw new OrderBusinessException("超出配送范围");
    //     }
    // }
}
