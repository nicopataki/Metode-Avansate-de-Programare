package com.example.guiex1.repository.dbrepo;

import com.example.guiex1.domain.Friendship;
import com.example.guiex1.domain.FriendshipRequest;
import com.example.guiex1.domain.FriendshipStatus;
import com.example.guiex1.domain.Validator;
import com.example.guiex1.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class FriendRequestDataBase implements Repository<Long, FriendshipRequest> {
    private final String url;
    private final String username;
    private final String password;

    public FriendRequestDataBase(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<FriendshipRequest> findOne(Long aLong) {
        String query = "SELECT * FROM \"FriendshipRequest\" WHERE \"id\" = ?";
        FriendshipRequest friendshiprequest = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long idFriend1 = resultSet.getLong("iduser1");
                Long idFriend2 = resultSet.getLong("iduser2");
                LocalDateTime date = resultSet.getTimestamp("since").toLocalDateTime();
                FriendshipStatus state = FriendshipStatus.valueOf(resultSet.getString("status"));

                friendshiprequest = new FriendshipRequest(idFriend1, idFriend2, date, state);
                friendshiprequest.setId(aLong);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(friendshiprequest);
    }

    @Override
    public Iterable<FriendshipRequest> findAll() {
        Map<Long, FriendshipRequest> friendshiprequests = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"FriendshipRequest\"");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idFriend1 = resultSet.getLong("iduser1");
                Long idFriend2 = resultSet.getLong("iduser2");
                LocalDateTime date = resultSet.getTimestamp("since").toLocalDateTime();
                FriendshipStatus state = FriendshipStatus.valueOf(resultSet.getString("status"));
                FriendshipRequest friendship = new FriendshipRequest(idFriend1, idFriend2, date, state);
                friendship.setId(id);
                friendshiprequests.put(friendship.getId(), friendship);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendshiprequests.values();
    }

    @Override
    public Optional<FriendshipRequest> save(FriendshipRequest entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Friendship can't be null!");
        }
        String query = "INSERT INTO \"FriendshipRequest\"(\"iduser1\", \"iduser2\", \"since\", \"status\") VALUES (?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, entity.getUser1());
            statement.setLong(2, entity.getUser2());
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(entity.getDate()));
            statement.setString(4, entity.getStatus().name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<FriendshipRequest> delete(Long aLong) {
        String query = "DELETE FROM \"FriendshipRequest\" WHERE \"id\" = ?";

        try (Connection connection = DriverManager.getConnection(url, username,password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        FriendshipRequest friendshipToDelete = null;
        for (FriendshipRequest friendship : findAll()) {
            if (Objects.equals(friendship.getId(), aLong)) {
                friendshipToDelete = friendship;
            }
        }
        return Optional.ofNullable(friendshipToDelete);
    }

    @Override
    public Optional<FriendshipRequest> update(FriendshipRequest friendship) {
        if(this.findOne(friendship.getId()).isEmpty()) {
            return Optional.empty();
        }

        String sql = "UPDATE \"FriendshipRequest\" SET " +
                "iduser1 = ?, iduser2 = ?, since = ?, status = ? WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            this.setDataInStatement(statement, friendship);
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(friendship.getDate()));
            statement.setString(4, friendship.getStatus().name());
            statement.setLong(5, friendship.getId());

            return statement.executeUpdate() > 0 ? Optional.of(friendship) : Optional.empty();
        }
        catch (SQLException e) {
            throw new RuntimeException("Eroare la actualizarea prieteniei in baza de date!");
        }
    }

    private void setDataInStatement(PreparedStatement statement, FriendshipRequest friendship) throws SQLException {
        statement.setLong(1, friendship.getUser1());
        statement.setLong(2, friendship.getUser2());
    }
}
