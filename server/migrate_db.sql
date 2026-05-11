-- Миграция для добавления новых полей и таблицы
-- Выполнять ПОСЛЕ того, как уже создана исходная БД

-- Добавляем friend_id (UUID) если его нет
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='users' AND column_name='friend_id') THEN
        ALTER TABLE users ADD COLUMN friend_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid();
    END IF;
END $$;

-- Добавляем avatar_filename если его нет
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='users' AND column_name='avatar_filename') THEN
        ALTER TABLE users ADD COLUMN avatar_filename VARCHAR(500);
    END IF;
END $$;

-- Создаём таблицу дружеских связей если её нет
CREATE TABLE IF NOT EXISTS friendships (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    friend_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(50) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, friend_id)
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_users_friend_id ON users(friend_id);
CREATE INDEX IF NOT EXISTS idx_friendships_user ON friendships(user_id);
CREATE INDEX IF NOT EXISTS idx_friendships_friend ON friendships(friend_id);
