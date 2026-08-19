# To-Do List Application

A beginner-friendly full-stack To-Do List project using **Java backend** and **HTML/CSS/JavaScript frontend**.

## Features

- Add a task
- View all tasks
- Edit a task
- Mark a task as completed
- Undo completed task
- Delete a task
- Filter All / Pending / Completed
- REST API communication using JavaScript `fetch()`
- Responsive frontend

## Project Structure

```text
to-do-list/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/pooja/todo/
│       ├── Main.java
│       ├── Task.java
│       └── TaskService.java
├── frontend/
│   ├── index.html
│   ├── style.css
│   └── script.js
└── README.md
```

## Requirements

- Java 17 or later
- Maven
- A web browser

## Run the backend

Open a terminal inside the `backend` folder:

```bash
cd backend
mvn clean compile
mvn exec:java
```

The backend starts at:

```text
http://localhost:8080
```

## Run the frontend

Open `frontend/index.html` in a browser after starting the backend.

For the best browser experience, you can also serve the frontend with a simple local server:

```bash
cd frontend
python3 -m http.server 5500
```

Then open:

```text
http://localhost:5500
```

## API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/tasks` | Get all tasks |
| POST | `/api/tasks` | Add a task |
| PUT | `/api/tasks/{id}` | Update a task |
| PATCH | `/api/tasks/{id}` | Toggle completed status |
| DELETE | `/api/tasks/{id}` | Delete a task |

## How it works

1. The user enters a task in the browser.
2. `script.js` sends the task to the Java backend using `fetch()`.
3. `Main.java` receives the HTTP request.
4. `TaskService` performs the task operation.
5. The Java backend returns JSON.
6. JavaScript receives the response and updates the page.

## Important note

This beginner version stores tasks in Java memory, so tasks are lost when the backend is stopped. A future version can add MySQL persistence using DAO classes.
