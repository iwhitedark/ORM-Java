# Learning Platform - Документация проекта

## Описание проекта

Учебная платформа для онлайн-курсов, разработанная на базе Spring Boot с использованием Hibernate/JPA для доступа к базе данных PostgreSQL. Платформа позволяет:

- Управлять курсами и учебными материалами
- Регистрировать студентов на курсы
- Создавать и проверять задания
- Проводить тестирование знаний (Quiz)
- Оставлять отзывы о курсах

## Технологический стек

- **Java 17+**
- **Spring Boot 3.2.2**
- **Spring Data JPA**
- **Hibernate ORM**
- **PostgreSQL**
- **H2 Database** (для тестирования)
- **Lombok**
- **Swagger/OpenAPI** (документация API)
- **Maven**

## Структура проекта

```
src/
├── main/
│   ├── java/com/learningplatform/
│   │   ├── LearningPlatformApplication.java
│   │   ├── config/
│   │   │   └── DataLoader.java              # Предзаполнение данными
│   │   ├── controller/                       # REST контроллеры
│   │   │   ├── UserController.java
│   │   │   ├── CourseController.java
│   │   │   ├── ModuleController.java
│   │   │   ├── LessonController.java
│   │   │   ├── AssignmentController.java
│   │   │   ├── SubmissionController.java
│   │   │   ├── QuizController.java
│   │   │   ├── EnrollmentController.java
│   │   │   ├── CategoryController.java
│   │   │   └── TagController.java
│   │   ├── dto/                              # Data Transfer Objects
│   │   ├── entity/                           # JPA сущности (18 штук)
│   │   ├── exception/                        # Обработка ошибок
│   │   ├── repository/                       # Spring Data JPA репозитории
│   │   └── service/                          # Бизнес-логика
│   └── resources/
│       └── application.yml                   # Конфигурация
└── test/
    ├── java/com/learningplatform/
    │   ├── repository/                       # Тесты репозиториев
    │   ├── service/                          # Тесты сервисов
    │   └── integration/                      # Интеграционные тесты
    └── resources/
        └── application-test.yml              # Тестовая конфигурация
```

## Модель данных (18 сущностей)

### Основные сущности:

1. **User** - пользователь (студент/преподаватель/админ)
2. **Profile** - профиль пользователя (1-1 с User)
3. **Category** - категория курса
4. **Tag** - тег курса (M-M с Course)
5. **Course** - курс
6. **Module** - модуль курса
7. **Lesson** - урок
8. **Assignment** - задание
9. **Submission** - решение задания
10. **Quiz** - тест
11. **Question** - вопрос теста
12. **AnswerOption** - вариант ответа
13. **QuizSubmission** - результат теста
14. **Enrollment** - запись на курс
15. **CourseReview** - отзыв о курсе
16. **Certificate** - сертификат
17. **Notification** - уведомление
18. **LessonProgress** - прогресс по уроку

### Типы связей:

- **1-1**: User <-> Profile, Module <-> Quiz
- **1-M**: Course -> Module, Module -> Lesson, Lesson -> Assignment, Quiz -> Question, Question -> AnswerOption
- **M-M**: Course <-> Tag (через join table), User <-> Course (через Enrollment)

### Диаграмма связей:

```
User (1) <---> (1) Profile
User (1) ---> (M) Course [as teacher]
User (1) ---> (M) Enrollment ---> (1) Course
User (1) ---> (M) Submission
User (1) ---> (M) QuizSubmission
User (1) ---> (M) CourseReview

Course (M) ---> (1) Category
Course (M) <---> (M) Tag
Course (1) ---> (M) Module

Module (1) ---> (M) Lesson
Module (1) <---> (1) Quiz

Lesson (1) ---> (M) Assignment
Assignment (1) ---> (M) Submission

Quiz (1) ---> (M) Question
Question (1) ---> (M) AnswerOption
Quiz (1) ---> (M) QuizSubmission
```

## Запуск приложения

### Требования

- Java 17+
- Maven 3.8+
- PostgreSQL 14+ (или Docker)

### Настройка базы данных

1. Создайте базу данных PostgreSQL:

```sql
CREATE DATABASE learning_platform;
```

2. Настройте переменные окружения или измените `application.yml`:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/learning_platform
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=postgres
```

### Сборка и запуск

```bash
# Сборка проекта
mvn clean package

# Запуск приложения
mvn spring-boot:run

# Или запуск JAR файла
java -jar target/learning-platform-1.0.0.jar
```

### Запуск тестов

```bash
# Все тесты
mvn test

# Только интеграционные тесты
mvn test -Dtest="*IntegrationTest"
```

## REST API

API доступен по адресу: `http://localhost:8080/api`

Swagger UI: `http://localhost:8080/swagger-ui.html`

### Основные эндпоинты

#### Users
- `POST /api/users` - создать пользователя
- `GET /api/users` - получить всех пользователей
- `GET /api/users/{id}` - получить пользователя по ID
- `PUT /api/users/{id}` - обновить пользователя
- `DELETE /api/users/{id}` - удалить пользователя
- `GET /api/users/teachers` - получить всех преподавателей
- `GET /api/users/students` - получить всех студентов

#### Courses
- `POST /api/courses` - создать курс
- `GET /api/courses` - получить все курсы
- `GET /api/courses/published` - получить опубликованные курсы
- `GET /api/courses/{id}` - получить курс по ID
- `GET /api/courses/{id}/details` - получить курс с модулями и уроками
- `PUT /api/courses/{id}` - обновить курс
- `DELETE /api/courses/{id}` - удалить курс
- `PATCH /api/courses/{id}/publish` - опубликовать курс
- `POST /api/courses/{id}/enroll?userId={userId}` - записать студента на курс
- `GET /api/courses/search?keyword={keyword}` - поиск курсов

#### Modules
- `POST /api/modules` - создать модуль
- `GET /api/modules/{id}` - получить модуль
- `GET /api/modules/{id}/lessons` - получить модуль с уроками
- `GET /api/modules/course/{courseId}` - получить модули курса

#### Lessons
- `POST /api/lessons` - создать урок
- `GET /api/lessons/{id}` - получить урок
- `GET /api/lessons/{id}/assignments` - получить урок с заданиями

#### Assignments
- `POST /api/assignments` - создать задание
- `GET /api/assignments/{id}` - получить задание
- `POST /api/assignments/{id}/submit` - отправить решение
- `GET /api/assignments/{id}/submissions` - получить все решения

#### Submissions
- `GET /api/submissions/{id}` - получить решение
- `PATCH /api/submissions/{id}/grade?score={score}&feedback={feedback}` - оценить решение
- `PATCH /api/submissions/{id}/accept` - принять решение
- `PATCH /api/submissions/{id}/reject` - отклонить решение

#### Quizzes
- `POST /api/quizzes` - создать тест
- `GET /api/quizzes/{id}` - получить тест
- `GET /api/quizzes/{id}/questions` - получить тест с вопросами
- `POST /api/quizzes/{id}/questions` - добавить вопрос
- `POST /api/quizzes/{id}/take?studentId={studentId}` - пройти тест

#### Enrollments
- `GET /api/enrollments/{id}` - получить запись
- `GET /api/enrollments/student/{studentId}` - получить записи студента
- `PATCH /api/enrollments/{id}/progress?progress={progress}` - обновить прогресс
- `PATCH /api/enrollments/{id}/complete` - завершить курс

### Примеры запросов

#### Создание пользователя
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "role": "STUDENT"
  }'
```

#### Создание курса
```bash
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Java Fundamentals",
    "description": "Learn Java programming from scratch",
    "duration": 40,
    "teacherId": 1,
    "categoryId": 1,
    "tags": ["Java", "Programming", "Beginner"]
  }'
```

#### Запись на курс
```bash
curl -X POST "http://localhost:8080/api/courses/1/enroll?userId=2"
```

#### Прохождение теста
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

Проект настроен для демонстрации проблем ленивой загрузки. По умолчанию все коллекции загружаются лениво (`FetchType.LAZY`).

### Проблема LazyInitializationException

При попытке обратиться к ленивой коллекции вне транзакции возникает исключение:

```java
// Это вызовет LazyInitializationException
Course course = courseRepository.findById(1L).get();
course.getModules().size(); // Ошибка!
```

### Решения

1. **JOIN FETCH запросы** (рекомендуется):
```java
@Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules WHERE c.id = :id")
Optional<Course> findByIdWithModules(@Param("id") Long id);
```

2. **Работа внутри транзакции**:
```java
@Transactional
public void processModules(Long courseId) {
    Course course = courseRepository.findById(courseId).get();
    course.getModules().size(); // OK внутри транзакции
}
```

3. **@EntityGraph**:
```java
@EntityGraph(attributePaths = {"modules", "modules.lessons"})
Optional<Course> findById(Long id);
```

## Интеграционные тесты

Проект включает тесты для демонстрации:

1. **CRUD операции** - создание, чтение, обновление, удаление сущностей
2. **Каскадные операции** - сохранение связанных объектов
3. **Ленивая загрузка** - демонстрация LazyInitializationException
4. **Бизнес-логика** - запись на курс, прохождение тестов

### Запуск тестов ленивой загрузки

```bash
mvn test -Dtest="LazyLoadingTest"
```

## Предзаполнение данными

При запуске приложения автоматически создаются тестовые данные:

- 6 пользователей (1 админ, 2 преподавателя, 3 студента)
- 4 категории
- 8 тегов
- 3 курса с модулями, уроками и заданиями
- 1 тест с вопросами
- Записи студентов на курсы

## Обработка ошибок

API возвращает структурированные ответы об ошибках:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Course not found with id: '999'",
  "path": "/api/courses/999",
  "timestamp": "2024-01-15T10:30:00"
}
```

### Коды ошибок

- `400 Bad Request` - ошибка валидации или бизнес-логики
- `404 Not Found` - ресурс не найден
- `409 Conflict` - дублирование ресурса
- `500 Internal Server Error` - внутренняя ошибка сервера
