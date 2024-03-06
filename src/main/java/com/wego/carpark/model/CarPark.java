package com.wego.carpark.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CarPark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String carParkNo;

    private String address;

    private Double longitude;

    private Double latitude;

    private String carParkType;

    private String typeOfParkingSystem;

    private String shortTermParking;

    private String freeParking;

    private String nightParking;

    private Integer carParkDecks;

    private Double gantryHeight;

    private String carParkBasement;
}
