# cinema

## Исходная задача

Необходимо написать REST сервис для бронирования мест в кинотеатрах.

Сервис должен иметь следующую функциональность:
1. Получение списка мест в зале со статусами свободно/забронировано. 
2. Бронирование одного или нескольких свободных мест в зале кинотеатра.

Примечания: 
1. Добавить недостающие по Вашему мнению методы в API (например: список 
кинотеатров, список залов в кинотеатре и прочее). 
2. Для реализации API можно использовать любой фреймворк. 
3. Для хранения данных использовать PostgreSQL или embedded db c SQL-
синтаксисом. 
4. При работе с базой использовать JDBC (не использовать ORM). 
5. Продемонстрировать работу API с помощью тестов. 
6. Обеспечить максимально простое развёртывание приложения (например 
docker-контейнер). 
7. Исходный код выложить на github или bitbucket.

## Описание сервиса

Перед вами backend сервиса для бронирования мест в кинотеатрах.

### Этапы разработки

1. Определяем пользовательские истории
2. Определить возможные допущения
3. Описываем доменную модель и основные команды
4. Описываем API по стандарту OpenAPI 3.0.0
5. Часть исходников можно сгенерировать в IntelliJ IDEA ktor плагином, но что-то это делать не хочется.
6. Написать тесты для RESTful API
7. Написать тесты для репозитория на Testcontainers
8. Запуск тестов на Github
9. Запуск Docker compose
10. Может быть, разобраться с логами?

### Пользовательские сценарии

#### Администратор 1

Сценарий описывает типовые действия Администратора кинотеатра.
1. Создать кинотеатр
2. Создать два зала в кинотеатре
3. Описать раскладку мест в зале
4. Создать фильм
5. Создать сеанс фильма в определенном зале, указать цену места
6. Получить список свободных мест, убедиться, что все места пока свободны

Сначала я думал, что добавлю такие сущности, как Тариф и Цены, но потом посчитал,
что это излишнее усложнение. Потому Все места на сеанс будут стоить одинаково.

#### Покупатель 1

Сценарий описывает типовые действия Покупателя билетов на сеанс

1. Получить список фильмов, выбрать филь
2. Получить список кинотеатров, где идет этот фильм, выбрать кинотеатр
3. Получить список сеансов фильма в кинотеатре, выбрать сеанс
4. Получить список свободных мест в зале на выбранный сеанс
5. Провести покупку билетов
6. Получить список свободных мест в зале на выбранный сеанс, убедиться, что места проданы

### Допущения

* Все места на сеанс будут стоить одинаково. Цена места указывается в Сеансе.
* Залы сложной формы, нестандартные места не учитываем. Считаем, что
все места в зале можно идентифицировать по признаку "X, Y" (Ряд, Место).
* Все цены указаны в условных единицах "рублях"; задачу создания
международного сервиса бронирования мест в зале не ставим.
* Список пользователей и администраторов оставляем за пределами системы.
При размещении заказа мы передаем ID пользователя, но самой базы пользователей
в разрабатываемом ПО нет.
* Сервис работает без аутентификации, авторизации.
* В системе ничего не редактируется. Просто чтобы немного упростить задачу
и не связываться с PATCH запросами.

### Список микросервисов

Так как нагрузка на сервисы в реальной среде будет сильно отличаться,
проект разбит на несколько модулей:
* Кинотеатры ([hall](/hall), такое название выбрано, чтобы не дублироваться с именем корня)
* Сеансы ([session](/session))
* Билеты ([ticket](/ticket))
* Приложение ([public](/public))

Идея разбиения проекта на модули основана на типе нагрузке на сервис.
* Информация о кинотеатрах меняется реже всего и хранится сколь угодно долго;
* Информация о сеансах постоянно дополняется, редко изменяется,
хранится не более определенного периода;
* Больше всего данных хранится в сервисе продажи билетов. В отличие от 
Кинотеатров и Сеансов, доступных лишь администратору
кинотеатра, Билеты доступны одновременно большому числу пользователей, 
чьи интересы сталкиваются при покупке того или иного места;
* Приложение представляет собой фасад, за которым скрыты обращения в 
остальные сервисы.

## Запуск

```
./gradlew shadowJar
docker build -t cinema-hall hall/
docker-compose up
```

После запуска, API доступны на портах:
* 8081 - hall
* 15432 - postgresql

### Создание схемы в базе данных

```
sudo -u postgres psql
postgres=# create database hall;
postgres=# create database session;
postgres=# create database ticket;
postgres=# CREATE USER cinema WITH ENCRYPTED PASSWORD 'cinemapass';
postgres=# grant all privileges on database hall to cinema;
postgres=# grant all privileges on database session to cinema;
postgres=# grant all privileges on database ticket to cinema;
postgres=# \connect hall
postgres=# grant all privileges on all tables in schema public to cinema;
postgres=# GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO cinema;
postgres=# \connect session
postgres=# grant all privileges on all tables in schema public to cinema;
postgres=# \connect ticket
postgres=# grant all privileges on all tables in schema public to cinema;
sudo -u postgres psql -d hall -a -f hall/src/main/resources/schema.sql
```

## Используемые технологии

* Язык программирования Kotlin 1.4;
* JDK 8;
* Легковесный фреймворк Ktor 1.4.0 для построения асинхронных серверных приложений;
* PostgreSQL 12, как основная база хранения данных;
* IntelliJ IDEA, как основная среда разработки;
* OpenAPI 3.0.0, как формат описания API для последующей генерации 
каркаса приложения сервера или клиента;
* Actions на GitHub для CI;
* Testcontainers для запуска БД в тестах;
* Docker 19.03.12.

## Список использованной 'литературы'

* Русскоязычное руководство по OpenAPI 3.0
https://starkovden.github.io/openapi-tutorial-overview.html
* Официальное руководство http://spec.openapis.org/oas/v3.0.0
* Рекомендации по проектированию JSON API https://jsonapi.org/
* Официальное руководство по Ktor https://ktor.io/
* Официальные примеры приложений на Ktor https://github.com/ktorio/ktor-samples
* Официальное руководство по Testcontainers https://www.testcontainers.org/
* Русскоязычная документация PostgresSQL https://postgrespro.ru/docs/postgresql/12
* Про хвостовую рекурсию в Kotlin https://kotlinlang.ru/docs/reference/functions.html
* Про Docker compose https://docs.docker.com/compose/
* Про запуск PostgeSQL в Docker https://hub.docker.com/_/postgres