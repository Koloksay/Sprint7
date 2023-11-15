package courier_test;

import client.CourierClient;
import data.CourierCredentials;
import data.CourierData;
import data.CourierGenerator;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.httpclient.HttpStatus;
import static org.hamcrest.Matchers.notNullValue;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

public class LoginCourierTest {

    CourierClient courierClient;
    CourierData courier;
    private Integer courierId;


    @Before
    public void setup() {
        courierClient = new CourierClient();
        courier = CourierGenerator.getRandomCourier();

    }

    @After
    //написать проверку, если id isNull не удаляем, если не Null - удаляем
    public void cleanUp() {
        if (courierId != null) {
            courierClient.deleteCourier(String.valueOf(courierId));
        }
    }

    @DisplayName("Курьер может залогиниться")
    @Test
    public void CourierCanLogin() {
        courierClient.createNewCourier(courier);
        ValidatableResponse responseLogin = courierClient.loginCourier(CourierCredentials.from(courier));
        //проверка
        responseLogin.assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @DisplayName("Ошибка, если не заполнено поле login")
    @Test
    public void ErrorIfFieldLoginIsNull() {
    CourierCredentials missingLoginCredentials = new CourierCredentials(null, courier.getPassword());
    ValidatableResponse responseMissingLogin = courierClient.loginCourier(missingLoginCredentials);
        responseMissingLogin.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @DisplayName("Ошибка, если не заполнено поле password")
    @Test
    public void ErrorIfFieldPasswordIsNull() {
        CourierCredentials missingPasswordCredentials = new CourierCredentials(courier.getLogin(), null);
        ValidatableResponse responseMissingPassword = courierClient.loginCourier(missingPasswordCredentials);
        responseMissingPassword.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .time(lessThan(5L), TimeUnit.SECONDS)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
    @DisplayName("Ошибка, если неверный пароль")
    @Test
    public void ErrorIfInvalidPassword() {
        CourierCredentials invalidCredentials = new CourierCredentials(courier.getLogin(), "invalidPassword");
        ValidatableResponse responseInvalidCredentials = courierClient.loginCourier(invalidCredentials);
        responseInvalidCredentials.assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }
    @DisplayName("Ошибка, если неверный логин")
    @Test
    public void ErrorIfInvalidLogin() {
        CourierCredentials invalidCredentials = new CourierCredentials("invalidLogin",courier.getPassword());
        ValidatableResponse responseInvalidCredentials = courierClient.loginCourier(invalidCredentials);
        responseInvalidCredentials.assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }
    @DisplayName("Ошибка, если авторизация под несуществующим пользователем")
    @Test
    public void ErrorIfNonExistingUser() {
        CourierCredentials nonExistingUserCredentials = new CourierCredentials("nonExistingUser", "password");
        ValidatableResponse responseNonExistingUser = courierClient.loginCourier(nonExistingUserCredentials);
        responseNonExistingUser.assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }
    @DisplayName("Успешный запрос возвращает id")
    @Test
    public void IfLoginIsPossibleReturnId() {
        courierClient.createNewCourier(courier);
        ValidatableResponse responseLogin = courierClient.loginCourier(CourierCredentials.from(courier));
        //проверка
        responseLogin.assertThat()
                .body("id", notNullValue());
    }
}