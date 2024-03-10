package com.example.project_sa;


import com.example.project_sa.Controller.MessageAlert;
import com.example.project_sa.domain.Events;
import com.example.project_sa.domain.User;
import com.example.project_sa.event.FriendshipChangeEvent;
import com.example.project_sa.observer.Observer;
import com.example.project_sa.service.FriendRequestService;
import com.example.project_sa.service.GeneralService;
import com.example.project_sa.service.MessageService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserScreen implements Observer<FriendshipChangeEvent> {

    User loggedUser;

    ObservableList<User> model = FXCollections.observableArrayList();
    GeneralService service ;
    FriendRequestService frservice;


    @FXML
    ListView<User> userListView = new ListView<>();

    FriendRequestService frService ;

    MessageService messageService;

    @FXML
    Label loggedUsername;

    public UserScreen() throws SQLException, ClassNotFoundException {

    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
        this.loggedUsername.setText(this.loggedUser.getUsername());
    }

    public void setService(GeneralService generalService, FriendRequestService friendRequestService, MessageService messageService) {
        this.frService = friendRequestService;
        this.service = generalService;
        this.messageService = messageService;
        frService.addObserver(this);
        initModel();
    }

    public void handleSendFriendshipRequest(){ sendFriendshipRequestScreen();}

    public void handleSeeChats(){ showChatsDialogue();}
    public void handleSeeEvents(){
            showEventsScreen();
    }
    private void showEventsScreen() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("event-screen.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Events");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EventScreen eventScreen=loader.getController();
            eventScreen.initialize();



            dialogStage.show();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void showChatsDialogue() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("chat-history.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chats");
            dialogStage.initModality(Modality.WINDOW_MODAL);


            Scene scene = new Scene(root);
            dialogStage.setScene(scene);


            ChatHistory chatHistory=loader.getController();
            chatHistory.setUtils(loggedUser,messageService, service);
            dialogStage.show();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendFriendshipRequestScreen() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("send-request.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Send Friendship Request");
            dialogStage.initModality(Modality.WINDOW_MODAL);


            Scene scene = new Scene(root);
            dialogStage.setScene(scene);



            SendRequest sendRequest = loader.getController();
            sendRequest.setUtils(loggedUser,frService,service);


            dialogStage.show();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleSeeFriendshipRequests(){
        showFriendshipRequestsScreen();
    }

    public void handleDeleteFriend(){
        if (userListView.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select an User first!");
            return;
        }

        User toBeDeleted = userListView.getSelectionModel().getSelectedItem();
        this.frService.deleteFriend(this.loggedUser.getId(),toBeDeleted.getId());

    }
    public void handleExit(){showLogInScreen();}
    public void showLogInScreen()
    {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage=(Stage)loggedUsername.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Log In Page");
            stage.show();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    private void initModel() {
        model.clear();
        userListView.getItems().clear();

        List<User> friends = service.friendsListForUser(loggedUser.getId());
        ArrayList<User> userArrayList = new ArrayList<>();
        friends.forEach(userArrayList::add);
        model.setAll(userArrayList);
        userListView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getUsername());
                }
            }
        });
        userListView.setItems(model);
    }

    private void showFriendshipRequestsScreen(){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("friendship_requests.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friendship Requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);


            FriendReqControl friendshipRequestsController = loader.getController();
            friendshipRequestsController.setUtils(loggedUser,frService);


            dialogStage.show();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void update(FriendshipChangeEvent friendshipRequestChangeEvent) {
        Platform.runLater(() -> initModel());
    }
}
