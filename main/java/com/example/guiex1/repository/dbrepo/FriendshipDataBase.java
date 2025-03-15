package com.example.guiex1.repository.dbrepo;

import com.example.guiex1.domain.Friendship;
import com.example.guiex1.domain.FriendshipStatus;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.Validator;
import com.example.guiex1.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class FriendshipDataBase implements Repository<Long, Friendship> {
    private final Validator<Friendship> validator;
    private final String url;
    private final String username;
    private final String password;

    public FriendshipDataBase(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Friendship> findOne(Long aLong) {
        String query = "SELECT * FROM \"Friendship\" WHERE \"id\" = ?";
        Friendship friendship = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long idFriend1 = resultSet.getLong("user1_id");
                Long idFriend2 = resultSet.getLong("user2_id");
                LocalDateTime date = resultSet.getTimestamp("since").toLocalDateTime();

                friendship = new Friendship(idFriend1, idFriend2, date);
                friendship.setId(aLong);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(friendship);
    }

    @Override
    public Iterable<Friendship> findAll() {
        Map<Long, Friendship> friendships = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"Friendship\"");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idFriend1 = resultSet.getLong("user1_id");
                Long idFriend2 = resultSet.getLong("user2_id");
                LocalDateTime date = resultSet.getTimestamp("since").toLocalDateTime();
                Friendship friendship = new Friendship(idFriend1, idFriend2, date);
                friendship.setId(id);
                friendships.put(friendship.getId(), friendship);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendships.values();
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Friendship can't be null!");
        }
        String query = "INSERT INTO \"Friendship\"(\"user1_id\", \"user2_id\", \"since\") VALUES (?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            //statement.setLong(1, entity.getId());
            statement.setLong(1, entity.getIdUser1());
            statement.setLong(2, entity.getIdUser2());
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(entity.getDate()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<Friendship> delete(Long aLong) {
        String query = "DELETE FROM \"Friendship\" WHERE \"id\" = ?";

        try (Connection connection = DriverManager.getConnection(url, username,password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Friendship friendshipToDelete = null;
        for (Friendship friendship : findAll()) {
            if (Objects.equals(friendship.getId(), aLong)) {
                friendshipToDelete = friendship;
            }
        }
        return Optional.ofNullable(friendshipToDelete);
    }

    @Override
    public Optional<Friendship> update(Friendship friendship) {
        if(this.findOne(friendship.getId()).isEmpty()) {
            return Optional.empty();
        }

        String sql = "UPDATE \"Friendships\" SET " +
                "user1_id = ?, user2_id = ?, \"since\" = ? WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            this.setDataInStatement(statement, friendship);
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(friendship.getDate()));
            statement.setLong(5, friendship.getId());

            return statement.executeUpdate() > 0 ? Optional.of(friendship) : Optional.empty();
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea prieteniei in baza de date!");
        }
    }

    private void setDataInStatement(PreparedStatement statement, Friendship friendship) throws SQLException {
        statement.setLong(1, friendship.getIdUser1());
        statement.setLong(2, friendship.getIdUser2());
    }
}
