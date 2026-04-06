# Schema Design - Smart Clinic Management System

## MySQL Database Design

### 1. `admin` Table
| Column | Data Type | Constraints |
|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| username | VARCHAR(50) | NOT NULL, UNIQUE |
| password | VARCHAR(255) | NOT NULL |

### 2. `doctor` Table
| Column | Data Type | Constraints |
|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(100) | NOT NULL |
| specialty | VARCHAR(100) | NOT NULL |
| email | VARCHAR(100) | NOT NULL, UNIQUE |
| password | VARCHAR(255) | NOT NULL |

### 3. `patient` Table
| Column | Data Type | Constraints |
|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(100) | NOT NULL |
| email | VARCHAR(100) | NOT NULL, UNIQUE |
| password | VARCHAR(255) | NOT NULL |
| phone | VARCHAR(15) | NOT NULL |
| address | VARCHAR(255) | NOT NULL |

### 4. `appointment` Table
| Column | Data Type | Constraints |
|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| doctor_id | BIGINT | FOREIGN KEY → doctor(id), NOT NULL |
| patient_id | BIGINT | FOREIGN KEY → patient(id), NOT NULL |
| appointment_time | DATETIME | NOT NULL |
| status | VARCHAR(20) | NOT NULL DEFAULT 'PENDING' |

### 5. `doctor_available_times` Table
| Column | Data Type | Constraints |
|---|---|---|
| doctor_id | BIGINT | FOREIGN KEY → doctor(id), NOT NULL |
| available_time | VARCHAR(50) | NOT NULL |

---

## MongoDB Collection Design

### Collection: `prescriptions`
```json
{
  "_id": "ObjectId('64f1a2b3c4d5e6f7a8b9c0d1')",
  "patientName": "John Doe",
  "patientId": 1,
  "doctorName": "Dr. Smith",
  "appointmentId": 5,
  "medication": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Twice daily",
      "duration": "5 days"
    },
    {
      "name": "Amoxicillin",
      "dosage": "250mg",
      "frequency": "Three times daily",
      "duration": "7 days"
    }
  ],
  "notes": "Take medication after meals. Rest well.",
  "createdAt": "2026-03-29T10:00:00Z"
}
```

### Design Justification
- **MySQL** is used for structured relational data (doctors, patients, appointments, admins) where relationships and constraints are important.
- **MongoDB** is used for prescriptions because they have flexible, nested structures (multiple medications per prescription) that don't fit neatly into relational tables.
