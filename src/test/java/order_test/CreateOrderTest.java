package order_test;

import client.OrderClient;
import data.OrderData;
import data.OrderGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.hamcrest.Matchers;
import org.apache.commons.httpclient.HttpStatus;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    @After
    public void cleanUp() {
        if (track != null) {
            orderClient.cancelOrder(String.valueOf(track));
        }
    }

    private final OrderClient orderClient;
    private final OrderData order;
    private Integer track;

    @Parameterized.Parameter(0)
    public String color1;

    @Parameterized.Parameter(1)
    public String color2;

    @Parameterized.Parameters(name = "Colors: {0}, {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"BLACK", "GREY"},
                {"BLACK", null},
                {null, "GREY"},
                {null, null}
        });
    }

    public CreateOrderTest() {
        this.orderClient = new OrderClient();

        // Если оба цвета указаны, создаем заказ с обоими цветами
        if (color1 != null && color2 != null) {
            this.order = OrderGenerator.getRandomOrderWithColors(color1, color2);
        }
        // Если указан только один цвет, создаем заказ с этим цветом
        else if (color2 != null) {
            this.order = OrderGenerator.getRandomOrderWithColor(color2);
        }
        // Если оба цвета не указаны, создаем заказ без цвета
        else {
            this.order = OrderGenerator.getRandomOrder();
        }
    }

    @DisplayName("Создание заказа успешно с цветом или без")
    @Test
    public void createOrderSuccessfullyWithColor() {
        ValidatableResponse response = orderClient.createOrder(order);

        response.assertThat()
                .statusCode(Matchers.is(HttpStatus.SC_CREATED))
                .body("track", Matchers.notNullValue());
    }
}