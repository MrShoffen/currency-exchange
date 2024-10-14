# Проект “Обмен валют”

Проект “Обмен валют” представляет собой REST API для описания валют и обменных курсов.
### Описание методов REST API
Описание методов REST API и техническое задание представлены по ссылке - https://zhukovsd.github.io/java-backend-learning-course/projects/currency-exchange/

### Используемые технологии и библиотеки:
- JDBC, HikariCP
- Java Servlets (Jakarta EE)
- SQLite
- Gradle
- Guice для DI
- MapStruct
- Hibernate Validator (реализация Java Bean Validation)
- В качестве фронтенда используется: https://github.com/zhukovsd/currency-exchange-frontend
- В качестве сервера для запуска используется Apache Tomcat 10: https://tomcat.apache.org/download-10.cgi

### Сборка и запуск проекта на локальном сервере
Для сборки проекта используется Maven и MavenWrapper

База данных SQLite в проекте уже создана и в нее добавлены таблицы с несколькими значениями.

Так же для удобства в папке resources/database/scripts лежат sql скрипты для создания и заполнения базы данных.

Для проверки запросов в папке test/http_request_test лежит набор запросов для теста REST API


1) Сборка war артефакта:
```
Windows: .\gradlew.bat build
Linux/MacOs: ./gradlew build
```
Собранный currency-exchange.war появится в папке build/libs.

2) Для запуска сервера необходим Tomcat 10 версии - поместите собранный
   currency-exchange.war артефакт в папку webapps вашего Tomcat.
3) Затем запустите Tomcat используя скрипт из папки bin
```
Windows:  TOMCAT_HOME\bin\startup.bat

Linux/MacOS: TOMCAT_HOME/bin/startup.sh
```
4) Проект запустится и будет доступен по ссылке:

http://localhost:8080/currency-exchange/
