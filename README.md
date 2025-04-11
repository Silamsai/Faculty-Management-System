# 🎓 Faculty Management System

A mobile + web application to simplify faculty leave permission requests and streamline approval by administrators. Built with **Android (Kotlin)**, **Spring Boot**, and **MySQL**.

---

## 🚀 Features

### 👩‍🏫 Faculty Module (Android App)
- Login with credentials
- Submit permission requests (Casual, Sick, Emergency, Other)
- View past requests and status (Approved / Disapproved / Pending)
- View welcome message: `Hello, [Name]`

### 👨‍💼 Admin Module (Android App)
- Login as admin
- View pending leave requests
- Approve or disapprove requests
- Update status in the backend
- Export leave history (Excel/PDF - Coming Soon)

### 🌐 Web Platform (Coming Soon)
- Web-based request submission and management
- User roles (Faculty/Admin)
- Dashboard with request statistics

---

## 🛠️ Tech Stack

| Layer       | Technology         |
|-------------|--------------------|
| Frontend    | Kotlin (Android Studio) |
| Backend     | Spring Boot (Java) |
| Database    | MySQL              |
| API Comm    | Retrofit           |
| Auth        | Role-based system  |

---

## 📂 Project Structure



---

## 🔧 Setup Instructions

### 🔹 Android App (Frontend)

1. Open Android Studio
2. Select `FacultyPermission` folder
3. Let Gradle sync
4. Run the app on emulator or physical device

> Ensure internet permission is enabled in the `AndroidManifest.xml` for API calls.

---

### 🔹 Spring Boot (Backend)

1. Open IntelliJ IDEA (or preferred IDE)
2. Open backend project directory
3. Set up your `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/faculty_permission
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

**SEELAM SAI MANIKANTA REDDY & BARRE SANJAY** – Android & Backend Developer



Feel free to open issues or pull requests.
We’d love your suggestions to improve this system! 🤝

