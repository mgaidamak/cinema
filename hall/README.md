# Кинотеатры (hall)

Информация о кинотеатрах меняется реже всего и хранится сколь угодно долго.

Информация хранится в PostgreSQL `Hall`.

Вся информация о кинотеатре хранится в таблицах:
1. Кинотеатры (Cinema)
2. Залы (Hall)
3. Места в зале (Seat)

Параметры кинотеатра:
1. ID (autoincrement)
2. Name
3. City
4. Address
5. Timezone

Параметры зала:
1. ID (autoincrement)
2. CinemaID (REFERENCES Cinema)
3. Name

Параметры места
1. ID (autoincrement)
2. HallID (REFERENCES Hall)
3. X (Номер ряда)
4. Y (Номер места)

# Запросы

```shell script
curl -v -X POST \
--data-ascii "{\"name\": \"Pobeda\", \"city\": \"Nsk\", \"address\": \"Lenina\", \"timezone\": \"Novosibirsk\"}" \
--header "Content-Type:application/json" \
localhost:8081/cinema
< HTTP/1.1 200 OK
{"id":1,"name":"Pobeda","city":"Nsk","address":"Lenina","timezone":"Novosibirsk"}

curl -v -X POST \
--data-ascii "{\"name\": \"Cosmos\", \"city\": \"Nsk\", \"address\": \"Bogdashka\", \"timezone\": \"Novosibirsk\"}" \
--header "Content-Type:application/json" \
localhost:8081/cinema
< HTTP/1.1 200 OK
{"id":2,"name":"Cosmos","city":"Nsk","address":"Bogdashka","timezone":"Novosibirsk"}

curl -v localhost:8081/cinema/
< HTTP/1.1 200 OK
[{"id":1,"name":"Pobeda","city":"Nsk","address":"Lenina","timezone":"Novosibirsk"},
{"id":2,"name":"Cosmos","city":"Nsk","address":"Bogdashka","timezone":"Novosibirsk"}]

curl -v localhost:8081/cinema/1
< HTTP/1.1 200 OK
{"id":1,"name":"Pobeda","city":"Nsk","address":"Lenina","timezone":"Novosibirsk"}

curl -v localhost:8081/cinema/10
< HTTP/1.1 404 Not Found
Not found by 10

curl -v -X DELETE localhost:8081/cinema/1
< HTTP/1.1 200 OK
```