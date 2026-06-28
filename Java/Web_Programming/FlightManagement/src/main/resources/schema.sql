CREATE DATABASE IF NOT EXISTS flightsystem;

USE flightsystem;

CREATE TABLE IF NOT EXISTS user
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)                                         NOT NULL UNIQUE,
    email         VARCHAR(100)                                        NOT NULL UNIQUE,
    password      VARCHAR(255)                                        NOT NULL,
    fullname      VARCHAR(100)                                        NOT NULL,
    id_number     VARCHAR(20)                                         NOT NULL UNIQUE,
    account_state ENUM ('ACTIVE', 'DISABLED') DEFAULT 'ACTIVE'        NOT NULL,
    role          ENUM ('CLIENT', 'FLIGHT_MANAGER', 'SYSTEM_MANAGER') NOT NULL
);

CREATE TABLE IF NOT EXISTS client
(
    user_id      INT          NOT NULL UNIQUE,
    afm          VARCHAR(9)   NOT NULL UNIQUE,
    home_address VARCHAR(150) NOT NULL,

    CONSTRAINT client_user_id FOREIGN KEY (user_id)
        REFERENCES user (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS employee
(
    user_id       INT         NOT NULL UNIQUE,
    employee_code VARCHAR(20) NOT NULL UNIQUE,

    CONSTRAINT employee_user_id FOREIGN KEY (user_id)
        REFERENCES user (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS flight
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    flight_number  VARCHAR(20)                                                                  NOT NULL UNIQUE,
    airplane       VARCHAR(50)                                                                  NOT NULL,
    departure_date DATE                                                                         NOT NULL,
    departure_time TIME                                                                         NOT NULL,
    insertion_date DATE                                                  DEFAULT (CURRENT_DATE) NOT NULL,
    update_date    DATE                                                  DEFAULT (CURRENT_DATE) NOT NULL,
    seats          INT                                                                          NOT NULL,
    rowss          INT                                                                          NOT NULL,
    seats_row      INT                                                                          NOT NULL,
    rows_business  INT                                                                          NOT NULL,
    state          ENUM ('CREATED', 'STAFFED', 'CANCELLED', 'COMPLETED') DEFAULT 'CREATED'      NOT NULL
);

CREATE TABLE IF NOT EXISTS booking
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    booking_number VARCHAR(20)                                                       NOT NULL UNIQUE,
    client_id      INT                                                               NOT NULL,
    flight_id      INT                                                               NOT NULL,
    book_date      DATE                                       DEFAULT (CURRENT_DATE) NOT NULL,
    update_date    DATE                                       DEFAULT (CURRENT_DATE) NOT NULL,
    type           ENUM ('BUSINESS', 'NORMAL', 'ECONOMY')                            NOT NULL,
    roww           INT,
    columnn        INT,
    state          ENUM ('CREATED', 'CANCELLED', 'COMPLETED') DEFAULT 'CREATED'      NOT NULL,
    CONSTRAINT booking_client_id FOREIGN KEY (client_id)
        REFERENCES client (user_id) ON DELETE CASCADE,
    CONSTRAINT booking_flight_id FOREIGN KEY (flight_id)
        REFERENCES flight (id) ON DELETE CASCADE
);

INSERT INTO user (id, username, email, password, fullname, id_number, account_state, role)
VALUES (1, 'manager', 'manager@gmail.com', '123', 'Νίκος Παπαδόπουλος', 'AM123456', 'ACTIVE',
        'FLIGHT_MANAGER'),
       (2, 'nikos', 'nikos@gmail.com', '123', 'Νικόλαος Παπαδόπουλος', 'AN789012', 'ACTIVE', 'CLIENT'),
       (3, 'eleni', 'eleni@gmail.com', '123', 'Ελένη Δημητρίου', 'AE345678', 'ACTIVE', 'CLIENT'),
       (4, 'giorgos', 'giorgos@gmail.com', '123', 'Γιώργος Δημήτρογλου', 'AG112233', 'ACTIVE', 'CLIENT'),
       (5, 'anna', 'anna@gmail.com', '123', 'Άννα Κωνσταντίνου', 'AK445566', 'ACTIVE', 'CLIENT');

INSERT INTO employee (user_id, employee_code)
VALUES (1, 'EMP-002');

INSERT INTO client (user_id, afm, home_address)
VALUES (2, '123456789', 'Γοργύρας, Καρλόβασι'),
       (3, '987654321', 'Πυθαγόρα, Καρλάβασι'),
       (4, '456789123', 'Κανάρη 22, Αθήνα'),
       (5, '741852963', 'Γοργύρας 45, Καρλόβασι');

INSERT INTO flight (id, flight_number, airplane, departure_date, departure_time, insertion_date, update_date, seats,
                    rowss, seats_row, rows_business, state)
VALUES (1, 'AIR-001', 'Airbus A320', '2026-07-20', '08:30:00', CURRENT_DATE, CURRENT_DATE, 120, 20, 6, 4,
        'CREATED'),
       (2, 'BON-001', 'Boeing 737', '2026-08-15', '14:15:00', CURRENT_DATE, CURRENT_DATE, 120, 20, 6, 4, 'STAFFED'),
       (3, 'BON-002', 'Boeing 737', '2026-08-20', '14:15:00', CURRENT_DATE, CURRENT_DATE, 120, 20, 6, 4, 'COMPLETED');

INSERT INTO booking (id, booking_number, client_id, flight_id, book_date, update_date, type, roww, columnn, state)
VALUES (1, 'BK-AIR-001-1', 2, 1, CURRENT_DATE, CURRENT_DATE, 'NORMAL', NULL, NULL, 'CREATED'),
       (2, 'BK-BON-001-1', 2, 2, CURRENT_DATE, CURRENT_DATE, 'BUSINESS', NULL, NULL, 'CREATED'),
       (3, 'BK-BON-001-2', 3, 2, CURRENT_DATE, CURRENT_DATE, 'NORMAL', 6, 2, 'CREATED');
