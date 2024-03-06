package com.wego.carpark.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CarParkAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String carParkNo;

    private String lotType;

    private Integer totalLot;

    private Integer availableLot;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;

}
