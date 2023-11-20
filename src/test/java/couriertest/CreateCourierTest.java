package couriertest;

import client.CourierClient;
import data.CourierData;
import data.CourierGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.httpclient.HttpStatus;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CreateCourierTest {
    CourierClient courierClient;
    CourierData courier;
    private Integer courierId;


    @Before
    public void setup(){
        //создаём тестовые данные
        courierClient = new CourierClient();

    }

    @After
    //написать проверку, если id isNull не удаляем, если не Null - удаляем
    public void cleanUp(){
        if (courierId != null) {
            courierClient.deleteCourier(String.valueOf(courierId));
        }
    }

    @DisplayName("Курьера можно создать. Код ответа сервера 201. Тело 'ok': true")
    @Test
    public void CourierCanBeCreated() {
        courier = CourierGenerator.getRandomCourier();
        ValidatableResponse response = courierClient.createNewCourier(courier);

        //проверка
        response.assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("ok", is(true));
    }

    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Test
    public void cannotCreateDuplicateCourier() {
        // создаем курьера
        courier = CourierGenerator.getRandomCourier();
        courierClient.createNewCourier(courier);

        // попытка создать курьера с теми же данными
        ValidatableResponse response = courierClient.createNewCourier(courier);

        // проверка
        response.assertThat()
                .statusCode(HttpStatus.SC_CONFLICT);
    }
    @DisplayName("Чтобы создать курьера, нужно заполнить поле login")
    @Test
    public void mustPassAllRequiredFieldsIncludeLogin() {
        // убедитесь, что у вас есть курьер без какого-либо обязательного поля
        CourierData invalidCourier = CourierGenerator.getRandomCourierWithoutLogin();

        // попытка создать курьера без обязательных полей
        ValidatableResponse response = courierClient.createNewCourier(invalidCourier);
        // проверка
        response.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @DisplayName("Чтобы создать курьера, нужно заполнить поле password")
    @Test
    public void mustPassAllRequiredFieldsIncludePassword() {
        // убедитесь, что у вас есть курьер без какого-либо обязательного поля
        CourierData invalidCourier = CourierGenerator.getRandomCourierWithoutPassword();

        // попытка создать курьера без обязательных полей
        ValidatableResponse response = courierClient.createNewCourier(invalidCourier);
        // проверка
        response.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @DisplayName("Чтобы создать курьера, можно не заполнять поле first_name")
    @Test
    public void mustPassAllRequiredFieldsIncludeFirstName() {
        // убедитесь, что у вас есть курьер без какого-либо обязательного поля
        CourierData invalidCourier = CourierGenerator.getRandomCourierWithoutFirstName();

        // попытка создать курьера без обязательных полей
        ValidatableResponse response = courierClient.createNewCourier(invalidCourier);
        // проверка
        response.assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("ok", is(true));
    }
}