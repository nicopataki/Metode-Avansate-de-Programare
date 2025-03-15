package com.example.guiex1.controller;

import com.example.guiex1.domain.*;
import com.example.guiex1.services.UtilizatorService;
import com.example.guiex1.utils.events.ChangeEventType;
import com.example.guiex1.utils.events.FriendshipEntityChangeEvent;
import com.example.guiex1.utils.observer.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class friendRequestsController {
    @FXML
    private TableView<FriendRequestTableEntry> friendrequestsTableview;
    @FXML
    private TableColumn<Utilizator, String> firstNameColumn;
    @FXML
    private TableColumn<Utilizator, String> lastNameColumn;
    @FXML
    private TableColumn<LocalDateTime, String> sinceColumn;
    @FXML
    private TableColumn<FriendshipStatus, String> statusColumn;

    private ObservableList<Utilizator> model = FXCollections.observableArrayList();

    private Utilizator user;
    private UtilizatorService service;
    private Stage stage;
    private friendshipController friends;

    public void setUser(Utilizator user, UtilizatorService service, Stage stage, friendshipController friends) {
        this.user = user;
        this.service = service;
        this.stage = stage;
        this.friends = friends;
        //service.addObserver(this);
        loadFriends();
    }

    private void loadFriends(){
        List<Utilizator> requests = service.getListRequests(user);
        List<FriendshipRequest> list = service.getLRequests(user);

        if (requests.size() != list.size()) {
            throw new IllegalStateException("Lists sizes do not match!");
        }

        ObservableList<FriendRequestTableEntry> tableData = FXCollections.observableArrayList();

        for (int i = 0; i < requests.size(); i++) {
            Utilizator utilizator = requests.get(i);
            FriendshipRequest correspondingRequest = list.get(i);

            tableData.add(new FriendRequestTableEntry(
                    utilizator.getFirstName(),
                    utilizator.getLastName(),
                    correspondingRequest.getDate(),
                    correspondingRequest.getStatus()
            ));
        }

        friendrequestsTableview.setItems(tableData);

        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        sinceColumn.setCellValueFactory(new PropertyValueFactory<>("since"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    public void handleAccept(ActionEvent actionEvent) {
        FriendRequestTableEntry selectedRequest = friendrequestsTableview.getSelectionModel().getSelectedItem();

        if (selectedRequest == null) {
            MessageAlert.showErrorMessage(null, "Please select a friend request to delete.");
            return;
        }

        if (selectedRequest.getStatus() == FriendshipStatus.REJECTED) {
            MessageAlert.showErrorMessage(null, "The request is already deleted!");
            return;
        }

        if (selectedRequest.getStatus() == FriendshipStatus.ACCEPTED) {
            MessageAlert.showErrorMessage(null, "The request is already accepted!");
            return;
        }

        String fullName = selectedRequest.getFirstName() + " " + selectedRequest.getLastName();
        Utilizator utilizator = service.findByName(fullName);

        if (utilizator == null) {
            MessageAlert.showErrorMessage(null, "The user for the selected request could not be found.");
            return;
        }

        FriendshipRequest request = service.getRequest(utilizator.getId(), user.getId());

        if (request == null) {
            MessageAlert.showErrorMessage(null, "The friend request could not be found.");
            return;
        }

        Friendship friendship = new Friendship(user.getId(), utilizator.getId(), LocalDateTime.now());
        service.addFriend(friendship);

        service.updateStatus(request, FriendshipStatus.ACCEPTED);
        friends.loadFriends();
        loadFriends();
        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Request Accepted", "The friend request has been successfully accepted.");
    }

    public void handleDelete(ActionEvent actionEvent) {
        FriendRequestTableEntry selectedRequest = friendrequestsTableview.getSelectionModel().getSelectedItem();

        if (selectedRequest == null) {
            MessageAlert.showErrorMessage(null, "Please select a friend request to delete.");
            return;
        }

        if (selectedRequest.getStatus() == FriendshipStatus.REJECTED) {
            MessageAlert.showErrorMessage(null, "The request is already deleted!");
            return;
        }

        if (selectedRequest.getStatus() == FriendshipStatus.ACCEPTED) {
            MessageAlert.showErrorMessage(null, "The request is already accepted!");
            return;
        }

        String fullName = selectedRequest.getFirstName() + " " + selectedRequest.getLastName();
        Utilizator utilizator = service.findByName(fullName);

        if (utilizator == null) {
            MessageAlert.showErrorMessage(null, "The user for the selected request could not be found.");
            return;
        }

        FriendshipRequest request = service.getRequest(utilizator.getId(), user.getId());

        if (request == null) {
            MessageAlert.showErrorMessage(null, "The friend request could not be found.");
            return;
        }

        service.updateStatus(request, FriendshipStatus.REJECTED);
        loadFriends();
        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Request Deleted", "The friend request has been successfully deleted.");
    }


}
