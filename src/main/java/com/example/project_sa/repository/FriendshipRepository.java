package com.example.project_sa.repository;

import com.example.project_sa.domain.Friendship;
import com.example.project_sa.domain.Tuple;
import com.example.project_sa.validators.Strategy;
import com.example.project_sa.validators.ValidatorFactory;
import com.example.project_sa.validators.ValidatorInterface;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipRepository implements Repository<Tuple<Long,Long>, Friendship> {
    private ValidatorInterface<Friendship> validator = new ValidatorFactory().createValidator(Strategy.friendship);

    String url;
    String username;
    String password;
    public FriendshipRepository(String url, String username, String password) throws SQLException, ClassNotFoundException {
        this.url = url;
        this.username = username;
        this.password = password;

    }
    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id) {
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = connection.prepareStatement("select * from friendships where (id_user1,id_user2) = (?,?)");

            statement.setInt(1,Math.toIntExact(id.getLeft()));
            statement.setInt(2,Math.toIntExact(id.getRight()));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Timestamp localDateTime =resultSet.getTimestamp("date_friendship");
                Friendship f = new Friendship(id.getLeft(),id.getRight());
                f.setFriendsFrom(localDateTime.toLocalDateTime());
                return Optional.ofNullable(f);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();

        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = connection.prepareStatement("select * from friendships");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long id_user1 = resultSet.getLong("id_user1");
                Long id_user2 = resultSet.getLong("id_user2");
                Timestamp localDateTime =resultSet.getTimestamp("date_friendship");
                Friendship f = new Friendship(id_user1,id_user2);
                f.setFriendsFrom(localDateTime.toLocalDateTime());
                friendships.add(f);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendships;
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        if (entity == null){
            throw new RepositoryException("Entity cannot be null!");
        }
        validator.validate(entity);
        String insertSQL = "insert into friendships(id_user1,id_user2) values (?,?) ON CONFLICT (id_user1, id_user2) DO NOTHING";
        try(Connection connection = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setLong(1,entity.getId().getLeft());
            statement.setLong(2,entity.getId().getRight());
            int answer = statement.executeUpdate();
            return answer==0 ? Optional.of(entity): Optional.empty();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id) {
        if (id == null) {
            throw new RepositoryException("id must not be null");
        }
        String deleteSQL = "delete from friendships where (id_user1,id_user2) = (?,?)";

        try(Connection connection2 = DriverManager.getConnection(url, username, password)){

            PreparedStatement statement = connection2.prepareStatement(deleteSQL);
            statement.setLong(1,id.getLeft());
            statement.setLong(2,id.getRight());
            Optional<Friendship> foundUser = findOne(id);
            int answer=0;
            answer = statement.executeUpdate();
            return answer == 0 ? Optional.empty() : foundUser;

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        if(entity == null){
            throw new RepositoryException("Entity cannot be null!");
        }
        validator.validate(entity);
        Optional<Friendship> foundFriendship = findOne(entity.getId());
        if(foundFriendship.isEmpty())
            throw new RepositoryException("The friendship you're trying to update does not exist!");
        String updateSQL = "update friendships set date_friendship=? where (id_user1,id_user2)=(?,?)?";
        try(Connection connection2 = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = connection2.prepareStatement(updateSQL);
            statement.setLong(2,entity.getId().getLeft());
            statement.setLong(3,entity.getId().getRight());
            statement.setTimestamp(1,Timestamp.valueOf(entity.getFriendsFrom()));
            int answer = statement.executeUpdate();
            return answer == 0 ? Optional.of(entity) : Optional.empty();

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
