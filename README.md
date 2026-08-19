# To-Do List - Java + JavaScript + MySQL

A beginner-friendly full-stack To-Do List application.

## Technologies

- Frontend: HTML, CSS, JavaScript
- Backend: Java 17
- API: Java built-in `HttpServer`
- Database: MySQL
- Database access: JDBC
- Build tool: Maven
- JSON: Gson

## Architecture

```text
Browser
   |
   | HTTP / JSON
   v
Java HTTP Server :8080
   |
   v
TaskService
   |
   v
TaskDAO
   |
   | JDBC
   v
MySQL: todo_db.tasks
```

## Features

- Add task
- View tasks
- Edit task
- Delete task
- Mark task completed
- Undo completed task
- Filter all/pending/completed tasks
- Data persists in MySQL

## 1. Create the database

Open MySQL and run the complete file:

`database/todo_db.sql`

It creates the `todo_db` database and `tasks` table and inserts sample records.

## 2. Configure MySQL password

The backend uses these environment variables:

```text
TODO_DB_USER
TODO_DB_PASSWORD
```

If they are not set, the defaults are:

```text
user = root
password = root
```

For Ubuntu/Linux, for example:

```bash
export TODO_DB_USER=root
export TODO_DB_PASSWORD=your_mysql_password
```

Do not commit real passwords to GitHub.

## 3. Start the Java backend

```bash
cd backend
mvn clean compile
mvn exec:java
```

The server starts at `http://localhost:8080`.

## 4. Start the frontend

For best results, serve the frontend with a local server:

```bash
cd frontend
python3 -m http.server 5500
```

Then open `http://localhost:5500` in the browser.

## API endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/tasks` | Get all tasks |
| POST | `/api/tasks` | Add a task |
| PUT | `/api/tasks/{id}` | Update a task |
| PATCH | `/api/tasks/{id}` | Toggle completed status |
| DELETE | `/api/tasks/{id}` | Delete a task |

## Database table

Only one table is required for the current application: `tasks`.

Important columns:

- `task_id` - primary key
- `title` - task title
- `description` - task details
- `completed` - task status
- `created_at` - creation time
- `updated_at` - last update time

## Request flow example

When the user clicks **Add Task**:

```text
JavaScript
   -> POST /api/tasks
   -> Main.java
   -> TaskService
   -> TaskDAO
   -> MySQL INSERT
   -> TaskDAO reads generated task_id
   -> Java returns JSON
   -> JavaScript refreshes the task list
```
