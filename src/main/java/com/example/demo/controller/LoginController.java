package com.example.demo.controller;

import com.example.demo.AddApplication;
import com.example.demo.RegisterApplication;
import com.example.demo.UsersApplication;
import com.example.demo.controller.alert.LoginActionAlert;
import com.example.demo.domain.Utilizator;
import com.example.demo.service.FriendRequestService;
import com.example.demo.service.MessageService;
import com.example.demo.service.PrietenieService;
import com.example.demo.service.UtilizatorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.xml.stream.events.StartElement;
import java.io.IOException;
import java.util.Optional;

public class LoginController {

    public Button createButton;
    private FriendRequestService friendRequestService;
    private UtilizatorService utilizatorService;
    private PrietenieService prietenieService;

    private MessageService messageService;

    @FXML
    private TextField id_password;

    @FXML
    private TextField passwordText;

   @FXML
   private Button cancel;

   private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public void setService(FriendRequestService friendRequestService, UtilizatorService utilizatorService, PrietenieService prietenieService,MessageService messageService){
        this.friendRequestService=friendRequestService;
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.messageService = messageService;
    }


    public void handle_cancel(){
        Node src = cancel;
        Stage stage = (Stage) src.getScene().getWindow();

        stage.close();
    }

    public void handle_login() {
        String id = id_password.getText();
        String password = passwordText.getText();
        long idNr = Long.parseLong(id);
        if(id.equals("admin"))
        {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(AddApplication.class.getResource("socialnetwork.fxml"));
            try {
                Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                stage.setScene(scene);

                EditController addController = fxmlLoader.getController();
                addController.setUserTaskService(utilizatorService);
                stage.show();
            }
            catch(IOException e){
                LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
            id_password.clear();
            passwordText.clear();
        }
        else {
            if (id.isEmpty()) {
                LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Parola  nu poate sa fie nula!");
                id_password.clear();
                passwordText.clear();
                return;
            }
            try {
                long password_int = Long.parseLong(id);
                Optional<Utilizator> utilizator = utilizatorService.getEntityById(password_int);
                if (passwordEncoder.matches(password, utilizator.get().getPassword())) {
                    Stage stage = new Stage();
                    FXMLLoader fxmlLoader = new FXMLLoader(AddApplication.class.getResource("add-view.fxml"));
                    try {
                        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                        stage.setScene(scene);

                        AddController addController = fxmlLoader.getController();
                        addController.setService(password_int, friendRequestService, utilizatorService, prietenieService,messageService);
                        stage.show();
                    } catch (IOException e) {
                        LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
                    }
                    id_password.clear();
                    passwordText.clear();
                    return;
                } else {
                    LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Nu am gasit nici un utilizator cu aceasta parola!");

                }
                id_password.clear();
                passwordText.clear();
            } catch (NumberFormatException err) {
                LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Parola trebuie sa contina doar cifre!");
            }
        }
    }

    public void handleCreate(){
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);

        FXMLLoader loader = new FXMLLoader(RegisterApplication.class.getResource("register-view.fxml"));
        try {
            Parent root = loader.load();

            RegisterController controller = loader.getController();
            controller.setService(utilizatorService);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setTitle("Create account");
            dialogStage.showAndWait();
        }
        catch (IOException e){
            LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }
}