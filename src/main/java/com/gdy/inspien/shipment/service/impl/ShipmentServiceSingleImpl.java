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

import java.util.List;

@Slf4j
@Service("ShipmentSingle")
@RequiredArgsConstructor
public class ShipmentServiceSingleImpl implements ShipmentService {

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
        log.info("=== 운송 배치 시작 ===");

        // 1. 미전송 주문 조회
        List<OrderDTO> pendingOrders = orderRepository.findPendingOrders();

        if (pendingOrders.isEmpty()) {
            log.info("처리할 미전송 주문이 없습니다.");
            return 0;
        }

        log.info("미전송 주문 {}건 조회 완료", pendingOrders.size());

        int successCount = 0;
        int failedCount = 0;

        for (OrderDTO order : pendingOrders) {
            try {
                // 2. ShipDTO로 변환
                ShipmentDTO shipment = ShipmentDTO.builder()
                        .shipId(shipmentIdGenerator.generate())
                        .orderId(order.getOrderId())
                        .itemId(order.getItemId())
                        .applicantKey(order.getApplicantKey())
                        .address(order.getAddress())
                        .build();

                // 3. SHIPMENT_TB 저장
                shipmentRepository.save(shipment);

                // 4. ORDER_TB STATUS 업데이트
                orderRepository.updateStatus(order.getOrderId(), "Y");

                integrationLogger.logSuccess(order.getOrderId(), "SHIPMENT_TB 적재 완료");
                successCount++;

            } catch (Exception e) {
                log.error("운송 적재 실패: ORDER_ID={}, 사유={}", order.getOrderId(), e.getMessage());
                integrationLogger.logFailure(order.getOrderId(), "SHIPMENT_TB 적재", e.getMessage());
                failedCount++;
            }
        }

        // 배치 작업 전체 결과 로깅
        integrationLogger.logBatch("운송 배치", successCount, failedCount);
        log.info("=== 운송 배치 종료: 전체 {}건 중 {}건 성공 ===", pendingOrders.size(), successCount);
        
        return successCount;
    }
}
