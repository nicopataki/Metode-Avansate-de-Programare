package com.example.guiex1;

import com.example.guiex1.controller.UtilizatorController;
import com.example.guiex1.controller.logInController;
import com.example.guiex1.controller.signInController;
import com.example.guiex1.domain.*;
import com.example.guiex1.repository.Repository;
import com.example.guiex1.repository.dbrepo.FriendRequestDataBase;
import com.example.guiex1.repository.dbrepo.FriendshipDataBase;
import com.example.guiex1.repository.dbrepo.MessageDataBase;
import com.example.guiex1.repository.dbrepo.UtilizatorDbRepository;
import com.example.guiex1.services.UtilizatorService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {

    Repository<Long, Utilizator> utilizatorRepository;
    UtilizatorService service;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        System.out.println("Reading data from database");
        String username="postgres";
        String pasword="parola";
        String url="jdbc:postgresql://localhost:5433/lab7";
        Repository<Long, Utilizator> utilizatorRepository =
                new UtilizatorDbRepository(url,username, pasword,  new UtilizatorValidator());
        Repository<Long, Friendship> friendshipRepository =
                new FriendshipDataBase(url,username, pasword,  new FriendshipValidator(utilizatorRepository));
        Repository<Long, FriendshipRequest> requestRepository =
                new FriendRequestDataBase(url,username, pasword);
        Repository<Long, Message> messageRepository =
                new MessageDataBase(url, username, pasword, utilizatorRepository);
        service =new UtilizatorService(utilizatorRepository, friendshipRepository, requestRepository, messageRepository);

        initView(primaryStage);
        primaryStage.setWidth(800);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/logIn.fxml"));

        AnchorPane userLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        logInController userController = fxmlLoader.getController();
        userController.setService(service);
    }
}