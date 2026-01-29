package com.gdy.inspien.integration.sftp;

import com.gdy.inspien.global.config.SftpConfig;
import com.gdy.inspien.global.exception.ErrorCode;
import com.gdy.inspien.global.exception.IntegrationException;
import com.gdy.inspien.integration.file.ReceiptFileGenerator;
import com.gdy.inspien.order.dto.OrderDTO;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SftpClient {

    private final SftpConfig sftpConfig;

    private final ReceiptFileGenerator receiptFileGenerator;

    /**
     * 영수증 파일 생성 및 SFTP 업로드
     */
    public void uploadReceipt(List<OrderDTO> orders) {
        String fileName = receiptFileGenerator.generateFileName();
        String content = receiptFileGenerator.generateContent(orders);
        Session session = null;
        ChannelSftp channel = null;

        try {
            // 1. SFTP 세션 연결
            JSch jsch = new JSch();

            session = jsch.getSession(
                    sftpConfig.getUsername(),
                    sftpConfig.getHost(),
                    sftpConfig.getPort()
            );

            session.setPassword(sftpConfig.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 2. SFTP 채널 열기
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            // 3. 원격 디렉토리로 이동
            channel.cd(sftpConfig.getRemotePath());

            // 4. 파일 업로드
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            channel.put(inputStream, fileName);

            log.info("SFTP 업로드 완료: {}", fileName);
        } catch (Exception e) {
            log.error("SFTP 업로드 실패: {}", e.getMessage());
            throw new IntegrationException(ErrorCode.SFTP_UPLOAD_ERROR, e);
        } finally {
            // 5. 연결 종료
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
}