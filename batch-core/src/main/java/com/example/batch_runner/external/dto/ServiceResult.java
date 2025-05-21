package com.example.batch_runner.external.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "ServiceResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceResult {

    @XmlElement(name = "comMsgHeader")
    private ComMsgHeader comMsgHeader;

    @XmlElement(name = "msgHeader")
    private MsgHeader msgHeader;

    @XmlElement(name = "msgBody")
    private MsgBody msgBody;
}