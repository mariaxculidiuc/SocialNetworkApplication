package com.example.demo.controller;

import com.example.demo.controller.alert.FriendRequestActionAlert;
import com.example.demo.controller.alert.MessageActionAlert;
import com.example.demo.controller.alert.UserActionsAlert;
import com.example.demo.domain.*;
import com.example.demo.repository.RepoDB.UserRepoPagingDB;
import com.example.demo.repository.paging.Page;
import com.example.demo.repository.paging.PageableImplementation;
import com.example.demo.service.FriendRequestService;
import com.example.demo.service.MessageService;
import com.example.demo.service.PrietenieService;
import com.example.demo.service.UtilizatorService;
import com.example.demo.utils.events.*;
import com.example.demo.utils.observer.Observer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class AddController implements Observer<UserTaskChangeEvent> {
    private Long id_user;
    String url = "jdbc:postgresql://localhost:5432/socialnetwork";
    String user = "postgres";
    String passwd = "selena23";

    UserRepoPagingDB userRepository = new UserRepoPagingDB(url,user,passwd);
    private UtilizatorService utilizatorService = new UtilizatorService(userRepository);
    private FriendRequestService friendRequestService;
    private PrietenieService prietenieService;
    private MessageService messageService;
    ObservableList<Utilizator> modelfriends = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelrequests = FXCollections.observableArrayList();
    private ObservableList<Message> modelMessage = FXCollections.observableArrayList();

    Page<Utilizator> page = null;

    PageableImplementation initialPageable = new PageableImplementation(1,1);

    @FXML
    ListView<Message> listMessages;
    @FXML
    public TableView<Utilizator> tableFriends;

    @FXML
    public TableView<Utilizator> tableFriendsRequests;

    @FXML
    public TableView<Utilizator> table_list_friends;

    @FXML
    public TableColumn<Utilizator, String> tableColumnFN;
    @FXML
    public TableColumn<Utilizator, String> tableColumnLN;

    @FXML
    public TableColumn<Utilizator, String> tableColumnFN2;
    @FXML
    public TableColumn<Utilizator, String> tableColumnLN2;

    @FXML
    public TableColumn<Utilizator, String> tableColumnFN3;
    @FXML
    public TableColumn<Utilizator, String> tableColumnLN3;

    @FXML
    public TextField text_fn;

    @FXML
    public TextField text_ln;

    @FXML
    public TextField text_pass;

    @FXML
    public TextField message;
    @FXML
    public TextField fn_account;

    @FXML
    TextField ln_account;

    @FXML
    public Button add;
    @FXML
    public Button exit1;

    @FXML
    public Button exit2;

    public void setService(Long id, FriendRequestService friendRequestService, UtilizatorService utilizatorService, PrietenieService prietenieService, MessageService messageService) {
        this.id_user = id;
        this.friendRequestService = friendRequestService;
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.messageService = messageService;
        utilizatorService.addObserver(this);
        prietenieService.addObserver(this);
        friendRequestService.addObserver(this);
        messageService.addObserver(this);
        initializeTableFriends();
        initializeTableFriendRequests();
        account();

    }

    public void update(UserTaskChangeEvent taskChangeEvent) {
        initializeTableFriends();
        initializeTableFriendRequests();
        handleSelect(new MouseEvent(MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, false, false, false, null));
    }


    public void initializeTableFriends() {
        List<Long> friends = utilizatorService.get_friends(id_user);
        Iterable<Utilizator> allFriendsUser = friends.stream()
                .map(x -> utilizatorService.getEntityById(x).orElse(null))
                .collect(Collectors.toList());

        List<Utilizator> allFriend = StreamSupport.stream(allFriendsUser.spliterator(), false).toList();
        modelfriends.setAll(allFriend);
    }

    public void initializeTableFriendRequests() {
        List<Long> friendRequests = friendRequestService.getFriendRequestIds(id_user);
        Iterable<Utilizator> allFriendRequestsUser = friendRequests.stream()
                .map(x -> {
                    return utilizatorService.getEntityById(x).get();
                })
                .collect(Collectors.toList());

        List<Utilizator> allFriendRequests = StreamSupport.stream(allFriendRequestsUser.spliterator(), false).toList();
        modelrequests.setAll(allFriendRequests);
    }

    @FXML
    public void initialize() throws SQLException {
        tableColumnFN.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLN.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableFriends.setItems(modelfriends);

        tableColumnFN2.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLN2.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableFriendsRequests.setItems(modelrequests);

        tableColumnFN3.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLN3.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));

        table_list_friends.setItems(modelfriends);
        table_list_friends.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        page = utilizatorService.findAllPage(initialPageable);
        initModel();
    }

    private void initModel() {
        modelfriends.clear();
        Stream<Utilizator> users = page.getContent();
        ArrayList<Utilizator> userArrayList = new ArrayList<>();
        users.forEach(userArrayList::add);
        modelfriends.addAll(userArrayList);
    }

    public void account() {
        Optional<Utilizator> u = utilizatorService.getEntityById(id_user);
        fn_account.setText(u.get().getFirstName());
        ln_account.setText(u.get().getLastName());
        fn_account.setEditable(false);
        ln_account.setEditable(false);
    }


    public void handleAccept() {
        Utilizator utilizator = tableFriendsRequests.getSelectionModel().getSelectedItem();
        Tuple<Long, Long> ids = new Tuple<>(utilizator.getId(), id_user);

        FriendRequest friendRequest = friendRequestService.getEntityById(ids).get();
        friendRequest.setStatus(FriendRequestStatus.APPROVED);
        friendRequestService.update(friendRequest);

        Prietenie prietenie = new Prietenie(LocalDateTime.now());
        prietenie.setId(ids);
        prietenieService.add(prietenie);
        friendRequestService.notifyObservers(new FriendRequestTaskChangeEvent(ChangeEventType.ADD, friendRequest));
        update(new PrietenieTaskChangeEvent(ChangeEventType.ADD, prietenie));
    }

    public void handleDelete() {
        Utilizator utilizator = tableFriendsRequests.getSelectionModel().getSelectedItem();
        Tuple<Long, Long> ids = new Tuple<>(utilizator.getId(), id_user);

        FriendRequest friendRequest = friendRequestService.getEntityById(ids).get();
        friendRequest.setStatus(FriendRequestStatus.REJECTED);
        friendRequestService.update(friendRequest);
        friendRequestService.notifyObservers(new FriendRequestTaskChangeEvent(ChangeEventType.ADD, friendRequest));

        update(new FriendRequestTaskChangeEvent(ChangeEventType.DELETE, friendRequest));
    }

    public void handle_add_friend() {
        String first_name = text_fn.getText();
        String last_name = text_ln.getText();
        String pass = text_pass.getText();


        if (first_name == null || last_name == null) {
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Trebuie sa completezi campurile!");
            return;
        }
        Optional<Utilizator> utilizator = utilizatorService.findUserByName(first_name, last_name,pass);
        if (utilizator.isEmpty()) {
            FriendRequestActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Utilizatorul introdus nu exista!");
            return;
        }

        FriendRequestStatus friendRequestStatus = FriendRequestStatus.PENDING;
        FriendRequest friendRequest = new FriendRequest(friendRequestStatus);
        friendRequest.setId(new Tuple<>(id_user, utilizator.get().getId()));

        try {
            Optional<FriendRequest> sentRequest = friendRequestService.add(friendRequest);
            if (sentRequest.isPresent())
                FriendRequestActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Exista deja o cerere catre utilizatorul dat!");
            else {
                FriendRequestActionAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes!", "Cererea a fost trimisa cu succes!");
                friendRequestService.notifyObservers(new FriendRequestTaskChangeEvent(ChangeEventType.ADD, friendRequest));
                Platform.runLater(() -> update(new FriendRequestTaskChangeEvent(ChangeEventType.ADD, friendRequest)));
            }
        } catch (Exception e) {
            FriendRequestActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
        }

        text_fn.clear();
        text_ln.clear();
    }

    private void loadListOfMessages(List<Long> userIds) {
        listMessages.getItems().clear();
        modelMessage.clear();

        for (Long userId : userIds) {
            List<Message> messages = messageService.getMessagesBetweenTwoUsers(id_user, userId);
            modelMessage.addAll(messages);
        }

        listMessages.setItems(modelMessage);
    }

    public void handleNextPage() throws SQLException {

        if(utilizatorService.findAllPage(page.nextPageable()).getContent().toList().isEmpty()){
            UserActionsAlert.showMessage(null, Alert.AlertType.WARNING, "No more pages!", "No more pages!");
            return;
        }
        page = utilizatorService.findAllPage(page.nextPageable());
        initModel();
    }

    public void handleLastPage() throws SQLException {

        if(page.getPageable().getPageNumber()==1){
            UserActionsAlert.showMessage(null, Alert.AlertType.WARNING, "First page!", "Cannot go further than first page!");
            return;
        }
        page = utilizatorService.findAllPage(page.lastPageable());
        initModel();
    }

    public void handleSelect(MouseEvent event) {
        List<Utilizator> utilizatori = table_list_friends.getSelectionModel().getSelectedItems();

        boolean isCtrlPressed = event.isControlDown();

        if (!utilizatori.isEmpty()) {
            if (!isCtrlPressed) {
                table_list_friends.getSelectionModel().clearSelection();
            }

            List<Long> userIds = utilizatori.stream().map(Utilizator::getId).collect(Collectors.toList());
            loadListOfMessages(userIds);
        } else {
            listMessages.getItems().clear();
            modelMessage.clear();
        }
    }

    public void handleSend() {
        String messageText = message.getText();

        if (messageText.isEmpty()) {
            MessageActionAlert.showErrorMessage(null, "Please fill in the message field to send a message!");
            return;
        }

        List<Utilizator> selectedUsers = table_list_friends.getSelectionModel().getSelectedItems();

        if (selectedUsers.isEmpty()) {
            MessageActionAlert.showErrorMessage(null, "Please select at least one friend to send a message!");
            return;
        }
        Utilizator sender = utilizatorService.getEntityById(id_user).orElseThrow(); // Obține utilizatorul curent

        for (Utilizator recipient : selectedUsers) {
            messageService.addMessage(sender.getId(), recipient.getId(), messageText);
        }

        table_list_friends.getSelectionModel().clearSelection();

        message.clear();
    }

    public void handle_exit1() {
        Node src = exit1;
        Stage stage = (Stage) src.getScene().getWindow();

        stage.close();
    }

    public void handle_exit2() {
        Node src = exit2;
        Stage stage = (Stage) src.getScene().getWindow();

        stage.close();
    }
}