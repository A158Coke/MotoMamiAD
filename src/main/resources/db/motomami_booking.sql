-- 创建数据库
CREATE
DATABASE hotel_booking_system;
USE
hotel_booking_system;

-- 用户表
CREATE TABLE users
(
    id         CHAR(36) PRIMARY KEY,
    username   VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    phone      VARCHAR(15),
    full_name  VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 酒店表
CREATE TABLE hotels
(
    id          CHAR(36) PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    location    VARCHAR(255) NOT NULL,
    description TEXT,
    rating      DECIMAL(2, 1) DEFAULT 0.0,
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 房间表
CREATE TABLE rooms
(
    id         CHAR(36) PRIMARY KEY,
    hotel_id   BIGINT         NOT NULL,
    room_type  VARCHAR(50)    NOT NULL,
    price      DECIMAL(10, 2) NOT NULL,
    available  BOOLEAN   DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (hotel_id) REFERENCES hotels (id)
);

-- 订单表
CREATE TABLE orders
(
    id             CHAR(36) PRIMARY KEY,
    user_id        BIGINT         NOT NULL,
    room_id        BIGINT         NOT NULL,
    check_in_date  DATE           NOT NULL,
    check_out_date DATE           NOT NULL,
    total_price    DECIMAL(10, 2) NOT NULL,
    status         ENUM('PENDING', 'CONFIRMED', 'CANCELLED') DEFAULT 'PENDING',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (room_id) REFERENCES rooms (id)
);

-- 用户评价表
CREATE TABLE reviews
(
    id         CHAR(36) PRIMARY KEY,
    user_id    BIGINT        NOT NULL,
    hotel_id   BIGINT        NOT NULL,
    room_id    BIGINT        NOT NULL,
    rating     DECIMAL(2, 1) NOT NULL,
    comment    TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (hotel_id) REFERENCES hotels (id),
    FOREIGN KEY (room_id) REFERENCES rooms (id)
);
CREATE TABLE user_orders
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    order_id   BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (order_id) REFERENCES orders (id)
);
-- 以下都是mock的模拟数据
-- 示例数据：酒店数据
INSERT INTO hotels (name, location, description, rating)
VALUES ('Oceanview Hotel', 'Miami Beach, FL', 'A beautiful beachfront hotel with ocean views', 4.5),
       ('Mountain Retreat', 'Aspen, CO', 'A cozy retreat nestled in the mountains', 4.7);

-- 示例数据：房间数据
INSERT INTO rooms (hotel_id, room_type, price, available)
VALUES (1, 'Single Room', 100.00, TRUE),
       (1, 'Double Room', 150.00, TRUE),
       (2, 'Single Room', 120.00, TRUE),
       (2, 'Suite', 250.00, TRUE);

-- 示例数据：用户数据
INSERT INTO users (username, password, email, phone, full_name)
VALUES ('johndoe', 'password123', 'john@example.com', '1234567890', 'John Doe'),
       ('janedoe', 'password456', 'jane@example.com', '0987654321', 'Jane Doe');

-- 示例数据：订单数据
INSERT INTO orders (user_id, room_id, check_in_date, check_out_date, total_price, status)
VALUES (1, 1, '2025-06-01', '2025-06-05', 400.00, 'PENDING'),
       (2, 3, '2025-06-15', '2025-06-18', 360.00, 'CONFIRMED');

-- 示例数据：评价数据
INSERT INTO reviews (user_id, hotel_id, room_id, rating, comment)
VALUES (1, 1, 1, 4.5, 'Great stay! Beautiful views and excellent service.'),
       (2, 2, 3, 5.0, 'Amazing retreat, perfect for a weekend getaway!');
