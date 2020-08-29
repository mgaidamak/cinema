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
7. Написать тесты для репозитория
8. Запуск тестов на Github

### Пользовательские сценарии

#### Администратор 1

Сценарий описывает типовые действия Администратора кинотеатра.
1. Создать кинотеатр
2. Создать два зала в кинотеатре
3. Создать тариф
4. Указать цены, входящие в состав тарифа
5. Описать раскладку мест в зале с учетом тарифа
6. Создать фильм
7. Создать сеанс фильма в определенном зале
8. Получить список свободных мест, убедиться, что все места пока свободны

#### Покупатель 1

Сценарий описывает типовые действия Покупателя билетов на сеанс

1. Получить список фильмов, выбрать филь
2. Получить список кинотеатров, где идет этот фильм, выбрать кинотеатр
3. Получить список сеансов фильма в кинотеатре, выбрать сеанс
4. Получить список свободных мест в зале на выбранный сеанс
5. Провести покупку билетов
6. Получить список свободных мест в зале на выбранный сеанс, убедиться, что места проданы

### Допущения

* Считаем, что в кинотеатре всегда используется одна тарифная сетка.
То есть весь зал единожды разбит на сектора разной стоимости, если в этом
есть необходимость. Изменить разбивку по секторам невозможно. Разница в
стоимости достигается путем создания нового тарифа, где модно переопределить
стоимость каждого сектора.
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
* Кинотеатры (hall, такое название выбрано, чтобы не дублироваться с именем корня)
* Сеансы (session)
* Билеты (ticket)
* Приложение (public)

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

TODO

## Используемые технологии

* Язык программирования Kotlin 1.4;
* JDK 8;
* Легковесный фреймворк Ktor 1.4.0 для построения асинхронных серверных приложений;
* PostgreSQL (v. todo), как основная база хранения данных;
* IntelliJ IDEA, как основная среда разработки;
* OpenAPI 3.0.0, как формат описания API для последующей генерации 
каркаса приложения сервера или клиента;
* Actions на GitHub для CI.

## Список использованной 'литературы'

* Русскоязычное руководство по OpenAPI 3.0
https://starkovden.github.io/openapi-tutorial-overview.html
* Официальное руководство
http://spec.openapis.org/oas/v3.0.0
* Рекомендации по проектированию JSON API
https://jsonapi.org/
* Официальное руководство по Ktor
https://ktor.io/
* Официальные примеры приложений на Ktor
https://github.com/ktorio/ktor-samples