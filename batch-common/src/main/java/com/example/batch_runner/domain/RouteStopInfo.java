package com.example.batch_runner.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class RouteStopInfo {

    @EmbeddedId
    private RouteStopInfoId id;

    @Column
    private String routeName;

    @Column
    private String nodeId;

    @Column
    private String arsId;

    @Column
    private String nodeName;

    @Column(name = "pos_x")
    private Double posX;

    @Column(name = "pos_y")
    private Double posY;
}
