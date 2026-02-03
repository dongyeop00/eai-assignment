package com.gdy.inspien.shipment.service.impl;

import com.gdy.inspien.global.logging.Logger;
import com.gdy.inspien.global.util.IdGenerator;
import com.gdy.inspien.order.dto.OrderDTO;
import com.gdy.inspien.order.repository.OrderRepository;
import com.gdy.inspien.shipment.dto.ShipmentDTO;
import com.gdy.inspien.shipment.repository.ShipmentRepository;
import com.gdy.inspien.shipment.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("ShipmentBatch")
@RequiredArgsConstructor
public class ShipmentServiceBatchImpl implements ShipmentService {

    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    private final IdGenerator shipmentIdGenerator;
    private final Logger integrationLogger;

    /**
     * 시나리오 2: 운송 회사 DB 적재 배치
     * 1. ORDER_TB에서 STATUS='N'인 미전송 주문 조회
     * 2. ShipDTO로 변환 후 SHIPMENT_TB 적재
     * 3. 적재 성공 시 ORDER_TB의 STATUS를 'Y'로 업데이트
     */
    @Transactional
    public int batchOrder() {
        List<OrderDTO> pendingOrders = orderRepository.findPendingOrders();

        if (pendingOrders.isEmpty()) {
            return 0;
        }

        List<ShipmentDTO> shipments = new ArrayList<>();
        List<String> orderIds = new ArrayList<>();

        // 1. 메모리에서 데이터 준비
        for (OrderDTO order : pendingOrders) {
            shipments.add(ShipmentDTO.builder()
                    .shipId(shipmentIdGenerator.generate())
                    .orderId(order.getOrderId())
                    .itemId(order.getItemId())
                    .applicantKey(order.getApplicantKey())
                    .address(order.getAddress())
                    .build());
            orderIds.add(order.getOrderId());
        }

        // 2. 모든 데이터 처리
        shipmentRepository.saveAllBatch(shipments);
        orderRepository.updateStatusesBatch(orderIds, "Y");

        integrationLogger.logBatch("운송 배치", shipments.size(), 0);
        return shipments.size();
    }
}
