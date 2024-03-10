package com.example.project_sa.repository;

import com.example.project_sa.AdminScreen;
import com.example.project_sa.domain.User;
import com.example.project_sa.validators.Strategy;
import com.example.project_sa.validators.ValidatorFactory;
import com.example.project_sa.validators.ValidatorInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserRepository implements Repository<Long,User>{
    private ValidatorInterface<User> validator = new ValidatorFactory().createValidator(Strategy.user);

    String url;
    String username;
    String password;

    public UserRepository( String url, String username, String password) throws SQLException, ClassNotFoundException {
        this.url = url;
        this.username = username;
        this.password = password;
        //connection = DriverManager.getConnection(url, username, password);
    }

    private ArrayList<User> findAllFriends(Long id){

        ArrayList<User> friends = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT U.id, U.first_name, U.last_name, U.username, U.email, U.password FROM friendships F JOIN users U ON (F.id_user1 = U.id OR F.id_user2 = U.id) WHERE (F.id_user1 = ? OR F.id_user2 = ?) AND U.id <> ?");

            statement.setInt(1,Math.toIntExact(id));
            statement.setInt(2,Math.toIntExact(id));
            statement.setInt(3,Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long id_friend = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username_user = resultSet.getString("username");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User u = new User(firstName,lastName,username_user,email,password);
                u.setId(id_friend);
                friends.add(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friends;
    }

    @Override
    public Optional<User> findOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = connection.prepareStatement("select * from users where id = ?");

            statement.setInt(1,Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username_user = resultSet.getString("username");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User u = new User(firstName,lastName,username_user,email,password);
                u.setId(id);
                u.setFriend_list(findAllFriends(id));
                return Optional.of(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();

        try(Connection connection = DriverManager.getConnection(url, username, password)){


            PreparedStatement statement = connection.prepareStatement("select * from users order by first_name");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username_user = resultSet.getString("username");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User u = new User(firstName,lastName,username_user,email,password);
                u.setId(id);
                u.setFriend_list(findAllFriends(id));
                users.add(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    @Override
    public Optional<User> save(User entity) {
        if (entity == null){
            throw new RepositoryException("Entity cannot be null!");
        }
        validator.validate(entity);
        String insertSQL = "insert into users(first_name,last_name,username,email, password) values (?,?,?,?,?)";
        try(Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setString(1,entity.getFirst_name());
            statement.setString(2,entity.getLast_name());
            statement.setString(3,entity.getUsername());
            statement.setString(4,entity.getEmail());
            statement.setString(5,entity.getPassword());
            int answer = statement.executeUpdate();
            return answer==0 ? Optional.empty(): Optional.of(entity);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> delete(Long id) {
        if (id == null) {
            throw new RepositoryException("id must not be null");
        }
        String deleteSQL = "delete from users where id=?";

        try(Connection connection2 = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection2.prepareStatement(deleteSQL);
            statement.setLong(1,id);
            Optional<User> foundUser = findOne(id);
            int answer=0;
            if(foundUser.isPresent()){
                answer = statement.executeUpdate();
            }
            return answer == 0 ? foundUser : Optional.empty();

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(User entity) {
        if(entity == null){
            throw new IllegalArgumentException("Entity cannot be null!");
        }
        validator.validate(entity);
        String updateSQL = "update users set first_name=?,last_name=?,username=?,email=?,password=? where id=?";
        try(Connection connection2 = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = connection2.prepareStatement(updateSQL);
            statement.setString(1,entity.getFirst_name());
            statement.setString(2,entity.getLast_name());
            statement.setString(3,entity.getUsername());
            statement.setString(4,entity.getEmail());
            statement.setString(5,entity.getPassword());
            statement.setLong(6,entity.getId());
            int answer = statement.executeUpdate();
            return answer == 0 ? Optional.empty() : Optional.of(entity);

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

