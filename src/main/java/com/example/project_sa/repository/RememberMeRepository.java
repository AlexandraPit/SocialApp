package com.example.project_sa.repository;

import com.example.project_sa.domain.RememberMe;

import java.sql.*;
import java.util.Optional;

public class RememberMeRepository {
    private static String url;
    private static String usernamee;
    private static String passwrd;

    public RememberMeRepository(String url, String username, String password) {
        this.url = url;
        this.usernamee = username;
        this.passwrd = password;
        //createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Connection connection = DriverManager.getConnection(url, usernamee, passwrd)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS remembered_credentials (email VARCHAR(255) PRIMARY KEY, password VARCHAR(255))";
            PreparedStatement statement = connection.prepareStatement(createTableSQL);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveCredentials(String email, String password, boolean checked) {
        try (Connection connection = DriverManager.getConnection(url, usernamee, passwrd)) {
            String insertSQL = "UPDATE remembered_credentials set email= '"+email+"', password='"+password+"', checked='"+checked+"'";
            Statement statement=connection.createStatement();
            statement.executeUpdate(insertSQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<RememberMe> getRememberedCredentials() {
        try (Connection connection = DriverManager.getConnection(url, usernamee, passwrd)) {
            String selectSQL = "SELECT * FROM remembered_credentials LIMIT 1";
            PreparedStatement statement = connection.prepareStatement(selectSQL);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String email = resultSet.getString("email");
                String savedPassword = resultSet.getString("password");
                Boolean checked = resultSet.getBoolean("checked");
                System.out.println(email);
                System.out.println(savedPassword);
                System.out.println(checked);
                return Optional.of(new RememberMe(email, savedPassword, checked));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public void clearRememberedCredentials() {
        try (Connection connection = DriverManager.getConnection(url, usernamee, passwrd)) {
            String deleteSQL = "DELETE FROM remembered_credentials";
            PreparedStatement statement = connection.prepareStatement(deleteSQL);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
