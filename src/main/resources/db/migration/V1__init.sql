CREATE TABLE car_park (
    id INT PRIMARY KEY AUTO_INCREMENT,
    car_park_no VARCHAR(50) NOT NULL UNIQUE,
    address TEXT,
    longitude DECIMAL(8, 5),
    latitude DECIMAL(7, 5),
    car_park_type VARCHAR(50),
    type_of_parking_system VARCHAR(50),
    short_term_parking VARCHAR(50),
    free_parking VARCHAR(50),
    night_parking VARCHAR(50),
    car_park_decks INT,
    gantry_height DECIMAL(5, 2),
    car_park_basement VARCHAR(50)
);

CREATE TABLE car_park_availability (
    id INT PRIMARY KEY AUTO_INCREMENT,
    car_park_no VARCHAR(50) NOT NULL,
    lot_type VARCHAR(50),
    total_lot INT,
    available_lot INT,
    update_datetime TIMESTAMP NULL,
    FOREIGN KEY (car_park_no) REFERENCES car_park(car_park_no),
    CONSTRAINT unique_carpark_lottype UNIQUE (car_park_no, lot_type)
);