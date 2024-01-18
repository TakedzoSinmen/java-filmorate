package ru.yandex.practicum.storage.daoImpl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.storage.api.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class GenreStorageImpl implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Genre> genreRowMapper() {
        return new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("genre_name")
                );
            }
        };
    }

    @Override
    public Optional<Genre> getById(Integer id) {
        String query = "SELECT genre_id, genre_name FROM Genre WHERE genre_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(query, genreRowMapper(), id));
    }

    @Override
    public List<Genre> getAll() {
        String query = "SELECT genre_id, genre_name FROM Genre ORDER BY genre_id";
        return jdbcTemplate.query(query, genreRowMapper());
    }

    @Override
    public List<Genre> getGenresByFilmId(int filmId) {
        String query = "SELECT g.genre_id, g.genre_name " +
                "FROM Genre g " +
                "JOIN Genre_Film gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id = ?";
        return jdbcTemplate.query(query, genreRowMapper(), filmId);
    }
}