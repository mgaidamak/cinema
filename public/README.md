# Приложение (public)

Приложение представляет собой фасад, за которым скрыты обращения в 
остальные сервисы. Это внешнее API для интернет-магазина.

curl -v localhost:8084/public/cinema
< HTTP/1.1 200 OK
[{"id":1,"name":"Pobeda","city":"Nsk","address":"Lenina"},
{"id":2,"name":"Pobeda","city":"Nsk","address":"Lenina"}]

curl -v localhost:8084/public/cinema?city=Tmsk
< HTTP/1.1 200 OK
[{"id":1,"name":"Pobeda","city":"Nsk","address":"Lenina"},
{"id":2,"name":"Pobeda","city":"Nsk","address":"Lenina"}]
