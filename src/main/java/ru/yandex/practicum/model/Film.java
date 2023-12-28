package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.model.validation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    private String name;
    private String description;
    @ValidReleaseDate
    private LocalDate releaseDate;
    private Integer duration;
    private Integer rate;
    private List<Genre> genres = new ArrayList<>();
    private Mpa mpa;
    private List<Director> directors = new ArrayList<>();
}