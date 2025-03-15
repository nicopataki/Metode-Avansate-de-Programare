package com.example.guiex1.controller;

import com.example.guiex1.HelloApplication;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.ValidationException;
import com.example.guiex1.services.UtilizatorService;
import com.example.guiex1.utils.events.ChangeEventType;
import com.example.guiex1.utils.events.UtilizatorEntityChangeEvent;
import com.example.guiex1.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.awt.*;
import javafx.geometry.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UtilizatorController implements Observer<UtilizatorEntityChangeEvent> {
    UtilizatorService service;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator,String> tableColumnFirstName;
    @FXML
    TableColumn<Utilizator,String> tableColumnLastName;
    @FXML
    TableColumn<Utilizator, String> tableColumnUsername;

    @FXML
    private ComboBox<String> searchComboBox;
    @FXML
    private Button searchButton;

    public void setUtilizatorService(UtilizatorService service) {
        this.service = service;
        service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("username"));
        tableView.setItems(model);
    }

    private void initModel() {
        Iterable<Utilizator> messages = service.getAll();
        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
    }

    @Override
    public void update(UtilizatorEntityChangeEvent utilizatorEntityChangeEvent) {
        if(utilizatorEntityChangeEvent.getType() == ChangeEventType.ADD){
            Utilizator user = (Utilizator) utilizatorEntityChangeEvent.getData();
            model.add(user);
            initModel();
        }
        if(utilizatorEntityChangeEvent.getType() == ChangeEventType.DELETE){
            Utilizator user = (Utilizator) utilizatorEntityChangeEvent.getData();
            model.remove(user);
        }
        if(utilizatorEntityChangeEvent.getType() == ChangeEventType.UPDATE){
            Utilizator updatedUser = (Utilizator) utilizatorEntityChangeEvent.getData();
            model.setAll(StreamSupport.stream(service.getAll().spliterator(), false).collect(Collectors.toList()));
        }
    }

    public void handleDeleteUtilizator(ActionEvent actionEvent) {
        Utilizator user=(Utilizator) tableView.getSelectionModel().getSelectedItem();
        if (user!=null) {
            Utilizator deleted= service.deleteUtilizator(user.getId());
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Delete user", "User succesfully deleted!");
        }
        else MessageAlert.showErrorMessage(null, "No user selected");
    }

    public void handleAddUtilizator(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/guiex1/views/addUtilizator.fxml"));
            AnchorPane root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add New User");
            stage.setScene(new Scene(root));

            AddController addController = loader.getController();
            addController.setUtilizatorService(service);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            //initModel();
        } catch (IOException e) {
            e.printStackTrace();
            MessageAlert.showErrorMessage(null, "Error loading the Add User window: " + e.getMessage());
        }
    }

    public void handleUpdateUtilizator(ActionEvent actionEvent) {
        Utilizator selectedUser = (Utilizator) tableView.getSelectionModel().getSelectedItem();

        if(selectedUser != null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/guiex1/views/modifyUtilizator.fxml"));
                AnchorPane root = loader.load();

                Stage modifyStage = new Stage();
                modifyStage.setTitle("Modify User");
                modifyStage.setScene(new Scene(root));

                ModifyController modifyController = loader.getController();
                modifyController.setUtilizatorService(service, modifyStage, selectedUser);

                modifyStage.show();
            } catch (IOException e) {
                e.printStackTrace();
                MessageAlert.showErrorMessage(null, "Error opening Modify window " + e.getMessage());
            }
        }
        else{
            MessageAlert.showErrorMessage(null, "No user selected!");
        }

    }

    public void showMessageTaskEditDialog(Utilizator user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/edit-user-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Message");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EditUserController editUserController = loader.getController();
            editUserController.setService(service, dialogStage, user);

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSearch() {
        String searchText = searchComboBox.getEditor().getText();
        if(searchText.isEmpty()){
            ObservableList<String> allUsers = FXCollections.observableArrayList();
            for(Utilizator user: service.getAll()){
                allUsers.add(user.getFirstName() + " " + user.getLastName());
            }
            searchComboBox.setItems(allUsers);
        } else{
            ObservableList<String> suggestions = FXCollections.observableArrayList(service.searchByName(searchText));
            searchComboBox.setItems(suggestions);
        }
    }

    private void showFriendsWindow(Utilizator user) {

    }

    public void handleSearchButton(ActionEvent actionEvent) {
        String selectedUser = searchComboBox.getValue();
        if ( selectedUser != null && !selectedUser.isEmpty()){
            Utilizator user = service.findByName(selectedUser);
            if(user != null){
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/guiex1/views/friendshipView.fxml"));
                    AnchorPane root = loader.load();

                    Stage friendStage = new Stage();
                    friendStage.setTitle("User's friends");
                    friendStage.setScene(new Scene(root));

                    friendshipController Controller = loader.getController();
                    Controller.setUser(user, service, friendStage);

                    friendStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    MessageAlert.showErrorMessage(null, "Error opening Friendship window " + e.getMessage());
                }
            }
            else{
                MessageAlert.showErrorMessage(null, "No user found");
            }

        } else{
            MessageAlert.showErrorMessage(null, "Please select a user!");
        }
    }
}
