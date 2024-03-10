package com.example.project_sa;

import com.example.project_sa.Controller.MessageAlert;
import com.example.project_sa.domain.Events;
import com.example.project_sa.domain.User;
import com.example.project_sa.observer.Observer;
import com.example.project_sa.service.GeneralService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

//import java.sql.Date;
//import java.util.Date;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddEvent  {

    @FXML
    public TextField eventName;
    @FXML
    public TextField loocation;
    @FXML
    public TextField date;

    private GeneralService service;
    public AddEvent(){}
    public void setService(GeneralService service){
        this.service = service;}
       // service.addObserver(this);
        public void handleAdd(){
            if(eventName.getText().isEmpty() || loocation.getText().isEmpty()){
                MessageAlert.showErrorMessage(null,"ALL fields must be completed! Try again.");
                eventName.clear();
                loocation.clear();
                return;
            }
            try {
                String dateText = date.getText();

                // Explicitly specify the expected date format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(dateText, formatter);
                Date eventDate = Date.valueOf(localDate);
                System.out.println(localDate);
                Events u = new Events(eventName.getText(), loocation.getText(), eventDate);
                this.service.addEvent(u);
            } catch (Exception e) {
                // Handle parsing errors or invalid date format
                MessageAlert.showErrorMessage(null, "Invalid date format! Please enter the date in yyyy-mm-dd format.");
                date.clear();
            }
        }


   // @Override
    //public void update(EventChangeEvent eventChangeEvent) {

    }

