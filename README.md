# Проект "Sprint_7"

## Описание проекта

Проект "Sprint7" представляет собой тестирование API с 
использованием библиотеки Rest Assured и фреймворка JUnit. 
Тесты написаны для проверки функциональностей API приложения "Яндекс.Самокат":
* GET /api/v1/order - получение списка заказов
* PUT /api/v1/orders - создание заказа 
* POST /api/v1/courier - создание курьера
* POST /api/v1/courier/login - логин курьера в системе

## Требования перед запуском

1. Java 11
2. Maven

## Установка и запуск

1. Склонируйте репозиторий:

   ```bash
   git clone <https://github.com/Koloksay/Sprint7>
   
2. Перейдите в каталог проекта:

   ```bash
   cd Sprint7
   
3. Запуск тестов
  ```bash
    mvn test
  ```
## Зависимости

* [Rest Assured](https://rest-assured.io/) - библиотека для тестирования API
* [JUnit](https://junit.org/junit4/) - фреймворк для написания тестов
* [Allure](http://allure.qatools.ru/) - фреймворк для создания красочных отчетов
* [Gson](https://github.com/google/gson) - библиотека для работы с форматом JSON
* [Lombok](https://projectlombok.org/) - библиотека для автоматической генерации кода

## Отчеты Allure
После выполнения тестов, вы можете сгенерировать красочные отчеты Allure, используя следующую команду:

```bash
mvn allure:serve
```

##### Автор: Катречко Александр
