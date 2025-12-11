package com.csl.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data // Genera getters, setters, toString, etc.
@NoArgsConstructor // Constructor vacío
@Entity
@Table(name = "log_events")
public class LogEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(name = "event_time")
    private LocalDateTime eventTime = LocalDateTime.now();

    @Column(name = "log_level")
    private String logLevel;

    @Column(name = "source_module")
    private String sourceModule;

    @Column(name = "event_message", columnDefinition = "TEXT")
    private String eventMessage;

    public LogEvent(String logLevel, String sourceModule, String eventMessage) {
        this.logLevel = logLevel;
        this.sourceModule = sourceModule;
        this.eventMessage = eventMessage;
    }
}