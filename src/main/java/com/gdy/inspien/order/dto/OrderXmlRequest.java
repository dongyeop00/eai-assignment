package com.gdy.inspien.order.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "ROOT")
public class OrderXmlRequest {

    @XmlElement(name = "HEADER")
    private List<OrderHeader> headers;

    @XmlElement(name = "ITEM")
    private List<OrderItem> items;
}