package com.pooja.todo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public List<Task> getAllTasks() throws SQLException {
        String sql = "SELECT task_id, title, description, completed FROM tasks ORDER BY task_id DESC";
        List<Task> tasks = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tasks.add(mapTask(rs));
            }
        }
        return tasks;
    }

    public Task addTask(String title, String description) throws SQLException {
        String sql = "INSERT INTO tasks (title, description, completed) VALUES (?, ?, false)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return getTaskById(keys.getInt(1));
                }
            }
        }
        throw new SQLException("Task could not be created");
    }

    public Task getTaskById(int id) throws SQLException {
        String sql = "SELECT task_id, title, description, completed FROM tasks WHERE task_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTask(rs);
                }
            }
        }
        throw new IllegalArgumentException("Task not found");
    }

    public Task updateTask(int id, String title, String description, boolean completed) throws SQLException {
        String sql = "UPDATE tasks SET title = ?, description = ?, completed = ? WHERE task_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setBoolean(3, completed);
            ps.setInt(4, id);

            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Task not found");
            }
        }
        return getTaskById(id);
    }

    public Task toggleTask(int id) throws SQLException {
        String sql = "UPDATE tasks SET completed = NOT completed WHERE task_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Task not found");
            }
        }
        return getTaskById(id);
    }

    public void deleteTask(int id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE task_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Task not found");
            }
        }
    }

    private Task mapTask(ResultSet rs) throws SQLException {
        return new Task(
                rs.getInt("task_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getBoolean("completed")
        );
    }
}
