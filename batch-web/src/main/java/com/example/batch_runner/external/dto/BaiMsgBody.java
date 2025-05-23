package com.example.batch_runner.external.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class BaiMsgBody {

    @XmlElement(name = "itemList")
    private List<BusArrivalInfoDto> itemList;
}