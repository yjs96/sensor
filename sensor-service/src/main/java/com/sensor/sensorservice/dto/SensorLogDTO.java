package com.sensor.sensorservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SensorLogDTO {
    private Long logId;
    private String tenantId;
    private String gatewayId;
    private String sensorId;
    private String sensorType;
    private Double val;
    private LocalDateTime sensingTime;
    private LocalDateTime createdTime;
}
