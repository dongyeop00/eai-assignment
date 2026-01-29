package com.gdy.inspien.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class Logger {

    private static final String LOG_FILE_PATH = "logs/integration.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 성공 로그 기록
     */
    public void logSuccess(String orderId, String operation) {
        String message = String.format("[%s] [SUCCESS] [%s] %s", LocalDateTime.now().format(formatter), orderId, operation);
        writeLog(message);
        log.info(message);
    }

    /**
     * 실패 로그 기록
     */
    public void logFailure(String orderId, String operation, String errorMessage) {
        String message = String.format("[%s] [FAILURE] [%s] %s - 오류: %s", LocalDateTime.now().format(formatter), orderId, operation, errorMessage);
        writeLog(message);
        log.error(message);
    }

    /**
     * 배치 작업 로그 기록
     */
    public void logBatch(String operation, int processedCount, int failedCount) {
        String message = String.format("[%s] [BATCH] %s - 처리: %d건, 실패: %d건", LocalDateTime.now().format(formatter), operation, processedCount, failedCount);
        writeLog(message);
        log.info(message);
    }

    /**
     * 쓰기 작업
     * @param message : 기록할 메세지
     */
    private void writeLog(String message) {
        File logFile = new File(LOG_FILE_PATH);
        File parentDir = logFile.getParentFile();
        
        // 부모 디렉토리가 없으면 생성
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            writer.println(message);
        } catch (IOException e) {
            log.error("로그 파일 기록 실패: {}", e.getMessage());
        }
    }
}