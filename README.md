# Task Manager API

A Spring Boot REST API for managing personal tasks, with an AI-powered suggestion endpoint backed by Claude.

## Requirements

- Java 17+
- Internet access (for the AI endpoint)

## Running the project

```bash
.\gradlew bootRun
```

The API will start at `http://localhost:8080`.
The UI is available at `http://localhost:8080/index.html`.

## AI-Powered Endpoint

### `POST /tasks/suggest`

Accepts a structured request with a task title and optional description. Claude analyzes the input and returns a suggested priority, due date, status, and a brief explanation.

**Request:**
```json
{
  "title": "Submit quarterly report",
  "description": "Compile Q3 numbers and send to manager before end of week"
}
```

**Response:**
```json
{
  "suggestedPriority": "HIGH",
  "suggestedDueDate": "2026-05-14",
  "suggestedStatus": "TODO",
  "explanation": "The phrase 'before end of week' signals urgency, suggesting HIGH priority with a due date of Friday."
}
```

**To enable the AI endpoint**, set your Anthropic API key as an environment variable before running:

```bash
# Windows PowerShell
$env:ANTHROPIC_API_KEY="your-key-here"
.\gradlew bootRun
```

Without a key, the endpoint will return a graceful fallback response with default values. All other endpoints work without a key.

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/tasks` | Create a task |
| `GET` | `/tasks` | List all tasks |
| `GET` | `/tasks/{id}` | Get a task by ID |
| `PUT` | `/tasks/{id}` | Update a task |
| `DELETE` | `/tasks/{id}` | Delete a task |
| `POST` | `/tasks/suggest` | Get AI suggestions for a task |

## Running tests

```bash
.\gradlew test
```

## Notes

- Uses an H2 in-memory database — data resets on restart
- No authentication required
- The H2 console is available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:taskdb`)