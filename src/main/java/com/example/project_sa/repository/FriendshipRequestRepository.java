package com.example.project_sa.repository;

import com.example.project_sa.domain.FriendshipRequest;
import com.example.project_sa.domain.Tuple;
import com.example.project_sa.domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipRequestRepository implements Repository<Tuple<Long,Long>, FriendshipRequest> {
    private String url;
    private String username;
    private String password;

    public FriendshipRequestRepository(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }
    public ArrayList<User> findAllFriends(Long id){

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

    private User findUser(Long id){
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
                return u;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    @Override
    public Optional<FriendshipRequest> findOne(Tuple<Long, Long> id) {
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = connection.prepareStatement("select * from friendship_requests where (from_id,to_id) = (?,?)");

            statement.setInt(1,Math.toIntExact(id.getLeft()));
            statement.setInt(2,Math.toIntExact(id.getRight()));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String status =resultSet.getString("status");
                User u1 = findUser(id.getLeft());
                if(u1!=null)
                    u1.setFriend_list(findAllFriends(id.getLeft()));
                User u2 = findUser(id.getRight());
                if(u2!=null)
                    u2.setFriend_list(findAllFriends(id.getRight()));
                FriendshipRequest fr = new FriendshipRequest(u1,u2);
                fr.setStatus(status);
                fr.setId(id);
                return Optional.of(fr);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<FriendshipRequest> findAll() {
        Set<FriendshipRequest> friendshipRequests = new HashSet<>();
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = connection.prepareStatement("select * from friendship_requests");

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long from_id = resultSet.getLong("from_id");
                Long to_id = resultSet.getLong("to_id");
                String status =resultSet.getString("status");
                User u1 = findUser(from_id);
                User u2 = findUser(to_id);
                FriendshipRequest fr = new FriendshipRequest(u1,u2);
                fr.setStatus(status);
                fr.setId(new Tuple<>(from_id,to_id));
                friendshipRequests.add(fr);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendshipRequests;
    }

    @Override
    public Optional<FriendshipRequest> save(FriendshipRequest entity) {
        if (entity == null) {
            throw new RepositoryException("id must not be null");
        }
        String deleteSQL = "insert into friendship_requests(from_id,to_id) values (?,?)";

        try(Connection connection2 = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection2.prepareStatement(deleteSQL);
            statement.setLong(1,entity.getId().getLeft());
            statement.setLong(2,entity.getId().getRight());
            Optional<FriendshipRequest> fr = findOne(entity.getId());
            int answer=0;
            if(fr.isEmpty()){
                answer = statement.executeUpdate();
            }
            return answer == 0 ? Optional.empty() : fr;

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendshipRequest> delete(Tuple<Long, Long> id) {
        if (id == null) {
            throw new RepositoryException("id must not be null");
        }
        String deleteSQL = "delete from friendship_requests where (from_id,to_id)=(?,?)";

        try(Connection connection2 = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection2.prepareStatement(deleteSQL);
            statement.setLong(1,id.getLeft());
            statement.setLong(2,id.getRight());
            Optional<FriendshipRequest> fr = findOne(id);
            int answer=0;
            if(fr.isPresent()){
                answer = statement.executeUpdate();
            }
            return answer == 0 ? fr : Optional.empty();

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendshipRequest> update(FriendshipRequest entity) {
        if(entity == null){
            throw new IllegalArgumentException("Entity cannot be null!");
        }

        String updateSQL = "update friendship_requests set status=? where (from_id,to_id)=(?,?)";
        try(Connection connection2 = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = connection2.prepareStatement(updateSQL);
            statement.setString(1,entity.getStatus());
            statement.setLong(2,entity.getFrom().getId());
            statement.setLong(3,entity.getTo().getId());
            int answer = statement.executeUpdate();
            return answer == 0 ? Optional.empty() : Optional.of(entity);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
