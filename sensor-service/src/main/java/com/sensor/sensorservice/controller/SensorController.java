package com.sensor.sensorservice.controller;

import com.sensor.sensorservice.dto.SensorLogDTO;
import com.sensor.sensorservice.dto.TimeSeriesDataDTO;
import com.sensor.sensorservice.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sensors")
public class SensorController {
    private final SensorService sensorService;

    @GetMapping("/recent/{minutes}")
    public ResponseEntity<List<SensorLogDTO>> getRecentLogs(@PathVariable int minutes) {
        return ResponseEntity.ok(sensorService.getRecentLogs(minutes));
    }

    @GetMapping("/{logId}")
    public ResponseEntity<SensorLogDTO> getSensorLogDetail(@PathVariable Long logId) {
        return ResponseEntity.ok(sensorService.getSensorLogDetail(logId));
    }

    @GetMapping("/average")
    public ResponseEntity<Map<String, Double>> getAveragesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(sensorService.getAveragesByDateRange(startDate, endDate));
    }

    @GetMapping("/time-series")
    public ResponseEntity<List<TimeSeriesDataDTO>> getTimeSeriesData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(sensorService.getTimeSeriesData(startTime, endTime));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<SensorLogDTO>> getLatestSensorLogs() {
        return ResponseEntity.ok(sensorService.getLatestSensorLogs());
    }
}
