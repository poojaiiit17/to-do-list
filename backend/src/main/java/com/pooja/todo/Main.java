package com.pooja.todo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Executors;

public class Main {
    private static final Gson GSON = new GsonBuilder().create();
    private static final TaskService SERVICE = new TaskService();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/tasks", Main::handleTasks);
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("To-Do backend running at http://localhost:8080");
    }

    private static void handleTasks(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        try {
            if ("OPTIONS".equalsIgnoreCase(method)) {
                send(exchange, 204, "");
                return;
            }

            if (parts.length == 3 && "GET".equalsIgnoreCase(method)) {
                sendJson(exchange, 200, SERVICE.getAllTasks());
                return;
            }

            if (parts.length == 3 && "POST".equalsIgnoreCase(method)) {
                TaskRequest request = GSON.fromJson(readBody(exchange), TaskRequest.class);
                sendJson(exchange, 201, SERVICE.addTask(request.title, request.description));
                return;
            }

            if (parts.length == 4) {
                int id = Integer.parseInt(parts[3]);

                if ("PUT".equalsIgnoreCase(method)) {
                    TaskRequest request = GSON.fromJson(readBody(exchange), TaskRequest.class);
                    sendJson(exchange, 200, SERVICE.updateTask(id, request.title, request.description, request.completed));
                    return;
                }

                if ("PATCH".equalsIgnoreCase(method)) {
                    sendJson(exchange, 200, SERVICE.toggleTask(id));
                    return;
                }

                if ("DELETE".equalsIgnoreCase(method)) {
                    SERVICE.deleteTask(id);
                    send(exchange, 204, "");
                    return;
                }
            }

            sendJson(exchange, 404, Map.of("error", "Endpoint not found"));
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            e.printStackTrace();
            sendJson(exchange, 500, Map.of("error", "Database error: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, Map.of("error", "Internal server error"));
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void sendJson(HttpExchange exchange, int status, Object data) throws IOException {
        send(exchange, status, GSON.toJson(data));
    }

    private static void send(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static class TaskRequest {
        String title;
        String description;
        boolean completed;
    }
}
