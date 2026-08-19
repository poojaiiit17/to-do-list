const API_URL = "http://localhost:8080/api/tasks";

let tasks = [];
let currentFilter = "all";

const titleInput = document.getElementById("title");
const descriptionInput = document.getElementById("description");
const addButton = document.getElementById("addButton");
const taskList = document.getElementById("taskList");
const taskCount = document.getElementById("taskCount");
const message = document.getElementById("message");

async function loadTasks() {
    try {
        clearMessage();
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error("Could not load tasks");
        tasks = await response.json();
        renderTasks();
    } catch (error) {
        showMessage("Backend is not running. Start the Java server first.");
    }
}

async function addTask() {
    const title = titleInput.value.trim();
    const description = descriptionInput.value.trim();

    if (!title) {
        showMessage("Please enter a task title.");
        titleInput.focus();
        return;
    }

    try {
        const response = await fetch(API_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ title, description })
        });
        if (!response.ok) throw new Error("Could not add task");
        titleInput.value = "";
        descriptionInput.value = "";
        await loadTasks();
    } catch (error) {
        showMessage("Unable to add task. Check the Java backend.");
    }
}

async function toggleTask(id) {
    await fetch(`${API_URL}/${id}`, { method: "PATCH" });
    await loadTasks();
}

async function deleteTask(id) {
    if (!confirm("Are you sure you want to delete this task?")) return;
    await fetch(`${API_URL}/${id}`, { method: "DELETE" });
    await loadTasks();
}

async function editTask(id) {
    const task = tasks.find(t => t.id === id);
    if (!task) return;

    const title = prompt("Enter new title:", task.title);
    if (title === null) return;
    const description = prompt("Enter new description:", task.description);
    if (description === null) return;

    if (!title.trim()) {
        showMessage("Task title cannot be empty.");
        return;
    }

    await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            title: title.trim(),
            description: description.trim(),
            completed: task.completed
        })
    });
    await loadTasks();
}

function renderTasks() {
    let visibleTasks = tasks;
    if (currentFilter === "pending") visibleTasks = tasks.filter(t => !t.completed);
    if (currentFilter === "completed") visibleTasks = tasks.filter(t => t.completed);

    taskCount.textContent = `${tasks.length} task${tasks.length === 1 ? "" : "s"}`;

    if (visibleTasks.length === 0) {
        taskList.innerHTML = `<div class="empty">No tasks found.</div>`;
        return;
    }

    taskList.innerHTML = visibleTasks.map(task => `
        <div class="task ${task.completed ? "completed" : ""}">
            <div class="task-content">
                <h3>${escapeHtml(task.title)}</h3>
                <p class="description">${escapeHtml(task.description || "No description")}</p>
            </div>
            <div class="actions">
                <button class="complete" onclick="toggleTask(${task.id})">
                    ${task.completed ? "Undo" : "Complete"}
                </button>
                <button class="edit" onclick="editTask(${task.id})">Edit</button>
                <button class="delete" onclick="deleteTask(${task.id})">Delete</button>
            </div>
        </div>
    `).join("");
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function showMessage(text) { message.textContent = text; }
function clearMessage() { message.textContent = ""; }

addButton.addEventListener("click", addTask);
titleInput.addEventListener("keydown", e => {
    if (e.key === "Enter") addTask();
});

document.querySelectorAll(".filter").forEach(button => {
    button.addEventListener("click", () => {
        document.querySelectorAll(".filter").forEach(b => b.classList.remove("active"));
        button.classList.add("active");
        currentFilter = button.dataset.filter;
        renderTasks();
    });
});

loadTasks();
