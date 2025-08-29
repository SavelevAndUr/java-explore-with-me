package ru.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto extends UserShortDto {
    @Email(regexp = ".+@.+\\..+")
    @Length(min = 6, max = 254)
    @NotNull
    private String email;

    @Builder(builderMethodName = "childBuilder")
    public UserDto(Long id, @NotNull String name, String email) {
        super(id, name);
        this.email = email;
    }
}