####  Скачать приложение:
>$ git clone https://github.com/ivansaprykin/task2-spring-user-service.git  
>$ cd task2-spring-user-service/

####Запуск тестов:
>$ ./mvnw test

####Запуск приложения:
>$ ./mvnw spring-boot:run

####Примеры запросов:
Файл с описанием пользователя для запросов
>$ cat src/test/resources/user.json 

#####Добавление пользователя:
>$ curl -X POST -H "Content-Type: application/json" --data @src/test/resources/user.json http://localhost:8080/user/add

#####Поиск пользователя по email
>$ curl http://localhost:8080/user/get?email=my@mail.com

#####Удаление пользователя по email
>$ curl -X DELETE http://localhost:8080/user/delete?email=my@mail.com

В приложении включена консоль для доступа к inMemory базе данных:  
http://localhost:8080/h2-console/  
Driver Class: org.h2.Driver  
JDBC URL: jdbc:h2:mem:testdb   
Можно убедиться, что пароль хранится в безопасной форме.
  
####Описание REST API

Все взаимодействие происходит с использованием JSON сериализованных данных.
Необходимо учитывать занчения кода HTTP статуса.
В случае ошибки тело HTTP ответа содержит описание ошибки в формате:  
>  "message": "Описание ошибки",
>  "timestamp": "Дата и время"  

Например:
>{  
  "message": "User with email: my@mail.com already exists.",  
  "timestamp": "2018-09-30T15:25:06.314Z"  
}

####Возможные действия

#####Добавление пользователя:
POST запрос по адресу:  
/user/add  
тело запроса содержит описание пользователя в формате:  
>{  
  "firstName": "Имя",  
  "lastName": "Фамилия",  
  "birthDate": "Дата рождения в формате: ГГГГ-ММ-ДД",  
  "email": "emailПользователя",  
  "password": "Пароль"  
}

Ответ содержит HTTP status code - 201 Created,
в заголовке ответа location содержится ссылка на добавленого пользователя  
Location: /user/get?email=Email  
При попытке добавить пользователя с не уникальным, относительно существующих в базе, email-ом получим код - 409 Conflict, а в теле ответа будет описание ошибки:  
>"message": "User with email: emailПользователя already exists!"
   
При выполнении запроса с неверно указанным MediaType (все, кроме "application/json") получим код - 415 Unsupported media type, а в теле ответа будет описание ошибки:  
>"message":"Unsupported  media type: 'неподдерживаемый_MediaType'. Try 'application/JSON'"
  
При выполнении запроса с неверным форматом JSON получим код - 400 Bad request, а в теле ответа будет описание ошибки:    
>"message":"UError processing JSON Cannot deserialize value of ..."  

#####Поиск пользователя по email
GET запрос по адресу:  
>/user/get  
> ? email=<emailПользователя>

Тело ответа будет содержать описание пользователя в формате:  
>{  
> "firstName": "Имя",  
>  "lastName": "Фамилия",  
>"birthDate": "Дата дождения в формате: ГГГГ-ММ-ДД",  
>  "email": "emailПользователя"  
}

(отсутствует поле password)

При поиске несуществующего пользователя получим код - 404 Not found, а в теле ответа будет описание ошибки:  
>"message":"User with email: emailПользователя does not exist."

При отправке запроса без указания параметра email получим код - 400 Bad request, а в теле ответа будет описание ошибки: 
>"message":"email parameter is missing."

#####Удаление пользователя по email
DELETE запрос по адресу:
>/user/delete  
> ? email=<emailПользователя>

######Согласно спецификации HTTP/1.1 метод DELETE должен обладать свойством идемпотентности.
######Данный API реализует это позволяя выполнять DELETE запросы несколько раз по одному и тому же параметру email не вызывая ошибку, с разницей лишь в получаемом HTTP статус-коде: если пользователь существовал и был удален, код - 204 No сontent, если пользователя с заданным email-ом нет, код - 404 Not found.  
При отправке запроса без указания параметра email получим код - 400 Bad request, а в теле ответа будет описание ошибки: 
>"message":"email parameter is missing."

При использовании не описанных HTTP методов запросов будет получен код - 405 Method not allowed, а в теле ответа будет описание ошибки: 
>"message":"НЕПОДДЕРЖИВАЕМЫЙ_МЕТОД method is not supported for this request. Supported methods are ПОДДЕРЖИВАЕМЫЙ_МЕТОД."