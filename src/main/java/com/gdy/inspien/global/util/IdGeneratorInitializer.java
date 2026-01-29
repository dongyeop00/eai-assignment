package com.gdy.inspien.global.util;

import com.gdy.inspien.order.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 서버 기동 시 IdGenerator의 카운터를 DB 최대값으로 초기화하는 컴포넌트
 * @PostConstruct를 사용하여 빈 생성 직후 자동 실행됨
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdGeneratorInitializer {

    private final IdGenerator idGenerator;
    private final OrderRepository orderRepository;

    @PostConstruct
    public void init() {
        log.info("=== IdGenerator 초기화 시작 ===");
        
        String maxId = orderRepository.findMaxOrderId();
        idGenerator.initCounterFromMaxId(maxId);

        if (maxId != null) {
            log.info("DB 마지막 ORDER_ID: {} -> 다음 ID부터 생성 준비 완료", maxId);
        } else {
            log.info("DB에 기존 주문 없음 -> A000부터 시작");
        }
        
        log.info("=== IdGenerator 초기화 완료 ===");
    }
}
