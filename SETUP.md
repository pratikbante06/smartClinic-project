# Smart Clinic Management System — Setup Guide

## Prerequisites

| Tool | Version | Where to get |
|------|---------|-------------|
| Java JDK | 17+ | https://adoptium.net |
| XAMPP | Any recent | https://apachefriends.org |
| MongoDB Community | 6.x+ | https://mongodb.com/try/download |
| IntelliJ IDEA | Any | https://jetbrains.com/idea |

---

## Step 1 — Start XAMPP MySQL

1. Open XAMPP Control Panel → Start **MySQL**
2. Open phpMyAdmin (`http://localhost/phpmyadmin`)
3. The database `cms` will be created automatically on first run (via `createDatabaseIfNotExist=true`)

> **Default XAMPP MySQL**: username=`root`, password=`""` (empty)  
> The `application.properties` is already configured for this.  
> If you set a custom password in phpMyAdmin, update `spring.datasource.password=` accordingly.

---

## Step 2 — Start MongoDB

1. Open Command Prompt as Administrator
2. Run: `mongod --dbpath C:\data\db`
   (Create `C:\data\db` first if it doesn't exist)
3. MongoDB runs on `localhost:27017` by default

> **Don't have MongoDB?** In `application.properties`, uncomment this line to disable it:
> ```
> spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,...
> ```
> Note: Prescriptions feature will NOT work without MongoDB.

---

## Step 3 — Open in IntelliJ

1. Open IntelliJ → **File → Open** → select the `app/` folder (where `pom.xml` is)
2. IntelliJ will auto-detect it as a Maven project
3. Wait for Maven to download dependencies (first time takes a few minutes)

---

## Step 4 — Run the App

1. Open `BackEndApplication.java`
2. Click the ▶ green Run button OR right-click → **Run 'BackEndApplication'**
3. Wait for: `Started BackEndApplication in X seconds`

---

## Step 5 — Access the App

Open browser → `http://localhost:8080`

### Test Credentials

**Admin:**
- Username: `admin`
- Password: `admin123`

**Doctor (any of these):**
- Name: `Dr. Emily Adams` | Password: `password123`
- Name: `Dr. Mark Johnson` | Password: `password123`

**Patient (any of these):**
- Email: `john@example.com` | Password: `password123`
- Email: `alice@example.com` | Password: `password123`

---

## Stored Procedures (Optional)

Run `stored_procedures.sql` in phpMyAdmin → SQL tab to create 3 reporting procedures:
1. `GetDailyAppointmentReportByDoctor(date)` — appointments per doctor per day
2. `GetDoctorWithMostPatientsByMonth(month, year)` — busiest doctor per month  
3. `GetDoctorWithMostPatientsByYear(year)` — busiest doctor per year

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `Access denied for user 'root'@'localhost'` | Update `spring.datasource.password=` in `application.properties` |
| `MongoSocketOpenException` on startup | Start MongoDB (`mongod`) or disable it in `application.properties` |
| Port 8080 already in use | Stop other apps on 8080, or add `server.port=8081` to `application.properties` |
| `Table 'cms.admin' doesn't exist` | Hibernate creates tables on startup — let it fully start first |
| Doctor dashboard shows no appointments | Make sure you're logged in as a doctor whose name exactly matches the DB |
