package com.gdy.inspien.shipment.service;

import com.gdy.inspien.global.logging.Logger;
import com.gdy.inspien.global.util.IdGenerator;
import com.gdy.inspien.order.dto.OrderDTO;
import com.gdy.inspien.order.repository.OrderRepository;
import com.gdy.inspien.shipment.dto.ShipmentDTO;
import com.gdy.inspien.shipment.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private Logger integrationLogger;

    @InjectMocks
    private ShipmentService shipmentService;

    private OrderDTO testOrder;

    @BeforeEach
    void setUp() {
        testOrder = OrderDTO.builder()
                .orderId("A001")
                .userId("USER1")
                .itemId("ITEM1")
                .applicantKey("TEST000001")
                .name("홍길동")
                .address("서울특별시 금천구")
                .itemName("청바지")
                .price("21000")
                .status("N")
                .build();
    }

    @Test
    @DisplayName("배치 성공 - 미전송 주문 1건 처리")
    void batchOrder_success() {
        // given
        when(orderRepository.findPendingOrders()).thenReturn(List.of(testOrder));
        when(idGenerator.generate()).thenReturn("S001");

        // when
        int result = shipmentService.batchOrder();

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("배치 - 미전송 주문 없음")
    void batchOrder_noOrders() {
        // given
        when(orderRepository.findPendingOrders()).thenReturn(Collections.emptyList());

        // when
        int result = shipmentService.batchOrder();

        // then
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("배치 실패 - DB 저장 실패 시 0건 반환")
    void batchOrder_failure() {
        // given
        when(orderRepository.findPendingOrders()).thenReturn(List.of(testOrder));
        when(idGenerator.generate()).thenReturn("S001");
        doThrow(new RuntimeException("DB 저장 실패")).when(shipmentRepository).save(any(ShipmentDTO.class));

        // when
        int result = shipmentService.batchOrder();

        // then
        assertThat(result).isEqualTo(0);
    }
}
