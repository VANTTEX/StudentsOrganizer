-- Создание базы данных
CREATE DATABASE student_organizer;

-- Подключение к БД
\c student_organizer;

-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    friend_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
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

-- Индекс для поиска по friend_id
CREATE INDEX idx_users_friend_id ON users(friend_id);

-- Таблица дружеских связей
CREATE TABLE IF NOT EXISTS friendships (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    friend_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(50) DEFAULT 'pending', -- 'pending', 'accepted', 'rejected'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, friend_id)
);

-- Индексы для friendships
CREATE INDEX idx_friendships_user ON friendships(user_id);
CREATE INDEX idx_friendships_friend ON friendships(friend_id);
