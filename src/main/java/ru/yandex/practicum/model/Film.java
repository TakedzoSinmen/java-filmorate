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
@AllArgsConstructor
public class Film {

    private final Set<Integer> likes = new HashSet<>();

    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    public void like(Integer id) {
        likes.add(id);
    }

    public void unlike(Integer id) {
        likes.remove(id);
    }
}
