package org.example.dao.impl;

import lombok.RequiredArgsConstructor;
import org.example.dao.UserDao;
import org.example.exceptions.ObjectNotFoundException;
import org.example.model.User;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Component
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public User createUser(User user) {

       SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        Map<String, Object> newUser = new HashMap<>(
                Map.of("last_name", user.getLastName(),
                        "first_name", user.getFirstName(),
                        "patronymic", user.getPatronymic(),
                        "date_of_birth", user.getDateOfBirth(),
                        "email", user.getEmail()
                ));

        Long id = simpleJdbcInsert.executeAndReturnKey(newUser).longValue();

        user.setId(id);

        return getById(id);
    }

    public User getById(Long id) {

        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, id);

        if (users.size() == 0) {
            throw new ObjectNotFoundException("No user with id = " + id);
        } else {
            return users.get(0);
        }
    }

    public List<User> findAllUsers() {

        String sql = "SELECT * FROM users";

        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public List<User> getUsersForParams(Integer from, Integer size, String nameFilter) {

        String sql = "SELECT * FROM users WHERE first_name ILIKE ? LIMIT ? OFFSET ?;";

        return jdbcTemplate.query(sql, this::mapRowToUser, nameFilter, size, from);
    }

    public User updateUser(Long id, User user) {

        getById(id);

        String sql = "UPDATE users SET " +
                "first_name = ?, last_name = ?, patronymic = ?, date_of_birth = ?, email = ? " +
                "WHERE user_id = ?";

        jdbcTemplate.update(sql,
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getDateOfBirth(),
                user.getEmail(),
                id
        );

        return getById(id);
    }

    @Override
    public void deleteUser(Long id) {

        String sqlQuery = "DELETE FROM users WHERE user_id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {

        return User.builder()
                .id(rs.getLong("user_id"))
                .lastName(rs.getString("last_name"))
                .firstName(rs.getString("first_name"))
                .patronymic(rs.getString("patronymic"))
                .dateOfBirth(rs.getDate("date_of_birth").toLocalDate())
                .email(rs.getString("email"))
                .build();
    }
}
