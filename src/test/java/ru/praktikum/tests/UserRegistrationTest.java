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

public class UserRegistrationTest {
    private static UserService userService;
    private String userId;
    private String accessToken;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        userService = new UserService();
    }

    @Before
    public void prepareTestData() {
        UserData user = new UserData("frodo_" + System.currentTimeMillis() + "@shire.net", "MyPrecious1", "Frodo Baggins");
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
    public void testRegisterUniqueUser() {
        UserData uniqueUser = new UserData("samwise_" + System.currentTimeMillis() + "@shire.net", "Gardener123", "Samwise Gamgee");
        Response response = userService.registerNewUser(uniqueUser);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    public void testRegisterExistingUser() {
        UserData existingUser = new UserData("aragorn_" + System.currentTimeMillis() + "@gondor.com", "Anduril1", "Aragorn");
        userService.registerNewUser(existingUser);
        Response secondResponse = userService.registerNewUser(existingUser);
        secondResponse.then()
                .statusCode(403)
                .body("message", equalTo("User already exists"));
    }

    @Test
    public void testRegisterUserWithMissingField() {
        UserData incompleteUser = new UserData("gandalf_" + System.currentTimeMillis() + "@middleearth.com", "", "Gandalf");
        Response response = userService.registerNewUser(incompleteUser);
        response.then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
