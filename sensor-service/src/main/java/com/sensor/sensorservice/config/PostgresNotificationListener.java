package com.sensor.sensorservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sensor.sensorservice.dto.SensorLogDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class PostgresNotificationListener {
    private final DataSource dataSource;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private ExecutorService executorService;
    private volatile boolean running = true;

    public PostgresNotificationListener(DataSource dataSource,
                                        SimpMessagingTemplate messagingTemplate) {
        this.dataSource = dataSource;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime 처리를 위해 추가
    }

    @PostConstruct
    public void startListening() {
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::listen);
    }

    private void listen() {
        try (Connection conn = dataSource.getConnection()) {
            PGConnection pgConn = conn.unwrap(PGConnection.class);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("LISTEN sensor_change");
                log.info("Listening for sensor changes...");

                while (running) {
                    PGNotification[] notifications = pgConn.getNotifications(1000);

                    if (notifications != null) {
                        for (PGNotification notification : notifications) {
                            try {
                                String payload = notification.getParameter();
                                log.debug("Received notification: {}", payload);

                                SensorLogDTO sensorLog = objectMapper.readValue(payload, SensorLogDTO.class);
                                messagingTemplate.convertAndSend("/topic/sensor", sensorLog);
                                log.debug("Sent to websocket: {}", sensorLog);
                            } catch (Exception e) {
                                log.error("Error processing notification: ", e);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error in notification listener: ", e);
            if (running) {
                log.info("Attempting to reconnect...");
                startListening(); // 재연결 시도
            }
        }
    }

    @PreDestroy
    public void stopListening() {
        running = false;
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
