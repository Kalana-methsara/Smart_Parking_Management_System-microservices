# SPMS — Final Testing & Submission Checklist

Use this checklist to verify the whole system before submitting, in the
order the assignment brief asks for (Gateway routing → Eureka services →
API responses → GitHub push).

## 1. Build everything cleanly
```bash
mvn clean install
```
- [ ] All 9 modules compile with no errors
- [ ] `BUILD SUCCESS` at the end

## 2. Start every service, in order
```bash
cd eureka-server        && mvn spring-boot:run   # 1
cd config-server        && mvn spring-boot:run   # 2
cd api-gateway          && mvn spring-boot:run   # 3
cd user-service         && mvn spring-boot:run   # 4
cd vehicle-service      && mvn spring-boot:run   # 5
cd parking-service      && mvn spring-boot:run   # 6
cd payment-service      && mvn spring-boot:run   # 7
cd analytics-service    && mvn spring-boot:run   # 8
cd notification-service && mvn spring-boot:run   # 9
```
- [ ] No service throws a startup exception
- [ ] Each logs `Started XxxApplication` before moving to the next

## 3. Verify Eureka registration
Open **http://localhost:8761**
- [ ] `CONFIG-SERVER` registered
- [ ] `API-GATEWAY` registered
- [ ] `USER-SERVICE` registered
- [ ] `VEHICLE-SERVICE` registered
- [ ] `PARKING-SERVICE` registered
- [ ] `PAYMENT-SERVICE` registered
- [ ] `ANALYTICS-SERVICE` registered
- [ ] `NOTIFICATION-SERVICE` registered
- [ ] **Take the Eureka dashboard screenshot here** — save as
      `docs/screenshots/eureka_dashboard.png` (referenced by the README)

## 4. Verify Gateway routing
Using the **End-to-End Flow (via Gateway)** folder in `postman_collection.json`
(everything through `localhost:8080/api/...` instead of individual ports):
- [ ] `POST /api/users/register` reaches User Service
- [ ] `POST /api/vehicles` reaches Vehicle Service
- [ ] `POST /api/spaces` reaches Parking Service
- [ ] `POST /api/payments` reaches Payment Service
- [ ] `GET /api/analytics/usage` reaches Analytics Service
- [ ] `GET /api/notifications/user/{id}` reaches Notification Service

## 5. Verify API responses — run the full Postman collection
Import `postman_collection.json` and run every folder top to bottom:
- [ ] Infrastructure (health checks) — all return `200`/`UP`
- [ ] User Service — register, list, get, update, bookings all work
- [ ] Vehicle Service — register, list, get, update, entry/exit/logs all work
- [ ] Parking Service — create, search/filter, reserve, release, expire-check all work
- [ ] Payment Service — successful payment, failed payment (expired card), receipt all work
- [ ] Analytics Service — usage numbers look sane (not all zero if you've created data)
- [ ] Notification Service — send, list, get, by-user all work
- [ ] End-to-End Flow folder — all 11 steps pass in order, chaining IDs correctly

## 6. Spot-check integration points
- [ ] Registering a vehicle with a bogus `userId` returns `404` (User↔Vehicle link)
- [ ] A successful payment auto-releases its parking space (Parking↔Payment link)
- [ ] A reservation and a successful payment each produce a Notification log entry
- [ ] `GET /payments/{id}/receipt` shows real user/vehicle/parking/duration data, not nulls
- [ ] `GET /analytics/usage` numbers change after creating more bookings/payments

## 7. Push to GitHub
- [ ] `git init` (if not already)
- [ ] `.gitignore` excludes `target/`, `.idea/`, etc. (already included)
- [ ] `git add . && git commit -m "Final submission: SPMS complete through Day 20"`
- [ ] `git remote add origin <your-repo-url>`
- [ ] `git push -u origin main`
- [ ] Confirm on GitHub: `README.md`, `postman_collection.json`, and
      `docs/screenshots/eureka_dashboard.png` are all visible in the repo

## 8. Final README check
- [ ] `[Postman Collection](./postman_collection.json)` link works
- [ ] `![Eureka Dashboard](./docs/screenshots/eureka_dashboard.png)` image renders
- [ ] All 9 module ports and responsibilities are documented
- [ ] Full API reference table is present and accurate

Once every box above is checked, the submission is ready.
