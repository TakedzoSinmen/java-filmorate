package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.model.validation.ValidReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotBlank
    @Size(max = 200, message = "Description cannot be more than 200 characters")
    private String description;
    @ValidReleaseDate
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private Integer duration;
    private Integer rate;
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();
    @NotNull
    private Mpa mpa;
    private Set<Director> directors = new HashSet<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}