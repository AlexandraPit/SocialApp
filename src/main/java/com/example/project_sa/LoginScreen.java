package com.example.project_sa;

import com.example.project_sa.Controller.MessageAlert;
import com.example.project_sa.domain.RememberMe;
import com.example.project_sa.domain.User;
import com.example.project_sa.repository.*;
import com.example.project_sa.service.FriendRequestService;
import com.example.project_sa.service.GeneralService;
import com.example.project_sa.service.MessageService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.prefs.Preferences;

import java.io.IOException;
import java.sql.SQLException;
import java.util.prefs.Preferences;

import static com.example.project_sa.repository.RememberMeRepository.getRememberedCredentials;

public class LoginScreen {

    String url = "jdbc:postgresql://localhost:5432/SocialNetwork";
    String user = "postgres";
    String password = "parola789";

    UserRepository userRepository = new UserRepository(url,user,password);
    FriendshipRepository friendshipRepository = new FriendshipRepository(url,user,password);
    FriendshipRequestRepository friedshipRequestRepository = new FriendshipRequestRepository(url, user,password);
    MessageRepository messageRepository = new MessageRepository(url,user,password);

    RememberMeRepository rememberMeRepository=new RememberMeRepository(url,user,password);
    EventRepository eventRepository=new EventRepository(url,user,password);
    GeneralService service = new GeneralService(userRepository,friendshipRepository, messageRepository,friedshipRequestRepository, eventRepository);

    FriendRequestService frservice = new FriendRequestService(userRepository,friendshipRepository,friedshipRequestRepository);
    MessageService messageService = new MessageService(userRepository,friendshipRepository,friedshipRequestRepository,messageRepository);

    @FXML
    public TextField EmailField;

    @FXML
    private PasswordField PAsswordField;
    @FXML
   private CheckBox rememberMeCheckBox;
  //  private static final String CHECK_PREFERENCE_KEY = "rememberMeCheck";
  //  boolean check;
/*    @FXML
    void LogInAction(ActionEvent event){}*/

    public LoginScreen() throws SQLException, ClassNotFoundException {
        System.out.println("wowoow");
        Optional<RememberMe> savedCredentials = getRememberedCredentials();
        savedCredentials.ifPresent(credentials -> {
            boolean checked = credentials.getChecked();
            String email = credentials.getEmail_r();
            String password = credentials.getPassword_r();
            if(checked==true)
                loadSavedCredentials(email, password);
        });

    }

    public void LogIn() {

        if(this.service.checkCredentials(EmailField.getText(),PAsswordField.getText()).isPresent()){

            if(rememberMeCheckBox.isSelected())
            {
                saveCredentials(EmailField.getText(), PAsswordField.getText(), true);
            }
            else
                saveCredentials("", "", false);
            showUserScreen(this.service.checkCredentials(EmailField.getText(),PAsswordField.getText()).get());
        }
        else
            HandleUnsuccessfulLogIn();
    }
   private void saveCredentials(String email, String password, boolean checked) {
        RememberMeRepository.saveCredentials(email, password, checked);
    }

    private void loadSavedCredentials(String email, String password) {
        /*Optional<RememberMe> savedCredentials = getRememberedCredentials();
        savedCredentials.ifPresent(credentials -> {
            String email = credentials.getEmail_r();
            String password = credentials.getPassword_r();
            boolean checked = credentials.getChecked();

            System.out.println("Loaded password: " + password);
            rememberMeCheckBox.setSelected(checked);


         */
        System.out.println("Loaded email: " + email);
            //if (!email.equals("")) {
               Platform.runLater(()-> EmailField.setText(email));
            //}

            //if (!password.equals("")) {
             Platform.runLater(()->PAsswordField.setText(password));
            //}
        //});
    }

    private void clearRememberedCredentials() {
        rememberMeRepository.clearRememberedCredentials();
    }

    private void showUserScreen(User loggedUser) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("UserScreen.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage dialogStage = (Stage)PAsswordField.getScene().getWindow();
            dialogStage.setTitle("User Page");
            //dialogStage.initModality(Modality.WINDOW_MODAL);


            dialogStage.setScene(scene);


            UserScreen userScreen = loader.getController();

            userScreen.setLoggedUser(loggedUser);
            userScreen.setService(service,frservice,messageService);


            dialogStage.show();

        } catch (IOException e ) {
            throw new RuntimeException(e);
        }
    }
public void createAccount()
{
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
public void showAdminScreen()
{
    try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-screen.fxml"));
        Parent root = loader.load();
        //Scene scene = new Scene(root);
        Stage stage = (Stage)PAsswordField.getScene().getWindow();
        stage.getScene().setRoot(root);
        stage.setTitle("Admin Page");
    }catch(Exception e){
        throw new RuntimeException(e);
    }
}

public void HandleUnsuccessfulLogIn()
{
    MessageAlert.showErrorMessage(null,"Could NOT log in! Try again.");
    this.EmailField.clear();
    this.PAsswordField.clear();
}
}