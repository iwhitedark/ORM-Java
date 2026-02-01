# ORM-фреймворки для Java  
## Итоговый проект

В рамках данного проекта вам предстоит разработать учебную платформу для онлайн-курса по ORM и Hibernate. Проект имитирует реальный заказ: образовательная компания нуждается в системе управления учебными курсами, которая позволит вести расписание занятий, раздавать задания студентам, собирать их решения и проводить тестирование знаний.

Этот проект поможет вам закрепить навыки работы с JPA/Hibernate, Spring Boot и реляционными БД, а также понять типичные проблемы, с которыми можно столкнуться при работе с ORM (например, проблему ленивой загрузки). В результате вы получите практический опыт создания сложной серверной системы и добавите в портфолио полноценный проект.

---

## 1. Общее описание

Необходимо разработать веб-приложение (учебную платформу) на базе Spring Boot, которое использует Hibernate/JPA для доступа к базе данных PostgreSQL.

Приложение должно обеспечивать:
- хранение структуры курсов и учебных материалов;
- управление пользователями (студенты, преподаватели, администраторы);
- процессы обучения: запись на курс, выполнение домашних заданий, прохождение тестов.

---

## 2. Технические требования

### 2.1 Стек технологий

- Java 17+
- Spring Boot
- Spring Data JPA
- Hibernate (JPA)
- PostgreSQL
- Maven или Gradle

---

### 2.2 Модель данных

Необходимо спроектировать **15–20 сущностей**, отражающих предметную область учебной платформы.

Обязательные типы связей:
- One-to-One
- One-to-Many
- Many-to-Many

Все коллекционные связи должны быть **LAZY**, чтобы в процессе разработки можно было столкнуться с проблемами ленивой загрузки (`LazyInitializationException`) и научиться их решать.

---

## 3. Структура базы данных

### User (Пользователь)

**Описание:**  
Представляет участника системы: студент, преподаватель или администратор.

**Поля:**
- `id`
- `name`
- `email` (уникальный)
- `role` (STUDENT / TEACHER / ADMIN)

**Связи:**
- преподаватель → курсы (One-to-Many)
- студент → курсы (Many-to-Many через Enrollment)
- One-to-One с Profile
- One-to-Many с Submission
- One-to-Many с QuizSubmission
- One-to-Many с CourseReview

---

### Profile (Профиль пользователя)

**Поля:**
- `id`
- `bio`
- `avatarUrl`

**Связи:**
- One-to-One с User (LAZY)

---

### Course (Курс)

**Поля:**
- `id`
- `title`
- `description`
- `duration`
- `startDate`

**Связи:**
- Many-to-One с Category
- Many-to-One с User (преподаватель)
- One-to-Many с Module
- One-to-Many с CourseReview
- Many-to-Many с Tag (через join-таблицу)
- Many-to-Many со студентами через Enrollment

---

### Category (Категория)

**Поля:**
- `id`
- `name`

**Связи:**
- One-to-Many с Course

---

### Enrollment (Запись на курс)

**Поля:**
- `id`
- `enrollDate`
- `status` (ACTIVE / COMPLETED)

**Связи:**
- Many-to-One с User
- Many-to-One с Course

---

### Module (Модуль)

**Поля:**
- `id`
- `title`
- `orderIndex`

**Связи:**
- Many-to-One с Course
- One-to-Many с Lesson
- One-to-One с Quiz (опционально)

---

### Lesson (Урок)

**Поля:**
- `id`
- `title`
- `content`
- `videoUrl`

**Связи:**
- Many-to-One с Module
- One-to-Many с Assignment

---

### Assignment (Задание)

**Поля:**
- `id`
- `title`
- `description`
- `dueDate`
- `maxScore`

**Связи:**
- Many-to-One с Lesson
- One-to-Many с Submission

---

### Submission (Решение задания)

**Поля:**
- `id`
- `submittedAt`
- `content`
- `score`
- `feedback`

**Связи:**
- Many-to-One с Assignment
- Many-to-One с User

---

### Quiz (Тест)

**Поля:**
- `id`
- `title`
- `timeLimit`

**Связи:**
- One-to-One или Many-to-One с Module
- One-to-Many с Question

---

### Question (Вопрос)

**Поля:**
- `id`
- `text`
- `type` (SINGLE_CHOICE / MULTIPLE_CHOICE)

**Связи:**
- Many-to-One с Quiz
- One-to-Many с AnswerOption

---

### AnswerOption (Вариант ответа)

**Поля:**
- `id`
- `text`
- `isCorrect`

**Связи:**
- Many-to-One с Question

---

### QuizSubmission (Результат теста)

**Поля:**
- `id`
- `score`
- `takenAt`

**Связи:**
- Many-to-One с Quiz
- Many-to-One с User

---

### CourseReview (Отзыв о курсе)

**Поля:**
- `id`
- `rating`
- `comment`
- `createdAt`

**Связи:**
- Many-to-One с Course
- Many-to-One с User

---

### Tag (Тег)

**Поля:**
- `id`
- `name`

**Связи:**
- Many-to-Many с Course

---

## 4. Этапы разработки

1. Анализ требований и проектирование модели данных  
2. Реализация сущностей, репозиториев и CRUD-операций  
3. Реализация бизнес-логики (курсы, задания, тесты)  
4. Интеграционное тестирование  
5. Подготовка документации и публикация проекта  

---

## 5. Требования к тестированию

- Интеграционные тесты CRUD
- Проверка каскадных операций
- Демонстрация проблемы Lazy Loading
- Проверка корректности связей

---

## 6. Дополнительные задания (опционально)

- REST API
- Swagger/OpenAPI
- Spring Security
- Docker / docker-compose
- CI/CD

---

## 7. Подготовка к сдаче

- Проект опубликован в GitHub
- README.md содержит инструкции по запуску
- Все требования ТЗ выполнены
- Тесты проходят успешно
