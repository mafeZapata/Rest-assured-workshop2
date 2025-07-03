package workshop;

import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.restassured.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GetSkillsTest extends UtilitiesTest {
    String appId;
    String apiToken;

    @BeforeTest
    public void SetEnvironmentParameters(){
        setEnvironmentParameters();
    }
    @Test
    public void testGetSkills() {

        Response response = given().
                header("X-Parse-Application-Id", appId).
                header("X-Parse-REST-API-Key", apiToken).
                when().
                get("/classes/Skills").
                then().
                statusCode(200).
                body("results.size()", greaterThan(0)).
                extract().response();


        System.out.println("---------------get skills------------------");
        System.out.println(response.asPrettyString());
    }

    @Test
    public void testGetSkillByID() {


        String skillId = "ODp5lTet6R";

        Response response = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .when()
                .get("/classes/Skills/" + skillId)
                .then()
                .statusCode(200)
                .body("name", notNullValue())
                .body("description", notNullValue())
                .body("objectId", equalTo(skillId))
                .extract().response();

        System.out.println("---------------get skills by Id" + skillId +"------------------");
        System.out.println(response.asPrettyString());

    }


}

