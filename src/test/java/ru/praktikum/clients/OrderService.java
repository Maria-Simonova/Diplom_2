package ru.praktikum.clients;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.praktikum.models.OrderData;
import java.util.List;

public class OrderService {

    @Step("Создание заказа с авторизацией")
    public Response createOrderWithAuth(OrderData order, String accessToken) {
        return RestAssured.given()
                .log().everything()
                .contentType("application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Создание заказа без авторизации")
    public Response createOrderWithoutAuth(OrderData order) {
        return RestAssured.given()
                .log().everything()
                .contentType("application/json")
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Получение заказов пользователя")
    public Response getUserOrders(String accessToken) {
        return RestAssured.given()
                .header("Authorization", accessToken)
                .when()
                .log().everything()
                .get("/api/orders");
    }

    @Step("Получение списка валидных ингредиентов")
    public List<String> fetchValidIngredients() {
        return RestAssured.given()
                .when()
                .get("/api/ingredients")
                .then()
                .extract()
                .jsonPath()
                .getList("_id", String.class);
    }
}