package com.example.guiex1.controller;

import com.example.guiex1.domain.*;
import com.example.guiex1.services.UtilizatorService;
import com.example.guiex1.utils.events.ChangeEventType;
import com.example.guiex1.utils.events.FriendshipEntityChangeEvent;
import com.example.guiex1.utils.events.UtilizatorEntityChangeEvent;
import com.example.guiex1.utils.observer.Observable;
import com.example.guiex1.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jdk.jshell.execution.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class friendshipController implements Observer<UtilizatorEntityChangeEvent> {
    @FXML
    private TableView<Utilizator> friendsTableview;
    @FXML
    private TableColumn<Utilizator, String> firstNameColumn;
    @FXML
    private TableColumn<Utilizator, String> lastNameColumn;
    @FXML
    private TableColumn<Utilizator, String> dateColumn;
    @FXML
    private Label notificationLabel;

    private ObservableList<Utilizator> model = FXCollections.observableArrayList();
    private ObservableList<FriendshipRequest> modelFriendRequests = FXCollections.observableArrayList();


    private Utilizator user;
    private UtilizatorService service;
    private Stage stage;

    private final Set<Long> notifiedRequests = new HashSet<>();

    public void setUser(Utilizator user, UtilizatorService service, Stage stage) {
        this.user = user;
        this.service = service;
        this.stage = stage;
        service.addObserver(this);
        loadFriends();
        //initModelRequests();
        updateFriendRequests();
    }

    private void initModelRequests() {
        Iterable<FriendshipRequest> requests = service.getRequests();
        List<FriendshipRequest> list = StreamSupport.stream(requests.spliterator(), false).collect(Collectors.toList());
        modelFriendRequests.setAll(list);
    }

    @FXML
    public void initialize() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        friendsTableview.setItems(model);
    }

    public void loadFriends(){
        List<Utilizator> friends = service.getListFriends(user);
        model.setAll(friends);
    }

    @Override
    public void update(UtilizatorEntityChangeEvent utilizatorEntityChangeEvent) {
        if(utilizatorEntityChangeEvent.getType() == ChangeEventType.ADD){
            Utilizator utilizator = (Utilizator) utilizatorEntityChangeEvent.getData();
            model.add(utilizator);
            loadFriends();
        }
        if(utilizatorEntityChangeEvent.getType() == ChangeEventType.DELETE){
            Utilizator user = (Utilizator) utilizatorEntityChangeEvent.getData();
            model.remove(user);
        }

        if(utilizatorEntityChangeEvent.getType() == ChangeEventType.ADAUGA){
            updateFriendRequests();
        }
    }

    private void initModel() {
        Iterable<Utilizator> messages = service.getAll();

        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
    }


    public void handleFriendRequests(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/guiex1/views/friendRequests.fxml"));
            AnchorPane root = loader.load();

            Stage requestStage = new Stage();
            requestStage.setTitle("User's friendships");
            requestStage.setScene(new Scene(root));

            friendRequestsController Controller = loader.getController();
            Controller.setUser(user, service, requestStage, this);

            notificationLabel.setText("You have no notification");
            notificationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black; -fx-font-weight: bold;");

            requestStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            MessageAlert.showErrorMessage(null, "Error opening Friendships window" + e.getMessage());
        }
    }


    public void handleDeleteFriend(ActionEvent actionEvent) {
        Utilizator friend=(Utilizator) friendsTableview.getSelectionModel().getSelectedItem();
        if (friend!=null) {
            Utilizator deleted= service.removeFriendship(user.getId(), friend.getId());
            loadFriends();
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Delete friendship", "Friendship succesfully deleted!");

        }
        else MessageAlert.showErrorMessage(null, "No user selected!");
    }

    public void handleSendRequest(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/guiex1/views/sendFriendRequest.fxml"));
            Stage dialogStage = new Stage();
            Scene scene = new Scene(loader.load());

            SendRequest controller = loader.getController();
            controller.setService(user, service, dialogStage);
            dialogStage.setScene(scene);
            dialogStage.setTitle("Send Friend Request");
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSettings(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/guiex1/views/settings.fxml"));
            Stage dialogStage = new Stage();
            Scene scene = new Scene(loader.load());

            settingsController controller = loader.getController();
            controller.SetService(service, user, dialogStage, this.stage);
            dialogStage.setScene(scene);
            dialogStage.setTitle("Modify Dates");
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMessages(ActionEvent actionEvent) {
        if(service == null){
            MessageAlert.showErrorMessage(null, "Service e null");
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/guiex1/views/messageView.fxml"));
            Stage dialogStage = new Stage();
            Scene scene = new Scene(loader.load());

            messageController controller = loader.getController();
            controller.SetService(user, service, dialogStage);
            if(service == null){
                MessageAlert.showErrorMessage(null, "Service e null");
            }
            dialogStage.setScene(scene);
            dialogStage.setTitle("Modify Dates");
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateFriendRequests(){
        initModelRequests();
        if(modelFriendRequests.size()>0){
            FriendshipRequest lastRequest = modelFriendRequests.get(modelFriendRequests.size()-1);
            if(!notifiedRequests.contains(lastRequest.getId()) && service.findUser(lastRequest.getUser2()).getUsername().equals(user.getUsername())
            && lastRequest.getStatus() == FriendshipStatus.PENDING){
                notifiedRequests.add(lastRequest.getId());
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "New Notitfication", "You have a new friendrequest");

                notificationLabel.setText("You have a new friend request");
                notificationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-font-weight: bold;");
            }
        }

    }
}
