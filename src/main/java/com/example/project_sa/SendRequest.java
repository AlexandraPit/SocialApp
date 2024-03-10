package com.example.project_sa;

import com.example.project_sa.Controller.MessageAlert;
import com.example.project_sa.domain.User;
import com.example.project_sa.event.FriendshipChangeEvent;
import com.example.project_sa.observer.Observer;
import com.example.project_sa.service.FriendRequestService;
import com.example.project_sa.service.GeneralService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.Set;

public class SendRequest implements Observer<FriendshipChangeEvent> {
    User loggedUser;
    ObservableList<User> model = FXCollections.observableArrayList();
    GeneralService service ;
    @FXML
    ListView<User> userListView = new ListView<>();

    FriendRequestService frService ;


    public SendRequest(){

    }

    public void setUtils(User loggedUser, FriendRequestService frservice, GeneralService service) {
        this.loggedUser = loggedUser;
        this.frService = frservice;
        this.service = service;
        frService.addObserver(this);
        initModel();
    }

    public void handleSendFriendRequest(){
        if (userListView.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select an User first!");
            return;
        }

        User possibleFriend = userListView.getSelectionModel().getSelectedItem();
        this.frService.sendFriendRequest(loggedUser.getId(),possibleFriend.getId());

        model.remove(possibleFriend);
        userListView.refresh();
    }

    private void initModel() {
        model.clear();
        userListView.getItems().clear();

        Set<User> possibleFriends = frService.possibleFriends(loggedUser.getId());
        ArrayList<User> userArrayList = new ArrayList<>();
        possibleFriends.forEach(userArrayList::add);
        model.setAll(userArrayList);
        userListView.setItems(model);
    }

    @Override
    public void update(FriendshipChangeEvent friendshipRequestChangeEvent) {

        Platform.runLater(() -> initModel());
    }
}
