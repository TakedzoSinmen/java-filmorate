package ru.yandex.practicum.storage.daoImpl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.storage.api.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class MpaStorageImpl implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Mpa> mpaRowMapper() {
        return new RowMapper<Mpa>() {
            @Override
            public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Mpa(
                        rs.getInt("mpa_id"),
                        rs.getString("mpa_name")
                );
            }
        };
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        String query = "SELECT * FROM Mpa WHERE mpa_id=?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(query, mpaRowMapper(), id));
    }

    @Override
    public List<Mpa> getAllMpa() {
        String query = "SELECT * FROM Mpa";
        return jdbcTemplate.query(query, mpaRowMapper());
    }
}