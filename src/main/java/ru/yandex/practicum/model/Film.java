package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.yandex.practicum.model.validation.ValidReleaseDate;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    private String name;
    @NonNull
    @Size(max = 200, message = "Description cannot be more than 200 characters")
    private String description;
    @ValidReleaseDate
    private LocalDate releaseDate;
    private Integer duration;
    private Integer rate;
    private List<Genre> genres = new ArrayList<>();
    private Mpa mpa;
    private List<Director> directors = new ArrayList<>();
}