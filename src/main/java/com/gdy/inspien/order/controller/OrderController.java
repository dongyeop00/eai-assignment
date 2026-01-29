package com.gdy.inspien.order.controller;

import com.gdy.inspien.global.response.ApiResponse;
import com.gdy.inspien.order.dto.OrderDTO;
import com.gdy.inspien.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성 API
     * - 요청: XML (application/xml 또는 text/xml)
     * - 응답: JSON
     */
    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
    public ApiResponse<List<OrderDTO>> createOrder(@RequestBody String xmlRequest) {
        log.info("주문 API 호출");

        List<OrderDTO> orders = orderService.processOrder(xmlRequest);

        return ApiResponse.success(orders, "주문이 완료되었습니다");
    }
}