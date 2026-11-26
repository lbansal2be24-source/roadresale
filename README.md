# RoadResale

A website to buy and sell used cars and bikes. Built for the UCS531 course (Thapar Institute) by Lovish Bansal.

**Status: complete and deployed on AWS** (EC2 + RDS MySQL, rebuilt with one CloudFormation template).

## Features

- Register and log in (BCrypt-hashed passwords, session-based login)
- Post an ad with photos, edit it, mark it as sold, or delete it
- Search with filters (type, city, price range, year, keywords)
- Favourites (save ads you like)
- Buyer-seller messaging (each chat belongs to one ad and two users; buyers write first, sellers can reply)
- A friendly error page instead of a raw stack trace

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3 (MVC) |
| Views | Thymeleaf templates |
| Database access | JdbcTemplate (plain SQL, parameterised) |
| Database | MySQL 8 |
| Passwords | BCrypt |
| Build | Maven |
| Hosting | AWS EC2 (app) + AWS RDS (database), created by CloudFormation |

## Demo accounts (loaded automatically on the AWS deployment)

| Name | Email | Password |
|---|---|---|
| Rahul Sharma | rahul@demo.com | Demo@1234 |
| Priya Singh | priya@demo.com | Demo@1234 |
| Amit Verma | amit@demo.com | Demo@1234 |

The demo data has 12 ads (6 cars, 6 bikes; one car is marked SOLD so it is hidden from the listing), a few favourites and a sample chat. You can also create your own account with "Create account".

## How the code is organised

```
Browser -> Controller -> Service -> Repository -> MySQL
```

Packages under `com.roadresale`: `model`, `filter`, `repository`, `service`, `controller`, `config`.

Photos are stored through a storage service. The setting `APP_STORAGE` chooses `local` (a folder on the server, served at `/uploads/...`) or `s3`.

## Run it on your laptop

1. Install Java 17+ and MySQL 8 (IntelliJ IDEA Community is optional).
2. Set your MySQL password in `src/main/resources/application.properties` (never commit your real password).
3. Start MySQL and run `RoadResaleApplication.java`, or run `mvn spring-boot:run`.
4. Open http://localhost:8080 (the database tables are created on first start).

## Deploy on AWS (one click)

The file `deploy/roadresale.yaml` is a CloudFormation template that builds everything:

- 1 EC2 instance (t3.micro, Amazon Linux 2023) that clones this repo, builds it with Maven and runs it as a systemd service on port 8080
- 1 RDS MySQL 8 database (db.t3.micro) that accepts connections only from the web server
- demo users and ads, with real vehicle photos from `demo-photos/`

Steps: AWS Console -> CloudFormation -> Create stack -> upload `deploy/roadresale.yaml` -> keep the default database password (or choose your own, letters and numbers only) -> Create. After about 12-15 minutes open the `WebsiteURL` shown in the stack's **Outputs** tab.

The template was made for the AWS Academy Learner Lab (it uses the lab's `LabInstanceProfile` and the `vockey` key pair), so it is rebuilt each session.

## Security notes

BCrypt password hashing, server-side sessions (the session id is renewed at login), parameterised SQL, ownership checks so users can only edit their own ads, favourites without duplicates, and cascade delete of favourites and messages when an ad is removed.

## Photo credits

The demo vehicle photos are from Wikimedia Commons under free licences. The photographer and licence of each photo are listed in [`demo-photos/CREDITS.txt`](demo-photos/CREDITS.txt).
