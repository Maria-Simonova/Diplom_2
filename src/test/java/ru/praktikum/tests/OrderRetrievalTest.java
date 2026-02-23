package ru.praktikum.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.praktikum.clients.OrderService;
import ru.praktikum.clients.UserService;
import ru.praktikum.models.UserData;
import static org.hamcrest.Matchers.*;

public class OrderRetrievalTest {
    private static OrderService orderService;
    private static UserService userService;
    private String accessToken;
    private String userId;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        orderService = new OrderService();
        userService = new UserService();
    }

    @Before
    public void prepareTestData() {
        String uniqueEmail = "pippin_" + System.currentTimeMillis() + "@shire.net";
        UserData user = new UserData(uniqueEmail, "FoolOfATook1", "Pippin Took");
        Response response = userService.registerNewUser(user);
        userId = response.jsonPath().getString("user.id");
        Response loginResponse = userService.authenticateUser(user);
        accessToken = userService.formatToken(loginResponse);
    }

    @After
    public void cleanUp() {
        if (userId != null) {
            userService.removeUser(userId);
        }
    }

    @Test
    public void testGetOrdersWithAuthorization() {
        Response response = orderService.getUserOrders(accessToken);
        response.then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Test
    public void testGetOrdersWithoutAuthorization() {
        Response response = orderService.getUserOrders("invalid_token");
        response.then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }
}