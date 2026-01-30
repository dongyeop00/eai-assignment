package com.gdy.inspien.shipment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ShipmentDTO {

    private String shipId;
    private String orderId;
    private String itemId;
    private String applicantKey;
    private String address;
}
