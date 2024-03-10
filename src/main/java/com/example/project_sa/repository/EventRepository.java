package com.example.project_sa.repository;

import com.example.project_sa.domain.Events;
import com.example.project_sa.domain.User;
import com.example.project_sa.validators.Strategy;
import com.example.project_sa.validators.ValidatorFactory;
import com.example.project_sa.validators.ValidatorInterface;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class EventRepository implements Repository<Long,Events>{

    String url;
    String username;
    String password;

    public EventRepository(String url, String username, String password) throws SQLException, ClassNotFoundException {
        this.url = url;
        this.username = username;
        this.password = password;
        //connection = DriverManager.getConnection(url, username, password);
    }
    private ValidatorInterface<Events> validator = new ValidatorFactory().createValidator(Strategy.event);

    @Override
    public Optional<Events> findOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = connection.prepareStatement("select * from events where id = ?");

            statement.setInt(1,Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String event_name = resultSet.getString("event_name");
                String location = resultSet.getString("location");
                Date date = resultSet.getDate("date");

                Events e = new Events(event_name,location,date);
                e.setId(id);
                return Optional.of(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable findAll() {
        Set<Events> events = new HashSet<>();

        try(Connection connection = DriverManager.getConnection(url, username, password)){


            PreparedStatement statement = connection.prepareStatement("select * from events");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                String event_name = resultSet.getString("event_name");
                String location = resultSet.getString("location");
                Date date = resultSet.getDate("date");


                Events e = new Events(event_name,location,date);
                e.setId(id);
                events.add(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return events;

    }

    @Override
    public Optional<Events> save(Events entity) {
        if (entity == null){
            throw new RepositoryException("Entity cannot be null!");
        }
        validator.validate(entity);
        String insertSQL = "insert into events(event_name,location,date) values (?,?,?)";
        try(Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setString(1,entity.getEvent_name());
            statement.setString(2,entity.getLocation());
            statement.setDate(3,entity.getDate());

 ;
            int answer = statement.executeUpdate();
            return answer==0 ? Optional.empty(): Optional.of(entity);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Events> delete(Long id) {
        if (id == null) {
            throw new RepositoryException("id must not be null");
        }
        String deleteSQL = "delete from events where id=?";

        try(Connection connection2 = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection2.prepareStatement(deleteSQL);
            statement.setLong(1,id);
            Optional<Events> foundEvent = findOne(id);
            int answer=0;
            if(foundEvent.isPresent()){
                answer = statement.executeUpdate();
            }
            return answer == 0 ? foundEvent : Optional.empty();

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Events> update(Events entity) {
        return Optional.empty();
    }


}
