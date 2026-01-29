package com.gdy.inspien.integration.file;

import com.gdy.inspien.order.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ReceiptFileGenerator {

    @Value("${APPLICANT_NAME}")
    private String applicantName;

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 영수증 파일명 생성
     * 형식: INSPIEN_[참여자명(한글)]_[yyyyMMddHHmmss].txt
     */
    public String generateFileName() {
        String timestamp = LocalDateTime.now().format(FILE_DATE_FORMAT);
        return String.format("INSPIEN_[%s]_[%s].txt", applicantName, timestamp);
    }

    /**
     * 영수증 파일 내용 생성
     * 형식: ORDER_ID^USER_ID^ITEM_ID^APPLICANT_KEY^NAME^ADDRESS^ITEM_NAME^PRICE\n
     *      A113^USER1^ITEM1^지원자키^홍길동^서울특별시 금천구^청바지^21000\n
     */
    public String generateContent(List<OrderDTO> orders) {
        StringBuilder sb = new StringBuilder();
        for (OrderDTO order : orders) {
            sb.append(order.getOrderId()).append("^")
              .append(order.getUserId()).append("^")
              .append(order.getItemId()).append("^")
              .append(order.getApplicantKey()).append("^")
              .append(order.getName()).append("^")
              .append(order.getAddress()).append("^")
              .append(order.getItemName()).append("^")
              .append(order.getPrice()).append("\n");
        }
        return sb.toString();
    }
}