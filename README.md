# java-shareit
ShareIt - это веб-приложение для обмена предметами между пользователями. 


Приложение состоит из трех сервисов:

### gateway: 
Приложение на Spring Boot, которое является точкой входа в приложение и обрабатывает пользователей.

### server: 
Приложение на Spring Boot, которое предоставляет основную функциональность приложения, такую как создание и управление предметами и бронированием.
### Схема приложения:
![schemeItemReq](https://github.com/moongrail/java-shareit/assets/97224620/f2e9ffa8-40fe-4c86-b4d2-818d6341ee2b)

### db:
База данных PostgreSQL, которая хранит данные приложения.
### Запуск:
- docker-compose up
### Конфигурация:
Приложение ShareIt может быть настроено с помощью переменных окружения. Доступны следующие переменные окружения:

SHAREIT_SERVER_URL: URL-адрес сервера ShareIt. По умолчанию: http://server:9090.

URL_DB: URL-адрес базы данных PostgreSQL. По умолчанию: postgresql://db:5432/shareit.

SERVER_USERNAME_DB: имя пользователя для базы данных PostgreSQL. По умолчанию: postgres.

SERVER_PASSWORD_DB: пароль для базы данных PostgreSQL. По умолчанию: 123.

### Стек:
- Java 11
- Maven
- Spring Boot 2.7
- Postgres
- Hibernate
- SpringTest(Junit 5/Mockito)
- Jacoco
- Spotbugs
