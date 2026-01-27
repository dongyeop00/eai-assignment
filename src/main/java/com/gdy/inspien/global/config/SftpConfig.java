package com.gdy.inspien.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SftpConfig {

    @Value("${SFTP_HOST}")
    private String host;

    @Value("${SFTP_PORT}")
    private int port;

    @Value("${SFTP_USERNAME}")
    private String username;

    @Value("${SFTP_PASSWORD}")
    private String password;

    @Value("${SFTP_REMOTE_PATH}")
    private String remotePath;

}