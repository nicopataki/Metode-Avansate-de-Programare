package com.example.guiex1.controller;

import com.example.guiex1.domain.Message;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.UtilizatorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class messageController {
    @FXML
    private ListView<Message> messages;
    @FXML
    private ListView<Utilizator> friends;
    @FXML
    private TextField messageInput;

    private UtilizatorService service;
    private Stage stage;
    private Utilizator user;

    ObservableList<Message> messagesModel = FXCollections.observableArrayList();
    ObservableList<Utilizator> friendsModel = FXCollections.observableArrayList();

    public void SetService(Utilizator user, UtilizatorService service, Stage stage){
        this.user = user;
        this.service = service;
        this.stage = stage;
        loadFriends();
    }

    @FXML
    public void initialize(){
        friends.setItems(friendsModel);
        messages.setItems(messagesModel);

        Label noMessagesPlaceholder = new Label("No messages with this friend.");
        noMessagesPlaceholder.setStyle("-fx-font-size: 14px; -fx-text-fill: gray; -fx-alignment: center;");
        messages.setPlaceholder(noMessagesPlaceholder);

        messages.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Creăm un container pentru mesaj
                    VBox messageBox = new VBox();
                    messageBox.setSpacing(5); // Spațiu între text și oră

                    // Label pentru textul mesajului
                    Label messageText = new Label(message.getMessage().replace("\"", ""));
                    messageText.setStyle("-fx-padding: 10px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

                    // Adăugăm ora sub textul mesajului
                    messageBox.getChildren().addAll(messageText);

                    // Verificăm dacă utilizatorul curent a trimis mesajul
                    if (message.getFrom().getId().equals(user.getId())) {
                        // Mesaj trimis de utilizatorul curent - aliniere dreapta
                        messageText.setStyle(messageText.getStyle() + "-fx-background-color: lightblue; -fx-alignment: center-right;");
                        messageBox.setStyle("-fx-alignment: center-right;");
                    } else {
                        // Mesaj trimis de prieten - aliniere stânga
                        messageText.setStyle(messageText.getStyle() + "-fx-background-color: lightgray; -fx-alignment: center-left;");
                        messageBox.setStyle("-fx-alignment: center-left;");
                    }

                    // Adăugăm mesajul formatat ca grafic
                    setGraphic(messageBox);
                }
            }
        });


        friends.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Utilizator friend, boolean empty) {
                super.updateItem(friend, empty);
                if (empty || friend == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label nameLabel = new Label(friend.getFirstName() + " " + friend.getLastName());
                    Label usernameLabel = new Label("@" + friend.getUsername());

                    // Stilizează username-ul mai mic
                    usernameLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

                    VBox vbox = new VBox(nameLabel, usernameLabel);
                    setGraphic(vbox);
                }
            }
        });

        friends.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                loadMessages(newValue);
            }
        });
    }

    private void loadFriends(){
        List<Utilizator> friendships = service.getListFriends(user);
        friendsModel.setAll(friendships);

        if(friendships.isEmpty()){
            MessageAlert.showErrorMessage(null, "You don't have any friends!");
        }
    }

    private void loadMessages(Utilizator friend){
        messagesModel.clear();
        List<Message> userMessages = service.getAllMessages(user.getId(), friend.getId());
        messagesModel.setAll(userMessages);
    }

    public void handleClose(ActionEvent actionEvent) {
        stage.close();
    }

    public void handleSendMessage(ActionEvent actionEvent) {
        String text = messageInput.getText();
        Utilizator selected = friends.getSelectionModel().getSelectedItem();
        if(text != null && !text.trim().isEmpty() && selected != null){
            Message lastMessage = getLastMessage();

            Message newMessage = new Message(user, new ArrayList<Utilizator>() {{
                add(selected);
            }}, text, LocalDateTime.now());

            if(lastMessage != null){
                newMessage.setReply(lastMessage);
            }

            service.addMessage(newMessage);

            messagesModel.add(newMessage);
            loadMessages(selected);
            messageInput.clear();
        }
    }

    private Message getLastMessage(){
        Utilizator selected = friends.getSelectionModel().getSelectedItem();
        List<Message> all = service.getAllMessages(user.getId(), selected.getId());
        if(all.isEmpty()){
            return null;
        }
        else{
            return all.get(all.size()-1);
        }
    }
}
