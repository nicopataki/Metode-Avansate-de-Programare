package com.example.guiex1.controller;

import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.UtilizatorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SendRequest {

    @FXML
    private TextField searchTextField;
    @FXML
    private ComboBox<String> userComboBox;

    private UtilizatorService service;
    private Stage dialogStage;
    private Utilizator user;

    public void setService(Utilizator user, UtilizatorService service, Stage dialogStage) {
        this.user = user;
        this.service = service;
        this.dialogStage = dialogStage;
    }

    @FXML
    private void initialize() {
        userComboBox.setPromptText("Select a user");
    }

    @FXML
    private void handleSearchBox() {
        String searchText = userComboBox.getEditor().getText();
        if(searchText.isEmpty()){
            ObservableList<String> allUsers = FXCollections.observableArrayList();
            for(Utilizator user: service.getAll()){
                allUsers.add(user.getFirstName() + " " + user.getLastName());
            }
            userComboBox.setItems(allUsers);
        } else{
            ObservableList<String> suggestions = FXCollections.observableArrayList(service.searchByName(searchText));
            userComboBox.setItems(suggestions);
        }
    }

    @FXML
    private void handleSendFriendRequest() {
        String selectedUser = userComboBox.getValue();
        if (selectedUser != null && !selectedUser.isEmpty()) {
            Utilizator userS = service.findByName(selectedUser);
            if (userS != null) {
                if(userS.getId() == user.getId()){
                    MessageAlert.showErrorMessage(null, "You can not send a friend request to yourself!");
                }
                else{
                    boolean alreadyFriends = service.getListFriends(user).stream()
                            .anyMatch(friend -> friend.getId().equals(userS.getId()));

                    if(alreadyFriends){
                        MessageAlert.showErrorMessage(null, "You are already friends with this user!");
                    }
                    else{
                        boolean requestExists = service.getLRequests(user).stream()
                                .anyMatch(request -> (request.getUser1().equals(user.getId()) && request.getUser2().equals(userS.getId())) ||
                                        (request.getUser1().equals(userS.getId()) && request.getUser2().equals(user.getId())));
                        if(requestExists){
                            MessageAlert.showErrorMessage(null, "Friend request already exists");
                        }else{
                            service.sendFriendRequest(this.user.getId(), userS.getId());
                            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Request sent", "The request was succesfully sent!");
                            dialogStage.close();
                        }
                    }
                }
            } else {
                MessageAlert.showErrorMessage(null, "User not found");
            }

        } else {
            MessageAlert.showErrorMessage(null, "Please select a user!");
        }
    }
}

