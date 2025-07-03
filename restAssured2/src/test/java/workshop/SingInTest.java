package workshop;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class SingInTest extends UtilitiesTest {
 de nuevo 
    @BeforeTest
    public void SetEnvironmentAndAuthenticate() {
        setEnvironmentParameters();
    }
    @Test
    public void VerifySignIn() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("username", "maria99");
        requestBody.put("password", "12345678");
        requestBody.put("email", "maria99@correo.com");

        Response response = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .contentType(ContentType.JSON)
                .body(requestBody.toJSONString())
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("username", equalTo("maria99"))
                .body("sessionToken", notNullValue())
                .extract().response();

        System.out.println("---------------Signup response------------------");
        System.out.println(response.asPrettyString());
    }
}
