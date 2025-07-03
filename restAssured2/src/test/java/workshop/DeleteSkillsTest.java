package workshop;

import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.restassured.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;

public class DeleteSkillsTest extends UtilitiesTest {

    @BeforeTest
    public void SetEnvironmentParameters(){
        setEnvironmentParameters();
    }

    @Test
    public void testDeleteSkill() {
        String skillId = "Z3NyhPqOQT";

        Response response = given()
                .header("X-Parse-Application-Id", appId)
                .header("X-Parse-REST-API-Key", apiToken)
                .when()
                .delete("/classes/Skills/" + skillId)
                .then()
                .statusCode(400)
                .body("error", containsString("Permission denied for action delete on class Skills"))
                .extract().response();

        System.out.println("---------- Intento de eliminar habilidad con ID: " + skillId + " ----------");
        System.out.println(response.asPrettyString());
    }

    @Test
    public void UnauthorizedGetSkills() {

        Response response = when()
                .get("/classes/Skills")
                .then()
                .statusCode(401)
                .body("error", containsString("unauthorized"))
                .extract().response();

        System.out.println("----------- UnauthorizedGetSkills -----------");
        System.out.println(response.asPrettyString());
    }
}
