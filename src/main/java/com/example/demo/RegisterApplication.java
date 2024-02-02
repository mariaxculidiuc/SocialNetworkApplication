package com.example.demo;

import com.example.demo.controller.RegisterController;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.repository.RepoDB.UserRepoPagingDB;
import com.example.demo.service.UtilizatorService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        UserRepoPagingDB userRepoDB = new UserRepoPagingDB("jdbc:postgresql://localhost:5432/socialnetwork","postgres","selena23");
        UtilizatorService service = new UtilizatorService(userRepoDB);

        FXMLLoader fxmlLoader = new FXMLLoader(RegisterApplication.class.getResource("register-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 450, 300);
        RegisterController controller = fxmlLoader.getController();
        controller.setService(service);
        stage.setTitle("REGISTER");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}