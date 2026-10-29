# Bakery Management System (Задание 3)

## Назначение приложения
REST API для управления производственным планированием пекарни.
Приложение позволяет вести учёт ингредиентов на складе, создавать рецептуры изделий,
запускать производственные партии, оформлять заявки на выпечку и управлять их статусами.

## Предметная модель
В системе реализованы следующие сущности:

- **User** — заказчик (пользователь системы).
- **Ingredient** — ингредиент (название, единица измерения, физический остаток).
- **Recipe** — рецептура изделия (название).
- **RecipeIngredient** — промежуточная сущность состава рецепта (связь M:N).
- **ProductionBatch** — производственная партия (рецепт, количество, статус).
- **BakingRequest** — заявка на выпечку (пользователь, рецепт, количество, партия, статус).

## Связи между таблицами
- **Recipe → RecipeIngredient → Ingredient**: связь «многие ко многим» через промежуточную таблицу `recipe_ingredients`.
- **ProductionBatch → Recipe**: «многие к одному» (у партии один рецепт).
- **BakingRequest → User**: «многие к одному» (у заявки один заказчик).
- **BakingRequest → Recipe**: «многие к одному» (заявка на конкретный рецепт).
- **BakingRequest → ProductionBatch**: «многие к одному» (заявка может быть привязана к партии).

## Ограничения целостности
- `Ingredient.name` — **UNIQUE** (нельзя два ингредиента с одинаковым названием).
- `BakingRequest.user`, `BakingRequest.recipe`, `BakingRequest.quantity` — **NOT NULL**.
- `ProductionBatch.recipe`, `ProductionBatch.quantity` — **NOT NULL**.
- Удаление ингредиента блокируется, если он используется в рецептах (через `RecipeIngredient`).
- Удаление рецепта блокируется, если на него есть ссылки в партиях или заявках.

## Реализованные бизнес-операции
| № | Операция | Метод | Затрагивает сущностей |
|---|---|---|---|
| 1 | Пополнение склада | `POST /api/business/ingredients/{id}/restock` | 1 (Ingredient) |
| 2 | Запуск партии | `POST /api/business/batches/start` | 2 (Batch + Ingredient) |
| 3 | Создание заявки | `POST /api/business/requests` | 1 (BakingRequest) |
| 4 | Завершение партии | `POST /api/business/batches/{id}/finish` | 2 (Batch + Request) |
| 5 | Отмена заявки | `POST /api/business/requests/{id}/cancel` | 1 (BakingRequest) |
| 6 | Отмена партии | `POST /api/business/batches/{id}/cancel` | 3 (Batch + Ingredient + Request) |

**Примечание:** Минимум 3 операции (№2, №4, №6) затрагивают две и более сущности одновременно, что соответствует требованиям задания.

## Технологии
- Java 21
- Spring Boot 4.1.1
- Spring Data JPA (Hibernate)
- PostgreSQL
- Maven

## Безопасность БД
- Пароли и логин вынесены в переменные окружения (`DB_URL`, `DB_USER`, `DB_PASSWORD`).
- Для работы приложения создаётся пользователь `app_user` с ограниченными правами (только DML: SELECT, INSERT, UPDATE, DELETE). Скрипт создания находится в `init.sql`.

## Запуск приложения
1. Установите PostgreSQL и создайте базу данных `bakery_db`.
2. Выполните SQL-скрипт `init.sql` для создания пользователя `app_user` с ограниченными правами.
3. Задайте переменные окружения (или используйте значения по умолчанию из `application.properties`):
    - `DB_URL` = `jdbc:postgresql://localhost:5432/bakery_db`
    - `DB_USER` = `app_user`
    - `DB_PASSWORD` = `app_password`
4. Запустите `BakeryDbApplication`.
5. Для проверки эндпоинтов используйте файл `requests.http` (все запросы выполняются по порядку сверху вниз) или Postman.