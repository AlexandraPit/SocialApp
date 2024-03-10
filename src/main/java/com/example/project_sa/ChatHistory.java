package com.example.project_sa;

import com.example.project_sa.Controller.MessageAlert;
import com.example.project_sa.domain.Message;
import com.example.project_sa.domain.User;
import com.example.project_sa.event.MessageChangeEvent;
import com.example.project_sa.observer.Observer;
import com.example.project_sa.service.GeneralService;
import com.example.project_sa.service.MessageService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class ChatHistory implements Observer<MessageChangeEvent> {
    @FXML
    public Label name = new Label();

    @FXML
    public ListView<HBox> conversation = new ListView<>();
    ObservableList<HBox> model = FXCollections.observableArrayList();
    private ArrayList<Message> messages = new ArrayList<>();

    @FXML
    public ListView<User> friendList= new ListView<>();

    @FXML
    public TextArea messageArea = new TextArea();

    private final ObservableList<User> friends = FXCollections.observableArrayList();

    private User loggedUser;
    private User selectedUser;

    private MessageService messageService;

    public void setUtils(User loggedUser, MessageService messageService, GeneralService generalService){
        this.loggedUser = loggedUser;
        this.name.setText(loggedUser.getUsername());
        this.messageService = messageService;
        this.messageService.addObserver(this);

        this.messageService.conversationBetween(loggedUser, generalService.findOne(16L)).forEach(this.messages::add);
        initializeFriendList();
        friendList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        friendList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && friendList.getSelectionModel().getSelectedItems().size()==1) {
                handleSelectSingle();
            }
        });

    }

    public void handleSend(){
        if(messageArea.getText().isEmpty()){
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Empty Message", "You cannot send an empty message!");
            return;
        }
        this.messageService.sendMessage(this.loggedUser,this.selectedUser,messageArea.getText());
        initializeChat();
        this.messageArea.clear();

    }

    public void handleSelectMultiple(){
        ObservableList<User> selectedUsers = friendList.getSelectionModel().getSelectedItems();
        if (selectedUsers.isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select at least 2 users!");
            return;
        }
        else if(selectedUsers.size()==1){
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "One user", "Please select at least one more user!");
            return;
        }
        if(messageArea.getText().isEmpty()){
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Empty Message", "You cannot send an empty message!");
            return;
        }
        ArrayList<User> friendsToSend = new ArrayList<>();
        selectedUsers.stream().forEach(friendsToSend::add);
        this.messageService.sendMessageToMultipleUsers(this.loggedUser,friendsToSend,messageArea.getText());
        this.messageArea.clear();
    }

    public void handleSelectSingle(){
        if (friendList.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select an User first!");
            return;
        }
        this.selectedUser =  friendList.getSelectionModel().getSelectedItem();
        this.messages.clear();
        this.messageService.conversationBetween(this.loggedUser,this.selectedUser).stream().forEach(this.messages::add);
        initializeChat();

    }

    private void initializeFriendList(){
        this.loggedUser.getFriend_list().stream().forEach(friends::add);
        friendList.setCellFactory(param -> new ListCell<User>() {
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
        this.friendList.setItems(friends);

    }



    private void initializeChat() {
        this.messages.clear();
        this.messageService.conversationBetween(loggedUser,selectedUser).forEach(this.messages::add);
        //initializeFriendList();
        this.conversation.getItems().clear();
        this.messages.forEach(
                x->{
                    Label label = new Label(x.getMessage());
                    label.setWrapText(true);
                    HBox container = new HBox();
                    container.getChildren().add(label);
                    if(x.getFrom().equals(this.loggedUser)){
                        label.setStyle("-fx-background-color: #b5ce99;\n" +
                                "-fx-border-color: #7b9f6d;\n" +
                                "-fx-border-radius: 15;\n" +
                                "-fx-start-margin: 15;\n" +
                                "-fx-padding: 10;\n" +
                                "-fx-background-radius: 15;\n" +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
                        container.setAlignment(Pos.CENTER_RIGHT);


                    }
                    else{
                        label.setStyle("-fx-background-color: #ffebed;\n" +
                                "-fx-border-color: rgb(232,169,179);\n" +
                                "-fx-border-radius: 15;\n" +
                                "    -fx-padding: 10;\n" +
                                "    -fx-background-radius: 15;\n" +
                                "    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
                        container.setAlignment(Pos.CENTER_LEFT);
                    }
                    this.model.add(container);

                }
        );

        this.conversation.setItems(model);
    }


    @Override
    public void update(MessageChangeEvent changeEvent) {

        Platform.runLater(() -> {
            initializeChat();
        });

    }
}
