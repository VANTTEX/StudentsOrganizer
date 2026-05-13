-- Создание базы данных
CREATE DATABASE student_organizer;

-- Подключение к БД
\c student_organizer;

-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    course VARCHAR(50),
    institute VARCHAR(255),
    avatar_filename VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Индекс для поиска по email
CREATE INDEX idx_users_email ON users(email);
