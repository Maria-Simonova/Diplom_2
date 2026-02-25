package ru.praktikum.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.praktikum.clients.OrderService;
import ru.praktikum.clients.UserService;
import ru.praktikum.models.OrderData;
import ru.praktikum.models.UserData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.*;

public class OrderCreationTest {
    private static OrderService orderService;
    private static UserService userService;
    private String accessToken;
    private String userId;

    private static final String VALID_INGREDIENT_1 = "61c0c5a71d1f82001bdaaa6d";
    private static final String VALID_INGREDIENT_2 = "61c0c5a71d1f82001bdaaa6f";
    private static final String INVALID_INGREDIENT = "invalid_id_12345";

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        orderService = new OrderService();
        userService = new UserService();
    }

    @Before
    public void prepareTestData() {
        String uniqueEmail = "merry_" + System.currentTimeMillis() + "@shire.net";
        UserData user = new UserData(uniqueEmail, "PippinFriend1", "Merry Brandybuck");
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
    public void testCreateOrderWithAuthorization() {
        List<String> ingredients = Arrays.asList(VALID_INGREDIENT_1, VALID_INGREDIENT_2);
        OrderData order = new OrderData(ingredients);
        Response response = orderService.createOrderWithAuth(order, accessToken);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    public void testCreateOrderWithoutIngredients() {
        OrderData order = new OrderData(new ArrayList<>());
        Response response = orderService.createOrderWithAuth(order, accessToken);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void testCreateOrderWithInvalidIngredients() {
        List<String> invalidIngredients = Arrays.asList(INVALID_INGREDIENT);
        OrderData order = new OrderData(invalidIngredients);
        Response response = orderService.createOrderWithAuth(order, accessToken);
        response.then()
                .statusCode(500);
    }
}

