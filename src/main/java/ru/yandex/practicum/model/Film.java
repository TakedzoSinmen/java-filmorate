package ru.yandex.practicum.model;

import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Component
public class Film {

    private final Set<Integer> likes = new HashSet<>();

    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void like (Integer id) {
        likes.add(id);
    }

    public void unlike (Integer id) {
        likes.remove(id);
    }
}
