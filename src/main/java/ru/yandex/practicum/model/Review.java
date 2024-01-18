package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    private Integer reviewId;
    @NotBlank(message = "Content must be not blank")
    @Size(max = 100, message = "Content must be less than 500 characters")
    private String content;
    @NotNull(message = "IsPositive field must be not null")
    private Boolean isPositive;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer filmId;
    private int useful;
}