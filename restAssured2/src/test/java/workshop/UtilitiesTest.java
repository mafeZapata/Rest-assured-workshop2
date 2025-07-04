package workshop;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeSuite;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeTest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import java.util.Random;

public class  UtilitiesTest  {

    //clase para hacer login y crear usuarios
    protected String appId;
    protected String apiToken;
    protected String baseUri;
    protected String sessionToken;


    public void setEnvironmentParameters() {
        String Uri = System.getenv("ACADEMY_API_PATH");
        appId = System.getenv("API_APPLICATION_ID");
        apiToken = System.getenv("API_TOKEN");
        RestAssured.baseURI = Uri;
    }

    @BeforeTest
    public void SetEnvironmentAndAuthenticate() {

        setEnvironmentParameters();
        sessionToken = Login();
    }

    public String Login() {

        String username = "maria99";
        String password = "12345678";

        Response response = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/login?username=" + username + "&password=" + password)
                .then()
                .statusCode(200)
                .body("username", equalTo(username))
                .body("email", notNullValue())
                .body("sessionToken", notNullValue())
                .extract().response();

        String sessionToken = response.jsonPath().getString("sessionToken");

        System.out.println("---------------login response" + sessionToken + "--------------");
        System.out.println(response.asPrettyString());

        return sessionToken;

    }

        public static String addRandomLetters(String base, int numberOfLetters) {
            String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            Random random = new Random();
            StringBuilder sb = new StringBuilder(base);

            for (int i = 0; i < numberOfLetters; i++) {
                char randomChar = letters.charAt(random.nextInt(letters.length()));
                sb.append(randomChar);
            }

            return sb.toString();
        }

}




