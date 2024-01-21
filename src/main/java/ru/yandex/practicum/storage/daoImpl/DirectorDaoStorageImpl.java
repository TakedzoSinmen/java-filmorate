package ru.yandex.practicum.storage.daoImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Director;
import ru.yandex.practicum.storage.api.DirectorStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DirectorDaoStorageImpl implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director getById(Integer id) {
        isExist(id);
        String sql = "SELECT * FROM Director WHERE director_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        rowSet.next();

        return rowToDirector(rowSet);
    }

    @Override
    public List<Director> getAll() {
        String sql = "SELECT * FROM Director";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        List<Director> directorsList = new ArrayList<>();

        while (rowSet.next()) {
            directorsList.add(rowToDirector(rowSet));
        }
        return directorsList;
    }

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("Director")
                .usingGeneratedKeyColumns("director_id");
        director.setId(insert.executeAndReturnKey(directorToMap(director)).intValue());

        log.debug("Director with id: {} was added", director.getId());
        return director;
    }

    @Override
    public Director updateById(Director director) {
        isExist(director.getId());
        String sql = "UPDATE Director SET director_name = ? WHERE director_id =?";
        jdbcTemplate.update(sql, director.getName(), director.getId());

        log.debug("Director with id: {} was updated", director.getId());
        return director;
    }

    @Override
    public void deleteById(Integer id) {
        isExist(id);
        String sql = "DELETE FROM Director WHERE director_id =?";
        jdbcTemplate.update(sql, id);

        log.debug("Director with id: {} was deleted", id);
    }

    private static Director rowToDirector(SqlRowSet rowSet) {
        int directorId = rowSet.getInt("director_id");
        String directorName = rowSet.getString("director_name");

        return Director.builder()
                .id(directorId)
                .name(directorName)
                .build();
    }

    private void isExist(int id) {
        String sql = "SELECT * FROM Director WHERE director_id = ?";
        if (!jdbcTemplate.queryForRowSet(sql, id).next()) {
            log.debug("Director with id: {} was not found", id);
            throw new EntityNotFoundException(String.format("Director with id: %d was not found", id));
        }
    }

    public Map<String, Object> directorToMap(Director director) {
        Map<String, Object> result = new HashMap<>();
        result.put("director_id", director.getId());
        result.put("director_name", director.getName());
        return result;
    }
}
