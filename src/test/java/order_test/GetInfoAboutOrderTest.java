package order_test;

import client.CourierClient;
import client.OrderClient;
import data.*;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;


public class GetInfoAboutOrderTest {
    OrderClient orderClient;
    OrderData order;
    ValidatableResponse responseLogin;
    Integer createdOrderTrack;
    CourierClient courierClient;
    CourierData courier;
    Integer courierId;
    Integer orderId;
    List<Integer> createdOrderTracks = new ArrayList<>();


    @Before
    public void setup() {
        // создаём тестовые данные
        orderClient = new OrderClient();
        order = OrderGenerator.getRandomOrder();
        courierClient = new CourierClient();
        courier = CourierGenerator.getRandomCourier();
        createdOrderTracks.clear();
    }

    @After
    public void cleanUp() {
        if (!createdOrderTracks.isEmpty()) {
            for (Integer orderTrack : createdOrderTracks) {
                orderClient.cancelOrder(String.valueOf(orderTrack));
            }
        }
        if (createdOrderTrack != null) {
            orderClient.cancelOrder(String.valueOf(createdOrderTrack));
        }
        if (courierId != null) {
            courierClient.deleteCourier(String.valueOf(courierId));
        }
    }


    @Test
    @DisplayName("Можно получить список всех заказов")
    public void getArrayListOfOrderIsPossible() {
        ValidatableResponse response = orderClient.getOrderInfo();

        response.assertThat().statusCode(HttpStatus.SC_OK).contentType(ContentType.JSON).body(not(empty()));
    }


    @Test
    @DisplayName("Можно получить список заказов конкретного курьера")
    public void getArrayListOfOrderForCourierIsPossible() {
        // Создаем курьера
        courierClient.createNewCourier(courier);
        // получаем ID курьера
        responseLogin = courierClient.loginCourier(CourierCredentials.from(courier));
        courierId = responseLogin.extract().path("id");
        // создаём заказ и получаем его трек-номер
        createdOrderTrack = orderClient.createOrder(order).extract().path("track");
        // получаем ID заказа
        orderId = orderClient.getOrderInfoByTrack(String.valueOf(createdOrderTrack)).extract().path("order.id");
        // курьер принимает созданный заказ
        orderClient.confirmOrderByCourier(String.valueOf(orderId), String.valueOf(courierId));

        // Получаем список заказов для курьера
        ValidatableResponse response = orderClient.getOrderInfoByCourierId(String.valueOf(courierId));

        // Проверяем, что ответ имеет статус код 200 и у всех выведенных заказов назначен наш курьер
        response.assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("orders.courierId", everyItem(is(courierId)));
    }

    @Test
    @DisplayName("Можно получить список заказов конкретного курьера возле конкретной станции")
        public void GetOrderInfoByCourierIdAndStationIsPossible() {
        // Создаем курьера
        courierClient.createNewCourier(courier);

        // получаем ID курьера и записываем в переменную courierId
        responseLogin = courierClient.loginCourier(CourierCredentials.from(courier));
        courierId = responseLogin.extract().path("id");

        // Создаем три заказа с различными данными
        for (int i = 0; i < 3; i++) {
            order = OrderGenerator.getRandomOrder();
            createdOrderTrack = orderClient.createOrder(order).extract().path("track");
            orderId = orderClient.getOrderInfoByTrack(String.valueOf(createdOrderTrack)).extract().path("order.id");

            // курьер принимает каждый созданный заказ
            orderClient.confirmOrderByCourier(String.valueOf(orderId), String.valueOf(courierId));

        }

        // Получаем список всех заказов курьера
        ValidatableResponse orderResponseCourier = orderClient.getOrderInfoByCourierId(String.valueOf(courierId));

        // Извлекаем номера станций метро из заказов в поле orders.metroStation
        List<String> metroStationsFromOrders = orderResponseCourier.extract().path("orders.metroStation");

        // Извлекаем данные о станциях метро из заказов в поле availableStations.number
        List<String> availableStations = orderResponseCourier.extract().path("availableStations.number");

        ValidatableResponse orderResponse = orderClient
                .getOrderInfoByCourierIdAnsIdStantion(String.valueOf(courierId),availableStations);

                orderResponse.assertThat()
                // Проверяем, что ответ имеет статус код 200_ОК
                .statusCode(HttpStatus.SC_OK)
                // Проверяем, что станции метро в ответе соответствуют принятым курьером заказам
                .body("orders.metroStation", everyItem(is(in(availableStations))))
                .body("orders.metroStation", everyItem(is(in(metroStationsFromOrders))));
    }


    @Test
    @DisplayName("Можно получить список определенное количество доступных заказов")
    public void getCertainNumberOfOrdersIsPossible() {
        // Создаем курьера
        courierClient.createNewCourier(courier);

        // Генерируем случайные данные для лимита заказов на странице и номера страницы
        int limit = RandomUtils.nextInt(1, 31);  // случайное число от 1 до 30
        int pageNumber = 0;

        // Создаем заказы с различными данными
        for (int i = 0; i < limit; i++) {
            order = OrderGenerator.getRandomOrder();
            createdOrderTrack = orderClient.createOrder(order).extract().path("track");
            createdOrderTracks.add(createdOrderTrack); // Сохраняем трек-номер в список
        }

        // Получаем список заказов
        ValidatableResponse response = orderClient.getOrderInfoOfCountAndPage(String.valueOf(limit), String.valueOf(pageNumber));

        response.assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("orders.status", everyItem(is(0)))
                .body("orders", hasSize(limit));
    }

    @Test
    @DisplayName("Есть ограничение в 30 заказов на странице")
    public void getMax30NumberOfOrders() {
        // Создаем курьера
        courierClient.createNewCourier(courier);
        int max_limit = 30;

        // Генерируем случайные данные для лимита заказов на странице и номера страницы
        int limit = RandomUtils.nextInt(31, 35);  // случайное число больше лимита
        int pageNumber = 0;

        // Создаем заказы с различными данными
        for (int i = 0; i < limit; i++) {
            order = OrderGenerator.getRandomOrder();
            createdOrderTrack = orderClient.createOrder(order).extract().path("track");
            createdOrderTracks.add(createdOrderTrack); // Сохраняем трек-номер в список
        }

        // Получаем список заказов
        ValidatableResponse response = orderClient.getOrderInfoOfCountAndPage(String.valueOf(limit), String.valueOf(pageNumber));

        response.assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("orders.status", everyItem(is(0)))
                .body("orders", hasSize(max_limit));
    }

    @Test
    @DisplayName("Можно получить определенное количество заказов возле конкретной станции")
    public void getOrdersNearStation() {

        List<String> stationIds = new ArrayList<>();
        // Создаем курьера
        courierClient.createNewCourier(courier);

        // Генерируем случайные данные для лимита заказов на странице и номера страницы
        int limit = RandomUtils.nextInt(1, 31);  // случайное число от 1 до 30
        int pageNumber = 0;

        // Создаем заказы с различными данными
        for (int i = 0; i < limit; i++) {
            createdOrderTrack = orderClient.createOrder(order).extract().path("track");
            createdOrderTracks.add(createdOrderTrack); // Сохраняем трек-номер в список
            // здесь нужно добавить значение ключа metroStation из order, который создаётся в @Before
            stationIds.add (order.getMetroStation());
        }

        // Получаем список заказов возле конкретной станции

        ValidatableResponse response = orderClient.getOrderInfoByIdStantionOfCountAndPage(
                String.valueOf(limit),
                String.valueOf(pageNumber),
                stationIds
        );

        response.assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("orders.status", everyItem(is(0)))
                .body("orders", hasSize(limit));
    }

    @Test
    @DisplayName("Ошибка 404 при вводе несуществующего ID курьера")
    public void getArrayListOfOrderForIncorrectCourierIsImpossible() {
        // Создаём тестовый номер ID (выбрал заранее 9 значный)
        String incorrectIdCourier = RandomStringUtils.randomNumeric(9);

        // Проверяем, существует ли курьер с таким ID
        ValidatableResponse checkResponse = orderClient.getOrderInfoByCourierId(incorrectIdCourier);

        // Если курьер существует, удаляем его
        if (checkResponse.extract().statusCode() == HttpStatus.SC_OK) {
            courierClient.deleteCourier(incorrectIdCourier);
        }
        // Получаем список заказов для курьера
        ValidatableResponse response = orderClient.getOrderInfoByCourierId(incorrectIdCourier);

        // Проверяем, что ответ имеет статус код 404
        response.assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo("Курьер с идентификатором " + incorrectIdCourier + " не найден"));
    }
    @Test
    @DisplayName("Можно получить заказ по его номеру")
    public void getOrderOfTrackIsPossible() {
        createdOrderTrack = orderClient.createOrder(order).extract().path("track");
        ValidatableResponse response = orderClient.getOrderInfoByTrack(String.valueOf(createdOrderTrack));
        response.assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("order.track", is(createdOrderTrack));
    }
}