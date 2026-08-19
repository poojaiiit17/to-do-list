package com.pooja.todo;

import java.sql.SQLException;
import java.util.List;

public class TaskService {
    private final TaskDAO taskDAO = new TaskDAO();

    public List<Task> getAllTasks() throws SQLException {
        return taskDAO.getAllTasks();
    }

    public Task addTask(String title, String description) throws SQLException {
        validateTitle(title);
        return taskDAO.addTask(title.trim(), description == null ? "" : description.trim());
    }

    public Task updateTask(int id, String title, String description, boolean completed) throws SQLException {
        validateTitle(title);
        return taskDAO.updateTask(id, title.trim(), description == null ? "" : description.trim(), completed);
    }

    public Task toggleTask(int id) throws SQLException {
        return taskDAO.toggleTask(id);
    }

    public void deleteTask(int id) throws SQLException {
        taskDAO.deleteTask(id);
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }
    }
}
