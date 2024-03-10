package com.example.project_sa;

import com.example.project_sa.Controller.MessageAlert;
import com.example.project_sa.domain.FriendshipRequest;
import com.example.project_sa.domain.Tuple;
import com.example.project_sa.domain.User;
import com.example.project_sa.service.FriendRequestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class FriendReqControl {
    User loggedUser;

    FriendRequestService service;
    ObservableList<FriendshipRequest> model = FXCollections.observableArrayList();

    @FXML
    TableView<FriendshipRequest> friendshipRequestTableView = new TableView<>();

    @FXML
    TableColumn<FriendshipRequest, String> fromColumn;

    @FXML
    private Label name = new Label();

    public void setUtils(User u, FriendRequestService s) {
        this.loggedUser = u;
        this.service = s;
        name.setText(u.getUsername());
        initialize();
    }

    public void handleAccept() {
        if (friendshipRequestTableView.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select a Friend Request First!");
            return;
        }

        FriendshipRequest fr = friendshipRequestTableView.getSelectionModel().getSelectedItem();
        this.service.acceptFriendRequest(new Tuple<>(fr.getFrom().getId(), fr.getTo().getId()));
        initModel();
    }

    public void handleDecline() {
        if (friendshipRequestTableView.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select a Friend Request First!");
            return;
        }

        FriendshipRequest fr = friendshipRequestTableView.getSelectionModel().getSelectedItem();
        this.service.declineFriendRequest(new Tuple<>(fr.getFrom().getId(), fr.getTo().getId()));
        initModel();
    }

    private void initialize() {
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));
        friendshipRequestTableView.setItems(model);
        initModel();
    }

    private void initModel() {
        model.clear();
        ArrayList<FriendshipRequest> pending = this.service.pendingFriendshipRequests(loggedUser.getId());
        model.addAll(pending);
    }
}

