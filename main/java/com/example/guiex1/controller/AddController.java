package com.example.guiex1.controller;

import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.ValidationException;
import com.example.guiex1.services.UtilizatorService;
import com.example.guiex1.utils.events.UtilizatorEntityChangeEvent;
import com.example.guiex1.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddController {
    UtilizatorService service;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    private Stage stage;

    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;

    public void setUtilizatorService(UtilizatorService service) {
        this.service = service;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    @FXML
    public void handleAdd(ActionEvent actionEvent){
        try{
            Utilizator user = new Utilizator(firstNameTextField.getText(), lastNameTextField.getText(), usernameTextField.getText(), passwordTextField.getText());
            service.addUtilizator(user);
            //MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "User added", "User succesfully added!");
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();
        } catch(ValidationException e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    public void handleCancel(ActionEvent actionEvent){
        Stage stage=(Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
