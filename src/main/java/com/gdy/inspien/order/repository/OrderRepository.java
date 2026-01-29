package com.gdy.inspien.order.repository;

import com.gdy.inspien.global.exception.ErrorCode;
import com.gdy.inspien.global.exception.IntegrationException;
import com.gdy.inspien.order.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${APPLICANT_KEY}")
    private String applicantKey;

    /**
     * 주문 데이터 저장
     */
    public void save(OrderDTO order) {
        String sql = """
            INSERT INTO ORDER_TB (ORDER_ID, USER_ID, ITEM_ID, APPLICANT_KEY, NAME, ADDRESS, ITEM_NAME, PRICE, STATUS)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try {
            jdbcTemplate.update(sql,
                    order.getOrderId(),
                    order.getUserId(),
                    order.getItemId(),
                    order.getApplicantKey(),
                    order.getName(),
                    order.getAddress(),
                    order.getItemName(),
                    order.getPrice(),
                    order.getStatus()
            );

            log.info("주문 저장 완료: ORDER_ID={}, APPLICANT_KEY={}", order.getOrderId(), order.getApplicantKey());
        } catch (DuplicateKeyException e) {
            log.error("주문 저장 실패 - 중복 키: ORDER_ID={}", order.getOrderId());
            throw new IntegrationException(ErrorCode.DB_DUPLICATE_KEY_ERROR, e);
        } catch (DataIntegrityViolationException e) {
            log.error("주문 저장 실패 - 무결성 위반: {}", e.getMessage());
            throw new IntegrationException(ErrorCode.DB_DATA_INTEGRITY_ERROR, e);
        } catch (DataAccessException e) {
            log.error("주문 저장 실패: {}", e.getMessage());
            throw new IntegrationException(ErrorCode.DB_INSERT_ERROR, e);
        }
    }

    /**
     * 여러 주문 데이터 저장
     */
    public void saveAll(List<OrderDTO> orders) {
        for (OrderDTO order : orders) {
            save(order);
        }
    }

    /**
     * 미전송 주문 조회 (STATUS = 'N')
     */
    public List<OrderDTO> findPendingOrders() {
        String sql = """
            SELECT ORDER_ID, USER_ID, ITEM_ID, APPLICANT_KEY, NAME, ADDRESS, ITEM_NAME, PRICE, STATUS
            FROM ORDER_TB
            WHERE APPLICANT_KEY = ? AND STATUS = 'N'
            """;
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> OrderDTO.builder()
                            .orderId(rs.getString("ORDER_ID"))
                            .userId(rs.getString("USER_ID"))
                            .itemId(rs.getString("ITEM_ID"))
                            .applicantKey(rs.getString("APPLICANT_KEY"))
                            .name(rs.getString("NAME"))
                            .address(rs.getString("ADDRESS"))
                            .itemName(rs.getString("ITEM_NAME"))
                            .price(rs.getString("PRICE"))
                            .status(rs.getString("STATUS"))
                            .build(),
                    applicantKey
            );
        } catch (DataAccessException e) {
            log.error("미전송 주문 조회 실패: {}", e.getMessage());
            throw new IntegrationException(ErrorCode.DB_SELECT_ERROR, e);
        }
    }

    /**
     * 주문 상태 업데이트 (N → Y)
     */
    public void updateStatus(String orderId, String status) {
        String sql = """
            UPDATE ORDER_TB 
            SET STATUS = ?
            WHERE ORDER_ID = ? AND APPLICANT_KEY = ?
            """;
        try {
            jdbcTemplate.update(sql, status, orderId, applicantKey);
            log.info("주문 상태 업데이트: ORDER_ID={}, STATUS={}", orderId, status);
        } catch (DataAccessException e) {
            log.error("주문 상태 업데이트 실패: {}", e.getMessage());
            throw new IntegrationException(ErrorCode.DB_UPDATE_ERROR, e);
        }
    }

    /**
     * 현재 가장 큰 ORDER_ID 조회
     */
    public String findMaxOrderId() {
        String sql = "SELECT MAX(ORDER_ID) FROM ORDER_TB WHERE APPLICANT_KEY = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, applicantKey);
        } catch (DataAccessException e) {
            log.warn("최대 ORDER_ID 조회 실패 (데이터 없음 가능): {}", e.getMessage());
            return null;
        }
    }
}