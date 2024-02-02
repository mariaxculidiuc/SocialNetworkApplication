package com.example.demo;

import com.example.demo.controller.EditController;
import com.example.demo.controller.LoginController;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.repository.RepoDB.UserRepoPagingDB;
import com.example.demo.service.UtilizatorService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class UsersApplication extends Application {



    @Override
    public void start(Stage stage) throws IOException {
        UserRepoPagingDB userRepoDB = new UserRepoPagingDB("jdbc:postgresql://localhost:5432/socialnetwork","postgres","selena23");
        UtilizatorService utilizatorService = new UtilizatorService(userRepoDB);


        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("socialnetwork.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("SocialNetwork");
        stage.setScene(scene);
        EditController controller = fxmlLoader.getController();
        controller.setUserTaskService(utilizatorService);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}