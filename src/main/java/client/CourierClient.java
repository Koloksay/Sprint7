package client;

import data.CourierCredentials;
import data.CourierData;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class CourierClient extends RestClient{
    private static final String COURIER_PATH = "/api/v1/courier";
    private static final String LOGIN_PATH = "/api/v1/courier/login";
    @Step("Send POST request to /api/v1/courier")
    public ValidatableResponse createNewCourier(CourierData courier) {
          return given()
                  .spec(RequestSpecification())
                  .and()
                  .body(courier)
                  .when()
                  .post(COURIER_PATH).then();
    }
    @Step("Send POST request to /api/v1/courier/login")
    public ValidatableResponse loginCourier(CourierCredentials courierCredentials){
        return given()
                .spec(RequestSpecification())
                .and()
                .body(courierCredentials)
                .when()
                .post(LOGIN_PATH).then();
    }

    @Step("Send DELETE request to /api/v1/courier/id")
    public ValidatableResponse deleteCourier(String id){

        return given()
                .spec(RequestSpecification())
                .when()
                .delete(COURIER_PATH+"/"+id).then();
    }

}