package com.example.guiex1.controller;

import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.UtilizatorService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class settingsController {
    private Utilizator user;
    private UtilizatorService service;
    private Stage stage;
    private Stage friendsStage;

    public void SetService(UtilizatorService service, Utilizator user, Stage stage, Stage friendsStage){
        this.service = service;
        this.user = user;
        this.stage = stage;
        this.friendsStage = friendsStage;
    }

    public void handleLogOut(ActionEvent actionEvent) {
        stage.close();
        friendsStage.close();
    }

    public void handleDeleteAccount(ActionEvent actionEvent) {
        service.deleteUtilizator(user.getId());
        stage.close();
        friendsStage.close();
    }

    public void handleupdateUtilizator(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/guiex1/views/modifyUtilizator.fxml"));
            AnchorPane root = loader.load();

            Stage modifyStage = new Stage();
            modifyStage.setTitle("Modify User");
            modifyStage.setScene(new Scene(root));

            ModifyController modifyController = loader.getController();
            modifyController.setUtilizatorService(service, modifyStage, user);

            modifyStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            MessageAlert.showErrorMessage(null, "Error opening Modify window " + e.getMessage());
        }

    }
}
