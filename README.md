# Smart Parking Management System (SPMS)

A cloud-native, microservice-based platform for real-time parking search,
reservation, vehicle tracking, and payment. Built with Spring Boot and
Spring Cloud, SPMS lets drivers find and reserve parking, lets space owners
manage their inventory dynamically, and gives administrators a live view
of system-wide usage — all through a set of independently deployable
services that discover and call each other automatically.

## Table of Contents

- [Background](#background)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [How to Run](#how-to-run)
- [Full API Reference](#full-api-reference)
- [End-to-End Test Flow](#end-to-end-test-flow)
- [Postman Collection](#postman-collection)
- [Build Notes by Part](#build-notes-by-part)
- [Resources](#resources)

## Background

Urban parking is a daily source of congestion, wasted fuel, and driver
frustration — problems that static, manual parking systems can't solve at
scale. SPMS addresses this with a decoupled, microservice architecture
where each concern (users, vehicles, parking spaces, payments, analytics,
notifications) is owned by its own service, independently scalable and
independently deployable, coordinated through service discovery rather
than hardcoded dependencies.

## Architecture

```
                        ┌─────────────────────┐
                        │   API Gateway (8080) │  <-- single entry point
                        └──────────┬───────────┘
                                   │ routes via Eureka
   ┌───────────┬─────────────┬────┴────────┬─────────────┬─────────────┐
   ▼           ▼             ▼             ▼             ▼             ▼
User Svc   Vehicle Svc   Parking Svc   Payment Svc   Analytics Svc  Notification Svc
 (8081)      (8082)        (8083)        (8084)         (8085)          (8086)
   │           │             │             │              │               │
   │  Vehicle→User (validates owner)       │              │               │
   │           │             │◄────────────┘ releases space after payment │
   │           │             │◄──────────────────────────────────────────┘ analytics reads spaces
   │           │             │                             │
   │           │        Payment→User/Vehicle/Parking (receipt aggregation)
   │           │             │
   │      Parking/Payment ───┴──────────────────────────────► Notification (best-effort)
   │
   └────────────────────┬──────────────────────────────────────────────────┘
                         ▼
              Eureka Server (8761)      Config Server (8888)
            (service discovery)       (centralized config)
```

Every inter-service call is resolved live through **Eureka service
discovery** (`http://user-service/...`, `http://parking-service/...`, etc.
— never a hardcoded host or port) via a `@LoadBalanced RestTemplate`.
Calls that shouldn't block a service's own result if a downstream
dependency is unavailable — notifications, receipts, analytics — are
implemented **best-effort**: they log a warning and degrade gracefully
rather than failing the primary request.

## Tech Stack

| Technology | Purpose |
|---|---|
| Spring Boot 3.2.5 | Core framework for every microservice |
| Spring Cloud Eureka | Service registry & discovery |
| Spring Cloud Config | Centralized configuration management |
| Spring Cloud Gateway | API Gateway / single entry point |
| Spring Data JPA + H2 | Persistence (User, Vehicle, Parking, Payment, Notification) |
| `@LoadBalanced RestTemplate` | Eureka-resolved inter-service HTTP calls |
| Java 17, Maven | Language & build tool |
| Postman | API testing |

## Project Structure

```
smart-parking-system/
├── pom.xml                    # parent/aggregator POM (9 modules)
├── eureka-server/             # Service registry (8761)
├── config-server/             # Centralized config (8888) + config-repo/
├── api-gateway/                # Single entry point (8080)
├── user-service/               # Users + booking history (8081)
├── vehicle-service/            # Vehicles + entry/exit logs (8082)
├── parking-service/            # Spaces + filters + dynamic pricing + reservation expiry (8083)
├── payment-service/            # Mock payments + itemized receipts (8084)
├── analytics-service/          # Live usage aggregation (8085)
├── notification-service/       # Event notification log (8086)
├── docs/
│   ├── screenshots/eureka_dashboard.png
│   └── FINAL_TESTING_CHECKLIST.md
└── postman_collection.json     # Full API collection, every service
```

## How to Run

Build every module from the root:
```bash
mvn clean install
```

Start each service **in this order**, each in its own terminal — later
services depend on earlier ones being registered with Eureka, and some
call each other directly on startup-adjacent actions:
```bash
cd eureka-server        && mvn spring-boot:run   # 1. service registry
cd config-server        && mvn spring-boot:run   # 2. centralized config
cd api-gateway          && mvn spring-boot:run   # 3. single entry point
cd user-service         && mvn spring-boot:run   # 4. users + bookings
cd vehicle-service      && mvn spring-boot:run   # 5. calls User Service
cd parking-service      && mvn spring-boot:run   # 6. calls Notification Service
cd payment-service      && mvn spring-boot:run   # 7. calls User/Vehicle/Parking/Notification
cd analytics-service    && mvn spring-boot:run   # 8. calls Parking/Payment
cd notification-service && mvn spring-boot:run   # 9. event log
```

Verify everything registered: **http://localhost:8761** should list all
nine services.

## Full API Reference

### User Service — `localhost:8081` (Gateway: `/api/users/...`)
Manages driver and parking-owner accounts, plus each user's booking history.

| Method | Path | Description |
|---|---|---|
| POST | `/users/register` | Register a new user or owner |
| GET | `/users` | List all users |
| GET | `/users/{id}` | Get a single user |
| PUT | `/users/{id}` | Update name / phone / password |
| POST | `/users/{userId}/bookings` | Add a booking history record |
| GET | `/users/{userId}/bookings` | Get a user's booking history |

### Vehicle Service — `localhost:8082` (Gateway: `/api/vehicles/...`)
Manages vehicle registration, ownership links, and entry/exit tracking.

| Method | Path | Description |
|---|---|---|
| POST | `/vehicles` | Register a vehicle, linked to a `userId` (validated live against User Service) |
| GET | `/vehicles` | List all vehicles |
| GET | `/vehicles/{id}` | Get a single vehicle |
| GET | `/vehicles/user/{userId}` | All vehicles owned by a user |
| PUT | `/vehicles/{id}` | Update model / color / type |
| POST | `/vehicles/{vehicleId}/entry` | Simulate the vehicle entering (starts a session) |
| POST | `/vehicles/{vehicleId}/exit` | Simulate the vehicle exiting (computes stay duration) |
| GET | `/vehicles/{vehicleId}/logs` | Full entry/exit history for a vehicle |

### Parking Service — `localhost:8083` (Gateway: `/api/spaces/...`)
Manages parking space inventory, availability, dynamic pricing, and
reservation lifecycle.

| Method | Path | Description |
|---|---|---|
| POST | `/spaces` | Create a parking space |
| GET | `/spaces` | List/search — `?location=&zone=&minPrice=&maxPrice=&status=`, any combination |
| GET | `/spaces/{id}` | Get a single space, including live `effectivePrice` |
| PUT | `/spaces/{id}` | Update location / zone / price |
| PUT | `/spaces/{id}/reserve` | Reserve an AVAILABLE space for a user/vehicle |
| PUT | `/spaces/{id}/release` | Release a RESERVED/OCCUPIED space back to AVAILABLE |
| POST | `/spaces/expire-check` | Manually trigger the reservation-expiry sweep (for testing) |

Every space response includes a live `effectivePrice`: surge pricing
(configurable multiplier, default ×1.5) applies once a zone crosses 80%
occupancy, and an optional peak-hour multiplier (default ×1.2) stacks on
top during configured time windows. A background scheduler also
auto-releases any reservation left unclaimed past a configurable expiry
window (default 15 minutes).

### Payment Service — `localhost:8084` (Gateway: `/api/payments/...`)
Simulates mock payment-gateway transactions and generates itemized
receipts.

| Method | Path | Description |
|---|---|---|
| POST | `/payments` | Process a mock payment (PENDING → SUCCESS/FAILED) |
| GET | `/payments` | List all payments |
| GET | `/payments/{id}` | Get a single payment |
| GET | `/payments/user/{userId}` | A user's payment history |
| GET | `/payments/{id}/receipt` | Full itemized receipt: user, vehicle, parking space, duration, amount |

Card numbers are validated (16 digits, 3–4 digit CVV, non-expired date)
but never stored in full — only a masked version is persisted. A
successful payment automatically releases the associated parking space
back to AVAILABLE.

### Analytics Service — `localhost:8085` (Gateway: `/api/analytics/...`)
Stateless aggregator giving a live usage summary across the system.

| Method | Path | Description |
|---|---|---|
| GET | `/analytics/usage` | Total completed bookings, most-used zone, system-wide occupancy rate |

### Notification Service — `localhost:8086` (Gateway: `/api/notifications/...`)
Simulates and logs event notifications — no real email/SMS provider is
involved; "sending" means logging and persisting the event.

| Method | Path | Description |
|---|---|---|
| POST | `/notifications` | Send (log + persist) a notification directly |
| GET | `/notifications` | Full notification log, all users |
| GET | `/notifications/{id}` | Get a single notification |
| GET | `/notifications/user/{userId}` | A user's notification history |

Parking Service triggers a `BOOKING_CONFIRMED` notification on every
successful reservation; Payment Service triggers `PAYMENT_SUCCESS` or
`PAYMENT_FAILED` after every processed payment.

## End-to-End Test Flow

```
1.  POST /users/register                → get {userId}
2.  POST /vehicles                      → get {vehicleId}  (validates userId via User Service)
3.  POST /spaces                        → get {spaceId}
4.  PUT  /spaces/{spaceId}/reserve      → RESERVED           (notifies: BOOKING_CONFIRMED)
5.  POST /vehicles/{vehicleId}/entry
6.  POST /vehicles/{vehicleId}/exit     → duration calculated
7.  POST /payments                      → SUCCESS            (releases space, notifies: PAYMENT_SUCCESS)
8.  GET  /spaces/{spaceId}              → back to AVAILABLE
9.  GET  /payments/{paymentId}/receipt  → full itemized receipt
10. GET  /notifications/user/{userId}   → BOOKING_CONFIRMED + PAYMENT_SUCCESS logged
11. GET  /analytics/usage               → totals reflect this session
```

This flow exercises every service and every inter-service connection in
the system in a single pass.

## Postman Collection

`postman_collection.json` at the project root covers every endpoint listed
above, organized into one folder per service, plus:
- An **Infrastructure** folder for health checks (Eureka, Config Server, Gateway)
- An **End-to-End Flow (via Gateway)** folder — the 11-step flow above,
  pre-built as ready-to-run requests through port 8080

Collection variables (`userServiceUrl`, `vehicleServiceUrl`, `userId`,
`vehicleId`, `spaceId`, `paymentId`, etc.) let you run requests directly
against each service, or point them at the Gateway instead, without
editing every URL by hand.

## Build Notes by Part

The system was built incrementally, in five parts, each layering on top
of the last:

**Part 1 — Infrastructure.** Environment and repository setup, followed by
the three foundation services: Eureka Server for service discovery,
Config Server for centralized configuration, and API Gateway as the
single entry point with routes for every downstream service.

**Part 2 — Core Domain Services.** User Service (registration, profiles,
booking history) and Vehicle Service (registration linked live to a real
user via Eureka, plus entry/exit tracking with automatic duration
calculation).

**Part 3 — Parking & Pricing.** Parking Service starting with the core
space model and CRUD, then layering on search filters, reserve/release
actions, dynamic surge and peak-hour pricing, and a background scheduler
that auto-expires stale reservations.

**Part 4 — Payments & Insight.** Payment Service simulating a full mock
gateway transaction lifecycle with itemized receipts aggregated live from
three other services; Analytics Service for stateless, real-time usage
reporting; Notification Service for logging booking and payment events,
wired into real triggers rather than left as a standalone API.

**Part 5 — Integration & Delivery.** Connecting the services that were
still working in isolation (most notably Parking ↔ Payment, so a
successful payment automatically frees the space), a full end-to-end test
pass across all nine services, a complete Postman collection, and this
documentation.

## Resources

- [Postman Collection](./postman_collection.json)
- [Final Testing Checklist](./docs/FINAL_TESTING_CHECKLIST.md)
- ![Eureka Dashboard](./docs/screenshots/eureka_dashboard.png)
