# Запросы

curl -v -X POST \
--data-ascii "{\"name\": \"Pobeda\", \"city\": \"Nsk\", \"address\": \"Lenina\", \"timezone\": \"Novosibirsk\"}" \
--header "Content-Type:application/json" localhost:8080/cinema
< HTTP/1.1 200 OK
{"id":1,"name":"Pobeda","city":"Nsk","address":"Lenina","timezone":"Novosibirsk"}

curl -v -X POST \
--data-ascii "{\"name\": \"Cosmos\", \"city\": \"Nsk\", \"address\": \"Bogdashka\", \"timezone\": \"Novosibirsk\"}" \
--header "Content-Type:application/json" localhost:8080/cinema
< HTTP/1.1 200 OK
{"id":2,"name":"Cosmos","city":"Nsk","address":"Bogdashka","timezone":"Novosibirsk"}

curl -v localhost:8080/cinema/
< HTTP/1.1 200 OK
[{"id":1,"name":"Pobeda","city":"Nsk","address":"Lenina","timezone":"Novosibirsk"},
{"id":2,"name":"Cosmos","city":"Nsk","address":"Bogdashka","timezone":"Novosibirsk"}]

curl -v localhost:8080/cinema/1
< HTTP/1.1 200 OK
{"id":1,"name":"Pobeda","city":"Nsk","address":"Lenina","timezone":"Novosibirsk"}

curl -v localhost:8080/cinema/10
< HTTP/1.1 404 Not Found
Not found by 10

curl -v -X DELETE localhost:8080/cinema/1
< HTTP/1.1 200 OK