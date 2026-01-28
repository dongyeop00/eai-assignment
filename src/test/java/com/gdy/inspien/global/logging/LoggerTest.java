package com.gdy.inspien.global.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class LoggerTest {

    private Logger logger;
    private static final Path LOG_PATH = Path.of("logs/integration.log");

    @BeforeEach
    void setUp() throws IOException {
        logger = new Logger();

        Files.createDirectories(LOG_PATH.getParent());
        Files.deleteIfExists(LOG_PATH);
        Files.createFile(LOG_PATH);
    }

    @AfterEach
    void clear() throws IOException {
        Files.deleteIfExists(LOG_PATH);
    }

    @Test
    @DisplayName("성공 로그가 파일에 정상적으로 기록됨")
    void logSuccess_writeToFile() throws IOException {
        // given
        String orderId = "A001";
        String operation = "주문 생성";

        // when
        logger.logSuccess(orderId, operation);

        //then
        List<String> lines = Files.readAllLines(LOG_PATH);

        for(String str : lines){
            System.out.println("로그 결과 : " + str);
        }

        assertThat(lines).hasSize(1);
        assertThat(lines.get(0))
                .contains("[SUCCESS")
                .contains(orderId)
                .contains(operation);
    }

    @Test
    @DisplayName("실패 로그가 파일에 정상적으로 기록됨")
    void logFailure_writeToFile() throws IOException {
        // given
        String orderId = "ORDER-002";
        String operation = "결제 처리";
        String errorMessage = "결제 실패";

        // when
        logger.logFailure(orderId, operation, errorMessage);

        // then
        List<String> lines = Files.readAllLines(LOG_PATH);

        for(String str : lines){
            System.out.println("로그 결과 : " + str);
        }

        assertThat(lines).hasSize(1);
        assertThat(lines.get(0))
                .contains("[FAILURE]")
                .contains(orderId)
                .contains(operation)
                .contains(errorMessage);
    }

    @Test
    @DisplayName("배치 로그가 파일에 정상적으로 기록됨")
    void logBatch_writeToFile() throws IOException {
        // given
        String operation = "배치";
        int processed = 100;
        int failed = 3;

        // when
        logger.logBatch(operation, processed, failed);

        // then
        List<String> lines = Files.readAllLines(LOG_PATH);

        for(String str : lines){
            System.out.println("로그 결과 : " + str);
        }

        assertThat(lines).hasSize(1);
        assertThat(lines.get(0))
                .contains("[BATCH]")
                .contains(operation)
                .contains("처리: 100건")
                .contains("실패: 3건");
    }

    @Test
    @DisplayName("여러 로그가 누적 기록됨")
    void multipleLogs_append() throws IOException {
        // when
        logger.logSuccess("ORDER-1", "작업1");
        logger.logFailure("ORDER-2", "작업2", "에러");
        logger.logBatch("배치", 10, 1);

        // then
        List<String> lines = Files.readAllLines(LOG_PATH);

        for(String str : lines){
            System.out.println("로그 결과 : " + str);
        }

        assertThat(lines).hasSize(3);
    }
}