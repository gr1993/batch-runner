package com.example.batch_runner.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class RouteBusPos {

    @Id
    private long vehId;

    @Column
    private String routeId;

    @Column
    private String plainNo;

    @Column
    private int congetion;

    @Column(name = "pos_x")
    private Double posX;

    @Column(name = "pos_y")
    private Double posY;
}
