package com.example.batch_runner.external.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class BusPosition {
    private int busType;
    private int congetion;
    private String dataTm;
    private double fullSectDist;
    private double gpsX;
    private double gpsY;
    private int isFullFlag;
    private int islastyn;
    private int isrunyn;
    private int lastStTm;
    private long lastStnId;
    private long nextStId;
    private int nextStTm;
    private String plainNo;
    private double posX;
    private double posY;
    private double rtDist;
    private double sectDist;
    private int sectOrd;
    private long sectionId;
    private int stopFlag;
    private long trnstnid;
    private long vehId;
}