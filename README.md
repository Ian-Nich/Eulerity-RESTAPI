# Task Manager API

A Spring Boot REST API for managing personal tasks, with an AI-powered suggestion endpoint backed by Claude.

---

## Prerequisites

You only need two things installed:

- **Java 17 or higher** — [Download from Adoptium](https://adoptium.net) if you don't have it
  - To check: `java -version`
- **Internet access** — Gradle will download all dependencies automatically on first run

No Maven, no database, no Docker, nothing else.

---

## Quickstart

### 1. Clone the repo

```bash
git clone https://github.com/iann2/RESTAPI.git
cd RESTAPI
```

### 2. Run the project

**Windows:**
```bash
.\gradlew bootRun
```

**Mac/Linux:**
```bash
./gradlew bootRun
```

Gradle will download all dependencies automatically. First run may take 1–2 minutes.

### 3. Open the UI

Once you see `Started TaskManagerApplication` in the terminal, open:

```
http://localhost:8080
```

That should be it. The app is running.

---

## Using the AI Endpoint (optional)

The AI suggestion endpoint requires a Claude API key from Anthropic.

**To get a key:** https://console.anthropic.com

**To use it**, set the key as an environment variable before running:

**Windows PowerShell:**
```powershell
$env:ANTHROPIC_API_KEY="your-key-here"
.\gradlew bootRun
```

**Mac/Linux:**
```bash
ANTHROPIC_API_KEY=your-key-here ./gradlew bootRun
```

> Without a key the app still runs normally. The AI endpoint will return a fallback
> response with default values instead of a real suggestion.

---

## What the app does

The UI at `http://localhost:8080` lets you:

- View all tasks in a live table
- Create a task with a title, description, due date, priority, and status
- Submit a task title and description to the AI suggestion tool, which returns a recommended priority, due date, and explanation

---

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/tasks` | Create a task |
| `GET` | `/tasks` | List all tasks |
| `GET` | `/tasks/{id}` | Get a task by ID |
| `PUT` | `/tasks/{id}` | Update a task |
| `DELETE` | `/tasks/{id}` | Delete a task |
| `POST` | `/tasks/suggest` | Get AI suggestions for a task |

### AI endpoint — example

**Request:**
```bash
curl -X POST http://localhost:8080/tasks/suggest \
  -H "Content-Type: application/json" \
  -d '{"title": "Submit quarterly report", "description": "Compile Q3 numbers and send to manager"}'
```

**Response:**
```json
{
  "suggestedPriority": "HIGH",
  "suggestedDueDate": "2026-05-14",
  "suggestedStatus": "TODO",
  "explanation": "The word 'submit' and a business deliverable context suggest HIGH priority. No explicit deadline was given, so one week from today is recommended."
}
```

---

## Running tests

```bash
.\gradlew test         # Windows
./gradlew test         # Mac/Linux
```

All tests pass without an API key. The AI call is mocked in the test suite.

---

## Notes

- Data is stored in an H2 in-memory database and resets when the server stops
- The H2 console is available at `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:taskdb`, username: `sa`, no password
- No authentication is required for any endpoint

```
