package com.example.demo.controller;

import com.example.demo.controller.alert.UserActionsAlert;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.RepoDB.UserRepoPagingDB;
import com.example.demo.repository.paging.Page;
import com.example.demo.repository.paging.PageableImplementation;
import com.example.demo.service.UtilizatorService;
import com.example.demo.utils.events.ChangeEventType;
import com.example.demo.utils.events.UserTaskChangeEvent;
import com.example.demo.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.demo.utils.observer.Observer;
import com.example.demo.utils.observer.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class EditController implements Observer<UserTaskChangeEvent>  {

    String url = "jdbc:postgresql://localhost:5432/socialnetwork";
    String user = "postgres";
    String passwd = "selena23";

    UserRepoPagingDB userRepository = new UserRepoPagingDB(url,user,passwd);
    private UtilizatorService service=new UtilizatorService(userRepository);
    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    Page<Utilizator> page = null;

    PageableImplementation initialPageable = new PageableImplementation(1,4);

    @FXML
    public TableView<Utilizator> tableView;
    @FXML
    public TableColumn<Utilizator, Long> tableColumnID;
    @FXML
    public TableColumn<Utilizator, String> tableColumnFN;
    @FXML
    public TableColumn<Utilizator, String> tableColumnLN;

    @FXML
    public TextField TextID;

    @FXML
    public TextField TextFN;

    @FXML
    public TextField TextLN;

    @FXML
    public TextField TextPass;

    @FXML
    public Button ExitButton;

    @FXML
    private TextField pageSizeTextField;

    public void handleSetPageSize() {
        try {
            int pageSize = Integer.parseInt(pageSizeTextField.getText());
            if (pageSize > 0) {
                initialPageable = new PageableImplementation(1, pageSize);
                page = service.findAllPage(initialPageable); // Adaugă această linie pentru a reface interogarea cu noul pageSize
                initModel();
            } else {
                UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Dimensiunea paginii trebuie să fie un număr pozitiv!");
            }
        } catch (NumberFormatException e) {
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Introduceți o valoare numerică validă pentru dimensiunea paginii!");
        }
    }

    public void setUserTaskService(UtilizatorService utilizatorService){

        this.service = utilizatorService;
        service.addObserver(this);
        initializeTableDataUser();

    }

    public void initializeTableDataUser(){
        Iterable<Utilizator> allUsers = service.getAll();
        List<Utilizator> allUsersList = StreamSupport.stream(allUsers.spliterator(), false).toList();
        model.setAll(allUsersList);
    }
    public void update(UserTaskChangeEvent taskChangeEvent) {
        initializeTableDataUser();
    }

    @FXML
    public void initialize() throws SQLException {
        tableColumnID.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        tableColumnFN.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLN.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableView.setItems(model);
        page = service.findAllPage(initialPageable);
        initModel();
    }

    private void initModel() {
        model.clear();
        Stream<Utilizator> users = page.getContent();
        ArrayList<Utilizator> userArrayList = new ArrayList<>();
        users.forEach(userArrayList::add);
        model.addAll(userArrayList);
    }

    public void handleAddUser(){
        String first_name = TextFN.getText();
        String last_name = TextLN.getText();
        String pass = TextPass.getText();
        try {
            Long id = Long.parseLong(TextID.getText());
            Utilizator user = new Utilizator(first_name, last_name,pass);
            user.setId(id);
            try {
                Optional<Utilizator> addedUser = service.add(user);
                if (addedUser.isPresent())
                    UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Exista deja un utilizator cu ID-ul dat!");
                else{
                    update(new UserTaskChangeEvent(ChangeEventType.ADD, user));
                }
            }
            catch (Exception e){
                UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
        catch (Exception e){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! ID-ul trebuie sa fie un numar!");
        }

        TextID.clear();
        TextFN.clear();
        TextLN.clear();
        TextPass.clear();
    }

    public void handleDeleteUser(){
        try {
            Long id = Long.parseLong(TextID.getText());
            try {
                Optional<Utilizator> deletedUser = service.delete(id);
                if (deletedUser.isEmpty())
                    UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Nu exista utilizator cu ID-ul dat!");
                else update(new UserTaskChangeEvent(ChangeEventType.DELETE, deletedUser.get()));
            }
            catch (Exception e){
                UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
        catch (Exception e){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! ID-ul trebuie sa fie un numar!");
        }
        TextID.clear();
        TextFN.clear();
        TextLN.clear();
    }

    public void handleUpdateUser(){
        String first_name = TextFN.getText();
        String last_name = TextLN.getText();
        String pass = TextPass.getText();

        try {
            Long id = Long.parseLong(TextID.getText());
            Utilizator user = new Utilizator(first_name,last_name,pass);
            user.setId(id);
            try {
                Optional<Utilizator> updatedUser = service.update(user);
                if (updatedUser.isEmpty())
                    UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Nu exista utilizatorul dat!");
                else update(new UserTaskChangeEvent(ChangeEventType.UPDATE, user));
            }
            catch (Exception e){
                UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
        catch (Exception e){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! ID-ul trebuie sa fie un numar!");
        }

        TextID.clear();
        TextFN.clear();
        TextLN.clear();
    }

    public void handleFindUser()  {
        try {
            Long id = Long.parseLong(TextID.getText());
            Optional<Utilizator> foundUser = service.getEntityById(id);
        if (foundUser.isEmpty()) {
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Nu exista utilizator cu ID-ul dat!");
            TextFN.clear();
            TextLN.clear();
        }
        else{
            TextFN.setText(foundUser.get().getFirstName());
            TextLN.setText(foundUser.get().getLastName());
        }
        }
        catch (Exception e){
        UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! ID-ul trebuie sa fie un numar!");
        TextFN.clear();
        TextLN.clear();
         }
    }

    public void handleNextPage() throws SQLException {

        if(service.findAllPage(page.nextPageable()).getContent().toList().isEmpty()){
            UserActionsAlert.showMessage(null, Alert.AlertType.WARNING, "No more pages!", "No more pages!");
            return;
        }
        page = service.findAllPage(page.nextPageable());
        initModel();
    }

    public void handleLastPage() throws SQLException {

        if(page.getPageable().getPageNumber()==1){
            UserActionsAlert.showMessage(null, Alert.AlertType.WARNING, "First page!", "Cannot go further than first page!");
            return;
        }
        page = service.findAllPage(page.lastPageable());
        initModel();
    }


    public void handleSelectUser(){
        Utilizator utilizator = (Utilizator) tableView.getSelectionModel().getSelectedItem();

        TextID.setText(utilizator.getId().toString());
        TextFN.setText(utilizator.getFirstName());
        TextLN.setText(utilizator.getLastName());
    }

    public void handleExitButton(){
        Node src = ExitButton;
        Stage stage = (Stage) src.getScene().getWindow();

        stage.close();
    }


}