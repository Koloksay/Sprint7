package client;

import data.OrderData;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient {
    private static final String ORDER_PATH = "/api/v1/orders";

    @Step("Send POST request to /api/v1/orders")
    public ValidatableResponse createOrder(OrderData order) {
        return given()
                .spec(RequestSpecification())
                .and()
                .body(order)
                .when()
                .post(ORDER_PATH).then();
    }

    @Step("Send PUT request to /api/v1/orders/cancel")
    public ValidatableResponse cancelOrder(String track) {
        return given()
                .spec(RequestSpecification())
                .when()
                .put(ORDER_PATH+"/cancel?track="+track).then();
    }

    @Step("Send PUT request to /v1/orders/accept/{track}?courierId={courierId}")
    public ValidatableResponse confirmOrder(String track, String courierId) {
        return given()
                .spec(RequestSpecification())
                .when()
                .put(ORDER_PATH+"/accept/"+track+"?courierId="+courierId).then();
    }

    @Step("Send GET request to /api/v1/orders/track?t={track}")
    public ValidatableResponse getOrderInfoByTrack(String track) {
        return given()
                .spec(RequestSpecification())
                .when()
                .get(ORDER_PATH+"/track?t="+track).then();
    }
    @Step("Send GET request to /api/v1/orders?courierId={CourierId}")
    public ValidatableResponse getOrderInfoByCourierId(String courierId) {
        return given()
                .spec(RequestSpecification())
                .when()
                .get(ORDER_PATH+"?courierId="+courierId).then();
    }

    @Step("Send GET request to /api/v1/orders")
    public ValidatableResponse getOrderInfo() {
        return given()
                .spec(RequestSpecification())
                .when()
                .get(ORDER_PATH).then();
    }

    @Step("Send PUT request to /api/v1/orders/accept/{idOrder}?courierId={CourierId}")
    public ValidatableResponse confirmOrderByCourier(String idOrder, String idCourier) {
        return given()
                .spec(RequestSpecification())
                .when()
                .put(ORDER_PATH+"/accept/"+idOrder+"?courierId="+idCourier).then();
    }

    @Step("Send GET request to /api/v1/orders?courierId={CourierId}&nearestStation=[{idStation1}, {idStation2},...]")
    public ValidatableResponse getOrderInfoByCourierIdAnsIdStantion(String courierId, List<String> nearestStationIds) {
        List<String> list = new ArrayList<>();
        for (String id : nearestStationIds) {
            String s = "\"" + id + "\"";
            list.add(s);
        }
        String nearestStationParam = String.join(",", list);

        return given()
                .spec(RequestSpecification())
                .when()
                .get(ORDER_PATH + "?courierId=" + courierId + "&nearestStation=[" + nearestStationParam + "]")
                .then();
    }

    @Step("Send GET request to /api/v1/orders?limit={countOrder}&page={pageOfOrderNumber}")
    public ValidatableResponse getOrderInfoOfCountAndPage(String countOrder, String pageOfOrderNumber) {

        return given()
                .spec(RequestSpecification())
                .when()
                .get(ORDER_PATH + "?limit=" + countOrder + "&page=" + pageOfOrderNumber)
                .then();
    }

    @Step("Send GET request to /api/v1/orders?limit={countOrder}&page={pageOfOrderNumber}&nearestStation=[{idStation1}, {idStation2},...]")
    public ValidatableResponse getOrderInfoByIdStantionOfCountAndPage(String countOrder, String pageOfOrderNumber, List<String> nearestStationIds) {
        List<String> list = new ArrayList<>();
        for (String id : nearestStationIds) {
            String s = "\"" + id + "\"";
            list.add(s);
        }
        String nearestStationParam = String.join(",", list);

        return given()
                .spec(RequestSpecification())
                .when()
                .get(ORDER_PATH + "?limit=" + countOrder + "&page=" + pageOfOrderNumber + "&nearestStation=[" + nearestStationParam + "]")
                .then();
    }
}