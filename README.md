# GitHub Organization Access Report Service

## 📌 Overview

This project is a Spring Boot application that integrates with the GitHub API to generate a report of users and the repositories they have access to within a given organization.

It fetches repositories from a GitHub organization and maps users to the repositories they are associated with.

---

## 🚀 Features

* Fetch all repositories of a GitHub organization
* Retrieve users associated with each repository
* Generate aggregated user-to-repository mapping
* Handle pagination for large datasets (100+ repos, 1000+ users)
* Expose REST API to fetch the report in JSON format
* Proper error handling and clean code structure

---

## ⚠️ Important Note

Due to GitHub API restrictions, the **contributors API** is used instead of the collaborators API for public repositories.

---

## 🛠️ Tech Stack

* Java 17
* Spring Boot
* Spring WebFlux (WebClient)
* Maven
* GitHub REST API

---

## 🔐 Authentication

This application uses a GitHub Personal Access Token (PAT) for authentication.

### Set environment variable:

#### Windows (PowerShell):

```
$env:GITHUB_PAT="your_token_here"
```

#### Alternative (for testing only):

You can temporarily hardcode the token in the code.

---

## ▶️ How to Run

1. Clone the repository:

```
git clone https://github.com/your-username/github-access-report.git
```

2. Navigate to the project folder:

```
cd github-access-report
```

3. Run the application:

```
mvn spring-boot:run
```

4. Access the API:

```
http://localhost:8084/api/github/report?org=octokit
```

> If port 8080 is in use, configure a different port in `application.properties`.

---

## 📡 API Endpoint

### GET /api/github/report

#### Query Parameter:

* `org` → GitHub organization name

#### Example:

```
http://localhost:8084/api/github/report?org=octokit
```

---

## 📊 Sample Response

```json
{
  "organization": "octokit",
  "generatedAt": "2026-04-09T20:31:19Z",
  "totalUsers": 1145,
  "totalRepositories": 71,
  "users": [
    {
      "username": "user1",
      "repositories": [
        {
          "repository": "repo1",
          "permission": "unknown"
        }
      ]
    }
  ]
}
```

---

## ⚙️ Design Decisions

* Used **WebClient** for non-blocking API calls
* Implemented pagination handling using GitHub Link headers
* Aggregated data efficiently to support large-scale organizations
* Modular and clean code structure for maintainability

---

## ❗ Assumptions

* Public repositories are considered
* Contributors are treated as users with access
* Permissions may show as "unknown" due to API limitations

---

## 📁 Project Structure

```
src/main/java/com/assignment/githubreport
 ├── client
 ├── config
 ├── controller
 ├── service
 ├── model
 └── exception
```

---

## ✅ Status

✔ Fully working
✔ API tested successfully
✔ Handles large data
✔ Ready for submission

---

## 🚀 Future Improvements

* Add caching for performance
* Add rate-limit handling
* Improve permission mapping
* Add unit & integration tests

---
