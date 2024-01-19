package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.yandex.practicum.model.validation.ValidReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200, message = "Description cannot be more than 200 characters")
    private String description;
    @ValidReleaseDate
    private LocalDate releaseDate;
    @NonNull
    @Positive
    private Integer duration;
    private Integer rate;
    private List<Genre> genres = new ArrayList<>();
    private Mpa mpa;
    private Set<Director> directors = new HashSet<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}