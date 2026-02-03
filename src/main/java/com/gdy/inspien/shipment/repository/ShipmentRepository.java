package com.gdy.inspien.shipment.repository;

import com.gdy.inspien.global.exception.ErrorCode;
import com.gdy.inspien.global.exception.IntegrationException;
import com.gdy.inspien.shipment.dto.ShipmentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ShipmentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${APPLICANT_KEY}")
    private String applicantKey;

    /**
     * 운송 데이터 저장
     */
    public void save(ShipmentDTO shipment) {
        String sql = """
            INSERT INTO SHIPMENT_TB (SHIPMENT_ID, ORDER_ID, ITEM_ID, APPLICANT_KEY, ADDRESS)
            VALUES (?, ?, ?, ?, ?)
            """;
        try {
            jdbcTemplate.update(sql,
                    shipment.getShipId(),
                    shipment.getOrderId(),
                    shipment.getItemId(),
                    shipment.getApplicantKey(),
                    shipment.getAddress()
            );

            log.info("운송 저장 완료: SHIPMENT_ID={}, ORDER_ID={}", shipment.getShipId(), shipment.getOrderId());
        } catch (DuplicateKeyException e) {
            log.error("운송 저장 실패 - 중복 키: SHIPMENT_ID={}", shipment.getShipId());
            throw new IntegrationException(ErrorCode.DB_DUPLICATE_KEY_ERROR, e);
        } catch (DataIntegrityViolationException e) {
            log.error("운송 저장 실패 - 무결성 위반: {}", e.getMessage());
            throw new IntegrationException(ErrorCode.DB_DATA_INTEGRITY_ERROR, e);
        } catch (DataAccessException e) {
            log.error("운송 저장 실패: {}", e.getMessage());
            throw new IntegrationException(ErrorCode.DB_INSERT_ERROR, e);
        }
    }

    /**
     * 운송 데이터 배치 저장
     */
    public void saveAllBatch(List<ShipmentDTO> shipments) {
        String sql = "INSERT INTO SHIPMENT_TB (SHIPMENT_ID, ORDER_ID, ITEM_ID, APPLICANT_KEY, ADDRESS) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ShipmentDTO shipment = shipments.get(i);
                ps.setString(1, shipment.getShipId());
                ps.setString(2, shipment.getOrderId());
                ps.setString(3, shipment.getItemId());
                ps.setString(4, shipment.getApplicantKey());
                ps.setString(5, shipment.getAddress());
            }

            @Override
            public int getBatchSize() { return shipments.size(); }
        });
    }

    /**
     * 현재 가장 큰 SHIPMENT_ID 조회
     */
    public String findMaxShipId() {
        String sql = "SELECT MAX(SHIPMENT_ID) FROM SHIPMENT_TB WHERE APPLICANT_KEY = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, applicantKey);
        } catch (DataAccessException e) {
            log.warn("최대 SHIP_ID 조회 실패 (데이터 없음 가능): {}", e.getMessage());
            return null;
        }
    }
}
