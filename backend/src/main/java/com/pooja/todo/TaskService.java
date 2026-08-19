package com.pooja.todo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskService {
    private final List<Task> tasks = new ArrayList<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public synchronized List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public synchronized Task addTask(String title, String description) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }
        Task task = new Task(nextId.getAndIncrement(), title.trim(),
                description == null ? "" : description.trim(), false);
        tasks.add(task);
        return task;
    }

    public synchronized Task updateTask(int id, String title, String description, boolean completed) {
        Task task = findById(id);
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }
        task.setTitle(title.trim());
        task.setDescription(description == null ? "" : description.trim());
        task.setCompleted(completed);
        return task;
    }

    public synchronized Task toggleTask(int id) {
        Task task = findById(id);
        task.setCompleted(!task.isCompleted());
        return task;
    }

    public synchronized void deleteTask(int id) {
        tasks.remove(findById(id));
    }

    private Task findById(int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }
}
