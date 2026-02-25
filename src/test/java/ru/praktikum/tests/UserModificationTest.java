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

public class UserModificationTest {
    private static UserService userService;
    private String userId;
    private String accessToken;
    private UserData testUser;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        userService = new UserService();
    }

    @Before
    public void prepareTestData() {
        String uniqueEmail = "arwen_" + System.currentTimeMillis() + "@rivendell.com";
        testUser = new UserData(uniqueEmail, "Evenstar1", "Arwen");
        Response response = userService.registerNewUser(testUser);
        userId = response.jsonPath().getString("user.id");
        Response loginResponse = userService.authenticateUser(testUser);
        accessToken = userService.formatToken(loginResponse);
    }

    @After
    public void cleanUp() {
        if (userId != null) {
            userService.removeUser(userId);
        }
    }

    @Test
    public void testUpdateUserWithAuthorization() {
        UserData updatedUser = new UserData(testUser.getEmail(), "ElrondDaughter1", "Arwen Undomiel");
        Response response = userService.modifyUserData(updatedUser, accessToken);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    public void testUpdateUserWithoutAuthorization() {
        UserData updatedUser = new UserData(testUser.getEmail(), "ElrondDaughter1", "Arwen Undomiel");
        Response response = userService.modifyUserData(updatedUser, "invalid_token");
        response.then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }
}

