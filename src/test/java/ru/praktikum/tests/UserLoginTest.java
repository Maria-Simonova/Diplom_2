package ru.praktikum.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.praktikum.clients.UserService;
import ru.praktikum.models.UserData;
import static org.hamcrest.Matchers.equalTo;

public class UserLoginTest {
    private static UserService userService;
    private String userId;
    private UserData testUser;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        userService = new UserService();
    }

    @Before
    public void prepareTestData() {
        String uniqueEmail = "gimli_" + System.currentTimeMillis() + "@erebor.com";
        testUser = new UserData(uniqueEmail, "AxeMaster1", "Gimli");
        Response response = userService.registerNewUser(testUser);
        userId = response.jsonPath().getString("user.id");
    }

    @After
    public void cleanUp() {
        if (userId != null) {
            userService.removeUser(userId);
        }
    }

    @Test
    public void testSuccessfulLogin() {
        Response response = userService.authenticateUser(testUser);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    public void testLoginWithInvalidEmail() {
        UserData invalidUser = new UserData("legolas_" + System.currentTimeMillis() + "@mirkwood.com", "BowMaster1", "Legolas");
        Response response = userService.authenticateUser(invalidUser);
        response.then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    public void testLoginWithInvalidPassword() {
        UserData invalidUser = new UserData(testUser.getEmail(), "WrongPassword", "Gimli");
        Response response = userService.authenticateUser(invalidUser);
        response.then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }
}