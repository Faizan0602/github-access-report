# GitHub Access Report Service

## 🚀 Project Overview

This project is a Java Spring Boot backend service that integrates with the GitHub REST API to generate an access report for an organization.

The service identifies which users have access to which repositories and returns a structured user-to-repositories mapping via a REST API.

This solution focuses on clean architecture, scalability, and efficient API usage.

---

## 🧠 How It Works

1. Fetch all repositories of the given GitHub organization
2. For each repository, fetch all collaborators and their permissions
3. Process and aggregate the data into a user → repositories mapping
4. Return a structured JSON response via REST API

---

## 🛠 Tech Stack

* Java 17
* Spring Boot 3
* Spring Web + WebClient
* Maven
* GitHub REST API

---

## 📁 Project Structure

src/main/java/com/assignment/githubreport
├── controller
├── service
├── client
├── dto
├── model
├── config
├── exception

---

## ⚙️ Setup Instructions

### Prerequisites

* Java 17
* Maven
* GitHub Personal Access Token (PAT)

### Configure GitHub Token

Set environment variable:

**Windows (PowerShell):**

```
$env:GITHUB_PAT="your_token"
```

**Mac/Linux:**

```
export GITHUB_PAT="your_token"
```

---

## ▶️ How to Run

```
mvn spring-boot:run
```

Server runs at:

```
http://localhost:8080
```

---

## 🔗 API Endpoint

```
GET /api/github/report?org={orgName}
```

### Example Request

```
http://localhost:8080/api/github/report?org=octo-org
```

---

## 📦 Sample Response

```json
{
  "organization": "octo-org",
  "users": [
    {
      "username": "alice",
      "repositories": [
        {
          "repository": "backend-service",
          "permission": "admin"
        }
      ]
    }
  ]
}
```

---

## ⚡ Scalability Considerations

* Parallel API calls using CompletableFuture
* Avoided sequential processing
* Pagination support for GitHub APIs
* Configurable parallelism

---

## ❗ Error Handling

* Invalid organization handling
* API failure handling
* Network error handling

---

## 🧩 Assumptions

* GitHub PAT has required permissions
* Organization is accessible
* GitHub API rate limits are manageable

---

## 📌 Design Decisions

* Layered architecture (Controller → Service → Client)
* DTO-based response structure
* Externalized configuration
* Clean and maintainable code structure

---

## 📎 Notes

* Designed for handling large-scale organizations (100+ repos, 1000+ users)
* Can be extended with caching or retry mechanisms for production use
