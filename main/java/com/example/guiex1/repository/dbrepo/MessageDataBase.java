package com.example.guiex1.repository.dbrepo;

import com.example.guiex1.domain.Message;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDataBase implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;
    private Repository<Long, Utilizator> userRepo;

    public MessageDataBase(String url, String username, String password, Repository<Long, Utilizator> userRepo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepo = userRepo;
    }

    public Optional<Message> findOneNoReply(Long aLong){
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"Messages\" WHERE id = ?")) {
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Long from_id = resultSet.getLong("from_id");
                Long to_id = resultSet.getLong("to_id");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Message msg = new Message(userRepo.findOne(from_id).get(), Collections.singletonList(userRepo.findOne(to_id).get()), message, date);
                msg.setId(aLong);
                return Optional.of(msg);
            }

        } catch(SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Message> findOne(Long aLong) {

        Message msg;
        if (findOneNoReply(aLong).isPresent()) {
            msg = findOneNoReply(aLong).get();
        } else return Optional.empty();

        String query = "SELECT * FROM \"Messages\" WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            Long reply_id = resultSet.getLong("result_id");
            if (!resultSet.next()) {
                Message replyMessage;
                if (findOneNoReply(reply_id).isPresent()) {
                    replyMessage = findOneNoReply(reply_id).get();
                } else return Optional.empty();

                msg.setReply(replyMessage);
                return Optional.of(msg);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }


    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM \"Messages\"";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long from_id = resultSet.getLong("from_id");
                Long to_id = resultSet.getLong("to_id");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                String message = resultSet.getString("message");
                Long reply_id = resultSet.getLong("reply_id");
                Utilizator from = userRepo.findOne(from_id).get();
                List<Utilizator> to = Collections.singletonList(userRepo.findOne(to_id).get());
                Message msg = new Message(from, to, message, date);
                msg.setId(id);
                messages.add(msg);
            }

        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    @Override
    public Optional<Message> save(Message entity) {

        String query = "INSERT INTO \"Messages\"(from_id, to_id, date, message, reply_id) VALUES (?,?,?,?,?)";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, entity.getFrom().getId());
            statement.setLong(2, entity.getTo().get(0).getId());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getData()));
            statement.setString(4, entity.getMessage());
            if(entity.getReply() == null) {
                statement.setNull(5, Types.NULL);
            } else statement.setLong(5, entity.getReply().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        String query = "DELETE FROM messages WHERE id = ?";

        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "albert", "admin");
            PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        String query = "UPDATE messages SET from_id = ?, to_id = ?, date = ?, message = ?, reply_id = ? WHERE id = ?";
        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "albert", "admin");
            PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, entity.getFrom().getId());
            statement.setLong(2, entity.getTo().get(0).getId());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getData()));
            statement.setString(4, entity.getMessage());
            statement.setLong(5, entity.getReply().getId());
            statement.setLong(6, entity.getId());
            statement.executeUpdate();
            return Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
