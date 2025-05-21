package com.example.batch_runner.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class FavoriteRoute {

    @Id
    private String routeId;
}
