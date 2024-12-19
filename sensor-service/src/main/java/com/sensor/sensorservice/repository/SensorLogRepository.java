package com.sensor.sensorservice.repository;

import com.sensor.sensorservice.entity.SensorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorLogRepository extends JpaRepository<SensorLog, Integer> {
    // 최근 n분 데이터 조회
    @Query("SELECT s FROM SensorLog s WHERE s.sensingTime >= :startTime")
    List<SensorLog> findRecentLogs(@Param("startTime") LocalDateTime startTime);

    // 특정 날짜 범위의 ZA, ZAA 평균값 조회
    @Query("SELECT s.sensorType, AVG(s.val) FROM SensorLog s " +
            "WHERE s.sensingTime BETWEEN :startDate AND :endDate " +
            "AND s.sensorType IN ('ZA', 'ZAA') " +
            "GROUP BY s.sensorType")
    List<Object[]> findAveragesByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 특정 시간대의 센서 데이터 조회 (10개씩 묶음)
    @Query("SELECT s FROM SensorLog s " +
            "WHERE s.sensingTime >= :startTime AND s.sensingTime < :endTime " +
            "ORDER BY s.sensingTime, s.sensorType")
    List<SensorLog> findByTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // 가장 최근 센서 데이터 조회
    @Query("SELECT s FROM SensorLog s " +
            "WHERE s.sensingTime = (SELECT MAX(sl.sensingTime) FROM SensorLog sl)")
    List<SensorLog> findLatestSensorLogs();
}
