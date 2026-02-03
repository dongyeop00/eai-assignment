package com.gdy.inspien.integration.sftp;

import com.gdy.inspien.global.config.SftpConfig;
import com.gdy.inspien.global.exception.ErrorCode;
import com.gdy.inspien.global.exception.IntegrationException;
import com.gdy.inspien.integration.file.ReceiptFileGenerator;
import com.gdy.inspien.order.dto.OrderDTO;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
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
            session = createSession();

            // 2. SFTP 채널 열기
            channel = openChannel(session);

            // 3. 파일 업로드
            uploadFile(channel, fileName, content);

            log.info("SFTP 업로드 완료: {}", fileName);

        } finally {
            // 4. 연결 종료
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
                log.debug("SFTP 채널 연결 해제");
            }

            if (session != null && session.isConnected()) {
                session.disconnect();
                log.debug("SFTP 세션 연결 해제");
            }
        }
    }

    /**
     * SFTP 세션 생성 및 연결
     */
    private Session createSession() {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(sftpConfig.getUsername(), sftpConfig.getHost(), sftpConfig.getPort());
            session.setPassword(sftpConfig.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            log.debug("SFTP 세션 연결 성공: {}@{}:{}", sftpConfig.getUsername(), sftpConfig.getHost(), sftpConfig.getPort());
            return session;

        } catch (JSchException e) {
            log.error("SFTP 세션 연결 실패: {}", e.getMessage());
            throw new IntegrationException(ErrorCode.SFTP_CONNECTION_ERROR, e);
        }
    }

    /**
     * SFTP 채널 열기
     */
    private ChannelSftp openChannel(Session session) {
        try {
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            // 원격 디렉토리로 이동
            channel.cd(sftpConfig.getRemotePath());

            log.debug("SFTP 채널 연결 성공: {}", sftpConfig.getRemotePath());
            return channel;
        } catch (JSchException e) {
            log.error("SFTP 채널 열기 실패: {}", e.getMessage());
            throw new IntegrationException(ErrorCode.SFTP_CONNECTION_ERROR, e);
        } catch (SftpException e) {
            log.error("SFTP 원격 디렉토리 이동 실패: {}", e.getMessage());
            throw new IntegrationException(ErrorCode.SFTP_UPLOAD_ERROR, e);
        }
    }

    /**
     * 파일 업로드
     */
    private void uploadFile(ChannelSftp channel, String fileName, String content) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(
                    content.getBytes(StandardCharsets.UTF_8)
            );
            channel.put(inputStream, fileName);

        } catch (SftpException e) {
            log.error("SFTP 파일 업로드 실패: {}", e.getMessage());
            throw new IntegrationException(ErrorCode.SFTP_UPLOAD_ERROR, e);
        }
    }

}