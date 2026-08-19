package com.pooja.todo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/todo_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = System.getenv().getOrDefault("TODO_DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("TODO_DB_PASSWORD", "root");

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
