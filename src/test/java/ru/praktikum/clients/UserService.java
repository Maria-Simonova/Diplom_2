package ru.praktikum.clients;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.praktikum.models.UserData;

public class UserService {

    @Step("Регистрация нового пользователя")
    public Response registerNewUser(UserData user) {
        return RestAssured.given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/api/auth/register");
    }

    @Step("Авторизация пользователя")
    public Response authenticateUser(UserData user) {
        return RestAssured.given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/api/auth/login");
    }

    @Step("Изменение данных пользователя")
    public Response modifyUserData(UserData user, String accessToken) {
        return RestAssured.given()
                .log().everything()
                .contentType("application/json")
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Удаление пользователя")
    public Response removeUser(String userId) {
        return RestAssured.delete("/api/auth/user/" + userId);
    }

    public String formatToken(Response response) {
        if (response == null || response.jsonPath().get("accessToken") == null) {
            throw new RuntimeException("Access token is missing in the response");
        }
        String rawToken = response.jsonPath().getString("accessToken");
        if (!rawToken.contains("Bearer")) {
            return "Bearer " + rawToken;
        }
        return rawToken;
    }
}