package com.example.project_sa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    public void start(Stage stage)throws IOException
    {
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setTitle("Social App");
        stage.setScene(scene);
        stage.show();


    }
    public static void main(String[] args) throws ClassNotFoundException{
        String url = "jdbc:postgresql://localhost:5432/SocialNetwork";
        String user = "postgres";
        String password = "parola789";
        launch();
    }
}
