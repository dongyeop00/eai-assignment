package com.gdy.inspien.order.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement(name = "ITEM")
public class OrderItem {

    @XmlElement(name = "USER_ID")
    private String userId;

    @XmlElement(name = "ITEM_ID")
    private String itemId;

    @XmlElement(name = "ITEM_NAME")
    private String itemName;

    @XmlElement(name = "PRICE")
    private String price;
}
