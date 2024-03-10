package com.example.project_sa;

import com.example.project_sa.repository.*;
import com.example.project_sa.service.GeneralService;

import java.sql.SQLException;

import static org.junit.Assert.*;


public class LoginScreenTest {
    String url = "jdbc:postgresql://localhost:5432/SocialNetwork";
    String user = "postgres";
    String password = "parola789";
    private FriendshipRepository friendshipRepository;
    private MessageRepository messageRepository;

    private FriendshipRequestRepository friedshipRequestRepository;
    EventRepository eventRepository=new EventRepository(url,user,password);
    UserRepository userRepository = new UserRepository(url,user,password);
    GeneralService generalService=new GeneralService(userRepository,friendshipRepository,messageRepository,friedshipRequestRepository,eventRepository);

    public LoginScreenTest() throws SQLException, ClassNotFoundException {
    }

    @org.junit.Test
    public void logIn() {

        String Email;
        Email=generalService.findOne(1L).getEmail();
        assertEquals("popana@gmail.com", Email);

    }

}