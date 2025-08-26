package ru.practicum.model;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 250)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    @Column(name = "email", nullable = false, unique = true)
    private String email;
}