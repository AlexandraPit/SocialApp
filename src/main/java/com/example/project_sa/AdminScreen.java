package com.example.project_sa;

import com.example.project_sa.Controller.MessageAlert;
import com.example.project_sa.domain.User;
import com.example.project_sa.event.UserChangeEvent;
import com.example.project_sa.observer.Observer;
import com.example.project_sa.repository.*;
import com.example.project_sa.service.GeneralService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AdminScreen implements Observer<UserChangeEvent> {
    String url = "jdbc:postgresql://localhost:5432/SocialNetwork";
    String user = "postgres";
    String passwd = "parola789";

    UserRepository userRepository = new UserRepository(url,user,passwd);
    FriendshipRepository friendshipRepository = new FriendshipRepository(url,user,passwd);
    MessageRepository messageRepository=new MessageRepository(url,user,passwd);
    FriendshipRequestRepository friedshipRequestRepository = new FriendshipRequestRepository(url, user,passwd);
    EventRepository eventRepository=new EventRepository(url,user,passwd);
    ObservableList<User> model = FXCollections.observableArrayList();
    @FXML
    TableView<User> userTable = new TableView<>();
    @FXML
    private TableColumn<User,Long> id = new TableColumn<>();
    @FXML
    private TableColumn<User,String> first_name= new TableColumn<>();
    @FXML
    private TableColumn<User,String> last_name= new TableColumn<>();
    @FXML
    private TableColumn<User,String> username= new TableColumn<>();
    @FXML
    private TableColumn<User,String> email= new TableColumn<>();
    @FXML
    private TableColumn<User,String> password= new TableColumn<>();
    @FXML
    private ChoiceBox OrderBy=new ChoiceBox();
    @FXML
    private TextField searchField = new TextField();
    private String[] order={"Alphabetic","By Id"};
    public int type;
    private GeneralService service= new GeneralService(userRepository,friendshipRepository,messageRepository,friedshipRequestRepository, eventRepository);
    public AdminScreen() throws SQLException, ClassNotFoundException {
        service.addObserver(this);
        userTable.setEditable(false);
        initialize();


        Platform.runLater(()->{
            OrderBy.getItems().addAll(order);
            OrderBy.setValue(order[0]);
            OrderBy.setOnAction(event -> {
                String ord = OrderBy.getValue().toString();
                if(ord.equals(order[0]))
                    type = 0;
                else  type = 1;
                initModel();
            });
        });

    }

    public void getOrder(ActionEvent e){

    }

    public void handleDelete(){
        if (userTable.getSelectionModel().isEmpty()) {

            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select an User first!");
            return;
        }

        User toBeDeleted = userTable.getSelectionModel().getSelectedItem();
        this.service.deleteUser(toBeDeleted.getId());
    }

    public void handleAdd(){
        showAddUserDialogue();
    }

    public void handleExit(){
        showLogInScreen();}


    public void showLogInScreen()
    {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage=(Stage)userTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Log In Page");
            stage.show();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }





    @Override
    public void update(UserChangeEvent userChangeEvent) {
        initModel();
    }


    @FXML
    protected void initialize(){
        id.setCellValueFactory(new PropertyValueFactory<User,Long>("id"));
        first_name.setCellValueFactory(new PropertyValueFactory<User,String>("first_name"));
        last_name.setCellValueFactory(new PropertyValueFactory<User,String>("last_name"));
        username.setCellValueFactory(new PropertyValueFactory<User,String>("username"));
        email.setCellValueFactory(new PropertyValueFactory<User,String>("email"));
        password.setCellValueFactory(new PropertyValueFactory<User,String>("password"));
        userTable.setItems(model);
        initModel();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchUsers(newValue));
    }

    private void initModel() {
        model.clear();
        Iterable<User> users = service.findAllUsers();
        List<User> usersList = StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList());
        switch (type) {
            case 0: // Alphabetic order by first_name
                usersList.sort(Comparator.comparing(User::getFirst_name));
                break;
            case 1: // Order by Id
                usersList.sort(Comparator.comparing(User::getId));
                break;
            // Add more cases if needed for different order types

            default:
                break;
        }
        model.addAll(usersList);
    }
    @FXML
    private void handleSearch()
    {
        Platform.runLater(()->{
            searchUsers(searchField.getText());
        });

    }
    private void searchUsers(String searchTerm) {
        model.clear();
        Iterable<User> users = service.findAllUsers();
        List<User> filteredUsers = StreamSupport.stream(users.spliterator(), false)
                .filter(user -> userMatchesSearch(user, searchTerm))
                .collect(Collectors.toList());

        switch (type) {
            case 0: // Alphabetic order by first_name
                filteredUsers.sort(Comparator.comparing(User::getFirst_name));
                break;
            case 1: // Order by Id
                filteredUsers.sort(Comparator.comparing(User::getId));
                break;
            // Add more cases if needed for different order types

            default:
                break;
        }

        model.addAll(filteredUsers);
    }
    private boolean userMatchesSearch(User user, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return true; // No search term, include all users
        }

        // Check if any user attribute contains the search term (case-insensitive)
        String lowerSearchTerm = searchTerm.toLowerCase();
        return user.getFirst_name().toLowerCase().contains(lowerSearchTerm) ||
                user.getLast_name().toLowerCase().contains(lowerSearchTerm) ||
                user.getUsername().toLowerCase().contains(lowerSearchTerm) ||
                user.getEmail().toLowerCase().contains(lowerSearchTerm) ||
                user.getPassword().toLowerCase().contains(lowerSearchTerm);
    }
    private void showAddUserDialogue(){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("add-user.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friendship Requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);


            AddUser addUser = loader.getController();
            addUser.setService(service);


            dialogStage.show();
            //initModel();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
