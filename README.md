
# Hospital Management System — Microservices Architecture

A comprehensive Hospital Management System built using **Spring Boot Microservices**. The system covers patient registration, appointment scheduling, doctor management, prescriptions, pharmacy & medicine dispensing, lab tests, billing & invoicing, payments, mediclaim processing, and real-time notifications.

---

## Architecture Overview

All microservices register with a **Service Registry (Eureka)** and are accessed through a central **API Gateway**. Inter-service communication is handled via **OpenFeign** clients with **Resilience4j Circuit Breakers** for fault tolerance.

```
                        ┌──────────────┐
                        │  API Gateway │  :8090
                        └──────┬───────┘
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                     │
   ┌──────┴──────┐   ┌────────┴────────┐   ┌───────┴────────┐
   │ User Service│   │ Patient Service │   │  Prescription  │
   │    :8081    │   │     :8086       │   │   Service :8083│
   └─────────────┘   └────────┬────────┘   └───────┬────────┘
                              │                     │
          ┌───────────────────┼─────────────────────┤
          │                   │                     │
   ┌──────┴──────┐   ┌───────┴───────┐   ┌────────┴────────┐
   │   Billing   │   │   Pharmacy    │   │   Lab Service   │
   │ Service:8091│   │ Service :8084 │   │     :8087       │
   └─────────────┘   └───────────────┘   └─────────────────┘
          │
   ┌──────┴──────────┐
   │  Notification    │
   │  Service :8092   │
   └──────────────────┘

   Service Registry (Eureka) :8761
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Service Discovery | Spring Cloud Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Inter-Service Communication | OpenFeign |
| Resilience | Resilience4j (Circuit Breaker) |
| Database | MySQL (per-service DB) |
| ORM | Spring Data JPA / Hibernate |
| Security | JWT Authentication |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| Build Tool | Maven |

---

## Microservices

| # | Service | Port | Description |
|---|---------|------|-------------|
| 1 | **service-registry** | `8761` | Eureka Server for service discovery and registration |
| 2 | **api-gateway** | `8090` | Central entry point; routes requests, attaches JWT-decoded headers (`X-User-Id`, `X-User-Role`) |
| 3 | **user-service** | `8081` | User registration, authentication (JWT), login, logout, token refresh, and user CRUD |
| 4 | **patient-service** | `8086` | Patient records, doctor slot management, and appointment scheduling/completion |
| 5 | **prescription-service** | `8083` | Doctor profile management and prescription creation for appointments |
| 6 | **pharmacy-service** | `8084` | Medicine inventory, dispense request workflow, and medicine dispensing |
| 7 | **lab-service** | `8087` | Lab test creation, sample collection, test execution, and result uploading |
| 8 | **billing-service** | `8091` | Invoice generation, medicine/lab fee updates, payments, and mediclaim processing |
| 9 | **notification-service** | `8092` | Stores and serves in-app notifications triggered by other services |

---

## Roles

| Role | Description |
|------|-------------|
| `ADMIN` | Full access to all services and endpoints |
| `USER` | Patient-facing role — can view own data, book appointments, make payments |
| `DOCTOR` | Can create prescriptions, dispense requests, lab test orders, manage profile & slots |
| `RECEPTIONIST` | Can manage patients, appointments, slots, invoices, and payments |
| `PHARMACIST` | Can manage medicine inventory and dispense medicines |
| `LAB_TECHNICIAN` | Can manage lab tests — collect samples, run tests, upload results |

---

## API Endpoints Reference

> **Base URL via Gateway:** `http://localhost:8090`
>
> All protected endpoints require a valid JWT token in the `Authorization` header. The API Gateway decodes the JWT and forwards `X-User-Id` and `X-User-Role` headers to downstream services.

---

### 1. User Service (`/auth`, `/users`)

#### Authentication (Public)

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| `POST` | `/auth/register` | Register a new patient account | Public |
| `POST` | `/auth/login` | Login and receive JWT tokens | Public |
| `POST` | `/auth/refresh` | Refresh access token | Public |
| `POST` | `/auth/logout` | Logout (invalidate refresh token) | Public |

#### User Management

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| `POST` | `/users/create` | Create a new hospital staff user | ADMIN |
| `GET` | `/users/id/{id}` | Get user by ID | ADMIN |
| `GET` | `/users/username/{username}` | Get user by username | ADMIN |
| `GET` | `/users/all` | Get all users | ADMIN |
| `DELETE` | `/users/delete/{id}` | Delete a user | ADMIN |

---

### 2. Patient Service (`/patient`, `/appointment`, `/doctor-slot`)

#### Patient Management

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| `POST` | `/patient/create` | Create a new patient record | ADMIN, RECEPTIONIST, USER |
| `PUT` | `/patient/update/{id}` | Update patient record | ADMIN, RECEPTIONIST, USER |
| `GET` | `/patient/` | Get all patients | ADMIN, RECEPTIONIST, USER |
| `GET` | `/patient/id/{id}` | Get patient by ID | ADMIN, RECEPTIONIST, USER |
| `GET` | `/patient/mrn/{mrn}` | Get patient by MRN | ADMIN, RECEPTIONIST, USER |
| `DELETE` | `/patient/delete/{id}` | Delete patient record | ADMIN, RECEPTIONIST, USER |

#### Appointment Management

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| `POST` | `/appointment/create` | Schedule a new appointment (auto-assigns from slot) | ADMIN, RECEPTIONIST, USER |
| `PUT` | `/appointment/update/{id}` | Update appointment details | ADMIN, RECEPTIONIST, USER |
| `GET` | `/appointment/id/{id}` | Get appointment by ID | ADMIN, RECEPTIONIST, USER |
| `GET` | `/appointment/patient/{patientId}` | Get appointment by patient ID | ADMIN, RECEPTIONIST, USER |
| `GET` | `/appointment/doctor/{doctorId}` | Get appointment by doctor ID | ADMIN, RECEPTIONIST, USER |
| `GET` | `/appointment/status?status=` | Get appointment by status | ADMIN, RECEPTIONIST, USER |
| `GET` | `/appointment/all` | Get all appointments | ADMIN, RECEPTIONIST, USER |
| `DELETE` | `/appointment/delete/{id}` | Delete/cancel appointment (frees slot) | ADMIN, RECEPTIONIST, USER |
| `PUT` | `/appointment/complete/{id}` | Mark appointment as completed (triggers invoice) | ADMIN, DOCTOR |

#### Doctor Slot Management

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| `POST` | `/doctor-slot/create` | Create a single doctor slot | ADMIN, DOCTOR, RECEPTIONIST |
| `POST` | `/doctor-slot/create-many` | Bulk-create doctor slots | ADMIN, DOCTOR, RECEPTIONIST |
| `PUT` | `/doctor-slot/update/{id}` | Update a doctor slot | ADMIN, DOCTOR, RECEPTIONIST |
| `GET` | `/doctor-slot/{id}` | Get slot by ID | ADMIN, DOCTOR, RECEPTIONIST, USER |
| `GET` | `/doctor-slot/doctor/{doctorId}` | Get all slots for a doctor | ADMIN, DOCTOR, RECEPTIONIST, USER |
| `GET` | `/doctor-slot/` | Get all slots | ADMIN, RECEPTIONIST |
| `DELETE` | `/doctor-slot/delete/{id}` | Delete a doctor slot | ADMIN, DOCTOR, RECEPTIONIST |

---

### 3. Prescription Service (`/doctors`, `/prescriptions`)

#### Doctor Profile

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| `POST` | `/doctors/profile/create` | Create doctor profile | ADMIN, DOCTOR |
| `GET` | `/doctors/profile/{userId}` | Get doctor profile by user ID | ADMIN, RECEPTIONIST, USER |
| `PUT` | `/doctors/profile/update/{userId}` | Update doctor profile | ADMIN, DOCTOR |
| `GET` | `/doctors/check/{doctorId}` | Get doctor by ID (internal) | ADMIN, DOCTOR, RECEPTIONIST, USER |

#### Prescriptions

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| `POST` | `/prescriptions/create` | Create a prescription | ADMIN, DOCTOR |
| `GET` | `/prescriptions/{id}` | Get prescription by ID | Any authenticated |
| `GET` | `/prescriptions/appointment/{appointmentId}` | Get prescription by appointment | ADMIN, DOCTOR, RECEPTIONIST, USER |

---

### 4. Pharmacy Service (`/medicines`, `/dispense`)

#### Medicine Management

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| `GET` | `/medicines` | Get all medicines | ADMIN, DOCTOR, PHARMACIST |
| `POST` | `/medicines/create` | Add a new medicine | ADMIN, PHARMACIST |
| `GET` | `/medicines/search?name=` | Search medicines by name | ADMIN, DOCTOR, PHARMACIST |
| `GET` | `/medicines/{id}` | Get medicine by ID | ADMIN, DOCTOR, PHARMACIST |
| `PUT` | `/medicines/update/{id}` | Update medicine details | ADMIN, PHARMACIST |
| `GET` | `/medicines/appointment/{appointmentId}` | Get dispensed medicines for appointment | ADMIN, DOCTOR, RECEPTIONIST, PHARMACIST, USER |

#### Dispense Workflow

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| `POST` | `/dispense` | Create a dispense request | ADMIN, DOCTOR |
| `GET` | `/dispense/pending` | Get pending dispense requests | ADMIN, PHARMACIST |
| `PUT` | `/dispense/{id}` | Dispense medicine (updates invoice medicine fee) | ADMIN, PHARMACIST |

---

### 5. Lab Service (`/lab-tests`)

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| `POST` | `/lab-tests/create` | Create lab test(s) (updates invoice lab fee) | ADMIN, DOCTOR |
| `GET` | `/lab-tests/pending` | Get pending lab tests | ADMIN, LAB_TECHNICIAN |
| `PUT` | `/lab-tests/{id}/collect` | Collect sample for a lab test | ADMIN, LAB_TECHNICIAN |
| `PUT` | `/lab-tests/{id}/start?assignedTo=` | Start a lab test | ADMIN, LAB_TECHNICIAN |
| `POST` | `/lab-tests/{id}/result` | Upload lab test result | ADMIN, LAB_TECHNICIAN |
| `GET` | `/lab-tests/{id}/results` | Get result for a lab test | ADMIN, DOCTOR, RECEPTIONIST |
| `GET` | `/lab-tests/patient/{patientId}/results` | Get all results for a patient | ADMIN, DOCTOR, USER |
| `GET` | `/lab-tests/appointment/tests/{appointmentId}` | Get lab tests by appointment ID | ADMIN, DOCTOR, RECEPTIONIST, LAB_TECHNICIAN, USER |

---

### 6. Billing Service (`/invoice`, `/payment`, `/mediclaim`)

#### Invoice Management

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| `POST` | `/invoice/generate/{patientId}/{appointmentId}` | Generate invoice (auto-triggered on appointment completion) | Internal / Any |
| `PUT` | `/invoice/update/medicine-fee/{appointmentId}` | Update medicine fee on invoice | ADMIN, PHARMACIST |
| `PUT` | `/invoice/update/lab-fee/{appointmentId}` | Update lab fee on invoice | ADMIN, LAB_TECHNICIAN |
| `GET` | `/invoice/id/{invoiceId}` | Get invoice by ID (with full details) | ADMIN, RECEPTIONIST, USER |
| `GET` | `/invoice/all-invoices` | Get all invoices | ADMIN, RECEPTIONIST |
| `DELETE` | `/invoice/delete/{invoiceId}` | Delete an invoice | ADMIN |
| `POST` | `/invoice/payment/{invoiceId}` | Create payment record for invoice | ADMIN, RECEPTIONIST |

#### Payment Management

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| `POST` | `/payment/initiate/{invoiceId}` | Initiate a payment for an invoice | ADMIN, RECEPTIONIST, LAB_TECHNICIAN, PHARMACIST |
| `PUT` | `/payment/update` | Update payment details | ADMIN, RECEPTIONIST, LAB_TECHNICIAN, PHARMACIST |
| `PUT` | `/payment/complete/{paymentId}?paymentMethod=` | Confirm/complete a payment | ADMIN, RECEPTIONIST, USER |
| `PUT` | `/payment/cancel/{paymentId}` | Cancel a payment | ADMIN, RECEPTIONIST, USER |
| `GET` | `/payment/id/{id}` | Get payment by ID | ADMIN, RECEPTIONIST |
| `GET` | `/payment/all-payments` | Get all payments | ADMIN, RECEPTIONIST |
| `GET` | `/payment/patient/{patientId}` | Get payments by patient ID | ADMIN, RECEPTIONIST, USER |
| `GET` | `/payment/invoice/{invoiceId}` | Get payment by invoice ID | ADMIN, RECEPTIONIST, USER |
| `GET` | `/payment/status?status=` | Get payments by status | ADMIN, RECEPTIONIST |

#### Mediclaim Management

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| `POST` | `/mediclaim/process` | Apply for a mediclaim | ADMIN, USER |
| `PUT` | `/mediclaim/update/{id}?status=` | Update mediclaim status (approve/reject) | ADMIN, USER |
| `GET` | `/mediclaim/id/{id}` | Get mediclaim by ID | ADMIN, RECEPTIONIST, USER |
| `GET` | `/mediclaim/patient/{patientId}` | Get all mediclaims for a patient | ADMIN, USER |
| `GET` | `/mediclaim/all` | Get all mediclaims | ADMIN, RECEPTIONIST |
| `GET` | `/mediclaim/status/{status}` | Get mediclaims by status | ADMIN, RECEPTIONIST |

---

### 7. Notification Service (`/notifications`)

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| `POST` | `/notifications/send` | Send a notification (internal) | Internal |
| `GET` | `/notifications/{userId}/allMessages` | Get all notifications for a user | Any authenticated |
| `GET` | `/notifications/{userId}/unread` | Get unread notifications | Any authenticated |
| `PUT` | `/notifications/{notificationId}/read` | Mark notification as read | Any authenticated |
| `PUT` | `/notifications/{userId}/read-all` | Mark all notifications as read | Any authenticated |

---

## Key Business Flows

### Appointment → Invoice → Payment Flow

```
1. Doctor creates slots          → POST /doctor-slot/create
2. Patient books appointment     → POST /appointment/create (slot marked as booked)
3. Doctor completes appointment  → PUT  /appointment/complete/{id}
       ↓ triggers
4. Invoice auto-generated        → POST /invoice/generate/{patientId}/{appointmentId}
5. Doctor creates prescription   → POST /prescriptions/create
6. Doctor orders lab tests       → POST /lab-tests/create        → updates invoice lab fee
7. Doctor creates dispense req   → POST /dispense                  
8. Pharmacist dispenses          → PUT  /dispense/{id}           → updates invoice medicine fee
9. Invoice becomes READY when all fees are updated
10. Payment initiated & confirmed → POST /payment/initiate → PUT /payment/complete
11. Mediclaim applied (optional)  → POST /mediclaim/process
```

---

## Getting Started

### Prerequisites
- **Java 21**
- **Maven 3.8+**
- **MySQL 8.0+**

### Database Setup
Create the following databases in MySQL:

```sql
CREATE DATABASE hospital_user_db;
CREATE DATABASE hospital_patient_db;
CREATE DATABASE hospital_prescription_db;
CREATE DATABASE hospital_pharmacy_db;
CREATE DATABASE hospital_lab_db;
CREATE DATABASE hospital_billing_db;
CREATE DATABASE hospital_notification_db;
```

### Startup Order

```bash
1. service-registry       (port 8761)
2. api-gateway            (port 8090)
3. user-service           (port 8081)
4. notification-service   (port 8092)
5. patient-service        (port 8086)
6. prescription-service   (port 8083)
7. pharmacy-service       (port 8084)
8. lab-service            (port 8087)
9. billing-service        (port 8091)
```

Start each service:
```bash
cd <service-directory>
./mvnw spring-boot:run
```

### Swagger UI

Each service exposes Swagger UI at:
```
http://localhost:<port>/swagger-ui/index.html
```

### Eureka Dashboard
```
http://localhost:8761
```

---

## Team 6

**Project:** INTISG26JFAGN001 — Hospital Management System (Microservices)

