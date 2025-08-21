package ru.practicum.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "ewm_stats_db")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StatRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String app;
    @NonNull
    private String uri;
    @NonNull
    private String ip;
    @NonNull
    private LocalDateTime timestamp;
}