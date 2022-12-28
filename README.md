# MtsAnalytics_3

Состав команды:
- Константин Больщиков;
- Мария Мокеева;
- Бергман Валерий.

---

Упор проекта сделан на создании ядра для сбора статистики с различных ресурсов.
Сейчас есть возможность использовать MTS-Analytics для любых приложений с доступом в Интернет.

## Кратко о запуске

Проект написан на языке Java с использованием библиотеки SpringBoot. Для запуска
требуется требуется Maven, Java 17 версии и выше, Docker.

Docker-compose задействует [PostgreSQL](https://www.postgresql.org/), 
[Clickhouse](https://clickhouse.com/) и [Apache Kafka](https://kafka.apache.org/).

### Инструкция к запуску:
1. Запуск Docker и Java.
```shell
# Проверьте, что вы находитесь в ~/path/to/project/mts-teta-final-task

# 1. Docker compose
# Файл docker-compose.yml приложен в репозитории
docker-compose up # может потребоваться дописать первой командой sudo для некоторых систем

# 2. Java run with Maven
mvn compile exec:java -Dexec.mainClass=com.mts.teta.MainApplication
# Вы также можете запустить приложение привычным образом, запустив его в IDE.
```
2. С помощью [swagger](http://localhost:8080/swagger-ui.html) создайте приложение, затем контейнер, наполните последний триггерами.
Из доступных триггеров сейчас есть:
- SET_INTERVAL;
- CLICK;
- SCROLL;
- BUTTON_CLICK;
- MOUSE_MOVE;
- FOCUS_IN;
- FOCUS_OUT.
3. В заголовках вашей тестовой страницы укажите:
```javascript
<script src="http://localhost:8080/api/container/1/jsFile" defer></script>
```
4. Запустите тестовую страницу, выполните действия, активирующие выбранные триггеры.
5. Перейдите в [Clickhouse](http://localhost:8123/play), выполните следующий запрос. Вы должны увидеть записи об активированных триггерах.
```sql
SELECT *
FROM db.event
```
---

# Указания maintainer\`a шаблона Семёна Кирекова

К Postgres можно цепляться через [DBeaver](https://dbeaver.io/). К Clickhouse тоже, но также
доступен web-интерфейс в браузере по ссылке [http://localhost:8123/play](http://localhost:8123/play)
. Обратите внимание, что в web-интерфейсе Clickhouse в правом верхнем углу нужно вписать логин и
пароль, который вы передавали в качестве переменных окружения (`CLICKHOUSE_USER`
и `CLICKHOUSE_PASSWORD`) при старте Docker-контейнера. Если у вас есть IDEA Ultimate, то и к
Clickhouse, и к PostgreSQL можно цепляться прямо из IDE (
вкладка [Databases](https://www.jetbrains.com/help/idea/database-tool-window.html)).

Важный момент относительно PostgreSQL. По умолчанию DBeaver цепляется к БД, которая
называется `postgres` (она создается автоматически). Но при старте Docker-контейнера мы явно говорим
о том, что нужно дополнительно создать БД с
названием `mts-teta-database` (`POSTGRES_DB=mts-teta-database`). Так что все таблицы приложение при
старте создаст именно в `mts-teta-database`.

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
   какой-то сервис для кэширования. Например, [Redis](https://redis.io/). Еще лучше, если эти связки
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
    1. Проверить, что контейнеры запущены с помощью команды `docker ps -a`. Либо, если вы
       используется [Docker Desktop](https://www.docker.com/products/docker-desktop/).
    2. Если какая-то ошибка, проверьте, что Docker daemon запущен с помощью команды (если у вас
       Linux) `sudo systemctl status docker`. Сервис должен быть `active` и `enabled`.
    3. Если Docker daemon выключен, запустить его можно с помощью команды
       Linux `sudo systemctl start docker`. Активировать – `sudo systemctl enable docker`.
7. Запустить Spring Boot приложение из main: `src/main/java/com/mts/teta/DemoApplication.java`
    1. При запуске появится уведомление `Lombok requires annotation pocessing`.
       Выбрать `Enable annotation pocessing`.
8. Открыть `http://localhost:8080/swagger-ui.html`
9. Создать изначальные сущности
    1. `POST /api/app -> Try it out -> name: [любое имя] -> Execute`
    2. `POST /api/container/app/{appId} -> Try it out -> appId: 1; name: [любое имя] -> Execute`
    3. `POST /api/trigger/container/{containerId} -> Try it out -> containerId: 1 -> Execute`
10. Открыть файл `index.html` в IDEA.
    1. Запустить его в браузере с помощью одной из иконок справа.
    2. Если у вас IDEA community, то можно просто открыть файл через любой браузер.

## FAQ

### Где задаётся, какие триггеры нужно подвешивать в js для index.html?

1. Открываем
   [ContainerController](src/main/java/com/mts/teta/tagmanager/controller/ContainerController.java)
2. Далее смотрим метод `getContainerAsJsFile`. Именно он вызывается, когда
   в [index.html](index.html) происходит запрос на получение скрипта (смотри тег `<script>`).
3. В самом методе видим, что для
   указанного [Container](src/main/java/com/mts/teta/tagmanager/domain/Container.java) запрашиваются
   все существующие [Trigger](src/main/java/com/mts/teta/tagmanager/domain/Trigger.java). То есть
   конкретные не выбираются. Мы просто получаем все, что есть.
4. Ну и в конце для каждого `Trigger` вызывается приватный метод `triggerToJsString`. Там уже
   формируется конкретный JS-код. Общий код для всех триггеров мы получаем конкатенацией все
   полученных строковых блоков (вызов `Collectors.joining(";\n")`).
5. Когда мы добавили новый тип `Trigger`, нужно, чтобы данные для нового типа `Trigger` как-то
   попали в PostgreSQL. Для этого есть
   endpoint [TriggerController.createTrigger](src/main/java/com/mts/teta/tagmanager/controller/TriggerController.java)
   .
6. Если мы с помощью него создадим `Trigger` с новым типом, то при
   вызове `ContainerController.getContainerAsJsFile`, этот `Trigger` также будет подставлен. Важно,
   что добавлять новый `Trigger` нужно для того же самого `containerId`, который указан у вас
   в `index.html`.
7. Еще важно отметить, что `ContainerController.getContainerAsJsFile` просто делегирует вызов для
   каждого существующего `Trigger` для заданного `Container` в приватный метод `triggerToJsString`.
   Если вы добавили новый тип `Trigger`, то в `triggerToJsString` вам нужно, добавить логику,
   которая будет подставлять для нового типа `Trigger` соответствующий JS-код.

