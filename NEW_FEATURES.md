# Новые функции StudentOrganizer

## 1. Аватарки пользователей

### Как работает
- Пользователь может загрузить аватарку через EditProfileScreen
- Аватарка сохраняется в папку `server/avatars/`
- Имя файла сохраняется в БД в поле `avatar_filename`
- Поддерживаемые форматы: JPG, PNG, WEBP (макс. 5MB)

### API Endpoints

#### POST /api/profile/avatar (multipart/form-data)
```
Parts:
- userId: номер пользователя
- avatar: файл изображения
```

**Response:**
```json
{
  "message": "Аватарка загружена",
  "user": {
    "id": 1,
    "avatarUrl": "1_1234567890.jpg"
  }
}
```

#### GET /api/avatars/{filename}
Возвращает файл аватарки.

---

## 2. Список ВУЗов

### Как работает
- Список ВУЗов хранится в `server/universities.json` (2167 ВУЗов России)
- При открытии EditProfileScreen список загружается автоматически
- Есть поиск по названию, городу и типу

### API Endpoints

#### GET /api/universities
Возвращает полный список ВУЗов.

**Response:**
```json
[
  {
    "name": "МГУ им. М.В. Ломоносова",
    "city": "Москва",
    "type": "Государственный вуз",
    "website": "http://www.msu.ru/",
    "phone": "...",
    "email": "...",
    "address": "..."
  }
]
```

#### GET /api/universities/search?q={запрос}
Поиск ВУЗов по названию, городу или типу (макс. 50 результатов).

---

## 3. Friend ID (система друзей)

### Как работает
- При регистрации каждому пользователю генерируется уникальный `friend_id` (UUID)
- Friend ID отображается в профиле пользователя
- В будущем можно будет добавлять друзей по этому ID

### API Endpoints

#### POST /api/friends/add
```json
{
  "friendId": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### GET /api/friends/list?userId={id}
Возвращает список друзей пользователя.

---

## Миграция БД

Если у вас уже есть БД, выполните:
```bash
psql -U postgres -d student_organizer -f migrate_db.sql
```

Или вручную:
```sql
ALTER TABLE users ADD COLUMN IF NOT EXISTS friend_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid();
ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar_filename VARCHAR(500);

CREATE TABLE IF NOT EXISTS friendships (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    friend_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(50) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, friend_id)
);
```

---

## Запуск сервера

```bash
cd server
./gradlew run
```

Сервер запустится на `http://localhost:8080`

Проверка: `curl http://localhost:8080/health` → `{"status":"ok"}`

---

## Структура проекта

```
CurseWork/
├── server/
│   ├── src/main/kotlin/.../
│   │   ├── Application.kt
│   │   ├── model/User.kt              # DTO + FriendRequest, UniversityDto
│   │   ├── database/UserRepository.kt # Все SQL операции
│   │   └── routing/AuthRoutes.kt      # Все API endpoints
│   ├── universities.json              # 2167 ВУЗов России
│   ├── avatars/                       # Загруженные аватарки (gitignored)
│   ├── init_db.sql                    # Скрипт создания БД с нуля
│   └── migrate_db.sql                 # Миграция для существующей БД
├── app/
│   └── src/main/java/.../
│       ├── data/
│       │   ├── model/User.kt          # Добавлены id, friendId, avatarUrl
│       │   ├── api/
│       │   │   ├── ApiModels.kt       # DTO для Android + UniversityDto, FriendDto
│       │   │   └── AuthApi.kt         # Все API методы
│       │   ├── repository/
│       │   │   └── AuthRepository.kt  # Бизнес-логика + uploadAvatar, ВУЗы, друзья
│       │   └── storage/
│       │       └── UserPreferencesRepository.kt # updateAvatarUrl
│       └── ui/screens/
│           ├── AuthViewModel.kt       # uploadAvatar, loadUniversities, searchUniversities
│           ├── EditProfileScreen.kt   # Выбор аватарки + поиск ВУЗов
│           └── ProfileScreen.kt       # Отображение аватарки + Friend ID
└── ...
```
