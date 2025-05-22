package com.example.batch_runner.domain;


import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class RouteStopInfoId implements Serializable {

    private String routeId;
    private Integer nodeSeq;

    public RouteStopInfoId(String routeId, Integer nodeSeq) {
        this.routeId = routeId;
        this.nodeSeq = nodeSeq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteStopInfoId)) return false;
        RouteStopInfoId that = (RouteStopInfoId) o;
        return Objects.equals(routeId, that.routeId) &&
                Objects.equals(nodeSeq, that.nodeSeq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeId, nodeSeq);
    }
}