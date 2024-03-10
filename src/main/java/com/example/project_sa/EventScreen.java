package com.example.project_sa;

import com.example.project_sa.Controller.MessageAlert;
import com.example.project_sa.domain.Events;
import com.example.project_sa.domain.User;
import com.example.project_sa.repository.*;
import com.example.project_sa.service.GeneralService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EventScreen {
    String url = "jdbc:postgresql://localhost:5432/SocialNetwork";
    String user = "postgres";
    String passwd = "parola789";
    UserRepository userRepository = new UserRepository(url,user,passwd);
    FriendshipRepository friendshipRepository = new FriendshipRepository(url,user,passwd);
    MessageRepository messageRepository=new MessageRepository(url,user,passwd);
    FriendshipRequestRepository friedshipRequestRepository = new FriendshipRequestRepository(url, user,passwd);

    ObservableList<Events> model = FXCollections.observableArrayList();

    @FXML
    ListView<Events> eventListView = new ListView<>();


    EventRepository eventRepository=new EventRepository(url,user,passwd);
    private GeneralService service= new GeneralService(userRepository,friendshipRepository,messageRepository,friedshipRequestRepository, eventRepository);


    public EventScreen() throws SQLException, ClassNotFoundException {
       // service.addObserver(this);
        eventListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        initialize();
    }

    public void handleDelete(){
        if (eventListView.getSelectionModel().isEmpty()) {

            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select an Event first!");
            return;
        }

        Events selectedEvent = eventListView.getSelectionModel().getSelectedItem();

        this.service.deleteEvent(selectedEvent.getId());
        initModel();
    }
    public void handleExit(){
        showUserScreen();}
    public void showUserScreen()
    {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserScreen.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage=(Stage)eventListView.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("User Page");
            stage.show();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    @FXML
    protected void initialize(){

        initModel();
    }
    private void initModel() {
        model.clear();
        eventListView.getItems().clear();

        Iterable<Events> events = service.findAllEvents();
        List<Events> eventsList = StreamSupport.stream(events.spliterator(), false)
                .collect(Collectors.toList());
        model.addAll(eventsList);
        this.eventListView.setItems(model);
    }
    public void handleDeleteEvent(){
        if(eventListView.getSelectionModel().isEmpty())
        {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING,"Slection Empty", "Please select an Event first!");
            return;
        }
        Events toBeDeleted = (Events) eventListView.getSelectionModel().getSelectedItems();
        this.service.deleteEvent(toBeDeleted.getId());
        initialize();
    }
    public void handleAdd(){showAddEventDialogue();}
    private void showAddEventDialogue(){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("add-event.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Event adder");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);


            AddEvent addEvent = loader.getController();


            dialogStage.show();
            initialize();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
