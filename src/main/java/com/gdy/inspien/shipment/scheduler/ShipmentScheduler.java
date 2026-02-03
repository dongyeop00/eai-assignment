package com.gdy.inspien.shipment.scheduler;

import com.gdy.inspien.shipment.service.ShipmentService;
import com.gdy.inspien.shipment.service.impl.ShipmentServiceSingleImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShipmentScheduler {

    private final ShipmentService shipmentService;

    public ShipmentScheduler(@Qualifier("ShipmentBatch")ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    /**
     * 5분 주기로 운송 배치 실행
     */
    @Scheduled(cron = "${SCHEDULE_TIME}")
    public void batchSchedule() {
        log.info("스케줄러 실행: 운송 배치 시작");

        int processedCount = shipmentService.batchOrder();

        log.info("스케줄러 종료: {}건 처리 완료", processedCount);
    }
}
