package com.gdy.inspien.order.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement(name = "HEADER")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderHeader {

    @XmlElement(name = "USER_ID")
    private String userId;

    @XmlElement(name = "NAME")
    private String name;

    @XmlElement(name = "ADDRESS")
    private String address;

    @XmlElement(name = "STATUS")
    private String status;
}
