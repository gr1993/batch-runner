package com.example.batch_runner.external.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class MsgHeader {
    private String headerCd;
    private String headerMsg;
    private int itemCount;
}