package com.example.project_sa.service;

import com.example.project_sa.domain.User;
import com.example.project_sa.repository.*;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class GeneralServiceTest {
    String url = "jdbc:postgresql://localhost:5432/SocialNetwork";
    String user = "postgres";
    String password = "parola789";

    UserRepository userRepository = new UserRepository(url,user,password);
    EventRepository eventRepository=new EventRepository(url, user,password);
    private FriendshipRepository friendshipRepository;
    private MessageRepository messageRepository;

    private FriendshipRequestRepository friedshipRequestRepository;

    public GeneralServiceTest() throws SQLException, ClassNotFoundException {
    }

    @Test
    public void findOne() {

        GeneralService generalService=new GeneralService(userRepository,
                 friendshipRepository,
                 messageRepository,
                friedshipRequestRepository,
                eventRepository
                );
        String lastName = generalService.findOne(16L).getLast_name();

        assertEquals("Ciuca", lastName);
    }
}