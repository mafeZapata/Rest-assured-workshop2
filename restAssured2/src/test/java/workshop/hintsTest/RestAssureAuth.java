package workshop.hintsTest;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;
import io.restassured.RestAssured;
import workshop.hintsTest.BaseClassAuth;

public class RestAssureAuth extends BaseClassAuth {

    @Test(priority = 0, description="Valid Autentication Scenario with valid username and password.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test Description: UtilitiesTest test with valid username and password.")
    @Story("Get autentication token")
    @Step("Petition get to autentication")
    public void test1() {

        RestAssured.given()
                .get()
                .then()
                .statusCode(200)
                .body("authenticated", equalTo(true));

    }

}