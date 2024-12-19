package com.sensor.sensorservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensorlog")
@Getter
@Setter
@NoArgsConstructor
public class SensorLog {
    @Id
    @Column(name = "logid")
    private Long logId;

    @Column(name = "tenantid")
    private String tenantId;

    @Column(name = "gatewayid")
    private String gatewayId;

    @Column(name = "sensorid")
    private String sensorId;

    @Column(name = "sensortype")
    private String sensorType;

    @Column(name = "val")
    private Double val;

    @Column(name = "sensingtime")
    private LocalDateTime sensingTime;

    @Column(name = "createdtime")
    private LocalDateTime createdTime;
}
