package com.sensor.sensorservice.service;

import com.sensor.sensorservice.dto.SensorLogDTO;
import com.sensor.sensorservice.dto.TimeSeriesDataDTO;
import com.sensor.sensorservice.entity.SensorLog;
import com.sensor.sensorservice.repository.SensorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SensorService {
    private final SensorLogRepository sensorLogRepository;

    public List<SensorLogDTO> getRecentLogs(int minutes) {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(minutes);
        return sensorLogRepository.findRecentLogs(startTime)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SensorLogDTO getSensorLogDetail(Long logId) {
        return sensorLogRepository.findById(logId.intValue())
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Sensor log not found"));
    }

    public Map<String, Double> getAveragesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = sensorLogRepository.findAveragesByDateRange(startDate, endDate);
        Map<String, Double> averages = new HashMap<>();

        for (Object[] result : results) {
            String sensorType = (String) result[0];
            Double average = (Double) result[1];
            averages.put(sensorType, average);
        }

        return averages;
    }

    public List<TimeSeriesDataDTO> getTimeSeriesData(LocalDateTime startTime, LocalDateTime endTime) {
        List<SensorLog> logs = sensorLogRepository.findByTimeRange(startTime, endTime);

        // Group by sensingTime
        Map<LocalDateTime, List<SensorLog>> groupedLogs = logs.stream()
                .collect(Collectors.groupingBy(SensorLog::getSensingTime));

        List<TimeSeriesDataDTO> timeSeriesData = new ArrayList<>();

        groupedLogs.forEach((dateTime, sensorLogs) -> {
            TimeSeriesDataDTO dto = new TimeSeriesDataDTO();
            dto.setDateTime(dateTime);

            Map<String, Double> data = sensorLogs.stream()
                    .collect(Collectors.toMap(
                            SensorLog::getSensorType,
                            SensorLog::getVal
                    ));

            dto.setData(data);
            timeSeriesData.add(dto);
        });

        // Sort by dateTime
        timeSeriesData.sort(Comparator.comparing(TimeSeriesDataDTO::getDateTime));

        return timeSeriesData;
    }

    public List<SensorLogDTO> getLatestSensorLogs() {
        return sensorLogRepository.findLatestSensorLogs()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private SensorLogDTO convertToDTO(SensorLog entity) {
        SensorLogDTO dto = new SensorLogDTO();
        dto.setLogId(entity.getLogId());
        dto.setTenantId(entity.getTenantId());
        dto.setGatewayId(entity.getGatewayId());
        dto.setSensorId(entity.getSensorId());
        dto.setSensorType(entity.getSensorType());
        dto.setVal(entity.getVal());
        dto.setSensingTime(entity.getSensingTime());
        dto.setCreatedTime(entity.getCreatedTime());
        return dto;
    }
}
