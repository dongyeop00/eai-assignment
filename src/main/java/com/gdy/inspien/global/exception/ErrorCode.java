package com.gdy.inspien.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // JDBC 관련
    DB_CONNECTION_ERROR("DB001", "데이터베이스 연결에 실패했습니다"),
    DB_INSERT_ERROR("DB002", "데이터 저장에 실패했습니다"),
    DB_SELECT_ERROR("DB003", "데이터 조회에 실패했습니다"),
    DB_UPDATE_ERROR("DB004", "데이터 수정에 실패했습니다"),
    DB_DELETE_ERROR("DB005", "데이터 삭제에 실패했습니다"),

    // SFTP 관련
    SFTP_CONNECTION_ERROR("SFTP001", "SFTP 서버 연결에 실패했습니다"),
    SFTP_UPLOAD_ERROR("SFTP002", "파일 업로드에 실패했습니다"),

    // XML 파싱 관련
    XML_PARSE_ERROR("XML001", "XML 파싱에 실패했습니다"),

    // 검증 관련
    VALIDATION_ERROR("VAL001", "데이터 검증에 실패했습니다"),

    // 시스템 오류
    SYSTEM_ERROR("SYS001", "시스템 오류가 발생했습니다");

    private final String code;
    private final String message;
}