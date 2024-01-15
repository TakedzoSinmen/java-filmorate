package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.model.Director;
import ru.yandex.practicum.storage.api.DirectorStorage;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DirectorServiceTest {

    @Mock
    private DirectorStorage directorStorage;

    @InjectMocks
    private DirectorService directorService;

    private Director director;

    @BeforeEach
    void setUp() {
        director = Director.builder().id(1).name("John Doe").build();
    }

    @Test
    void testGetDirectorByIdWhenValidIdThenReturnDirector() {
        when(directorStorage.getById(director.getId())).thenReturn(director);

        Director result = directorService.getDirectorById(director.getId());

        assertThat(result).isEqualTo(director);
    }

    @Test
    void testGetDirectorByIdWhenInvalidIdThenThrowException() {
        int invalidId = 999;
        when(directorStorage.getById(invalidId)).thenThrow(new IllegalArgumentException("Director not found"));

        assertThatThrownBy(() -> directorService.getDirectorById(invalidId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Director not found");
    }

    @Test
    void testGetAllDirectors() {
        List<Director> directors = Arrays.asList(director, Director.builder().id(2).name("Jane Smith").build());
        when(directorStorage.getAll()).thenReturn(directors);

        List<Director> result = directorService.getAllDirectors();

        assertThat(result).hasSize(2).containsExactlyInAnyOrderElementsOf(directors);
    }

    @Test
    void testCreateDirector() {
        when(directorStorage.create(director)).thenReturn(director);

        Director result = directorService.createDirector(director);

        assertThat(result).isEqualTo(director);
    }

    @Test
    void testUpdateDirector() {
        when(directorStorage.updateById(director)).thenReturn(director);

        Director result = directorService.updateDirector(director);

        assertThat(result).isEqualTo(director);
    }

    @Test
    void testDeleteDirectorById() {
        doNothing().when(directorStorage).deleteById(director.getId());

        directorService.deleteDirectorById(director.getId());

        verify(directorStorage, times(1)).deleteById(director.getId());
    }
}