create database Restaurant_Reservation;
use Restaurant_Reservation;

create table reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(10) NOT NULL,
    reservation_date DATETIME NOT NULL,
    num_guests INT NOT NULL CHECK (num_guests > 0 AND num_guests <= 20),
    special_request TEXT,
    UNIQUE (reservation_date, num_guests) --  avoid double bookings in the same slot
);
insert into reservations (name, contact, reservation_date, num_guests, special_request)
VALUES
('John Doe', '1234567890', '2024-12-25 19:00:00', 4, 'Window seat'),
('Jane Smith', '0987654321', '2024-12-25 19:30:00', 2, 'Vegan options'),
('Mike Johnson', '1122334455', '2024-12-25 20:00:00', 3, 'Celebrating anniversary'),
('Emily Davis', '2233445566', '2024-12-25 20:30:00', 5, NULL),
('Chris Brown', '3344556677', '2024-12-25 21:00:00', 4, 'High chair'),
('Anna White', '4455667788', '2024-12-25 21:30:00', 2, 'Outdoor seating'),
('David Lee', '5566778899', '2024-12-25 22:00:00', 6, 'Quiet area'),
('Olivia Green', '6677889900', '2024-12-25 22:30:00', 3, NULL),
('Lucas Turner', '7788990011', '2024-12-25 23:00:00', 4, 'Late night snack');
drop database restaurant_reservation;
SELECT * FROM reservations;
TRUNCATE TABLE reservations;


