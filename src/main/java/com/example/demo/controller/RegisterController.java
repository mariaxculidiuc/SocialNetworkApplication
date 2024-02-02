package com.example.demo.controller;

import com.example.demo.domain.Utilizator;
import com.example.demo.controller.alert.UserActionsAlert;
import com.example.demo.service.UtilizatorService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

public class RegisterController{
    private UtilizatorService service;
    @FXML
    public TextField id_password;
    @FXML
    public TextField lastNameText;
    @FXML
    public TextField firstNameText;
    @FXML
    public TextField passwordText;

    @FXML
    public Button exitButton;

    public void setService(UtilizatorService utilizatorService){
        service = utilizatorService;
    }

    public void handleExit(){
        Node src = exitButton;
        Stage stage = (Stage) src.getScene().getWindow();

        stage.close();
    }

    public void handleCreate(){
        String id = id_password.getText();
        String lastName = lastNameText.getText();
        String firstName = firstNameText.getText();
        String password = passwordText.getText();
        try{
            long password_int = Long.parseLong(id);
            Utilizator user = new Utilizator(firstName, lastName,password);
            user.setId(password_int);
            Optional<Utilizator> addedUser = service.add(user);
            if(addedUser.isPresent())
                UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Exista deja un utilizator cu ID-ul dat!");
            else handleExit();
        }
        catch (Exception e){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
        }

        lastNameText.clear();
        firstNameText.clear();
        passwordText.clear();
    }
}
