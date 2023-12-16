package ru.yandex.practicum.model;

import lombok.*;
import ru.yandex.practicum.model.validation.ValidReleaseDate;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank(message = "Title cannot be empty")
    @NotNull(message = "Title cannot be empty")
    private String name;
    @Size(max = 200, message = "Description cannot be more than 200 characters")
    private String description;
    @ValidReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "Film duration must be positive")
    private Integer duration;
    private Integer rate;
    private List<Genre> genres = new ArrayList<>();
    private Mpa mpa;
}