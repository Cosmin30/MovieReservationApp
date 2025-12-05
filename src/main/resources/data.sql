
SET session_replication_role = 'replica';

DELETE FROM payments;
DELETE FROM tickets;
DELETE FROM reservations;
DELETE FROM seats;
DELETE FROM screenings;
DELETE FROM movies;
DELETE FROM halls;

SET session_replication_role = 'origin';

INSERT INTO halls (id, name, number, capacity) VALUES
('2c7279ec-f071-4cb0-a505-b514560b2eca', 'Sala IMAX', 1, 150),
('3d8380fd-f182-5db1-b616-c625671c3fdb', 'Sala Dolby Atmos', 2, 120),
('4e9491fe-f293-6ec2-c727-d736782d4fec', 'Sala Standard', 3, 100),
('5f05a2ff-f304-7fd3-d838-e847893e5ffd', 'Sala VIP', 4, 80),
('6f16b3ff-f415-8fe4-e949-f958904f6ffe', 'Sala 3D', 5, 130);

-- ============================================
-- MOVIES (Filme)
-- ============================================
INSERT INTO movies (id, title, description, genre, duration, release_date) VALUES
('c3df7a18-c161-4d1c-a968-2dd8a1f34845', 'The Last Guardian', 'Un supererou se confruntă cu un nou inamic puternic. O poveste despre sacrificiu și curaj în fața răului.', 'Aventură', 134, '2024-11-15'),
('d4ef8b29-d272-5e2d-b079-3ee9b2f45956', 'Cyber Nexus', 'În viitorul apropiat, oamenii trăiesc în simbioză cu inteligența artificială. Dar când AI-ul devine conștient, lumea se schimbă pentru totdeauna.', 'Sci-Fi', 142, '2024-12-01'),
('e5ff9c3a-e383-6f3e-c18a-4ffac3f56a67', 'Midnight Shadows', 'Un detectiv dur investighează o serie de crime misterioase într-un oraș plin de secrete întunecate.', 'Thriller', 118, '2024-10-20'),
('f6ff0d4b-f494-7f4f-d29b-5ffbd4f67b78', 'Eternal Love', 'O poveste de dragoste epică care traversează decenii și continente, demonstrând că adevărata iubire rezistă timpului.', 'Dramă', 165, '2024-09-10'),
('f7ff1e5c-f5a5-8f5f-e3ac-6ffce5f78c89', 'Comedy Central', 'Cinci prieteni pornesc într-o aventură hilară care le schimbă viața pentru totdeauna. Râsete garantate!', 'Comedie', 95, '2024-11-25'),
('f8ff2f6d-f6b6-9f6f-f4bd-7ffdf6f89d9a', 'Horror Mansion', 'Un grup de tineri descoperă o casă bântuită cu o istorie întunecată. Supraviețuirea devine o luptă pentru viață.', 'Horror', 108, '2024-10-05'),
('f9ff3f7e-f7c7-0f7f-f5ce-8ffef7f9aeab', 'Action Hero', 'Un agent secret trebuie să salveze lumea de la o amenințare globală. Acțiune non-stop și scene spectaculoase.', 'Acțiune', 125, '2024-12-10'),
('faff4f8f-f8d8-1f8f-f6df-9ffff8f0bfbc', 'Fantasy Realm', 'Într-o lume magică, un tânăr vrăjitor trebuie să-și descopere puterea pentru a salva regatul de forțele întunecate.', 'Fantasy', 152, '2024-11-20'),
('fbff5f9f-f9e9-2f9f-f7ef-0ffff9f1cfcd', 'Documentary: Earth', 'Un documentar captivant despre frumusețea planetei noastre și importanța protejării mediului.', 'Documentar', 98, '2024-09-15');


INSERT INTO screenings (id, movie_id, hall_id, start_time, room_number, capacity) VALUES
-- Sala IMAX (150 locuri)
('5e772c9e-6d8e-43b5-9279-38aab61d305e', 'c3df7a18-c161-4d1c-a968-2dd8a1f34845', '2c7279ec-f071-4cb0-a505-b514560b2eca', '2025-12-05 10:00:00+02', 1, 150),
('6f883daf-7e9f-54c6-a38a-49bbc72e416f', 'd4ef8b29-d272-5e2d-b079-3ee9b2f45956', '2c7279ec-f071-4cb0-a505-b514560b2eca', '2025-12-05 13:30:00+02', 1, 150),
('7f994ebf-8f0f-65d7-b49b-5accd83f527f', 'e5ff9c3a-e383-6f3e-c18a-4ffac3f56a67', '2c7279ec-f071-4cb0-a505-b514560b2eca', '2025-12-05 17:00:00+02', 1, 150),
('8f0a5fcf-9f1f-76e8-c5ac-6bdde94f638f', 'f6ff0d4b-f494-7f4f-d29b-5ffbd4f67b78', '2c7279ec-f071-4cb0-a505-b514560b2eca', '2025-12-05 20:30:00+02', 1, 150),

-- Sala Dolby Atmos (120 locuri)
('9f1b6fdf-0f2f-87f9-d6bd-7ceef05f749f', 'f7ff1e5c-f5a5-8f5f-e3ac-6ffce5f78c89', '3d8380fd-f182-5db1-b616-c625671c3fdb', '2025-12-05 11:00:00+02', 2, 120),
('0f2c7fef-1f3f-98f0-e7ce-8dfff16f85af', 'f8ff2f6d-f6b6-9f6f-f4bd-7ffdf6f89d9a', '3d8380fd-f182-5db1-b616-c625671c3fdb', '2025-12-05 14:30:00+02', 2, 120),
('1f3d8fff-2f4f-09f1-f8df-9efff27f96bf', 'f9ff3f7e-f7c7-0f7f-f5ce-8ffef7f9aeab', '3d8380fd-f182-5db1-b616-c625671c3fdb', '2025-12-05 18:00:00+02', 2, 120),
('2f4e9fff-3f5f-10f2-f9ef-0ffff38f07cf', 'faff4f8f-f8d8-1f8f-f6df-9ffff8f0bfbc', '3d8380fd-f182-5db1-b616-c625671c3fdb', '2025-12-05 21:30:00+02', 2, 120),

('3f5f0fff-4f6f-21f3-f0ff-1ffff49f18df', 'fbff5f9f-f9e9-2f9f-f7ef-0ffff9f1cfcd', '4e9491fe-f293-6ec2-c727-d736782d4fec', '2025-12-05 10:30:00+02', 3, 100),
('4f6f1fff-5f7f-32f4-f1ff-2ffff50f29ef', 'c3df7a18-c161-4d1c-a968-2dd8a1f34845', '4e9491fe-f293-6ec2-c727-d736782d4fec', '2025-12-05 14:00:00+02', 3, 100),
('5f7f2fff-6f8f-43f5-f2ff-3ffff61f3aff', 'd4ef8b29-d272-5e2d-b079-3ee9b2f45956', '4e9491fe-f293-6ec2-c727-d736782d4fec', '2025-12-05 17:30:00+02', 3, 100),
('6f8f3fff-7f9f-54f6-f3ff-4ffff72f4bff', 'e5ff9c3a-e383-6f3e-c18a-4ffac3f56a67', '4e9491fe-f293-6ec2-c727-d736782d4fec', '2025-12-05 21:00:00+02', 3, 100),

('7f9f4fff-8f0f-65f7-f4ff-5ffff83f5cff', 'f6ff0d4b-f494-7f4f-d29b-5ffbd4f67b78', '5f05a2ff-f304-7fd3-d838-e847893e5ffd', '2025-12-05 12:00:00+02', 4, 80),
('8f0f5fff-9f1f-76f8-f5ff-6ffff94f6dff', 'f7ff1e5c-f5a5-8f5f-e3ac-6ffce5f78c89', '5f05a2ff-f304-7fd3-d838-e847893e5ffd', '2025-12-05 15:30:00+02', 4, 80),
('9f1f6fff-0f2f-87f9-f6ff-7ffff05f7eff', 'f8ff2f6d-f6b6-9f6f-f4bd-7ffdf6f89d9a', '5f05a2ff-f304-7fd3-d838-e847893e5ffd', '2025-12-05 19:00:00+02', 4, 80),

('0f2f7fff-1f3f-98f0-f7ff-8ffff16f8fff', 'f9ff3f7e-f7c7-0f7f-f5ce-8ffef7f9aeab', '6f16b3ff-f415-8fe4-e949-f958904f6ffe', '2025-12-05 11:30:00+02', 5, 130),
('1f3f8fff-2f4f-09f1-f8ff-9ffff27f9fff', 'faff4f8f-f8d8-1f8f-f6df-9ffff8f0bfbc', '6f16b3ff-f415-8fe4-e949-f958904f6ffe', '2025-12-05 15:00:00+02', 5, 130),
('2f4f9fff-3f5f-10f2-f9ff-0ffff38f0fff', 'fbff5f9f-f9e9-2f9f-f7ef-0ffff9f1cfcd', '6f16b3ff-f415-8fe4-e949-f958904f6ffe', '2025-12-05 18:30:00+02', 5, 130),
('3f5f0fff-4f6f-21f3-f0ff-1ffff49f1fff', 'c3df7a18-c161-4d1c-a968-2dd8a1f34845', '6f16b3ff-f415-8fe4-e949-f958904f6ffe', '2025-12-05 22:00:00+02', 5, 130);


DO $$
DECLARE
    screening_record RECORD;
    seat_number INTEGER := 1;
    seats_per_row INTEGER := 10;
    total_seats INTEGER;
    current_row INTEGER;
    seats_in_row INTEGER;
    i INTEGER;
BEGIN
    FOR screening_record IN 
        SELECT s.id, s.capacity, h.number as hall_number
        FROM screenings s
        JOIN halls h ON s.hall_id = h.id
    LOOP
        total_seats := screening_record.capacity;
        seat_number := 1;
        current_row := 1;
        
        WHILE seat_number <= total_seats LOOP
            seats_in_row := LEAST(seats_per_row, total_seats - seat_number + 1);
            
            FOR i IN 1..seats_in_row LOOP
                INSERT INTO seats (id, screening_id, row, number, is_available)
                VALUES (
                    gen_random_uuid(),
                    screening_record.id,
                    current_row::TEXT,
                    seat_number,
                    TRUE  -- Toate locurile sunt disponibile inițial
                );
                seat_number := seat_number + 1;
            END LOOP;
            
            current_row := current_row + 1;
        END LOOP;
    END LOOP;
END $$;

SELECT 'Halls inserted: ' || COUNT(*) FROM halls;
SELECT 'Movies inserted: ' || COUNT(*) FROM movies;
SELECT 'Screenings inserted: ' || COUNT(*) FROM screenings;
SELECT 'Seats inserted: ' || COUNT(*) FROM seats;

