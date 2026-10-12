# RoadResale

A website to buy and sell used cars and bikes. UCS531 project.

Done so far (**Phases 2 and 3**): register/login, post an ad with a photo, edit / mark sold / delete,
search with filters, favourites, and messaging between buyer and seller.
Next is Phase 4: putting it on AWS (EC2, RDS, S3).

## How to run it on your laptop

You need three things installed:

1. **Java 17 or newer** (JDK)
2. **MySQL 8** (remember the root password you set)
3. **IntelliJ IDEA Community** (free) - it already includes Maven

Then:

1. Open the `roadresale` folder in IntelliJ (File > Open). Wait until it finishes downloading the libraries.
2. Open `src/main/resources/application.properties` and replace `CHANGE_ME` with your MySQL root password.
3. Make sure MySQL is running.
4. Open `RoadResaleApplication.java` and click the green Run button.
5. Open <http://localhost:8080> in your browser.

The database `roadresale` and its tables are created automatically on first start.

Without IntelliJ: install Maven and run `mvn spring-boot:run` inside this folder.

> Before pushing to GitHub, do not commit your real MySQL password.

## How the code is organised

```
com.roadresale
├── model        plain classes: Vehicle (abstract), Car, Bike, User, Ad
├── filter       SearchFilter interface + one small class per filter
├── repository   SQL only (one class per table)
├── service      the rules (UserService, AdService, FavouriteService, MessageService, StorageService)
├── controller   reads the request, calls a service, picks an HTML page
└── config       makes the uploads folder visible to the browser
```

A request travels in one direction: **Controller → Service → Repository → MySQL**.

Example - a buyer searches for cars in Patiala:

1. `AdController.home()` receives `type=CAR` and `city=Patiala`.
2. `AdService.search()` makes a `TypeFilter` and a `CityFilter`.
3. `AdRepository.findAllActive()` runs the SQL and returns `Ad` objects.
4. The service keeps the ads that pass every filter and the controller shows `index.html`.

## Photo storage: local folder or S3

`application.properties` has the line `app.storage=local`. With `local`, Spring creates
`LocalStorageService` (photos go in the `uploads` folder). Change it to `s3` and Spring creates
`S3StorageService` instead (photos go to the S3 bucket named in `app.s3.bucket`).
Both classes implement `StorageService`, so no other code changes.

`S3StorageService` has no access keys in it. On EC2 it uses the IAM role attached to the server.

## How messaging works

- A chat is about one ad, between two users. Its address is `/messages/{adId}/{otherUserId}`.
- A buyer can only write to the seller. The seller can only reply to buyers who wrote first (`MessageService.send`).
- The inbox shows one row per chat. `MessageService.getInbox` loops over the messages (newest first)
  and keeps the first one it sees for each chat.
- It is a simple inbox: refresh the page to see new replies.

## OOP ideas used (for the viva)

| Idea | Where | In one line |
|---|---|---|
| Abstraction | `Vehicle` | Abstract class: nobody sells "a vehicle", only a car or a bike |
| Inheritance | `Car`, `Bike` extend `Vehicle` | Common fields are written once in the parent |
| Polymorphism | `getType()`, `getExtraDetails()` | The detail page calls the same method; a car answers "Transmission", a bike answers "Engine cc" |
| Encapsulation | all model classes | Fields are private, used through getters/setters |
| Composition | `Ad` has a `Vehicle` | An ad contains a vehicle, it is not a vehicle |
| Interface | `StorageService` | `AdService` does not know where photos are stored; S3 can replace the local folder later |
| Strategy pattern | `SearchFilter` | Each filter is its own class; a new filter needs no change to old code |
| Layered design | controller / service / repository | Each layer has one job |

## Other points you may be asked

- **Passwords** are stored as BCrypt hashes, never as plain text (`UserService`).
- **Login** is remembered with a server-side session (`AuthController`).
- **SQL injection** is prevented because every query uses `?` placeholders.
- **Ownership**: `AdService.getOwnAd()` stops a user from editing someone else's ad.
- **Favourites** use a table with a two-column primary key (`user_id`, `ad_id`), so the same ad cannot be saved twice.
- **Deleting an ad** also removes its favourites and messages (`ON DELETE CASCADE` in `schema.sql`).
- **Why filter in Java and not SQL?** To show the strategy pattern clearly. For a very large
  site the filters would be moved into the SQL `WHERE` clause.
