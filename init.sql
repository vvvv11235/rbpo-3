-- 1. Создаём пользователя БД с ограниченными правами
CREATE USER app_user WITH PASSWORD 'app_password';

-- 2. Даём права только на чтение и изменение данных
GRANT CONNECT ON DATABASE bakery_db TO app_user;
GRANT USAGE ON SCHEMA public TO app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;

-- 3. Запрещаем создание/удаление таблиц (правильный способ)
REVOKE CREATE ON SCHEMA public FROM app_user;