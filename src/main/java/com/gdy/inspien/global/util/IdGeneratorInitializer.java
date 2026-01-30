package com.gdy.inspien.global.util;

import com.gdy.inspien.order.repository.OrderRepository;
import com.gdy.inspien.shipment.repository.ShipmentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdGeneratorInitializer {

    private final IdGenerator orderIdGenerator;
    private final IdGenerator shipmentIdGenerator;
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;

    @PostConstruct
    public void init() {
        log.info("=== IdGenerators 초기화 시작 ===");
        
        // 1. Order ID Generator 초기화
        String maxOrderId = orderRepository.findMaxOrderId();
        orderIdGenerator.initCounterFromMaxId(maxOrderId);
        if (maxOrderId != null) {
            log.info("DB 마지막 ORDER_ID: {} -> 다음 ID부터 생성 준비 완료", maxOrderId);
        } else {
            log.info("DB에 기존 주문 없음 -> A000부터 시작 (Order)");
        }

        // 2. Shipment ID Generator 초기화
        String maxShipId = shipmentRepository.findMaxShipId();
        shipmentIdGenerator.initCounterFromMaxId(maxShipId);
        if (maxShipId != null) {
            log.info("DB 마지막 SHIP_ID: {} -> 다음 ID부터 생성 준비 완료", maxShipId);
        } else {
            log.info("DB에 기존 운송 없음 -> A000부터 시작 (Shipment)");
        }
        
        log.info("=== IdGenerators 초기화 완료 ===");
    }
}
