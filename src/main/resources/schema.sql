-- RoadResale database tables (MySQL)

CREATE TABLE IF NOT EXISTS users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    phone         VARCHAR(20)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ads (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    seller_id    INT          NOT NULL,
    title        VARCHAR(150) NOT NULL,
    description  TEXT,
    price        INT          NOT NULL,
    city         VARCHAR(80)  NOT NULL,
    status       VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE',   -- ACTIVE or SOLD
    photo_path   VARCHAR(300),
    -- vehicle details
    vehicle_type VARCHAR(10)  NOT NULL,                    -- CAR or BIKE
    brand        VARCHAR(60)  NOT NULL,
    model        VARCHAR(60)  NOT NULL,
    model_year   INT          NOT NULL,
    km_driven    INT          NOT NULL,
    fuel_type    VARCHAR(20)  NOT NULL,
    transmission VARCHAR(20),                              -- cars only
    engine_cc    INT,                                      -- bikes only
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES users(id)
);

-- Phase 3: favourites and messages.
-- ON DELETE CASCADE: when an ad is deleted, its favourites and messages go with it.

CREATE TABLE IF NOT EXISTS favourites (
    user_id    INT NOT NULL,
    ad_id      INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, ad_id),                          -- one user can save an ad only once
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (ad_id)   REFERENCES ads(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS messages (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    ad_id       INT NOT NULL,
    sender_id   INT NOT NULL,
    receiver_id INT NOT NULL,
    body        VARCHAR(1000) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ad_id)       REFERENCES ads(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id)   REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id)
);
