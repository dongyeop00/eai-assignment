package com.gdy.inspien.order.service.impl;

import com.gdy.inspien.global.logging.Logger;
import com.gdy.inspien.integration.sftp.SftpClient;
import com.gdy.inspien.order.dto.OrderDTO;
import com.gdy.inspien.order.mapper.OrderMapper;
import com.gdy.inspien.order.repository.OrderRepository;
import com.gdy.inspien.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service("OrderBatch")
@RequiredArgsConstructor
public class OrderServiceBatchImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final SftpClient sftpClient;
    private final Logger integrationLogger;

    /**
     * 시나리오 1: 주문 처리 (동기)
     * 1. XML 파싱
     * 2. ORDER_TB 저장
     * 3. SFTP 영수증 전송
     */
    @Transactional
    public List<OrderDTO> processOrder(String xmlRequest) {
        log.info("주문 처리 시작");

        // 1. XML 파싱 및 매핑
        List<OrderDTO> orders = orderMapper.parseAndMap(xmlRequest);
        log.info("XML 파싱 완료: {}건", orders.size());

        // 2. ORDER_TB 저장
        orderRepository.saveAllBatch(orders);
        for (OrderDTO order : orders) {
            integrationLogger.logSuccess(order.getOrderId(), "ORDER_TB 저장 완료");
        }

        // 3. SFTP 영수증 전송
        sftpClient.uploadReceipt(orders);
        integrationLogger.logSuccess(orders.get(0).getOrderId(), "SFTP 영수증 전송 완료");
        log.info("주문 처리 완료: {}건", orders.size());

        return orders;
    }
}
