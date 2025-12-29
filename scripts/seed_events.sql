CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO events (id, name, venue_name, description, price, total_seats, available_seats, sold_seats, start_time, end_time, created_at, updated_at)
VALUES
  ('5f1c0c5a-0a4b-4c2b-9d0d-8b8e9f5a2b01', 'Live Concert 29', 'My Dinh Stadium', 'Year-end mega show', 750000, 60, 60, 0, NOW() + interval '7 days', NOW() + interval '7 days' + interval '2 hours', NOW(), NOW()),
  ('9a5a3b2e-1d4f-4f2b-8b18-0b5e54ad3a12', 'Anh Trai Say Hi', 'Thong Nhat Stadium', 'Special night show', 500000, 60, 60, 0, NOW() + interval '10 days', NOW() + interval '10 days' + interval '2 hours', NOW(), NOW()),
  ('1f2a9c77-3c5b-4e15-9b0f-2d14c5c3a7e4', 'Blackpink World Tour', 'My Dinh Stadium', 'Blackpink live in Hanoi', 1200000, 60, 60, 0, NOW() + interval '14 days', NOW() + interval '14 days' + interval '2 hours', NOW(), NOW()),
  ('7d3b9a0c-6e2f-4d8e-8c1a-5b1f7e3a9c02', 'Taylor Swift Eras Night', 'My Dinh Stadium', 'Eras Night Hanoi', 1500000, 60, 60, 0, NOW() + interval '21 days', NOW() + interval '21 days' + interval '2 hours', NOW(), NOW()),
  ('2c7e9b1d-5f4a-4c6b-8d2e-7a1c9f0b3e15', 'Coldplay Music of the Spheres', 'National Stadium', 'Coldplay live in Hanoi', 1300000, 60, 60, 0, NOW() + interval '30 days', NOW() + interval '30 days' + interval '2 hours', NOW(), NOW()),
  ('8b4e2d6f-1c3a-4f7b-9d0e-6c2f8a5b1d09', 'Son Tung M-TP Sky Tour', 'Phu Tho Arena', 'Sky Tour 2026', 600000, 60, 60, 0, NOW() + interval '35 days', NOW() + interval '35 days' + interval '2 hours', NOW(), NOW()),
  ('0c1e2f3a-4b5d-6e7f-8a9b-1c2d3e4f5a60', 'Westlife The Hits', 'QK7 Arena', 'Westlife The Hits Live', 650000, 60, 60, 0, NOW() + interval '45 days', NOW() + interval '45 days' + interval '2 hours', NOW(), NOW()),
  ('b3c4d5e6-f7a8-49b0-9c1d-2e3f4a5b6c7d', 'Countdown 2026', 'Nguyen Hue Street', 'Countdown 2026 mega show', 400000, 60, 60, 0, NOW() + interval '60 days', NOW() + interval '60 days' + interval '2 hours', NOW(), NOW()),
  ('4e6f7a8b-9c0d-4e1f-8a2b-3c4d5e6f7a8b', 'Vpop Rising Stars', 'Hoa Binh Theater', 'Vpop Rising Stars', 350000, 60, 60, 0, NOW() + interval '75 days', NOW() + interval '75 days' + interval '2 hours', NOW(), NOW()),
  ('c1d2e3f4-5a6b-4c7d-8e9f-0a1b2c3d4e5f', 'EDM Festival Neon', 'Yen So Park', 'EDM Festival Neon', 550000, 60, 60, 0, NOW() + interval '90 days', NOW() + interval '90 days' + interval '2 hours', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

WITH seed_events(id) AS (
  VALUES
    ('5f1c0c5a-0a4b-4c2b-9d0d-8b8e9f5a2b01'),
    ('9a5a3b2e-1d4f-4f2b-8b18-0b5e54ad3a12'),
    ('1f2a9c77-3c5b-4e15-9b0f-2d14c5c3a7e4'),
    ('7d3b9a0c-6e2f-4d8e-8c1a-5b1f7e3a9c02'),
    ('2c7e9b1d-5f4a-4c6b-8d2e-7a1c9f0b3e15'),
    ('8b4e2d6f-1c3a-4f7b-9d0e-6c2f8a5b1d09'),
    ('0c1e2f3a-4b5d-6e7f-8a9b-1c2d3e4f5a60'),
    ('b3c4d5e6-f7a8-49b0-9c1d-2e3f4a5b6c7d'),
    ('4e6f7a8b-9c0d-4e1f-8a2b-3c4d5e6f7a8b'),
    ('c1d2e3f4-5a6b-4c7d-8e9f-0a1b2c3d4e5f')
)
INSERT INTO seats (id, event_id, row, col, status, held_by, held_until, created_at, updated_at)
SELECT gen_random_uuid(), se.id, r.row_label, c.col, 'AVAILABLE', NULL, NULL, NOW(), NOW()
FROM seed_events se
CROSS JOIN (VALUES ('A'), ('B'), ('C'), ('D'), ('E'), ('F')) r(row_label)
CROSS JOIN generate_series(1, 10) c(col)
ON CONFLICT (event_id, row, col) DO NOTHING;
