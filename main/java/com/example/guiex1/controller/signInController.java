package com.example.guiex1.controller;

import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.UtilizatorValidator;
import com.example.guiex1.domain.ValidationException;
import com.example.guiex1.services.UtilizatorService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class signInController {

    @FXML
    private TextField first_name;
    @FXML
    private TextField last_name;
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private TextField password_confirm;

    @FXML
    private Text firstnameErrorText;
    @FXML
    private Text lastnameErrorText;
    @FXML
    private Text emailErrorText;
    @FXML
    private Text passwordErrorText;

    private UtilizatorValidator userValidator = new UtilizatorValidator();
    private UtilizatorService service;

    public void setService(UtilizatorService service) {
        this.service = service;
    }

    @FXML
    protected void onCreateAccountClick(ActionEvent event) throws IOException {
        Utilizator newUser = new Utilizator(first_name.getText(), last_name.getText(), email.getText(), password.getText());

        try{
            userValidator.validate(newUser);
        }
        catch (ValidationException exception) {
            String err = exception.toString().split(":")[1];
            switch (err.charAt(1)) {
                case '1' -> {
                    firstnameErrorText.setText(err.substring(1));
                    firstnameErrorText.setVisible(true);
                }
                case '2' -> {
                    lastnameErrorText.setText(err.substring(1));
                    lastnameErrorText.setVisible(true);
                }
                default -> {
                    emailErrorText.setText(err);
                    System.out.println(err);
                    emailErrorText.setVisible(true);
                }
            }
            return;
        }

        if(!password.getText().equals(password_confirm.getText()))
            passwordErrorText.setVisible(true);
        else { // adaugam utilizator
            service.addUtilizator(newUser);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/guiex1/views/friendshipView.fxml"));
            AnchorPane root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("User's friends");
            stage.setScene(new Scene(root));

            friendshipController Controller = loader.getController();
            Controller.setUser(newUser, service, stage);

            stage.setOnCloseRequest(action -> {
                try {

                    FXMLLoader logInLoader = new FXMLLoader(getClass().getResource("/com/example/guiex1/views/logIn.fxml"));
                    AnchorPane logInLayout = logInLoader.load();

                    Stage logInStage = new Stage();
                    logInStage.setTitle("Log In");
                    logInStage.setScene(new Scene(logInLayout));

                    logInController logInController = logInLoader.getController();
                    logInController.setService(service);

                    logInStage.show();
                    ((Node) event.getSource()).getScene().getWindow().hide();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        }
    }

    public void goBackToLogIn(ActionEvent actionEvent) throws IOException {
        FXMLLoader stageLoader = new FXMLLoader();
        stageLoader.setLocation(getClass().getResource("/com/example/guiex1/views/logIn.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

        AnchorPane logInLayout = stageLoader.load();
        Scene scene = new Scene(logInLayout);
        stage.setScene(scene);

        logInController logInController = stageLoader.getController();
        logInController.setService(this.service);

        stage.show();
    }

    public void onFirstnameTextChanged() {
        firstnameErrorText.setVisible(false);
    }

    public void onLastnameTextChanged() {
        lastnameErrorText.setVisible(false);
    }

    public void onEmailTextChanged() {
        emailErrorText.setVisible(false);
    }

    public void onPasswordTextChanged() {
        passwordErrorText.setVisible(false);
    }

    public void onConfirmPasswordTextChanged() {
        passwordErrorText.setVisible(false);
    }
}