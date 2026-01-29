package com.gdy.inspien.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class OrderDTO {

    private String orderId;
    private String userId;
    private String itemId;
    private String applicantKey; // 지원자 키
    private String name;
    private String address;
    private String itemName;
    private String price;
    private String status;       // N: 미전송, Y: 전송완료

}
