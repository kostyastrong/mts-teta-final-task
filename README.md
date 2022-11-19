# MTS Teta Final Task Template

Шаблон для выполнения финального задания Java-курсе МТС Тета.

Для запуска требуется PostgreSQL и Clickhouse.

Docker-команды:

```shell
# Postgres
docker run --name mts-teta-postgres -e POSTGRES_PASSWORD=password -e POSTGRES_USER=user -e POSTGRES_DB=mts-teta-database -p 5432:5432 -d postgres

# Clickhouse
docker run -e CLICKHOUSE_DB=db -e CLICKHOUSE_USER=username -e CLICKHOUSE_PASSWORD=password -p 8123:8123 -d yandex/clickhouse-server
```

К Postgres можно цепляться через [DBeaver](https://dbeaver.io/). К Clickhouse тоже, но также
доступен web-интерфейс в браузере по ссылке [http://localhost:8123/play](http://localhost:8123/play)
. Обратите внимание, что в web-интерфейса Clickhouse в правом верхнем углу нужно вписать логин и
пароль.

Чтобы посмотреть, какие данные записались в Clickhouse, сделайте запрос:

```sql
SELECT *
FROM db.event
```

Если вы выполняете Docker-команды по умолчанию, не меняя пользователей, названия баз и порты, то
достаточно просто запустить приложение через [main](src/main/java/com/mts/teta/DemoApplication.java)
. Иначе же нужно также предварительно поправить конфиги
в [application.properties](src/main/resources/application.properties).

Для запуска требуется Java 17.

## Модули

Если кратко, то flow отправки сообщения и его обогащения выглядит следующим образом.

1. Модуль `tagmanager` предоставляет endpoint (
   смотри [ContainerController.getContainerAsJsFile](src/main/java/com/mts/teta/tagmanager/controller/ContainerController.java))
   для получения Javascript-файла, который можно встроить в веб-сайт. Для простоты в репозитории
   также есть [index.html](index.html). Достаточно просто открыть его в браузере как файл (не
   обязательно даже подключать веб-сервер), чтобы события начали отправляться. Но перед этим нужно
   создать App, Container и Trigger. Про swagger написано ниже.
2. [MessageController](src/main/java/com/mts/teta/enricher/controller/MessageController.java)
   принимает сообщение и обогащает его, делегируя
   вызов [EnricherService](src/main/java/com/mts/teta/enricher/process/EnricherService.java).
3. [EnricherService](src/main/java/com/mts/teta/enricher/process/EnricherService.java) вытаскивает
   поле `userId` и по нему пытается определить `msisdn`. Реализован вариант, когда
   связки `userId -> msisdn` просто хранятся в памяти приложения.
4. [AnalyticDB](src/main/java/com/mts/teta/enricher/db/AnalyticDB.java) предоставляет интерфейс для
   записи обогащенного сообщения в аналитиеское хранилище. В проекте есть реализация для Clickhouse.
5. [DBInitializer](src/main/java/com/mts/teta/enricher/db/DBInitializer.java) запускается при старте
   и создает в Clickhouse таблицу для записи данных, если ее там нет.
6. [liquibase-changelog.xml](src/main/resources/liquibase-changelog.xml) представляет собой
   Liquibase changelog для изменения структуры PostgreSQL.

Также, когда приложение запущено, вы можете открыть в
браузере [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html), чтобы
увидеть все доступные REST endpoints по OpenAPI спецификации. Рекомендуется отправлять запросы
именно оттуда для удобства. Для этого нужно кликнуть на нужный endpoint, и нажать `Try it out`.

## Точки расширения системы

1. Добавить новый тип [Trigger](src/main/java/com/mts/teta/tagmanager/domain/Trigger.java). Сейчас
   есть только [setInterval](https://developer.mozilla.org/en-US/docs/Web/API/setInterval). Было бы
   здорово, если бы еще отслеживались, например, события клика или скролла. Еще лучше, если
   отслеживать можно не все подряд элементы, а какие-то определенные.
2. Сейчас
   в [ContainerController.getContainerAsJsFile](src/main/java/com/mts/teta/tagmanager/controller/ContainerController.java)
   подставляется случайный `userId` из всех тех, что есть в памяти. Лучше, если разные
   коллекции `userId` будут привязаны к разным приложениям. А еще лучше, если иногда будут
   происходить ошибки. Например, подставиться `userId`, для которого `msisdn` неизвестен.
3. Связка `userId -> msisdn` сейчас хранится в памяти. Лучше, если для этого будет использоваться
   какой-то сервси для кэширования. Например, [Redis](https://redis.io/). Еще лучше, если эти связки
   будут обновляться во время работы системы. Например, какие-то сообщения уже могут содержать
   и `msisdn` и `userId`. Тогда недостающую связку можно записать в real-time.
4. События сейчас напрямую пишутся в Clickhouse. Лучше, если для этого будет использоваться
   промежуточная очередь. Например, [Kafka](https://kafka.apache.org/)
   или [RabbitMQ](https://www.rabbitmq.com/). То есть, когда `MessageController` принимает
   сообщения, он отправляет его в очередь. А отдельно есть какой-то листенер, который читает
   сообщения из этой очереди и пишет их в Clickhouse.
   
## Подробная инструкция по запуску проекта

1. Установить [Docker](https://www.docker.com/)
2. Сделать fork репозитория (кнопка fork в правом верхнем углу страницы с репозиторием).
3. Склонировать репозиторий: `git clone [ссылка на ваш репозиторий]`
4. Открыть проект в IDEA
5. Загрузить зависимости
    1. Открыть pom.xml
    2. Нажать ПКМ -> Maven -> Reload project
6. Запустить PostgreSQL и Clickhouse (смотрите инструкцию выше)
    1. Проверить, что контейнеры запущены с помощью команды `docker ps -a`. Либо, если вы используется [Docker Desktop](https://www.docker.com/products/docker-desktop/).
    2. Если какая-то ошибка, проверьте, что Docker daemon запущен с помощью команды (если у вас Linux) `sudo systemctl status docker`. Сервис должен быть `active` и `enabled`.
    3. Если Docker daemon выключен, запустить его можно с помощью команды Linux `sudo systemctl start docker`. Активировать – `sudo systemctl enable docker`.
7. Запустить Spring Boot приложение из main: `src/main/java/com/mts/teta/DemoApplication.java`
    1. При запуске появится уведомление `Lombok requires annotation pocessing`. Выбрать `Enable annotation pocessing`.
8. Открыть `http://localhost:8080/swagger-ui.html`
9. Создать изначальные сущности
    1. `POST /api/app -> Try it out -> name: [любое имя] -> Execute`
    2. `POST /api/container/app/{appId} -> Try it out -> appId: 1; name: [любое имя] -> Execute`
    3. `POST /api/trigger/container/{containerId} -> Try it out -> containerId: 1 -> Execute`
10. Открыть файл `index.html` в IDEA.
    1. Запустить его в браузере с помощью одной из иконок справа.
    2. Если у вас IDEA community, то можно просто открыть файл через любой браузер.
