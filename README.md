# Учебная платформа - Система управления онлайн-курсами

## Описание проекта

Учебная платформа для онлайн-курсов, разработанная на базе Spring Boot с использованием Hibernate/JPA для доступа к базе данных PostgreSQL.

### Возможности платформы:
- Управление курсами и учебными материалами
- Регистрация студентов на курсы
- Создание и проверка домашних заданий
- Проведение тестирования знаний (Quiz)
- Оставление отзывов о курсах
- Выдача сертификатов

## Технологический стек

| Технология | Версия | Описание |
|------------|--------|----------|
| Java | 17+ | Язык программирования |
| Spring Boot | 3.2.2 | Фреймворк для веб-приложений |
| Spring Data JPA | - | Работа с базой данных |
| Hibernate ORM | - | ORM-фреймворк |
| PostgreSQL | 14+ | Основная база данных |
| H2 Database | - | База данных для тестов |
| Lombok | - | Генерация boilerplate кода |
| Swagger/OpenAPI | 2.3.0 | Документация API |
| Maven | 3.8+ | Система сборки |

## Структура проекта

```
src/
├── main/
│   ├── java/com/learningplatform/
│   │   ├── LearningPlatformApplication.java  # Главный класс приложения
│   │   ├── config/
│   │   │   └── DataLoader.java               # Предзаполнение данными
│   │   ├── controller/                        # REST контроллеры (10 шт.)
│   │   ├── dto/                               # Объекты передачи данных
│   │   ├── entity/                            # JPA сущности (18 шт.)
│   │   ├── exception/                         # Обработка ошибок
│   │   ├── repository/                        # Репозитории (14 шт.)
│   │   └── service/                           # Бизнес-логика (10 шт.)
│   └── resources/
│       └── application.yml                    # Конфигурация
└── test/
    ├── java/com/learningplatform/
    │   ├── repository/                        # Тесты репозиториев
    │   ├── service/                           # Тесты сервисов
    │   └── integration/                       # Интеграционные тесты
    └── resources/
        └── application-test.yml               # Тестовая конфигурация
```

## Модель данных (18 сущностей)

### Список сущностей:

| № | Сущность | Описание | Связи |
|---|----------|----------|-------|
| 1 | User | Пользователь (студент/преподаватель/админ) | 1-1: Profile |
| 2 | Profile | Профиль пользователя | 1-1: User |
| 3 | Category | Категория курса | 1-M: Course |
| 4 | Tag | Тег курса | M-M: Course |
| 5 | Course | Курс | M-1: Category, Teacher; 1-M: Module |
| 6 | Module | Модуль курса | M-1: Course; 1-M: Lesson; 1-1: Quiz |
| 7 | Lesson | Урок | M-1: Module; 1-M: Assignment |
| 8 | Assignment | Задание | M-1: Lesson; 1-M: Submission |
| 9 | Submission | Решение задания | M-1: Assignment, Student |
| 10 | Quiz | Тест | 1-1: Module; 1-M: Question |
| 11 | Question | Вопрос теста | M-1: Quiz; 1-M: AnswerOption |
| 12 | AnswerOption | Вариант ответа | M-1: Question |
| 13 | QuizSubmission | Результат теста | M-1: Quiz, Student |
| 14 | Enrollment | Запись на курс | M-1: Student, Course |
| 15 | CourseReview | Отзыв о курсе | M-1: Course, Student |
| 16 | Certificate | Сертификат | M-1: Course, Student |
| 17 | Notification | Уведомление | M-1: User |
| 18 | LessonProgress | Прогресс по уроку | M-1: Lesson, Student |

### Диаграмма связей:

```
Пользователь (1) <---> (1) Профиль
Пользователь (1) ---> (M) Курс [как преподаватель]
Пользователь (1) ---> (M) Запись ---> (1) Курс
Пользователь (1) ---> (M) Решение
Пользователь (1) ---> (M) Результат теста
Пользователь (1) ---> (M) Отзыв

Курс (M) ---> (1) Категория
Курс (M) <---> (M) Тег
Курс (1) ---> (M) Модуль

Модуль (1) ---> (M) Урок
Модуль (1) <---> (1) Тест

Урок (1) ---> (M) Задание
Задание (1) ---> (M) Решение

Тест (1) ---> (M) Вопрос
Вопрос (1) ---> (M) Вариант ответа
Тест (1) ---> (M) Результат теста
```

## Установка и запуск

### Требования

- Java 17 или выше
- Maven 3.8 или выше
- PostgreSQL 14 или выше

### Шаг 1: Настройка базы данных

Создайте базу данных PostgreSQL:

```sql
CREATE DATABASE learning_platform;
```

### Шаг 2: Настройка переменных окружения

Установите переменные окружения:

```bash
# Linux/Mac
export DATABASE_URL=jdbc:postgresql://localhost:5432/learning_platform
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=ваш_пароль

# Windows (PowerShell)
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/learning_platform"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="ваш_пароль"
```

Или измените файл `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/learning_platform
    username: postgres
    password: ваш_пароль
```

### Шаг 3: Сборка проекта

```bash
mvn clean package
```

### Шаг 4: Запуск приложения

```bash
# Через Maven
mvn spring-boot:run

# Или через JAR файл
java -jar target/learning-platform-1.0.0.jar
```

### Шаг 5: Проверка работы

Откройте в браузере:
- Swagger UI: http://localhost:8080/swagger-ui.html
- API: http://localhost:8080/api

## Запуск тестов

```bash
# Все тесты
mvn test

# Только интеграционные тесты
mvn test -Dtest="*IntegrationTest"

# Тест ленивой загрузки
mvn test -Dtest="LazyLoadingTest"
```

## REST API эндпоинты

### Пользователи (`/api/users`)

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/users` | Создать пользователя |
| GET | `/api/users` | Получить всех пользователей |
| GET | `/api/users/{id}` | Получить пользователя по ID |
| PUT | `/api/users/{id}` | Обновить пользователя |
| DELETE | `/api/users/{id}` | Удалить пользователя |
| GET | `/api/users/teachers` | Получить всех преподавателей |
| GET | `/api/users/students` | Получить всех студентов |

### Курсы (`/api/courses`)

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/courses` | Создать курс |
| GET | `/api/courses` | Получить все курсы |
| GET | `/api/courses/published` | Получить опубликованные курсы |
| GET | `/api/courses/{id}` | Получить курс по ID |
| GET | `/api/courses/{id}/details` | Получить курс с модулями |
| PUT | `/api/courses/{id}` | Обновить курс |
| DELETE | `/api/courses/{id}` | Удалить курс |
| PATCH | `/api/courses/{id}/publish` | Опубликовать курс |
| POST | `/api/courses/{id}/enroll?userId={id}` | Записать на курс |
| GET | `/api/courses/search?keyword={слово}` | Поиск курсов |

### Модули (`/api/modules`)

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/modules` | Создать модуль |
| GET | `/api/modules/{id}` | Получить модуль |
| GET | `/api/modules/{id}/lessons` | Получить модуль с уроками |
| GET | `/api/modules/course/{courseId}` | Получить модули курса |

### Уроки (`/api/lessons`)

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/lessons` | Создать урок |
| GET | `/api/lessons/{id}` | Получить урок |
| GET | `/api/lessons/{id}/assignments` | Получить урок с заданиями |

### Задания (`/api/assignments`)

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/assignments` | Создать задание |
| GET | `/api/assignments/{id}` | Получить задание |
| POST | `/api/assignments/{id}/submit` | Отправить решение |
| GET | `/api/assignments/{id}/submissions` | Получить все решения |

### Решения (`/api/submissions`)

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/submissions/{id}` | Получить решение |
| PATCH | `/api/submissions/{id}/grade?score={балл}` | Оценить решение |
| PATCH | `/api/submissions/{id}/accept` | Принять решение |
| PATCH | `/api/submissions/{id}/reject` | Отклонить решение |

### Тесты (`/api/quizzes`)

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/quizzes` | Создать тест |
| GET | `/api/quizzes/{id}` | Получить тест |
| GET | `/api/quizzes/{id}/questions` | Получить тест с вопросами |
| POST | `/api/quizzes/{id}/questions` | Добавить вопрос |
| POST | `/api/quizzes/{id}/take?studentId={id}` | Пройти тест |

### Записи (`/api/enrollments`)

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/enrollments/{id}` | Получить запись |
| GET | `/api/enrollments/student/{studentId}` | Записи студента |
| PATCH | `/api/enrollments/{id}/progress?progress={%}` | Обновить прогресс |
| PATCH | `/api/enrollments/{id}/complete` | Завершить курс |

## Примеры использования API

### Создание пользователя

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Иван Иванов",
    "email": "ivan@example.com",
    "role": "STUDENT"
  }'
```

### Создание курса

```bash
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Основы Java",
    "description": "Изучение языка Java с нуля",
    "duration": 40,
    "teacherId": 1,
    "categoryId": 1,
    "tags": ["Java", "Программирование", "Начинающий"]
  }'
```

### Запись на курс

```bash
curl -X POST "http://localhost:8080/api/courses/1/enroll?userId=2"
```

### Прохождение теста

```bash
curl -X POST "http://localhost:8080/api/quizzes/1/take?studentId=2" \
  -H "Content-Type: application/json" \
  -d '{
    "1": 2,
    "2": 5,
    "3": 9
  }'
```

## Ленивая загрузка (Lazy Loading)

Проект демонстрирует проблемы ленивой загрузки в Hibernate.

### Проблема

При попытке обратиться к ленивой коллекции вне транзакции возникает `LazyInitializationException`:

```java
// Это вызовет ошибку!
Course course = courseRepository.findById(1L).get();
course.getModules().size(); // LazyInitializationException
```

### Решения

**1. JOIN FETCH запросы (рекомендуется):**
```java
@Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules WHERE c.id = :id")
Optional<Course> findByIdWithModules(@Param("id") Long id);
```

**2. Работа внутри транзакции:**
```java
@Transactional
public void processModules(Long courseId) {
    Course course = courseRepository.findById(courseId).get();
    course.getModules().size(); // OK
}
```

**3. EntityGraph:**
```java
@EntityGraph(attributePaths = {"modules", "modules.lessons"})
Optional<Course> findById(Long id);
```

## Предзаполнение данными

При первом запуске приложения автоматически создаются тестовые данные:

- **Пользователи:** 1 администратор, 2 преподавателя, 3 студента
- **Категории:** Программирование, Базы данных, Веб-разработка, DevOps
- **Теги:** Java, Spring, Hibernate, SQL, PostgreSQL и др.
- **Курсы:** 3 курса с модулями, уроками и заданиями
- **Тесты:** 1 тест с вопросами и вариантами ответов
- **Записи:** Студенты записаны на курсы

## Обработка ошибок

API возвращает структурированные JSON-ответы об ошибках:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Курс не найден с id: '999'",
  "path": "/api/courses/999",
  "timestamp": "2024-01-15T10:30:00"
}
```

### HTTP коды ответов

| Код | Описание |
|-----|----------|
| 200 | Успешный запрос |
| 201 | Ресурс создан |
| 204 | Нет содержимого (успешное удаление) |
| 400 | Ошибка валидации или бизнес-логики |
| 404 | Ресурс не найден |
| 409 | Конфликт (дублирование) |
| 500 | Внутренняя ошибка сервера |

