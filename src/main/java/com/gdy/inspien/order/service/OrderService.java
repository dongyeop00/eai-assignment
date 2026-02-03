package com.gdy.inspien.order.service;

import com.gdy.inspien.order.dto.OrderDTO;

import java.util.List;

public interface OrderService {

    List<OrderDTO> processOrder(String xmlRequest);

}
