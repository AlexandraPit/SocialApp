package com.example.project_sa;

import com.example.project_sa.Controller.MessageAlert;
import com.example.project_sa.domain.User;
import com.example.project_sa.event.UserChangeEvent;
import com.example.project_sa.observer.Observer;
import com.example.project_sa.service.GeneralService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AddUser implements Observer<UserChangeEvent> {
    @FXML
    public TextField firstName;
    @FXML
    public TextField lastName;
    @FXML
    public TextField username;
    @FXML
    public TextField email;
    @FXML
    public TextField password;



    private GeneralService service;

    public AddUser(){}

    public void setService(GeneralService service){
        this.service = service;
        service.addObserver(this);

    }

    public void handleAdd(){
        if(firstName.getText().isEmpty() || lastName.getText().isEmpty() ||
                username.getText().isEmpty() || email.getText().isEmpty() ||
                password.getText().isEmpty()){
            MessageAlert.showErrorMessage(null,"ALL fields must be completed! Try again.");
            firstName.clear();
            lastName.clear();
            username.clear();
            email.clear();
            password.clear();
            return;
        }
        User u = new User(firstName.getText(),lastName.getText(),username.getText(),email.getText(),password.getText());
        try {
            this.service.addUser(u);
        } catch (RuntimeException e) {
            // Handle the case where the username already exists
            MessageAlert.showErrorMessage(null, "Username or Email already exists! Please choose different ones.");
            username.clear();
        }

    }

    @Override
    public void update(UserChangeEvent userChangeEvent) {

    }

}
