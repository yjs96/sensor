package com.sensor.sensorservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class TimeSeriesDataDTO {
    private LocalDateTime dateTime;
    private Map<String, Double> data;
}
