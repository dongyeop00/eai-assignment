package com.gdy.inspien.order.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement(name = "ITEM")
@XmlAccessorType(XmlAccessType.FIELD)
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
