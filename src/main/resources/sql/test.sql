insert into users (id, name, email, password, email_confirmed, created_at)
VALUES ( 1, 'test', 'test@gmail.com', '$2a$10$j4DyursSocELVR0Hsw4rk.pNT0K9dt9VERR6WIPUq9R/bvsFyljEG', 'T', now());

ALTER SEQUENCE users_seq RESTART WITH 2;