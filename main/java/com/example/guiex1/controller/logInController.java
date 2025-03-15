package com.example.guiex1.controller;

import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.UtilizatorService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class logInController {
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private Text emailErrorText;
    @FXML
    private Text passwordErrorText;
    private UtilizatorService service;

    public void setService(UtilizatorService service){
        this.service = service;
    }

    @FXML
    protected void onLogInButtonClick(ActionEvent action) throws IOException{
        Utilizator user = service.getUserByEmail(email.getText());
        if(user == null){
            emailErrorText.setVisible(true);
            passwordErrorText.setVisible(false);
        }
        else if(!password.getText().equals(user.getPassword())){
            passwordErrorText.setVisible(true);
            emailErrorText.setVisible(false);
        }
        else{
            emailErrorText.setVisible(false);
            passwordErrorText.setVisible(false);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/guiex1/views/friendshipView.fxml"));
            AnchorPane root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("User's friends");
            stage.setScene(new Scene(root));

            friendshipController Controller = loader.getController();
            Controller.setUser(user, service, stage);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            email.setText("");
            password.setText("");
        }

    }

    @FXML
    public void onSignInClick(ActionEvent event) throws IOException{
        FXMLLoader stageLoader = new FXMLLoader();
        stageLoader.setLocation(getClass().getResource("/com/example/guiex1/views/signIn.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        AnchorPane singUpLayout = stageLoader.load();
        Scene scene = new Scene(singUpLayout);
        stage.setScene(scene);

        signInController signUpController = stageLoader.getController();
        signUpController.setService(this.service);

        stage.show();

    }

    public void onTextChanged(KeyEvent evt){
        emailErrorText.setVisible(false);
        passwordErrorText.setVisible(false);
    }

    public void onPasswordChanged(KeyEvent evt){
        passwordErrorText.setVisible(false);
    }

}
