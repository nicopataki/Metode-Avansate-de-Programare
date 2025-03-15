package com.example.guiex1.controller;

import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.ValidationException;
import com.example.guiex1.services.UtilizatorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModifyController {
    UtilizatorService service;
    private Stage stage;
    private Utilizator user;

    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;

    public void setUtilizatorService(UtilizatorService service, Stage dialogStage, Utilizator user) {
        this.service = service;
        this.stage = dialogStage;
        this.user = user;

        if(user != null){
            firstNameTextField.setText(user.getFirstName());
            lastNameTextField.setText(user.getLastName());
            usernameTextField.setText(user.getUsername());
            passwordTextField.setText(user.getPassword());

        }
    }

    @FXML
    public void handleUpdate(){
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String userName = usernameTextField.getText();
        String password = passwordTextField.getText();

        if(user != null){
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(userName);
            user.setPassword(password);

            service.updateUtilizator(user);
            stage.close();
        }
        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Update", "Succesfully updated!");
    }

    public void handleCancel(ActionEvent actionEvent){
        Stage stage=(Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
