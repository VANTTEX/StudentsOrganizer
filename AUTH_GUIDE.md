# StudentOrganizer — Авторизация с PostgreSQL

## Архитектура

```
┌─────────────────────┐       HTTP (JSON)       ┌──────────────────┐       JDBC       ┌──────────────┐
│  Android-приложение  │ ◄────────────────────► │  Ktor Server     │ ◄──────────────► │  PostgreSQL  │
│  (Jetpack Compose)   │      Retrofit          │  (порт 8080)      │   HikariCP       │  (localhost) │
└─────────────────────┘                        └──────────────────┘                  └──────────────┘
```

## Структура проекта

```
CurseWork/
├── app/                          # Android-приложение
│   └── src/main/java/.../
│       ├── data/
│       │   ├── api/              # Retrofit API клиент
│       │   │   ├── AuthApi.kt
│       │   │   ├── ApiModels.kt
│       │   │   └── RetrofitClient.kt
│       │   ├── model/            # Модели данных
│       │   │   └── User.kt
│       │   ├── repository/       # Бизнес-логика
│       │   │   ├── AuthRepository.kt
│       │   │   └── ValidationUtil.kt
│       │   └── storage/          # DataStore (локальный кеш)
│       │       └── UserPreferencesRepository.kt
│       └── ui/screens/
│           ├── AuthViewModel.kt
│           ├── LoginScreen.kt
│           ├── RegisterScreen.kt
│           └── ...
│
├── server/                       # Ktor сервер
│   └── src/main/kotlin/.../
│       ├── Application.kt        # Точка входа
│       ├── model/
│       │   ├── User.kt           # DTO модели
│       │   └── ValidationUtil.kt # Валидация
│       ├── database/
│       │   ├── DatabaseFactory.kt # Подключение к БД
│       │   └── UserRepository.kt  # SQL операции
│       └── routing/
│           └── AuthRoutes.kt     # API обработчики
│   ├── build.gradle.kts
│   └── init_db.sql               # Скрипт инициализации БД
```

## Настройка и запуск

### 1. PostgreSQL

```bash
# Подключитесь к PostgreSQL
psql -U postgres

# Выполните скрипт инициализации
\i C:/Users/bonda/Desktop/CurseWork/server/init_db.sql
```

Или вручную:
```sql
CREATE DATABASE student_organizer;
\c student_organizer;
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    course VARCHAR(50),
    institute VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. Запуск сервера

```bash
cd server
gradlew.bat run
```

Сервер запустится на `http://localhost:8080`

Проверка: `curl http://localhost:8080/health` → `{"status":"ok"}`

### 3. Запуск Android-приложения

Откройте проект в Android Studio и запустите на эмуляторе.

> **Важно:** Для эмулятора используется адрес `10.0.2.2:8080` (это localhost хост-машины).
> Для реального устройства замените в `RetrofitClient.kt` BASE_URL на IP вашего компьютера.

## API Endpoints

### POST /api/auth/register
Регистрация нового пользователя.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "Иванов Иван Иванович",
  "course": "1",
  "institute": "Институт ИТ"
}
```

**Success (201):**
```json
{
  "message": "Регистрация успешна",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "fullName": "Иванов Иван Иванович",
    "course": "1",
    "institute": "Институт ИТ"
  }
}
```

**Error (400):**
```json
{
  "message": "Некорректный формат email",
  "field": "email"
}
```

### POST /api/auth/login
Вход в систему.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Success (200):**
```json
{
  "message": "Вход выполнен",
  "user": { ... }
}
```

**Error (401):**
```json
{
  "message": "Неверный email или пароль"
}
```

### GET /health
Проверка работоспособности.

## Валидация

### На сервере и на клиенте:

| Поле | Правило |
|------|---------|
| **Email** | Обязательно, формат `name@domain.ext` |
| **Пароль** | Обязательно, ≥8 символов, ≥1 буква, ≥1 цифра |
| **ФИО** | Обязательно, ≥2 символа |
| **Курс** | Опционально |
| **Институт** | Опционально |

### Дополнительные проверки сервера:
- Уникальность email (409 Conflict при дубликате)
- Хеширование паролей через bcrypt

## Безопасность

- ✅ Пароли хешируются bcrypt (не хранятся в открытом виде)
- ✅ Валидация на сервере И на клиенте
- ✅ SQL-параметризация (защита от SQL injection)
- ⚠️ HTTP без HTTPS (для разработки; для продакшена нужен HTTPS)
- ⚠️ Нет JWT-токенов (для курсовой работы достаточно; для продакшена нужна сессия)

## Технологии

**Android:**
- Jetpack Compose (UI)
- Retrofit 2 (HTTP-клиент)
- OkHttp (логирование)
- DataStore (локальный кеш)
- Kotlin Coroutines

**Сервер:**
- Ktor 3.0.3
- HikariCP (connection pool)
- PostgreSQL JDBC Driver
- jBCrypt (хеширование)
