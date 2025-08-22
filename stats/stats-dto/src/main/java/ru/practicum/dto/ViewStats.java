package ru.practicum.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;

}